/*******************************************************************************
 * Copyright (c) 2007, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 431082
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * @since 2.0
 * @deprecated Use IProgressMonitor instance instead
 */
public interface EvaluationMonitor {
	/**
	 * The caller signals the request for cancel execution. 
	 */
	void cancel();
	
	/**
	 * Indicates whether QVT evaluation associated with this monitor has been 
	 * canceled by calling {@link #cancel()}.
	 */
	boolean isCanceled();
	

	/**
	 * The purpose of this class is to support wrapping of the IProgressMonitor interface to
	 * deprecated <code>EvaluationMonitor</code> interface for API compatibility reason. 
	 * 
	 * @since 3.4
	 */
	class EvaluationMonitorWrapper extends ProgressMonitorWrapper implements EvaluationMonitor {

		private EvaluationMonitorWrapper(IProgressMonitor monitor) {
			super(monitor);
		}

		public void cancel() {
			setCanceled(true);
		}

		public static EvaluationMonitor convert(IProgressMonitor monitor) {
			return new EvaluationMonitorWrapper(monitor);
		}

		public static IProgressMonitor convert(final EvaluationMonitor monitor) {
			return new NullProgressMonitor() {
				@Override
				public boolean isCanceled() {
					return monitor.isCanceled();
				}
			};
		}
	}	
	
}
