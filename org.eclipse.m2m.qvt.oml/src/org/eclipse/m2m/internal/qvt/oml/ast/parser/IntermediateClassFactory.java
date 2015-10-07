/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 388801
 *     Alex Paperno - bug 419299
 *******************************************************************************/

package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.cst.adapters.AbstractGenericAdapter;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.EvaluationUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTStackTraceElement;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitor;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtRuntimeException;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.model.ExceptionInstance;
import org.eclipse.ocl.expressions.OCLExpression;

/**
 * @author sboyko
 * 
 */
public class IntermediateClassFactory extends EFactoryImpl {

	public static IntermediateClassFactory getFactory(Module module) {
		for (EPackage subPackage : module.getESubpackages()) {
			// should be fast we will always have a single and the only 
			// intermediate package
			if(isIntermediatePackage(subPackage)) {
				EFactory eFactory = subPackage.getEFactoryInstance();
				if(eFactory.getClass().equals(IntermediateClassFactory.class)) {
					return (IntermediateClassFactory) eFactory;
				}
			}
		}
		return null;
	}

	public IntermediateClassFactory(EPackage intermediatePackage) {
		if(isIntermediatePackage(intermediatePackage) == false) {
			throw new IllegalArgumentException("Requires intermediate package"); //$NON-NLS-1$
		}
		myModule = (Module) intermediatePackage.getESuperPackage();
		myIntermediatePackage = intermediatePackage;
		myIntermediatePackage.setEFactoryInstance(this);

		EClassifier intermModuleType = myModule.getEClassifier(INTERMEDIATE_MODELTYPE_NAME);
		if(intermModuleType instanceof ModelType == false) {
			throw new IllegalArgumentException("Intermediate model type must exist"); //$NON-NLS-1$
		}
		
		myIntermediateModelType = (ModelType) intermModuleType;		
	}

	public IntermediateClassFactory(Module module) {
		super();
		myModule = module;

		/*
		 * 8.2.1.3 Module The model type package is also named '_INTERMEDIATE'.
		 * This package is nested by the transformation (by means of the
		 * inherited Package::nestedPackage property).
		 */
		myIntermediatePackage = EcoreFactory.eINSTANCE.createEPackage();
		myIntermediatePackage.setEFactoryInstance(this);

		myIntermediatePackage.setName(INTERMEDIATE_MODELTYPE_NAME);
		myIntermediatePackage.setNsPrefix(INTERMEDIATE_PACKAGE_NAME);
		myIntermediatePackage.setNsURI(ExpressionsPackage.eNS_URI
				+ "/" + INTERMEDIATE_MODELTYPE_NAME); //$NON-NLS-1$
		module.getESubpackages().add(myIntermediatePackage);

		/*
		 * 8.2.1.3 Module If the module contains the declaration of intermediate
		 * classes (see OperationalTransformation::intermediateClass definition)
		 * a model type named '_INTERMEDIATE' is automatically defined and
		 * inserted in the list of owned types.
		 */
		myIntermediateModelType = ExpressionsFactory.eINSTANCE
				.createModelType();
		myIntermediateModelType.setName(INTERMEDIATE_MODELTYPE_NAME);
		myIntermediateModelType.getMetamodel().add(myIntermediatePackage); // metamodel:
																			// Package
																			// [1..*]
																			// {ordered}
		module.getUsedModelType().add(myIntermediateModelType); // usedModelType
																// : ModelType
																// [0..*]
																// {ordered}
		module.getEClassifiers().add(myIntermediateModelType); // /ownedType :
																// Type [0..*]
																// (from
																// Package)
																// {composes,ordered}

		myIntermediateModelType.getMetamodel().add(myIntermediatePackage);
		// No need to use a metamodel adapter, the package is referenced by the
		// modeltype directly
		// ModelTypeMetamodelsAdapter.addMetamodel(myIntermediateModelType,
		// myIntermediatePackage);
	}

	@Override
	public EObject create(EClass class_) {
		// return super.create(class_);

		if (myClassifierInitializations.get(class_) == null) {
			cacheClassifierInitExp(class_);
		}

		if (EmfUtil.isAssignableFrom(QvtOperationalStdLibrary.INSTANCE.getExceptionClass(), class_)) {
			return new ExceptionClassInstance(class_);
		}
		else {
			return new IntermediateClassInstance(class_);
		}
	}

