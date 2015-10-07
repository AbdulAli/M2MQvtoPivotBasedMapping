/*******************************************************************************
 * Copyright (c) 2007, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Alex Paperno - bugs 414616, 424584
 *     Christopher Gerking - bug 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;
import org.eclipse.m2m.internal.qvt.oml.expressions.ConstructorBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.util.QVTOperationalVisitor;
import org.eclipse.m2m.internal.qvt.oml.stdlib.QVTUMLReflection;
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
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeExpression;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeIterateExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeLoopExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.InstantiationExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
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
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.IfExp;
import org.eclipse.ocl.ecore.IterateExp;
import org.eclipse.ocl.ecore.IteratorExp;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.ecore.TupleType;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.FeatureCallExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.internal.l10n.OCLMessages;
import org.eclipse.ocl.lpg.BasicEnvironment;
import org.eclipse.ocl.lpg.FormattingHelper;
import org.eclipse.ocl.lpg.ProblemHandler;
import org.eclipse.ocl.options.ProblemOption;
import org.eclipse.ocl.parser.ValidationVisitor;
import org.eclipse.ocl.types.BagType;
import org.eclipse.ocl.types.OrderedSetType;
import org.eclipse.ocl.types.SequenceType;
import org.eclipse.ocl.types.SetType;
import org.eclipse.ocl.types.TypeType;
import org.eclipse.ocl.util.OCLStandardLibraryUtil;
import org.eclipse.ocl.util.OCLUtil;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.PredefinedType;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;
import org.eclipse.ocl.utilities.Visitable;

/**
 * 
 * @author sboyko
 * 
 * Ill-based validation visitor for OCL expressions only. 
 * Presented till 'https://bugs.eclipse.org/bugs/show_bug.cgi?id=215544'
 * is not resolved.
 *
 */
public class QvtOperationalValidationVisitor extends QvtOperationalAstWalker {
	private QvtOperationalEnv fEnv;
	
	public QvtOperationalValidationVisitor(QvtOperationalEnv environment) {
		super (new ValidationNodeProcessor(environment));
		((ValidationNodeProcessor) getNodeProcessor()).myOclValidationVisitor.setVisitor(this);
		fEnv = environment;
	}
	
	private static class ValidationNodeProcessor implements NodeProcessor {
			
		ValidationNodeProcessor(QvtOperationalEnv environment) {
			myOclValidationVisitor = new CustomOclValidationVisitor(environment);
		}
		
		public void process(Visitable e, Visitable parent) {
			if (e.eClass().eContainer() == org.eclipse.ocl.ecore.EcorePackage.eINSTANCE) {
				try {
					e.accept(myOclValidationVisitor);
				}
				catch (UnsupportedOperationException ex) {
				}
			}
		}
		
		final CustomOclValidationVisitor myOclValidationVisitor;
	}
	
	@Override
	public Object visitIfExp(org.eclipse.ocl.expressions.IfExp<EClassifier> ifExp) {
		// QVT language also defines an imperative "if-then-else" construct that is less constrained 
		// as the corresponding OCL construct
		
		if (ifExp.getCondition() == null || ifExp.getThenExpression() == null) {
			QvtOperationalUtil.reportError(fEnv,
					NLS.bind(ValidationMessages.QvtOperationalVisitorCS_ifExpIncomplete, new Object[] { }),
					ifExp.getStartPosition(), ifExp.getEndPosition());
		}
		else if (ifExp.getCondition().getType() != fEnv.getOCLStandardLibrary().getBoolean()) {
			QvtOperationalUtil.reportError(fEnv,
					NLS.bind(ValidationMessages.QvtOperationalVisitorCS_ifExpNonBooleanCond, new Object[] { }),
					ifExp.getStartPosition(), ifExp.getEndPosition());
		}
		
		EObject container = ifExp.eContainer();
		while (container != null) {
			if (container instanceof VariableInitExp) {
				if (ifExp.getElseExpression() == null) {
					QvtOperationalUtil.reportWarning(fEnv,
							NLS.bind(ValidationMessages.QvtOperationalVisitorCS_ifExpWithoutElseAssignment, new Object[] { }),
							ifExp.getStartPosition(), ifExp.getEndPosition());
					break;
				}
			}
			container = container.eContainer();
		}
		
		return super.visitIfExp(ifExp);
	}
	
	@Override
	public Object visitContinueExp(ContinueExp astNode) {
		validateBreakContinue(astNode);
		return super.visitContinueExp(astNode);
	}
	
	@Override
	public Object visitBreakExp(BreakExp astNode) {
		validateBreakContinue(astNode);
		return super.visitBreakExp(astNode);
	}

	private void validateBreakContinue(ImperativeExpression breakContinueExp) {
		boolean isLoopFound = false;
		EObject container = breakContinueExp.eContainer();
		while (container != null) {
			if (container instanceof ImperativeLoopExp || container instanceof WhileExp) {
				isLoopFound = true;
				break;
			}
			if (container instanceof IterateExp || container instanceof IteratorExp) {
				break;
			}
			container = container.eContainer();
		}
		
		if (!isLoopFound) {
	    	QvtOperationalUtil.reportError(fEnv, 
	    			NLS.bind(ValidationMessages.BreakContinue_InvalidExpressionUsage, breakContinueExp.eClass().getName()),
	    			breakContinueExp.getStartPosition(), breakContinueExp.getEndPosition());
		}
		else if (false == breakContinueExp.eContainer() instanceof ImperativeExpression
				&& false == breakContinueExp.eContainer() instanceof IfExp) {
			// QVT language also defines an imperative �if-then-else� construct that is less constrained 
			// as the corresponding OCL construct
	    	QvtOperationalUtil.reportError(fEnv, 
	    			NLS.bind(ValidationMessages.BreakContinue_InvalidExpressionOwner, breakContinueExp.eClass().getName()),
	    			breakContinueExp.getStartPosition(), breakContinueExp.getEndPosition());
		}
		
	}

