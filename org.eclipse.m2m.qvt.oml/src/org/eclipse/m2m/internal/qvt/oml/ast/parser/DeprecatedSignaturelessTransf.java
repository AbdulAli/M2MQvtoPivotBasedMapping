/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtEnvironmentBase;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;

public class DeprecatedSignaturelessTransf {

	
	public static void patchModule(Module module) {
		ImperativeOperation mainOperation = QvtOperationalParserUtil.getMainOperation(module);
		if(mainOperation != null && module instanceof OperationalTransformation) {
			OperationalTransformation transformation = (OperationalTransformation) module;
			if(!transformation.getModelParameter().isEmpty()) {
				return;
			}

			List<EParameter> parameters = new ArrayList<EParameter>();
			parameters.addAll(mainOperation.getEParameters());
			parameters.addAll(mainOperation.getResult());
			
			for (EParameter eParameter : parameters) {
				ModelParameter modelParam = createModelParam(eParameter);				
				transformation.getModelParameter().add(modelParam);
				EClassifier type = modelParam.getEType();
				if(type instanceof ModelType) {
					ModelType modelType = (ModelType) type;
					transformation.getEClassifiers().add(modelType);					
					transformation.getUsedModelType().add(modelType);					
				}
			}
		}
	}

	private static ModelParameter createModelParam(EParameter param) {
		ModelParameter modelParam = ExpressionsFactory.eINSTANCE.createModelParameter();
		modelParam.setName(QvtEnvironmentBase.GENERATED_NAME_SPECIAL_PREFIX + param.getName());
		modelParam.setType(createModelType(param.getEType()));

		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QvtOperationalParserUtil.QVT_AUTOGEN_MODELPARAM_EXPRESSION_URI);
		modelParam.getEAnnotations().add(annotation);
		
		if(param instanceof VarParameter) {
			VarParameter varParameter = (VarParameter) param;
			modelParam.setKind(varParameter.getKind());
			if(varParameter instanceof MappingParameter) {
				MappingParameter mappingParameter = (MappingParameter) varParameter;
				if(mappingParameter.getExtent() == null) {
					mappingParameter.setExtent(modelParam);
				}
			}
		} else {
			modelParam.setKind(DirectionKind.IN);
		}
		
		return modelParam;
	}
	
	private static ModelType createModelType(EClassifier usedType) {
		ModelType modelType = ExpressionsFactory.eINSTANCE.createModelType();
		modelType.setName("<modeltype>"); //$NON-NLS-1$
		
		if(usedType != null) {
			EPackage ePackage = usedType.getEPackage();			
			if(ePackage != null) {
				modelType.setName(String.valueOf(ePackage.getName()));
				modelType.getMetamodel().add(ePackage);
			}
		}
		
		return modelType;
	}	
}
