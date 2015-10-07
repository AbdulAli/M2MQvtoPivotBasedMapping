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
 * $Id: EValue.java,v 1.3 2009/05/15 12:02:28 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EValue</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getPrimitiveValue <em>Primitive Value</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getModelElement <em>Model Element</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getOclObject <em>Ocl Object</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getIntermediateElement <em>Intermediate Element</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollectionType <em>Collection Type</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollection <em>Collection</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue()
 * @model
 * @generated
 */
public interface EValue extends EObject {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * Returns the value of the '<em><b>Primitive Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Primitive Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Primitive Value</em>' attribute.
	 * @see #setPrimitiveValue(String)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_PrimitiveValue()
	 * @model
	 * @generated
	 */
    String getPrimitiveValue();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getPrimitiveValue <em>Primitive Value</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Primitive Value</em>' attribute.
	 * @see #getPrimitiveValue()
	 * @generated
	 */
    void setPrimitiveValue(String value);

    /**
	 * Returns the value of the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Element</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Element</em>' reference.
	 * @see #setModelElement(EObject)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_ModelElement()
	 * @model
	 * @generated
	 */
    EObject getModelElement();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getModelElement <em>Model Element</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model Element</em>' reference.
	 * @see #getModelElement()
	 * @generated
	 */
    void setModelElement(EObject value);

    /**
	 * Returns the value of the '<em><b>Ocl Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ocl Object</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Ocl Object</em>' attribute.
	 * @see #setOclObject(Object)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_OclObject()
	 * @model transient="true"
	 * @generated
	 */
    Object getOclObject();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getOclObject <em>Ocl Object</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ocl Object</em>' attribute.
	 * @see #getOclObject()
	 * @generated
	 */
    void setOclObject(Object value);

    /**
	 * Returns the value of the '<em><b>Intermediate Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Intermediate Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Intermediate Element</em>' containment reference.
	 * @see #setIntermediateElement(EObject)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_IntermediateElement()
	 * @model containment="true"
	 * @generated
	 */
    EObject getIntermediateElement();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getIntermediateElement <em>Intermediate Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Intermediate Element</em>' containment reference.
	 * @see #getIntermediateElement()
	 * @generated
	 */
    void setIntermediateElement(EObject value);

    /**
	 * Returns the value of the '<em><b>Collection Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collection Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Collection Type</em>' attribute.
	 * @see #setCollectionType(String)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_CollectionType()
	 * @model
	 * @generated
	 */
    String getCollectionType();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EValue#getCollectionType <em>Collection Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Collection Type</em>' attribute.
	 * @see #getCollectionType()
	 * @generated
	 */
    void setCollectionType(String value);

    /**
	 * Returns the value of the '<em><b>Collection</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.m2m.internal.qvt.oml.trace.EValue}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collection</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Collection</em>' containment reference list.
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEValue_Collection()
	 * @model containment="true"
	 * @generated
	 */
    EList<EValue> getCollection();

} // EValue
