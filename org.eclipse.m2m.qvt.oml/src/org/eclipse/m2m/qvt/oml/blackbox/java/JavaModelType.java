/*******************************************************************************
 * Copyright (c) 2014 S.Boyko and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Sergey Boyko - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.blackbox.java;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * Provides a mapping of QVT <code>qvt.oml.expressions.ModelType</code> classifier for blackbox Java modules.
 * 
 * @since 3.4
 */
public interface JavaModelType {
	
	/**
	 * Returns 'Ecore' for the model type declaration below
	 * <pre>
	 * modeltype Ecore "strict" uses ecore('http://www.eclipse.org/emf/2002/Ecore');
	 * </pre>
	 * 
	 * @return name of the model type
	 */
	String getName();

	/**
	 * List of metamodels (<code>org.eclipse.emf.ecore.EPackage</code> instances) used in declaration of this model type.
	 *  
	 * @return List of metamodels the model type consists of
	 */
	List<EPackage> getMetamodels();
	
}
