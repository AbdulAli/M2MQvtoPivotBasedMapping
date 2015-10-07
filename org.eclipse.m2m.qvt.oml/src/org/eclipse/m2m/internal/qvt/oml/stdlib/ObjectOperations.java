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

import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;


public class ObjectOperations extends AbstractContextualOperations {

	static final String REPR_NAME = "repr"; //$NON-NLS-1$
	
	
	public ObjectOperations(AbstractQVTStdlib library) {
		super(library, library.getObject());
	}
		
	@Override
	protected OperationProvider[] getOperations() {
		return new OperationProvider[] { 
		};
	}
	
	
	static final CallHandler REPR = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			try {
				Object toPrint = source;
				if (args.length == 1) {
					toPrint = args[0];
				}
				return String.valueOf(toPrint);
			}
			catch (Exception e) {
				QvtPlugin.error("Object::repr()", e); //$NON-NLS-1$
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
		}
	};

}