	@Override
	public Object visitOperationCallExp(OperationCallExp<EClassifier, EOperation> callExp) {
		if (callExp.getReferredOperation() instanceof Constructor) {
			QvtOperationalUtil.reportError(fEnv,
					NLS.bind(ValidationMessages.OperationIsUndefined, operationString(fEnv, callExp.getReferredOperation().getName(), callExp.getArgument()),
					callExp.getSource() == null ? null : fEnv.getUMLReflection().getName(callExp.getSource().getType())), 
					callExp.getStartPosition(), 
					callExp.getEndPosition());
		}
		return super.visitOperationCallExp(callExp);
	}
	
	protected String operationString(QvtOperationalEnv env, String operName, List<? extends TypedElement<EClassifier>> args) {
		StringBuffer result = new StringBuffer();

		result.append(operName);
		result.append('(');

		for (Iterator<? extends TypedElement<EClassifier>> iter = args.iterator(); iter.hasNext();) {

			TypedElement<EClassifier> arg = iter.next();
			EClassifier type = arg.getType();

			result.append((type == null) ? (String) null : env.getUMLReflection().getName(type));

			if (iter.hasNext()) {
				result.append(", "); //$NON-NLS-1$
			}
		}

		result.append(')');

		return result.toString();
	}	
		
	@SuppressWarnings("unchecked")
	@Override
	public Object visitInstantiationExp(InstantiationExp instantiationExp) {
		Boolean result = Boolean.TRUE;
		EClass instantiatedClass = instantiationExp.getInstantiatedClass();
		Variable<EClassifier, EParameter> referredObject = null;
		if (instantiationExp.eContainer() instanceof Variable<?, ?>) {
			referredObject = (Variable<EClassifier, EParameter>) instantiationExp.eContainer();
		}

		if(instantiatedClass == null && (referredObject != null && QVTUMLReflection.isUserModelElement(referredObject.getType()))) {
			instantiatedClass = (EClass)referredObject.getType();
		}
		
		// declared constructors
		Adapter adapter = EcoreUtil.getAdapter(instantiationExp.eAdapters(), ConstructorOperationAdapter.class);
		if (adapter != null) {
			assert ((ConstructorOperationAdapter) adapter).getReferredConstructor() != null;
			
			if (instantiatedClass != null 
					&& (instantiatedClass.isAbstract() || instantiatedClass.isInterface())) {
				
				QvtOperationalUtil.reportError(fEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_canNotInstantiateAbstractType, 
						fEnv.getFormatter().formatType(instantiatedClass)), 
						instantiationExp.getStartPosition(), 
						instantiationExp.getEndPosition());
				result = Boolean.FALSE;
			}
			return result;
		}

