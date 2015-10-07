/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 326871
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.m2m.qvt.oml.blackbox.java.Module;

public class StandaloneModuleHandle extends ModuleHandle {
		
	StandaloneModuleHandle(String className, String moduleName) throws ClassNotFoundException {
		super(className, moduleName);
		
		getModuleJavaClass();
	}
	
	public List<String> getUsedPackages() {
		try {
			Module annotation = getModuleJavaClass().getAnnotation(Module.class);
			return annotation == null ? Collections.<String>emptyList() : Arrays.asList(annotation.packageURIs());
		}
		catch(ClassNotFoundException e) {
			return Collections.emptyList();
		}
	}

}
