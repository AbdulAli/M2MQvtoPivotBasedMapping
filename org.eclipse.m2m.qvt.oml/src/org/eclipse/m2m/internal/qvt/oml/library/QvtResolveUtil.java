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
 *     Christopher Gerking - bugs 358709, 433292, 463416
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.ast.env.InternalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.EvaluationUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitor;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.trace.EMappingResults;
import org.eclipse.m2m.internal.qvt.oml.trace.Trace;
import org.eclipse.m2m.internal.qvt.oml.trace.TraceRecord;
import org.eclipse.m2m.internal.qvt.oml.trace.VarParameterValue;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.AssignExp;
import org.eclipse.ocl.ecore.OperationCallExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.types.CollectionType;
import org.eclipse.ocl.util.CollectionUtil;
import org.eclipse.ocl.utilities.PredefinedType;

/**
 * @author aigdalov
 */

public class QvtResolveUtil {
	/**
	 * A helper interface to hold the source object for late resolve call which
	 * is to be called on during deferred assignment execution.
	 * <p>
	 * Note: The motivation for this interface is the need to distinguish
	 * between not- passing a source object of a source resulting in
	 * <code>null</code> during evaluation.
	 */
	interface SavedSourceObjectHolder {
		/**
		 * @return the source object of <code>null</code>
		 */
		Object getSourceObj();
		/**
		 * Indicates whether the source object is used in the end of the
		 * transformation as source of late resolve calls.
		 */
		boolean isInDeferredExecution();
	}
	
	private QvtResolveUtil() {
		super();
	}
	
	/**
	 * Indicates whether the given assignment has late resolved right value and
	 * is to be executed as deferred assignment.
	 * 
	 * @param resolveExp
	 *            the resolve expression to analyze
	 * @return <code>true</code> if the assignment is to receive a future
	 *         value from late resolve; <code>false</code> otherwise
	 */
	public static boolean hasDeferredRightSideValue(AssignExp assignExp) {
		if(assignExp.getValue().isEmpty()) {
			return false;
		}

		OCLExpression<EClassifier> rightValue = assignExp.getValue().get(0);
		if(rightValue instanceof ResolveExp && ((ResolveExp)rightValue).isIsDeferred()) {
			return true;
		}
		
		if(rightValue instanceof OperationCallExp) {
			OperationCallExp operCall = (OperationCallExp) rightValue;
			return isLateResolveResultConversion(operCall);
		}
		return false;
	}	

	/**
	 * Indicates whether the given resolve expression can be used in conjunction
	 * with deferred assignment.
	 * <p>
	 * Note: The late resolve call result is assigned at deferred time if is assigned
	 * to a property directly or by calling a single <code>->as...</code> collection type
	 * conversion method.
	 *  
	 * @param resolveExp
	 *            the resolve expression to analyze.
	 * @return <code>true</code> if there is a supported deferred assignment
	 *         to receive the future value; <code>false</code> otherwise.
	 * @see #getDeferredAssignmentFor(ResolveExp)
	 */	
	public static boolean isSuppportedAsDeferredAssigned(ResolveExp  resolveExp) {
		return getDeferredAssignmentFor(resolveExp) != null;
	}
	
	/**
	 * Gets deferred assignment used with the given resolve expression which is
	 * to be executed in the end of the transformation.
	 * 
	 * @param resolveExp
	 *            a resolve expression
	 * @return the assignment receiving the late resolve result, if the resolve
	 *         expression is deferred and its result is assigned to it directly
	 *         or by using a collection type conversion. Otherwise,
	 *         <code>null</code> is returned.
	 */
	public static AssignExp getDeferredAssignmentFor(ResolveExp resolveExp) {
		if(!resolveExp.isIsDeferred()) {
			return null;
		}
		
    	EObject resolveContainer = resolveExp.eContainer();
		if(resolveContainer instanceof AssignExp) {
			AssignExp assignExp = (AssignExp) resolveContainer;
			if(assignExp.getLeft() instanceof PropertyCallExp) {
				return assignExp;
			}
		} else if(resolveContainer instanceof OperationCallExp) {
			OperationCallExp operCall = (OperationCallExp) resolveContainer;
			if(!isLateResolveResultConversion(operCall)) {
				return null;
			}
			// lookup the closest outer assignment node
			EObject parent = operCall.eContainer();
			while(parent != null) {
				 if(parent instanceof AssignExp) {
					 AssignExp assignExp = (AssignExp)parent;							 
					 if(assignExp.getValue().indexOf(operCall) >= 0) {
						 return assignExp;
					 }
				 }
				 parent = parent.eContainer();
			}
		} 

		return null;		
	}	
	
