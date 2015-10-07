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

import org.eclipse.emf.ecore.EPackage.Registry;

public class LoadContext {

	private Registry fMetamodelRegistry;
	
	public LoadContext(Registry metamodelRegistry) {
		fMetamodelRegistry = metamodelRegistry;
	}
	
	public Registry getMetamodelRegistry() {
		return fMetamodelRegistry;
	}
}
