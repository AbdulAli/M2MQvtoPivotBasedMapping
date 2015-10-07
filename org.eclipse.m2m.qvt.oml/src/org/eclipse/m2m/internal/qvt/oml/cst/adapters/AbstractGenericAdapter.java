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

package org.eclipse.m2m.internal.qvt.oml.cst.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

/**
 * @author aigdalov
 */

public abstract class AbstractGenericAdapter<T> implements Adapter {
    private Notifier myTarget;
    
    public Notifier getTarget() {
        return myTarget;
    }

    public void notifyChanged(Notification notification) {
    }

    public void setTarget(Notifier newTarget) {
        myTarget = newTarget;
    }

}