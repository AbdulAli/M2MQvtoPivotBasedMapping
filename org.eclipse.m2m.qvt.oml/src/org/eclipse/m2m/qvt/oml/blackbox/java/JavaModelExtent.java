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

import org.eclipse.emf.ecore.EObject;

/**
 * Represents an abstraction of a model content (that is MOF extent corresponding to each parameter)
 * passed to Java implementation of a blackbox transformation.
 * 
 * @since 3.4
 */
public interface JavaModelExtent {
	
	/**
	 * Adds non-null object to the model extent. 
	 * 
	 * @param obj instance to be added to the extent
	 */
	void addObject(EObject obj);
	
	/**
	 * Removes non-null object of the model extent. All links that the object have with other objects 
	 * in the extent are deleted.
	 * 
	 * @param obj instance to be removed from the extent
	 */
	void removeObject(EObject obj);

	/**
	 * Gets the list of root objects belong to this model extent. Root object is the one that 
	 * either does not have parent object (obj.eContainer() == null)
	 * or initially appear in the model extent before transformation execution begins. 
	 * 
	 * @return list of root objects or an empty list
	 */
	List<EObject> getRootObjects();

	/**
	 * Gets the list of all objects belong to this model extent. 
	 * 
	 * @return list of all objects or an empty list
	 */
	List<Object> getAllObjects();
	
}
