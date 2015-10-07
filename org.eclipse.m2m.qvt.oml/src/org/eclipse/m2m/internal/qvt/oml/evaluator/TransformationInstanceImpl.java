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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance.InternalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;


class TransformationInstanceImpl extends ModuleInstanceImpl implements TransformationInstance, InternalTransformation {

	private final Map<ModelParameter, ModelInstance> fModelParams;
	private ModelInstance fIntermediateData;
	private CallHandler fTransHandler;
	
	TransformationInstanceImpl(OperationalTransformation type) {
		super(type);
		fModelParams = new HashMap<ModelParameter, ModelInstance>(3);
		initIntermediateExtentIfRequired();		
	}
	
	public void setModel(ModelParameter parameter, ModelInstance extent) {
		if(parameter == null || extent == null) {
			throw new IllegalArgumentException();
		}

		fModelParams.put(parameter, extent);
	}
	
	public ModelInstance getIntermediateExtent() {	
		return fIntermediateData;
	}

	public OperationalTransformation getTransformation() {	
		return (OperationalTransformation) eClass();
	}
	
	public ModelInstance getModel(ModelParameter modelParam) {
		return fModelParams.get(modelParam);
	}
	
	@Override
	public <T> T getAdapter(Class<T> adapterType) {
		if(InternalTransformation.class == adapterType || 
			TransformationInstance.class == adapterType ||
			ModuleInstance.class == adapterType) {
			return adapterType.cast(this);
		}

		return super.getAdapter(adapterType);
	}
	
	public CallHandler getTransformationHandler() {
		return fTransHandler;
	}
	
	public void setTransformationHandler(CallHandler handler) {
		fTransHandler = handler;
	}
	
	@Override
	public void dispose() {	
		super.dispose();
	}

	@Override
	public String toString() {
		OperationalTransformation transformation = getTransformation();
		
		StringBuilder buf = new StringBuilder();
		buf.append("transformation ").append(transformation.getName()).append("("); //$NON-NLS-1$ //$NON-NLS-2$
		
		int pos = 0;
		for (ModelParameter modelParameter : transformation.getModelParameter()) {
			if(pos++ > 0) {
				buf.append(',').append(' ');
			}
			buf.append(modelParameter.getKind()).append(' ')
				.append(modelParameter.getName())
				.append(" : "); //$NON-NLS-1$
			
			EClassifier type = modelParameter.getEType();
			if(type != null) {
				buf.append(type.getName());
			}
		}
		
		buf.append(") @").append(Integer.toHexString(System.identityHashCode(this))); //$NON-NLS-1$
		return buf.toString();
	}

	private void initIntermediateExtentIfRequired() {
		for (EClassifier ownedType : getTransformation().getEClassifiers()) {
			if(ownedType instanceof ModelType) {
				ModelType modelType = (ModelType) ownedType;			
				if(IntermediateClassFactory.isIntermediateModelType(modelType)) {
					fIntermediateData = new ModelInstanceImpl(modelType, new ModelParameterExtent());
					return;
				}
			}
		}
	}
}
