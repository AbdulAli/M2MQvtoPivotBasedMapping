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
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;


class DiagnosticUtil {

	static final String DIAGNOSTIC_SOURCE = "org.eclipse.m2m.qvt.oml.blackbox.java"; //$NON-NLS-1$	
	
	// no instances
	private DiagnosticUtil() {
		super();
	}
	
	public static boolean isSuccess(Diagnostic diagnostic) {
		int severity = diagnostic.getSeverity();
		return severity == Diagnostic.OK || severity == Diagnostic.WARNING || severity == Diagnostic.INFO;
	}
		
	public static BasicDiagnostic createRootDiagnostic(String message) {
		return new BasicDiagnostic(DIAGNOSTIC_SOURCE, 0, message, null);
	}
	
	public static BasicDiagnostic createErrorDiagnostic(String message, Exception exception) {
		return new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, message, new Object[] { exception });
	}
	
	public static BasicDiagnostic createErrorDiagnostic(String message) {
		return new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, message, null);
	}	
}
