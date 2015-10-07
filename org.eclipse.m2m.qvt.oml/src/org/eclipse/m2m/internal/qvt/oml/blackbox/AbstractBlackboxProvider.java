/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ValidationMessages;
import org.eclipse.m2m.internal.qvt.oml.compiler.BlackboxUnitResolver;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstanceFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandlerAdapter;

public abstract class AbstractBlackboxProvider {
		
	private static final ResolutionContext GLOBAL_RESOLUTION_CONTEXT = new ResolutionContextImpl(BlackboxUnitResolver.GLOBAL_CONTEXT);

	public interface InstanceAdapterFactory {
		Object createAdapter(EObject moduleInstance);
	}

	protected AbstractBlackboxProvider() {
		super();
	}

	protected CompilationUnit createCompilationUnit(
			QvtOperationalModuleEnv moduleEnv) {
		return createCompilationUnit(Collections.singletonList(moduleEnv));
	}

	protected CompilationUnit createCompilationUnit(
			final List<QvtOperationalModuleEnv> loadedModules) {
		return new CompilationUnit() {
			public List<QvtOperationalModuleEnv> getElements() {
				return Collections.unmodifiableList(loadedModules);
			}
			public Diagnostic getDiagnostic() {
				return Diagnostic.OK_INSTANCE;
			}
		};
	}

	public static void setInstanceAdapterFactory(Module module, final InstanceAdapterFactory factory) {
		ModuleInstanceFactory moduleInstanceFactory = (ModuleInstanceFactory) module
				.getEFactoryInstance();
		moduleInstanceFactory
				.addPostCreateHandler(new ModuleInstanceFactory.PostCreateHandler() {
					public void created(ModuleInstance moduleInstance) {
						Object adapterInstance = factory
								.createAdapter(moduleInstance);
						moduleInstance
								.getAdapter(ModuleInstance.Internal.class)
								.addAdapter(adapterInstance);
					}
				});
	}

	protected void setOperationHandler(EOperation operation,
			final CallHandler handler, boolean adaptSource) {
		CallHandler actualHandler = handler;
		if (adaptSource) {
			actualHandler = new CallHandler() {
				public Object invoke(ModuleInstance module, Object source,
						Object[] args, QvtOperationalEvaluationEnv evalEnv) {
					return handler.invoke(module, source, args, evalEnv);
				}
			};
		}

		CallHandlerAdapter.attach(operation, actualHandler);
	}

	public abstract Collection<? extends AbstractCompilationUnitDescriptor> getModuleDescriptors(
			ResolutionContext resolutionContext);

	public abstract AbstractCompilationUnitDescriptor getModuleDescriptor(
			String qualifiedName, ResolutionContext resolutionContext);

	public abstract CompilationUnit loadCompilationUnit(
			AbstractCompilationUnitDescriptor descriptor,
			LoadContext loadContext) throws BlackboxException;
	
	public abstract void cleanup();
	
	private void handleBlackboxException(BlackboxException e, AbstractCompilationUnitDescriptor descriptor) {
		
		Diagnostic diagnostic = e.getDiagnostic();
		if(diagnostic != null) {
			QvtPlugin.logDiagnostic(diagnostic);					
		} else {
			QvtPlugin.error(NLS.bind(ValidationMessages.FailedToLoadUnit, 
					new Object[] { descriptor.getQualifiedName() }), e);
		}
		
	}
	
	public Collection<CallHandler> getBlackboxCallHandler(ImperativeOperation operation, QvtOperationalModuleEnv env) {
		Collection<CallHandler> result = Collections.emptyList();
		for (AbstractCompilationUnitDescriptor d : getModuleDescriptors(GLOBAL_RESOLUTION_CONTEXT)) {
			if (env.getImportedNativeLibs().isEmpty()) {
				try {
					loadCompilationUnit(d, new LoadContext(env.getEPackageRegistry()));
				} catch (BlackboxException e) {
					handleBlackboxException(e, d);
					
					continue;
				}
			}
			else {
				if (!env.getImportedNativeLibs().containsKey(d.getURI())) {
					continue;
				}
			}
			
			Collection<CallHandler> handlers = d.getBlackboxCallHandler(operation, env);
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
		for (AbstractCompilationUnitDescriptor d : getModuleDescriptors(GLOBAL_RESOLUTION_CONTEXT)) {
			if (env.getImportedNativeLibs().isEmpty()) {
				try {
					loadCompilationUnit(d, new LoadContext(env.getEPackageRegistry()));
				} catch (BlackboxException e) {
					handleBlackboxException(e, d);
					
					continue;
				}
			}
			else {
				if (!env.getImportedNativeLibs().containsKey(d.getURI())) {
					continue;
				}
			}
			
			Collection<CallHandler> handlers = d.getBlackboxCallHandler(transformation, env);
			if (!handlers.isEmpty()) {
				if (result.isEmpty()) {
					result = new LinkedList<CallHandler>();
				}
				result.addAll(handlers);
			}
		}		
		return result;
	}
	
}
