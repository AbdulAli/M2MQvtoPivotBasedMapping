/*******************************************************************************
 * Copyright (c) 2009, 2015 Borland Software Corporation and others.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ValidationMessages;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxRegistry;
import org.eclipse.m2m.internal.qvt.oml.blackbox.AbstractCompilationUnitDescriptor;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxException;
import org.eclipse.m2m.internal.qvt.oml.blackbox.CompilationUnit;
import org.eclipse.m2m.internal.qvt.oml.blackbox.LoadContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContextImpl;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitContents.ModelContents;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;

public class BlackboxUnitResolver implements UnitResolver {
	
	public static final URI GLOBAL_CONTEXT = URI.createURI("/"); //$NON-NLS-1$ 

	// the global scope black-box resolver
	public static final BlackboxUnitResolver DEFAULT = new BlackboxUnitResolver(GLOBAL_CONTEXT);
	
	private ResolutionContext fContext;
	
	public BlackboxUnitResolver(URI context) {
		fContext = new ResolutionContextImpl(context);
	}
	
	public static boolean isBlackboxUnitURI(URI uri) {
		return AbstractCompilationUnitDescriptor.URI_SCHEME.equals(uri.scheme()) && 
			AbstractCompilationUnitDescriptor.URI_AUTHORITY.equals(uri.authority());
	}
		
	public UnitProxy resolveUnit(String qualifiedName) {
		
		AbstractCompilationUnitDescriptor descriptor = BlackboxRegistry.INSTANCE.getCompilationUnitDescriptor(qualifiedName, fContext);
		
		if (descriptor != null) {
			int namePos = qualifiedName.lastIndexOf('.');
			String name;
			String namespace;
			if(namePos > 0) {
				namespace = qualifiedName.substring(0, namePos);
				
				if(namePos + 1 < qualifiedName.length()) {
					++namePos;
				}
				name = qualifiedName.substring(namePos);

			} else {
				name = qualifiedName;
				namespace = null;
			}
			
			return new BBoxUnit(namespace, name, descriptor);
		}

		return null;
	}


	class BBoxUnit extends UnitProxy {

		private AbstractCompilationUnitDescriptor fDescriptor;
		
		BBoxUnit(String namespace, String name, AbstractCompilationUnitDescriptor descriptor) {
			super(namespace, name, descriptor.getURI());
			
			fDescriptor = descriptor;
		}

		@Override
		public int getContentType() {
			return UnitProxy.TYPE_MODEL;
		}
		
		@Override
		public UnitContents getContents() throws IOException {
			return new BBoxUnitContents(fDescriptor);
		}

		@Override
		public UnitResolver getResolver() {
			return BlackboxUnitResolver.this;
		}
	}
	
	class BBoxUnitContents implements ModelContents {
		
		private AbstractCompilationUnitDescriptor fDescriptor;
		private Diagnostic fProblems;
		
		
		BBoxUnitContents(AbstractCompilationUnitDescriptor descriptor) {
			assert descriptor != null;
			fDescriptor = descriptor;
		}
				
		public Diagnostic getProblems() {
			return fProblems;
		}
		
		public List<EObject> loadElements(Registry packageRegistry) {
			LoadContext loadContext = new LoadContext(packageRegistry);
			CompilationUnit cunit = null;
			try {
				cunit = BlackboxRegistry.INSTANCE.loadCompilationUnit(fDescriptor, loadContext);
				if (cunit.getDiagnostic().getSeverity() == Diagnostic.ERROR) {
					String errMessage = NLS.bind(ValidationMessages.FailedToLoadUnit, fDescriptor.getQualifiedName());
					fProblems = new BasicDiagnostic(cunit.getDiagnostic().getSource(), cunit.getDiagnostic().getCode(), 
							QvtOperationalParserUtil.wrappInSeeErrorLogMessage(errMessage), null);
				}
			} catch (BlackboxException e) {
				Diagnostic diagnostic = e.getDiagnostic();
				if(diagnostic != null) {
					QvtPlugin.logDiagnostic(diagnostic);					
				} else {
					QvtPlugin.error(NLS.bind(ValidationMessages.FailedToLoadUnit, 
							new Object[] { fDescriptor.getQualifiedName() }), e);
				}

				String errMessage = NLS.bind(ValidationMessages.FailedToLoadUnit, fDescriptor.getQualifiedName());
				fProblems = new BasicDiagnostic(diagnostic.getSource(), diagnostic.getCode(), 
						QvtOperationalParserUtil.wrappInSeeErrorLogMessage(errMessage), null);
			}
			
			if(cunit == null) {
				return Collections.emptyList();
			}
			
			List<QvtOperationalModuleEnv> elementEnvs = cunit.getElements();			
			List<EObject> unitElements = new ArrayList<EObject>(elementEnvs.size());
						
			for (QvtOperationalModuleEnv nextEnv : elementEnvs) {
				Module module = nextEnv.getModuleContextType();
				if(module != null) {
					unitElements.add(module);
					ASTBindingHelper.setEnvironment(module, nextEnv);					
				}
			}

			return unitElements;   
		}
	}
	
}
