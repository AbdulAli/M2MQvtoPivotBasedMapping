/*******************************************************************************
 * Copyright (c) 2014 Borland Software Corporation and others.
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

import org.eclipse.emf.common.util.URI;

public class BlackboxUnitResolverFactory extends UnitResolverFactory {

	@Override
	public boolean accepts(URI uri) {
		return BlackboxUnitResolver.isBlackboxUnitURI(uri) && (uri.segmentCount() > 0);
	}

	@Override
	public UnitResolver getResolver(URI uri) {
		return BlackboxUnitResolver.DEFAULT;
	}

	@Override
	public String getQualifiedName(URI uri) {
		// TODO - use 1st segment as provider ID to be used in library ID clash resolution
		return uri.lastSegment(); 
	}

}
