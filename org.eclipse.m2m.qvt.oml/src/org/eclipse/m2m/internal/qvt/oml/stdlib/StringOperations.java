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
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.evaluator.ModuleInstance;
import org.eclipse.m2m.qvt.oml.util.ISessionData;
import org.eclipse.ocl.types.OCLStandardLibrary;

import com.ibm.icu.lang.UCharacter;


public class StringOperations extends AbstractContextualOperations {

	static final String LENGTH_NAME = "length"; //$NON-NLS-1$
	static final String SUBSTRING_BEFORE_NAME = "substringBefore"; //$NON-NLS-1$
	static final String SUBSTRING_AFTER_NAME = "substringAfter"; //$NON-NLS-1$	
	static final String FIRST_TO_UPPER_NAME = "firstToUpper"; //$NON-NLS-1$
	static final String LAST_TO_UPPER_NAME = "lastToUpper"; //$NON-NLS-1$
	static final String NORMALIZE_SPACE_NAME = "normalizeSpace"; //$NON-NLS-1$
	static final String REPLACE_NAME = "replace"; //$NON-NLS-1$
	static final String MATCH_NAME = "match"; //$NON-NLS-1$
	static final String FIND_NAME = "find"; //$NON-NLS-1$
	static final String RFIND_NAME = "rfind"; //$NON-NLS-1$
	static final String IS_QUOTED_NAME = "isQuoted"; //$NON-NLS-1$
	static final String QUOTIFY_NAME = "quotify"; //$NON-NLS-1$
	static final String UNQUOTIFY_NAME = "unquotify"; //$NON-NLS-1$
	static final String MATCH_BOOLEAN_NAME = "matchBoolean"; //$NON-NLS-1$
	static final String MATCH_INTEGER_NAME = "matchInteger"; //$NON-NLS-1$
	static final String MATCH_FLOAT_NAME = "matchFloat"; //$NON-NLS-1$
	static final String MATCH_IDENTIFIER_NAME = "matchIdentifier"; //$NON-NLS-1$
	static final String AS_BOOLEAN_NAME = "asBoolean"; //$NON-NLS-1$
	static final String AS_INTEGER_NAME = "asInteger"; //$NON-NLS-1$
	static final String AS_FLOAT_NAME = "asFloat"; //$NON-NLS-1$
	static final String START_STR_COUNTER_NAME = "startStrCounter"; //$NON-NLS-1$
	static final String GET_STR_COUNTER_NAME = "getStrCounter"; //$NON-NLS-1$
	static final String INCR_STR_COUNTER_NAME = "incrStrCounter"; //$NON-NLS-1$
	static final String RESTART_ALL_STR_COUNTER_NAME = "restartAllStrCounter"; //$NON-NLS-1$
	static final String ADD_SUFFIX_NUMBER_NAME = "addSuffixNumber"; //$NON-NLS-1$

	
	public StringOperations(AbstractQVTStdlib library) {
		super(library, library.getEnvironment().getOCLStandardLibrary().getString());
	}
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getOCLStdLib();
		
