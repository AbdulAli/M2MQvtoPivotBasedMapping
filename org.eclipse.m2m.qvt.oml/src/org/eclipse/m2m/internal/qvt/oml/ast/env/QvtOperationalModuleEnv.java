/*******************************************************************************
 * Copyright (c) 2008, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 302594, 310991
 *     Alex Paperno - bugs 416584
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.ocl.ecore.EcoreFactory;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.lpg.ProblemHandler;
import org.eclipse.ocl.options.ProblemOption;

public class QvtOperationalModuleEnv extends QvtOperationalEnv {

	private Module myContextModule;
	private QvtOperationalFileEnv myFileParent;
	
	QvtOperationalModuleEnv(EPackage.Registry registry, Resource resource) {
		super(registry, resource != null ?
				resource : new EcoreResourceFactoryImpl().createResource(URI.createURI("qvto:/module.env"))); //$NON-NLS-1$

        // Eliminate parsing warning on "" occurrences, used in model types URIs, etc.
        // TODO - solve in QVT grammar
        setOption(ProblemOption.ELEMENT_NAME_QUOTE_ESCAPE, ProblemHandler.Severity.OK);
        setOption(ProblemOption.STRING_CASE_CONVERSION, ProblemHandler.Severity.OK);
        setOption(ProblemOption.STRING_SINGLE_QUOTE_ESCAPE, ProblemHandler.Severity.OK);
        setOption(ProblemOption.CONCEPTUAL_OPERATION_NAME, ProblemHandler.Severity.OK);
	}
	
	@Override
	protected void addedVariable(String name, Variable<EClassifier, EParameter> elem, boolean isExplicit) {
        super.addedVariable(name, elem, isExplicit);
		if(name != null && name.equals(THIS)) {
			if(myContextModule != null) {
				myContextModule.getOwnedVariable().add((org.eclipse.ocl.ecore.Variable)elem);
			}
		}
	}
	
	@Override
	protected void removedVariable(String name, Variable<EClassifier, EParameter> variable, boolean isExplicit) {
		super.removedVariable(name, variable, isExplicit);
		if(name != null && name.equals(THIS)) {
			if(myContextModule != null) {
				myContextModule.getOwnedVariable().remove((org.eclipse.ocl.ecore.Variable)variable);
			}
		}
	}
			    
    public void setContextModule(Module module) {
    	if (myContextModule != module) {
	    	deleteElement(THIS);
	    	
			myContextModule = null;    	
	
	    	if(module != null) {
	    		myContextModule = module;
	    		org.eclipse.ocl.ecore.Variable thisVar = EcoreFactory.eINSTANCE.createVariable();
	    		thisVar.setName(THIS);
	    		thisVar.setType(module);    		
	            addElement(THIS, thisVar, false);    		

	        	// confine module in resource
	        	getTypeResolver().getResource().getContents().add(module);
	    	}
    	}
    	
    	if(module instanceof OperationalTransformation) {
    		registerModelParameters((OperationalTransformation)module);
    	}
    }
    
    public ModelParameter lookupModelParameter(String name, DirectionKind directionKind) {
		if(name == null) {
			return null;
		}
		
		List<ModelParameter> myModelParameters = getModelParameters();
		for (ModelParameter modelParam : myModelParameters) {
			if (directionKind != DirectionKind.IN) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}

			String nextParamName = modelParam.getName();
			if (nextParamName != null) {
				if (nextParamName.equals(name)) {
					return modelParam;
				}
			}
		}
		
		return null;
	}
    
	/**
	 * Get names of all available extents of given direction kind in this
	 * environments.
	 * 
	 * @param directionKind
	 *            filtering condition to be satisfied by returned extents or
	 *            <code>null</code> if all kinds are acceptable
	 * @return list of corresponding model parameter names
	 */
	public List<String> getAllExtentNames(DirectionKind directionKind) {
		List<ModelParameter> modelParameters = getModelParameters();
		List<String> result = new ArrayList<String>(modelParameters.size());
		for (ModelParameter modelParam : modelParameters) {
			if (directionKind != DirectionKind.IN) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}

			String nextParam = modelParam.getName();
			if(nextParam != null && nextParam.length() > 0) {
				result.add(nextParam);
			}
		}
		
		return Collections.unmodifiableList(result);
	}
		
	public ModelParameter resolveModelParameter(EClassifier type, DirectionKind directionKind) {
		if (!isMayBelongToExtent(type)) {
			return null;
		}
		return findModelParameter(type, directionKind, getModelParameters());
	}
	
	public ModelParameter resolveModelParameterDeprecated(EClassifier type, DirectionKind directionKind) {
		if (!isMayBelongToExtent(type)) {
			return null;
		}
		return findModelParameterDeprecated(type, directionKind, getModelParameters());
	}
	
	static ModelParameter findModelParameter(EClassifier type, DirectionKind directionKind, 
			Collection<ModelParameter> modelParameters) {
		EObject rootContainer = EcoreUtil.getRootContainer(type);
		
		// lookup explicit extent 
		for (ModelParameter modelParam : modelParameters) {
			if (directionKind != DirectionKind.IN) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}
			
			ModelType modelType = QvtOperationalUtil.getModelType(modelParam);
			if(modelType != null) {
				List<EPackage> metamodels = modelType.getMetamodel();
				if (!metamodels.isEmpty() && rootContainer == metamodels.get(0)) {
					return modelParam;
				}
			}
		}
		
		// lookup implicit extent 
		for (ModelParameter modelParam : modelParameters) {
			if (directionKind != DirectionKind.IN) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}
			return modelParam;
		}
		
		return null;
	}    
    
	static ModelParameter findModelParameterDeprecated(EClassifier type, DirectionKind directionKind, 
			Collection<ModelParameter> modelParameters) {
		EObject rootContainer = EcoreUtil.getRootContainer(type);
		
		// lookup explicit extent 
		for (ModelParameter modelParam : modelParameters) {
			if (directionKind == DirectionKind.OUT) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}
			
			ModelType modelType = QvtOperationalUtil.getModelType(modelParam);
			if(modelType != null) {
				List<EPackage> metamodels = modelType.getMetamodel();
				if (!metamodels.isEmpty() && rootContainer == metamodels.get(0)) {
					return modelParam;
				}
			}
		}
		
		// lookup implicit extent 
		for (ModelParameter modelParam : modelParameters) {
			if (directionKind == DirectionKind.OUT) {
				if (modelParam.getKind() == DirectionKind.IN) {
					continue;
				}
			}
			return modelParam;
		}
		
		return null;
	}    
    
    
	@Override
	public final Module getModuleContextType() {
		return myContextModule;
	}
    
	@Override
    public String toString() {
    	if(myContextModule != null && myContextModule.getName() != null) {
    		return "Module env: <" + myContextModule.getName() + ">@" + Integer.toHexString(System.identityHashCode(this)); //$NON-NLS-1$ //$NON-NLS-2$
    	}
    	return super.toString();
    }
    
	private List<ModelParameter> getModelParameters() {
		if(getModuleContextType() instanceof OperationalTransformation == false) {
			return Collections.emptyList();
		}
		OperationalTransformation transformation = (OperationalTransformation) getModuleContextType();
		List<ModelParameter> myModelParameters = transformation.getModelParameter();
		return myModelParameters;
	}    
    
	public boolean isMayBelongToExtent(EClassifier myType) {
		return myType != null 
			&& !IntermediateClassFactory.isIntermediateClass(myType)
			&& getOCLStandardLibrary().getOclVoid() != myType
			&& getOCLStandardLibrary().getOclInvalid() != myType;
	}
	
    
	private void registerModelParameters(OperationalTransformation module) {
		for (ModelParameter modelParam : module.getModelParameter()) {
			addElement(modelParam.getName(), modelParam, true);
		}
	}
	
	public QvtOperationalFileEnv getFileParent() {
		return myFileParent;
	}

	public void setFileParent(QvtOperationalFileEnv parentEnv) {
		myFileParent = parentEnv;		
	}
	
}
