/*******************************************************************************
 * Copyright (c) 2007, 2013 Borland Software Corporation and others.
 * 
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
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.types.BagType;
import org.eclipse.ocl.types.OrderedSetType;
import org.eclipse.ocl.types.SequenceType;
import org.eclipse.ocl.types.SetType;
import org.eclipse.ocl.types.TypeType;
import org.eclipse.ocl.types.VoidType;

/**
 * @author aigdalov
 * Created on Aug 2, 2007
 */

public class QvtOperationalTypesUtil {
    private static final String UNKNOWN_TYPE_NAME = "unknown"; //$NON-NLS-1$

    public static String getTypeFullName(EClassifier type) {
        if (type == null) {
            return UNKNOWN_TYPE_NAME;
        }
        String fullName = getTypeName(type);
        EObject parent = type.eContainer();
        while (parent != null) {
            if (parent instanceof ENamedElement) {
                fullName = ((ENamedElement) parent).getName() + EmfUtil.PATH_SEPARATOR + fullName;
            }
            parent = parent.eContainer();
        }
        if (type instanceof TypeType<?, ?>) {
        	fullName = TypeType.SINGLETON_NAME + "(" + fullName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return fullName;
    }
    
    // Workaround for CollectionType.getName
    public static final String getTypeName(EClassifier type) {
        if (type instanceof CollectionType) {
            return getCollectionTypeName((CollectionType) type);
        }
        return type.getName();
    }
    
    // Workaround for CollectionType.getName
    private static final String getCollectionTypeName(CollectionType collectionType) {
        StringBuffer sbName = new StringBuffer();

        switch (collectionType.getKind()) {
        case SET_LITERAL:
            sbName.append(SetType.SINGLETON_NAME);
            break;
        case ORDERED_SET_LITERAL:
            sbName.append(OrderedSetType.SINGLETON_NAME);
            break;
        case BAG_LITERAL:
            sbName.append(BagType.SINGLETON_NAME);
            break;
        case SEQUENCE_LITERAL:
            sbName.append(SequenceType.SINGLETON_NAME);
            break;
        default:
            sbName.append(collectionType.getName());
        	return sbName.toString();
        
        }

        sbName.append('(');

        EClassifier elementType = collectionType.getElementType();
        String elementTypeName;
        if (elementType == null) { // Here is the workaround
            elementTypeName = UNKNOWN_TYPE_NAME;
        } else if (elementType instanceof VoidType<?>) {
                elementTypeName = "T"; //$NON-NLS-1$
        } else {
            elementTypeName = getTypeName(elementType);
        }

        sbName.append(elementTypeName);
        sbName.append(')');

        return sbName.toString();
    }
}