		// default constructor
		if (instantiatedClass != null
				&& !QvtOperationalStdLibrary.INSTANCE.getTransformationClass().isSuperTypeOf(instantiatedClass)
				&& instantiationExp.getArgument().isEmpty()) {

			if (instantiatedClass.isAbstract() || instantiatedClass.isInterface()) {
				
				QvtOperationalUtil.reportError(fEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_canNotInstantiateAbstractType, 
						fEnv.getFormatter().formatType(instantiatedClass)), 
						instantiationExp.getStartPosition(), 
						instantiationExp.getEndPosition());
				result = Boolean.FALSE;
			}
			return result;
		}

		// transformation instantiation
		if(instantiatedClass == null || QvtOperationalStdLibrary.INSTANCE.getTransformationClass().isSuperTypeOf(instantiatedClass) == false) {
			if(referredObject != null)	{ 
				if(referredObject.getType() != null && (referredObject.getType() instanceof org.eclipse.ocl.types.CollectionType<?,?> == false)) { 
					fEnv.reportError(NLS.bind(
							ValidationMessages.QvtOperationalValidationVisitor_invalidInstantiatedType, 
							fEnv.getFormatter().formatType(instantiatedClass)), 
							instantiationExp.getStartPosition(), 
							instantiationExp.getEndPosition());
					result = Boolean.FALSE;
				}
			}
		} else {
			Module instantiatedModule = (Module) instantiatedClass;
			if (!instantiatedModule.isIsBlackbox()) {
				ImperativeOperation mainOperation = QvtOperationalParserUtil.getMainOperation(instantiatedModule);
				if(mainOperation instanceof EntryOperation == false || 
					mainOperation.getEParameters().isEmpty() == false) {
					String message = NLS.bind(ValidationMessages.QvtOperationalValidationVisitor_ParameterlessMainExpected, instantiatedModule.getName());
					fEnv.reportError(message, instantiationExp.getStartPosition(), instantiationExp.getEndPosition());
				}
			}
		}
		
		if(instantiatedClass instanceof OperationalTransformation) {
			OperationalTransformation transf = (OperationalTransformation) instantiatedClass;
			List<org.eclipse.ocl.ecore.OCLExpression> actualArgs = instantiationExp.getArgument();
			EList<ModelParameter> formalArgs = transf.getModelParameter();
			
			if(actualArgs.size() == formalArgs.size()) {
				int i = 0;
				for (ModelParameter modelParameter : formalArgs) {
					EClassifier paramType = modelParameter.getType();
					org.eclipse.ocl.ecore.OCLExpression nextActualArg = actualArgs.get(i++);
					EClassifier expectedType = nextActualArg.getType();
					
					boolean compatible = paramType instanceof ModelType && expectedType instanceof ModelType &&
										checkCompatibleModelType((ModelType)paramType, (ModelType)expectedType);
					if(!compatible) {
						fEnv.reportError(NLS.bind(
								ValidationMessages.QvtOperationalValidationVisitor_incompatibleArgumentModelType,
								fEnv.getFormatter().formatType(nextActualArg.getType()),								
								fEnv.getFormatter().formatType(modelParameter.getEType())),								
								nextActualArg.getStartPosition(), 
								nextActualArg.getEndPosition());
						result = Boolean.FALSE;						
					}
					// check for compatible direction kind 
					
				}
			} else {
				fEnv.reportError(NLS.bind(
						ValidationMessages.QvtOperationalValidationVisitor_unresolvedTransformationSignature,
						new Object [] {
							fEnv.getFormatter().formatName(transf),
							formatArgumentList(actualArgs, fEnv.getFormatter()), 
							formatArgumentList(formalArgs, fEnv.getFormatter())
						}),
						instantiationExp.getStartPosition(), 
						instantiationExp.getEndPosition());
				result = Boolean.FALSE;				
			}
		}	
		
		return result;		
	}
		
	private String formatArgumentList(List<?> args, FormattingHelper helper) {
		StringBuilder buf = new StringBuilder();
		buf.append('(');
		int i = 0;
		for (Object nextArg : args) {
			if(i++ > 0) {
				buf.append(',').append(' ');
			}
			if(nextArg instanceof TypedElement<?>) {
				@SuppressWarnings("unchecked")
				TypedElement<EClassifier> typedElement = (TypedElement<EClassifier>) nextArg;
				buf.append(helper.formatType(typedElement.getType()));
			} 
			else if(nextArg instanceof ETypedElement) {
				buf.append(helper.formatType(((ETypedElement) nextArg).getEType()));
			} else if(nextArg instanceof EClassifier) {
				buf.append(helper.formatType(nextArg));
			}
		}
		
		buf.append(')');		
		return buf.toString();
	}

	private boolean checkCompatibleModelType(ModelType modelType1, ModelType modelType2) {
		EList<EPackage> metamodel1 = modelType1.getMetamodel();
		EList<EPackage> metamodel2 = modelType2.getMetamodel();
		if(metamodel1.size() < metamodel2.size()) {
			return false;
		}		

		LinkedList<String> uris1 = new LinkedList<String>();
		for (EPackage ePackage : metamodel1) {
			if(ePackage.getNsURI() != null) {
				uris1.add(ePackage.getNsURI());
			} else {
				return false;
			}
		};
		
		LinkedList<String> uris2 = new LinkedList<String>();
		for (EPackage ePackage : metamodel1) {
			if(ePackage.getNsURI() != null) {
				uris2.add(ePackage.getNsURI());
			} else {
				return false;
			}
		};
		
		return uris1.containsAll(uris2);
	}
	
	@Override
	public Object visitMappingCallExp(MappingCallExp mappingCallExp) {
		if(mappingCallExp.getReferredOperation() instanceof MappingOperation) {
			MappingOperation mappingOperation = (MappingOperation) mappingCallExp.getReferredOperation();
			
			if(QvtOperationalUtil.hasAbstractOutputParameter(mappingOperation) && 
					QvtOperationalParserUtil.isAbstractOperation(mappingOperation) &&
					mappingOperation.getDisjunct().isEmpty()) {
				String errMessage = NLS.bind(ValidationMessages.directCallToAbstractMappingDisallowed,
						QvtOperationalParserUtil.safeGetMappingQualifiedName(fEnv, mappingOperation));
				
				fEnv.reportError(errMessage,  mappingCallExp.getStartPosition(), mappingCallExp.getEndPosition());
			}
			
			Iterator<OCLExpression<EClassifier>> itArgument = mappingCallExp.getArgument().iterator();
			Iterator<EParameter> itParams = mappingOperation.getEParameters().iterator();
			while (itArgument.hasNext()) {
				OCLExpression<EClassifier> arg = itArgument.next();
				MappingParameter mappingParam = (MappingParameter) itParams.next();
				if (mappingParam.getKind() != DirectionKind.OUT) {
					continue;
				}

				if (!(arg instanceof VariableExp<?, ?> || arg instanceof PropertyCallExp<?, ?>)) {
					fEnv.reportError(ValidationMessages.outParamNotAnLValueError, arg.getStartPosition(), arg.getEndPosition());
				}
				
				if (arg.getType() != mappingParam.getEType()) {
					fEnv.reportError(ValidationMessages.outParamNotSameTypeError, arg.getStartPosition(), arg.getEndPosition());
				}
			}			
		}
		return super.visitMappingCallExp(mappingCallExp);
	}

	
	@Override
	public Object visitReturnExp(ReturnExp returnExp) {
		OperationBody body = QvtOperationalParserUtil.findParentElement(returnExp, OperationBody.class);
		
		if(body != null && body.getOperation() != null) {
			EClassifier actualType = returnExp.getType();			
			EClassifier declaredType = body.getOperation().getEType();
			
			if(actualType != null && declaredType != null) {
				int rel = TypeUtil.getRelationship(fEnv, actualType, declaredType);
				if((rel & UMLReflection.SUBTYPE) == 0) {
					String typeName = QvtOperationalParserUtil.safeGetQualifiedName(fEnv, declaredType);
					fEnv.reportError(NLS.bind(ValidationMessages.typeMismatchError, typeName),
							returnExp.getStartPosition(), returnExp.getEndPosition());
				}
				
				if(returnExp.getValue() == null && !body.getOperation().getResult().isEmpty()) {
					fEnv.reportError(ValidationMessages.missingReturnValueError, returnExp.getStartPosition(), returnExp.getEndPosition());					 
				}
				
			} else {
				if(actualType != null && returnExp.getValue() == null && declaredType == fEnv.getOCLStandardLibrary().getOclVoid()) {
					return Boolean.TRUE;
				}
				String typeName = QvtOperationalParserUtil.safeGetQualifiedName(fEnv, declaredType);
				fEnv.reportError(NLS.bind(ValidationMessages.typeMismatchError, typeName),
						returnExp.getStartPosition(), returnExp.getEndPosition());
			}
						
			if(body instanceof MappingBody) {
				// do not support explicit return from mapping operation yet
				fEnv.reportError(ValidationMessages.returnNotAllowedInMappingYet, returnExp.getStartPosition(), returnExp.getEndPosition());
			}
		} else {
			fEnv.reportError(ValidationMessages.returnUsedOutsideOperationBody, returnExp.getStartPosition(), returnExp.getEndPosition());
		}
		
		return super.visitReturnExp(returnExp);
	}
		
	@Override
	public Object visitMappingOperation(MappingOperation operation) {
		boolean result = MappingExtensionHelper.validate(operation, fEnv);
		
		for (VarParameter resultParam : operation.getResult()) {
			result &= validateOutParamType(resultParam);			
		}
		
		for (EParameter nextEParam : operation.getEParameters()) {
			if(nextEParam instanceof VarParameter) {
				VarParameter varParameter = (VarParameter) nextEParam;
				if(varParameter.getKind() == DirectionKind.OUT) {
					result &= validateOutParamType(varParameter);
				}
			}
		}

		return Boolean.TRUE.equals(super.visitMappingOperation(operation)) && result;
	}

	@Override
	public Object visitEntryOperation(EntryOperation entry) {
		EObject eContainer = entry.eContainer();
		if (eContainer instanceof Library) {
			fEnv.reportError(ValidationMessages.QvtOperationalValidationVisitor_MainInLibraryError, entry.getStartPosition(), entry.getEndPosition());
		}
		for (EParameter param : entry.getEParameters()) {
			if (param instanceof ASTNode) {
				QvtOperationalUtil.reportWarning(fEnv,
						NLS.bind(ValidationMessages.EntryOp_DepricatedParamDecl, null), 
						((ASTNode) param).getStartPosition(), 
						((ASTNode) param).getEndPosition());
			}
		}
		return super.visitEntryOperation(entry);
	}

	@Override
	public Object visitImperativeOperation(ImperativeOperation imperativeOperation) {
		boolean result = true;
		VarParameter context = imperativeOperation.getContext();
		if(context != null) {
			EClassifier eType = context.getEType();
			if(!isValidContextualType(eType)) {
				result = false;
				String errMessage = NLS.bind(ValidationMessages.QvtOperationalValidationVisitor_invalidContextualType, 
						QvtOperationalParserUtil.safeGetQualifiedName(fEnv, eType));
				fEnv.reportError(errMessage, context.getStartPosition(), context.getEndPosition());
			}
		}

		validateUniqueParamNames(imperativeOperation);
		// TODO - 1. validate no param name for single result param, for no explicit but default out direction kind
		
		
		for (EParameter nextEParam : imperativeOperation.getEParameters()) {
			VarParameter varParameter = (VarParameter) nextEParam;
			validateParamNameRequired(varParameter);
		}		
		
		for (VarParameter nextResultParam : imperativeOperation.getResult()) {
			validateParamNameRequired(nextResultParam);
			if(nextResultParam.getKind() != DirectionKind.OUT) {
				fEnv.reportError(ValidationMessages.QvtOperationalValidationVisitor_resultParamDirectionMustBeOut, nextResultParam.getStartPosition(), nextResultParam.getEndPosition());
			}
		}
		
		return Boolean.TRUE.equals(super.visitImperativeOperation(imperativeOperation)) && result;
	}
	
	@Override
	public Object visitOperationBody(OperationBody operationBody) {
		if(operationBody instanceof MappingBody == false) {
			ImperativeOperation operation = operationBody.getOperation();
			if(operation.getEType() == null || operation.getEType() == fEnv.getOCLStandardLibrary().getOclVoid()) {
				//return Boolean.TRUE; // continue to super type visit
			} else {
				EList<org.eclipse.ocl.ecore.OCLExpression> content = operationBody.getContent();
				if(operation.getResult().size() == 1) {
					// in case of multiple result, a tuple composed from the result variables is is always returned
					// so we do not care about the return statement
					int bodySize = content.size();
					if((bodySize == 0 && !operation.isIsBlackbox()) || 
						(bodySize > 1 && content.get(bodySize - 1) instanceof ReturnExp == false)) {
						// Note: every single expression is OK from the AST point of view as
						// it corresponds to a single expression query
						String message = ValidationMessages.useReturnExpForOperationResult;
						fEnv.reportWarning(message, operation.getStartPosition(), operationBody.getStartPosition());
					}
				}
			}
		}
		return super.visitOperationBody(operationBody);
	}
	
	@Override
	public Object visitVariableInitExp(VariableInitExp variableInitExp) {
		EObject parentExp = variableInitExp.eContainer();
		if (!(
				(parentExp instanceof OperationBody)
				|| (parentExp instanceof BlockExp)
				|| (parentExp instanceof AssignExp)
				|| ((parentExp instanceof Variable<?, ?>) 
						&& (parentExp.eContainer() != null) 
						&& (parentExp.eContainer() instanceof VariableInitExp))
				)) {
			fEnv.reportError(ValidationMessages.QvtOperationalValidationVisitor_CannotDeclareVariables, variableInitExp.getStartPosition(), variableInitExp.getEndPosition());
		}
		return super.visitVariableInitExp(variableInitExp);
	}

	private static boolean isValidContextualType(EClassifier type) {
		return type != null && !QVTUMLReflection.isModuleInstance(type);
	}

	private boolean validateOutParamType(VarParameter resultParam) {
		boolean result = true;
		EClassifier paramType = resultParam.getEType();
		if(paramType != null) {
			if(QVTUMLReflection.isModelTypeInstance(paramType) ||
				QVTUMLReflection.isModuleInstance(paramType)
				|| (!QVTUMLReflection.isUserModelElement(paramType)
				        && (paramType != null) && !(paramType instanceof CollectionType))) {
				result = false;
				fEnv.reportError(NLS.bind(ValidationMessages.nonModelTypeError, 
									QvtOperationalParserUtil.safeGetQualifiedName(fEnv, paramType)), 
									resultParam.getStartPosition(), resultParam.getEndPosition());
			}
		}
		return result;
	}
	
	private void validateUniqueParamNames(ImperativeOperation operation) {
		List<? extends VarParameter> modelParams = getModelParamsInScope(operation);
		@SuppressWarnings("unchecked")
		List<? extends VarParameter> regularParams = (List<? extends VarParameter>)operation.getEParameters();
		List<? extends VarParameter> resultParams = operation.getResult();
		validateUniqueParamNames(regularParams, modelParams);
		validateUniqueParamNames(regularParams, regularParams);
		
		validateUniqueParamNames(resultParams, modelParams);		
		validateUniqueParamNames(resultParams, regularParams);
		validateUniqueParamNames(resultParams, resultParams);	
	}
	
	private static List<ModelParameter> getModelParamsInScope(ImperativeOperation mappingOperation) {
		Module module = QvtOperationalParserUtil.getOwningModule(mappingOperation);
		return (module instanceof OperationalTransformation) ? ((OperationalTransformation)module).getModelParameter() : Collections.<ModelParameter>emptyList();
	}
		
	
	private boolean validateParamNameRequired(VarParameter param) {
		boolean result = true;
		String name = param.getName();
		if(name == null || name.trim().length() == 0) {
			result = false;
            fEnv.reportError(ValidationMessages.QvtOperationalValidationVisitor_parameterNamedRequired,
                     ((VarParameter) param).getStartPosition(), 
                     ((VarParameter) param).getEndPosition());								
		}
		return result;
	}
	
	private <T extends VarParameter> boolean validateUniqueParamNames(List<? extends T> params, List<? extends T> scopeParameters) {
		boolean result = true;		
		for (T nextParam : params) {
			if(nextParam.getName() == null) {
				// this case is handled by 
				continue; 
			}
			T sameNameParam = findParamByName(nextParam.getName(), scopeParameters);
			if(sameNameParam != null && sameNameParam != nextParam) {
				result = false;
	            fEnv.reportError(NLS.bind(ValidationMessages.NameAlreadyDefinedError,	            		 
	                     new Object[] { nextParam.getName() }),
	                     ((VarParameter) nextParam).getStartPosition(), 
	                     ((VarParameter) nextParam).getEndPosition());				
			}
		}
		return result;
	}
	
	private static <T extends EParameter> T findParamByName(String name, List<T> parameters) {
		for (T nextParam : parameters) {
			String paramName = nextParam.getName();
			if((name != null) ? name.equals(paramName) : name == paramName) {
				return nextParam;
			}
		}
		return null;
	}
}

