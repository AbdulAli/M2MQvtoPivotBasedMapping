/*******************************************************************************
 * Copyright (c) 2009, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;

public class ClassPathUnitResolver implements UnitResolver {
	
	public static ClassPathUnitResolver INSTANCE = new ClassPathUnitResolver();
	
	private ClassPathUnitResolver() {}

	public UnitProxy resolveUnit(String qualifiedName) {
				
		String resourcePath = ResolverUtils.toNamespaceRelativeUnitFilePath(qualifiedName);
		
		URL resourceUrl = ClassLoader.getSystemResource(resourcePath);
		
		if (resourceUrl != null) {
			try {
				int numberOfNameSegments = ResolverUtils.getNameSegments(qualifiedName).length;
				String resourceUri = URIUtil.toURI(resourceUrl).toString();
				URI baseUri = URI.createURI(resourceUri).trimSegments(numberOfNameSegments);
				
				return new URIUnitResolver(baseUri).resolveUnit(qualifiedName);		
			} catch(URISyntaxException e) {			
				QvtPlugin.INSTANCE.log(e);
			}
		}
		
		return null;
	}

}
