/*******************************************************************************
 * Copyright (c) 2014, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Boyko Sergey - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxRegistry;
import org.eclipse.m2m.internal.qvt.oml.blackbox.java.JavaBlackboxProvider;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.m2m.qvt.oml.blackbox.java.Module;

public class TransformationExecutorBlackboxRegistry implements TransformationExecutor.BlackboxRegistry {

	public Diagnostic registerModules(Class<?>... classes) {
		for(Class<?> cls : classes) {
			registerModule(cls);
		}
		return Diagnostic.OK_INSTANCE;
	}
	
	public Diagnostic registerModule(Class<?> cls) {
		return registerModule(cls, null, null);
	}
		
	public Diagnostic registerModule(Class<?> cls, String unitQualifiedName, String moduleName) {
		
		return registerModule(cls, unitQualifiedName, moduleName, null);
	}
	
	public Diagnostic registerModule(Class<?> cls, String unitQualifiedName, String moduleName, String[] packageURIs) {
		
		if (moduleName == null) {
			moduleName = cls.getSimpleName();
		}
		
		if (unitQualifiedName == null) {
			unitQualifiedName = cls.getPackage().getName() + JavaBlackboxProvider.CLASS_NAME_SEPARATOR + moduleName;
		}
		
		if (packageURIs == null) {
			Module annotation = cls.getAnnotation(Module.class);
			packageURIs = (annotation == null) ? new String[] {} : annotation.packageURIs();
		}
		
		BlackboxRegistry.INSTANCE.addStandaloneModule(cls, unitQualifiedName, moduleName, packageURIs);
		return Diagnostic.OK_INSTANCE;
	}

}
