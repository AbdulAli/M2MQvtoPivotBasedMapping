/*******************************************************************************
 * Copyright (c) 2009, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.env.IVirtualOperationTable;
import org.eclipse.m2m.internal.qvt.oml.ast.env.VirtualTable;
import org.eclipse.m2m.internal.qvt.oml.ast.env.VirtualTableAdapter;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstanceFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.CatchExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.InstantiationExp;
import org.eclipse.ocl.ecore.OperationCallExp;
import org.eclipse.ocl.ecore.Variable;

/**
 * This helper class solves the problem of serialization/deserialization of 
 * executable QVTo XMI, with AST extensions like property init expressions.
 * 
 * FIXME - consider elimination of some of the adapters and use only annotations
 * so we can stick to plain serialization. 
 */
public class ExecutableXMIHelper {
	
	private static final String SOURCE_URI_BASE = QvtOperationalParserUtil.QVT_NAMESPACE_URI;
	private static final String VIRTUAL_TABLE_SOURCE = SOURCE_URI_BASE + "/VTable"; //$NON-NLS-1$
	private static final String CONSTRUCTOR_SOURCE = SOURCE_URI_BASE + "/constructor"; //$NON-NLS-1$
	private static final String STATIC_SOURCE = SOURCE_URI_BASE + "/static"; //$NON-NLS-1$
	private static final String OPERATION_CALL_SOURCE = SOURCE_URI_BASE + "/opCall"; //$NON-NLS-1$
	private static final String OPERATION_CALL_DETAILS_OPCODE = "opCode"; //$NON-NLS-1$
	private static final String CATCH_VAR_SOURCE = SOURCE_URI_BASE + "/catchVar"; //$NON-NLS-1$

	public static void fixResourceOnLoad(Resource resource) {
		TreeIterator<EObject> it = resource.getAllContents();
		while(it.hasNext()) {
			EObject nextObj = it.next();
			if(nextObj instanceof Module) {
				Module module = (Module) nextObj;
				module.setEFactoryInstance(new ModuleInstanceFactory());
			} else if(nextObj instanceof EOperation) {
				loadVTOperFromAnnotation((EOperation)nextObj);
			} else if(nextObj instanceof InstantiationExp) {
				loadConstructorReferenceFromAnnotation((InstantiationExp) nextObj);
			} else if(nextObj instanceof EPackage) {
				EPackage ePackage = (EPackage)nextObj;
				if(IntermediateClassFactory.isIntermediatePackage(ePackage)) {					
					new IntermediateClassFactory(ePackage);
				}
			} else if(nextObj instanceof EStructuralFeature) {
				loadStaticFeatureFromAnnotation((EStructuralFeature) nextObj);
			} else if(nextObj instanceof OperationCallExp) {
				loadOperationCallDetails((OperationCallExp) nextObj);
			} else if(nextObj instanceof CatchExp) {
				loadCatchDetails((CatchExp) nextObj);
			}
		}
	}
	
	public static void fixResourceOnSave(Resource next) {
		TreeIterator<EObject> it = next.getAllContents();
		while(it.hasNext()) {
			EObject nextObj = it.next();
			if(nextObj instanceof EOperation) {
				saveVTOperTable2Annotation((EOperation)nextObj);
			} else if(nextObj instanceof InstantiationExp) {
				saveConstructorReference((InstantiationExp) nextObj);
			} else if(nextObj instanceof EStructuralFeature) {
				saveStaticFeature((EStructuralFeature) nextObj);
			} else if(nextObj instanceof OperationCallExp) {
				saveOperationCallDetails((OperationCallExp) nextObj);
			} else if(nextObj instanceof CatchExp) {
				saveCatchDetails((CatchExp) nextObj);
			}
		}
	}	
	
	private static void loadConstructorReferenceFromAnnotation(InstantiationExp instantiation) {
		EAnnotation annotation = instantiation.getEAnnotation(CONSTRUCTOR_SOURCE);
		if(annotation != null && !annotation.getReferences().isEmpty()) {
			EObject referencedObj = annotation.getReferences().get(0);
			if(referencedObj instanceof Constructor) {
				instantiation.eAdapters().add(new ConstructorOperationAdapter((Constructor) referencedObj));
			}
		}
	}

