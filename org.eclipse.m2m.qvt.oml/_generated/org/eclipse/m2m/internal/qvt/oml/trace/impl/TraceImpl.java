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
 * $Id: TraceImpl.java,v 1.4 2009/05/15 12:07:10 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;
import org.eclipse.m2m.internal.qvt.oml.trace.TracePackage;
import org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl#getTraceRecords <em>Trace Records</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl#getTraceRecordMap <em>Trace Record Map</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl#getSourceToTraceRecordMap <em>Source To Trace Record Map</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceImpl#getTargetToTraceRecordMap <em>Target To Trace Record Map</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TraceImpl extends EObjectImpl implements Trace {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public static final String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The cached value of the '{@link #getTraceRecords() <em>Trace Records</em>}' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getTraceRecords()
	 * @generated
	 * @ordered
	 */
    protected EList<TraceRecord> traceRecords;

    /**
	 * The cached value of the '{@link #getTraceRecordMap() <em>Trace Record Map</em>}' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getTraceRecordMap()
	 * @generated
	 * @ordered
	 */
    protected EMap<MappingOperation, EList<TraceRecord>> traceRecordMap;

    /**
	 * The cached value of the '{@link #getSourceToTraceRecordMap() <em>Source To Trace Record Map</em>}' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getSourceToTraceRecordMap()
	 * @generated
	 * @ordered
	 */
    protected EMap<Object, EList<TraceRecord>> sourceToTraceRecordMap;

    /**
	 * The cached value of the '{@link #getTargetToTraceRecordMap() <em>Target To Trace Record Map</em>}' map.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getTargetToTraceRecordMap()
	 * @generated
	 * @ordered
	 */
    protected EMap<Object, EList<TraceRecord>> targetToTraceRecordMap;


    /**
	 * @generated NOT
	 */
    private final Map<MappingOperation, Map<Object, TraceRecord>> mapping2Records = new HashMap<MappingOperation, Map<Object, TraceRecord>>();    
    
    
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected TraceImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return TracePackage.Literals.TRACE;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EList<TraceRecord> getTraceRecords() {
		if (traceRecords == null) {
			traceRecords = new EObjectContainmentEList<TraceRecord>(TraceRecord.class, this, TracePackage.TRACE__TRACE_RECORDS);
		}
		return traceRecords;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMap<MappingOperation, EList<TraceRecord>> getTraceRecordMap() {
		if (traceRecordMap == null) {
			traceRecordMap = new EcoreEMap<MappingOperation,EList<TraceRecord>>(TracePackage.Literals.MAPPING_OPERATION_TO_TRACE_RECORD_MAP_ENTRY, MappingOperationToTraceRecordMapEntryImpl.class, this, TracePackage.TRACE__TRACE_RECORD_MAP);
		}
		return traceRecordMap;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMap<Object, EList<TraceRecord>> getSourceToTraceRecordMap() {
		if (sourceToTraceRecordMap == null) {
			sourceToTraceRecordMap = new EcoreEMap<Object,EList<TraceRecord>>(TracePackage.Literals.OBJECT_TO_TRACE_RECORD_MAP_ENTRY, ObjectToTraceRecordMapEntryImpl.class, this, TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP);
		}
		return sourceToTraceRecordMap;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMap<Object, EList<TraceRecord>> getTargetToTraceRecordMap() {
		if (targetToTraceRecordMap == null) {
			targetToTraceRecordMap = new EcoreEMap<Object,EList<TraceRecord>>(TracePackage.Literals.OBJECT_TO_TRACE_RECORD_MAP_ENTRY, ObjectToTraceRecordMapEntryImpl.class, this, TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP);
		}
		return targetToTraceRecordMap;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TracePackage.TRACE__TRACE_RECORDS:
				return ((InternalEList<?>)getTraceRecords()).basicRemove(otherEnd, msgs);
			case TracePackage.TRACE__TRACE_RECORD_MAP:
				return ((InternalEList<?>)getTraceRecordMap()).basicRemove(otherEnd, msgs);
			case TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP:
				return ((InternalEList<?>)getSourceToTraceRecordMap()).basicRemove(otherEnd, msgs);
			case TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP:
				return ((InternalEList<?>)getTargetToTraceRecordMap()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TracePackage.TRACE__TRACE_RECORDS:
				return getTraceRecords();
			case TracePackage.TRACE__TRACE_RECORD_MAP:
				if (coreType) return getTraceRecordMap();
				else return getTraceRecordMap().map();
			case TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP:
				if (coreType) return getSourceToTraceRecordMap();
				else return getSourceToTraceRecordMap().map();
			case TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP:
				if (coreType) return getTargetToTraceRecordMap();
				else return getTargetToTraceRecordMap().map();
		}
		return super.eGet(featureID, resolve, coreType);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TracePackage.TRACE__TRACE_RECORDS:
				getTraceRecords().clear();
				getTraceRecords().addAll((Collection<? extends TraceRecord>)newValue);
				return;
			case TracePackage.TRACE__TRACE_RECORD_MAP:
				((EStructuralFeature.Setting)getTraceRecordMap()).set(newValue);
				return;
			case TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP:
				((EStructuralFeature.Setting)getSourceToTraceRecordMap()).set(newValue);
				return;
			case TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP:
				((EStructuralFeature.Setting)getTargetToTraceRecordMap()).set(newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public void eUnset(int featureID) {
		switch (featureID) {
			case TracePackage.TRACE__TRACE_RECORDS:
				getTraceRecords().clear();
				return;
			case TracePackage.TRACE__TRACE_RECORD_MAP:
				getTraceRecordMap().clear();
				return;
			case TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP:
				getSourceToTraceRecordMap().clear();
				return;
			case TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP:
				getTargetToTraceRecordMap().clear();
				return;
		}
		super.eUnset(featureID);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public boolean eIsSet(int featureID) {
		switch (featureID) {
			case TracePackage.TRACE__TRACE_RECORDS:
				return traceRecords != null && !traceRecords.isEmpty();
			case TracePackage.TRACE__TRACE_RECORD_MAP:
				return traceRecordMap != null && !traceRecordMap.isEmpty();
			case TracePackage.TRACE__SOURCE_TO_TRACE_RECORD_MAP:
				return sourceToTraceRecordMap != null && !sourceToTraceRecordMap.isEmpty();
			case TracePackage.TRACE__TARGET_TO_TRACE_RECORD_MAP:
				return targetToTraceRecordMap != null && !targetToTraceRecordMap.isEmpty();
		}
		return super.eIsSet(featureID);
	}
    
    /**
	 * @generated NOT
	 */    
    public boolean hasRecordsBySource() {
		return !mapping2Records.isEmpty();
    }

    /**
	 * @generated NOT
	 */    
    public void  clearRecordsBySource() {
		mapping2Records.clear();
    }

    /**
	 * @generated NOT
	 */    
    public TraceRecord getRecordBySource(MappingOperation mapping, Object contextSource) {
		Map<Object, TraceRecord> records = mapping2Records.get(mapping);
		if(records != null) {
			return records.get(contextSource);
		}
		
		return null;
    }

    /**
	 * @generated NOT
	 */
	public void addRecordBySource(Object sourceObject, MappingOperation mapping, TraceRecord traceRecord) {
		Map<Object, TraceRecord> records = mapping2Records.get(mapping);
		if(records == null) {								
			records = new HashMap<Object, TraceRecord>();
			mapping2Records.put(mapping, records);
		}
		
		records.put(sourceObject, traceRecord);
	}    
    
} //TraceImpl
