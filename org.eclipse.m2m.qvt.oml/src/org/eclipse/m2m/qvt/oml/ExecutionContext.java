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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.qvt.oml.util.EvaluationMonitor;
import org.eclipse.m2m.qvt.oml.util.ISessionData;
import org.eclipse.m2m.qvt.oml.util.Log;

/**
 * The execution context interface groups the details needed by the execution
 * engine to execute a transformation.
 * 
 * @since 2.0
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings("deprecation")
public interface ExecutionContext {

	/**
	 * Gets read-only view of the names of the configuration properties that are
	 * set in this context
	 * 
	 * @return set of the name strings in undefined order
	 */
	Set<String> getConfigPropertyNames();

	/**
	 * Get the value of the specified configuration property which was set to
	 * this context.
	 * 
	 * @param name
	 *            the name of the configuration property
	 * @return the value assigned to the queried property. <b>Remark:</b></br>
	 *         It's possible that <code>null</code> is returned in the following
	 *         cases: <li>no property of the given name has been set</li> <li>
	 *         <code>null</code> value has been explicitly set</li> </br> The
	 *         client can distinguish the cases above by using
	 *         {@link #getConfigPropertyNames}
	 * 
	 */
	Object getConfigProperty(String name);

	/**
	 * Gets the logger implementation assigned to this context.
	 * 
	 * @return the logger instance, never <code>null</code>
	 */
	Log getLog();

	/**
	 * Gets the evaluation monitor assigned to this context. The execution
	 * engine will query the monitor the check for the user interruption
	 * request.
	 * 
	 * @return the monitor implementation, never <code>null</code>
	 * @deprecated Use getProgressMonitor() method
	 */
	EvaluationMonitor getMonitor();
	
	/**
	 * Gets the evaluation monitor assigned to this context. The execution
	 * engine will query the monitor the check for the user interruption
	 * request.
	 * 
	 * @return the monitor implementation, never <code>null</code>
	 * @since 3.4
	 */
	IProgressMonitor getProgressMonitor();
	
    /**
     * Returns execution specific settings like QVTEvaluationOptions.EVALUATION_MAX_STACK_DEPTH
     * 
	 * @since 3.4
	 */
    ISessionData getSessionData();
    
    /**
     * Returns all entries stored in the session data.
     * 
	 * @since 3.4
	 */
    Collection<ISessionData.Entry<Object>> getSessionDataEntries();

}
