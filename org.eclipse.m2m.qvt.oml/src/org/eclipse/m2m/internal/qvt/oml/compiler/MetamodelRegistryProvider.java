/*******************************************************************************
 * Copyright (c) 2008, 2014 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.MetamodelRegistry;


/**
 * @author aigdalov
 * Created on Oct 10, 2007
 */
public class MetamodelRegistryProvider implements IMetamodelRegistryProvider {
	
	public static IRepositoryContext createContext(final URI uri) {
		if (uri == null) {
			throw new IllegalArgumentException();
		}

		return new IRepositoryContext() {

			public URI getURI() {
				return uri;
			}
		};
	}

    public MetamodelRegistry getRegistry(IRepositoryContext context) {
        return MetamodelRegistry.getInstance();
    }
}