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
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
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
import org.eclipse.ocl.EvaluationVisitorDecorator;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
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
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.Visitable;

public abstract class QvtGenericEvaluationVisitor 
	extends EvaluationVisitorDecorator<EPackage, EClassifier, EOperation, 
				EStructuralFeature, EEnumLiteral, EParameter, EObject, 
				CallOperationAction, SendSignalAction, Constraint, EClass, EObject> 
	implements QvtOperationalEvaluationVisitor {

	public QvtGenericEvaluationVisitor(QvtOperationalEvaluationVisitor qvtExtVisitor) {
		super(qvtExtVisitor);
	}
			
	protected QvtOperationalEvaluationVisitor getQVTDelegate() {
		return (QvtOperationalEvaluationVisitor) getDelegate();
	}	
	
	protected Object genericPreVisitAST(ASTNode visited) {
		return null;
	}
	
	protected Object genericPostVisitAST(ASTNode visited, Object preVisitState, Object result) {
		return result;
	}
	
	protected Object genericPreVisitVisitable(Visitable visited) {
		return null;
	}
	
	protected Object genericPostVisitVisitable(Visitable visited, Object preVisitState, Object result) {
		return result;
	}	
		
	protected void genericPreVisitEObject(EObject visited) {
	}

	protected Object genericPostVisitEObject(EObject visited, Object result) {
		return result;
	}
	
	@Override
	public Object visitAssociationClassCallExp(AssociationClassCallExp<EClassifier, EStructuralFeature> callExp) {
		Object preVisitState = genericPreVisitAST(callExp);
		return genericPostVisitAST(callExp, preVisitState, super.visitAssociationClassCallExp(callExp));
	}

	@Override
	public Object visitBooleanLiteralExp(BooleanLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitBooleanLiteralExp(literalExp));
	}

	@Override
	public Object visitCollectionItem(CollectionItem<EClassifier> item) {
		Object preVisitState = genericPreVisitVisitable(item);
		return genericPostVisitVisitable(item, preVisitState, super.visitCollectionItem(item));
	}

	@Override
	public Object visitCollectionLiteralExp(CollectionLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitCollectionLiteralExp(literalExp));
	}

	@Override
	public Object visitCollectionRange(CollectionRange<EClassifier> range) {
		Object preVisitState = genericPreVisitVisitable(range);
		return genericPostVisitVisitable(range, preVisitState, super.visitCollectionRange(range));
	}

	@Override
	public Object visitConstraint(Constraint constraint) {
		genericPreVisitEObject(constraint);
		return genericPostVisitEObject(constraint, super.visitConstraint(constraint));
	}

	@Override
	public Object visitEnumLiteralExp(EnumLiteralExp<EClassifier, EEnumLiteral> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitEnumLiteralExp(literalExp));
	}

	@Override
	public final Object visitExpression(OCLExpression<EClassifier> expression) {
		// only element specific visits can perform pre/post interceptions
		return super.visitExpression(expression);
	}

	@Override
	public Object visitExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> expression) {
		Object preVisitState = genericPreVisitVisitable(expression);
		return genericPostVisitVisitable(expression, preVisitState, super.visitExpressionInOCL(expression));
	}

	@Override
	public Object visitIfExp(IfExp<EClassifier> ifExp) {
		Object preVisitState = genericPreVisitAST(ifExp);
		return genericPostVisitAST(ifExp, preVisitState, super.visitIfExp(ifExp));
	}

	@Override
	public Object visitIntegerLiteralExp(IntegerLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitIntegerLiteralExp(literalExp));
	}

	@Override
	public Object visitInvalidLiteralExp(InvalidLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitInvalidLiteralExp(literalExp));
	}

	@Override
	public Object visitIterateExp(IterateExp<EClassifier, EParameter> callExp) {
		Object preVisitState = genericPreVisitAST(callExp);
		return genericPostVisitAST(callExp, preVisitState, super.visitIterateExp(callExp));
	}

	@Override
	public Object visitIteratorExp(IteratorExp<EClassifier, EParameter> callExp) {
		Object preVisitState = genericPreVisitAST(callExp);
		return genericPostVisitAST(callExp, preVisitState, super.visitIteratorExp(callExp));
	}

	@Override
	public Object visitLetExp(LetExp<EClassifier, EParameter> letExp) {
		Object preVisitState = genericPreVisitAST(letExp);
		return genericPostVisitAST(letExp, preVisitState, super.visitLetExp(letExp));
	}

	@Override
	public Object visitMessageExp(MessageExp<EClassifier, CallOperationAction, SendSignalAction> messageExp) {
		Object preVisitState = genericPreVisitVisitable(messageExp);
		return genericPostVisitVisitable(messageExp, preVisitState, super.visitMessageExp(messageExp));
	}

	@Override
	public Object visitNullLiteralExp(NullLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitNullLiteralExp(literalExp));
	}

	@Override
	public Object visitOperationCallExp(OperationCallExp<EClassifier, EOperation> callExp) {
		Object preVisitState = genericPreVisitAST(callExp);
		return genericPostVisitAST(callExp, preVisitState, super.visitOperationCallExp(callExp));
	}

	@Override
	public Object visitPropertyCallExp(PropertyCallExp<EClassifier, EStructuralFeature> callExp) {
		Object preVisitState = genericPreVisitAST(callExp);
		return genericPostVisitAST(callExp, preVisitState, super.visitPropertyCallExp(callExp));
	}

	@Override
	public Object visitRealLiteralExp(RealLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitRealLiteralExp(literalExp));
	}

	@Override
	public Object visitStateExp(StateExp<EClassifier, EObject> stateExp) {
		Object preVisitState = genericPreVisitAST(stateExp);
		return genericPostVisitAST(stateExp, preVisitState, super.visitStateExp(stateExp));
	}

	@Override
	public Object visitStringLiteralExp(StringLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitStringLiteralExp(literalExp));
	}

	@Override
	public Object visitTupleLiteralExp(TupleLiteralExp<EClassifier, EStructuralFeature> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitTupleLiteralExp(literalExp));
	}

	@Override
	public Object visitTupleLiteralPart(TupleLiteralPart<EClassifier, EStructuralFeature> part) {
		Object preVisitState = genericPreVisitAST(part);
		return genericPostVisitAST(part, preVisitState, super.visitTupleLiteralPart(part));
	}

	@Override
	public Object visitTypeExp(TypeExp<EClassifier> typeExp) {
		Object preVisitState = genericPreVisitAST(typeExp);
		return genericPostVisitAST(typeExp, preVisitState, super.visitTypeExp(typeExp));
	}

	@Override
	public Object visitUnlimitedNaturalLiteralExp(UnlimitedNaturalLiteralExp<EClassifier> literalExp) {
		Object preVisitState = genericPreVisitAST(literalExp);
		return genericPostVisitAST(literalExp, preVisitState, super.visitUnlimitedNaturalLiteralExp(literalExp));
	}

	@Override
	public Object visitUnspecifiedValueExp(UnspecifiedValueExp<EClassifier> unspecExp) {
		Object preVisitState = genericPreVisitAST(unspecExp);
		return genericPostVisitAST(unspecExp, preVisitState, super.visitUnspecifiedValueExp(unspecExp));
	}

	@Override
	public Object visitVariable(Variable<EClassifier, EParameter> variable) {
		Object preVisitState = genericPreVisitAST(variable);
		return genericPostVisitAST(variable, preVisitState, super.visitVariable(variable));
	}

	@Override
	public Object visitVariableExp(VariableExp<EClassifier, EParameter> variableExp) {
		Object preVisitState = genericPreVisitAST(variableExp);
		return genericPostVisitAST(variableExp, preVisitState, super.visitVariableExp(variableExp));
	}

	
	/////////////////////////////////////////////////////////////////////////
	// QVT specific visitors
	/////////////////////////////////////////////////////////////////////////	
	
	public Object visitAssignExp(AssignExp assignExp) {
		Object preVisitState = genericPreVisitAST(assignExp);
		return genericPostVisitAST(assignExp, preVisitState, getQVTDelegate().visitAssignExp(assignExp));
	}

	public Object visitBlockExp(BlockExp blockExp) {
		Object preVisitState = genericPreVisitAST(blockExp);
		return genericPostVisitAST(blockExp, preVisitState, getQVTDelegate().visitBlockExp(blockExp));
	}
	
	public Object visitComputeExp(ComputeExp computeExp) {
		Object preVisitState = genericPreVisitAST(computeExp);
		return genericPostVisitAST(computeExp, preVisitState, getQVTDelegate().visitComputeExp(computeExp));
    }

	public Object visitHelper(Helper helper) {
		Object preVisitState = genericPreVisitAST(helper);
		return genericPostVisitAST(helper, preVisitState, getQVTDelegate().visitHelper(helper));
	}

	public Object visitImperativeOperation(ImperativeOperation imperativeOperation) {
		Object preVisitState = genericPreVisitAST(imperativeOperation);
		return genericPostVisitAST(imperativeOperation, preVisitState, getQVTDelegate().visitImperativeOperation(imperativeOperation));
	}

	public Object visitLibrary(Library library) {
		Object preVisitState = genericPreVisitAST(library);
		return genericPostVisitAST(library, preVisitState, getQVTDelegate().visitLibrary(library));
	}

	public Object visitContextualProperty(ContextualProperty contextualProperty) {
		Object preVisitState = genericPreVisitAST(contextualProperty);
		return genericPostVisitAST(contextualProperty, preVisitState, getQVTDelegate().visitContextualProperty(contextualProperty));
	}

	public Object visitMappingBody(MappingBody mappingBody) {
		Object preVisitState = genericPreVisitAST(mappingBody);
		return genericPostVisitAST(mappingBody, preVisitState, getQVTDelegate().visitMappingBody(mappingBody));
	}

	public Object visitMappingCallExp(MappingCallExp mappingCallExp) {		
		Object preVisitState = genericPreVisitAST(mappingCallExp);
		return genericPostVisitAST(mappingCallExp, preVisitState, getQVTDelegate().visitMappingCallExp(mappingCallExp));
	}

	public Object visitMappingOperation(MappingOperation mappingOperation) {
		Object preVisitState = genericPreVisitAST(mappingOperation);
		return genericPostVisitAST(mappingOperation, preVisitState, getQVTDelegate().visitMappingOperation(mappingOperation));
	}

	public Object visitModelType(ModelType modelType) {
		Object preVisitState = genericPreVisitAST(modelType);
		return genericPostVisitAST(modelType, preVisitState, getQVTDelegate().visitModelType(modelType));
	}

	public Object visitModule(Module module) {
		Object preVisitState = genericPreVisitAST(module);
		return genericPostVisitAST(module, preVisitState, getQVTDelegate().visitModule(module));
	}

	public Object visitModuleImport(ModuleImport moduleImport) {
		Object preVisitState = genericPreVisitAST(moduleImport);
		return genericPostVisitAST(moduleImport, preVisitState, getQVTDelegate().visitModuleImport(moduleImport));
	}

	public Object visitObjectExp(ObjectExp objectExp) {
		Object preVisitState = genericPreVisitAST(objectExp);
		return genericPostVisitAST(objectExp, preVisitState, getQVTDelegate().visitObjectExp(objectExp));
	}
		
	public Object visitInstantiationExp(InstantiationExp instatiationExp) {
		Object preVisitState = genericPreVisitAST(instatiationExp);
		return genericPostVisitAST(instatiationExp, preVisitState, getQVTDelegate().visitInstantiationExp(instatiationExp));
	}

	public Object visitOperationBody(OperationBody operationBody) {
		Object preVisitState = genericPreVisitAST(operationBody);
		return genericPostVisitAST(operationBody, preVisitState, getQVTDelegate().visitOperationBody(operationBody));
	}

	public Object visitResolveExp(ResolveExp resolveExp) {
		Object preVisitState = genericPreVisitAST(resolveExp);
		return genericPostVisitAST(resolveExp, preVisitState, getQVTDelegate().visitResolveExp(resolveExp));
	}

	public Object visitResolveInExp(ResolveInExp resolveInExp) {
		Object preVisitState = genericPreVisitAST(resolveInExp);
		return genericPostVisitAST(resolveInExp, preVisitState, getQVTDelegate().visitResolveInExp(resolveInExp));
	}

	public Object visitAltExp(AltExp switchAltExp) {
		Object preVisitState = genericPreVisitAST(switchAltExp);
		return genericPostVisitAST(switchAltExp, preVisitState, getQVTDelegate().visitAltExp(switchAltExp));
	}

	public Object visitSwitchExp(SwitchExp switchExp) {
		Object preVisitState = genericPreVisitAST(switchExp);
		return genericPostVisitAST(switchExp, preVisitState, getQVTDelegate().visitSwitchExp(switchExp));
	}

	public Object visitVariableInitExp(VariableInitExp variableInitExp) {
		Object preVisitState = genericPreVisitAST(variableInitExp);
		return genericPostVisitAST(variableInitExp, preVisitState, getQVTDelegate().visitVariableInitExp(variableInitExp));
	}

	public Object visitVarParameter(VarParameter varParameter) {
		Object preVisitState = genericPreVisitAST(varParameter);
		return genericPostVisitAST(varParameter, preVisitState, getQVTDelegate().visitVarParameter(varParameter));
	}

	public Object visitWhileExp(WhileExp whileExp) {
		Object preVisitState = genericPreVisitAST(whileExp);
		return genericPostVisitAST(whileExp, preVisitState, getQVTDelegate().visitWhileExp(whileExp));
	}			
	
	public Object visitAssertExp(AssertExp assertExp) {
		Object preVisitState = genericPreVisitAST(assertExp);
		return genericPostVisitAST(assertExp, preVisitState, getQVTDelegate().visitAssertExp(assertExp));
	}
	
	public Object visitLogExp(LogExp logExp) {
		Object preVisitState = genericPreVisitAST(logExp);
		return genericPostVisitAST(logExp, preVisitState, getQVTDelegate().visitLogExp(logExp));
	}
	
