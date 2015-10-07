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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator.iterators;

/**
 * @author aigdalov
 * Created on Jan 31, 2008
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl.BreakingResult;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl.ContinueResult;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.types.OCLStandardLibrary;

public abstract class QvtIterationTemplate<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> {
    private EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> myEvalVisitor;
    private EvaluationEnvironment<C, O, P, CLS, E> myEvalEnv;
    private boolean done = false;

    // singleton
    protected QvtIterationTemplate(EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> v) {

        myEvalVisitor = v;
        myEvalEnv = v.getEvaluationEnvironment();
    }

    public EvaluationVisitor<PK, C, O, P, EL, PM, S, COA, SSA, CT, CLS, E> getEvaluationVisitor() {
        return myEvalVisitor;
    }

    public EvaluationEnvironment<C, O, P, CLS, E> getEvalEnvironment() {
        return myEvalEnv;
    }

    public final void setDone(boolean done) {
        this.done = done;
    }

    public final boolean isDone() {
        return done;
    }
    
    protected abstract Object evaluateResultTemplate(List<Variable<C, PM>> iterators, Variable<EClassifier, EParameter> target, String resultName, OCLExpression<C> condition, OCLExpression<C> body, boolean isOne);

    public Object evaluate(Collection<?> coll, 
            List<Variable<C, PM>> iterators,
            Variable<EClassifier, EParameter> target,
            OCLExpression<C> condition, 
            OCLExpression<C> body, 
            String resultName,
            boolean isOne) {
        
        // if the collection is empty, then nothing to do
            if (coll.isEmpty())
                return myEvalEnv.getValueOf(resultName);

        // construct an array of java iterators, one for each
            // ocl iterator in the expression
            int numIters = iterators.size();
            Iterator<?>[] javaIters = new Iterator[numIters];
            initializeIterators(iterators, javaIters, coll);

            while (true) {
            	Object resultVal = null;
            	boolean isUpdateResultVal = true;
        		resultVal = evaluateResultTemplate(iterators, target, resultName, condition, body, isOne);
        		
        		if(resultVal instanceof BreakingResult) {
        			// Control flow was broken (break, continue, or return). 
        			if(!(resultVal instanceof ContinueResult)) {
        				// No continue, so no more iterations.
        				setDone(true);
        			}
            		isUpdateResultVal = false;
        		}

                // set the result variable in the environment with the result value
                if (isUpdateResultVal) {
                	myEvalEnv.replace(resultName, resultVal);
                }

                // find the next unfinished iterator
                int curr = getNextUnfinishedIterator(javaIters);

                if (!moreToGo(curr, numIters)) {
                    // all iterators are finished and so are we:

                    // remove the iterators from the environment
                    removeIterators(iterators);
                    
                    // remove the iterators from the environment
                    removeTarget(target);
                    
                    // return the result value
                    return myEvalEnv.getValueOf(resultName);
                }

                // more iteration to go:
                // reset all iterators up to the current unfinished one
                // and replace their assignments in the environment
                advanceIterators(iterators, javaIters, coll, curr);
            }
    }

    protected void initializeIterators(
            List<Variable<C, PM>> iterators,
            Iterator<?>[] javaIters,
            Collection<?> c) {

        for (int i = 0, n = javaIters.length; i < n; i++) {
            javaIters[i] = c.iterator();
            Variable<C, PM> iterDecl = iterators.get(i);
            String iterName = (String) iterDecl.accept(myEvalVisitor);
            Object value = javaIters[i].next();
            myEvalEnv.replace(iterName, value);
        }
    }

    protected int getNextUnfinishedIterator(Iterator<?>[] javaIters) {
        int curr;
        int numIters = javaIters.length;
        for (curr = 0; curr < numIters; curr++)
            if (javaIters[curr].hasNext())
                break;
        return curr;
    }

    protected void advanceIterators(
            List<Variable<C, PM>> iterators,
            Iterator<?>[] javaIters,
            Collection<?> c,
            int curr) {

        // assumes all the iterators have been added to the environment
        // already by initializeIterators().
        for (int i = 0, n = curr; i <= n; i++) {
            Variable<C, PM> iterDecl = iterators.get(i);
            String iterName = iterDecl.getName();
            if (i != curr)
                javaIters[i] = c.iterator();
            Object value = javaIters[i].next();
            myEvalEnv.replace(iterName, value);
        }
    }

    protected void removeIterators(List<Variable<C, PM>> iterators) {
        // remove the iterators from the environment
        for (int i = 0, n = iterators.size(); i < n; i++) {
            Variable<C, PM> iterDecl = iterators.get(i);
            String iterName = iterDecl.getName();
            myEvalEnv.remove(iterName);
        }
    }

    protected boolean moreToGo(int curr, int numIters) {
        if (done)
            return false;
        return curr < numIters;
    }
    
    protected void advanceTarget(Variable<EClassifier, EParameter> target, Object bodyVal) {
        if (target != null) {
            String varName = target.getName();
            myEvalEnv.replace(varName, bodyVal);
        }
    }

    protected void removeTarget(Variable<EClassifier, EParameter> target) {
        if (target != null) {
            String varName = target.getName();
            myEvalEnv.remove(varName);
        }
    }

    protected OCLStandardLibrary<C> getOCLStandardLibrary() {
        return myEvalVisitor.getEnvironment().getOCLStandardLibrary();
    }

    protected Object getInvalid() {
        return getOCLStandardLibrary().getInvalid();
    }
}