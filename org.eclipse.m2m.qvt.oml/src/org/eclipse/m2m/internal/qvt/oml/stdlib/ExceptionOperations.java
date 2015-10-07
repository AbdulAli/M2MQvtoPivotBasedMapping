/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Alex Paperno - bug 419299 
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.stdlib.model.ExceptionInstance;
import org.eclipse.ocl.types.OCLStandardLibrary;


public class ExceptionOperations  extends AbstractContextualOperations {

	static final String GET_ARGUMENT_NAME = "getArgument"; //$NON-NLS-1$
	
	
	public ExceptionOperations(AbstractQVTStdlib library) {
		super(library, library.getExceptionClass());
	}
		
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		
		return new OperationProvider[] { 
			new OwnedOperationProvider(GET_ARGUMENT, GET_ARGUMENT_NAME, oclStdlib.getString()),
		};
	}
	
	
	private static final CallHandler GET_ARGUMENT = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			ExceptionInstance exception = (ExceptionInstance) source;
		    return exception.getArgument();
		}
	};
	
}