	public EClass createIntermediateClassifier() {
		EClass eClassifier = EcoreFactory.eINSTANCE.createEClass();

		/*
		 * 8.2.1.3 Module Associations /ownedType : Type [0..] (from Package)
		 * {composes,ordered}
		 * 
		 * All the types being defined by this module. Specifically this
		 * includes the model types, locally defined classes, and any composite
		 * type used to define the type of a variable or a parameter - for
		 * instance a 'Set(MyMetaclass)' user-defined datatype.
		 */
		// myModule.getEClassifiers().add(eClassifier);
		myIntermediatePackage.getEClassifiers().add(eClassifier);

		/*
		 * 8.2.1.1 OperationalTransformation Associations intermediateClass :
		 * Class [] {ordered}
		 * 
		 * The classes that are defined explicitly by the transformation writer
		 * to contain structured intermediate data used for the purpose of the
		 * transformation. These intermediate classes are to be distinguished
		 * from the trace classes that are implicitly and automatically derived
		 * from the relations. Instances of intermediate classes do not survive
		 * the execution of the transformation, except for ensuring trace
		 * persistence.
		 */
		if (myModule instanceof OperationalTransformation) {
			((OperationalTransformation) myModule).getIntermediateClass().add(
					eClassifier);
		}

		return eClassifier;
	}

	public ModelType getIntermediateModelType() {
		return myIntermediateModelType;
	}
	
	public static boolean isIntermediateModelType(ModelType modelType) {
		return modelType.eContainer() instanceof OperationalTransformation
				&& INTERMEDIATE_MODELTYPE_NAME.equals(modelType.getName());
	}

	public static boolean isIntermediateClass(EClassifier class_) {
		if (class_ == null) {
			return false;
		}

		EPackage ePackage = class_.getEPackage();
		return ePackage != null && isIntermediatePackage(ePackage);
	}
	
	public static boolean isIntermediatePackage(EPackage ePackage) {
		return INTERMEDIATE_MODELTYPE_NAME.equals(ePackage.getName())
				&& ePackage.getESuperPackage() instanceof Module;
	}

	public void cacheClassifierInitExp(EClass clazz) {
		Map<EStructuralFeature, OCLExpression<EClassifier>> clsFeatures = myClassifierInitializations
				.get(clazz);
		if (clsFeatures == null) {
			// Remark: follow the declaration order during feature
			// initialization
			clsFeatures = new LinkedHashMap<EStructuralFeature, OCLExpression<EClassifier>>(
					2);
			myClassifierInitializations.put(clazz, clsFeatures);
		}

		for (EStructuralFeature next : clazz.getEAllStructuralFeatures()) {
			OCLExpression<EClassifier> initExp = QvtOperationalParserUtil
					.getInitExpression(next);
			if (initExp != null) {
				clsFeatures.put(next, initExp);
			}
		}
	}

	public void doInstancePropertyInit(Object instance,
			QvtOperationalEvaluationVisitor evalVisitor) {
		if (false == instance instanceof EObject) {
			return;
		}
		EObject eInstance = (EObject) instance;

			EClass intermediateClass = eInstance.eClass();
			
			Map<EStructuralFeature, OCLExpression<EClassifier>> clsFeatures = myClassifierInitializations
					.get(intermediateClass);
			if (clsFeatures == null) {
				return;
			}
			for (EStructuralFeature eFeature : intermediateClass.getEAllStructuralFeatures()) {
				IntermediateStaticFieldAdapter adapter = (IntermediateStaticFieldAdapter) EcoreUtil
						.getAdapter(eFeature.eAdapters(),
								IntermediateStaticFieldAdapter.class);
				if (adapter != null && adapter.isInitialized()) {
					continue;
				}
	
				OCLExpression<EClassifier> expression = clsFeatures
						.get(eFeature);
				
				Object evalResult = expression != null ? evalVisitor.visitExpression(expression) : null;
								
				if (evalResult == null) {
					// no init expression specified for a single-valued feature, or init expression evaluated to null 
					EClassifier featureType = evalVisitor.getEnvironment().getUMLReflection().getOCLType(eFeature);
					evalResult = EvaluationUtil.createInitialValue(featureType, evalVisitor.getEnvironment().getOCLStandardLibrary(),
							evalVisitor.getEvaluationEnvironment());
				}
	
				// temporary switch off read-only property
				boolean isChangeable = eFeature.isChangeable();
				eFeature.setChangeable(true);
				
				boolean isUndefined = QvtOperationalUtil.isUndefined(evalResult, evalVisitor.getEvaluationEnvironment());
				evalVisitor.getOperationalEvaluationEnv().callSetter(eInstance, eFeature, evalResult, isUndefined, true);
				
				eFeature.setChangeable(isChangeable);
			}
	}

