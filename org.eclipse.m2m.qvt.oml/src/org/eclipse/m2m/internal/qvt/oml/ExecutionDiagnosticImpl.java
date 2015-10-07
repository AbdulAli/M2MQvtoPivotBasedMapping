/*******************************************************************************
 * Copyright (c) 2009 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ExecutionStackTraceElement;

/**
 * A diagnostic class representing the result status of a transformation
 * execution.
 * 
 * @since 3.0
 * 
 * @noextend This class is not intended to be sub-classed by clients.
 */
public final class ExecutionDiagnosticImpl extends BasicDiagnostic implements ExecutionDiagnostic {
	
	public static final ExecutionDiagnosticImpl OK_INSTANCE = new ExecutionDiagnosticImpl(
			Diagnostic.OK, 0, "OK", null); //$NON-NLS-1$

	// instance fields

	private List<ExecutionStackTraceElement> fStackTrace;

	public ExecutionDiagnosticImpl(int severity, int code, String message, Object[] data) {
		super(severity, SOURCE, code, message, data);
	}

	public ExecutionDiagnosticImpl(int severity, int code, String message) {
		super(severity, SOURCE, code, message, null);
	}

	public void setStackTrace(List<? extends ExecutionStackTraceElement> stackElements) {
		fStackTrace = new ArrayList<ExecutionStackTraceElement>(stackElements);
	}

	/**
	 * Gets the stack trace that resulted from interrupted execution either by
	 * user termination request or exception thrown
	 * 
	 * @return list of trace elements or an empty list
	 */
	public List<ExecutionStackTraceElement> getStackTrace() {
		return fStackTrace != null ? Collections.unmodifiableList(fStackTrace)
				: Collections.<ExecutionStackTraceElement> emptyList();
	}

	/**
	 * Prints the execution stack-trace (if available) of this diagnostic to the
	 * specified print writer.
	 * 
	 * @param writer
	 *            <code>PrintWriter</code> to use for output
	 */
	public void printStackTrace(PrintWriter writer) {
		if (fStackTrace != null) {
			QvtRuntimeException.printQvtStackTrace(writer, fStackTrace);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Diagnostic "); //$NON-NLS-1$
		switch (severity) {
		case OK: {
			result.append("OK"); //$NON-NLS-1$
			break;
		}
		case INFO: {
			result.append("INFO"); //$NON-NLS-1$
			break;
		}
		case WARNING: {
			result.append("WARNING"); //$NON-NLS-1$
			break;
		}
		case ERROR: {
			result.append("ERROR"); //$NON-NLS-1$
			break;
		}
		case CANCEL: {
			result.append("CANCEL"); //$NON-NLS-1$
			break;
		}
		default: {
			result.append(Integer.toHexString(severity));
			break;
		}
		}

		result.append(" source="); //$NON-NLS-1$
		result.append(source);

		result.append(" code="); //$NON-NLS-1$
		result.append(code);

		result.append(": "); //$NON-NLS-1$
		result.append(message);

		if (data != null && !data.isEmpty()) {
			result.append(" data="); //$NON-NLS-1$
			result.append(data);
		}
		if (children != null && !children.isEmpty()) {
			result.append(' ');
			result.append(children);
		}

		return result.toString();
	}
}
