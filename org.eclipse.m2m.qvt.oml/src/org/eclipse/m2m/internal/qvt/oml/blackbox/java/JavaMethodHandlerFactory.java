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
 *     Christopher Gerking - bug 289982
 *     Camille Letavernier - Bug 458651
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.env.InternalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.NumberConversions;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandlerAdapter;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.ocl.types.OCLStandardLibrary;

class JavaMethodHandlerFactory {
	
	private static int FAILURE_COUNT_TOLERANCE = 5;
	
	final private Object fInvalid;
	
	JavaMethodHandlerFactory(OCLStandardLibrary<EClassifier> oclStdLib) {
		fInvalid = oclStdLib.getInvalid();
	}
	
	CallHandler createHandler(Method method) {
		if(method == null) {
			throw new IllegalArgumentException();
		}
		
		Operation opAnnotation = method.getAnnotation(Operation.class);		
		return new Handler(method, opAnnotation != null && opAnnotation.contextual(),
				opAnnotation != null && opAnnotation.withExecutionContext());
	}
	
	private Object getInvalidResult() {
		return fInvalid;
	}
		
	private class Handler extends CallHandler {
				
		private final Method fMethod;
		private final Class<?>[] fCachedParamTypes;
		private final boolean fIsContextual;
		private final boolean fWithExecutionContext;
		private final boolean fRequiresNumConversion;
		private volatile int fFatalErrorCount;		
		
		Handler(Method method, boolean isContextual, boolean isWithExecutionContext) {
			assert method != null;
			
			fMethod = method;
			fCachedParamTypes = fMethod.getParameterTypes(); 			
			fIsContextual = isContextual;
			fWithExecutionContext = isWithExecutionContext;
			fRequiresNumConversion = requiresNumberConversion(); 
			fFatalErrorCount = 0;
		}		

		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			try {
				if(isDisabled()) {
					return getInvalidResult();
				}
							
				Object[] actualArgs = prepareArguments(source, args, evalEnv);
				Object javaCallSource = null;
				
				boolean isStatic = Modifier.isStatic(fMethod.getModifiers());
				if(!isStatic) {
					Class<?> moduleJavaClass = fMethod.getDeclaringClass();					
					javaCallSource = getJavaCallSource(module, moduleJavaClass, evalEnv); //module.getAdapter(moduleJavaClass);					
					assert javaCallSource != null;
				}
								
				return fMethod.invoke(javaCallSource, actualArgs);
			}
			catch (IllegalArgumentException e) {
				incrementFatalErrorCount();
				QvtPlugin.error(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e);
				evalEnv.getAdapter(InternalEvaluationEnv.class).throwQVTException(
						new QvtRuntimeException(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e));
				return CallHandlerAdapter.getInvalidResult(evalEnv);			
			} 
			catch (IllegalAccessException e) {
				incrementFatalErrorCount();				
				QvtPlugin.error(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e);				
				evalEnv.getAdapter(InternalEvaluationEnv.class).throwQVTException(
						new QvtRuntimeException(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e));
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
			catch (InstantiationException e) {
				incrementFatalErrorCount();				
				QvtPlugin.error(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e);				
				evalEnv.getAdapter(InternalEvaluationEnv.class).throwQVTException(
						new QvtRuntimeException(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e));
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
			catch (InvocationTargetException e) {
				incrementFatalErrorCount();
				QvtPlugin.error(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod), e.getTargetException());				
				// should not happen at all, as we do not support QVT exception in signature yet
				String localized = "\nCaused by: " + e.getTargetException().getClass().getName() + //$NON-NLS-1$ 
						(e.getTargetException().getLocalizedMessage() == null ? "" : ": " + e.getTargetException().getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				evalEnv.getAdapter(InternalEvaluationEnv.class).throwQVTException(
						new QvtRuntimeException(NLS.bind(JavaBlackboxMessages.MethodInvocationError, fMethod) + localized, e.getTargetException()));
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
		}
		
		private void incrementFatalErrorCount(){
			fFatalErrorCount++;
		}
						
		private Object getJavaCallSource(ModuleInstance moduleInstance, Class<?> javaClass, QvtOperationalEvaluationEnv evalEnv)
				throws IllegalAccessException, InstantiationException {

			Object callSource = moduleInstance.getAdapter(javaClass);
			if (callSource != null) {
				return callSource;
			}

			TransformationInstance rootTransformation = evalEnv.getRoot().getAdapter(InternalEvaluationEnv.class).getCurrentTransformation();

			callSource = rootTransformation.getAdapter(javaClass);
			if (callSource == null) {
				callSource = javaClass.newInstance();
				rootTransformation.getAdapter(ModuleInstance.Internal.class).addAdapter(callSource);
			}

			moduleInstance.getAdapter(ModuleInstance.Internal.class).addAdapter(callSource);

			return callSource;
		}
		
		private boolean isDisabled() {
			return fFatalErrorCount > FAILURE_COUNT_TOLERANCE;
		}
		
		private Object[] prepareArguments(Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			int argCount = args.length;
			if (fIsContextual) {
				argCount++;
			}
			if (fWithExecutionContext) {
				argCount++;
			}

			Object resultArgs[] = new Object[argCount];
			
			int argIndex = 0;
			if (fWithExecutionContext) {
				resultArgs[argIndex] = evalEnv.getContext();
				argIndex++;
			}
			if (fIsContextual) {
				resultArgs[argIndex] = source;
				argIndex++;
			}
			
			// filter out possible OclInvalid argument values passed from AST based evaluation
			// source can't be this case as the call can not be made
			for (int i = 0; i < args.length; i++) {
				Object nextArg = args[i];
				if(nextArg == getInvalidResult()) {
					// convert OclInvalid to 'null' as java reflection invocation would fail
					// with the argument class incompatible to the method signature
					nextArg = null;
				}
				// number have to converted as java binary compatible
				if(fRequiresNumConversion) {
					nextArg = NumberConversions.convertNumber(nextArg, fCachedParamTypes[argIndex]);
				}
				resultArgs[argIndex++] = nextArg;
			}
			
			return resultArgs;
		}
		
		private boolean requiresNumberConversion() {
			assert fMethod != null;
			
			for (Class<?> paramType : fMethod.getParameterTypes()) {
				if(Number.class.isAssignableFrom(paramType)) {
					return true;
				}
			}
			return false;
		}		
	}	
}
