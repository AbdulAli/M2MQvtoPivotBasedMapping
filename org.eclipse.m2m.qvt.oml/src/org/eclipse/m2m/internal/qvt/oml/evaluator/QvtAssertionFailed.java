/*******************************************************************************
 * Copyright (c) 2007, 2008 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;


/**
* Thrown if the condition of <code>assertion</code> with severity <code>fatal</code> fails.
*/
public class QvtAssertionFailed extends QvtException {
	private static final long serialVersionUID = -1690018812250274122L;

	public QvtAssertionFailed(String message) {
		super(message, QvtOperationalStdLibrary.INSTANCE.getAssertionFailedClass());
	}
}
