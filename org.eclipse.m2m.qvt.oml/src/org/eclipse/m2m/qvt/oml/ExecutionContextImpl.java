/*******************************************************************************
 * Copyright (c) 2009, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 422269, 431082
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.internal.qvt.oml.library.Context;
import org.eclipse.m2m.qvt.oml.util.EvaluationMonitor;
import org.eclipse.m2m.qvt.oml.util.ISessionData;
import org.eclipse.m2m.qvt.oml.util.Log;

/**
 * Execution context implementation.
 * 
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @see TransformationExecutor
 */
@SuppressWarnings("deprecation")
public final class ExecutionContextImpl implements ExecutionContext {

	private final Map<String, Object> fConfigProperties = new HashMap<String, Object>(5);

	private IProgressMonitor fMonitor;

	private Log fLog;
	
	private final Map<ISessionData.Entry<Object>, Object> fSessionStorage = new HashMap<ISessionData.Entry<Object>, Object>(5);
	private final ISessionData fSessionData;

	/**
	 * Constructs a default context for execution.
	 */
	public ExecutionContextImpl() {
		fLog = Log.NULL_LOG;
		fMonitor = createDefaultMonitor();
		fSessionData = new Context.SessionDataImpl(fSessionStorage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.m2m.qvt.oml.ExecutionContext#getConfigProperty(java.lang.String)
	 */
	public Object getConfigProperty(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return fConfigProperties.get(name);
	}

	/**
	 * Sets the value object for the given configuration property
	 * 
	 * @param name
	 *            the name of the property, never <code>null</code>
	 * @param value
	 *            the value object
	 */
	public void setConfigProperty(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException("null config property name"); //$NON-NLS-1$
		}

		fConfigProperties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ExecutionContext#getConfigPropertyNames()
	 */
	public Set<String> getConfigPropertyNames() {
		return Collections.unmodifiableSet(fConfigProperties.keySet());
	}

	/**
	 * Sets the log implementation to this context.
	 * 
	 * @param log
	 *            the log implementation, never <code>null</code>
	 */
	public void setLog(Log log) {
		if (log == null) {
			throw new IllegalArgumentException("Non-null logger required"); //$NON-NLS-1$
		}

		this.fLog = log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ExecutionContext#getLog()
	 */
	public Log getLog() {
		return fLog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ExecutionContext#getMonitor()
	 */
	public EvaluationMonitor getMonitor() {
		return EvaluationMonitor.EvaluationMonitorWrapper.convert(fMonitor);
	}

	/**
	 * @since 3.4
	 */
	public IProgressMonitor getProgressMonitor() {
		return fMonitor;
	}

	/**
	 * Set evaluation monitor to this context
	 * 
	 * @param monitor
	 *            the monitor implementation, never <code>null</code>
	 * @deprecated
	 */
	public void setMonitor(EvaluationMonitor monitor) {
		if (monitor == null) {
			throw new IllegalArgumentException("null monitor"); //$NON-NLS-1$
		}

		setProgressMonitor(EvaluationMonitor.EvaluationMonitorWrapper.convert(monitor));
	}

	/**
	 * Set evaluation monitor to this context
	 * 
	 * @param monitor
	 *            the monitor implementation, never <code>null</code>
	 * @since 3.4
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		if (monitor == null) {
			throw new IllegalArgumentException("null monitor"); //$NON-NLS-1$
		}

		fMonitor = monitor;
	}
	
	/**
	 * @since 3.4
	 */
	public ISessionData getSessionData() {
		return fSessionData;
	}
	
	/**
	 * @since 3.4
	 */
	public Collection<ISessionData.Entry<Object>> getSessionDataEntries() {
		return fSessionStorage.keySet();
	}
	
	private static IProgressMonitor createDefaultMonitor() {
		return new NullProgressMonitor();
	}
}