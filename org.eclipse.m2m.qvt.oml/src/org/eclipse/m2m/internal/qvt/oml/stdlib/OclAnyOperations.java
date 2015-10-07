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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.utilities.PredefinedType;


public class OclAnyOperations extends AbstractContextualOperations {

	public OclAnyOperations(AbstractQVTStdlib library) {
		super(library, library.getEnvironment().getOCLStandardLibrary().getOclAny());
	}
		
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		
		return new OperationProvider[] {
			new OperationProvider(ObjectOperations.REPR, ObjectOperations.REPR_NAME,
					oclStdlib.getString()),
				
			new OperationProvider(StdlibModuleOperations.DUMP, StdlibModuleOperations.DUMP_NAME,
					oclStdlib.getOclVoid()).deprecate(),
					
			new OperationProvider(ElementOperations.ALL_SUBOBJECTS_OF_KIND, PredefinedType.ALL_INSTANCES_NAME,
					oclStdlib.getSet(),	oclStdlib.getOclType())
					.deprecateBy("Element::allSubobjectsOfKind(OclType)"), //$NON-NLS-1$
		};
	}
	
}
