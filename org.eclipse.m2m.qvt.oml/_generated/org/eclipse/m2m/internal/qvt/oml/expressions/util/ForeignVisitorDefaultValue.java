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
package org.eclipse.m2m.internal.qvt.oml.expressions.util;

import org.eclipse.ocl.parser.ValidationVisitor;
import org.eclipse.ocl.util.ToStringVisitor;
import org.eclipse.ocl.utilities.Visitor;


public class ForeignVisitorDefaultValue {

	private ForeignVisitorDefaultValue() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getDefaultValueForVisitor(Visitor<T, ?, ?, ?, ?, ?, ?, ?, ?, ?> visitor) {
		if(visitor != null) {
			Class<?> visitorClass = visitor.getClass();
			if(visitorClass == ValidationVisitor.class) {
				return (T)Boolean.TRUE;
			} else if(visitorClass == ToStringVisitor.class) {
				return (T)""; //$NON-NLS-1$ 
			}
		}
		
		return null;
	}
}
