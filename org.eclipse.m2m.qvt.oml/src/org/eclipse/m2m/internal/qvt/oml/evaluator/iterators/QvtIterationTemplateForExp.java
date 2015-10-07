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

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl.BreakingResult;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;

/**
 * @author aigdalov
 * Created on Sep 25, 2008
 */
public class QvtIterationTemplateForExp<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E>
extends QvtIterationTemplate<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> {
    private QvtIterationTemplateForExp(EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> v) {
        super(v);
    }

    public static<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E>
    QvtIterationTemplate<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> getInstance(
            EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> v) {
        return new QvtIterationTemplateForExp<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E>(v);
    }

    @Override
    protected Object evaluateResultTemplate(List<Variable<C, PM>> iterators,
            Variable<EClassifier, EParameter> target,
            String resultName, OCLExpression<C> condition,
            OCLExpression<C> body, boolean isOne) {
        boolean isConditionOk = condition == null;
        if (!isConditionOk) {
            // evaluate the condition of the expression in this environment
            Object conditionVal = condition.accept(getEvaluationVisitor());
            isConditionOk = (conditionVal instanceof Boolean) && (Boolean) conditionVal;
        }
        if (isConditionOk) {
            Object result = getEvaluationVisitor().visitExpression(body);
            if (isOne) {
                setDone(true);
            }
            if(result instanceof BreakingResult) {
            	return result;
            }
        }
        return null;
    }
}