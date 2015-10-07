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

package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * @author sboyko
 *
 */
public class QvtEvaluationResult {
	
	public QvtEvaluationResult(List<ModelExtentContents> modelExtents, List<EObject> unboundedObjects, List<Object> outParamValues) {
		myModelExtents = modelExtents;
		myUnboundedObjects = unboundedObjects;
		myOutParamValues = outParamValues;
	}
	
	public List<ModelExtentContents> getModelExtents() {
		return myModelExtents;
	}
	
	public List<EObject> getUnboundedObjects() {
		return myUnboundedObjects;
	}
	
	public List<Object> getOutParamValues() {
		return myOutParamValues;
	}
	
	private final List<ModelExtentContents> myModelExtents;
	private final List<EObject> myUnboundedObjects;
	private final List<Object> myOutParamValues;

}
