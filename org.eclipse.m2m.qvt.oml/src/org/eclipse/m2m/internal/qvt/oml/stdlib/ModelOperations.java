/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.Messages;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModelInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;


public class ModelOperations extends AbstractContextualOperations {

	static final String CREATE_EMPTY_MODEL_NAME = "createEmptyModel"; //$NON-NLS-1$	
	static final String COPY_NAME = "copy"; //$NON-NLS-1$	
	static final String OBJECTS_NAME = "objects"; //$NON-NLS-1$
	static final String ROOT_OBJECTS_NAME = "rootObjects"; //$NON-NLS-1$
	static final String OBJECTS_OF_TYPE_NAME = "objectsOfType"; //$NON-NLS-1$
	static final String OBJECTS_OF_KIND_NAME = "objectsOfKind"; //$NON-NLS-1$
	static final String REMOVE_ELEMENT_NAME = "removeElement"; //$NON-NLS-1$
	
	
	public ModelOperations(AbstractQVTStdlib library) {
		super(library, library.getModelClass());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();		
		EClassifier setOfElement = TypeUtil.resolveSetType(getStdlib().getEnvironment(), getStdlib().getElementType());
		
		return new OperationProvider[] {
			new OwnedOperationProvider(UNSUPPORTED_OPER, "asTransformation", new String[] { "model" }, //$NON-NLS-1$ //$NON-NLS-2$
					getStdlib().getTransformationClass(), getStdlib().getModelClass()),
			new OwnedOperationProvider(COPY, COPY_NAME, oclStdlib.getT()),
			
			createOwnedStaticOperationProvider(CREATE_EMPTY_MODEL, CREATE_EMPTY_MODEL_NAME, null, oclStdlib.getT()),
			
			new OwnedOperationProvider(OBJECTS, OBJECTS_NAME, setOfElement),
			new OwnedOperationProvider(OBJECTS_OF_TYPE, OBJECTS_OF_TYPE_NAME, new String[] { "type" }, //$NON-NLS-1$
					oclStdlib.getSet(), oclStdlib.getOclType()),
			new OwnedOperationProvider(OBJECTS_OF_KIND, OBJECTS_OF_KIND_NAME, new String[] { "type" }, //$NON-NLS-1$
					oclStdlib.getSet(), oclStdlib.getOclType()),
			new OwnedOperationProvider(REMOVE_ELEMENT, REMOVE_ELEMENT_NAME, new String[] { "element" }, //$NON-NLS-1$
					oclStdlib.getOclVoid(), getStdlib().getElementType()),
			new OwnedOperationProvider(ROOT_OBJECTS, ROOT_OBJECTS_NAME, setOfElement),
			
			new OwnedOperationProvider(OBJECTS_OF_KIND, PredefinedType.ALL_INSTANCES_NAME,
					oclStdlib.getSet(), oclStdlib.getOclType())
					.deprecateBy("Model::objectsOfKind(OclType)"), //$NON-NLS-1$
			
		};
	}
	
	
	private static final  CallHandler OBJECTS = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, OBJECTS_NAME));
			}

			return getObjects((ModelInstance) source, null, ElementOperations.FILTER_ALL, evalEnv);
		}
	};

	private static final CallHandler ROOT_OBJECTS = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, ROOT_OBJECTS_NAME));
			}
			
			Set<Object> instances = new LinkedHashSet<Object>();			
			ModelInstance model = (ModelInstance) source;
			ModelParameterExtent modelParam = model.getExtent();			
			instances.addAll(modelParam.getRootObjects());
			return instances;
		}
	};

	private static final CallHandler OBJECTS_OF_TYPE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, OBJECTS_OF_TYPE_NAME));
			}

			EClassifier type = ElementOperations.getTypeFilterArg(args);
			return getObjects((ModelInstance) source, type, ElementOperations.FILTER_OF_TYPE, evalEnv);
		}
	};
	
	private static final CallHandler OBJECTS_OF_KIND = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, OBJECTS_OF_KIND_NAME));
			}

			EClassifier type = ElementOperations.getTypeFilterArg(args);
			return getObjects((ModelInstance) source, type, ElementOperations.FILTER_OF_KIND, evalEnv);
		}
	};
	
	private static final CallHandler REMOVE_ELEMENT = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, REMOVE_ELEMENT_NAME));
			}
			ModelInstance model = (ModelInstance) source;
			ModelParameterExtent modelParam = model.getExtent();
			Object elementObject = args[0];
			if(elementObject instanceof EObject) {
				modelParam.removeElement((EObject) elementObject);
			}
	        return null;
		}
	};

	private static final CallHandler COPY = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, COPY_NAME));
			}
			ModelInstance model = (ModelInstance) source;
	        ModelInstance modelCopy = model.copy();
	        evalEnv.addModelExtent(modelCopy.getExtent());
	        return modelCopy;
		}
	};
		
	private static final CallHandler CREATE_EMPTY_MODEL = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof ModelInstance) {
				source = ((ModelInstance) source).eClass();
			}
			if(source instanceof ModelType == false) {
				throw new IllegalArgumentException(NLS.bind(Messages.InvalidSourceForOperationCall, CREATE_EMPTY_MODEL_NAME));
			}
			
			ModelType modelType = (ModelType) source;
			// TODO - user typed factory, beware of special handling with top-level model types 
			// as these do not have 
			EObject modelObj = modelType.getEPackage().getEFactoryInstance().create(modelType);
			assert modelObj instanceof ModelInstance : "model must implement ModelInstance interface"; //$NON-NLS-1$
	        return modelObj;
		}
	};
	
	private static Object getObjects(ModelInstance model, EClassifier type, final int filterFlag, QvtOperationalEvaluationEnv evalEnv) {
        Set<Object> instances = new LinkedHashSet<Object>();	       
		ModelParameterExtent modelParam = model.getExtent();	        
		for (Object obj : modelParam.getAllObjects()) {
			boolean accept = filterFlag == ElementOperations.FILTER_ALL;
			if(ElementOperations.FILTER_OF_KIND == filterFlag) {
				accept = evalEnv.isKindOf(obj, type);
			} else if(ElementOperations.FILTER_OF_TYPE == filterFlag) {
				accept = evalEnv.isTypeOf(obj, type);					
			}
			if(accept) {
				instances.add(obj);
			}
        }	        
        return instances;
	}

}
