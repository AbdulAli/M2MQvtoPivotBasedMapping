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

package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import org.eclipse.m2m.internal.qvt.oml.cst.adapters.AbstractGenericAdapter;
import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;

public class ConstructorOperationAdapter extends AbstractGenericAdapter<ConstructorOperationAdapter> {

	public ConstructorOperationAdapter(Constructor referredConstructor) {
		myReferredConstructor = referredConstructor;
	}
	
	public Constructor getReferredConstructor() {
		return myReferredConstructor;
	}
	
	public boolean isAdapterForType(Object type) {	
		return ConstructorOperationAdapter.class == type;
	}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConstructorOperationAdapter;
    }

    @Override
    public int hashCode() {
        return ConstructorOperationAdapter.class.hashCode();
    }
    
    private final Constructor myReferredConstructor;

}
