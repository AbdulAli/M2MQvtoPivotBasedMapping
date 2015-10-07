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
 * $Id: TraceFactory.java,v 1.3 2009/05/15 12:02:28 radvorak Exp $
 */
package org.eclipse.m2m.internal.qvt.oml.trace;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.m2m.internal.qvt.oml.trace.TracePackage
 * @generated
 */
public interface TraceFactory extends EFactory {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    String copyright = "Copyright (c) 2007 Borland Software Corporation\r\n\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n  \r\nContributors:\r\n    Borland Software Corporation - initial API and implementation"; //$NON-NLS-1$

    /**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    TraceFactory eINSTANCE = org.eclipse.m2m.internal.qvt.oml.trace.impl.TraceFactoryImpl.init();

    /**
	 * Returns a new object of class '<em>Trace</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trace</em>'.
	 * @generated
	 */
    Trace createTrace();

    /**
	 * Returns a new object of class '<em>Record</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>Record</em>'.
	 * @generated
	 */
    TraceRecord createTraceRecord();

    /**
	 * Returns a new object of class '<em>Var Parameter Value</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>Var Parameter Value</em>'.
	 * @generated
	 */
    VarParameterValue createVarParameterValue();

    /**
	 * Returns a new object of class '<em>EMapping Operation</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>EMapping Operation</em>'.
	 * @generated
	 */
    EMappingOperation createEMappingOperation();

    /**
	 * Returns a new object of class '<em>EValue</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>EValue</em>'.
	 * @generated
	 */
    EValue createEValue();

    /**
	 * Returns a new object of class '<em>ETuple Part Value</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>ETuple Part Value</em>'.
	 * @generated
	 */
    ETuplePartValue createETuplePartValue();

    /**
	 * Returns a new object of class '<em>EMapping Context</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>EMapping Context</em>'.
	 * @generated
	 */
    EMappingContext createEMappingContext();

    /**
	 * Returns a new object of class '<em>EMapping Parameters</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>EMapping Parameters</em>'.
	 * @generated
	 */
    EMappingParameters createEMappingParameters();

    /**
	 * Returns a new object of class '<em>EMapping Results</em>'.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return a new object of class '<em>EMapping Results</em>'.
	 * @generated
	 */
    EMappingResults createEMappingResults();

    /**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
    TracePackage getTracePackage();

} //TraceFactory
