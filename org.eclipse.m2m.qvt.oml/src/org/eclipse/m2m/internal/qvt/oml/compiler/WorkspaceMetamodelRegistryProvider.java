/*******************************************************************************
 * Copyright (c) 2007, 2014 Borland Software Corporation and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelProvider;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.MetamodelRegistry;
import org.eclipse.m2m.internal.qvt.oml.emf.util.urimap.MetamodelURIMappingHelper;

public class WorkspaceMetamodelRegistryProvider implements IMetamodelRegistryProvider {

	private ResourceSet resolutionRSet;
	private	Map<String, MetamodelRegistry> perProjectRegs;
	
	public WorkspaceMetamodelRegistryProvider() {
		this(new ResourceSetImpl());
	}
	
	public WorkspaceMetamodelRegistryProvider(ResourceSet resourceSet) {
		if(resourceSet == null) {
			throw new IllegalArgumentException();
		}
		
		this.resolutionRSet = resourceSet;
	}
		
	public ResourceSet getResolutionResourceSet() {
		return resolutionRSet;
	}
	
	public MetamodelRegistry getRegistry(IRepositoryContext context) {
		if(context == null) {
			throw new IllegalArgumentException("Null context"); //$NON-NLS-1$
		}
		
		URI uri = context.getURI();
		if(!uri.isPlatformResource()) {
			return getDelegateRegistry();
		}
		
		IPath wsLocation = new Path(uri.toPlatformString(true));		
		IResource wsResource = ResourcesPlugin.getWorkspace().getRoot().findMember(wsLocation);		
		if(wsResource == null) {
			// not a file, could be a folder
			wsResource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(wsLocation);		
		}
		
		if(wsResource != null) {
			IProject project = wsResource.getProject();
			if(MetamodelURIMappingHelper.hasMappingResource(project)) {
				if(perProjectRegs == null) {
					perProjectRegs = new HashMap<String, MetamodelRegistry>();
				}
				
				String projectKey = project.getFullPath().toString();
				MetamodelRegistry reg = perProjectRegs.get(projectKey);
				if (reg == null) {
					IMetamodelProvider provider = MetamodelURIMappingHelper.createMetamodelProvider(project,
							createDelegateMetamodelProvider(), resolutionRSet);
					reg = new MetamodelRegistry(provider);
					perProjectRegs.put(projectKey, reg);
				}
				return reg;
			}			
		}
		
		return getDelegateRegistry();
	}
	
	private MetamodelRegistry getDelegateRegistry() {
		return new MetamodelRegistry(createDelegateMetamodelProvider()); 
	}
	
	protected IMetamodelProvider createDelegateMetamodelProvider() {
		return MetamodelRegistry.getDefaultMetamodelProvider();
	}
	
}
