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
 * $Id: TraceRecordImpl.java,v 1.3 2009/05/15 12:07:10 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingContext;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingParameters;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults;
import org.eclipse.m2m.internal.qvt.oml.trace.TracePackage;
import org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Record</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl#getMappingOperation <em>Mapping Operation</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceRecordImpl#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TraceRecordImpl extends EObjectImpl implements TraceRecord {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public static final String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The cached value of the '{@link #getMappingOperation() <em>Mapping Operation</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getMappingOperation()
	 * @generated
	 * @ordered
	 */
    protected EMappingOperation mappingOperation;

    /**
	 * The cached value of the '{@link #getContext() <em>Context</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getContext()
	 * @generated
	 * @ordered
	 */
    protected EMappingContext context;

    /**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
    protected EMappingParameters parameters;

    /**
	 * The cached value of the '{@link #getResult() <em>Result</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getResult()
	 * @generated
	 * @ordered
	 */
    protected EMappingResults result;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected TraceRecordImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return TracePackage.Literals.TRACE_RECORD;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMappingOperation getMappingOperation() {
		return mappingOperation;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain basicSetMappingOperation(EMappingOperation newMappingOperation, NotificationChain msgs) {
		EMappingOperation oldMappingOperation = mappingOperation;
		mappingOperation = newMappingOperation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__MAPPING_OPERATION, oldMappingOperation, newMappingOperation);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setMappingOperation(EMappingOperation newMappingOperation) {
		if (newMappingOperation != mappingOperation) {
			NotificationChain msgs = null;
			if (mappingOperation != null)
				msgs = ((InternalEObject)mappingOperation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__MAPPING_OPERATION, null, msgs);
			if (newMappingOperation != null)
				msgs = ((InternalEObject)newMappingOperation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__MAPPING_OPERATION, null, msgs);
			msgs = basicSetMappingOperation(newMappingOperation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__MAPPING_OPERATION, newMappingOperation, newMappingOperation));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMappingContext getContext() {
		return context;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain basicSetContext(EMappingContext newContext, NotificationChain msgs) {
		EMappingContext oldContext = context;
		context = newContext;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__CONTEXT, oldContext, newContext);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setContext(EMappingContext newContext) {
		if (newContext != context) {
			NotificationChain msgs = null;
			if (context != null)
				msgs = ((InternalEObject)context).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__CONTEXT, null, msgs);
			if (newContext != null)
				msgs = ((InternalEObject)newContext).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__CONTEXT, null, msgs);
			msgs = basicSetContext(newContext, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__CONTEXT, newContext, newContext));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMappingParameters getParameters() {
		return parameters;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain basicSetParameters(EMappingParameters newParameters, NotificationChain msgs) {
		EMappingParameters oldParameters = parameters;
		parameters = newParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__PARAMETERS, oldParameters, newParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setParameters(EMappingParameters newParameters) {
		if (newParameters != parameters) {
			NotificationChain msgs = null;
			if (parameters != null)
				msgs = ((InternalEObject)parameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__PARAMETERS, null, msgs);
			if (newParameters != null)
				msgs = ((InternalEObject)newParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__PARAMETERS, null, msgs);
			msgs = basicSetParameters(newParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__PARAMETERS, newParameters, newParameters));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EMappingResults getResult() {
		return result;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain basicSetResult(EMappingResults newResult, NotificationChain msgs) {
		EMappingResults oldResult = result;
		result = newResult;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__RESULT, oldResult, newResult);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setResult(EMappingResults newResult) {
		if (newResult != result) {
			NotificationChain msgs = null;
			if (result != null)
				msgs = ((InternalEObject)result).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__RESULT, null, msgs);
			if (newResult != null)
				msgs = ((InternalEObject)newResult).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TracePackage.TRACE_RECORD__RESULT, null, msgs);
			msgs = basicSetResult(newResult, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.TRACE_RECORD__RESULT, newResult, newResult));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TracePackage.TRACE_RECORD__MAPPING_OPERATION:
				return basicSetMappingOperation(null, msgs);
			case TracePackage.TRACE_RECORD__CONTEXT:
				return basicSetContext(null, msgs);
			case TracePackage.TRACE_RECORD__PARAMETERS:
				return basicSetParameters(null, msgs);
			case TracePackage.TRACE_RECORD__RESULT:
				return basicSetResult(null, msgs);
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
			case TracePackage.TRACE_RECORD__MAPPING_OPERATION:
				return getMappingOperation();
			case TracePackage.TRACE_RECORD__CONTEXT:
				return getContext();
			case TracePackage.TRACE_RECORD__PARAMETERS:
				return getParameters();
			case TracePackage.TRACE_RECORD__RESULT:
				return getResult();
		}
		return super.eGet(featureID, resolve, coreType);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TracePackage.TRACE_RECORD__MAPPING_OPERATION:
				setMappingOperation((EMappingOperation)newValue);
				return;
			case TracePackage.TRACE_RECORD__CONTEXT:
				setContext((EMappingContext)newValue);
				return;
			case TracePackage.TRACE_RECORD__PARAMETERS:
				setParameters((EMappingParameters)newValue);
				return;
			case TracePackage.TRACE_RECORD__RESULT:
				setResult((EMappingResults)newValue);
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
			case TracePackage.TRACE_RECORD__MAPPING_OPERATION:
				setMappingOperation((EMappingOperation)null);
				return;
			case TracePackage.TRACE_RECORD__CONTEXT:
				setContext((EMappingContext)null);
				return;
			case TracePackage.TRACE_RECORD__PARAMETERS:
				setParameters((EMappingParameters)null);
				return;
			case TracePackage.TRACE_RECORD__RESULT:
				setResult((EMappingResults)null);
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
			case TracePackage.TRACE_RECORD__MAPPING_OPERATION:
				return mappingOperation != null;
			case TracePackage.TRACE_RECORD__CONTEXT:
				return context != null;
			case TracePackage.TRACE_RECORD__PARAMETERS:
				return parameters != null;
			case TracePackage.TRACE_RECORD__RESULT:
				return result != null;
		}
		return super.eIsSet(featureID);
	}

} //TraceRecordImpl
