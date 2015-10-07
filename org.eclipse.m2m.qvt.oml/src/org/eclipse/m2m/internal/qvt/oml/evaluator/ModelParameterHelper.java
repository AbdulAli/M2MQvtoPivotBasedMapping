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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance.InternalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;

/**
 * This class encapsulates the logic of model extent binding for imported transformation.
 * </p>
 * The QVT <code>1.0</code> model parameter binding is implemented here, taking the following strategy.
 * <ul>
 *  <li>1. Try binding model parameters that are strictly compatible by modeltype and direction kind</li>

 *  <li>2. Try binding (not yet bound) model parameters by the first available of a compatible direction kind</li>  
 *  
 *  <li>3. Create empty model extent for all model parameter remaining unbound</li>  
 * </ul>
 * 
 * @author dvorak
 */
public class ModelParameterHelper {

	private OperationalTransformation fMainTransformation;
	private List<ModelInstance> fModelArguments;	

	/**
	 * Create actual model arguments for the given transformation.
	 * 
	 * @param transformation
	 *            the transformation of which the <code>main</code> operation is
	 *            to be executed
	 * @param modelExtentArgs
	 *            the model extents that are passed to transformation execution
	 */
	public static List<ModelInstance> createModelArguments(OperationalTransformation transformation, List<ModelParameterExtent> modelExtentArgs) {
		if(transformation == null || modelExtentArgs == null) {
			throw new IllegalArgumentException();
		}

		if(modelExtentArgs.size() != transformation.getModelParameter().size()) {
			throw new IllegalArgumentException("Invalid number of transformation arguments"); //$NON-NLS-1$
		}		
		
		List<ModelInstance> modelArgs = new ArrayList<ModelInstance>(modelExtentArgs.size()); 
		int pos = 0;
		for (ModelParameterExtent nextExtent : modelExtentArgs) {
			if(nextExtent == null) {
				throw new IllegalArgumentException("null model extent argument"); //$NON-NLS-1$
			}
			ModelParameter modelParam = transformation.getModelParameter().get(pos++);
			modelArgs.add(createModel(modelParam, nextExtent));
		}
		
		return modelArgs;
	}
	
	ModelParameterHelper(OperationalTransformation mainTransformation, List<ModelInstance> modelArgs) {
		if(mainTransformation == null || modelArgs == null) {
			throw new IllegalArgumentException();
		}
		
		if(modelArgs.size() != mainTransformation.getModelParameter().size()) {
			throw new IllegalArgumentException("Invalid number of transformation arguments"); //$NON-NLS-1$
		}		
		
		int pos = 0;
		for (ModelInstance nextModel : modelArgs) {
			if(nextModel == null) {
				throw new IllegalArgumentException("null model argument"); //$NON-NLS-1$
			}
			
			ModelParameter modelParam = mainTransformation.getModelParameter().get(pos++);
			EClassifier modelType = modelParam.getEType();
			if(nextModel.eClass().isInstance(modelType)) {
				throw new IllegalArgumentException("Invalid model for parameter: " + modelParam); //$NON-NLS-1$
			}
		}

		fMainTransformation = mainTransformation;
		fModelArguments = new ArrayList<ModelInstance>(modelArgs);
	}

	/**
	 * Binds model extents to model parameters of the given
	 * transformation in the context of the main transformation and its imported transformations.
	 * 
	 * @param transformation
	 *            the transformation to be bind with its model parameters. It must be the trasnformation
	 *            assigned to this helper or one of its imported transformations 
	 */
	void initModelParameters(TransformationInstance transformation) {
		if(transformation == null) {
			throw new IllegalArgumentException();
		}
		
		boolean isMainTransformation = transformation.getTransformation() == fMainTransformation;		
		OperationalTransformation transformationType = transformation.getTransformation(); 				
		Collection<ModelInstance> alreadyBound = new UniqueEList.FastCompare<ModelInstance>();
		
		int pos = 0;
		for (ModelParameter modelParam : transformationType.getModelParameter()) {
			ModelInstance passedModel;
			if(isMainTransformation) {
				passedModel = fModelArguments.get(pos);
			} else {
				passedModel = findAvailableStrictlyCompatibleExtent(modelParam, alreadyBound);			
			}
			
			if(passedModel == null) {
				continue;
			}
			
			ModelInstance actualModel;
			if(passedModel.getModelType() == modelParam.getEType()) {
				// model parameter refer the same modeltype as the model instance
				// => reuse the model instance for passing as model argument
				actualModel = passedModel;
			} else {
				// we are just compatible by referred metamodels, so create
				// new model instance of a specific modeltype
				actualModel = createModel(modelParam, passedModel.getExtent());
			}
			
			transformation.getAdapter(InternalTransformation.class).setModel(modelParam, actualModel);
			
			pos++;
		}
		
		if(isMainTransformation) {
			return;
		}
		
		// second pass
		// 1) try resolving by compatible direction only		
		// 2) create unbound extents to capture the contents for not yet bound model parameters
		for (ModelParameter modelParam : transformationType.getModelParameter()) {
			// process only not set parameters
			if(transformation.getModel(modelParam) == null) {
				ModelInstance resolvedModel = findFirstDirectionCompatibleExtent(modelParam, alreadyBound);
				
				ModelInstance actualModel;
				if(resolvedModel == null) {
					// can't find any compatible model parameter, just create an empty extent
					actualModel = createModel(modelParam, new ModelParameterExtent());	
				} else {
					// take just the content as we have not found strictly compatible model parameter
					// in the 1. pass, we have to create new model instance but reusing the extent
					actualModel = createModel(modelParam, resolvedModel.getExtent());
				}

				transformation.getAdapter(InternalTransformation.class).setModel(modelParam, actualModel);				
			}
		}
	}
		
	private ModelInstance findFirstDirectionCompatibleExtent(ModelParameter modelParam, Collection<ModelInstance> alreadyBound) {
		int pos = 0;
		for (ModelParameter nextParam : fMainTransformation.getModelParameter()) {
			if(QvtOperationalUtil.isModelParamEqual(nextParam, modelParam, false)) {
				ModelInstance extent = fModelArguments.get(pos);
				if(!alreadyBound.contains(extent)) {
					alreadyBound.add(extent);					
					return extent;
				}
			}
			
			pos++;
		}

		return null;
	}	
	
	private ModelInstance findAvailableStrictlyCompatibleExtent(ModelParameter modelParam, Collection<ModelInstance> alreadyBound) {
		int pos = 0;
		for (ModelParameter nextParam : fMainTransformation.getModelParameter()) {
			if(QvtOperationalUtil.isModelParamEqual(nextParam, modelParam, true)) {
				ModelInstance extent = fModelArguments.get(pos);
				if(!alreadyBound.contains(extent)) {
					alreadyBound.add(extent);
					return extent;
				}
			}
			
			pos++;
		}
		
		return null;
	}
	
	private static ModelInstance createModel(ModelParameter modelParam, ModelParameterExtent extent) {
		assert modelParam != null;
		assert extent != null;
		
		ModelType modelType = (ModelType) modelParam.getEType();
		ModelInstance model = new ModelInstanceImpl(modelType, extent);
    	return model;
	}	
}
