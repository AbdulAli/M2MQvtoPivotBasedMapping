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
package org.eclipse.m2m.internal.qvt.oml.stdlib.model;

import org.eclipse.emf.ecore.EObject;

public interface StatusInstance extends EObject {

	/**
	 * Indicates whether this status represents a successfully executed
	 * transformation.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	boolean succeeded();
	
	/**
	 * Indicates error condition during transformation execution.
	 * 
	 * @return <code>true</code> if a transformation execution failed,
	 *         <code>false</code> if it was successful.
	 */
	boolean failed();	
	
	/**
	 * Gets the exception raised by failed transformation execution.
	 * 
	 * @return exception object or <code>null</code> if no exception is
	 *         available.
	 */
	ExceptionInstance raisedException();
}
