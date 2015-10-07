/*******************************************************************************
 * Copyright (c) 2014 C.Gerpheide and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Christine Gerpheide - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2m.internal.qvt.oml.evaluator;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl.OperationCallResult;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.Visitable;

public class QvtGenericVisitorDecorator extends QvtGenericEvaluationVisitor implements InternalEvaluator {
	
	static abstract class Any extends QvtGenericVisitorDecorator {
		
		protected Any(InternalEvaluator qvtExtVisitor) {
			super(qvtExtVisitor);
		}

		protected abstract Object genericVisitAny(Object object);

		@Override
		protected Object genericPreVisitAST(ASTNode visited) {
			return genericVisitAny(visited);			
		}
		
		@Override
		protected Object genericPreVisitVisitable(Visitable visited) {
			return genericVisitAny(visited);
		}	
			
		@Override
		protected void genericPreVisitEObject(EObject visited) {
			genericVisitAny(visited);			
		}
	}
	
	public QvtGenericVisitorDecorator(InternalEvaluator qvtExtVisitor) {
		super(qvtExtVisitor);
	}
	
	public IContext getContext() {
		return getQVTDelegate().getContext();
	}
	
	public void setOperationalEvaluationEnv(QvtOperationalEvaluationEnv evalEnv) {
		getQVTDelegate().setOperationalEvaluationEnv(evalEnv);
	}
	
	public QvtOperationalEvaluationEnv getOperationalEvaluationEnv() {
		return getQVTDelegate().getOperationalEvaluationEnv();
	}
	
	protected InternalEvaluator getInternalEvalDelegate() {
		return (InternalEvaluator) getQVTDelegate();
	}
	
	public ModuleInstance callTransformationImplicitConstructor(OperationalTransformation transformation, List<ModelInstance> args) {
		return getInternalEvalDelegate().callTransformationImplicitConstructor(transformation, args);
	}
	
	public OperationCallResult runMainEntry(OperationalTransformation transformation, List<Object> args) {
		return getInternalEvalDelegate().runMainEntry(transformation, args);
	}
	
	public Object execute(OperationalTransformation transformation) throws QvtRuntimeException {
		return getInternalEvalDelegate().execute(transformation);
	}
}