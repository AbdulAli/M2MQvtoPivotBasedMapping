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
 *     Christopher Gerking - bug 431082
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml;

import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.internal.qvt.oml.InternalTransformationExecutor.TracesAwareExecutor;
import org.eclipse.m2m.internal.qvt.oml.TransformationExecutorBlackboxRegistry;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTEvaluationOptions;
import org.eclipse.m2m.qvt.oml.util.Trace;

/**
 * A utility class that enables to execute existing transformation in the
 * specified execution context.
 * 
 * @since 2.0
 */
public final class TransformationExecutor {
	
	/**
	 * Support for blackbox libraries in standalone mode.
	 * Allows to import a Java classes by means of their fully qualified names, and call its methods directly from QVTo.
	 * 
	 * <p> To make this work, there is {@code Annotation} type called 'Module' which enables annotating a Java class with 
	 * the set of nsURIs that it requires to resolve types:
	 * 
	 * <p> <blockquote><pre>
	 *  @Module(packageURIs={"http://www.eclipse.org/emf/2002/Ecore"})
	 *  public class Test {
   	 *    ...
	 *  }
	 * </pre></blockquote>
	 * 
	 * Usage example:
	 * 
	 * <p> <blockquote><pre>
	 * TransformationExecutor.BlackboxRegistry.INSTANCE.registerModules(Test.class);

	 * TransformationExecutor executor = new TransformationExecutor(uri);
	 * ExecutionContextImpl context = new ExecutionContextImpl();
	 * ExecutionDiagnostic result = executor.execute(context);
	 * </pre></blockquote>
	 * 
	 * @since 3.5
	 * 
	 */
	public interface BlackboxRegistry {
		BlackboxRegistry INSTANCE = new TransformationExecutorBlackboxRegistry();
				
		Diagnostic registerModules(Class<?>... classes);
		
		Diagnostic registerModule(Class<?> cls);
				
		Diagnostic registerModule(Class<?> cls, String unitQualifiedName, String moduleName);
		
		Diagnostic registerModule(Class<?> cls, String unitQualifiedName, String moduleName, String[] packageURIs);
	}
	

	private TracesAwareExecutor fExector;
	
	/**
	 * Constructs the executor for the given transformation URI.
	 * <p>
	 * No attempt to resolve and load the transformation is done at this step
	 * 
	 * @param uri
	 *            the URI of an existing transformation
	 */
	public TransformationExecutor(URI uri) {
		fExector = new TracesAwareExecutor(uri);
	}
	
	/**
	 * Constructs the executor for the given transformation URI.
	 * <p>
	 * No attempt to resolve and load the transformation is done at this step
	 * 
	 * @param uri
	 *            the URI of an existing transformation
	 * @param registry
	 *            a package registry of meta-models to be referenced by the
	 *            executed transformation
	 * @since 3.0
	 */
	public TransformationExecutor(URI uri, EPackage.Registry registry) {
		fExector = new TracesAwareExecutor(uri, registry);
	}
			
	/**
	 * Attempts to load the transformation referred by this executor and checks
	 * if it is valid for execution.
	 * <p>
	 * <b>Remark:</b></br> Only the first performs the actual transformation
	 * loading, subsequent calls to this method will return the existing
	 * diagnostic.
	 * 
	 * @return the diagnostic indicating possible problems of the load action
	 */
	public Diagnostic loadTransformation() {
		return loadTransformation(new NullProgressMonitor());
	}
	
	/**
	 * Attempts to load the transformation referred by this executor and checks
	 * if it is valid for execution.
	 * <p>
	 * <b>Remark:</b></br> Only the first performs the actual transformation
	 * loading, subsequent calls to this method will return the existing
	 * diagnostic.
	 * 
	 * @since 3.4
	 * @param the monitor indicating load progress and cancellation
	 * @return the diagnostic indicating possible problems of the load action
	 */
	public Diagnostic loadTransformation(IProgressMonitor monitor) {
		return fExector.loadTransformation(monitor);
	}

	/**
	 * Executes the transformation referred by this executor using the given
	 * model parameters and execution context.
	 * 
	 * @param executionContext
	 *            the context object keeping the execution environment details
	 * @param modelParameters
	 *            the actual model arguments to the transformation
	 * 
	 * @return the diagnostic object indicating the execution result status,
	 *         also keeping the details of possible problems
	 * @throws IllegalArgumentException
	 *             if the context or any of the model parameters is
	 *             <code>null</code>
	 */
	public ExecutionDiagnostic execute(ExecutionContext executionContext,
			ModelExtent... modelParameters) {
		ExecutionDiagnostic executionDiagnostic = fExector.execute(executionContext, modelParameters);
		
		Trace trace = executionContext.getSessionData().getValue(QVTEvaluationOptions.INCREMENTAL_UPDATE_TRACE);
		if (trace != null) {
			trace.setTraceContent(Collections.singletonList(fExector.getTraces()));
		}
		
		return executionDiagnostic;
	}
	
	/**
	 * Clean all resources associated with the executor.
	 * 
	 * @since 3.5
	 */
	public void cleanup() {
		fExector.cleanup();
	}
}
