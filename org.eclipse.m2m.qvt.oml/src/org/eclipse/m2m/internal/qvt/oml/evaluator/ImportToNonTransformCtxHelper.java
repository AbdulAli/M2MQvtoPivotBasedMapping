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
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;

/**
 * This class encapsulates the internal logic used import and initialize instance
 * of a given set of modules into QVT evaluation environment.
 * <p>
 * The motivation for this class is to isolate the non-transformation executors of 
 * Imperative OCL code and calls to QVT libraries from internals.
 * 
 * @author dvorak
 */
public class ImportToNonTransformCtxHelper {

	final private HashMap<Module, ModuleInstance> fInstanceMap;
	final private LinkedHashSet<Module> fImportedModules;
	final private boolean fIncludeStdLib;
	final private ModuleInstanceCachingFactory fModuleFactory;
	
	/**
	 * Constructs import helper for the given set of modules.
	 * 
	 * @param importedModules
	 *            set of modules to be instantiated by this helper
	 * @param includeStdLib
	 *            indicates whether QVT Standard Library should be included.
	 */
	public ImportToNonTransformCtxHelper(LinkedHashSet<Module> importedModules, boolean includeStdLib) {
		if(importedModules == null) {
			throw new IllegalArgumentException("non-null module set required"); //$NON-NLS-1$
		}

		fIncludeStdLib = includeStdLib;
		fImportedModules = new LinkedHashSet<Module>(importedModules);
		fInstanceMap = new HashMap<Module, ModuleInstance>();
		fModuleFactory = new ModuleInstanceCachingFactory();
	}
	
	/**
	 * Constructs the import helper including only QVT Standard Library.   
	 */
	public ImportToNonTransformCtxHelper() {
		this(new LinkedHashSet<Module>(1), true);
	}

	/**
	 * Add another module to the set of modules already imported by this helper.
	 * 
	 * @param module
	 *            add another module to be imported (instantiated) into
	 *            evaluation environment.
	 * @return <code>true</code> if it was added; <code>false</code> if it was
	 *         already imported
	 */
	public boolean addImportedModule(Module module) {
		if(module == null) {
			throw new IllegalArgumentException();
		}
		if(fImportedModules.contains(module)) {
			// re-insertion would cause re-ordering
			return false;
		}

		return fImportedModules.add(module);
	}

	/**
	 * Get set of modules imported by this helper.
	 * 
	 * @return set of modules
	 */
	public Set<Module> getModules() {
		return Collections.unmodifiableSet(fImportedModules);
	}
	
	/**
	 * Gets read-only map of module to its single created instance.
	 * 
	 * @return model to instance map
	 */
	public Map<Module, ModuleInstance> getModuleInstances() {
		return Collections.unmodifiableMap(fInstanceMap);
	}	
	
	ThisInstanceResolver createImportedInstanceResolver() {
		return fModuleFactory.createImportedInstanceResolver();
	}
	
	/**
	 * Get instances of modules that are explicitly added to this helper.
	 * 
	 * @param initialized
	 *            <code>true</code> if only initialized module instances should
	 *            be return; <code>false</code> if only those uninitialized
	 * @return module instances
	 */
	List<ModuleInstance> getModuleInstances(boolean initialized) {
		List<ModuleInstance> result = new ArrayList<ModuleInstance>();
		for (Module nextModule : fImportedModules) {
			ModuleInstance moduleInstance = fInstanceMap.get(nextModule);
			assert moduleInstance != null;
			if(initialized == moduleInstance.getAdapter(ModuleInstance.Internal.class).isInitialized()) {
				result.add(moduleInstance);
			}
		}
		
		return result;
	}
	
	private class ModuleInstanceCachingFactory extends ModuleInstanceFactory {
		
		public ThisInstanceResolver createImportedInstanceResolver() {
			if(fIncludeStdLib) {
				basicCreateModuleInstance(QvtOperationalStdLibrary.INSTANCE.getStdLibModule(), fInstanceMap, null);
			}

			for (Module module : fImportedModules) {
				createModuleInstance(module, fInstanceMap, null);
			}
			
			return new ThisInstanceResolver() {					
				public ModuleInstance getThisInstanceOf(Module module) {			
					return fInstanceMap.get(module);
				}
			};
		}		
	}
}
