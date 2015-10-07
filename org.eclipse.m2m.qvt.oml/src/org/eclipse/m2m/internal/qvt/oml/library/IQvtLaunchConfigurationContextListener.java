/*******************************************************************************
 * Copyright (c) 2007, 2010 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.library;

import org.eclipse.m2m.qvt.oml.util.IContext;

/**
 * @author Aleksandr Igdalov
 */
public interface IQvtLaunchConfigurationContextListener {
	public void handleConfigurationStarted(IContext context);
	public void handleConfigurationStopped(IContext context);
}