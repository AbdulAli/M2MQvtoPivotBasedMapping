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
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * This interface provides read-only access to the contents of a model extent.
 */
public interface ModelExtentContents {
	/**
	 * Gets the elements which were initially contained at the creation time of this extent.
	 */
	List<EObject> getInitialElements();
	/**
	 * Gets all the root objects of the current extent contents.
	 * Note: The result may include the original objects if not removed or moved to container.
	 */
	List<EObject> getAllRootElements();
}