	/**
	 * Indicate whether a operation call expression is supported collection
	 * type conversion for late resolve results. 
	 */	
	private static boolean isLateResolveResultConversion(OperationCallExp operCall) {
		if(operCall.getSource() instanceof ResolveExp) {
			ResolveExp resolveExp = (ResolveExp) operCall.getSource();
			if(!resolveExp.isIsDeferred()) {
				return false;
			}

			return isCollectionConversionCall(operCall) && resolveExp.getType() instanceof CollectionType<?, ?>;			
		}
		return false;
	}
	    
	/**
	 * Indicate whether a operation call expression is collection type conversion. 
	 */
	private static boolean isCollectionConversionCall(OperationCallExp operCall) {
		switch(operCall.getOperationCode()) {
		case PredefinedType.AS_BAG :
		case PredefinedType.AS_SEQUENCE :
		case PredefinedType.AS_SET :
		case PredefinedType.AS_ORDERED_SET :
			return true;
		}
		
		return false;
	}
	
	private static Object coerceResultValue(ResolveExp resolveExp, Object resolveRawResult) {
    	// always return non-null if collection type is expected
    	if(resolveRawResult == null) {
    		resolveRawResult = createEmptyCollectionOrNull(resolveExp);
    	}
    	
    	if (resolveExp.isIsDeferred() && resolveExp.eContainer() instanceof OperationCallExp) {
			OperationCallExp opCallExp = (OperationCallExp) resolveExp.eContainer();
			if(opCallExp.getSource() == resolveExp) {
				Collection<?> resultCollection = (resolveRawResult instanceof Collection) ? 
						(Collection<?>) resolveRawResult : Collections.singletonList(resolveRawResult); 
				// supported collection conversion operation call on the result of late resolve
				switch (opCallExp.getOperationCode()) {
				case PredefinedType.AS_SET:
					return CollectionUtil.asSet((Collection<?>) resultCollection);
				case PredefinedType.AS_BAG:
					return CollectionUtil.asBag((Collection<?>) resultCollection);
				case PredefinedType.AS_ORDERED_SET:
					return CollectionUtil.asOrderedSet((Collection<?>) resultCollection);
				case PredefinedType.AS_SEQUENCE:
					return CollectionUtil.asSequence((Collection<?>) resultCollection);
				}
			}
		}

    	return resolveRawResult;
	}
    
    public static final Object resolveNow(ResolveExp resolveExp, QvtOperationalEvaluationVisitor visitor, QvtOperationalEvaluationEnv env) {
    	return resolveNow(resolveExp, visitor, env, null);
    }
	
    public static final Object resolveInNow(ResolveInExp resolveInExp, QvtOperationalEvaluationVisitor visitor, QvtOperationalEvaluationEnv env) {
    	return resolveInNow(resolveInExp, visitor, env, null);
    }
    
    /**
	 * Resolves resolve expression using the given evaluation visitor and
	 * environment.
	 * 
	 * @param resolveExp
	 *            the resolve expression to resolve
	 * @param visitor
	 *            the visitor to perform evaluation
	 * @param env
	 *            environment for condition or eventual source object evaluation
	 * @param savedSrcObj
	 *            the source object evaluated and saved for late resolve
	 *            execution or <code>null</code> if the source is to be
	 *            evaluated by this method
	 * @return resolved object or collection of objects
	 */
    static final Object resolveNow(ResolveExp resolveExp, QvtOperationalEvaluationVisitor visitor, QvtOperationalEvaluationEnv env, SavedSourceObjectHolder savedSrcObj) {
    	InternalEvaluationEnv internEnv = env.getAdapter(InternalEvaluationEnv.class);
        Trace trace = internEnv.getTraces();
        EMap<Object, EList<TraceRecord>> map = chooseKeyToTraceRecordMap(resolveExp, trace);
        OCLExpression<EClassifier> source = resolveExp.getSource();
        Object sourceEval = (savedSrcObj == null) ? (source == null ? null : source.accept(visitor)) : savedSrcObj.getSourceObj();
        EClassifier sourceType = (source == null) ? null : source.getType();
        List<TraceRecord> traceRecords = lookupTraceRecordsBySource(sourceEval, sourceType, map);
        if (traceRecords == null) {
            return createEmptyCollectionOrNull(resolveExp);
        }
        Object result = searchByTypeAndCondition(resolveExp, traceRecords, visitor, env);
        if(savedSrcObj != null && savedSrcObj.isInDeferredExecution() && resolveExp.isIsDeferred()) {
        	// Note: executing immediately but at deferred execution time, this is possible for instance if late resolve is called from with a condition 
        	result = coerceResultValue(resolveExp, result);
        }
        return result;
    }