final class CustomOclValidationVisitor extends 
		ValidationVisitor<EPackage, EClassifier, EOperation, 
				EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, 
				Constraint, EClass, EObject>
								implements QVTOperationalVisitor<Boolean>{

	private QvtOperationalValidationVisitor myDelegateVisitor = null;
	private QvtOperationalEnv myEnv;
	private UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> myUml;

	protected CustomOclValidationVisitor(QvtOperationalEnv environment) {
		super(environment);
		myEnv = environment;
		myUml = environment.getUMLReflection();
	}
	
	void setVisitor(QvtOperationalValidationVisitor visitor) {
		myDelegateVisitor = visitor;		
	}

	@Override
	public Boolean visitIfExp(org.eclipse.ocl.expressions.IfExp<EClassifier> i) {
		if (myDelegateVisitor != null) {
			myDelegateVisitor.visitIfExp(i);
			return Boolean.TRUE;
		}
		return super.visitIfExp(i);
	}

	public Boolean visitConstructor(Constructor constructor) {
		return Boolean.TRUE;
	}

	public Boolean visitConstructorBody(ConstructorBody constructorBody) {
		return Boolean.TRUE;
	}

	public Boolean visitContextualProperty(ContextualProperty contextualProperty) {
		return Boolean.TRUE;
	}

	public Boolean visitEntryOperation(EntryOperation entryOperation) {
		return Boolean.TRUE;
	}

	public Boolean visitHelper(Helper helper) {
		return Boolean.TRUE;
	}

	public Boolean visitImperativeOperation(ImperativeOperation imperativeOperation) {
		return Boolean.TRUE;
	}

	public Boolean visitLibrary(Library library) {
		return Boolean.TRUE;
	}

	public Boolean visitMappingBody(MappingBody mappingBody) {
		return Boolean.TRUE;
	}

	public Boolean visitMappingCallExp(MappingCallExp mappingCallExp) {
		return Boolean.TRUE;
	}

	public Boolean visitMappingOperation(MappingOperation mappingOperation) {
		return Boolean.TRUE;
	}

	public Boolean visitModelType(ModelType modelType) {
		return Boolean.TRUE;
	}

	public Boolean visitModule(Module module) {
		return Boolean.TRUE;
	}

	public Boolean visitModuleImport(ModuleImport moduleImport) {
		return Boolean.TRUE;
	}

	public Boolean visitObjectExp(ObjectExp objectExp) {
		return Boolean.TRUE;
	}

	public Boolean visitOperationBody(OperationBody operationBody) {
		return Boolean.TRUE;
	}

	public Boolean visitResolveExp(ResolveExp resolveExp) {
		return Boolean.TRUE;
	}

	public Boolean visitResolveInExp(ResolveInExp resolveInExp) {
		return Boolean.TRUE;
	}

	public Boolean visitVarParameter(VarParameter varParameter) {
		return Boolean.TRUE;
	}

	public Boolean visitAltExp(AltExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitAssertExp(AssertExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitAssignExp(AssignExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitBlockExp(BlockExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitBreakExp(BreakExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitCatchtExp(CatchExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitComputeExp(ComputeExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitContinueExp(ContinueExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitDictLiteralExp(DictLiteralExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitDictLiteralPart(DictLiteralPart astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitForExp(ForExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitImperativeIterateExp(ImperativeIterateExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitInstantiationExp(InstantiationExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitLogExp(LogExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitOrderedTupleLiteralExp(OrderedTupleLiteralExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitOrderedTupleLiteralPart(OrderedTupleLiteralPart astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitRaiseExp(RaiseExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitReturnExp(ReturnExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitSwitchExp(SwitchExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitTryExp(TryExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitUnlinkExp(UnlinkExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitUnpackExp(UnpackExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitVariableInitExp(VariableInitExp astNode) {
		return Boolean.TRUE;
	}

	public Boolean visitWhileExp(WhileExp astNode) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visitCollectionLiteralExp(CollectionLiteralExp<EClassifier> cl) {
		if (cl.getType() instanceof ListType) {
			return Boolean.TRUE;
		}
		return super.visitCollectionLiteralExp(cl);
	}

	private String getElementName(Object element) {
		return (element == null)? null : myUml.getName(element);
	}

    private boolean isStatic(Object feature) {
        return (myUml != null) && myUml.isStatic(feature);
    }

    public Boolean visitFeatureCallExp(FeatureCallExp<EClassifier> exp) {
		if (exp.isMarkedPre()) {
			// check for a postcondition constraint
			if (!myEnv.isInPostcondition(exp)) {
				String message = ValidationMessages.AtPreInPostcondition_ERROR_;
				return validatorError(exp, message, "visitFeatureCallExp");//$NON-NLS-1$
			}
		}
        
        // check for static access to non-static features
        if (exp.getSource() != null) {
            OCLExpression<EClassifier> source = exp.getSource();
            
            if (source.getType() instanceof TypeType<?, ?>) {
                @SuppressWarnings("unchecked")
                TypeType<EClassifier, ?>  typeType = (TypeType<EClassifier, ?>) source.getType();
                
                Object feature = null;
                
                if (exp instanceof OperationCallExp<?, ?>) {
                    feature = ((OperationCallExp<?, ?>) exp).getReferredOperation();
                    
                    // operation must either be defined by the TypeType
                    //    (e.g., allInstances()) or be a static operation of
                    //    the referred classifier
                    if (!(typeType.oclOperations().contains(feature)
                            || isStatic(feature))) {
                        String message = ValidationMessages.bind(
                            ValidationMessages.NonStaticOperation_ERROR_,
                            getElementName(feature));
                        return validatorError(exp, message, "visitFeatureCallExp");//$NON-NLS-1$
                    }
                } else if (exp instanceof PropertyCallExp<?, ?>) {
                    feature = ((PropertyCallExp<?, ?>) exp).getReferredProperty();
                    
                    // property must be a static attribute of
                    //    the referred classifier
                    if (!isStatic(feature)) {
                        String message = ValidationMessages.bind(
                            ValidationMessages.NonStaticAttribute_ERROR_,
                            getElementName(feature));
                        return validatorError(exp, message, "visitFeatureCallExp");//$NON-NLS-1$
                    }
                }
            }
        }
        return Boolean.FALSE;
	}

	// Overridden due to bug 271987. FQN operations validation in MDT OCL
	// cannot be applied here. See comment below.
	@Override
	public Boolean visitOperationCallExp(
			OperationCallExp<EClassifier, EOperation> oc) {
		OCLExpression<EClassifier> source = oc.getSource();
		EOperation oper = oc.getReferredOperation();
		int opcode = oc.getOperationCode();
		List<OCLExpression<EClassifier>> args = oc.getArgument();

		if (oper == null) {
			String message = ValidationMessages.bind(
					ValidationMessages.NullOperation_ERROR_,
					oc.toString());
			return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
		}

		if (source == null) {
			String message = ValidationMessages.bind(
					ValidationMessages.NullSourceOperation_ERROR_,
					oc.toString());
			return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
		}

		EClassifier sourceType = source.getType();
		String operName = getElementName(oper);

		for (OCLExpression<EClassifier> expr : args) {
			expr.accept(this);
		}
		
		if (visitFeatureCallExp(oc)) {
            return Boolean.TRUE;
        }
		
		if (opcode == PredefinedType.OCL_IS_NEW) {
			// oclIsNew() may only be used in postcondition constraints
			if (!myEnv.isInPostcondition(oc)) {
				return validatorError(oc, ValidationMessages.OCLIsNewInPostcondition_ERROR_, "visitOperationCallExp");//$NON-NLS-1$
			}
		}
		
		source.accept(this);

		// NB: This check is incorrect for FQN operation calls
//		// Check argument conformance.
//		O oper1 = env.lookupOperation(sourceType,
//			operName, args);
//		if (oper1 != oper) {
//			String message = ValidationMessages.bind(
//					ValidationMessages.IllegalOperation_ERROR_,
//					oc.toString());
//			return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
//		}
		
		if (!myUml.isQuery(oper)) {
			String message = ValidationMessages.bind(
					ValidationMessages.NonQueryOperation_ERROR_,
					getElementName(oper));
			return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
		}
		
		EClassifier resultType;

		if (TypeUtil.isStandardLibraryFeature(myEnv, sourceType, oper)) {
			if (opcode != OCLStandardLibraryUtil.getOperationCode(operName)) {
				String message = ValidationMessages.bind(
						ValidationMessages.IllegalOpcode_ERROR_,
						operName);
				return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
			}
			
			resultType = TypeUtil
				.getResultType(oc, myEnv, sourceType, oper, args);
			
			if (resultType == null) {
				// maybe this operation was an "extra" contribution by a
				//    custom environment implementation
				resultType = getOCLType(oper);
			}
		} else if (TypeUtil.isOclAnyOperation(myEnv, oper)) {
			// source is an EClass, an enumeration, or a user data type and
			//   operation is defined by OclAny (not the source, itself)
			if (opcode != OCLStandardLibraryUtil.getOclAnyOperationCode(operName)) {
				String message = ValidationMessages.bind(
						ValidationMessages.IllegalOpcode_ERROR_,
						operName);
				return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
			}
			
			resultType = TypeUtil
				.getResultType(oc, myEnv, sourceType, oper, args);
			
			if (resultType == null) {
				resultType = getOCLType(oper);
			}
		} else {
			// user-defined operation
			resultType = TypeUtil
				.getResultType(oc, myEnv, sourceType, oper, args);
		}
		
		if (!TypeUtil.exactTypeMatch(myEnv, resultType, oc.getType())) {
			String message = ValidationMessages.bind(
					ValidationMessages.TypeConformanceOperation_ERROR_,
					oc.getType().toString());
			return validatorError(oc, message, "visitOperationCallExp");//$NON-NLS-1$
		}
		
		if ((opcode == PredefinedType.TO_LOWER) || (opcode == PredefinedType.TO_UPPER)) {
			// check settings for using non-standard closure iterator
			ProblemHandler.Severity sev = ProblemHandler.Severity.OK;
			BasicEnvironment benv = OCLUtil.getAdapter(myEnv, BasicEnvironment.class);
			
			if (benv != null) {
				sev = benv.getValue(ProblemOption.STRING_CASE_CONVERSION);
			}
			if ((sev != null) && (sev != ProblemHandler.Severity.OK)) {
                benv.problem(
                        sev,
                        ProblemHandler.Phase.VALIDATOR,
                        ValidationMessages.bind(
                                ValidationMessages.NonStd_Operation_,
                                (opcode == PredefinedType.TO_LOWER) ? "String::toLower()" //$NON-NLS-1$
                                    : "String::toUpper()"), "operationCallExp", //$NON-NLS-1$ //$NON-NLS-2$
                        oc);
            }
		}
		
		return Boolean.TRUE;
	}
	

	String getUMLName(Object element) {
		return (element == null)? null : uml.getName(element);
	}

	//@Override
	public Boolean visitIteratorExp(
			org.eclipse.ocl.expressions.IteratorExp<EClassifier, EParameter> ie) {
		EClassifier type = ie.getType();
		OCLExpression<EClassifier> body = ie.getBody();
		OCLExpression<EClassifier> source = ie.getSource();
		List<Variable<EClassifier, EParameter>> iterators = ie.getIterator();
		String name = ie.getName();

		if (type == null || name == null || source == null || body == null || iterators.isEmpty()) {
			QvtOperationalUtil.reportError(myEnv,
					NLS.bind(ValidationMessages.QvtOperationalVisitorCS_iterateExpIncomplete, new Object[] { }),
					ie.getStartPosition(), ie.getEndPosition());
			return Boolean.TRUE;
		}
		
		int opcode = 0;
		if (source.getType() instanceof PredefinedType<?>) {
			opcode = OCLStandardLibraryUtil.getOperationCode(name);
		}
		
		// Validate all of the iterate parts
		source.accept(this);
		body.accept(this);

		switch (opcode) {
		case PredefinedType.FOR_ALL:
		case PredefinedType.EXISTS:
		case PredefinedType.IS_UNIQUE:
			if (type != env.getOCLStandardLibrary().getBoolean()) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceIteratorResult_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
		}
		
		if (opcode == PredefinedType.COLLECT) {
			if (source.getType() instanceof SequenceType<?, ?>
				|| source.getType() instanceof OrderedSetType<?, ?>
				|| source.getType() instanceof ListType) {
				if (!(type instanceof SequenceType<?, ?>)) {
					QvtOperationalUtil.reportError(myEnv,
							NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceCollectSequence_ERROR_, new Object[] { }),
							ie.getStartPosition(), ie.getEndPosition());
					return Boolean.TRUE;
				}
			} else if (!(type instanceof BagType<?, ?>)) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceCollectBag_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
		}
		
		switch (opcode) {
		case PredefinedType.SELECT:
		case PredefinedType.REJECT:
			if (source.getType() instanceof ListType && type instanceof SequenceType<?, ?>) {
				// OK
			}
			else if (!TypeUtil.exactTypeMatch(env, type, source.getType())) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceSelectReject_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
		}

		switch (opcode) {
		case PredefinedType.SELECT:
		case PredefinedType.REJECT:
		case PredefinedType.FOR_ALL:
		case PredefinedType.ANY:
		case PredefinedType.EXISTS:
		case PredefinedType.ONE:
			if (body.getType() != env.getOCLStandardLibrary().getBoolean()) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceIteratorBodyBoolean_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
		}

		EClassifier sourceType = source.getType();
		if (!(sourceType instanceof org.eclipse.ocl.types.CollectionType<?, ?>)) {
			QvtOperationalUtil.reportError(myEnv,
					NLS.bind(ValidationMessages.QvtOperationalVisitorCS_IteratorSource_ERROR_, new Object[] { }),
					ie.getStartPosition(), ie.getEndPosition());
			return Boolean.TRUE;
		}
		
		if (opcode == PredefinedType.CLOSURE) {
			// check settings for using non-standard closure iterator
			ProblemHandler.Severity sev = ProblemHandler.Severity.OK;
			BasicEnvironment benv = OCLUtil.getAdapter(env, BasicEnvironment.class);
			
			if (benv != null) {
				sev = benv.getValue(ProblemOption.CLOSURE_ITERATOR);
				if ((sev != null) && (sev != ProblemHandler.Severity.OK)) {
					@SuppressWarnings("restriction")
					String message = OCLMessages.bind(OCLMessages.NonStd_Iterator_, PredefinedType.CLOSURE_NAME);
	                benv.problem(sev, ProblemHandler.Phase.VALIDATOR, message, "iteratorExp", ie); //$NON-NLS-1$
	            }
			}
			
			if (!(type instanceof SetType<?, ?>) && !(type instanceof OrderedSetType<?, ?>)) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceClosure_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
			
			// recursive reference must be to a type conforming
			//   to the source, otherwise it isn't recursive
			
			// checked above that the source is a collection type
			@SuppressWarnings("unchecked")
			org.eclipse.ocl.types.CollectionType<EClassifier, EOperation> sourceCT = (org.eclipse.ocl.types.CollectionType<EClassifier, EOperation>) source.getType();
			@SuppressWarnings("unchecked")
			org.eclipse.ocl.types.CollectionType<EClassifier, EOperation> bodyCT = (org.eclipse.ocl.types.CollectionType<EClassifier, EOperation>) type;
			
			EClassifier sourceElementType = sourceCT.getElementType();
			EClassifier bodyType = bodyCT.getElementType();
			
			if (!TypeUtil.compatibleTypeMatch(env, bodyType, sourceElementType)) {
				@SuppressWarnings("restriction")
				String message = OCLMessages.bind(
						OCLMessages.ElementTypeConformanceClosure_ERROR_,
						getUMLName(bodyType),
						getUMLName(sourceElementType));
					return validatorError(ie, message,  "visitIteratorExp");//$NON-NLS-1$
			}
		}
        
        if (opcode == PredefinedType.SORTED_BY) {
            // the body type must be comparable (in OCL terms, it must
            //   define the '<' operation)
            
            if (!uml.isComparable(body.getType())) {
                // FIXME: Should be more specifically about the sortedBy iterator
                @SuppressWarnings("restriction")
				String message = OCLMessages.bind(
                    OCLMessages.OperationNotFound_ERROR_,
                    PredefinedType.LESS_THAN_NAME,
                    getUMLName(body.getType()));
                return validatorError(ie, message, "visitIteratorExp");//$NON-NLS-1$
            }
        }

        // validate the number of iterators
        switch (opcode) {
        case PredefinedType.FOR_ALL:
        case PredefinedType.EXISTS:
            if (iterators.size() > 2) {
                @SuppressWarnings("restriction")
				String message = OCLMessages.bind(
                    OCLMessages.TooManyIteratorVariables_ERROR_,
                    ie.getName());
                return validatorError(ie, message, "visitIteratorExp");//$NON-NLS-1$
            }
            break;
        default:
            if (iterators.size() > 1) {
                @SuppressWarnings("restriction")
				String message = OCLMessages.bind(
                    OCLMessages.TooManyIteratorVariables_ERROR_,
                    ie.getName());
                return validatorError(ie, message, "visitIteratorExp");//$NON-NLS-1$
            }
        }
        
		for (Variable<EClassifier, EParameter> loopiter : iterators) {
			// Validate the iterator expressions
			loopiter.accept(this);
			if (loopiter.getInitExpression() != null) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_IterateExpLoopVarInit_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
			
			@SuppressWarnings("unchecked")
			org.eclipse.ocl.types.CollectionType<EClassifier, EOperation> ct = (org.eclipse.ocl.types.CollectionType<EClassifier, EOperation>) sourceType;
			
			if (!TypeUtil.exactTypeMatch(env, loopiter.getType(), ct.getElementType())) {
				QvtOperationalUtil.reportError(myEnv,
						NLS.bind(ValidationMessages.QvtOperationalVisitorCS_TypeConformanceIteratorExpLoopVar_ERROR_, new Object[] { }),
						ie.getStartPosition(), ie.getEndPosition());
				return Boolean.TRUE;
			}
		}

		return Boolean.TRUE;
	}
	
	public Boolean visitVariableExp(VariableExp<EClassifier, EParameter> variableExp) {
		if (variableExp.getType() instanceof TupleType &&
        	variableExp.getReferredVariable().getName().equals(Environment.RESULT_VARIABLE_NAME)) {
     
			EParameter param = variableExp.getReferredVariable().getRepresentedParameter();
			if (param == null) {
				// tuple result variable doesn't represent a parameter => deprecated synthetic result tuple
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=432112
				QvtOperationalUtil.reportWarning(myEnv, NLS.bind(ValidationMessages.QvtOperationalVisitorCS_deprecatedResultTupleAccess, new Object[] {}), variableExp.getStartPosition(), variableExp.getEndPosition());
			}	
        }
		
		return super.visitVariableExp(variableExp);
	}
}