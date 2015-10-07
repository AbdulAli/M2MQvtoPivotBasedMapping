/*******************************************************************************
 * Copyright (c) 2014, 2015 S.Boyko and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Boyko - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTOEnvironment;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.EvaluationUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.util.MutableList;
import org.eclipse.m2m.qvt.oml.util.Utils;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.CollectionUtil;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;


public class OrderedSetTypeOperations extends AbstractContextualOperations {

	static final String REVERSE_NAME = "reverse"; //$NON-NLS-1$
	
	
	private OrderedSetTypeOperations(AbstractQVTStdlib library, EClassifier contextType) {
		super(library, contextType);		
	}

	public static AbstractContextualOperations[] getAllOperations(AbstractQVTStdlib library) {
		QVTOEnvironment environment = library.getEnvironment();
		return new AbstractContextualOperations[] {
			new OrderedSetTypeOperations(library, environment.getOCLStandardLibrary().getOrderedSet()),
		};		
	}	
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getEnvironment().getOCLStandardLibrary();
		EClassifier orderedSetOfCommonT = TypeUtil.resolveOrderedSetType(getStdlib().getEnvironment(), getStdlib().getCommonT());
		
		return new OperationProvider[] {
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=467600
			//
			new OperationProvider(APPEND, PredefinedType.APPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					orderedSetOfCommonT, oclStdlib.getT2()),
			new OperationProvider(PREPEND, PredefinedType.PREPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					orderedSetOfCommonT, oclStdlib.getT2()),
			new OperationProvider(INSERT_AT, PredefinedType.INSERT_AT_NAME, new String[] {"index", "object"}, //$NON-NLS-1$ //$NON-NLS-2$
					orderedSetOfCommonT, oclStdlib.getInteger(), oclStdlib.getT2()),
			new OperationProvider(INDEX_OF, PredefinedType.INDEX_OF_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getInteger(), oclStdlib.getT2()),
	
			new OperationProvider(REVERSE, REVERSE_NAME, oclStdlib.getOrderedSet()),
		};
	}


	static final CallHandler APPEND = new CallHandler() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0) {
				if (args[0] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				Collection<?> result = CollectionUtil.append((Collection<Object>) source, args[0]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler PREPEND = new CallHandler() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0) {
				if (args[0] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				Collection<?> result = CollectionUtil.prepend((Collection<Object>) source, args[0]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler INSERT_AT = new CallHandler() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 1 && args[0] instanceof Integer) {
				if (args[1] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				Collection<?> result = CollectionUtil.insertAt((Collection<Object>) source, (Integer) args[0], args[1]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler INDEX_OF = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0) {
				if (args[0] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				Integer index = CollectionUtil.indexOf((Collection<?>) source, args[0]);
				if (index == null) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				return index;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler REVERSE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection) {
				List<?> result = new ArrayList<Object>((Collection<?>) source);
				Collections.reverse(result);
				
				if (source instanceof MutableList) {
					return EvaluationUtil.asList(result);
				}
				if (source instanceof LinkedHashSet) {
					return CollectionUtil.asOrderedSet(result);
				}
				return CollectionUtil.asSequence(result);
				
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
}
