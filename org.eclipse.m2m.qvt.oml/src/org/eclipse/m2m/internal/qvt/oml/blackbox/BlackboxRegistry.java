/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 289982, 326871, 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.blackbox.java.StandaloneBlackboxProvider;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;

/*
 * TODO - handle collisions of multiple descriptors of the same qualified name
 */
public class BlackboxRegistry {

	public static final BlackboxRegistry INSTANCE = EMFPlugin.IS_ECLIPSE_RUNNING ? new BlackboxRegistry.Eclipse()
			: new BlackboxRegistry();

	private final StandaloneBlackboxProvider fStandaloneProvider = new StandaloneBlackboxProvider();
	private final List<AbstractBlackboxProvider> fProviders;

	public BlackboxRegistry() {
		fProviders = Collections.<AbstractBlackboxProvider>singletonList(fStandaloneProvider);
	}

	protected List<? extends AbstractBlackboxProvider> getProviders() {
		return fProviders;
	}

	public AbstractCompilationUnitDescriptor getCompilationUnitDescriptor(String qualifiedName, ResolutionContext context) {
		for (AbstractBlackboxProvider provider : getProviders()) {
			AbstractCompilationUnitDescriptor descriptor = provider.getModuleDescriptor(qualifiedName, context);
			if (descriptor != null) {
				return descriptor;
			}
		}
		return null;
	}

	public CompilationUnit loadCompilationUnit(AbstractCompilationUnitDescriptor descriptor, LoadContext loadContext)
			throws BlackboxException {
		if (descriptor == null) {
			throw new IllegalArgumentException("Null blackbox descriptor"); //$NON-NLS-1$
		}

		AbstractBlackboxProvider provider = descriptor.getProvider();
		return provider.loadCompilationUnit(descriptor, loadContext);
	}

	public List<AbstractCompilationUnitDescriptor> getCompilationUnitDescriptors(ResolutionContext loadContext) {
		ArrayList<AbstractCompilationUnitDescriptor> result = new ArrayList<AbstractCompilationUnitDescriptor>();
		for (AbstractBlackboxProvider provider : getProviders()) {
			for (AbstractCompilationUnitDescriptor abstractCompilationUnitDescriptor : provider.getModuleDescriptors(loadContext)) {
				result.add(abstractCompilationUnitDescriptor);
			}
		}
		return result;
	}

	public void cleanup() {
		for (AbstractBlackboxProvider provider : getProviders()) {
			provider.cleanup();
		}
	}

	public Collection<CallHandler> getBlackboxCallHandler(ImperativeOperation operation, QvtOperationalModuleEnv env) {
		Collection<CallHandler> result = Collections.emptyList();
		for (AbstractBlackboxProvider provider : getProviders()) {
			Collection<CallHandler> handlers = provider.getBlackboxCallHandler(operation, env);
			if (!handlers.isEmpty()) {
				if (result.isEmpty()) {
					result = new LinkedList<CallHandler>();
				}
				result.addAll(handlers);
			}
		}
		return result;
	}

	public Collection<CallHandler> getBlackboxCallHandler(OperationalTransformation transformation, QvtOperationalModuleEnv env) {
		Collection<CallHandler> result = Collections.emptyList();
		for (AbstractBlackboxProvider provider : getProviders()) {
			Collection<CallHandler> handlers = provider.getBlackboxCallHandler(transformation, env);
			if (!handlers.isEmpty()) {
				if (result.isEmpty()) {
					result = new LinkedList<CallHandler>();
				}
				result.addAll(handlers);
			}
		}
		return result;
	}
	
	public void addStandaloneModule(Class<?> cls, String unitQualifiedName, String moduleName, String[] packageURIs) {
		fStandaloneProvider.registerDescriptor(cls, unitQualifiedName, moduleName, packageURIs);
	}


	private static class Eclipse extends BlackboxRegistry {

		private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

		private static final String PROVIDER_ELEMENT = "provider"; //$NON-NLS-1$

		private static final String BLACKBOX_PROVIDER_EXTENSION = "blackboxProvider"; //$NON-NLS-1$

		private final List<AbstractBlackboxProvider> fProviders;

		@Override
		protected List<? extends AbstractBlackboxProvider> getProviders() {
			return fProviders;
		}

		Eclipse() {
			fProviders = new LinkedList<AbstractBlackboxProvider>();
			readProviders(fProviders);
			fProviders.addAll(super.getProviders());
		}

		private void readProviders(List<AbstractBlackboxProvider> providers) {
			IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(QvtPlugin.ID,
					BLACKBOX_PROVIDER_EXTENSION);

			for (IConfigurationElement element : configs) {
				try {
					if (element.getName().equals(PROVIDER_ELEMENT)) {
						Object extension = element.createExecutableExtension(CLASS_ATTR);
						if (extension instanceof AbstractBlackboxProvider == false) {
							QvtPlugin.error("Provider must implement AbstractBlackboxProvider interface: " + extension); //$NON-NLS-1$
							continue;
						}

						providers.add((AbstractBlackboxProvider) extension);
					}
				} catch (CoreException e) {
					QvtPlugin.getDefault().log(e.getStatus());
				} catch (RuntimeException e) {
					QvtPlugin.error(e);
				}
			}
		}
	}

}
