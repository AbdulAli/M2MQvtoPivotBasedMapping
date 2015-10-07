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
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.stdlib.model.StatusInstance;
import org.eclipse.ocl.types.OCLStandardLibrary;

public class StatusOperations extends AbstractContextualOperations {
	
	static final String SUCCEEDED_NAME = "succeeded"; //$NON-NLS-1$
	static final String FAILED_NAME = "failed"; //$NON-NLS-1$
	static final String RAISED_EXCEPTION_NAME = "raisedException"; //$NON-NLS-1$

	
    public StatusOperations(AbstractQVTStdlib library) {
		super(library, library.getStatusClass());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		
		return new OwnedOperationProvider[] {
			new OwnedOperationProvider(SUCCEEDED, SUCCEEDED_NAME, oclStdlib.getBoolean()),
			new OwnedOperationProvider(FAILED, FAILED_NAME, oclStdlib.getBoolean()),
			new OwnedOperationProvider(RAISED_EXCEPTION, RAISED_EXCEPTION_NAME, getStdlib().getExceptionClass()),
		};
	}
	
	
	private static final CallHandler SUCCEEDED = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof StatusInstance) {
				return ((StatusInstance) source).succeeded();
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv); 
		}
	};

	private static final CallHandler FAILED = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof StatusInstance) {
				return ((StatusInstance) source).failed();
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv); 
		}
	};

	private static final CallHandler RAISED_EXCEPTION = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof StatusInstance) {
				return ((StatusInstance) source).raisedException();
			}
			
			return CallHandlerAdapter.getInvalidResult(evalEnv); 
		}
	};	
}
