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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;

public interface ModuleInstance extends EObject, ThisInstanceResolver {

	Module getModule();

	/**
	 * Obtains an adapter for the specified interface type.
	 * 
	 * @param <T> the requested adapter interface
	 * 
	 * @param adapterType the requested adapter interface
	 * @return an instance of the requested interface, or <code>null</code>
	 *     if this module instance does not adapt to it
	 */
	<T> T getAdapter(Class<T> adapterType);
 
 	/**
	* Get instances of imported modules.
	* @return module instances if the module of this instance defines any imports
	*/
	List<ModuleInstance> getImportedModules();
	
	interface Internal {
		
		void addAdapter(Object adapter);
		
		void dispose();
		
		boolean isInitialized();
		/**
		 * @throws IllegalStateException in case this module instance has already been initialized
		 */
		void setInitialized();
		
		ImperativeOperation getOverridingOperation(ImperativeOperation overridden);		
	}
}