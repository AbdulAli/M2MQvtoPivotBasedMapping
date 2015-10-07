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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.blackbox.AbstractCompilationUnitDescriptor;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContext;


public class StandaloneBlackboxProvider extends JavaBlackboxProvider {
	
	private Map<String, JavaUnitDescriptor> fDescriptorMap = new LinkedHashMap<String, JavaUnitDescriptor>();
	
	@Override
	public AbstractCompilationUnitDescriptor getModuleDescriptor(String qualifiedName, ResolutionContext resolutionContext) { 
		try {
			JavaUnitDescriptor d = fDescriptorMap.get(qualifiedName);
			if (d == null) {
				d = new StandaloneDescriptor(qualifiedName);
				fDescriptorMap.put(qualifiedName, d);
			}
			return d;
		} catch(ClassNotFoundException e) {
			return null;
		}
		catch(RuntimeException e) {
			return null;
		}
	}
	
	public void registerDescriptor(final Class<?> cls, String unitQualifiedName, String moduleName, final String[] packageURIs) {
		JavaUnitDescriptor d = fDescriptorMap.get(unitQualifiedName);
		
		if (d == null) {
			d = new JavaUnitDescriptor(unitQualifiedName) {};
			fDescriptorMap.put(unitQualifiedName, d);
		}
		
		try {
			d.addModuleHandle(
				new StandaloneModuleHandle(cls.getName(), moduleName) {
					@Override
					public Class<?> getModuleJavaClass() throws ClassNotFoundException {
						return cls;
					}
					
					@Override
					public List<String> getUsedPackages() {
						return Arrays.asList(packageURIs);
					}
				}
			);
		} catch (ClassNotFoundException e) {
			QvtPlugin.error(e);
		}
	}

	@Override
	public Collection<JavaUnitDescriptor> getModuleDescriptors(ResolutionContext resolutionContext) {
		return fDescriptorMap.values();
	}

	@Override
	public void cleanup() {
		super.cleanup();
		fDescriptorMap = new LinkedHashMap<String, JavaUnitDescriptor>();
	}
	
	private class StandaloneDescriptor extends JavaUnitDescriptor {		
				
		StandaloneDescriptor(String unitQualifiedName) throws ClassNotFoundException {
			this(unitQualifiedName, Collections.singletonList(getSimpleNameFromJavaClass(unitQualifiedName))); 
		}
		
		StandaloneDescriptor(String unitQualifiedName, List<String> moduleNames) throws ClassNotFoundException {
			super(unitQualifiedName); 
			
			for(String moduleName : moduleNames) {
				addModuleHandle(new StandaloneModuleHandle(unitQualifiedName, moduleName));
			}
		}
						
	}

}
