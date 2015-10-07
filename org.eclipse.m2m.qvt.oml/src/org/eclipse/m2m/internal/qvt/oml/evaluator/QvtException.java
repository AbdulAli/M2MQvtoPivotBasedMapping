/*******************************************************************************
 * Copyright (c) 2008, 2013 Borland Software Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Alex Paperno - bug 419299 
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import org.eclipse.emf.ecore.EClass;

public class QvtException extends QvtRuntimeException {
	private static final long serialVersionUID = -4611540417238844821L;

	public QvtException(String message, EClass exceptionType) {
		super(message);
		this.exceptionType = exceptionType;
	}

	/**
	 * @return the exceptionType
	 */
	public EClass getExceptionType() {
		return exceptionType;
	}

	private final EClass exceptionType;
}
