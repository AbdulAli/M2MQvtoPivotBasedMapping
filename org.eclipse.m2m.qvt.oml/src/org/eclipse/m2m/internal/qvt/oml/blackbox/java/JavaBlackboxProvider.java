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
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.blackbox.AbstractBlackboxProvider;
import org.eclipse.m2m.internal.qvt.oml.blackbox.AbstractCompilationUnitDescriptor;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxException;
import org.eclipse.m2m.internal.qvt.oml.blackbox.CompilationUnit;
import org.eclipse.m2m.internal.qvt.oml.blackbox.LoadContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.OperationMatcher;
import org.eclipse.m2m.internal.qvt.oml.cst.CSTFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandlerAdapter;

public abstract class JavaBlackboxProvider extends AbstractBlackboxProvider {

	public static final String CLASS_NAME_SEPARATOR = "."; //$NON-NLS-1$

	private final Map<JavaUnitDescriptor, CompilationUnit> fBlackboxUnits = new LinkedHashMap<JavaUnitDescriptor, CompilationUnit>();

	static InstanceAdapterFactory createInstanceAdapterFactory(final Class<?> javaModuleClass) {
		return new InstanceAdapterFactory() {
			public Object createAdapter(EObject moduleInstance) {
				try {
					return javaModuleClass.newInstance();
				} catch (InstantiationException e) {
					// FIXME - choose a better exception
					throw new IllegalArgumentException("Illegal adapter instance", e); //$NON-NLS-1$
				} catch (IllegalAccessException e) {
					// FIXME - choose a better exception
					throw new IllegalArgumentException("Illegal adapter instance", e); //$NON-NLS-1$
				}
			}
		};
	}

	@Override
	public CompilationUnit loadCompilationUnit(AbstractCompilationUnitDescriptor descriptor, LoadContext loadContext)
			throws BlackboxException {
		if (descriptor instanceof JavaUnitDescriptor == false) {
			throw new IllegalArgumentException("Invalid descriptor"); //$NON-NLS-1$
		}
		JavaUnitDescriptor libDescriptor = (JavaUnitDescriptor) descriptor;

		if (fBlackboxUnits.containsKey(libDescriptor)) {
			return fBlackboxUnits.get(libDescriptor);
		}

		JavaModuleLoader javaModuleLoader = createJavaModuleLoader();

		BasicDiagnostic errors = null;
		List<QvtOperationalModuleEnv> loadedModules = new LinkedList<QvtOperationalModuleEnv>();

		for (Map.Entry<ModuleHandle, Map<String, List<EOperation>>> nextEntry : libDescriptor.fModules.entrySet()) {
			Diagnostic diagnostic = javaModuleLoader.loadModule(nextEntry.getKey(), nextEntry.getValue(), loadContext);

			if (DiagnosticUtil.isSuccess(diagnostic)) {
				QvtOperationalModuleEnv nextModuleEnv = javaModuleLoader.getLoadedModule();
				nextModuleEnv.getTypeResolver().getResource().setURI(descriptor.getURI());
				loadedModules.add(nextModuleEnv);

				if (diagnostic.getSeverity() != Diagnostic.OK) {
					QvtPlugin.logDiagnostic(diagnostic);
				}
			} else {
				if (errors == null) {
					String message = NLS.bind(JavaBlackboxMessages.BlackboxUnitLoadFailed, descriptor.getQualifiedName());
					errors = DiagnosticUtil.createErrorDiagnostic(message);
				}

				errors.add(diagnostic);
			}
		}

		if (errors != null) {
			final Diagnostic failDiagnostic = errors;
			fBlackboxUnits.put(libDescriptor, new CompilationUnit() {

				public List<QvtOperationalModuleEnv> getElements() {
					return Collections.emptyList();
				}

				public Diagnostic getDiagnostic() {
					return failDiagnostic;
				}
				
			});
			
			assert errors.getSeverity() == Diagnostic.ERROR;
			throw new BlackboxException(errors);
		}

		CompilationUnit compilationUnit = createCompilationUnit(loadedModules);
		fBlackboxUnits.put(libDescriptor, compilationUnit);
		return compilationUnit;
	}
	
	@Override
	public void cleanup() {
		fBlackboxUnits.clear();		
	}

