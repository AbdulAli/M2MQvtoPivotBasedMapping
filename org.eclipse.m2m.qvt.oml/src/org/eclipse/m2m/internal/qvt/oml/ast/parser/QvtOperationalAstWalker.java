/*******************************************************************************
 * Copyright (c) 2007, 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;
import org.eclipse.m2m.internal.qvt.oml.expressions.ConstructorBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.util.QVTOperationalVisitor;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AltExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AssertExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AssignExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.BlockExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.BreakExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.CatchExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ComputeExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ContinueExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictLiteralExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictLiteralPart;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ForExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeIterateExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeLoopExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.InstantiationExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.LogExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.OrderedTupleLiteralExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.OrderedTupleLiteralPart;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.RaiseExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ReturnExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.SwitchExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.TryExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.UnlinkExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.UnpackExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.VariableInitExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.WhileExp;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.CollectionLiteralPart;
import org.eclipse.ocl.expressions.CollectionRange;
import org.eclipse.ocl.expressions.EnumLiteralExp;
import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.InvalidLiteralExp;
import org.eclipse.ocl.expressions.IterateExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.LetExp;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StateExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.expressions.UnspecifiedValueExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.Visitable;

public class QvtOperationalAstWalker implements QVTOperationalVisitor<Object> {

	public interface NodeProcessor {
        void process(Visitable e, Visitable parent);
    }

    public QvtOperationalAstWalker(final NodeProcessor processor) {
        myNodeProcessor = processor;
        myProcessed = new HashSet<Visitable>();
    }
    
    public Object visitDictLiteralExp(DictLiteralExp dictLiteralExp) {
        for (DictLiteralPart part : dictLiteralExp.getPart()) {
        	doProcess(part.getKey(), dictLiteralExp);
        	doProcess(part.getValue(), dictLiteralExp);
        }
        
    	return null;
    }
    
    public Object visitAssignExp(AssignExp assignExp) {
        doProcess(assignExp.getLeft(), assignExp);
        for (OCLExpression<EClassifier> exp : assignExp.getValue()) {
            doProcess(exp, assignExp);
        }
        return null;
    }

    public Object visitHelper(Helper helper) {
        visitImperativeOperation(helper);
        return null;
    }

    public Object visitEntryOperation(EntryOperation entryOperation) {    
    	return visitImperativeOperation(entryOperation);
    }

    public Object visitImperativeOperation(ImperativeOperation imperativeOperation) {
        doProcess(imperativeOperation.getBody(), imperativeOperation);
        return null;
    }


    public Object visitLibrary(Library library) {
        return visitModule(library);
    }

    public Object visitReturnExp(ReturnExp returnExp) {
    	if(returnExp.getValue() != null) {
    		doProcess(returnExp.getValue(), returnExp);
    	}
    	return null;
    }
    
    public Object visitMappingBody(MappingBody mappingBody) {
        for (OCLExpression<EClassifier> exp : mappingBody.getInitSection()) {
            doProcess(exp, mappingBody);
        }
        for (OCLExpression<EClassifier> exp : mappingBody.getContent()) {
            doProcess(exp, mappingBody);
        }
        for (OCLExpression<EClassifier> exp : mappingBody.getEndSection()) {
            doProcess(exp, mappingBody);
        }
        return null;
    }


    public Object visitMappingCallExp(MappingCallExp mappingCallExp) {
        visitOperationCallExp(mappingCallExp);
        return null;
    }


    public Object visitMappingOperation(MappingOperation mappingOperation) {
        visitImperativeOperation(mappingOperation);
        for (OCLExpression<EClassifier> exp : mappingOperation.getWhen()) {
            doProcess(exp, mappingOperation);
        }
        if (mappingOperation.getWhere() instanceof BlockExp) {
        	for (OCLExpression<EClassifier> exp : ((BlockExp) mappingOperation.getWhere()).getBody()) {
        		doProcess(exp, mappingOperation);
        	}
        }
        return null;
    }


    public Object visitModule(Module module) {
        myNodeProcessor.process(module, null);

        // Remark: using QvtOperationalParserUtil.getOwnedOperations() operation instead of direct
        // access to EClass::getEOperations(), as contextual mappings are in owned by the module type 
        // as it is understood by MDT OCL and would produce wrong behavior in operations lookup
        for (EOperation op : new ArrayList<EOperation>(QvtOperationalParserUtil.getOwnedOperations(module))) {
            doProcess((ImperativeOperation) op, module);
        }
        for (EStructuralFeature feature : module.getEStructuralFeatures()) {
        	if(feature instanceof ContextualProperty) {
            	ContextualProperty ctxProp = (ContextualProperty) feature;
        		doProcess(ctxProp, module);        		
        	} else {
        		//propAST = QvtOperationalParserUtil.getLocalPropertyAST(prop);
        		OCLExpression<EClassifier> initExp = QvtOperationalParserUtil.getInitExpression(feature);
        		if(initExp != null) {
        			doProcess(initExp, module);
        		}
        	}
        }
        return null;
    }


    public Object visitModuleImport(ModuleImport moduleImport) {
        return null;
    }


    public Object visitObjectExp(ObjectExp objectExp) {
    	if(objectExp.getBody() != null) {
    		EList<org.eclipse.ocl.ecore.OCLExpression> contents = objectExp.getBody().getContent();
	        for (OCLExpression<EClassifier> exp : contents) {
	            doProcess(exp, objectExp);
	        }
    	}
        return null;
    }

    public Object visitInstantiationExp(InstantiationExp instantiationExp) {
	    for (OCLExpression<EClassifier> argExp : instantiationExp.getArgument()) {
	        doProcess(argExp, argExp);
	    }
        return null;
    }

    public Object visitOperationBody(OperationBody operationBody) {
        for (OCLExpression<EClassifier> exp : operationBody.getContent()) {
            doProcess(exp, operationBody);
        }
        return null;
    }
	
    public Object visitVarParameter(VarParameter varParameter) {
        return null;
    }


    public Object visitVariableInitExp(VariableInitExp variableInitExp) {
        org.eclipse.ocl.ecore.Variable referredVariable = variableInitExp.getReferredVariable();
        if(referredVariable.getInitExpression() != null) {
        	doProcess(referredVariable.getInitExpression(), variableInitExp);
        }
        return null;
    }


    public Object visitBlockExp(BlockExp blockExp) {
        for (OCLExpression<EClassifier> exp : blockExp.getBody()) {
            doProcess(exp, blockExp);
        }
        return null;
    }
    
    public Object visitComputeExp(ComputeExp computeExp) {
    	if(computeExp.getReturnedElement() != null) {
    		doProcess(computeExp.getReturnedElement(), computeExp);
    	}
    	
    	if(computeExp.getBody() != null) {
    		doProcess(computeExp.getBody(), computeExp);
    	}
        return null;
    }

    public Object visitWhileExp(WhileExp whileExp) {
        if (whileExp.getCondition() != null) {
            doProcess(whileExp.getCondition(), whileExp);
        }
    	
        if(whileExp.getBody() != null) {
        	doProcess(whileExp.getBody(), whileExp);
        }

        return null;
    }


    public Object visitAssociationClassCallExp(AssociationClassCallExp<EClassifier, EStructuralFeature> callExp) {
        for (OCLExpression<EClassifier> exp : callExp.getQualifier()) {
            doProcess(exp, callExp);
        }
        doProcess(callExp.getSource(), callExp);
        return null;
    }


    public Object visitBooleanLiteralExp(BooleanLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitCollectionItem(CollectionItem<EClassifier> item) {
        doProcess(item.getItem(), item);
        return null;
    }


    public Object visitCollectionLiteralExp(CollectionLiteralExp<EClassifier> literalExp) {
        for (CollectionLiteralPart<EClassifier> part : literalExp.getPart()) {
            doProcess(part, literalExp);
        }
        return null;
    }


    public Object visitCollectionRange(CollectionRange<EClassifier> range) {
        doProcess(range.getFirst(), range);
        doProcess(range.getLast(), range);
        return null;
    }


    public Object visitConstraint(Constraint constraint) {
        doProcess(constraint.getSpecification().getBodyExpression(), constraint.getSpecification());
        return null;
    }


    public Object visitEnumLiteralExp(EnumLiteralExp<EClassifier, EEnumLiteral> literalExp) {
        return null;
    }


    public Object visitExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> expression) {
        return null;
    }


    public Object visitIfExp(IfExp<EClassifier> ifExp) {
        doProcess(ifExp.getCondition(), ifExp);
        doProcess(ifExp.getThenExpression(), ifExp);
        doProcess(ifExp.getElseExpression(), ifExp);
        return null;
    }


    public Object visitIntegerLiteralExp(IntegerLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitInvalidLiteralExp(InvalidLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitIterateExp(IterateExp<EClassifier, EParameter> callExp) {
        doProcess(callExp.getResult(), callExp);
        doProcess(callExp.getBody(), callExp);
        return null;
    }


    public Object visitIteratorExp(IteratorExp<EClassifier, EParameter> callExp) {
    	if(callExp.getSource() != null) {
    		doProcess(callExp.getSource(), callExp);
    	}
        doProcess(callExp.getBody(), callExp);
        return null;
    }


    public Object visitLetExp(LetExp<EClassifier, EParameter> letExp) {
        doProcess(letExp.getVariable(), letExp);
        doProcess(letExp.getIn(), letExp);
        return null;
    }


    public Object visitMessageExp(MessageExp<EClassifier, CallOperationAction, SendSignalAction> messageExp) {
        doProcess(messageExp.getTarget(), messageExp);
        return null;
    }


    public Object visitNullLiteralExp(NullLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitOperationCallExp(OperationCallExp<EClassifier, EOperation> callExp) {
        OCLExpression<EClassifier> source = callExp.getSource();
        if (source != null) {
            doProcess(source, callExp);
        }

        for (OCLExpression<EClassifier> exp : callExp.getArgument()) {
            doProcess(exp, callExp);
        }
        EOperation referredOperation = callExp.getReferredOperation();
        if (referredOperation instanceof Visitable) {
        	// TODO -
        	// Should not visit referenced operation, as thesemight com from different top container (Module)
        	// Should strictly traverse on containment basis, no need to track processed nodes
            //doProcess((Visitable) referredOperation, callExp);
        }
        return null;
    }


    public Object visitPropertyCallExp(PropertyCallExp<EClassifier, EStructuralFeature> callExp) {
        return null;
    }


    public Object visitRealLiteralExp(RealLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitStateExp(StateExp<EClassifier, EObject> stateExp) {
        return null;
    }


    public Object visitStringLiteralExp(StringLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitTupleLiteralExp(TupleLiteralExp<EClassifier, EStructuralFeature> literalExp) {
        return null;
    }


    public Object visitTupleLiteralPart(TupleLiteralPart<EClassifier, EStructuralFeature> part) {
        return null;
    }


    public Object visitTypeExp(TypeExp<EClassifier> typeExp) {
        return null;
    }


    public Object visitUnlimitedNaturalLiteralExp(UnlimitedNaturalLiteralExp<EClassifier> literalExp) {
        return null;
    }


    public Object visitUnspecifiedValueExp(UnspecifiedValueExp<EClassifier> unspecExp) {
        return null;
    }


    public Object visitVariable(Variable<EClassifier, EParameter> variable) {
        return null;
    }


    public Object visitVariableExp(VariableExp<EClassifier, EParameter> variableExp) {
        Variable<EClassifier, EParameter> referredVariable = variableExp.getReferredVariable();
        if (referredVariable instanceof Visitable) {
            doProcess((Visitable) referredVariable, variableExp);
        }
        return null;
    }

    public Object visitResolveExp(ResolveExp resolveExp) {
        doProcess(resolveExp.getCondition(), resolveExp);
        return null;
    }

    public Object visitResolveInExp(ResolveInExp resolveInExp) {
    	// do not process 'inMapping' reference, as it is not containment
        return visitResolveExp(resolveInExp);
    }

    public Object visitModelType(ModelType modelType) {
        for (OCLExpression<EClassifier> exp : modelType.getAdditionalCondition()) {
            doProcess(exp, modelType);
        }
        return null;
    }

    public Object visitLogExp(LogExp logExp) {
        for (OCLExpression<EClassifier> arg : logExp.getArgument()) {
            doProcess(arg, logExp);
        }

        if(logExp.getCondition() != null) {
            doProcess(logExp.getCondition(), logExp);
        }

        return null;
    }

    public Object visitAssertExp(AssertExp assertExp) {
        if(assertExp.getAssertion() != null) {
            doProcess(assertExp.getAssertion(), assertExp);
        }

        if(assertExp.getLog() != null) {
            doProcess(assertExp.getLog(), assertExp);
        }

        return null;
    }

    protected NodeProcessor getNodeProcessor() {
    	return myNodeProcessor;
    }

    protected void doProcess(Visitable e, Visitable parent) {
        if(e != null && !myProcessed.contains(e)) {
            myNodeProcessor.process(e, parent);
            myProcessed.add(e);
            e.accept(this);
        }
    }

    private final NodeProcessor myNodeProcessor;
    private final Set<Visitable> myProcessed;

    public Object visitAltExp(AltExp altExp) {
        doProcess(altExp.getCondition(), altExp);
        doProcess(altExp.getBody(), altExp);
        return null;
    }

    public Object visitSwitchExp(SwitchExp switchExp) {
        EList<AltExp> alternativePart = switchExp.getAlternativePart();
        if (alternativePart != null) {
            for (AltExp altExp  : alternativePart) {
                doProcess(altExp, switchExp);
            }
        }
        doProcess(switchExp.getElsePart(), switchExp);
        return null;
    }
    
    public Object visitImperativeLoopExp(ImperativeLoopExp imperativeLoopExp) {
    	if(imperativeLoopExp.getSource() != null) {
    		doProcess(imperativeLoopExp.getSource(), imperativeLoopExp);
    	}
        doProcess(imperativeLoopExp.getCondition(), imperativeLoopExp);
        doProcess(imperativeLoopExp.getBody(), imperativeLoopExp);
        return null;
    }

    public Object visitImperativeIterateExp(ImperativeIterateExp imperativeIterateExp) {
        return visitImperativeLoopExp(imperativeIterateExp);
    }

	public Object visitContextualProperty(ContextualProperty contextualProperty) {
        doProcess(contextualProperty.getInitExpression(), contextualProperty);
        return null;
	}

    public Object visitForExp(ForExp forExp) {
        return visitImperativeLoopExp(forExp);
    }

	public Object visitConstructor(Constructor constructor) {
		return visitImperativeOperation(constructor);
	}

	public Object visitConstructorBody(ConstructorBody constructorBody) {
		return visitOperationBody(constructorBody);
	}    

	public Object visitBreakExp(BreakExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitCatchtExp(CatchExp catchExp) {
        for (OCLExpression<EClassifier> exp : catchExp.getBody()) {
            doProcess(exp, catchExp);
        }
        return null;
	}

	public Object visitContinueExp(ContinueExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitDictLiteralPart(DictLiteralPart astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitOrderedTupleLiteralExp(OrderedTupleLiteralExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitOrderedTupleLiteralPart(OrderedTupleLiteralPart astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitRaiseExp(RaiseExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitTryExp(TryExp tryExp) {
        for (OCLExpression<EClassifier> exp : tryExp.getTryBody()) {
            doProcess(exp, tryExp);
        }
        for (CatchExp catchExp : tryExp.getExceptClause()) {
        	visitCatchtExp(catchExp);
        }
        return null;
	}

	public Object visitUnlinkExp(UnlinkExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitUnpackExp(UnpackExp astNode) {
		// TODO Auto-generated method stub
		return null;
	}

}