//    public Object visitImperativeLoopExp(ImperativeLoopExp imperativeLoopExp) {
//        return getQVTDelegate().visitImperativeLoopExp(imperativeLoopExp);
//    }

    public Object visitForExp(ForExp forExp) {
    	Object preVisitState = genericPreVisitAST(forExp);
    	return genericPostVisitAST(forExp, preVisitState, getQVTDelegate().visitForExp(forExp));
    }

    public Object visitImperativeIterateExp(ImperativeIterateExp imperativeIterateExp) {
    	Object preVisitState = genericPreVisitAST(imperativeIterateExp);
    	return genericPostVisitAST(imperativeIterateExp, preVisitState, getQVTDelegate().visitImperativeIterateExp(imperativeIterateExp));
    }
    
    public Object visitReturnExp(ReturnExp returnExp) {
    	Object preVisitState = genericPreVisitAST(returnExp);
    	return genericPostVisitAST(returnExp, preVisitState, getQVTDelegate().visitReturnExp(returnExp));
    }
    
    public Object visitEntryOperation(EntryOperation entryOperation) {    
    	Object preVisitState = genericPreVisitAST(entryOperation);
    	return genericPostVisitAST(entryOperation, preVisitState, getQVTDelegate().visitEntryOperation(entryOperation));
    }
    
    public Object visitDictLiteralExp(DictLiteralExp dictLiteralExp) {
    	Object preVisitState = genericPreVisitAST(dictLiteralExp);
    	return genericPostVisitAST(dictLiteralExp, preVisitState, getQVTDelegate().visitDictLiteralExp(dictLiteralExp));
    }
    
	public Object visitBreakExp(BreakExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitBreakExp(astNode));
	}

	public Object visitCatchtExp(CatchExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitCatchtExp(astNode));
	}

	public Object visitContinueExp(ContinueExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitContinueExp(astNode));
	}

	public Object visitDictLiteralPart(DictLiteralPart part) {
		genericPreVisitEObject(part);
		return genericPostVisitEObject(part, getQVTDelegate().visitDictLiteralPart(part));
	}

	public Object visitOrderedTupleLiteralExp(OrderedTupleLiteralExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitOrderedTupleLiteralExp(astNode));
	}

	public Object visitOrderedTupleLiteralPart(OrderedTupleLiteralPart astNode) {
		genericPreVisitEObject(astNode);
		return genericPostVisitEObject(astNode, getQVTDelegate().visitOrderedTupleLiteralPart(astNode));
	}

	public Object visitRaiseExp(RaiseExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitRaiseExp(astNode));
	}

	public Object visitTryExp(TryExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitTryExp(astNode));
	}

	public Object visitUnlinkExp(UnlinkExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitUnlinkExp(astNode));
	}

	public Object visitUnpackExp(UnpackExp astNode) {
		Object preVisitState = genericPreVisitAST(astNode);
		return genericPostVisitAST(astNode, preVisitState, getQVTDelegate().visitUnpackExp(astNode));
	}
	
	public Object visitConstructor(Constructor constructor) {
		Object preVisitState = genericPreVisitAST(constructor);
		return genericPostVisitAST(constructor, preVisitState, getQVTDelegate().visitConstructor(constructor));
	}
	
	public Object visitConstructorBody(ConstructorBody constructorBody) {
		Object preVisitState = genericPreVisitAST(constructorBody);
		return genericPostVisitAST(constructorBody, preVisitState, getQVTDelegate().visitConstructorBody(constructorBody));
	}
}