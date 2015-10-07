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
 * $Id: EValueImpl.java,v 1.3 2009/05/15 12:07:10 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.m2m.internal.qvt.oml.trace.EValue;
import org.eclipse.m2m.internal.qvt.oml.trace.TracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EValue</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getPrimitiveValue <em>Primitive Value</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getModelElement <em>Model Element</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getOclObject <em>Ocl Object</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getIntermediateElement <em>Intermediate Element</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getCollectionType <em>Collection Type</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.impl.EValueImpl#getCollection <em>Collection</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EValueImpl extends EObjectImpl implements EValue {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public static final String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The default value of the '{@link #getPrimitiveValue() <em>Primitive Value</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPrimitiveValue()
	 * @generated
	 * @ordered
	 */
    protected static final String PRIMITIVE_VALUE_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getPrimitiveValue() <em>Primitive Value</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPrimitiveValue()
	 * @generated
	 * @ordered
	 */
    protected String primitiveValue = PRIMITIVE_VALUE_EDEFAULT;

    /**
	 * The cached value of the '{@link #getModelElement() <em>Model Element</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getModelElement()
	 * @generated
	 * @ordered
	 */
    protected EObject modelElement;

    /**
	 * The default value of the '{@link #getOclObject() <em>Ocl Object</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getOclObject()
	 * @generated
	 * @ordered
	 */
    protected static final Object OCL_OBJECT_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getOclObject() <em>Ocl Object</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getOclObject()
	 * @generated
	 * @ordered
	 */
    protected Object oclObject = OCL_OBJECT_EDEFAULT;

    /**
	 * The cached value of the '{@link #getIntermediateElement() <em>Intermediate Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getIntermediateElement()
	 * @generated
	 * @ordered
	 */
    protected EObject intermediateElement;

    /**
	 * The default value of the '{@link #getCollectionType() <em>Collection Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getCollectionType()
	 * @generated
	 * @ordered
	 */
    protected static final String COLLECTION_TYPE_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getCollectionType() <em>Collection Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getCollectionType()
	 * @generated
	 * @ordered
	 */
    protected String collectionType = COLLECTION_TYPE_EDEFAULT;

    /**
	 * The cached value of the '{@link #getCollection() <em>Collection</em>}' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getCollection()
	 * @generated
	 * @ordered
	 */
    protected EList<EValue> collection;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected EValueImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return TracePackage.Literals.EVALUE;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getPrimitiveValue() {
		return primitiveValue;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setPrimitiveValue(String newPrimitiveValue) {
		String oldPrimitiveValue = primitiveValue;
		primitiveValue = newPrimitiveValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__PRIMITIVE_VALUE, oldPrimitiveValue, primitiveValue));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EObject getModelElement() {
		if (modelElement != null && modelElement.eIsProxy()) {
			InternalEObject oldModelElement = (InternalEObject)modelElement;
			modelElement = eResolveProxy(oldModelElement);
			if (modelElement != oldModelElement) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TracePackage.EVALUE__MODEL_ELEMENT, oldModelElement, modelElement));
			}
		}
		return modelElement;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EObject basicGetModelElement() {
		return modelElement;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setModelElement(EObject newModelElement) {
		EObject oldModelElement = modelElement;
		modelElement = newModelElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__MODEL_ELEMENT, oldModelElement, modelElement));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Object getOclObject() {
		return oclObject;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setOclObject(Object newOclObject) {
		Object oldOclObject = oclObject;
		oclObject = newOclObject;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__OCL_OBJECT, oldOclObject, oclObject));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EObject getIntermediateElement() {
		return intermediateElement;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain basicSetIntermediateElement(EObject newIntermediateElement, NotificationChain msgs) {
		EObject oldIntermediateElement = intermediateElement;
		intermediateElement = newIntermediateElement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__INTERMEDIATE_ELEMENT, oldIntermediateElement, newIntermediateElement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setIntermediateElement(EObject newIntermediateElement) {
		if (newIntermediateElement != intermediateElement) {
			NotificationChain msgs = null;
			if (intermediateElement != null)
				msgs = ((InternalEObject)intermediateElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TracePackage.EVALUE__INTERMEDIATE_ELEMENT, null, msgs);
			if (newIntermediateElement != null)
				msgs = ((InternalEObject)newIntermediateElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TracePackage.EVALUE__INTERMEDIATE_ELEMENT, null, msgs);
			msgs = basicSetIntermediateElement(newIntermediateElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__INTERMEDIATE_ELEMENT, newIntermediateElement, newIntermediateElement));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getCollectionType() {
		return collectionType;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setCollectionType(String newCollectionType) {
		String oldCollectionType = collectionType;
		collectionType = newCollectionType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TracePackage.EVALUE__COLLECTION_TYPE, oldCollectionType, collectionType));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EList<EValue> getCollection() {
		if (collection == null) {
			collection = new EObjectContainmentEList<EValue>(EValue.class, this, TracePackage.EVALUE__COLLECTION);
		}
		return collection;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TracePackage.EVALUE__INTERMEDIATE_ELEMENT:
				return basicSetIntermediateElement(null, msgs);
			case TracePackage.EVALUE__COLLECTION:
				return ((InternalEList<?>)getCollection()).basicRemove(otherEnd, msgs);
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
			case TracePackage.EVALUE__PRIMITIVE_VALUE:
				return getPrimitiveValue();
			case TracePackage.EVALUE__MODEL_ELEMENT:
				if (resolve) return getModelElement();
				return basicGetModelElement();
			case TracePackage.EVALUE__OCL_OBJECT:
				return getOclObject();
			case TracePackage.EVALUE__INTERMEDIATE_ELEMENT:
				return getIntermediateElement();
			case TracePackage.EVALUE__COLLECTION_TYPE:
				return getCollectionType();
			case TracePackage.EVALUE__COLLECTION:
				return getCollection();
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
			case TracePackage.EVALUE__PRIMITIVE_VALUE:
				setPrimitiveValue((String)newValue);
				return;
			case TracePackage.EVALUE__MODEL_ELEMENT:
				setModelElement((EObject)newValue);
				return;
			case TracePackage.EVALUE__OCL_OBJECT:
				setOclObject(newValue);
				return;
			case TracePackage.EVALUE__INTERMEDIATE_ELEMENT:
				setIntermediateElement((EObject)newValue);
				return;
			case TracePackage.EVALUE__COLLECTION_TYPE:
				setCollectionType((String)newValue);
				return;
			case TracePackage.EVALUE__COLLECTION:
				getCollection().clear();
				getCollection().addAll((Collection<? extends EValue>)newValue);
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
			case TracePackage.EVALUE__PRIMITIVE_VALUE:
				setPrimitiveValue(PRIMITIVE_VALUE_EDEFAULT);
				return;
			case TracePackage.EVALUE__MODEL_ELEMENT:
				setModelElement((EObject)null);
				return;
			case TracePackage.EVALUE__OCL_OBJECT:
				setOclObject(OCL_OBJECT_EDEFAULT);
				return;
			case TracePackage.EVALUE__INTERMEDIATE_ELEMENT:
				setIntermediateElement((EObject)null);
				return;
			case TracePackage.EVALUE__COLLECTION_TYPE:
				setCollectionType(COLLECTION_TYPE_EDEFAULT);
				return;
			case TracePackage.EVALUE__COLLECTION:
				getCollection().clear();
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
			case TracePackage.EVALUE__PRIMITIVE_VALUE:
				return PRIMITIVE_VALUE_EDEFAULT == null ? primitiveValue != null : !PRIMITIVE_VALUE_EDEFAULT.equals(primitiveValue);
			case TracePackage.EVALUE__MODEL_ELEMENT:
				return modelElement != null;
			case TracePackage.EVALUE__OCL_OBJECT:
				return OCL_OBJECT_EDEFAULT == null ? oclObject != null : !OCL_OBJECT_EDEFAULT.equals(oclObject);
			case TracePackage.EVALUE__INTERMEDIATE_ELEMENT:
				return intermediateElement != null;
			case TracePackage.EVALUE__COLLECTION_TYPE:
				return COLLECTION_TYPE_EDEFAULT == null ? collectionType != null : !COLLECTION_TYPE_EDEFAULT.equals(collectionType);
			case TracePackage.EVALUE__COLLECTION:
				return collection != null && !collection.isEmpty();
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
		result.append(" (primitiveValue: "); //$NON-NLS-1$
		result.append(primitiveValue);
		result.append(", oclObject: "); //$NON-NLS-1$
		result.append(oclObject);
		result.append(", collectionType: "); //$NON-NLS-1$
		result.append(collectionType);
		result.append(')');
		return result.toString();
	}

} //EValueImpl
