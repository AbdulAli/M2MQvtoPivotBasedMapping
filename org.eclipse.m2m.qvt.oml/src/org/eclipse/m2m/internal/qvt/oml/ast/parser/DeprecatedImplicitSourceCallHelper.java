/*******************************************************************************
 * Copyright (c) 2008, 2009 Borland Software Corporation and others.
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTParsingOptions;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.cst.ObjectExpCS;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.CallExpCS;
import org.eclipse.ocl.cst.OCLExpressionCS;
import org.eclipse.ocl.cst.SimpleNameCS;
import org.eclipse.ocl.cst.VariableExpCS;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.CallExp;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;

/**
 * Used to perform validation checks on obsolete usage of calls on implicit source object.
 * TODO -
 *  1) Perform appropriate renamings, to get rid of being a deprecation issue, but a normal validation.
 * 	2) Move me to the central QVT validator class
 */
class DeprecatedImplicitSourceCallHelper {

	/**
	 * Checks the given call expression if it does not used obsolete calls to
	 * implicit source object.
	 * <p>
	 * <li/> <code>self</code> is not allowed to be implicitly resolved within
	 * the contextual operation scope.
	 * <li/> <code>this</code> is not allowed to be implicitly resolved when calling non-module owned
	 * operation/property
	 * 
	 * @param callExpCS
	 *            CST node representing the call, can be {@link VariableExpCS}
	 *            or {@link CallExpCS}
	 * @param resultAST
	 *            AST representation of the call expression
	 * @param env
	 *            the environment to receive validation problems
	 */
	static void validateCallExp(OCLExpressionCS callExpCS, CallExp<EClassifier> resultAST, Environment<EPackage, EClassifier, EOperation, EStructuralFeature,
			EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint,
			EClass, EObject> env) {
	
		CSTNode causeNode = null;
		if(callExpCS instanceof CallExpCS) {
			CallExpCS actualCallExpCS = (CallExpCS) callExpCS;
			if(actualCallExpCS.getSource() != null) {
				return;
			}
			
			causeNode = (actualCallExpCS.getSimpleNameCS() != null) ? actualCallExpCS.getSimpleNameCS() : callExpCS;
		} 
		else if(callExpCS instanceof VariableExpCS && resultAST instanceof PropertyCallExp<?, ?>) {
			// property call using implicit source
			causeNode = callExpCS;
		}
		
		if(causeNode != null) {
			// call performed on implicit source
			if(resultAST instanceof CallExp<?>) {
				CallExp<EClassifier> call = (CallExp<EClassifier>) resultAST;
				if(call.getSource() instanceof VariableExp<?, ?>) {
					@SuppressWarnings("unchecked")
					VariableExp<EClassifier, EParameter> varExp = (VariableExp<EClassifier, EParameter>) call.getSource();     				
					Variable<EClassifier, EParameter> refVar = varExp.getReferredVariable();
					
					if(refVar != null && refVar.getName() != null) {
						String refVarName = refVar.getName();
						if(QvtOperationalEnv.SELF_VARIABLE_NAME.equals(refVarName)) {
						    boolean isImplicitSelfOk = false;
						    EObject tempCS = callExpCS;
						    while (tempCS != null) {
						        if (tempCS instanceof ObjectExpCS) {
						        	ObjectExpCS objectExpCS = (ObjectExpCS) tempCS;
						            SimpleNameCS referredObject = objectExpCS.getSimpleNameCS();
                                    if (referredObject != null) {
                                        if (QvtOperationalEnv.SELF_VARIABLE_NAME.equals(referredObject.getValue())) {
                                            isImplicitSelfOk = true;
                                            break;
                                        }
						            }
						        }
						        tempCS = tempCS.eContainer();
						    }
						    if (!isImplicitSelfOk && isExplicitSelfEnforced(env)) {
						        QvtOperationalUtil.reportError(env, ValidationMessages.DeprecatedImplicitSourceCall_contextualImplicitCall, causeNode);
						    }
						} 
						else if(refVarName != null && refVarName.equals(QvtOperationalEnv.THIS)) {
							if(resultAST instanceof OperationCallExp<?, ?>) {
								@SuppressWarnings("unchecked")
								OperationCallExp<EClassifier, EOperation> operCall = (OperationCallExp<EClassifier, EOperation>)resultAST;
								EOperation referredOperation = operCall.getReferredOperation();
								
								if(referredOperation != null && !isModuleOperation(referredOperation, env)) {
									QvtOperationalUtil.reportWarning(env, ValidationMessages.DeprecatedImplicitSourceCall_moduleScopeImplicitCall, 
		    								causeNode);
								}
							} else if(resultAST instanceof PropertyCallExp<?, ?>) {
								@SuppressWarnings("unchecked")
					        	PropertyCallExp<EClassifier, EStructuralFeature> propCall = (PropertyCallExp<EClassifier, EStructuralFeature>) resultAST;					        	
								EStructuralFeature referredProperty = propCall.getReferredProperty();
								
								if(referredProperty != null && !isModuleProperty(referredProperty, env)) {
									QvtOperationalUtil.reportWarning(env, ValidationMessages.DeprecatedImplicitSourceCall_moduleScopeImplicitCall, 
		    								causeNode);
								}    				        	
							}    						    						
						}						
					}
					
				}
			}
		}
	}

	private static boolean isModuleOperation(EOperation operation, Environment<EPackage, EClassifier, EOperation, EStructuralFeature,
			EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint,
			EClass, EObject> env) {
		EClassifier owningClassifier = env.getUMLReflection().getOwningClassifier(operation);
		return owningClassifier instanceof Module;
	}

	private static boolean isModuleProperty(EStructuralFeature feature, Environment<EPackage, EClassifier, EOperation, EStructuralFeature,
			EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint,
			EClass, EObject> env) {
		return env.getUMLReflection().getOwningClassifier(feature) instanceof Module;
	}
	
	private static boolean isExplicitSelfEnforced(Environment<EPackage, EClassifier, EOperation, EStructuralFeature,
			EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env) {
		return Boolean.TRUE.equals(QVTParsingOptions.getValue(env, QVTParsingOptions.ENFORCE_EXPLICIT_SELF_VARIABLE));
	}
}
