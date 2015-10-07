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
 * $Id: EMappingOperation.java,v 1.3 2009/05/15 12:02:27 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EMapping Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getPackage <em>Package</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getModule <em>Module</em>}</li>
 *   <li>{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getRuntimeMappingOperation <em>Runtime Mapping Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEMappingOperation()
 * @model
 * @generated
 */
public interface EMappingOperation extends EObject {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEMappingOperation_Name()
	 * @model required="true"
	 * @generated
	 */
    String getName();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
    void setName(String value);

    /**
	 * Returns the value of the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Package</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Package</em>' attribute.
	 * @see #setPackage(String)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEMappingOperation_Package()
	 * @model required="true"
	 * @generated
	 */
    String getPackage();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getPackage <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package</em>' attribute.
	 * @see #getPackage()
	 * @generated
	 */
    void setPackage(String value);

    /**
	 * Returns the value of the '<em><b>Module</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Module</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Module</em>' attribute.
	 * @see #setModule(String)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEMappingOperation_Module()
	 * @model required="true"
	 * @generated
	 */
    String getModule();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getModule <em>Module</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Module</em>' attribute.
	 * @see #getModule()
	 * @generated
	 */
    void setModule(String value);

    /**
	 * Returns the value of the '<em><b>Runtime Mapping Operation</b></em>' reference.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Runtime Mapping Operation</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Runtime Mapping Operation</em>' reference.
	 * @see #setRuntimeMappingOperation(MappingOperation)
	 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage#getEMappingOperation_RuntimeMappingOperation()
	 * @model required="true"
	 * @generated
	 */
    MappingOperation getRuntimeMappingOperation();

    /**
	 * Sets the value of the '{@link org.eclipse.m2m.internal.qvt.oml.trace.EMappingOperation#getRuntimeMappingOperation <em>Runtime Mapping Operation</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Runtime Mapping Operation</em>' reference.
	 * @see #getRuntimeMappingOperation()
	 * @generated
	 */
    void setRuntimeMappingOperation(MappingOperation value);

} // EMappingOperation
