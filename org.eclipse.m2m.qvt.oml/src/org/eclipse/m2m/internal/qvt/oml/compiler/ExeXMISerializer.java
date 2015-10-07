/*******************************************************************************
 * Copyright (c) 2009, 2010 Borland Software Corporation and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ExecutableXMIHelper;
import org.eclipse.m2m.internal.qvt.oml.common.MDAConstants;

public class ExeXMISerializer {

	public static URI toSourceUnitURI(URI uri) {
		return uri.trimFileExtension().appendFileExtension(MDAConstants.QVTO_FILE_EXTENSION);
	}
	
	public static URI toXMIUnitURI(URI uri) {
		return uri.trimFileExtension().appendFileExtension(ExeXMISerializer.COMPILED_XMI_FILE_EXTENSION);
	}	
	
	public static void saveUnitXMI(CompiledUnit[] units, EPackage.Registry metamodelRegistry) throws IOException {
		ResourceSetImpl rs = new ResourceSetImpl();
		rs.setPackageRegistry(metamodelRegistry);
		
		for (CompiledUnit nextUnit : units) {
			if(nextUnit.getUnitCST() == null) {
				// loaded from compiled QVT XMI or java black-box library
				continue;
			}
			if (nextUnit.getModuleEnvironments().isEmpty()) {
				// nothing to save
				continue;
			}

			URI uri = nextUnit.getURI();
			URI xmiUnitURI = toXMIUnitURI(uri);

			Resource xmiUnitResource = new ExeXMIResource(xmiUnitURI);
			rs.getResources().add(xmiUnitResource);
			
			for (QvtOperationalModuleEnv nextModuleEnv : nextUnit.getModuleEnvironments()) {
				Resource unitResource = nextModuleEnv.getTypeResolver().getResource();
				unitResource.setURI(xmiUnitURI);
				
				ExecutableXMIHelper.fixResourceOnSave(unitResource);

				xmiUnitResource.getContents().addAll(unitResource.getContents());
			}
		}
		
		for (Resource next : rs.getResources()) {
			next.save(Collections.EMPTY_MAP);

			URI savedXMIURI = next.getURI();
			URI srcURI = toSourceUnitURI(savedXMIURI);
			Map<Object, Object> options = Collections.emptyMap();
			
	        Map<String, ?> xmiAttrs = URIConverter.INSTANCE.getAttributes(savedXMIURI, options);
			Map<String, Object> xmiNewAttributes = new HashMap<String, Object>(xmiAttrs);
			
			Object srcFileTimeStmp = URIConverter.INSTANCE.getAttributes(srcURI, options).get(URIConverter.ATTRIBUTE_TIME_STAMP);
			xmiNewAttributes.put(URIConverter.ATTRIBUTE_TIME_STAMP, srcFileTimeStmp);			
			// save identical time stamp to save XMI  
			URIConverter.INSTANCE.setAttributes(savedXMIURI, xmiNewAttributes, options);
			
			if(!next.getErrors().isEmpty()) {
				// FIXME -
				Diagnostic diagnostic = EcoreUtil.computeDiagnostic(next, false);
				QvtPlugin.logDiagnostic(diagnostic);
			}
		}
	}

	public static final String COMPILED_XMI_FILE_EXTENSION = "qvtox"; //$NON-NLS-1$ 
	
}
