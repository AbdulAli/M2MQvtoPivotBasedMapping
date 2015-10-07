/*******************************************************************************
 * Copyright (c) 2007, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNode;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNodeAccess;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.IModuleSourceInfo;
import org.eclipse.m2m.internal.qvt.oml.ast.env.InternalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.ecore.FeatureCallExp;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.ASTNode;

/**
 * Helps to build QVT stack trace from a given state of QVT code execution.
 */
public class QvtStackTraceBuilder {
	
	private static final String UNKNOWN_NAME = "<Unknown>"; //$NON-NLS-1$
	private static final int UNKNOWN_LINE_NUM = -1;
	
	private QvtOperationalEvaluationEnv fEvalEnv;

	/**
	 * Constructs stack trace builder for the given evaluation environment.
	 * 
	 * @param evalEnv
	 *            the evaluation environment representing the top stack trace
	 *            
	 * @param astNodeIPOffset explicit the AST node offset representing the current instruction 
	 *		pointer of execution in a QVT module
	 */
	public QvtStackTraceBuilder(QvtOperationalEvaluationEnv evalEnv) {
		if(evalEnv == null) {
			throw new IllegalArgumentException();
		}
		
		fEvalEnv = evalEnv;
	}
	
	/**
	 * Builds the stack trace corresponding to evaluation environments hierarchy
	 * associated with this builder.
	 * 
	 * @return list of QVT stack elements
	 */
    public List<QVTStackTraceElement> buildStackTrace() {
    	LinkedList<QVTStackTraceElement> elements = new LinkedList<QVTStackTraceElement>();
    	
    	for(QvtOperationalEvaluationEnv nextEnv = fEvalEnv; nextEnv != null; nextEnv = nextEnv.getParent()) {
    		// skip all the root execution environments as they 
    		// are not bound to any module code locations
    		QvtOperationalEvaluationEnv parent = nextEnv.getParent();
			if(parent != null) {
        		InternalEvaluationEnv internalEnv = nextEnv.getAdapter(InternalEvaluationEnv.class);
        		// skip all stack frames not running in a module, 
        		// IOW possible non QVT transformation clients
        		if(internalEnv.getCurrentModule() != null) {		
        			elements.addLast(createStackElement(nextEnv));
        		}
    		}
    	}
    	
    	QvtOperationalEvaluationEnv rootEnv = fEvalEnv.getRoot();
		QvtOperationalEvaluationEnv aggregatingEnv = EvaluationUtil.getAggregatingContext(rootEnv);
		if(aggregatingEnv != null) {
			List<QVTStackTraceElement> aggregatedStackTrace = new QvtStackTraceBuilder(aggregatingEnv).buildStackTrace();			
			List<QVTStackTraceElement> result = new ArrayList<QVTStackTraceElement>(elements.size() + aggregatedStackTrace.size());
			result.addAll(elements);
			result.addAll(aggregatedStackTrace);
			return result;
		}
    	return Collections.unmodifiableList(elements);
    }

    private QVTStackTraceElement createStackElement(QvtOperationalEvaluationEnv env) {
    	String unitName = null;
    	String moduleName = UNKNOWN_NAME;
    	String operName = UNKNOWN_NAME;
    	int lineNumber = UNKNOWN_LINE_NUM;    	
    	
    	Module module = null;
    	ImperativeOperation operation = env.getOperation();

    	InternalEvaluationEnv internEvalEnv = env.getAdapter(InternalEvaluationEnv.class);
    	int resultOffset = getCurrentASTOffset(internEvalEnv);
		
    	ModuleInstance thisInstance = internEvalEnv.getCurrentModule();
    	if(thisInstance == null) {
    		throw new IllegalArgumentException("Currently executed model is not set in environment"); //$NON-NLS-1$
    	}
    	
    	module = thisInstance.getModule();
    	assert module != null;
    	moduleName = module.getName();
    	
		if(operation == null) {
			// we must be executing a module instance initialization - synthetic constructor
	    	operName = moduleName;
	    	
	    	if(internEvalEnv.getCurrentIP() == module || resultOffset < -1) {
	    		ASTSyntheticNode astNode = ASTSyntheticNodeAccess.getASTNode(module);
		    	if(astNode != null) {
		    		resultOffset = astNode.getStartPosition();
		    	}
	    	}
		} else {
    		operName = operation.getName();	    		
    		EClassifier contextType = QvtOperationalParserUtil.getContextualType(operation);
    		if(contextType != null) {
    			operName = contextType.getName() + EmfUtil.PATH_SEPARATOR + operName;
    		}
		}

		IModuleSourceInfo sourceInfo = ASTBindingHelper.getModuleSourceBinding(module);
		if(sourceInfo != null) {
			URI uri = sourceInfo.getSourceURI();
			unitName = uri.lastSegment();
			if(resultOffset >= 0) {
				lineNumber = sourceInfo.getLineNumberProvider().getLineNumber(resultOffset);
			}
		}
    	
    	return new QVTStackTraceElement(moduleName, operName, unitName, lineNumber);
    }
    
    private static int getCurrentASTOffset(InternalEvaluationEnv evalEnv) {
    	// TODO - for cases that AST does not fill all offset
    	// traverse up to the enclosing operation scope, taking the closest 
    	// offset which has been initialized    	
    	EObject currentIPObject = evalEnv.getCurrentIP();
    	
    	if(currentIPObject instanceof ASTNode) {
    		ASTNode astNode = (ASTNode) currentIPObject;
    		
			if(astNode.getStartPosition() < 0 && astNode instanceof VariableExp<?, ?>) {
				// Remark: special processing for implicit source variables represented as
				// synthetic variable expression in AST. These do not have any CST representation
				// but are rather synthetic nodes => point to the call AST
				if(astNode.eContainer() instanceof FeatureCallExp) {
					astNode = (FeatureCallExp) astNode.eContainer();
				}
			}    		
    		
    		return astNode.getStartPosition();
    	}
    	
		ASTSyntheticNode astNode = ASTSyntheticNodeAccess.getASTNode(currentIPObject);
		if(astNode != null) {
			return astNode.getStartPosition();
		}
    	
    	return -1;
    }
}
