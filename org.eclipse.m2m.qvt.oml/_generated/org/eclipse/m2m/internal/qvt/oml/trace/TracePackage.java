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
 * $Id: TracePackage.java,v 1.3 2009/05/15 12:01:11 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceFactory
 * @model kind="package"
 * @generated
 */
public interface TracePackage extends EPackage {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The package name.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String eNAME = "trace"; //$NON-NLS-1$

    /**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String eNS_URI = "http:///www.eclipse.org/m2m/qvt/operational/trace.ecore"; //$NON-NLS-1$

    /**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String eNS_PREFIX = "trace"; //$NON-NLS-1$

    /**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    TracePackage eINSTANCE = org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl.init();

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl <em>Trace</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getTrace()
	 * @generated
	 */
    int TRACE = 0;

    /**
	 * The feature id for the '<em><b>Trace Records</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE__TRACE_RECORDS = 0;

    /**
	 * The feature id for the '<em><b>Trace Record Map</b></em>' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE__TRACE_RECORD_MAP = 1;

    /**
	 * The feature id for the '<em><b>Source To Trace Record Map</b></em>' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE__SOURCE_TO_TRACE_RECORD_MAP = 2;

    /**
	 * The feature id for the '<em><b>Target To Trace Record Map</b></em>' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE__TARGET_TO_TRACE_RECORD_MAP = 3;

    /**
	 * The number of structural features of the '<em>Trace</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_FEATURE_COUNT = 4;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl <em>Record</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getTraceRecord()
	 * @generated
	 */
    int TRACE_RECORD = 1;

    /**
	 * The feature id for the '<em><b>Mapping Operation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_RECORD__MAPPING_OPERATION = 0;

    /**
	 * The feature id for the '<em><b>Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_RECORD__CONTEXT = 1;

    /**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_RECORD__PARAMETERS = 2;

    /**
	 * The feature id for the '<em><b>Result</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_RECORD__RESULT = 3;

    /**
	 * The number of structural features of the '<em>Record</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int TRACE_RECORD_FEATURE_COUNT = 4;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.VarParameterValueImpl <em>Var Parameter Value</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.VarParameterValueImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getVarParameterValue()
	 * @generated
	 */
    int VAR_PARAMETER_VALUE = 2;

    /**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int VAR_PARAMETER_VALUE__KIND = 0;

    /**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int VAR_PARAMETER_VALUE__NAME = 1;

    /**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int VAR_PARAMETER_VALUE__TYPE = 2;

    /**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int VAR_PARAMETER_VALUE__VALUE = 3;

    /**
	 * The number of structural features of the '<em>Var Parameter Value</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int VAR_PARAMETER_VALUE_FEATURE_COUNT = 4;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.MappingOperationToTraceRecordMapEntryImpl <em>Mapping Operation To Trace Record Map Entry</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.MappingOperationToTraceRecordMapEntryImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getMappingOperationToTraceRecordMapEntry()
	 * @generated
	 */
    int MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY = 3;

    /**
	 * The feature id for the '<em><b>Value</b></em>' reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY__VALUE = 0;

    /**
	 * The feature id for the '<em><b>Key</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY__KEY = 1;

    /**
	 * The number of structural features of the '<em>Mapping Operation To Trace Record Map Entry</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY_FEATURE_COUNT = 2;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl <em>EMapping Operation</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingOperation()
	 * @generated
	 */
    int EMAPPING_OPERATION = 4;

    /**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_OPERATION__NAME = 0;

    /**
	 * The feature id for the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_OPERATION__PACKAGE = 1;

    /**
	 * The feature id for the '<em><b>Module</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_OPERATION__MODULE = 2;

    /**
	 * The feature id for the '<em><b>Runtime Mapping Operation</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION = 3;

    /**
	 * The number of structural features of the '<em>EMapping Operation</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_OPERATION_FEATURE_COUNT = 4;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl <em>EValue</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEValue()
	 * @generated
	 */
    int EVALUE = 5;

    /**
	 * The feature id for the '<em><b>Primitive Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__PRIMITIVE_VALUE = 0;

    /**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__MODEL_ELEMENT = 1;

    /**
	 * The feature id for the '<em><b>Ocl Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__OCL_OBJECT = 2;

    /**
	 * The feature id for the '<em><b>Intermediate Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__INTERMEDIATE_ELEMENT = 3;

    /**
	 * The feature id for the '<em><b>Collection Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__COLLECTION_TYPE = 4;

    /**
	 * The feature id for the '<em><b>Collection</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE__COLLECTION = 5;

    /**
	 * The number of structural features of the '<em>EValue</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EVALUE_FEATURE_COUNT = 6;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.ETuplePartValueImpl <em>ETuple Part Value</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.ETuplePartValueImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getETuplePartValue()
	 * @generated
	 */
    int ETUPLE_PART_VALUE = 6;

