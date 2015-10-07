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
 *     Alex Paperno - bugs 404647
 ********************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.util.MutableList;
import org.eclipse.m2m.qvt.oml.util.Utils;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.CollectionUtil;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;


public class ListOperations extends AbstractContextualOperations {

	static final String ADD_NAME = "add"; //$NON-NLS-1$
	static final String REMOVE_NAME = "remove"; //$NON-NLS-1$
	static final String REMOVE_ALL_NAME = "removeAll"; //$NON-NLS-1$
	static final String REMOVE_AT_NAME = "removeAt"; //$NON-NLS-1$
	static final String REMOVE_FIRST_NAME = "removeFirst"; //$NON-NLS-1$
	static final String REMOVE_LAST_NAME = "removeLast"; //$NON-NLS-1$
	static final String JOINFIELDS_NAME = "joinfields"; //$NON-NLS-1$
	
	
	public ListOperations(AbstractQVTStdlib library) {
		super(library, library.getList());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		EClassifier listOfT2 = getStdlib().getEnvironment().getTypeResolver().resolveListType(oclStdlib.getT2());
		EClassifier listOfCommonT = getStdlib().getEnvironment().getTypeResolver().resolveListType(getStdlib().getCommonT());
		EClassifier collectionOfT2 = TypeUtil.resolveCollectionType(getStdlib().getEnvironment(),
				CollectionKind.COLLECTION_LITERAL, oclStdlib.getT2());
		
		return new OperationProvider[] {
			new OperationProvider(ADD, ADD_NAME, new String[] { "object"}, oclStdlib.getOclVoid(), oclStdlib.getT()), //$NON-NLS-1$
			new OperationProvider(UNSUPPORTED_OPER, ADD_NAME,
					new String[] {"obj", "List(T)::add(object:T2) is supported only for case T2_isSubtypeOf_T"}, //$NON-NLS-1$ //$NON-NLS-2$
					oclStdlib.getOclVoid(), oclStdlib.getT2()),
			new OperationProvider(INSERT_AT_VOID, PredefinedType.INSERT_AT_NAME, new String[] { "object", "index"}, //$NON-NLS-1$ //$NON-NLS-2$
					oclStdlib.getOclVoid(), oclStdlib.getT(), oclStdlib.getInteger()),
//			new OperationProvider(UNSUPPORTED_OPER, PredefinedType.INSERT_AT_NAME, 
//					new String[] { "obj", "index", "List(T)::insertAt(object:T2,index:Integer) is supported only for case T2_isSubtypeOf_T" }, //$NON-NLS-1$ //$NON-NLS-2$
//					oclStdlib.getOclVoid(), oclStdlib.getT2(), oclStdlib.getInteger()),
					
			new OperationProvider(REMOVE, REMOVE_NAME, new String[] { "element"}, oclStdlib.getOclVoid(), oclStdlib.getT2()), //$NON-NLS-1$
			new OperationProvider(REMOVE_ALL, REMOVE_ALL_NAME, new String[] { "elements"}, oclStdlib.getOclVoid(), collectionOfT2), //$NON-NLS-1$
			new OperationProvider(REMOVE_AT, REMOVE_AT_NAME, new String[] { "index"}, oclStdlib.getT(), oclStdlib.getInteger()), //$NON-NLS-1$
			new OperationProvider(REMOVE_FIRST, REMOVE_FIRST_NAME, oclStdlib.getT()), //$NON-NLS-1$
			new OperationProvider(REMOVE_LAST, REMOVE_LAST_NAME, oclStdlib.getT()), //$NON-NLS-1$

			new OperationProvider(JOINFIELDS, JOINFIELDS_NAME, new String[] { "sep", "begin", "end" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
					oclStdlib.getString(), oclStdlib.getString(), oclStdlib.getString(), oclStdlib.getString()),			

			new OperationProvider(FIRST, PredefinedType.FIRST_NAME, oclStdlib.getT()),
			new OperationProvider(LAST, PredefinedType.LAST_NAME, oclStdlib.getT()),
			new OperationProvider(AT, PredefinedType.AT_NAME, new String[] { "i" }, //$NON-NLS-1$
					oclStdlib.getT(), oclStdlib.getInteger()),
			new OperationProvider(CLONE, ElementOperations.CLONE_NAME, getStdlib().getList()),
			new OperationProvider(UNSUPPORTED_OPER, ElementOperations.DEEP_CLONE_NAME,
					new String[] { "List(T)::deepclone() is not supported yet" }, //$NON-NLS-1$ //$NON-NLS-2$
					getStdlib().getList()),

			new OperationProvider(SELECT_BY_TYPE, PredefinedType.SELECT_BY_TYPE_NAME, new String[] { "type" }, //$NON-NLS-1$ //$NON-NLS-2$
					getStdlib().getList(), oclStdlib.getOclType()),
			new OperationProvider(SELECT_BY_KIND, PredefinedType.SELECT_BY_KIND_NAME, new String[] { "type" }, //$NON-NLS-1$ //$NON-NLS-2$
					getStdlib().getList(), oclStdlib.getOclType()),
			new OperationProvider(SUB_SEQUENCE, PredefinedType.SUB_SEQUENCE_NAME, new String[] { "lower", "upper" }, //$NON-NLS-1$ //$NON-NLS-2$
					getStdlib().getList(), oclStdlib.getInteger(), oclStdlib.getInteger()),
			
			new OperationProvider(CollectionTypeOperations.FLATTEN, PredefinedType.FLATTEN_NAME, listOfT2),

			new OperationProvider(SetTypeOperations.INCLUDING, PredefinedType.INCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					listOfCommonT, oclStdlib.getT2()),
			new OperationProvider(SetTypeOperations.EXCLUDING, PredefinedType.EXCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					getStdlib().getList(), oclStdlib.getT2()),
			new OperationProvider(SetTypeOperations.UNION, PredefinedType.UNION_NAME, new String[] {"s"}, //$NON-NLS-1$
					listOfCommonT, listOfT2),

			new OperationProvider(OrderedSetTypeOperations.REVERSE, OrderedSetTypeOperations.REVERSE_NAME, getStdlib().getList()),
			new OperationProvider(OrderedSetTypeOperations.APPEND, PredefinedType.APPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					listOfCommonT, oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.PREPEND, PredefinedType.PREPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					listOfCommonT, oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.INSERT_AT, PredefinedType.INSERT_AT_NAME, new String[] {"index", "object"}, //$NON-NLS-1$ //$NON-NLS-2$
					listOfCommonT, oclStdlib.getInteger(), oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.INDEX_OF, PredefinedType.INDEX_OF_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getInteger(), oclStdlib.getT2()),

		};
	}	

	
	static CallHandler ADD = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 0) {
				List<Object> list = (List<Object>) source;
				Object element = args[0];
				if(element != CallHandlerAdapter.getInvalidResult(evalEnv)) {
					list.add(element);
				}
				return list;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler INSERT_AT_VOID = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 1 && args[1] instanceof Integer) {
				MutableList<Object> list = (MutableList<Object>) source;
				Object element = args[0];
				if(element != CallHandlerAdapter.getInvalidResult(evalEnv)) {				
					list.insertAt(element, (Integer) args[1]);
				}
				return list;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler REMOVE = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 0) {
				List<Object> list = (List<Object>) source;
				Object element = args[0];
				if(element != CallHandlerAdapter.getInvalidResult(evalEnv)) {
					list.removeAll(Collections.singleton(element));
				}
				return list;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler REMOVE_ALL = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 0 && args[0] instanceof Collection) {
				List<Object> list = (List<Object>) source;
				list.removeAll((Collection<?>) args[0]);
				return list;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler REMOVE_AT = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 0 && args[0] instanceof Integer) {
				List<Object> list = (List<Object>) source;
		        int index = (Integer) args[0] - 1;
		        if (index < 0 || index >= list.size()) {
		        	return CallHandlerAdapter.getInvalidResult(evalEnv);
		        }
				return list.remove(index);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler REMOVE_FIRST = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList) {
				List<Object> list = (List<Object>) source;
		        if (list.isEmpty()) {
		        	return CallHandlerAdapter.getInvalidResult(evalEnv);
		        }
				return list.remove(0);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler REMOVE_LAST = new CallHandlerMutator() {
		
		@SuppressWarnings("unchecked")
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList) {
				List<Object> list = (List<Object>) source;
		        if (list.isEmpty()) {
		        	return CallHandlerAdapter.getInvalidResult(evalEnv);
		        }
				return list.remove(list.size()-1);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	static CallHandler JOINFIELDS = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof MutableList && args.length > 2 && args[0] instanceof String && args[1] instanceof String && args[2] instanceof String) {
				MutableList<?> list = (MutableList<?>) source;
				
				String sep = (String) args[0];
				String begin = (String) args[1];			
				String end = (String) args[2];
							
				return list.joinfields(sep, begin, end);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};
	
	private static CallHandler FIRST = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection) {
				if (((Collection<?>) source).isEmpty()) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				return CollectionUtil.first((Collection<?>) source);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler LAST = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection) {
				if (((Collection<?>) source).isEmpty()) {
					return CallHandlerAdapter.getInvalidResult(evalEnv);
				}
				return CollectionUtil.last((Collection<?>) source);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler AT = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0 && args[0] instanceof Integer) {
		        int index = (Integer) args[0] - 1;
		        if (index < 0 || index >= ((Collection<?>) source).size()) {
		        	return CallHandlerAdapter.getInvalidResult(evalEnv);
		        }
				return CollectionUtil.at((Collection<?>) source, (Integer) args[0]);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler CLONE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection) {
				return Utils.createList((Collection<?>) source);
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler SELECT_BY_TYPE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0 && args[0] instanceof EClassifier) {
				MutableList<Object> result = Utils.createList();
				EClassifier type = (EClassifier) args[0];
		        for (Iterator<?> it = ((Collection<?>) source).iterator(); it.hasNext();) {
		        	Object object = it.next();
					if (evalEnv.isTypeOf(object, type)) {
			        	result.add(object);
					}
		        }
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler SELECT_BY_KIND = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 0 && args[0] instanceof EClassifier) {
				MutableList<Object> result = Utils.createList();
				EClassifier type = (EClassifier) args[0];
		        for (Iterator<?> it = ((Collection<?>) source).iterator(); it.hasNext();) {
		        	Object object = it.next();
					if (evalEnv.isKindOf(object, type)) {
			        	result.add(object);
					}
		        }
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

	private static CallHandler SUB_SEQUENCE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(source instanceof Collection && args.length > 1 && args[0] instanceof Integer && args[1] instanceof Integer) {
		        Collection<?> result = CollectionUtil.subSequence((Collection<?>) source, (Integer) args[0], (Integer) args[1]);
				if (source instanceof MutableList && false == result instanceof MutableList) {
					result = Utils.createList(result);
				}
				return result;
			}
			return CallHandlerAdapter.getInvalidResult(evalEnv);
		}
	};

}
