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
 *******************************************************************************/

package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.util.Log;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.TypeUtil;

public class StdlibModuleOperations extends AbstractContextualOperations {
	
    static final String DUMP_NAME = "dump"; //$NON-NLS-1$

    
    public StdlibModuleOperations(AbstractQVTStdlib library) {
		super(library, library.getStdLibModule());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		EClassifier collectionOfT2 = TypeUtil.resolveCollectionType(getStdlib().getEnvironment(),
				CollectionKind.COLLECTION_LITERAL, oclStdlib.getT2());

		return new OperationProvider[] {
			new OwnedOperationProvider(DUMP, DUMP_NAME, oclStdlib.getOclVoid(), oclStdlib.getOclAny())
				.deprecate(),
			new OwnedOperationProvider(DUMP, DUMP_NAME, oclStdlib.getOclVoid(), collectionOfT2)
				.deprecate(),
		};
	}
	
	
	static final CallHandler DUMP = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Log logger = evalEnv.getContext().getLog();
			Object toPrint = source;
			if (args.length == 1) {
				toPrint = args[0];
			}

			logger.log(String.valueOf(toPrint));
		    return null;
		}
	};
}
