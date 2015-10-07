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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;

class ModuleInstanceImpl extends DynamicEObjectImpl implements ModuleInstance, ModuleInstance.Internal {

	
	private Map<Module, ModuleInstance> fInstanceMap;
    private Map<Module, OperationOverrideMap> fOverrideMap;	
	private List<Object> fAdapters = Collections.emptyList();
	private boolean fIsInitialized = false;	
	
	ModuleInstanceImpl(Module moduleType) {
		if(moduleType == null) {
			throw new IllegalArgumentException("Null module class"); //$NON-NLS-1$
		}
		
		eSetClass(moduleType);
	}

	public List<ModuleInstance> getImportedModules() {
		Module thisModule = getModule();
		EList<ModuleImport> moduleImports = thisModule.getModuleImport();
		List<ModuleInstance> result = null;
		
		for (ModuleImport nextImport : moduleImports) {
			Module importedModule = nextImport.getImportedModule();
			if(nextImport.getKind() == ImportKind.EXTENSION || importedModule instanceof Library) {
				// create only instances of extended modules and implicit accessed library singletons 
				if(result == null) {
					result = new ArrayList<ModuleInstance>(moduleImports.size());
				}

				result.add(getThisInstanceOf(importedModule));
			}
		}
		
		return (result != null) ? Collections.<ModuleInstance>unmodifiableList(result) : 
			Collections.<ModuleInstance>emptyList();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ThisInstanceResolver#getThisInstanceOf(org.eclipse.m2m.internal.qvt.oml.expressions.Module)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance#getThisInstanceOf(org.eclipse.m2m.internal.qvt.oml.expressions.Module)
	 */
	public ModuleInstance getThisInstanceOf(Module module) {
		if(module == eClass()) {
			return this;
		}

		return (fInstanceMap != null) ? fInstanceMap.get(module) : null;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance#getModule()
	 */
	public Module getModule() {
		return (Module)eClass();
	}
	
	void setInstanceMap(Map<Module, ModuleInstance> instanceMap) {
		if(instanceMap == null) {
			throw new IllegalArgumentException();
		}
		fInstanceMap = instanceMap;
	}
	
	void setOverrideMap(Map<Module, OperationOverrideMap> overrideMap) {
		if(overrideMap == null) {
			throw new IllegalArgumentException();
		}
		fOverrideMap = overrideMap;
	}
	
	public ImperativeOperation getOverridingOperation(ImperativeOperation overridden) {
		if(fOverrideMap != null) {
			Module callerModule = getModule();
			OperationOverrideMap perModuleMap = fOverrideMap.get(callerModule);
			if(perModuleMap != null) {
				return perModuleMap.getOverridingOperation(overridden); 
			}
		}
		
		return null;
	}
	
	public <T> T getAdapter(Class<T> adapterType) {
		if(adapterType == ModuleInstance.Internal.class ||
			adapterType == ModuleInstance.class) {			
			return adapterType.cast(this);
		}
		
		for (Object nextAdapter : fAdapters) {
			Class<?> nextAdapterType = nextAdapter.getClass();
			if(adapterType == nextAdapterType || adapterType.isAssignableFrom(nextAdapterType)) {
				return adapterType.cast(nextAdapter);
			}
		}
		
		return null;
	}
	
	public void addAdapter(Object adapter) {
		if(adapter == null) {
			throw new IllegalArgumentException();
		}
		
		if(fAdapters.isEmpty()) {
			fAdapters = new LinkedList<Object>();
		}
		
		fAdapters.add(adapter);
	}
	
	public boolean isInitialized() {	
		return fIsInitialized;
	}
	
	public void setInitialized() {
		if(fIsInitialized == true) {
			throw new IllegalStateException("module instance already initialized"); //$NON-NLS-1$
		}
		
		fIsInitialized = true;
	}

	public void dispose() {
		// do nothing by default
	};
	
	@Override
	public String toString() {
		return eClass().getName() + " @" + Integer.toHexString(System.identityHashCode(this)); //$NON-NLS-1$
	}
}
