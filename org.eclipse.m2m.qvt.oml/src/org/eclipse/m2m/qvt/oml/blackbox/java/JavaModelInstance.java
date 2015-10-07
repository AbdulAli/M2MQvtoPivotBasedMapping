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

/**
 * Represents an abstraction of a model instance (transformation parameter)
 * passed to Java implementation of a blackbox transformation.
 * 
 * @since 3.4
 */
public interface JavaModelInstance {
	
	/**
	 * Obtains model type of this model instance. This is a regular 'type - instance' relation.
	 * 
	 * @return model type representation
	 */
	JavaModelType getType();
	
	/**
	 * Obtains model extent of this model instance. Model instance always has exactly one associated 
	 * model extent.
	 * 
	 * @return model extent representation
	 */
	JavaModelExtent getExtent();
	
}
