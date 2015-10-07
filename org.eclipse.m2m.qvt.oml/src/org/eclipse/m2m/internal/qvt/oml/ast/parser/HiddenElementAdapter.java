/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.cst.adapters.AbstractGenericAdapter;

public class HiddenElementAdapter extends AbstractGenericAdapter<HiddenElementAdapter> {
	
	private HiddenElementAdapter() {
	}
	
	public boolean isAdapterForType(Object type) {	
		return HiddenElementAdapter.class == type;
	}
	
	public static void markAsHidden(EObject element) {
		if(!isMarkedAsHidden(element)) {
			element.eAdapters().add(new HiddenElementAdapter());
		}
	}
	
	public static boolean isMarkedAsHidden(EObject element) {
		return EcoreUtil.getExistingAdapter(element, HiddenElementAdapter.class) != null;
	}
}
