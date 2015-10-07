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
 *     Alex Paperno - bugs 404647
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.m2m.qvt.oml.util.Dictionary;
import org.eclipse.ocl.types.OCLStandardLibrary;


public class DictionaryOperations extends AbstractContextualOperations {

	static final String GET_NAME = "get"; //$NON-NLS-1$
	static final String PUT_NAME = "put"; //$NON-NLS-1$
	static final String CLEAR_NAME = "clear"; //$NON-NLS-1$	
	static final String HASKEY_NAME = "hasKey"; //$NON-NLS-1$
	static final String VALUES_NAME = "values"; //$NON-NLS-1$
	static final String KEYS_NAME = "keys"; //$NON-NLS-1$	
	static final String DEFAULTGET_NAME = "defaultget"; //$NON-NLS-1$	
	
	
	public DictionaryOperations(AbstractQVTStdlib library) {
		super(library, library.getDictionary());
	}	
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		ListType listOfKeyT = getStdlib().getEnvironment().getTypeResolver().resolveListType(getStdlib().getKeyT());
		
		return new OperationProvider[] {
			new OperationProvider(GET, GET_NAME, new String[] { "key" }, //$NON-NLS-1$ 
					oclStdlib.getT(), getStdlib().getKeyT()),
					
			new OperationProvider(PUT, PUT_NAME, new String[] { "key", "value" }, //$NON-NLS-1$ //$NON-NLS-2$
					oclStdlib.getOclVoid(), getStdlib().getKeyT(), oclStdlib.getT()),
					
			new OperationProvider(HASKEY, HASKEY_NAME, new String[] { "key" }, //$NON-NLS-1$
					oclStdlib.getBoolean(), getStdlib().getKeyT()),
					
			new OperationProvider(CLEAR, CLEAR_NAME, oclStdlib.getOclVoid()),
			
			new OperationProvider(KEYS, KEYS_NAME, listOfKeyT),
					
			new OperationProvider(VALUES, VALUES_NAME, getStdlib().getList()),
			
			new OperationProvider(DEFAULTGET, DEFAULTGET_NAME, new String[] { "key", "default" }, //$NON-NLS-1$ //$NON-NLS-2$
					oclStdlib.getT(), getStdlib().getKeyT(), oclStdlib.getT()),			
		};
	}

	
	static CallHandler GET = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")			
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			return dict.get(args[0]);
		}
	};
	
	static CallHandler DEFAULTGET = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")			
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			return dict.defaultget(args[0], args[1]);
		}
	};

	static CallHandler PUT = new CallHandlerMutator() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")			
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			
			final Object key = args[0];			
			final Object val = args[1];
			
			Object invalidObj = CallHandlerAdapter.getInvalidResult(evalEnv);
			// skip invalid values
			if(key != invalidObj && val != invalidObj) {
				dict.put(key, val);
			}
			return null;
		}
	};
	
	static CallHandler CLEAR = new CallHandlerMutator() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")			
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			dict.clear();
			return null;
		}
	};	
	
	static CallHandler HASKEY = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")			
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			return dict.hasKey(args[0]);
		}
	};
	
	static CallHandler VALUES = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			return dict.values();
		}
	};

	static CallHandler KEYS = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			@SuppressWarnings("unchecked")
			Dictionary<Object, Object> dict = (Dictionary<Object, Object>) source;
			return dict.keys();
		}
	};	
	
}
