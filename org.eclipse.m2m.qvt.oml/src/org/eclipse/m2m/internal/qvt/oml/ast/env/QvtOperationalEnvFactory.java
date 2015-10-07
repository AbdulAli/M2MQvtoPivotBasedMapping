/*******************************************************************************
 * Copyright (c) 2007, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Alex Paperno - bugs 416584
 *     Christine Gerpheide - bug 432969
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.evaluator.InternalEvaluator;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtGenericVisitorDecorator;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;

/**
 * A factory class for operational QVT environments creation.  
 */
public class QvtOperationalEnvFactory extends EcoreEnvironmentFactory {

	/**
     * A shared instance of the QV environment factory using the global package registry 
     * for creating environments.
     * <p>
     * FIXME - Using a copy of the global registry until it's ensured that QVT environments do not 
     * store into its package registry imported metamodels, so other EMF clients are not affected.     
	 */	
	public static final QvtOperationalEnvFactory INSTANCE = new QvtOperationalEnvFactory();
		
	/* TODO - Do we need a default constructor? */
	public QvtOperationalEnvFactory() {
		super(copyPackageRegistry(EPackage.Registry.INSTANCE));
	}

	/**
	 * Initializes me with an <code>EPackage.Registry</code> that the
     * environments I create will use to look up packages.
     * 
     * @param reg my package registry (must not be <code>null</code>)
	 */
	public QvtOperationalEnvFactory(Registry reg) {
		super(reg);
	}
	
	@Override
	public QvtOperationalEnv createEnvironment() {
		QvtOperationalEnv env = new QvtOperationalEnv(getEPackageRegistry(), null);
		env.setFactory(this);
		return env;
	}

	public QvtOperationalFileEnv createEnvironment(final URI uri) {
		QvtOperationalFileEnv env = new QvtOperationalFileEnv(uri, getEPackageRegistry());
		env.setFactory(this);
		return env;
	}
	
	@Override
    public QvtOperationalEnv createEnvironment(Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> parent) {
        if (!(parent instanceof QvtOperationalEnv)) {
            throw new IllegalArgumentException(
                "Parent environment must be a QvtOperationalEnv: " + parent); //$NON-NLS-1$
        }
        return createEnvironment((QvtOperationalEnv) parent);
    }

    public QvtOperationalEnv createEnvironment(final QvtOperationalEnv parent) {
		QvtOperationalEnv env = new QvtOperationalEnv(parent);
		env.setFactory(this);
		return env;
	}
	
	public QvtOperationalModuleEnv createModuleEnvironment(final Module module) {		
		QvtOperationalModuleEnv env = new QvtOperationalModuleEnv(getEPackageRegistry(), module.eResource());
		env.setFactory(this);		
		env.setContextModule(module);
		QvtOperationalStdLibrary.INSTANCE.importTo(env);		
		return env;
	}

	public QvtOperationalModuleEnv createModuleEnvironment(final Module module, QvtOperationalFileEnv parentEnv) {
		QvtOperationalModuleEnv moduleEnv = new QvtOperationalModuleEnv(parentEnv.getEPackageRegistry(), module.eResource());
		moduleEnv.setFactory(this);		
		moduleEnv.setContextModule(module);
		QvtOperationalStdLibrary.INSTANCE.importTo(moduleEnv);
		parentEnv.addInnerEnvironment(moduleEnv);
		moduleEnv.setFileParent(parentEnv);
		moduleEnv.setASTNodeToCSTNodeMap(parentEnv.getASTNodeToCSTNodeMap());
		return moduleEnv;
	}
	
	
	@Override
	public QvtOperationalEnv createOperationContext(
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> parent,
			EOperation operation) {
		QvtOperationalEnv newEnv = createEnvironment(parent);
		QvtOperationalEnv parentEnv = (QvtOperationalEnv) parent;
		newEnv.setASTNodeToCSTNodeMap(parentEnv.getASTNodeToCSTNodeMap());
		
		newEnv.setContextOperation(operation);		
		return newEnv;
	}
	
	@Override
	public EvaluationEnvironment<EClassifier, EOperation, EStructuralFeature, EClass, EObject> createEvaluationEnvironment(
			EvaluationEnvironment<EClassifier, EOperation, EStructuralFeature, EClass, EObject> parent) {

		if(parent instanceof QvtOperationalEvaluationEnv) {
			QvtOperationalEvaluationEnv qvtParentEnv = (QvtOperationalEvaluationEnv) parent;
			return new QvtOperationalEvaluationEnv(qvtParentEnv.getContext(), qvtParentEnv);
		}

		return super.createEvaluationEnvironment(parent);
	}
	
	public QvtOperationalEvaluationEnv createEvaluationEnvironment(IContext context, QvtOperationalEvaluationEnv parent) {
		return new QvtOperationalEvaluationEnv(context, parent);
	}
	
	@Override
	public EvaluationVisitor<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> createEvaluationVisitor(
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			EvaluationEnvironment<EClassifier, EOperation, EStructuralFeature, EClass, EObject> evalEnv,
			Map<? extends EClass, ? extends Set<? extends EObject>> extentMap) {
		
		if((env instanceof QvtOperationalEnv == false) || (evalEnv instanceof QvtOperationalEvaluationEnv == false)) {
			return super.createEvaluationVisitor(env, evalEnv, extentMap);
		}
		
		InternalEvaluator visitor = (InternalEvaluator) QvtOperationalEvaluationVisitorImpl.createVisitor((QvtOperationalEnv)env, (QvtOperationalEvaluationEnv)evalEnv);

		// Wrap in any decorators
		for(Class<? extends QvtGenericVisitorDecorator> cls : ((QvtOperationalEvaluationEnv)evalEnv).getVisitorDecoratorClasses()) {
			try {
				visitor = cls.getDeclaredConstructor(InternalEvaluator.class).newInstance(visitor);
			} catch(Exception e) {
				String message = NLS.bind("Error while constructing visitor decorator:''{0}''",  cls.getName());
				QvtPlugin.getDefault().log(new Status(IStatus.ERROR, QvtPlugin.ID, message, e));
			}
		}
		
		return visitor;
	}
	
	private static EPackage.Registry copyPackageRegistry(EPackage.Registry registry) {
		EPackageRegistryImpl result = new EPackageRegistryImpl();
		result.putAll(registry);
		return result;
	}
}