	public static void markFeatureAsStatic(EStructuralFeature eFeature) {
		IntermediateStaticFieldAdapter adapter = (IntermediateStaticFieldAdapter) EcoreUtil
				.getAdapter(eFeature.eAdapters(),
						IntermediateStaticFieldAdapter.class);
		if (adapter == null) {
			adapter = new IntermediateStaticFieldAdapter();
			eFeature.eAdapters().add(adapter);
		}
	}

	public static boolean isFeatureStatic(EStructuralFeature eFeature) {
		return EcoreUtil.getAdapter(eFeature.eAdapters(),
				IntermediateStaticFieldAdapter.class) != null;
	}

	private static class IntermediateClassInstance extends DynamicEObjectImpl {
		IntermediateClassInstance(EClass eClass) {
			super(eClass);
		}

		@Override
		public Object eGet(EStructuralFeature feature, boolean resolve,
				boolean coreType) {
			IntermediateStaticFieldAdapter adapter = (IntermediateStaticFieldAdapter) EcoreUtil
					.getAdapter(feature.eAdapters(),
							IntermediateStaticFieldAdapter.class);
			if (adapter != null) {
				return adapter.eGet(feature);
			}

			return super.eGet(feature, resolve, coreType);
		}

		@Override
		public void eSet(EStructuralFeature feature, Object newValue) {
			IntermediateStaticFieldAdapter adapter = (IntermediateStaticFieldAdapter) EcoreUtil
					.getAdapter(feature.eAdapters(),
							IntermediateStaticFieldAdapter.class);
			if (adapter != null) {
				adapter.eSet(feature, newValue);
			}

			super.eSet(feature, newValue);
		}

		@Override
		public String toString() {
			return eClass().getName();
		}
	}

	public static class ExceptionClassInstance extends IntermediateClassInstance implements ExceptionInstance{
		private String argument;
		private List<QVTStackTraceElement> stackElements;		
		
		public ExceptionClassInstance(EClass eClass) {
			super(eClass);
		}

		public String getArgument() {		
			return argument;
		}
		
		public void setArgument(String argumentNew) {		
			argument = argumentNew;
		}

		public void setStackElements(List<QVTStackTraceElement> stackElementsNew) {
			stackElements = stackElementsNew;
		}
		
		public List<QVTStackTraceElement> getStackElements() {
			return (stackElements != null) ? stackElements : Collections.<QVTStackTraceElement>emptyList();
		}
		
		@Override
		public String toString() {
    		StringWriter contents = new StringWriter();
    		contents.write(eClass().getName());
			if(getArgument() != null) {
				contents.write(" : "); //$NON-NLS-1$
				contents.write(argument);
				contents.write(System.getProperty("line.separator")); //$NON-NLS-1$
			}
			PrintWriter pw = new PrintWriter(contents);
			QvtRuntimeException.printQvtStackTrace(pw, getStackElements());
			return contents.toString(); 
		}
	}

	
	private static class IntermediateStaticFieldAdapter extends
			AbstractGenericAdapter<IntermediateStaticFieldAdapter> {

		private IntermediateStaticFieldAdapter() {
			super();
		}

		public Object eGet(EStructuralFeature eFeature) {
			return myValue;
		}

		public void eSet(EStructuralFeature eFeature, Object value) {
			myValue = value;
			myIsInitialized = true;
		}

		public boolean isInitialized() {
			return myIsInitialized;
		}

		public boolean isAdapterForType(Object type) {
			return IntermediateStaticFieldAdapter.class == type;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof IntermediateStaticFieldAdapter;
		}

		@Override
		public int hashCode() {
			return IntermediateStaticFieldAdapter.class.hashCode();
		}

		private Object myValue = null;
		private boolean myIsInitialized = false;

	}

	private final EPackage myIntermediatePackage;

	private final Module myModule;

	private final ModelType myIntermediateModelType;

	private final Map<EClass, Map<EStructuralFeature, OCLExpression<EClassifier>>> myClassifierInitializations = new HashMap<EClass, Map<EStructuralFeature, OCLExpression<EClassifier>>>();
	
	private static final String INTERMEDIATE_MODELTYPE_NAME = "_INTERMEDIATE"; //$NON-NLS-1$

	private static final String INTERMEDIATE_PACKAGE_NAME = INTERMEDIATE_MODELTYPE_NAME.toLowerCase();

}
