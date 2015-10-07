/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 475123
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ThisInstanceResolver;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;

/**
 * Virtual table class grouping related virtual operations to the table  owning operation.
 * @since 2.0
 */
public abstract class VirtualTable implements IVirtualOperationTable {
	private List<EOperation> fOperations;
	private Map<Module, List<EOperation>> fModule2OperationsMap;
	
	protected VirtualTable() {
	}
	
	protected abstract EOperation getOwningOperation();
	
	/**
	 * Adds operation that is virtual in relation to the owning operation of this table.
	 * <p>
	 * Note: No check is done for non-compatible signature, owner type. It supposed to be done 
	 * done externally.
	 * 
	 * @param operation
	 *            operation to add to this table
	 */
	public void addOperation(EOperation operation) {
		if(operation == null) {
			throw new IllegalArgumentException();
		}
		
		if(fOperations == null) {
			fOperations = new LinkedList<EOperation>();
		}

		if(!fOperations.contains(operation)) {
			fOperations.add(operation);
		}
	}
	
	public EOperation lookupActualOperation(
			EClassifier actualContextType,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			InternalEvaluationEnv evalEnv) {
		return lookupActualOperation(actualContextType, env, null, evalEnv);
	}
	
	public EOperation lookupActualOperation(
			EClassifier actualContextType,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			Module scope, InternalEvaluationEnv evalEnv) {
		if(actualContextType == null || env == null) {
			throw new IllegalArgumentException();
		}

		EOperation formalOperation = this.getOwningOperation();
		if(formalOperation == null) {
			return null;			
		}
		
		if(env.getUMLReflection().getOwningClassifier(formalOperation) == actualContextType) {
			// the closest match satisfied			
			return formalOperation;
		}
		
		// check we can lookup operation directly for the actual type
		if(scope != null) {
			for (EOperation nextOperation : getModuleScopeOperations(scope)) {
				if(env.getUMLReflection().getOwningClassifier(nextOperation) == actualContextType) {
					if(isOperationInScope(nextOperation, evalEnv)) {
						return nextOperation;
					}
				}
			}
		}
		
		Collection<EOperation> candidateOperations = QvtOperationalUtil.filterOverriddenOperations(getOperations());	
		for (EOperation nextOperation : candidateOperations) {
			if(env.getUMLReflection().getOwningClassifier(nextOperation) == actualContextType) {
				if(isOperationInScope(nextOperation, evalEnv)) {
					return nextOperation;
				}
			} 
		}

		if(actualContextType instanceof EClass) {
			// try lookup in actual type's super-types for the closest match
			for (EClass superClass : ((EClass)actualContextType).getESuperTypes()) {
				EOperation superOperation = lookupActualOperation(superClass, env, scope, evalEnv);
				if(superOperation != null) {
					return superOperation;
				}
			}
		}
		
		return null;
	}

	private List<EOperation> getModuleScopeOperations(Module module) {
		if(module == null || fModule2OperationsMap == null || fModule2OperationsMap.containsKey(module) == false) {
			return Collections.emptyList();
		}
		return fModule2OperationsMap.get(module); 
	}
	
	public Collection<EOperation> getOperations() {
		return fOperations != null ? fOperations : Collections.<EOperation>emptySet();
	}
	
	private boolean isOperationInScope(EOperation operation, InternalEvaluationEnv evalEnv) {
		if(evalEnv != null && operation != null) {
			Module module = QvtOperationalParserUtil.getOwningModule(operation);
			if(module != null) {
				ThisInstanceResolver thisResolver = evalEnv.getThisResolver();
				return thisResolver != null && thisResolver.getThisInstanceOf(module) != null;
			} else {
				// coming from metamodel or the OCL Standard Library
				// call-able in any scope
				return true;
			}
		}
			
		return false;
	}
}