    /**
	 * Resolves resolveIn expression using the given evaluation visitor and
	 * environment.
	 * 
	 * @param resolveInExp
	 *            the resolve expression to resolve
	 * @param visitor
	 *            the visitor to perform evaluation
	 * @param env
	 *            environment for condition or eventual source object evaluation
	 * @param savedSrcObj
	 *            the source object evaluated and saved for late resolve
	 *            execution or <code>null</code> if the source is to be
	 *            evaluated by this method
	 * 
	 * @return resolved object or collection of objects
	 */
    static final Object resolveInNow(ResolveInExp resolveInExp, QvtOperationalEvaluationVisitor visitor, QvtOperationalEvaluationEnv env, SavedSourceObjectHolder savedSrcObj) {
    	InternalEvaluationEnv internEnv = env.getAdapter(InternalEvaluationEnv.class);
    	
        OCLExpression<EClassifier> source = resolveInExp.getSource();
        List<TraceRecord> selectedTraceRecords = new ArrayList<TraceRecord>();
        Trace trace = internEnv.getTraces();
        if (source == null) {
            List<TraceRecord> traceRecords = new ArrayList<TraceRecord>();
            MappingOperation inMapping = resolveInExp.getInMapping();
            
            // bug 358709: consider overriding mapping
            ImperativeOperation overridingOper = EvaluationUtil.getOverridingOperation(env, inMapping);
            if (overridingOper instanceof MappingOperation) {
            	inMapping = (MappingOperation) overridingOper;
            }
            
            EList<TraceRecord> inMappingTraceRecords = trace.getTraceRecordMap().get(inMapping);
            if (inMappingTraceRecords != null) {
                traceRecords.addAll(inMappingTraceRecords);
            }
            
            if (traceRecords.isEmpty()) {
                return createEmptyCollectionOrNull(resolveInExp);
            }
            selectedTraceRecords.addAll(traceRecords);
        } else {
            EMap<Object, EList<TraceRecord>> map = chooseKeyToTraceRecordMap(resolveInExp, trace);
            Object sourceEval = (savedSrcObj == null) ? source.accept(visitor) : savedSrcObj.getSourceObj();

            List<TraceRecord> traceRecords = lookupTraceRecordsBySource(sourceEval, source.getType(), map);
            if (traceRecords.isEmpty()) {
            	return createEmptyCollectionOrNull(resolveInExp);
            }
            for (TraceRecord traceRecord : traceRecords) {
                MappingOperation inMapping = resolveInExp.getInMapping();
                
                // bug 463416: consider overriding mapping
	    		ImperativeOperation overridingOper = EvaluationUtil.getOverridingOperation(env, inMapping);
	    		if (overridingOper instanceof MappingOperation) {
	    			inMapping = (MappingOperation) overridingOper;
	    		}
                
                if (traceRecord.getMappingOperation().getRuntimeMappingOperation().equals(inMapping)) {
                    selectedTraceRecords.add(traceRecord);
                }                
            }
        }
        Object result = searchByTypeAndCondition(resolveInExp, selectedTraceRecords, visitor, env);
        if(savedSrcObj != null && savedSrcObj.isInDeferredExecution() && resolveInExp.isIsDeferred()) {
        	// Note: executing immediately but at deferred execution time, this is possible for instance if late resolve is called from with a condition 
        	result = coerceResultValue(resolveInExp, result);
        }
        return result;
    }
    
