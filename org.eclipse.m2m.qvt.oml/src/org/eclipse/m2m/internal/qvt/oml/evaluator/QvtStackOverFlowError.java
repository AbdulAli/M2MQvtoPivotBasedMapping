/*******************************************************************************
 * Copyright (c) 2007, 2008 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

/**
* QVT analogy of <code>java.lang.StackOverflowError</code>.
*/
public class QvtStackOverFlowError extends QvtRuntimeException {

	private static final long serialVersionUID = -7765202587379744295L;

	public QvtStackOverFlowError(String message) {
		super(message); 
	}

	public QvtStackOverFlowError(Throwable cause) {
		super(cause);
	}

	public QvtStackOverFlowError(String message, Throwable cause) {
		super(message, cause);
	}
}
