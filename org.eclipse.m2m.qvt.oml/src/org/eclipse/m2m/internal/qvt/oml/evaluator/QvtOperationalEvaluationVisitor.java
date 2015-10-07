/*******************************************************************************
 * Copyright (c) 2007, 2010 Borland Software Corporation and others.
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.expressions.util.QVTOperationalVisitor;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;

public interface QvtOperationalEvaluationVisitor extends QVTOperationalVisitor<Object>,
	EvaluationVisitor<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, 
		EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> {
	
	/**
	 * Gets the evaluation context.
	 */
    IContext getContext();
    
    /**
     * Set the evaluation environment to be used by this visitor
     */
    void setOperationalEvaluationEnv(QvtOperationalEvaluationEnv evalEnv);

    /**
     * Get the evaluation environment used by this visitor
     */    
    QvtOperationalEvaluationEnv getOperationalEvaluationEnv();    
}
