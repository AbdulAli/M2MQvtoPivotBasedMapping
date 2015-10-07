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

import java.io.IOException;

import org.eclipse.emf.common.util.URI;


public abstract class UnitProxy {
	
	public static final int TYPE_MODEL = 1;

	public static final int TYPE_CST_STREAM = 0;
		
	public static final char NAMESPACE_SEP = '.';  
	
	// instance fields
	
	private String fName;
	
	private String fNamespace;
	
	private String fQName;	
	
	private URI fUri;
	
	protected UnitProxy(String namespace, String unitName, URI uri) {
		if(unitName == null || uri == null || !uri.isHierarchical() || uri.isRelative()) {
			throw new IllegalArgumentException("Invalid unit URI: " + uri); //$NON-NLS-1$
		}
		
		fNamespace = namespace;
		fName = unitName;
		if(fNamespace == null) {
			fQName = fName;
		} else {
			fQName = fNamespace + NAMESPACE_SEP + fName;
		}
		
		fUri = uri;
	}	
	
	public abstract int getContentType();
	
	public abstract UnitContents getContents() throws IOException;
	
	public abstract UnitResolver getResolver();
	

	public String getName() {
		return fName;
	}
	
	public String getNamespace() {
		return fNamespace;
	}
	
	public String getQualifiedName() {
		return fQName;
	}
		
	// !!!absolute & hierarchical URI
	public URI getURI() {
		return fUri;
	}
		
	@Override
	public String toString() {
		return fUri.toString();
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof UnitProxy) {
			UnitProxy another = (UnitProxy) obj;
			return fUri.equals(another.fUri);
		}
		return super.equals(obj);
	}
	
	@Override
	public final int hashCode() {	
		return fUri.hashCode();
	}
}
