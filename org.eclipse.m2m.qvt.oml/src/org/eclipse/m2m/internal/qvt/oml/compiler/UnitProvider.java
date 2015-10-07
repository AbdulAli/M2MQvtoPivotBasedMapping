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
package org.eclipse.m2m.internal.qvt.oml.compiler;


public interface UnitProvider {

	interface UnitVisitor {
	
		int DEPTH_ZERO = 0;
		int DEPTH_ONE = 1;
		int DEPTH_INFINITE = 2;		
		
		boolean visitUnit(UnitProxy unit);
	}

	void accept(UnitProvider.UnitVisitor visitor, String scopeQualifiedName,
			int depth, boolean includeExternal);

}