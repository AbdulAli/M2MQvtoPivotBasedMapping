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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTOEnvironment;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.util.MutableList;
import org.eclipse.m2m.qvt.oml.util.Utils;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.CollectionUtil;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;


public class SetTypeOperations extends AbstractContextualOperations {

	private SetTypeOperations(AbstractQVTStdlib library, EClassifier contextType) {
		super(library, contextType);		
	}

	public static AbstractContextualOperations[] getAllOperations(AbstractQVTStdlib library) {
		QVTOEnvironment environment = library.getEnvironment();
		return new AbstractContextualOperations[] {
			new SetTypeOperations(library, environment.getOCLStandardLibrary().getSet()),
		};		
	}	
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getEnvironment().getOCLStandardLibrary();
		EClassifier setOfT2 = TypeUtil.resolveSetType(getStdlib().getEnvironment(), oclStdlib.getT2());
		EClassifier bagOfT2 = TypeUtil.resolveBagType(getStdlib().getEnvironment(), oclStdlib.getT2());
		EClassifier setOfCommonT = TypeUtil.resolveSetType(getStdlib().getEnvironment(), getStdlib().getCommonT());
		EClassifier bagOfCommonT = TypeUtil.resolveBagType(getStdlib().getEnvironment(), getStdlib().getCommonT());
		
		return new OperationProvider[] {
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=467600
			//
			new OperationProvider(UNION, PredefinedType.UNION_NAME, new String[] {"set"}, //$NON-NLS-1$
					setOfCommonT, setOfT2),
			new OperationProvider(UNION, PredefinedType.UNION_NAME, new String[] {"bag"}, //$NON-NLS-1$
					bagOfCommonT, bagOfT2),

			new OperationProvider(INTERSECTION, PredefinedType.INTERSECTION_NAME, new String[] {"set"}, //$NON-NLS-1$
					oclStdlib.getSet(), setOfT2),
			new OperationProvider(INTERSECTION, PredefinedType.INTERSECTION_NAME, new String[] {"bag"}, //$NON-NLS-1$
					oclStdlib.getSet(), bagOfT2),

			new OperationProvider(MINUS, PredefinedType.MINUS_NAME, new String[] {"set"}, //$NON-NLS-1$
					oclStdlib.getSet(), setOfT2),

			new OperationProvider(INCLUDING, PredefinedType.INCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					setOfCommonT, oclStdlib.getT2()),
			new OperationProvider(EXCLUDING, PredefinedType.EXCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getSet(), oclStdlib.getT2()),

			new OperationProvider(SYMMETRIC_DIFFERENCE, PredefinedType.SYMMETRIC_DIFFERENCE_NAME, new String[] {"set"}, //$NON-NLS-1$
					setOfCommonT, setOfT2),
		};
	}


	static final CallHandler UNION = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0 && args[0] instanceof Collection) {
				Collection<?> result = CollectionUtil.union((Collection<?>) source, (Collection<?>) args[0]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler INTERSECTION = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0 && args[0] instanceof Collection) {
				return CollectionUtil.intersection((Collection<?>) source, (Collection<?>) args[0]);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	private static final CallHandler MINUS = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Set && args.length > 0 && args[0] instanceof Set) {
				return CollectionUtil.minus((Set<?>) source, (Set<?>) args[0]);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler INCLUDING = new CallHandler() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0) {
				if (args[0] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				if (source instanceof LinkedHashSet) {
					source = CollectionUtil.createNewSet((Collection<?>) source);
				}
				Collection<?> result = CollectionUtil.including((Collection<Object>) source, args[0]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	static final CallHandler EXCLUDING = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0) {
				if (args[0] == CallHandlerAdapter.getInvalidResult(evalEnv)) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				if (source instanceof LinkedHashSet) {
					source = CollectionUtil.createNewSet((Collection<?>) source);
				}
				Collection<?> result = CollectionUtil.excluding((Collection<?>) source, args[0]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
	private static final CallHandler SYMMETRIC_DIFFERENCE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Set && args.length > 0 && args[0] instanceof Set) {
				return CollectionUtil.symmetricDifference((Set<?>) source, (Set<?>) args[0]);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};	
			
}