    /**
	 * The feature id for the '<em><b>Primitive Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__PRIMITIVE_VALUE = EVALUE__PRIMITIVE_VALUE;

    /**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__MODEL_ELEMENT = EVALUE__MODEL_ELEMENT;

    /**
	 * The feature id for the '<em><b>Ocl Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__OCL_OBJECT = EVALUE__OCL_OBJECT;

    /**
	 * The feature id for the '<em><b>Intermediate Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__INTERMEDIATE_ELEMENT = EVALUE__INTERMEDIATE_ELEMENT;

    /**
	 * The feature id for the '<em><b>Collection Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__COLLECTION_TYPE = EVALUE__COLLECTION_TYPE;

    /**
	 * The feature id for the '<em><b>Collection</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__COLLECTION = EVALUE__COLLECTION;

    /**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__NAME = EVALUE_FEATURE_COUNT + 0;

    /**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE__VALUE = EVALUE_FEATURE_COUNT + 1;

    /**
	 * The number of structural features of the '<em>ETuple Part Value</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ETUPLE_PART_VALUE_FEATURE_COUNT = EVALUE_FEATURE_COUNT + 2;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingContextImpl <em>EMapping Context</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingContextImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingContext()
	 * @generated
	 */
    int EMAPPING_CONTEXT = 7;

    /**
	 * The feature id for the '<em><b>Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_CONTEXT__CONTEXT = 0;

    /**
	 * The number of structural features of the '<em>EMapping Context</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_CONTEXT_FEATURE_COUNT = 1;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingParametersImpl <em>EMapping Parameters</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingParametersImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingParameters()
	 * @generated
	 */
    int EMAPPING_PARAMETERS = 8;

    /**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_PARAMETERS__PARAMETERS = 0;

    /**
	 * The number of structural features of the '<em>EMapping Parameters</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_PARAMETERS_FEATURE_COUNT = 1;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingResultsImpl <em>EMapping Results</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingResultsImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingResults()
	 * @generated
	 */
    int EMAPPING_RESULTS = 9;

    /**
	 * The feature id for the '<em><b>Result</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_RESULTS__RESULT = 0;

    /**
	 * The number of structural features of the '<em>EMapping Results</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int EMAPPING_RESULTS_FEATURE_COUNT = 1;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.ObjectToTraceRecordMapEntryImpl <em>Object To Trace Record Map Entry</em>}' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.ObjectToTraceRecordMapEntryImpl
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getObjectToTraceRecordMapEntry()
	 * @generated
	 */
    int OBJECT_TO_TRACE_RECORD_MAP_ENTRY = 10;

    /**
	 * The feature id for the '<em><b>Value</b></em>' reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int OBJECT_TO_TRACE_RECORD_MAP_ENTRY__VALUE = 0;

    /**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int OBJECT_TO_TRACE_RECORD_MAP_ENTRY__KEY = 1;

    /**
	 * The number of structural features of the '<em>Object To Trace Record Map Entry</em>' class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int OBJECT_TO_TRACE_RECORD_MAP_ENTRY_FEATURE_COUNT = 2;

    /**
	 * The meta object id for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind <em>EDirection Kind</em>}' enum.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEDirectionKind()
	 * @generated
	 */
    int EDIRECTION_KIND = 11;


    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace <em>Trace</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Trace</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.Trace
	 * @generated
	 */
    EClass getTrace();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecords <em>Trace Records</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Trace Records</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecords()
	 * @see #getTrace()
	 * @generated
	 */
    EReference getTrace_TraceRecords();

    /**
	 * Returns the meta object for the map '{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecordMap <em>Trace Record Map</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Trace Record Map</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTraceRecordMap()
	 * @see #getTrace()
	 * @generated
	 */
    EReference getTrace_TraceRecordMap();

    /**
	 * Returns the meta object for the map '{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getSourceToTraceRecordMap <em>Source To Trace Record Map</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Source To Trace Record Map</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.Trace#getSourceToTraceRecordMap()
	 * @see #getTrace()
	 * @generated
	 */
    EReference getTrace_SourceToTraceRecordMap();

