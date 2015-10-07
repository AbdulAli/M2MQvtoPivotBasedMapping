/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
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

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;


class RuntimePropertyShadowHelper {
	private EPackage fPackage;
	private Map<EClass, EClass> fReal2ShadowMap;
		
	RuntimePropertyShadowHelper() {
		fReal2ShadowMap = new IdentityHashMap<EClass, EClass>();
		fPackage = EcoreFactory.eINSTANCE.createEPackage();
		fPackage.setName("interm_classes"); //$NON-NLS-1$
	}
	
	public EStructuralFeature createShadowProperty(ContextualProperty property) {
		if(property.getContext() == null) {
			throw new IllegalArgumentException();
		}

		return addProperty(property, property.getContext());		
	}
	
	private EStructuralFeature addProperty(ContextualProperty property, EClass owner) {
		EClass shadowClass = getShadow(owner);
		
		EStructuralFeature shadowProperty;
		String name = property.getName();		
		EClassifier type = property.getEType();
		if (type instanceof EClass) {
			shadowProperty = EcoreFactory.eINSTANCE.createEReference();
		} else {
			shadowProperty = EcoreFactory.eINSTANCE.createEAttribute();
		}
		
		shadowProperty.setName(name);
		shadowProperty.setEType(type);
		shadowClass.getEStructuralFeatures().add(shadowProperty);
		
		return shadowProperty;
	}
		
	private EClass getShadow(EClass realClass) {
		EClass result = fReal2ShadowMap.get(realClass);
		if(result == null) {
			result = createShadow(realClass);
			fReal2ShadowMap.put(realClass, result);
		}
		return result;
	}
	
	private EClass createShadow(EClass realClass) {
		EClass shadowClass = EcoreFactory.eINSTANCE.createEClass();
		shadowClass.setName(realClass.getName());		

		fPackage.getEClassifiers().add(shadowClass);		
		fReal2ShadowMap.put(realClass, shadowClass);				
		return shadowClass;
	}
}
