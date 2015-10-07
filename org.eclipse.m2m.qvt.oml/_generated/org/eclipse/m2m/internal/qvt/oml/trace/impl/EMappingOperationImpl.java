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
 * $Id: EMappingOperationImpl.java,v 1.3 2009/05/15 12:07:10 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation;
import org.eclipse.m2m.internal.qvt.oml.trace.TracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMapping Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl#getPackage <em>Package</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl#getModule <em>Module</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EMappingOperationImpl#getRuntimeMappingOperation <em>Runtime Mapping Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMappingOperationImpl extends EObjectImpl implements EMappingOperation {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public static final String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected static final String NAME_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected String name = NAME_EDEFAULT;

    /**
	 * The default value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
    protected static final String PACKAGE_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
    protected String package_ = PACKAGE_EDEFAULT;

    /**
	 * The default value of the '{@link #getModule() <em>Module</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getModule()
	 * @generated
	 * @ordered
	 */
    protected static final String MODULE_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getModule() <em>Module</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getModule()
	 * @generated
	 * @ordered
	 */
    protected String module = MODULE_EDEFAULT;

    /**
	 * The cached value of the '{@link #getRuntimeMappingOperation() <em>Runtime Mapping Operation</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getRuntimeMappingOperation()
	 * @generated
	 * @ordered
	 */
    protected MappingOperation runtimeMappingOperation;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected EMappingOperationImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return TracePackage.Literals.EMAPPING_OPERATION;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getName() {
		return name;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EMAPPING_OPERATION__NAME, oldName, name));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getPackage() {
		return package_;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setPackage(String newPackage) {
		String oldPackage = package_;
		package_ = newPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EMAPPING_OPERATION__PACKAGE, oldPackage, package_));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getModule() {
		return module;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setModule(String newModule) {
		String oldModule = module;
		module = newModule;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EMAPPING_OPERATION__MODULE, oldModule, module));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public MappingOperation getRuntimeMappingOperation() {
		if (runtimeMappingOperation != null && runtimeMappingOperation.eIsProxy()) {
			InternalEObject oldRuntimeMappingOperation = (InternalEObject)runtimeMappingOperation;
			runtimeMappingOperation = (MappingOperation)eResolveProxy(oldRuntimeMappingOperation);
			if (runtimeMappingOperation != oldRuntimeMappingOperation) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION, oldRuntimeMappingOperation, runtimeMappingOperation));
			}
		}
		return runtimeMappingOperation;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public MappingOperation basicGetRuntimeMappingOperation() {
		return runtimeMappingOperation;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setRuntimeMappingOperation(MappingOperation newRuntimeMappingOperation) {
		MappingOperation oldRuntimeMappingOperation = runtimeMappingOperation;
		runtimeMappingOperation = newRuntimeMappingOperation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION, oldRuntimeMappingOperation, runtimeMappingOperation));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TracePackage.EMAPPING_OPERATION__NAME:
				return getName();
			case TracePackage.EMAPPING_OPERATION__PACKAGE:
				return getPackage();
			case TracePackage.EMAPPING_OPERATION__MODULE:
				return getModule();
			case TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION:
				if (resolve) return getRuntimeMappingOperation();
				return basicGetRuntimeMappingOperation();
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
			case TracePackage.EMAPPING_OPERATION__NAME:
				setName((String)newValue);
				return;
			case TracePackage.EMAPPING_OPERATION__PACKAGE:
				setPackage((String)newValue);
				return;
			case TracePackage.EMAPPING_OPERATION__MODULE:
				setModule((String)newValue);
				return;
			case TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION:
				setRuntimeMappingOperation((MappingOperation)newValue);
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
			case TracePackage.EMAPPING_OPERATION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case TracePackage.EMAPPING_OPERATION__PACKAGE:
				setPackage(PACKAGE_EDEFAULT);
				return;
			case TracePackage.EMAPPING_OPERATION__MODULE:
				setModule(MODULE_EDEFAULT);
				return;
			case TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION:
				setRuntimeMappingOperation((MappingOperation)null);
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
			case TracePackage.EMAPPING_OPERATION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case TracePackage.EMAPPING_OPERATION__PACKAGE:
				return PACKAGE_EDEFAULT == null ? package_ != null : !PACKAGE_EDEFAULT.equals(package_);
			case TracePackage.EMAPPING_OPERATION__MODULE:
				return MODULE_EDEFAULT == null ? module != null : !MODULE_EDEFAULT.equals(module);
			case TracePackage.EMAPPING_OPERATION__RUNTIME_MAPPING_OPERATION:
				return runtimeMappingOperation != null;
		}
		return super.eIsSet(featureID);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", package: "); //$NON-NLS-1$
		result.append(package_);
		result.append(", module: "); //$NON-NLS-1$
		result.append(module);
		result.append(')');
		return result.toString();
	}

} //EMappingOperationImpl