    /**
	 * Returns the meta object for the map '{@link org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTargetToTraceRecordMap <em>Target To Trace Record Map</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Target To Trace Record Map</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.Trace#getTargetToTraceRecordMap()
	 * @see #getTrace()
	 * @generated
	 */
    EReference getTrace_TargetToTraceRecordMap();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord <em>Record</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Record</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord
	 * @generated
	 */
    EClass getTraceRecord();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getMappingOperation <em>Mapping Operation</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Mapping Operation</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getMappingOperation()
	 * @see #getTraceRecord()
	 * @generated
	 */
    EReference getTraceRecord_MappingOperation();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Context</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getContext()
	 * @see #getTraceRecord()
	 * @generated
	 */
    EReference getTraceRecord_Context();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameters</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getParameters()
	 * @see #getTraceRecord()
	 * @generated
	 */
    EReference getTraceRecord_Parameters();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Result</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord#getResult()
	 * @see #getTraceRecord()
	 * @generated
	 */
    EReference getTraceRecord_Result();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue <em>Var Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Var Parameter Value</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue
	 * @generated
	 */
    EClass getVarParameterValue();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getKind()
	 * @see #getVarParameterValue()
	 * @generated
	 */
    EAttribute getVarParameterValue_Kind();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getName()
	 * @see #getVarParameterValue()
	 * @generated
	 */
    EAttribute getVarParameterValue_Name();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getType()
	 * @see #getVarParameterValue()
	 * @generated
	 */
    EAttribute getVarParameterValue_Type();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue#getValue()
	 * @see #getVarParameterValue()
	 * @generated
	 */
    EReference getVarParameterValue_Value();

    /**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Mapping Operation To Trace Record Map Entry</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapping Operation To Trace Record Map Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model features="value key" 
	 *        valueType="org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord" valueMany="true"
	 *        keyType="org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation" keyRequired="true"
	 * @generated
	 */
    EClass getMappingOperationToTraceRecordMapEntry();

    /**
	 * Returns the meta object for the reference list '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getMappingOperationToTraceRecordMapEntry()
	 * @generated
	 */
    EReference getMappingOperationToTraceRecordMapEntry_Value();

    /**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getMappingOperationToTraceRecordMapEntry()
	 * @generated
	 */
    EReference getMappingOperationToTraceRecordMapEntry_Key();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation <em>EMapping Operation</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMapping Operation</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation
	 * @generated
	 */
    EClass getEMappingOperation();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getName()
	 * @see #getEMappingOperation()
	 * @generated
	 */
    EAttribute getEMappingOperation_Name();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getPackage <em>Package</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getPackage()
	 * @see #getEMappingOperation()
	 * @generated
	 */
    EAttribute getEMappingOperation_Package();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getModule <em>Module</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Module</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getModule()
	 * @see #getEMappingOperation()
	 * @generated
	 */
    EAttribute getEMappingOperation_Module();

    /**
	 * Returns the meta object for the reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getRuntimeMappingOperation <em>Runtime Mapping Operation</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Runtime Mapping Operation</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getRuntimeMappingOperation()
	 * @see #getEMappingOperation()
	 * @generated
	 */
    EReference getEMappingOperation_RuntimeMappingOperation();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue <em>EValue</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EValue</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue
	 * @generated
	 */
    EClass getEValue();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getPrimitiveValue <em>Primitive Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Primitive Value</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getPrimitiveValue()
	 * @see #getEValue()
	 * @generated
	 */
    EAttribute getEValue_PrimitiveValue();

    /**
	 * Returns the meta object for the reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getModelElement <em>Model Element</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Model Element</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getModelElement()
	 * @see #getEValue()
	 * @generated
	 */
    EReference getEValue_ModelElement();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getOclObject <em>Ocl Object</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ocl Object</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getOclObject()
	 * @see #getEValue()
	 * @generated
	 */
    EAttribute getEValue_OclObject();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getIntermediateElement <em>Intermediate Element</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Intermediate Element</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getIntermediateElement()
	 * @see #getEValue()
	 * @generated
	 */
    EReference getEValue_IntermediateElement();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollectionType <em>Collection Type</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Collection Type</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollectionType()
	 * @see #getEValue()
	 * @generated
	 */
    EAttribute getEValue_CollectionType();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollection <em>Collection</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Collection</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollection()
	 * @see #getEValue()
	 * @generated
	 */
    EReference getEValue_Collection();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue <em>ETuple Part Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ETuple Part Value</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue
	 * @generated
	 */
    EClass getETuplePartValue();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue#getName()
	 * @see #getETuplePartValue()
	 * @generated
	 */
    EAttribute getETuplePartValue_Name();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.ETuplePartValue#getValue()
	 * @see #getETuplePartValue()
	 * @generated
	 */
    EReference getETuplePartValue_Value();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext <em>EMapping Context</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMapping Context</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext
	 * @generated
	 */
    EClass getEMappingContext();

