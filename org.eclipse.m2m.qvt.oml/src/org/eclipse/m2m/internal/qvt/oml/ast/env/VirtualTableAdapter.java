/*******************************************************************************
 * Copyright (c) 2007, 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EOperation;

/**
 * Virtual table adapter grouping related virtual operations to the adapted target operation.
 */
public class VirtualTableAdapter extends AdapterImpl {
	
	private VirtualTable fVTable;
	
	private VirtualTableAdapter() {		
	}
		
	/**
	 * Gets existing virtual table adapter for the given operation.
	 * 
	 * @param operation
	 *            the target operation
	 * @return the virtual table or <code>null</code> in case it does not
	 *         exist
	 */
	static VirtualTableAdapter getAdapter(EOperation operation) {
		return getAdapter(operation, false);
	}
	
	/**
	 * Gets virtual table adapter for the given operation.
	 * 
	 * @param operation
	 *            the target operation
	 * @param createOnDemand
	 *            indicates whether the virtual table is to be created in case
	 *            it does not exist
	 * @return the virtual table, never <code>null</code>
	 */
	public static VirtualTableAdapter getAdapter(EOperation operation, boolean createOnDemand) {
		for (Adapter adapter : operation.eAdapters()) {
			if(adapter instanceof VirtualTableAdapter) {
				return (VirtualTableAdapter) adapter;
			}
		}
		
		if(createOnDemand == false) {
			return null;
		}
		VirtualTableAdapter adapter = new VirtualTableAdapter();
		operation.eAdapters().add(adapter);
		return adapter;
	}	
	
	public VirtualTable getVirtualTable() {
		if (fVTable == null) {
			fVTable = new VirtualTable() {
				@Override
				protected EOperation getOwningOperation() {
					return (EOperation) getTarget();
				}
			};
		}

		return fVTable;
	}
}
