/*******************************************************************************
 * Copyright (c) 2008, 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;

public class BlackboxException extends Exception {

	private static final long serialVersionUID = -4825358108795928298L;

	private Diagnostic fDiagnostic;
	
	public BlackboxException(String message) {
		createDiagnostic(message, null);
	}
	
	public BlackboxException(Diagnostic diagnostic) {
		super(diagnostic.getMessage());
		fDiagnostic = diagnostic;		
	}

	public BlackboxException(String message, Throwable throwable) {
		super(message, throwable);
		fDiagnostic = createDiagnostic(message, throwable);
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		return (message != null) ? message : fDiagnostic.getMessage();
	}
	
	/** 
	 * @return the diagnostic object
	 */
	public Diagnostic getDiagnostic() {
		return fDiagnostic;
	}
	
	static Diagnostic createDiagnostic(String message, Throwable throwable) {
		Object[] data = (throwable != null) ?  new Object[] { throwable } : null;
		return new BasicDiagnostic(Diagnostic.ERROR,
				"org.eclipse.m2m.qvt.oml.blackbox", 0, message, //$NON-NLS-1$
				data);
	}
}
