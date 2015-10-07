/*******************************************************************************
 * Copyright (c) 2009, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;

public interface QVTOStandardLibrary {
	
	Library getStdLibModule();

	EClassifier getList();

	EClassifier getDictionary();

	EClassifier getOrderedTupleType();

	EClassifier getKeyT();

	EClassifier getCommonT();

	EClass getModelClass();

	EClass getStatusClass();

	EClass getExceptionClass();

	EClass getTransformationClass();

	EClass getModuleType();

	EClassifier getElementType();

	EClassifier getObject();
}