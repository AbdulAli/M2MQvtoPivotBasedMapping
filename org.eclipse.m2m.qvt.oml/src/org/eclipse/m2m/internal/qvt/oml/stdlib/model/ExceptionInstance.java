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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTStackTraceElement;

public interface ExceptionInstance extends EObject {

	/**
	 * Gets the optional argument to the exception throwing
	 * 
	 * @return the argument message or <code>null</code>
	 */
	String getArgument();
	
	/**
	 * Get stack trace elements if any are available
	 * 
	 * @return the list of elements or empty list, in case no stack trace is
	 *         available
	 */
	List<QVTStackTraceElement> getStackElements();
}
