/*******************************************************************************
 * Copyright (c) 2008, 2009 Borland Software Corporation and others.
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

import java.math.BigDecimal;
import java.math.BigInteger;


public class NumberConversions {
	
	private NumberConversions() {
		super();
	}

	/**
	 * Converts the given value to a compatible value for assignment to the
	 * target type.
	 * 
	 * @param value
	 *            the value to be converted
	 * @param targetClass
	 *            the target type as a potential left side of assignment
	 * @return A new value compatible to the required type if the conversion is
	 *         possible. Otherwise, the original passed value is returned.
	 *         Note: Non-compatible value for a conversion to the target type is returned
	 *         as is
	 */
	public static Object convertNumber(Object value, Class<?> targetClass) {
		if(targetClass == null) {
			throw new IllegalArgumentException();
		}
		
		if(value == null) {
			return value;
		}
		
		Class<?> valueClass = value.getClass();
		if(valueClass == targetClass || (value instanceof Number == false)) {
			return value;
		}

		Number numVal = (Number) value;
		if(targetClass == BigInteger.class) {
			return new BigInteger(String.valueOf(numVal.longValue()));			
		}
		else if(targetClass == Long.class || targetClass == long.class) {
			return new Long(numVal.longValue()); 
		} 
		else if(targetClass == Integer.class || targetClass == int.class) {
			return new Integer(numVal.intValue()); 
		}
		else if(targetClass == Short.class || targetClass == short.class) {
			return new Short(numVal.shortValue()); 
		}
		else if(targetClass == Byte.class || targetClass == byte.class) {
			return new Byte(numVal.byteValue()); 
		}
		else if(targetClass == BigDecimal.class) {
			return new BigDecimal(String.valueOf(numVal.doubleValue())); 
		}
		else if(targetClass == Double.class || targetClass == double.class) {
			return new Double(numVal.doubleValue()); 
		}
		else if(targetClass == Float.class || targetClass == float.class) {
			return new Float(numVal.floatValue()); 
		}

		return value; // NFE fill be raised in further processing
	}
	
	/**
	 * Converts the given value to a compatible number value for assignment to the
	 * target type.
	 * 
	 * @param value
	 *            the value to be converted
	 * @param targetClass
	 *            the target type as a potential left side of assignment
	 * @return A new value compatible to the required type if the conversion is
	 *         possible. Otherwise, <code>null</code> is returned
	 */
	public static <T extends Number> T strictConvertNumber(Object value, Class<T> targetClass) {
		Object converted = convertNumber(value, targetClass);
		return targetClass.isInstance(converted) ? targetClass.cast(converted) : null;
	}
}
