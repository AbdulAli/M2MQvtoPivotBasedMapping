/*******************************************************************************
 * Copyright (c) 2009, 2015 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *     Christopher Gerking - bug 431082
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.m2m.internal.qvt.oml.InternalTransformationExecutor.TracesAwareExecutor;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnvFactory;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.ModelContent;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTEvaluationOptions;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;
import org.eclipse.m2m.qvt.oml.ExecutionContext;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;

public class TransformationRunner  {

	private final URI fTransformationURI;	
	private final TracesAwareExecutor fExecutor;
	private final List<URI> fModelParamURIs;
	private URI fTraceFileURI;
	private boolean fIsSaveTrace;
	private boolean fIsIncrementalUpdate;
	
	private BasicDiagnostic fDiagnostic;
	private List<ModelExtent> fModelParams;		
	private ModelExtentHelper fExtentHelper;
	private org.eclipse.m2m.qvt.oml.util.Trace fIncrementalTrace;
	
	
	public TransformationRunner(URI transformationURI, 
			EPackage.Registry packageRegistry,
			List<URI> modelParamURIs) {
		
		if (transformationURI == null || modelParamURIs == null
				|| modelParamURIs.contains(null)) {
			throw new IllegalArgumentException();
		}

		fExecutor = packageRegistry == null ? new TracesAwareExecutor(transformationURI) : 
			new TracesAwareExecutor(transformationURI, packageRegistry);
		fTransformationURI = transformationURI;
		fModelParamURIs = modelParamURIs;
	}
	
	protected InternalTransformationExecutor getExecutor() {
		return fExecutor;
	};
	
	public URI getTransformationURI() {
		return fTransformationURI;
	}
	
	public void setTraceFile(URI traceFileURI) {
		fTraceFileURI = traceFileURI;
	}
	
	public void setSaveTrace(boolean isSaveTrace) {
		fIsSaveTrace = isSaveTrace;
	}

	public void setIncrementalUpdate(boolean isIncrementalUpdate) {
		fIsIncrementalUpdate = isIncrementalUpdate;
	}

	public Diagnostic initialize() {
		if(fDiagnostic != null) {
			return fDiagnostic;
		}
		
		fDiagnostic = QvtPlugin.createDiagnostic("Transformation runner initialize"); //$NON-NLS-1$
		
		Diagnostic loadDiagnostic = fExecutor.loadTransformation(new NullProgressMonitor());
		if(!QvtPlugin.isSuccess(loadDiagnostic)) {
			fDiagnostic.add(loadDiagnostic);
		}

		handleLoadTransformation(loadDiagnostic);
		
		OperationalTransformation transformation = fExecutor.getTransformation();
		if(transformation == null) {
			return fDiagnostic;
		}
		
		// Note: initialized here already loaded transformation is required
		fExtentHelper = new ModelExtentHelper(transformation, fModelParamURIs, fExecutor.getResourceSet());
		
		Diagnostic extentsDiagnostic = Diagnostic.OK_INSTANCE; 
		try {
			fModelParams = fExtentHelper.loadExtents();
		} catch (DiagnosticException e) {
			extentsDiagnostic = e.getDiagnostic();
		}
		
		handleLoadExtents(extentsDiagnostic);
		if(!QvtPlugin.isSuccess(extentsDiagnostic)) {
			fDiagnostic.add(extentsDiagnostic);
		}
		
		fIncrementalTrace = null;
		if (fIsIncrementalUpdate) {
			Diagnostic traceDiagnostic = Diagnostic.OK_INSTANCE; 

			ModelContent traceContent = EmfUtil.safeLoadModel(fTraceFileURI, fExecutor.getResourceSet());
			if (traceContent != null) {
				fIncrementalTrace = new org.eclipse.m2m.qvt.oml.util.Trace(traceContent.getContent());
			}
			else {
				traceDiagnostic = QvtPlugin.createWarnDiagnostic(
						NLS.bind(Messages.FailToLoadTraceForIncrementalUpdateExecution, fTraceFileURI));
			}
			
			handleLoadTrace(traceDiagnostic);
			if(!QvtPlugin.isSuccess(traceDiagnostic)) {
				fDiagnostic.add(traceDiagnostic);
			}
		}
		
		// FIXME - 
		// add validation for configuration properties and param count
		// into the internal executor

		// TODO - collect WARN, INFO diagnostics?
		return fDiagnostic;
	}
	
	protected QvtOperationalEnvFactory getEnvFactory() {
		return new QvtOperationalEnvFactory(); 
	}
	
	protected void handleLoadTransformation(Diagnostic diagnostic) {
		// do nothing
	}	
			
	protected void handleLoadExtents(Diagnostic diagnostic) {
		// do nothing
	}	

	protected void handleLoadTrace(Diagnostic diagnostic) {
		// do nothing
	}	

	protected void handleExecution(ExecutionDiagnostic execDiagnostic) {
		// do nothing
	}
	
	protected void handleSaveExtents(Diagnostic diagnostic) {
		// do nothing
	}		


	public Diagnostic execute(ExecutionContext context) {
		Diagnostic diagnostic = initialize();
		
		if(!isSuccess(diagnostic)) {
			return diagnostic;
		}

		fExecutor.setEnvironmentFactory(getEnvFactory());
		try {			
			ModelExtent[] params = fModelParams.toArray(new ModelExtent[fModelParams.size()]);
			
			if (fIncrementalTrace != null) {
				context.getSessionData().setValue(QVTEvaluationOptions.INCREMENTAL_UPDATE_TRACE, fIncrementalTrace);
			}
			ExecutionDiagnostic execDiagnostic = fExecutor.execute(context, params);
			handleExecution(execDiagnostic);
			
			if(!isSuccess(execDiagnostic)) {
				// skip saving any output
				return execDiagnostic;
			}

			// can continue and save output
			Diagnostic saveExtentsDiagnostic = fExtentHelper.saveExtents();
			handleSaveExtents(saveExtentsDiagnostic);
			
			if(!isSuccess(saveExtentsDiagnostic)) {
				return saveExtentsDiagnostic;
			}

			Diagnostic saveTracesDiagnostic = saveTraces(fExecutor.getTraces());
			if(!isSuccess(saveTracesDiagnostic)) {
				return saveTracesDiagnostic;
			}

			return execDiagnostic;
		} finally {
			fExecutor.cleanup();
		}			
	}
	
	private Diagnostic saveTraces(Trace trace) { 
		if(fTraceFileURI != null && fIsSaveTrace) {
			Resource resource = new ResourceSetImpl().createResource(fTraceFileURI);
			resource.getContents().add(trace);
			try {
		        Map<String, String> options = new HashMap<String, String>();
		        options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
				resource.save(options);
			} catch (IOException e) {
				String message = NLS.bind("Failed to save trace model uri={0}", fTraceFileURI);
				return new BasicDiagnostic(Diagnostic.ERROR, QvtPlugin.ID, 0,
						message, new Object[] { e });
			}
		}
		
		return Diagnostic.OK_INSTANCE;
	}
	
	/**
	 * Successfully finished execution, no errors and user interruption 
	 */
	private static boolean isSuccess(Diagnostic diagnostic) {
		int severity = diagnostic.getSeverity();
		return severity != Diagnostic.ERROR && severity != Diagnostic.CANCEL;
	}
		
}