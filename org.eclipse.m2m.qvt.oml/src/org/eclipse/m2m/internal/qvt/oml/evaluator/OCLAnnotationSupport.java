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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.ExpressionInOCL;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCLExpression;

/**
 * This class is intended to assist in utilization of OCL (derive, body) constraints in
 * EOperation evalutation in QVT scripts.   
 */
public class OCLAnnotationSupport {
	
	/**
	 * Handles parsing problems of OCL expressions attached in OCL related EAnnotations.
	 */
	public interface ParseErrorHandler {
		/**
		 * Handling operation called on parsing errors.
		 * <p>
		 * @param parserException the causing parser exception
		 * @param targetElement the element to which the failed OCL annotation is targeted
		 * 
		 * @return <code>null</code> or a default expression to be used instead of the invalid one.
		 */
		OCLExpression handleError(ParserException parserException, EModelElement targetElement);
	}
	
	private static class EOperationBinding {
		final EClass targetType;
		final EOperation operation;
		
		EOperationBinding(EClass targetType, EOperation operation) {
			assert targetType != null && operation != null;
			
			this.targetType = targetType;
			this.operation = operation;
		}
	}
	
	private static final String OCL_SOURCE_URI = "http://www.eclipse.org/2007/OCL";	 //$NON-NLS-1$
	private static final String BODY = "body";	 //$NON-NLS-1$
	private static final String DERIVE = "derive"; //$NON-NLS-1$
		
	private OCL oclInstance;
	private Map<EOperation, List<EOperationBinding>> operationMap;
	private Map<ETypedElement, ExpressionInOCL> element2ExprMap;
	private ParseErrorHandler errorHandler;
	
	
	public OCLAnnotationSupport() {
		oclInstance = OCL.newInstance();
		
		operationMap = new HashMap<EOperation, List<EOperationBinding>>();
		element2ExprMap = new HashMap<ETypedElement, ExpressionInOCL>();
	}
	
