/*******************************************************************************
 * Copyright (c) 2009, 2014 Borland Software Corporation and others.
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

import java.util.HashSet;
import java.util.Set;

public abstract class DelegatingUnitResolver implements UnitResolver, RecursiveUnitResolver {
	
	private UnitResolver fParent;
	
		
	protected DelegatingUnitResolver() {
		super();		
	}

	protected abstract UnitProxy doResolveUnit(String qualifiedName);
	
	protected UnitResolver getParent() {
		return fParent;
	}
	
	protected void setParent(UnitResolver resolver) {
		fParent = resolver;
	}
		
	public final UnitProxy resolveUnit(String qualifiedName) {	
		return resolveUnit(qualifiedName, new HashSet<RecursiveUnitResolver>());
	}

	public final UnitProxy resolveUnit(String qualifiedName, Set<RecursiveUnitResolver> accessedParents) {
		if (accessedParents.contains(this)) {
			return null;
		}
		
		UnitProxy unit = doResolveUnit(qualifiedName);
		if(unit == null) {
			UnitResolver parent = getParent();
			if(parent instanceof RecursiveUnitResolver) {
				accessedParents.add(this);
				return ((RecursiveUnitResolver) parent).resolveUnit(qualifiedName, accessedParents);
			}
			else if(parent != null) {
				return parent.resolveUnit(qualifiedName);
			}
		}
		
		return unit;
	}
	
}
