/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.binding;

import org.eclipse.emf.ecore.EObject;

/**
 * @noimplement
 */
public interface ASTSyntheticNode {
	
	int getStartPosition();

	int getEndPosition();

	void setEndPosition(int endPosition);

	void setStartPosition(int startPosition);
		
	EObject getActualElement();	
}