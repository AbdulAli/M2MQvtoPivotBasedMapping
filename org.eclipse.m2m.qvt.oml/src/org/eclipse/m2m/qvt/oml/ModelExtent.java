/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * The interface represents a simplified abstraction of a model contents, 
 * passed to a transformation in a model parameter.
 * 
 * @since 2.0
 */
public interface ModelExtent {

	/**
	 * The read-only view of this model extent's contents.
	 * 
	 * @return list of elements contained in this extent
	 */
	List<EObject> getContents();

	/**
	 * Sets the contents of this model extent.
	 * 
	 * @param contents
	 *            list of elements to become the contents of this extent
	 * @throws UnsupportedOperationException
	 *             if this model extent does not allow modifications
	 */
	void setContents(List<? extends EObject> contents);
}
