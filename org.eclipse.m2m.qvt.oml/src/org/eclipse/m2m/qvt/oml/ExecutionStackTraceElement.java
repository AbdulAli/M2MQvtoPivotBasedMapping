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

/**
 * An element of the QVT execution stack trace. The frame at the top of the
 * stack represents the execution point at which the stack trace was generated.
 * Typically, this is the point at which a forcible execution interruption
 * occurred.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface ExecutionStackTraceElement {
	/**
	 * Gets the name of the the unit containing the execution point.
	 * 
	 * @return the name string
	 */
	String getUnitName();

	/**
	 * Gets the name of the executed module.
	 * 
	 * @return the name string or <code>null</code>, in case the executed code
	 *         is not part of any module, for instance a condition of a top
	 *         level <code>modeltype</code> element.
	 */
	String getModuleName();

	/**
	 * Gets the name of the operation associated with this trace element.
	 * <p>
	 * <b>Remark:</b> </br> It's possible that the name does not refer to an
	 * operation explicitly defined in QVT, but rather to a synthesized one.
	 * 
	 * @return the name string
	 */
	String getOperationName();

	/**
	 * Returns the line number of the source line containing the execution point
	 * represented by this stack trace element.
	 * 
	 * @return the line number of the source line containing the execution point
	 *         represented by this stack trace element, or a negative number if
	 *         this information is unavailable.
	 */
	int getLineNumber();
}