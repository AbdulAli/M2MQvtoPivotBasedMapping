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
package org.eclipse.m2m.internal.qvt.oml.stdlib.model;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

public interface StdlibPackage {

	EClassifier getObject();
	
	EClass getModelClass();

	EClass getStatusClass();

	EClass getExceptionClass();

	EClass getAssertionFailedClass();

	EClass getTransformationClass();

	EClass getModuleType();

	EClassifier getElementType();
	
	EClassifier getList();

	EClassifier getDictionary();
	
	EClassifier getKeyT();
	
	EClassifier getCommonT();
	
	EClassifier getOrderedTupleType();	
}