    private static List<TraceRecord> lookupTraceRecordsBySource(Object source, EClassifier declaredSourceType, EMap<Object, EList<TraceRecord>> source2RecordMap) {
    	List<TraceRecord> result = null;
    	
    	if (declaredSourceType == null) {
    		for (EList<TraceRecord> rec : source2RecordMap.values()) {
        		if(result == null) {
        			result = new BasicEList<TraceRecord>();
        		}        			
    			result.addAll(rec);
    		}
    	}
    	else {
	    	// Remark: Should be removed as soon as implict collect is support on resolve too
	        if(declaredSourceType instanceof CollectionType<?, ?> && source instanceof Collection<?>) {
	        	Collection<?> srcCol = (Collection<?>)source;
	        	for (Object nextSrc : srcCol) {
	        		EList<TraceRecord> nextPart = source2RecordMap.get(nextSrc);
	
	        		if(nextPart != null) {
	            		if(result == null) {
	            			result = new BasicEList<TraceRecord>();
	            		}        			
	        			result.addAll(nextPart);
	        		}
				}	            	
	        } else {
	        	result = source2RecordMap.get(source);
	        }
    	}
    	
        return (result != null) ? Collections.unmodifiableList(result) : Collections.<TraceRecord>emptyList();
    }
    	
    private static Object searchByTypeAndCondition(ResolveExp resolveExp, List<TraceRecord> traceRecords, QvtOperationalEvaluationVisitor visitor, QvtOperationalEvaluationEnv env) {
        if (resolveExp.isOne()) {
            for (TraceRecord traceRecord : traceRecords) {
                EMappingResults results = traceRecord.getResult();
                if (resolveExp.isIsInverse()) {
                    if (traceRecord.getContext().getContext() != null) {
                        Object target = traceRecord.getContext().getContext().getValue().getOclObject();
                        if (!checkTypeAndCondition(resolveExp, target, visitor, env)) {
                            continue;
                        }
                        return target;
                    }
                } else {
                    for (VarParameterValue varParameterValue : results.getResult()) {
                        Object target = varParameterValue.getValue().getOclObject();
                        if (!checkTypeAndCondition(resolveExp, target, visitor, env)) {
                            continue;
                        }
                        return target;
                    }
                }
            }
            return null;
        } else {
            List<Object> sequence = CollectionUtil.createNewSequence();
            for (TraceRecord traceRecord : traceRecords) {
                EMappingResults results = traceRecord.getResult();
                if (resolveExp.isIsInverse()) {
                    if (traceRecord.getContext().getContext() != null) {
                        Object target = traceRecord.getContext().getContext().getValue().getOclObject();
                        if (!checkTypeAndCondition(resolveExp, target, visitor, env)) {
                            continue;
                        }
                        sequence.add(target);
                    }
                } else {
                    for (VarParameterValue varParameterValue : results.getResult()) {
                        Object target = varParameterValue.getValue().getOclObject();
                        if (!checkTypeAndCondition(resolveExp, target, visitor, env)) {
                            continue;
                        }
                        sequence.add(target);
                    }
                }
            }
            return sequence;
        }
    }
    
    private static EMap<Object, EList<TraceRecord>> chooseKeyToTraceRecordMap(ResolveExp resolveExp, Trace trace) {
        return resolveExp.isIsInverse() ? trace.getTargetToTraceRecordMap() : trace.getSourceToTraceRecordMap();
    }
    
    private static boolean checkTypeAndCondition(ResolveExp resolveExp, Object resolveCandidate, QvtOperationalEvaluationVisitor visitor,
            QvtOperationalEvaluationEnv env) {
        if ((resolveExp.getTarget() != null) && (resolveExp.getTarget().getType() != null)) {
            EClassifier type = resolveExp.getTarget().getType();
            if (!type.isInstance(resolveCandidate)) { // TODO : Perhaps, this won't work on primitive datatypes
                return false;
            }
        }
        
        if (resolveExp.getCondition() != null) {
            if ((resolveExp.getTarget() != null) && (resolveExp.getTarget().getName() != null)) {
                env.add(resolveExp.getTarget().getName(), resolveCandidate);
            }
            Object conditionEval = resolveExp.getCondition().accept(visitor);
            if ((resolveExp.getTarget() != null) && (resolveExp.getTarget().getName() != null)) {
                env.remove(resolveExp.getTarget().getName());
            }
            return Boolean.TRUE.equals(conditionEval);
        }
        return true;
    }
    
    private static Object createEmptyCollectionOrNull(ResolveExp resolveExp) {
        if (resolveExp.isOne()) {
            return null;
        }
        return CollectionUtil.createNewSequence();
    }
}