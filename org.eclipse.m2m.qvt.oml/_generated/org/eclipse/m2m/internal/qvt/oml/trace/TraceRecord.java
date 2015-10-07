/**
 * Copyright (c) 2007, 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *
 * $Id: TraceRecord.java,v 1.3 2009/05/15 12:02:28 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getMappingOperation <em>Mapping Operation</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTraceRecord()
 * @model
 * @generated
 */
public interface TraceRecord extends EObject {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * Returns the value of the '<em><b>Mapping Operation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Operation</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping Operation</em>' containment reference.
	 * @see #setMappingOperation(EMappingOperation)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTraceRecord_MappingOperation()
	 * @model containment="true" required="true"
	 * @generated
	 */
    EMappingOperation getMappingOperation();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getMappingOperation <em>Mapping Operation</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapping Operation</em>' containment reference.
	 * @see #getMappingOperation()
	 * @generated
	 */
    void setMappingOperation(EMappingOperation value);

    /**
	 * Returns the value of the '<em><b>Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Context</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Context</em>' containment reference.
	 * @see #setContext(EMappingContext)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTraceRecord_Context()
	 * @model containment="true" required="true"
	 * @generated
	 */
    EMappingContext getContext();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getContext <em>Context</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context</em>' containment reference.
	 * @see #getContext()
	 * @generated
	 */
    void setContext(EMappingContext value);

    /**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference.
	 * @see #setParameters(EMappingParameters)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTraceRecord_Parameters()
	 * @model containment="true" required="true"
	 * @generated
	 */
    EMappingParameters getParameters();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getParameters <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameters</em>' containment reference.
	 * @see #getParameters()
	 * @generated
	 */
    void setParameters(EMappingParameters value);

    /**
	 * Returns the value of the '<em><b>Result</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Result</em>' containment reference.
	 * @see #setResult(EMappingResults)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTraceRecord_Result()
	 * @model containment="true" required="true"
	 * @generated
	 */
    EMappingResults getResult();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getResult <em>Result</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result</em>' containment reference.
	 * @see #getResult()
	 * @generated
	 */
    void setResult(EMappingResults value);

} // TraceRecord