	private JavaModuleLoader createJavaModuleLoader() {
		return new JavaModuleLoader() {
			JavaMethodHandlerFactory handlerFactory;

			@Override
			protected void loadModule(QvtOperationalModuleEnv moduleEnv, final Class<?> javaModuleClass) {
				handlerFactory = new JavaMethodHandlerFactory(moduleEnv.getOCLStandardLibrary());

				Module module = moduleEnv.getModuleContextType();
				// FIXME - workaround to make Environment available with the
				// module for
				// non-transformation execution context
				ASTBindingHelper.createCST2ASTBinding(CSTFactory.eINSTANCE.createLibraryCS(), module, moduleEnv);

				setInstanceAdapterFactory(module, createInstanceAdapterFactory(javaModuleClass));
			}

			@Override
			protected void loadOperation(EOperation operation, Method javaOperation) {
				setOperationHandler(operation, handlerFactory.createHandler(javaOperation), true);
			}
		};
	}

	protected static String getPackageNameFromJavaClass(String className) {
		int lastSeparatorPos = className.lastIndexOf(CLASS_NAME_SEPARATOR);
		if (lastSeparatorPos < 0) {
			return null;
		}

		return className.substring(0, lastSeparatorPos);
	}

	protected static String getSimpleNameFromJavaClass(String className) {
		int lastSeparatorPos = className.lastIndexOf(CLASS_NAME_SEPARATOR);
		if (lastSeparatorPos < 0) {
			return className;
		}

		return className.substring(lastSeparatorPos + 1);
	}

	abstract class JavaUnitDescriptor extends AbstractCompilationUnitDescriptor {

		private Map<ModuleHandle, Map<String, List<EOperation>>> fModules = new LinkedHashMap<ModuleHandle, Map<String, List<EOperation>>>();

		JavaUnitDescriptor(String unitQualifiedName) {
			super(JavaBlackboxProvider.this, unitQualifiedName);
		}

		protected void addModuleHandle(ModuleHandle moduleHandle) {
			fModules.put(moduleHandle, new LinkedHashMap<String, List<EOperation>>());
		}

		public Collection<CallHandler> getBlackboxCallHandler(ImperativeOperation imperativeOp, QvtOperationalModuleEnv env) {
			Set<String> importedLibs = env.getImportedNativeLibs().get(getURI());
			Collection<CallHandler> result = Collections.emptyList();

			for (Map.Entry<ModuleHandle, Map<String, List<EOperation>>> nextEntry : fModules.entrySet()) {
				if (!env.getImportedNativeLibs().isEmpty()) {
					if (!importedLibs.contains(nextEntry.getKey().getModuleName())) {
						continue;
					}
				}

				List<EOperation> listOp = nextEntry.getValue().get(imperativeOp.getName());
				if (listOp == null) {
					continue;
				}

				for (EOperation libraryOp : listOp) {
					if (OperationMatcher.matchOperation(env, imperativeOp, libraryOp)) {
						if (result.isEmpty()) {
							result = new LinkedList<CallHandler>();
						}
						result.add(CallHandlerAdapter.getDispatcher(libraryOp));
					}
				}
			}

			return result;
		}

		public Collection<CallHandler> getBlackboxCallHandler(OperationalTransformation transformation,
				QvtOperationalModuleEnv env) {
			Set<String> importedLibs = env.getImportedNativeLibs().get(getURI());
			Collection<CallHandler> result = Collections.emptyList();

			for (Map.Entry<ModuleHandle, Map<String, List<EOperation>>> nextEntry : fModules.entrySet()) {
				if (!env.getImportedNativeLibs().isEmpty()) {
					if (!importedLibs.contains(nextEntry.getKey().getModuleName())) {
						continue;
					}
				}

				List<EOperation> listOp = nextEntry.getValue().get(transformation.getName());
				if (listOp == null) {
					continue;
				}

				for (EOperation libraryOp : listOp) {
					if (OperationMatcher.matchOperation(env, transformation, libraryOp)) {
						if (result.isEmpty()) {
							result = new LinkedList<CallHandler>();
						}
						result.add(CallHandlerAdapter.getDispatcher(libraryOp));
					}
				}
			}

			return result;
		}
		
	}

}
