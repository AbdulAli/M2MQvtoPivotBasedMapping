/**
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *
 * $Id: Trace.java,v 1.4 2009/05/15 12:02:27 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecords <em>Trace Records</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecordMap <em>Trace Record Map</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getSourceToTraceRecordMap <em>Source To Trace Record Map</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTargetToTraceRecordMap <em>Target To Trace Record Map</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTrace()
 * @model
 * @generated
 */
public interface Trace extends EObject {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * Returns the value of the '<em><b>Trace Records</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Trace Records</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Trace Records</em>' containment reference list.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTrace_TraceRecords()
	 * @model containment="true"
	 * @generated
	 */
    EList<TraceRecord> getTraceRecords();

    /**
	 * Returns the value of the '<em><b>Trace Record Map</b></em>' map.
	 * The key is of type {@link org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation},
	 * and the value is of type list of {@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord},
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Trace Record Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Trace Record Map</em>' map.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTrace_TraceRecordMap()
	 * @model mapType="org.eclipse.m2m.internal.qvt.oml.trace.MappingOperationToTraceRecordMapEntry<org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation, org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord>" transient="true" ordered="false"
	 * @generated
	 */
    EMap<MappingOperation, EList<TraceRecord>> getTraceRecordMap();

    /**
	 * Returns the value of the '<em><b>Source To Trace Record Map</b></em>' map.
	 * The key is of type {@link java.lang.Object},
	 * and the value is of type list of {@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord},
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source To Trace Record Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Source To Trace Record Map</em>' map.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTrace_SourceToTraceRecordMap()
	 * @model mapType="org.eclipse.m2m.internal.qvt.oml.trace.ObjectToTraceRecordMapEntry<org.eclipse.emf.ecore.EJavaObject, org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord>" transient="true" ordered="false"
	 * @generated
	 */
    EMap<Object, EList<TraceRecord>> getSourceToTraceRecordMap();

    /**
	 * Returns the value of the '<em><b>Target To Trace Record Map</b></em>' map.
	 * The key is of type {@link java.lang.Object},
	 * and the value is of type list of {@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord},
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target To Trace Record Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Target To Trace Record Map</em>' map.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getTrace_TargetToTraceRecordMap()
	 * @model mapType="org.eclipse.m2m.internal.qvt.oml.trace.ObjectToTraceRecordMapEntry<org.eclipse.emf.ecore.EJavaObject, org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord>" transient="true" ordered="false"
	 * @generated
	 */
    EMap<Object, EList<TraceRecord>> getTargetToTraceRecordMap();

    /**
	 * @generated NOT
	 */
    public boolean hasRecordsBySource();
    
    /**
	 * @generated NOT
	 */
    public void clearRecordsBySource();
    
    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	TraceRecord getRecordBySource(MappingOperation mapping, Object sourceObject);

				/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void addRecordBySource(Object sourceObject, MappingOperation mapping, TraceRecord trace);
} // Trace
