/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;

class OperationOverrideMap {

	private Map<ImperativeOperation, ImperativeOperation> fMapping;
	
	OperationOverrideMap() {
		super();
	}
					
	public ImperativeOperation getOverridingOperation(ImperativeOperation overridden) {
		if(overridden == null) {
			throw new IllegalArgumentException();
		}
		
		return fMapping == null ? null : fMapping.get(overridden);
	}
		
	static Map<Module, OperationOverrideMap> create(Module mainModule) {
		HashMap<Module, OperationOverrideMap> module2OverrideMap = new HashMap<Module, OperationOverrideMap>(3);
		buildModuleOverrideMap(mainModule, module2OverrideMap);
		for (ModuleImport moduleImport : mainModule.getModuleImport()) {
			if(moduleImport.getKind() == ImportKind.EXTENSION) {
				Module importedModule = moduleImport.getImportedModule();				
				inherit(mainModule, importedModule, module2OverrideMap);
			}
		}
		
		module2OverrideMap.get(mainModule);
		return module2OverrideMap;
	}
	
	private static void buildModuleOverrideMap(Module module, Map<Module, OperationOverrideMap> modules2OverrideMap) {
		for (EOperation operation : module.getEOperations()) {
			if(operation instanceof ImperativeOperation) {
				ImperativeOperation overriding = (ImperativeOperation) operation;
				ImperativeOperation overridden = overriding.getOverridden();
				if(overridden != null) {
					pushUpOperation(overriding, overridden, modules2OverrideMap);
				}
			}
		}
		
		for (ModuleImport moduleImport : module.getModuleImport()) {
			if(moduleImport.getKind() == ImportKind.EXTENSION) {
				Module importedModule = moduleImport.getImportedModule();
				buildModuleOverrideMap(importedModule, modules2OverrideMap);				
			}
		}
	}
	
	private static void inherit(Module extending, Module extended, Map<Module, OperationOverrideMap> modules2OverrideMap) {
		for (ModuleImport moduleImport : extended.getModuleImport()) {
			if(moduleImport.getKind() == ImportKind.EXTENSION) {
				inherit(extended, moduleImport.getImportedModule(), modules2OverrideMap);
			}
		}
		
		OperationOverrideMap extendedMap = modules2OverrideMap.get(extended);
		if(extendedMap != null) {
			OperationOverrideMap extendingMap = modules2OverrideMap.get(extending);
			if(extendingMap == null) {
				modules2OverrideMap.put(extending, extendingMap = new OperationOverrideMap());
			}
			extendingMap.merge(extendedMap);			
		}
	}
	
	private static void pushUpOperation(ImperativeOperation overriding, ImperativeOperation overriden, Map<Module, OperationOverrideMap> tableMap) {
		Module extendedModule = (Module)overriden.getEContainingClass();
		OperationOverrideMap nextTable = tableMap.get(extendedModule);
		if(nextTable == null) {
			nextTable = new OperationOverrideMap();
			tableMap.put(extendedModule, nextTable);
		}

		ImperativeOperation existingOverride = nextTable.getOverridingOperation(overriden);
		if(existingOverride != null) {
			return;
		}
		
		nextTable.add(overriden, overriding);
		
		if(overriden.getOverridden() != null) {
			pushUpOperation(overriding, overriden.getOverridden(), tableMap);
		}
	}
	
	private void merge(OperationOverrideMap mergedMap) {
		if(fMapping == null) {
			fMapping = new HashMap<ImperativeOperation, ImperativeOperation>();
		}
		if(mergedMap != null) {
			fMapping.putAll(mergedMap.fMapping);
		}
	}
	
	private void add(ImperativeOperation overriden, ImperativeOperation overriding) {
		if(fMapping == null) {
			fMapping = new HashMap<ImperativeOperation, ImperativeOperation>();
		}
		fMapping.put(overriden, overriding);
	}		
}