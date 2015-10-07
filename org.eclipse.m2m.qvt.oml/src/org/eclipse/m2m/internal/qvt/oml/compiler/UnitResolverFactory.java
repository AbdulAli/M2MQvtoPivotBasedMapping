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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;

public abstract class UnitResolverFactory {

	protected abstract boolean accepts(URI uri);
	
	protected abstract UnitResolver getResolver(URI uri);
	
	protected abstract String getQualifiedName(URI uri);

	
	private UnitProxy findUnit(URI uri) {
		UnitResolver resolver = getResolver(uri);
		return resolver != null ? resolver.resolveUnit(getQualifiedName(uri)) : null;
	}
	
	
	public interface Registry {
		
		UnitProxy getUnit(URI uri);
		
		List<UnitProxy> findAllUnits(URI uri);
		
		
		Registry INSTANCE = EMFPlugin.IS_ECLIPSE_RUNNING ? new EclipseRegistry() : new BasicRegistry();
	
	
		class EclipseRegistry extends BasicRegistry {
	
			private static final String POINT_ID = QvtPlugin.ID + ".unitResolverFactory"; //$NON-NLS-1$
			private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
			
			public EclipseRegistry() {
				super(readFactories());		
			}
		
			private static List<UnitResolverFactory> readFactories() {
				ArrayList<UnitResolverFactory> factoryEntries = new ArrayList<UnitResolverFactory>();
				if(EMFPlugin.IS_ECLIPSE_RUNNING) {
					IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
					IExtensionPoint extensionPoint = pluginRegistry.getExtensionPoint(POINT_ID);
					if(extensionPoint != null) {
						IExtension[] allExtensions = extensionPoint.getExtensions();
						for (IExtension nextExtension : allExtensions) {
							IConfigurationElement[] elements = nextExtension.getConfigurationElements();
							Object factoryObj = null;
							try {
								factoryObj = elements[0].createExecutableExtension(CLASS_ATTR);
								if(factoryObj instanceof UnitResolverFactory) {
									factoryEntries.add((UnitResolverFactory)factoryObj);
								}
							} catch (CoreException e) {								
								QvtPlugin.getDefault().log(e.getStatus());
							}
						}						
					}
				}
				return factoryEntries;
		    }
		}
		
		
		class BasicRegistry implements Registry {
			
			private final List<UnitResolverFactory> fFactories;
			
			BasicRegistry() {
				this(Collections.<UnitResolverFactory>emptyList());
			}
			
			BasicRegistry(List<UnitResolverFactory> factories) {
				assert factories != null;
				
				factories = new ArrayList<UnitResolverFactory>(factories);
				factories.add(new URIUnitResolverFactory());
									
				this.fFactories = factories;
			}
			
			UnitResolverFactory getFactory(URI uri) {
				for (UnitResolverFactory nextFactory : fFactories) {
					if(nextFactory.accepts(uri)) {
						return nextFactory;
					}
				}
				return null;
			}
			
			public UnitProxy getUnit(URI uri) {
				UnitResolverFactory factory = getFactory(uri);
				if(factory != null) {
					return factory.findUnit(uri);
				}
				return null;
			}
			
			public List<UnitProxy> findAllUnits(URI uri) {
				UnitResolverFactory factory = getFactory(uri);
				if(factory == null) {
					return Collections.emptyList();
				}

				UnitResolver resolver = factory.getResolver(uri);
				if (false == resolver instanceof UnitProvider) {
					return Collections.emptyList();
				}

				final List<UnitProxy> result = new ArrayList<UnitProxy>();
				
				UnitProvider.UnitVisitor visitor = new UnitProvider.UnitVisitor() {
					
					public boolean visitUnit(UnitProxy unit) {
						result.add(unit);
						return true;
					}
				};

				((UnitProvider) resolver).accept(visitor, null, UnitProvider.UnitVisitor.DEPTH_INFINITE, true);		
				return result;
			}
			
		}
	
	}
		
}
