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
* Thrown if QVT script execution is interrupted forcibly.
*/
public class QvtInterruptedExecutionException extends QvtRuntimeException {

	private static final long serialVersionUID = 1457660201707930705L;

	public QvtInterruptedExecutionException() {
		super(); 
	}
	
	public QvtInterruptedExecutionException(String message) {
		super(message); 
	}

	public QvtInterruptedExecutionException(Throwable cause) {
		super(cause);
	}

	public QvtInterruptedExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
