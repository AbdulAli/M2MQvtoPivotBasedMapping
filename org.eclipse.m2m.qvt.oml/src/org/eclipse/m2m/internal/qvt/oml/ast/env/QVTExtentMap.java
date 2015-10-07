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
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModelInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.LazyExtentMap;
import org.eclipse.ocl.ecore.EcoreEvaluationEnvironment;

class QVTExtentMap extends HashMap<EClass, Set<EObject>> {

	private static final long serialVersionUID = -7110659347649872111L;
	
	private EcoreEvaluationEnvironment fEvalEnv;

	
	QVTExtentMap(EcoreEvaluationEnvironment evalEnv) {
		if(evalEnv == null) {
			throw new IllegalArgumentException();
		}
		
		fEvalEnv = evalEnv;
	}
	
	@Override
	public Set<EObject> get(Object key) {
		if(key instanceof EClass) {
			return collectInstances((EClass) key);
		}
		return Collections.emptySet();
	}

	private List<ModelParameterExtent> getExtents() {
		InternalEvaluationEnv internEvalEnv = fEvalEnv.getAdapter(InternalEvaluationEnv.class);
		Object thisObj = internEvalEnv.getCurrentTransformation();
		if(thisObj instanceof TransformationInstance) {
			TransformationInstance thisTransf = (TransformationInstance) thisObj;
			LinkedList<ModelParameterExtent> extents = new LinkedList<ModelParameterExtent>();			
			
			for (ModelParameter modelParam : thisTransf.getTransformation().getModelParameter()) {
				ModelInstance model = thisTransf.getModel(modelParam);
				extents.add(model.getExtent());
			}

			extents.add(internEvalEnv.getUnboundExtent());
			return extents;
		}
		
		return null;
	}
	
	private Set<EObject> collectInstances(EClass context) {
		List<ModelParameterExtent> extents = getExtents();
		if(extents == null) {
			Object selfObj = fEvalEnv.getValueOf(Environment.SELF_VARIABLE_NAME);
			if(selfObj instanceof EObject) {
				return new LazyExtentMap<EClass, EObject>((EObject) selfObj) {
					@Override
					protected boolean isInstance(EClass cls, EObject element) {
						return fEvalEnv.isKindOf(element, cls);
					};
				}.get(context);
			}
			return Collections.emptySet();
		}
		
		HashSet<EObject> result = new HashSet<EObject>();		
		for (ModelParameterExtent nextExtent : extents) {
			for (Object nextObj : nextExtent.getAllObjects()) {
				if(nextObj instanceof EObject && fEvalEnv.isKindOf(nextObj, context)) {
					result.add((EObject)nextObj);
				}
			}
		}
		return result;
	}
}
