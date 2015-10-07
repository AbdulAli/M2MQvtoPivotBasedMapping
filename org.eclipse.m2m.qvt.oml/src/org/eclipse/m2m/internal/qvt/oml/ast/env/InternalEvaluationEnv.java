/*******************************************************************************
 * Copyright (c) 2008,2013 Borland Software Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 392153
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTStackTraceElement;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ThisInstanceResolver;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance;
import org.eclipse.m2m.internal.qvt.oml.library.EObjectEStructuralFeaturePair;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;


/**
 * This interface separates the internals of QVT Evaluation Environments that 
 * are not supposed to exposed to usual clients. 
 */
public interface InternalEvaluationEnv {

	/**
	 * Retrieves the currently executed transformation, if one is available.
	 * 
	 * @return the transformation object or <code>null</code>, if the current
	 *         evaluation environment is not part of a transformation execution,
	 *         but for instance Imperative OCL. 
	 */
	TransformationInstance getCurrentTransformation();

	/**
	 * Retrieves the module in which the current evaluation occurs, if one is available.
	 * Note: The current module may be transformation or library
	 * 
	 * @return the module instance or may be null <code>null</code>, if the current
	 *         evaluation environment is not part of a transformation execution
	 *         and is to be executes outside the scope of any module, for instance 
	 *         Imperative OCL. 
	 */	
	ModuleInstance getCurrentModule();
		
	/**
	 * Sets the instruction pointer object, representing the current point of
	 * execution using this evaluation environment
	 * 
	 * @param currentIPObj
	 *            the current instruction pointer object
	 * @return the previously set object
	 * @see #getCurrentIP()
	 */
	EObject setCurrentIP(EObject currentIPObj);

	/**
	 * Gets the instruction pointer object, representing the current point of
	 * execution using this evaluation environment
	 * 
	 * @return the current IP object or <code>null</code> if no object has been
	 *         set the current instruction pointer object
	 * @see #setCurrentIP(EObject)
	 */
	EObject getCurrentIP();	
	
	/**
	 * Throws the given runtime exception, filling it with the current stack trace elements.
	 * Gets list of stack elements representing the current stack trace.
	 * @see #getStackTraceElements()
	 */
	void throwQVTException(QvtRuntimeException exception) throws QvtRuntimeException;

	/**
	 * Gets list of stack elements representing the current stack trace.
	 * @see #throwQVTException(QvtRuntimeException) 
	 */
	List<QVTStackTraceElement> getStackTraceElements();	
	
	Object getInvalid();
	    	        
	ThisInstanceResolver getThisResolver();

	void setThisResolver(ThisInstanceResolver myThisResolver);
	    
    void addDeferredTask(Runnable task);
    
    void processDeferredTasks();
    
    /**
     * Indicates whether this environment is used during deferred execution (late resolutions)
     */
	boolean isDeferredExecution();    
    
    EObjectEStructuralFeaturePair getLastAssignmentLvalueEval();
    
    void setLastAssignmentLvalueEval(EObjectEStructuralFeaturePair lvalue);
    
    ModelParameterExtent getUnboundExtent();
	
	/**
	 * Get traces for the transformation execution associated with this
	 * environment.
	 * 
	 * @return the trace object
	 */
	Trace getTraces();
	
	void setTraces(Trace trace);
}