    /**
	 * Returns the meta object for the containment reference '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Context</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext#getContext()
	 * @see #getEMappingContext()
	 * @generated
	 */
    EReference getEMappingContext_Context();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters <em>EMapping Parameters</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMapping Parameters</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters
	 * @generated
	 */
    EClass getEMappingParameters();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters#getParameters()
	 * @see #getEMappingParameters()
	 * @generated
	 */
    EReference getEMappingParameters_Parameters();

    /**
	 * Returns the meta object for class '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults <em>EMapping Results</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMapping Results</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults
	 * @generated
	 */
    EClass getEMappingResults();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Result</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults#getResult()
	 * @see #getEMappingResults()
	 * @generated
	 */
    EReference getEMappingResults_Result();

    /**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Object To Trace Record Map Entry</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Object To Trace Record Map Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model features="value key" 
	 *        valueType="org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord" valueMany="true"
	 *        keyDataType="org.eclipse.emf.ecore.EJavaObject" keyRequired="true"
	 * @generated
	 */
    EClass getObjectToTraceRecordMapEntry();

    /**
	 * Returns the meta object for the reference list '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getObjectToTraceRecordMapEntry()
	 * @generated
	 */
    EReference getObjectToTraceRecordMapEntry_Value();

    /**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getObjectToTraceRecordMapEntry()
	 * @generated
	 */
    EAttribute getObjectToTraceRecordMapEntry_Key();

    /**
	 * Returns the meta object for enum '{@link org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind <em>EDirection Kind</em>}'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>EDirection Kind</em>'.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind
	 * @generated
	 */
    EEnum getEDirectionKind();

    /**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
    TraceFactory getTraceFactory();

    /**
	 * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
	 * @generated
	 */
    interface Literals {
        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl <em>Trace</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getTrace()
		 * @generated
		 */
        EClass TRACE = eINSTANCE.getTrace();

        /**
		 * The meta object literal for the '<em><b>Trace Records</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE__TRACE_RECORDS = eINSTANCE.getTrace_TraceRecords();

        /**
		 * The meta object literal for the '<em><b>Trace Record Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE__TRACE_RECORD_MAP = eINSTANCE.getTrace_TraceRecordMap();

        /**
		 * The meta object literal for the '<em><b>Source To Trace Record Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE__SOURCE_TO_TRACE_RECORD_MAP = eINSTANCE.getTrace_SourceToTraceRecordMap();

        /**
		 * The meta object literal for the '<em><b>Target To Trace Record Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE__TARGET_TO_TRACE_RECORD_MAP = eINSTANCE.getTrace_TargetToTraceRecordMap();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl <em>Record</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getTraceRecord()
		 * @generated
		 */
        EClass TRACE_RECORD = eINSTANCE.getTraceRecord();

        /**
		 * The meta object literal for the '<em><b>Mapping Operation</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE_RECORD__MAPPING_OPERATION = eINSTANCE.getTraceRecord_MappingOperation();

        /**
		 * The meta object literal for the '<em><b>Context</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE_RECORD__CONTEXT = eINSTANCE.getTraceRecord_Context();

        /**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE_RECORD__PARAMETERS = eINSTANCE.getTraceRecord_Parameters();

        /**
		 * The meta object literal for the '<em><b>Result</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference TRACE_RECORD__RESULT = eINSTANCE.getTraceRecord_Result();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.VarParameterValueImpl <em>Var Parameter Value</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.VarParameterValueImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getVarParameterValue()
		 * @generated
		 */
        EClass VAR_PARAMETER_VALUE = eINSTANCE.getVarParameterValue();

        /**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute VAR_PARAMETER_VALUE__KIND = eINSTANCE.getVarParameterValue_Kind();

        /**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute VAR_PARAMETER_VALUE__NAME = eINSTANCE.getVarParameterValue_Name();

        /**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute VAR_PARAMETER_VALUE__TYPE = eINSTANCE.getVarParameterValue_Type();

        /**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference VAR_PARAMETER_VALUE__VALUE = eINSTANCE.getVarParameterValue_Value();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.MappingOperationToTraceRecordMapEntryImpl <em>Mapping Operation To Trace Record Map Entry</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.MappingOperationToTraceRecordMapEntryImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getMappingOperationToTraceRecordMapEntry()
		 * @generated
		 */
        EClass MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY = eINSTANCE.getMappingOperationToTraceRecordMapEntry();

        /**
		 * The meta object literal for the '<em><b>Value</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY__VALUE = eINSTANCE.getMappingOperationToTraceRecordMapEntry_Value();

        /**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY__KEY = eINSTANCE.getMappingOperationToTraceRecordMapEntry_Key();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl <em>EMapping Operation</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingOperation()
		 * @generated
		 */
        EClass EMAPPING_OPERATION = eINSTANCE.getEMappingOperation();

