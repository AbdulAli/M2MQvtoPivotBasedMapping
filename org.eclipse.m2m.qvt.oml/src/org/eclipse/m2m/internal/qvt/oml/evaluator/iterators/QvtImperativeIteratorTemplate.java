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
package org.eclipse.m2m.internal.qvt.oml.evaluator.iterators;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl.BreakingResult;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.types.InvalidType;
import org.eclipse.ocl.types.VoidType;

/**
 * @author aigdalov
 * Created on Sep 25, 2008
 */
public abstract class QvtImperativeIteratorTemplate<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E>
extends QvtIterationTemplate<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> {

    protected QvtImperativeIteratorTemplate(EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> v) {
        super(v);
    }

    // override this method for different iterator behaviors
    protected abstract Object evaluateResult(List<Variable<C, PM>> iterators, String resultName, OCLExpression<EClassifier> conditionExp, Object bodyVal, boolean isOne);

    @Override
    protected Object evaluateResultTemplate(List<Variable<C, PM>> iterators,
            Variable<EClassifier, EParameter> target,
            String resultName, OCLExpression<C> condition,
            OCLExpression<C> body, boolean isOne) {
        // evaluate the body of the expression in this environment
        Object bodyVal = null;
        if (body != null) {
        	bodyVal = getEvaluationVisitor().visitExpression(body);
        }
        else if (iterators.size() == 1) {
        	bodyVal = getEvalEnvironment().getValueOf(iterators.get(0).getName());
        }

        if(bodyVal instanceof BreakingResult) {
        	// Control flow was broken (break, continue, return); propagate this.
        	return bodyVal;
        }
        
        advanceTarget(target, bodyVal);

        // get the new result value
        @SuppressWarnings("unchecked")
        Object resultVal = evaluateResult(iterators, resultName, (OCLExpression<EClassifier>) condition, bodyVal, isOne);
        return resultVal;
    }
    
    protected Boolean isConditionOk(OCLExpression<EClassifier> conditionExp, Object bodyVal) {
        // evaluate the condition of the expression in this environment
        Object conditionVal = conditionExp.accept(getEvaluationVisitor());
        if (conditionVal instanceof Boolean) {
            return (Boolean) conditionVal;
        } else if (conditionVal instanceof EClassifier){
            QvtOperationalEvaluationEnv env = (QvtOperationalEvaluationEnv) getEvalEnvironment();
            return oclIsKindOf(bodyVal, (EClassifier) conditionVal, env);
        } else {
            setDone(true);
            return null;
        }
    }
    
    
    protected Object returnCheckedEvaluationResult(Object addedElement, boolean isOne, String resultName) {
        // If the body result is invalid then the entire expression's value
        // is invalid, because OCL does not permit OclInvalid in a collection
        if (addedElement == getInvalid()) {
            setDone(true);
            return getInvalid();
        }
        if (isOne) {
            setDone(true);
            return addedElement;
        }
        @SuppressWarnings("unchecked")
        Collection<Object> resultingCollection = (Collection<Object>) getEvalEnvironment().getValueOf(resultName);
        resultingCollection.add(addedElement);
        return resultingCollection;
    }
    
    
    /* TODO Moved these bits from QvtOperationalUtil
     * Why OCL iskindof, istypeof is not sufficient?
    */ 
    private Boolean oclIsKindOf(Object value, EClassifier type, QvtOperationalEvaluationEnv env) {
        // regardless of the source value, if the type is undefined, then so
        //    is oclIsTypeOf
        if (type == null) {
            return null;
        }
        
        // OclVoid and Invalid conform to all classifiers but their instances
        // aren't actually useful as any type but their own.  So, check for
        // exact type match in these cases
        if (isUndefined(value)) {
            return oclIsTypeOf(value, type);
        }

        return Boolean.valueOf(env.isKindOf(value, type));
    }
    
    private boolean isUndefined(Object value) {
    	return QvtOperationalUtil.isUndefined(value, getEvalEnvironment());
    }

    private Boolean oclIsTypeOf(Object value, EClassifier type) {
        // regardless of the source value, if the type is undefined, then so
        //    is oclIsTypeOf
        if (type == null) {
            return null;
        }
        
        // the type of null is OclVoid
        // FIXME - may change in OCL 3.0
        if (value == null) {
            return Boolean.valueOf(type instanceof VoidType<?>);
        }
        
        // the type of OclInvalid is Invalid
        if (QvtOperationalUtil.isInvalid(value, getEvalEnvironment())) {
            return Boolean.valueOf(type instanceof InvalidType<?>);
        }

        // FIXME
        @SuppressWarnings("unchecked")
        EvaluationEnvironment<EClassifier, ?, ?, ?, ?> evalEnv = (EvaluationEnvironment<EClassifier, ?, ?, ?, ?>) getEvalEnvironment();        
        return Boolean.valueOf(evalEnv.isTypeOf(value, type));
	}        
}