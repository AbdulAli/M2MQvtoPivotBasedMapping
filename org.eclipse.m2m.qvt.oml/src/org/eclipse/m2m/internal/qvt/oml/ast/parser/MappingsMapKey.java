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
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.ocl.ecore.VoidType;

/**
 * @author aigdalov
 */

public class MappingsMapKey {
    private EClassifier myContext;
    private String myName;
    
    public MappingsMapKey(EClassifier context, String name) {
        super();
        myContext = context;
        myName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MappingsMapKey)) {
            return false;
        }
        MappingsMapKey candidate = (MappingsMapKey) obj;
        if (isVoid(myContext)) {
            if (!isVoid(candidate.myContext)) {
                return false;
            }
        } else if (!myContext.equals(candidate.myContext)) {
            return false;
        }
        return myName.equals(candidate.myName);
    }

    @Override
    public int hashCode() {
        int contextHashCode = isVoid(myContext) ? 0 : myContext.hashCode();
        return 31 * contextHashCode + myName.hashCode();
    }
    
    private static boolean isVoid(EClassifier eClassifier) {
        return (eClassifier == null) || (eClassifier instanceof VoidType);
    }
}