        /**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EMAPPING_OPERATION__NAME = eINSTANCE.getEMappingOperation_Name();

        /**
		 * The meta object literal for the '<em><b>Package</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EMAPPING_OPERATION__PACKAGE = eINSTANCE.getEMappingOperation_Package();

        /**
		 * The meta object literal for the '<em><b>Module</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EMAPPING_OPERATION__MODULE = eINSTANCE.getEMappingOperation_Module();

        /**
		 * The meta object literal for the '<em><b>Runtime Mapping Operation</b></em>' reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION = eINSTANCE.getEMappingOperation_RuntimeMappingOperation();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl <em>EValue</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEValue()
		 * @generated
		 */
        EClass EVALUE = eINSTANCE.getEValue();

        /**
		 * The meta object literal for the '<em><b>Primitive Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EVALUE__PRIMITIVE_VALUE = eINSTANCE.getEValue_PrimitiveValue();

        /**
		 * The meta object literal for the '<em><b>Model Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EVALUE__MODEL_ELEMENT = eINSTANCE.getEValue_ModelElement();

        /**
		 * The meta object literal for the '<em><b>Ocl Object</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EVALUE__OCL_OBJECT = eINSTANCE.getEValue_OclObject();

        /**
		 * The meta object literal for the '<em><b>Intermediate Element</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EVALUE__INTERMEDIATE_ELEMENT = eINSTANCE.getEValue_IntermediateElement();

        /**
		 * The meta object literal for the '<em><b>Collection Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute EVALUE__COLLECTION_TYPE = eINSTANCE.getEValue_CollectionType();

        /**
		 * The meta object literal for the '<em><b>Collection</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EVALUE__COLLECTION = eINSTANCE.getEValue_Collection();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.ETuplePartValueImpl <em>ETuple Part Value</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.ETuplePartValueImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getETuplePartValue()
		 * @generated
		 */
        EClass ETUPLE_PART_VALUE = eINSTANCE.getETuplePartValue();

        /**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ETUPLE_PART_VALUE__NAME = eINSTANCE.getETuplePartValue_Name();

        /**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference ETUPLE_PART_VALUE__VALUE = eINSTANCE.getETuplePartValue_Value();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingContextImpl <em>EMapping Context</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingContextImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingContext()
		 * @generated
		 */
        EClass EMAPPING_CONTEXT = eINSTANCE.getEMappingContext();

        /**
		 * The meta object literal for the '<em><b>Context</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EMAPPING_CONTEXT__CONTEXT = eINSTANCE.getEMappingContext_Context();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingParametersImpl <em>EMapping Parameters</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingParametersImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingParameters()
		 * @generated
		 */
        EClass EMAPPING_PARAMETERS = eINSTANCE.getEMappingParameters();

        /**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EMAPPING_PARAMETERS__PARAMETERS = eINSTANCE.getEMappingParameters_Parameters();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingResultsImpl <em>EMapping Results</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingResultsImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEMappingResults()
		 * @generated
		 */
        EClass EMAPPING_RESULTS = eINSTANCE.getEMappingResults();

        /**
		 * The meta object literal for the '<em><b>Result</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference EMAPPING_RESULTS__RESULT = eINSTANCE.getEMappingResults_Result();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.ObjectToTraceRecordMapEntryImpl <em>Object To Trace Record Map Entry</em>}' class.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.ObjectToTraceRecordMapEntryImpl
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getObjectToTraceRecordMapEntry()
		 * @generated
		 */
        EClass OBJECT_TO_TRACE_RECORD_MAP_ENTRY = eINSTANCE.getObjectToTraceRecordMapEntry();

        /**
		 * The meta object literal for the '<em><b>Value</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EReference OBJECT_TO_TRACE_RECORD_MAP_ENTRY__VALUE = eINSTANCE.getObjectToTraceRecordMapEntry_Value();

        /**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute OBJECT_TO_TRACE_RECORD_MAP_ENTRY__KEY = eINSTANCE.getObjectToTraceRecordMapEntry_Key();

        /**
		 * The meta object literal for the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind <em>EDirection Kind</em>}' enum.
		 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.EDirectionKind
		 * @see org.eclipse.m2m.internal.qvt.oml.trace.impl.TracePackageImpl#getEDirectionKind()
		 * @generated
		 */
        EEnum EDIRECTION_KIND = eINSTANCE.getEDirectionKind();

    }

} //TracePackage
