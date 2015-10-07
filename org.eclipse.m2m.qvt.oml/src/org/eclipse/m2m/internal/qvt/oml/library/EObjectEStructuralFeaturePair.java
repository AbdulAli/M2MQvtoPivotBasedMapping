/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.library;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author aigdalov
 */

public class EObjectEStructuralFeaturePair {
    private EObject myEObject;
    private EStructuralFeature myEStructuralFeature;
 
    public EObjectEStructuralFeaturePair(EObject object, EStructuralFeature structuralFeature) {
        myEObject = object;
        myEStructuralFeature = structuralFeature;
    }
    
    public EObject getEObject() {
        return myEObject;
    }
    
    public void setEObject(EObject object) {
        myEObject = object;
    }
    
    public EStructuralFeature getEStructuralFeature() {
        return myEStructuralFeature;
    }
    
    public void setEStructuralFeature(EStructuralFeature structuralFeature) {
        myEStructuralFeature = structuralFeature;
    }
}