	public void setErrorHandler(ParseErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

    public OCLExpression getDerivedProperty(EStructuralFeature feature) {
    	return getOCLExpression(feature, feature.getEContainingClass(), DERIVE);
    }
    
    public OCLExpression getBody(EOperation operation) {
    	return getOCLExpression(operation, operation, BODY);
    }
    	
	public EOperation resolveDynamic(EOperation compiledOperation, EObject targetInstance) {
		return resolveRecursiveDepthLast(compiledOperation, targetInstance.eClass());
	}
	
	public static boolean isDynamicInstance(EObject instance) {
		return instance != null && instance.eClass() != null && instance.eClass().getInstanceClass() == null;
	}
	
	public static boolean isDynamicClassFeature(EStructuralFeature feature) {
		EClass owner = feature.getEContainingClass();
		return owner != null && owner.getInstanceClass() == null;
	}
	
	public static boolean isDynamicClassOperation(EOperation operation) {
		EClass owner = operation.getEContainingClass();
		return owner != null && owner.getInstanceClass() == null;		
	}
	
    private OCLExpression getOCLExpression(ETypedElement element, Object context, String kind) {
    	ExpressionInOCL result = this.element2ExprMap.get(element);
    	if(result != null) {
    		return (OCLExpression)result.getBodyExpression();
    	}
    	
    	EAnnotation annotation = element.getEAnnotation(OCL_SOURCE_URI);
    	if(annotation != null) {
    		String body = annotation.getDetails().get(kind);
    		if(body == null) {
    			return null;
    		}
    		
    		OCL.Helper oclHelper = oclInstance.createOCLHelper();
    		
    		if(context instanceof EClassifier) {
    			oclHelper.setContext((EClassifier)context);
    		} else if(context instanceof EOperation) {
    			EOperation operation = (EOperation)context;
    			oclHelper.setOperationContext(operation.getEContainingClass(), operation);
    		} else {
    			throw new IllegalArgumentException("Unsupported OCL annotation context"); //$NON-NLS-1$ 
    		}
			
    		result = org.eclipse.ocl.ecore.EcoreFactory.eINSTANCE.createExpressionInOCL();
    		this.element2ExprMap.put(element, result);
    		try {
	    		OCLExpression parsedBody = oclHelper.createQuery(body);
	    		result.setBodyExpression(parsedBody);
	    		return parsedBody;
	    		
    		} catch (ParserException e) {
    			if(errorHandler != null) {
    				result.setBodyExpression(errorHandler.handleError(e, element));
    			}
			}
    		
    		return (OCLExpression)result.getBodyExpression();
    	}
    	
    	return null;
    }	
	
	private EOperation resolveRecursiveDepthLast(EOperation compiledOperation, EClass targetType) {
		EOperation resolvedOper = resolveDynamic(compiledOperation, targetType);
		if(resolvedOper == null) {
			for (EClass superType : targetType.getESuperTypes()) {
				resolvedOper = resolveDynamic(compiledOperation, superType);
				if(resolvedOper != null) {
					List<EOperationBinding> infos = getOperInfos(compiledOperation);
					infos.add(new EOperationBinding(targetType, resolvedOper));
					
					infos = getOperInfos(resolvedOper);
					infos.add(new EOperationBinding(targetType, resolvedOper));					
					
					return resolvedOper;
				}
			}
			
			for (EClass superType : targetType.getESuperTypes()) {
				for (EClass nextSuperType : superType.getESuperTypes()) {
					resolvedOper = resolveRecursiveDepthLast(compiledOperation, nextSuperType);
					if(resolvedOper != null) {
						List<EOperationBinding> infos = getOperInfos(compiledOperation);
						infos.add(new EOperationBinding(superType, resolvedOper));												
						infos.add(new EOperationBinding(targetType, resolvedOper));						
						
						if(resolvedOper != compiledOperation) {
							infos = getOperInfos(resolvedOper);
							infos.add(new EOperationBinding(superType, resolvedOper));												
							infos.add(new EOperationBinding(targetType, resolvedOper));
						}

						return resolvedOper;
					}
				}
			}
		}
		
		return resolvedOper;
	}

	private List<EOperationBinding> getOperInfos(EOperation operation) {
		List<EOperationBinding> bindings = operationMap.get(operation);
		if(bindings == null) {
			bindings = new LinkedList<EOperationBinding>();
			operationMap.put(operation, bindings);
		}
		return bindings;
	}
	
	private EOperation resolveDynamic(EOperation compiledOperation, EClass targetType) {		
		EClass owner = compiledOperation.getEContainingClass();
		if(owner == null || targetType == null) {
			return null;
		}

		if(targetType == compiledOperation.getEContainingClass()) {
			return compiledOperation;
		}
				
		List<EOperationBinding> bindings = getOperInfos(compiledOperation);
		for (EOperationBinding operInfo : bindings) {
			if(operInfo.targetType == targetType) {
				return operInfo.operation;
			}
		}

		EOperation actualOperation = findLocalOperationMatch(targetType, compiledOperation);
		if(actualOperation != null) {
			bindings.add(new EOperationBinding(targetType, actualOperation));
		}
		
		return actualOperation;
	}
		
	private EOperation findLocalOperationMatch(EClass targetType, EOperation operation) {
		List<EOperation> opers = targetType.getEOperations();
		for (EOperation nextOper : opers) {
			if(canOverride(nextOper, operation)) {
				return nextOper;
			}
		}
		
		return null;
	}
	
	private static boolean canOverride(EOperation o1, EOperation o2) {
		if(safeEquals(o1.getName(), o2.getName()) && 
			o1.getEParameters().size() == o2.getEParameters().size() &&
			safeEquals(o1.getEType(), o2.getEType()) ) {
			
			EList<EParameter> params1 = o1.getEParameters();
			EList<EParameter> params2 = o2.getEParameters();
			assert params1.size() == params2.size();
			
			int count = params1.size();
			for (int i = 0; i < count; i++) {
				if(matches(params1.get(i), params2.get(i)) == false) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private static boolean matches(EParameter p1, EParameter p2) {
		return safeEquals(p1.getEType(), p2.getEType()) && 
			p1.isMany() == p2.isMany() && 
			p1.isOrdered() == p2.isOrdered() &&
			p1.isUnique() == p2.isUnique();
	}
	
	private static boolean safeEquals(Object o1, Object o2) {
		return o1 != null ? o1.equals(o2) : (o2 != null ? o2.equals(o1) : o1 == o2);
	}	
}
