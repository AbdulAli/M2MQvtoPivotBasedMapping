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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.expressions.ExpressionsFactory;
import org.eclipse.ocl.expressions.Variable;

public abstract class AbstractContextualOperations {
	
	protected static final CallHandler UNSUPPORTED_OPER = new CallHandler() {
		public Object invoke(ModuleInstance module, Object source,
				Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			throw new UnsupportedOperationException();
		}
	};		
	
	private final EClassifier fContextType;		
	private final AbstractQVTStdlib fLib;
			
	protected AbstractContextualOperations(AbstractQVTStdlib lib, EClassifier contextType) {
		if(lib == null || contextType == null) {
			throw new IllegalArgumentException();
		}
		
		fContextType = contextType;
		fLib = lib;
	}
	
	protected abstract OperationProvider[] getOperations();
			
	public AbstractQVTStdlib getStdlib() {
		return fLib;
	}
			
	public void define(EcoreEnvironment env) {
		for (OperationProvider operation : getOperations()) {
			EOperation defOper = operation.define(env);
			if(operation.fIsStatic) {
				QvtOperationalParserUtil.markAsStaticOperation(defOper);
			}
			
			if(CallHandlerAdapter.getDispatcher(defOper) == UNSUPPORTED_OPER) {
				QvtOperationalParserUtil.markAsUnsupported(defOper,
						(operation.fParamNames != null && operation.fParamNames.length > operation.fParamTypes.length) 
							? operation.fParamNames[operation.fParamTypes.length] : null);
			}
		}
	}
	
	protected OperationProvider createOwnedStaticOperationProvider(CallHandler dispatcher, String name, String[] parameterNames, EClassifier returnType, EClassifier... paramTypes) {
		OperationProvider provider = new OwnedOperationProvider(dispatcher, name, parameterNames, returnType, paramTypes);
		provider.fIsStatic = true;		
		return provider;
	}
	
	protected OperationProvider createStaticOperationProvider(CallHandler dispatcher, String name, String[] parameterNames, EClassifier returnType, EClassifier... paramTypes) {
		OperationProvider provider = new OperationProvider(dispatcher, name, parameterNames, returnType, paramTypes);
		provider.fIsStatic = true;		
		return provider;
	}		
	
	protected class OwnedOperationProvider extends OperationProvider {
		
		public OwnedOperationProvider(CallHandler dispatcher, String name,
				EClassifier returnType, EClassifier... paramTypes) {
			super(dispatcher, name, returnType, paramTypes);
		}
		
		public OwnedOperationProvider(CallHandler dispatcher, String name, String[] parameterNames, 
				EClassifier returnType, EClassifier... paramTypes) {
			super(dispatcher, name, parameterNames, returnType, paramTypes);
		}

		@Override
		public EOperation define(EcoreEnvironment env) {
			EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
			eOperation.setName(fName);
			int pos = 0;
			for (EClassifier cls : fParamTypes) {
				EParameter eParam = EcoreFactory.eINSTANCE.createEParameter();
				String paramName = cls.getName();
				if(fParamNames != null) {
					paramName = fParamNames[pos++];
				}
				
				eParam.setName(paramName);
				eParam.setEType(cls);
				eOperation.getEParameters().add(eParam);
			}
						 
			eOperation.setEType(fReturnType);
			
			assert fContextType instanceof EClass;
			((EClass)fContextType).getEOperations().add(eOperation);
			
			CallHandlerAdapter.attach(eOperation, fDispatcher);
			setupDeprecated(eOperation);
			
			return eOperation;
		}		
	}
	
	protected class OperationProvider {
		protected final String fName;
		protected final String[] fParamNames;		
		protected final EClassifier fReturnType;
		protected final EClassifier[] fParamTypes;
		protected final CallHandler fDispatcher;
		protected boolean fIsStatic; 
		protected boolean fIsDeprecated;		
		protected String fDeprecatedBy;
				
		protected OperationProvider(CallHandler dispatcher, String name, String[] paramNames, EClassifier returnType, EClassifier... paramTypes) {
			this.fName = name;
			this.fReturnType = returnType;
			this.fParamTypes = paramTypes;
			this.fDispatcher = dispatcher;
			this.fIsStatic = false;
			
			if(paramNames != null && paramNames.length < paramTypes.length) {
				throw new IllegalArgumentException("Invalid number of parameter names"); //$NON-NLS-1$
			}
			this.fParamNames = paramNames; 
		}
		
		protected OperationProvider(CallHandler dispatcher, String name, EClassifier returnType, EClassifier... paramTypes) {
			this(dispatcher, name, null /*no parameter names*/, returnType, paramTypes);			
		}
		
		public OperationProvider deprecateBy(String deprecatingReplacement) {
			fIsDeprecated = true;
			fDeprecatedBy = deprecatingReplacement;
			return this;
		}
				
		public OperationProvider deprecate() {
			return deprecateBy(null);
		}		
		
		public CallHandler callDispatcher() {
			return fDispatcher;
		}
		
		public OperationProvider implentBy(CallHandler dispatch) {
			return this;
		}
		
		public EOperation define(EcoreEnvironment env) {
			List<Variable<EClassifier, EParameter>> argList = new ArrayList<Variable<EClassifier, EParameter>>();
			int pos = 0;
			for (EClassifier cls : fParamTypes) {
				Variable<EClassifier, EParameter> stringVariable = ExpressionsFactory.eINSTANCE.createVariable();
				
				String paramName = cls.getName();
				if(fParamNames != null) {
					paramName = fParamNames[pos++];
				}
				stringVariable.setName(paramName);
				stringVariable.setType(cls);
				argList.add(stringVariable);
			}

			EOperation result = env.defineOperation(fContextType, fName, fReturnType, argList,
						org.eclipse.ocl.ecore.EcoreFactory.eINSTANCE.createConstraint());

			CallHandlerAdapter.attach(result, fDispatcher);			
			setupDeprecated(result);
			
			return result;
		}

		void setupDeprecated(EOperation result) {
			if(fIsDeprecated) {
				if(fDeprecatedBy != null) {
					QvtOperationalParserUtil.markAsDeprecated(result, fDeprecatedBy);
				} else {
					QvtOperationalParserUtil.markAsDeprecated(result);					
				}
			}
		}
		
		@Override
		public String toString() {
			return fName + (fParamNames != null ? Arrays.toString(fParamNames) : "");
		}
	}
}