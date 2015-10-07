/*******************************************************************************
 * Copyright (c) 2008, 2009 Borland Software Corporation and others.
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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;


class ModelInstanceImpl extends DynamicEObjectImpl  implements ModelInstance {

	private ModelParameterExtent fExtent;
	
	ModelInstanceImpl(ModelType modelType, ModelParameterExtent extent) {
		if(modelType == null || extent == null) {
			throw new IllegalArgumentException();
		}
		
		eSetClass(modelType);
		fExtent = extent;
	}
		
	public ModelType getModelType() {
		return (ModelType) eClass();
	}
	
	public ModelParameterExtent getExtent() {
		return fExtent;
	}
	
	public ModelInstance copy() {
		ModelParameterExtent extentCopy = new ModelParameterExtent(getExtent());
		return new ModelInstanceImpl(getModelType(), extentCopy);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(eClass().getName()).append(" - "); //$NON-NLS-1$
		ModelType modelType = getModelType();
		
		for(EPackage metamodel : modelType.getMetamodel()) {
			buf.append(metamodel.getNsURI());
		}
		
		return buf.toString();
	}
}