	private static void loadVTOperFromAnnotation(EOperation operation) {
		EAnnotation annotation = operation.getEAnnotation(VIRTUAL_TABLE_SOURCE);
		if(annotation != null) {
			VirtualTable vt = VirtualTableAdapter.getAdapter(operation, true).getVirtualTable();
			for(EObject next : annotation.getReferences()) {
				if(next instanceof EOperation) {
					vt.addOperation((EOperation)next);
				}
			}
		}
	}
	
	private static void saveVTOperTable2Annotation(EOperation operation) {
		IVirtualOperationTable virtualTable = IVirtualOperationTable.Access.INSTANCE.getVirtualTable(operation);
		if(virtualTable instanceof VirtualTable) {
			VirtualTable vt = (VirtualTable) virtualTable;
			Collection<EOperation> operations = vt.getOperations();
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(VIRTUAL_TABLE_SOURCE);
			for (EOperation next : operations) {
				annotation.getReferences().add(next);
			}
			
			operation.getEAnnotations().add(annotation);
		}
	}	

	private static void saveConstructorReference(InstantiationExp instantiation) {
		Adapter adapter = EcoreUtil.getAdapter(instantiation.eAdapters(), ConstructorOperationAdapter.class);
		if (adapter != null) {
			ConstructorOperationAdapter constructorAdapter = (ConstructorOperationAdapter) adapter;
			Constructor referredConstructor = constructorAdapter.getReferredConstructor();
			if(referredConstructor != null) {
	    		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
	    		annotation.setSource(CONSTRUCTOR_SOURCE);
	    		annotation.getReferences().add(referredConstructor);
	    		instantiation.getEAnnotations().add(annotation);
			}
		}
	}
	
	private static void saveStaticFeature(EStructuralFeature feature) {
		if(IntermediateClassFactory.isFeatureStatic(feature)) {
    		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    		annotation.setSource(STATIC_SOURCE);
    		feature.getEAnnotations().add(annotation);
		}
	}
	
	private static void loadStaticFeatureFromAnnotation(EStructuralFeature feature) {
		EAnnotation annotation = feature.getEAnnotation(STATIC_SOURCE);
		if (annotation != null) {
			IntermediateClassFactory.markFeatureAsStatic(feature);
		}
	}
	
	private static void saveOperationCallDetails(OperationCallExp opCallExp) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(OPERATION_CALL_SOURCE);
		annotation.getDetails().put(OPERATION_CALL_DETAILS_OPCODE, String.valueOf(opCallExp.getOperationCode()));
		opCallExp.getEAnnotations().add(annotation);
	}

	private static void loadOperationCallDetails(OperationCallExp opCallExp) {
		EAnnotation annotation = opCallExp.getEAnnotation(OPERATION_CALL_SOURCE);
		if (annotation == null) {
			return;
		}
		String opCodeString = annotation.getDetails().get(OPERATION_CALL_DETAILS_OPCODE);
		if (opCodeString == null) {
			return;
		}
		try {
			opCallExp.setOperationCode(Integer.valueOf(opCodeString));
		}
		catch (NumberFormatException e) {
		}
	}

	private static void saveCatchDetails(CatchExp catchExp) {
		Variable catchVar = ASTBindingHelper.getCatchVariable(catchExp);
		if (catchVar == null) {
			return;
		}
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(CATCH_VAR_SOURCE);
		annotation.getContents().add(ASTBindingHelper.getCatchVariable(catchExp));
		catchExp.getEAnnotations().add(annotation);
	}

	private static void loadCatchDetails(CatchExp catchExp) {
		EAnnotation annotation = catchExp.getEAnnotation(CATCH_VAR_SOURCE);
		if (annotation == null) {
			return;
		}
		if (annotation.getContents().isEmpty() || false == annotation.getContents().get(0) instanceof Variable) {
			return;
		}
		Variable catchVar = (Variable) annotation.getContents().get(0);
		annotation.getContents().clear();
		ASTBindingHelper.setCatchVariable(catchExp, catchVar);
	}

}