		return new OperationProvider[] {
			new OperationProvider(ADD_SUFFIX_NUMBER, ADD_SUFFIX_NUMBER_NAME, oclStdlib.getString()),
			new OperationProvider(AS_BOOLEAN, AS_BOOLEAN_NAME, oclStdlib.getBoolean()),			
			new OperationProvider(AS_FLOAT, AS_FLOAT_NAME, oclStdlib.getReal()),
			new OperationProvider(AS_INTEGER, AS_INTEGER_NAME, oclStdlib.getInteger()),
			new OperationProvider(FIND, FIND_NAME, new String[] { "match" }, oclStdlib.getInteger(), oclStdlib.getString()), //$NON-NLS-1$
			new OperationProvider(FIRST_TO_UPPER, FIRST_TO_UPPER_NAME, oclStdlib.getString()),
			
			new OperationProvider(UNSUPPORTED_OPER, "format", new String[] { "value" }, oclStdlib.getString(), getStdlib().getObject()), //$NON-NLS-1$ //$NON-NLS-2$
			
			createStaticOperationProvider(GET_STR_COUNTER, GET_STR_COUNTER_NAME, new String[] { "s" }, oclStdlib.getInteger(), oclStdlib.getString()), //$NON-NLS-1$
			createStaticOperationProvider(INCR_STR_COUNTER, INCR_STR_COUNTER_NAME, new String[] { "s" }, oclStdlib.getInteger(), oclStdlib.getString()), //$NON-NLS-1$			
			new OperationProvider(IS_QUOTED, IS_QUOTED_NAME, new String[] { "s" }, oclStdlib.getBoolean(), oclStdlib.getString()), //$NON-NLS-1$
			new OperationProvider(LAST_TO_UPPER, LAST_TO_UPPER_NAME, oclStdlib.getString()),			
			new OperationProvider(LENGTH, LENGTH_NAME, oclStdlib.getInteger()),
			new OperationProvider(MATCH, MATCH_NAME, new String[] { "matchPattern" }, oclStdlib.getBoolean(), oclStdlib.getString()), //$NON-NLS-1$			
			new OperationProvider(MATCH_BOOLEAN, MATCH_BOOLEAN_NAME, new String[] { "b" }, oclStdlib.getBoolean(), oclStdlib.getBoolean()), //$NON-NLS-1$
			new OperationProvider(MATCH_FLOAT, MATCH_FLOAT_NAME, new String[] { "r" }, oclStdlib.getBoolean(), oclStdlib.getReal()), //$NON-NLS-1$
			new OperationProvider(MATCH_IDENTIFIER, MATCH_IDENTIFIER_NAME, new String[] { "s" }, oclStdlib.getBoolean(), oclStdlib.getString()), //$NON-NLS-1$			
			new OperationProvider(MATCH_INTEGER, MATCH_INTEGER_NAME, new String[] { "s" }, oclStdlib.getBoolean(), oclStdlib.getInteger()), //$NON-NLS-1$			
			new OperationProvider(NORMALIZE, NORMALIZE_SPACE_NAME, oclStdlib.getString()),
			new OperationProvider(REPLACE, REPLACE_NAME, new String[] { "m1", "m2" }, //$NON-NLS-1$ //$NON-NLS-2$ 
					oclStdlib.getString(), oclStdlib.getString(), oclStdlib.getString()),
			createStaticOperationProvider(RESTART_ALL_STR_COUNTER, RESTART_ALL_STR_COUNTER_NAME, null, oclStdlib.getOclVoid()),
			new OperationProvider(RFIND, RFIND_NAME, new String[] { "match" }, oclStdlib.getInteger(), oclStdlib.getString()), //$NON-NLS-1$			
			createStaticOperationProvider(START_STR_COUNTER, START_STR_COUNTER_NAME, new String[] { "s" }, oclStdlib.getOclVoid(), oclStdlib.getString()), //$NON-NLS-1$
			new OperationProvider(SUBSTRING_AFTER, SUBSTRING_AFTER_NAME, new String[] { "match" }, oclStdlib.getString(), oclStdlib.getString()), //$NON-NLS-1$			
			new OperationProvider(SUBSTRING_BEFORE, SUBSTRING_BEFORE_NAME, new String[] { "match" }, oclStdlib.getString(), oclStdlib.getString()), //$NON-NLS-1$			
			new OperationProvider(QUOTIFY, QUOTIFY_NAME, new String[] { "s" }, oclStdlib.getString(), oclStdlib.getString()), //$NON-NLS-1$
			new OperationProvider(UNQUOTIFY, UNQUOTIFY_NAME, new String[] { "s" }, oclStdlib.getString(), oclStdlib.getString()), //$NON-NLS-1$
		};
	}
	

	static final CallHandler LENGTH = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			return ((String) source).length();
		}
	};	
	
	static final CallHandler SUBSTRING_BEFORE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
			String self = (String) source;
			String arg = (String) leftVal;
			int pos = self.indexOf(arg);
			if(pos < 0) {
				return null;
			}
			
			return self.substring(0, pos);
		}
	};
	
	static final CallHandler SUBSTRING_AFTER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}
			String self = (String) source;
			String arg = (String) leftVal;
			int pos = self.indexOf(arg);
			if(pos < 0) {
				return null;
			}
			
			return self.substring(pos + arg.length());
		}
	};	

	static final CallHandler FIRST_TO_UPPER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			if(self.length() == 0) {
				return self;
			}
			StringBuilder buf = new StringBuilder(self);
			buf.setCharAt(0, Character.toUpperCase(self.charAt(0)));
			return buf.toString();
		}
	};
	
	static final CallHandler LAST_TO_UPPER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			if(self.length() == 0) {
				return self;
			}
			StringBuilder buf = new StringBuilder(self);
			int lastPos = self.length() - 1;
			buf.setCharAt(lastPos, Character.toUpperCase(self.charAt(lastPos)));
			return buf.toString();
		}
	};
	
	static final CallHandler NORMALIZE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			self = self.trim();
			StringBuilder buf = new StringBuilder(self.length());
			boolean isWhiteArea = false;
			for (int i = 0; i < self.length(); i++) {
				char c = self.charAt(i);
				if(UCharacter.isWhitespace(c)) {
					if(isWhiteArea) {
						continue;
					} 
					
					isWhiteArea = true;
					buf.append(c);					
				} else {
					isWhiteArea = false;
					buf.append(c);
				}
			}
			return buf.toString();
		}
	};

	static final CallHandler REPLACE = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			Object arg0 = args[0];
			if(arg0 == null && arg0 == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return self;
			}
			
			Object arg1 = args[1];
			if(arg1 == null && arg1 == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return CallHandlerAdapter.getInvalidResult(evalEnv);
			}

			return self.replace((String)arg0, (String)arg1);
		}
	};

	static final CallHandler MATCH = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}

			return Boolean.valueOf(Pattern.matches((String)leftVal, self));
		}
	};
	
	static final CallHandler FIND = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return -1;
			}
			
			return self.indexOf((String)leftVal) + 1;
		}
	};
	
	static final CallHandler RFIND = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			String self = (String) source;
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return -1;
			}
			
			return self.lastIndexOf((String)leftVal) + 1;
		}
	};
	
	static final CallHandler IS_QUOTED = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}
			String quoteStr = (String) leftVal;		
			String self = (String) source;			
			return Boolean.valueOf(self.length() >= (quoteStr.length()*2) && self.startsWith(quoteStr) && self.endsWith(quoteStr));
		}
	};
	
	static final CallHandler QUOTIFY = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return source;
			}
			String quoteStr = (String) leftVal;
			String self = (String) source;
			return quoteStr + self + quoteStr;
		}
	};
	
	static final CallHandler UNQUOTIFY = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			if(!Boolean.TRUE.equals(IS_QUOTED.invoke(module, source, args, evalEnv))) {
				return source;
			}
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}
			String quoteStr = (String) leftVal;
			String self = (String) source;
			return self.substring(quoteStr.length(), self.length() - quoteStr.length());
		}
	};	

	static final CallHandler MATCH_BOOLEAN = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}		

			Boolean arg = (Boolean) leftVal;
			Object self = AS_BOOLEAN.invoke(module, source, args, evalEnv);  
			return Boolean.valueOf(arg.equals(self));
		}
	};
	
	static final CallHandler MATCH_INTEGER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}		

			Integer arg = (Integer) leftVal;
			Object self = AS_INTEGER.invoke(module, source, args, evalEnv);  
			return Boolean.valueOf(arg.equals(self));
		}
	};
	
	static final CallHandler MATCH_FLOAT = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}		

			Number arg = (Number) leftVal;
			Object self = AS_FLOAT.invoke(module, source, args, evalEnv);
			if(self != null) {
				return arg.doubleValue() == ((Number)self).doubleValue();
			}
			return Boolean.valueOf(false);
		}
	};
	
	static final CallHandler MATCH_IDENTIFIER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}

			String self = (String) source;
			for (int i = 0; i < self.length(); i++) {
				char c = self.charAt(i);
				if( (i == 0 && !UCharacter.isLetter(c)) ||
					!UCharacter.isLetterOrDigit(c) ) {
					return Boolean.FALSE;
				}				
			}
			return Boolean.TRUE;
		}
	};

	
	static final CallHandler AS_BOOLEAN = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			String self = (String) source;
			if(self.equals("1") || self.equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
				return Boolean.TRUE;
			} else if(self.equals("0") || self.equalsIgnoreCase("false")) { //$NON-NLS-1$ //$NON-NLS-2$
				return Boolean.FALSE;
			}

			return null;
		}
	};

	static final CallHandler AS_INTEGER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			String self = (String) source;
			Integer result = null;
			try {
				result = Integer.valueOf(self);
			} catch (NumberFormatException e) {
				// result will be null
			}
			return result;
		}
	};
	
	static final CallHandler AS_FLOAT = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			String self = (String) source;
			Double result = null;
			try {
				result = Double.valueOf(self);
			} catch (NumberFormatException e) {
				// result will be null
			}
			return result;
		}
	};
	
	
	static final CallHandler START_STR_COUNTER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}
			
			String counterName = (String) leftVal;
			StringCounter.getInstance(evalEnv).start(counterName);
			return null;
		}
	};
		
	static final CallHandler GET_STR_COUNTER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}
			
			String counterName = (String) leftVal;
			return StringCounter.getInstance(evalEnv).value(counterName);
		}
	};
	
	static final CallHandler INCR_STR_COUNTER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			Object leftVal = args[0];
			if(leftVal == null && leftVal == CallHandlerAdapter.getInvalidResult(evalEnv)) {
				return false;
			}
			
			String counterName = (String) leftVal;
			return StringCounter.getInstance(evalEnv).increment(counterName);
		}
	};	

	static final CallHandler RESTART_ALL_STR_COUNTER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			StringCounter.getInstance(evalEnv).restartAll();
			return null;
		}
	};
	
	static final CallHandler ADD_SUFFIX_NUMBER = new CallHandler() {
		
		public Object invoke(ModuleInstance module, Object source, Object[] args, QvtOperationalEvaluationEnv evalEnv) {			
			String self = (String) source;			
			StringCounter counters = StringCounter.getInstance(evalEnv);
			Integer currentValue = counters.value(self);
			
			if(currentValue == null) {
				counters.start(self);
				return self;
			}
			
			counters.increment(self);
			return self + currentValue.toString();
		}
	};

	
	private static class StringCounter {
		
		static final ISessionData.SimpleEntry<StringCounter> DATA_KEY = new ISessionData.SimpleEntry<StringCounter>(); 
		
		HashMap<String, CounterValue> counters = new HashMap<String, CounterValue>();
		
		static StringCounter getInstance(QvtOperationalEvaluationEnv env) {
			 env.getThisOfType(QvtOperationalStdLibrary.INSTANCE.getStdLibModule());
			// FIXME - this way we have String counters in global only, should be done 
			// per Stdlib instance (every aggregated transformation)
			StringCounter counters = (StringCounter)env.getContext().getSessionData().getValue(DATA_KEY);
			if(counters == null) {
				counters = new StringCounter() ;
				env.getContext().getSessionData().setValue(DATA_KEY, counters);
			}
			
			return counters;
		}

		void restartAll() {
			for (CounterValue nextCounter : counters.values()) {
				nextCounter.reset();
			}
		}
		
		CounterValue get(String key, boolean onDemandCreate) {
			CounterValue value = counters.get(key);
			if(value == null && onDemandCreate) {
				value = new CounterValue();
				counters.put(key, value);
			}
			return value;
		}
		
		void start(String key) {
			get(key, true).reset();
		}
		
		int increment(String key) {
			return get(key, true).increment();
		}
		
		Integer value(String key) {
			CounterValue counterValue = get(key, false);
			if(counterValue != null) {
				return counterValue.value;
			}
			return null;
		}
	}
		
	private static class CounterValue {
		
		int value;

		CounterValue() {
			reset();
		}
		
		int increment() {
			return ++value;
		}
				
		void reset() {
			value = 0;
		}		
	}
	
}
