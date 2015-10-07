/*******************************************************************************
 * Copyright (c) 2009, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alex Paperno - bugs 267917
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.m2m.qvt.oml.util.Utils;
import org.eclipse.ocl.ecore.BagType;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.ecore.OrderedSetType;
import org.eclipse.ocl.ecore.SequenceType;
import org.eclipse.ocl.ecore.SetType;
import org.eclipse.ocl.util.CollectionUtil;

/**
 * @since 3.4
 */
public class ConversionCollection implements ConversionDelegate {

	final private CollectionType fMyType;

	public ConversionCollection(CollectionType type) {
		this.fMyType = type;
	}

	public String convertToString(Object value) {
		EClassifier elementType = fMyType.getElementType();
		if (!(value instanceof Collection<?>) || !(elementType instanceof EDataType)) {
			return ""; //$NON-NLS-1$
		}
		
		// support nested declarations
		ConversionUtils.setupConversionDelegate(elementType);
		
		StringBuilder result = new StringBuilder();
		@SuppressWarnings("unchecked")
		Collection<Object> collection = (Collection<Object>)value;
		for (Object element : collection) {
			if (result.length() > 0) {
				result.append(", "); //$NON-NLS-1$
			}
			String data = elementType.getEPackage().getEFactoryInstance().convertToString((EDataType)elementType, element);
	        if (ConversionUtils.isStringType(elementType)) {
	        	data = escapeCollectionElem(data);
	        }
	        result.append(data);
		}
		return result.insert(0, "[").append("]").toString();  //$NON-NLS-1$  //$NON-NLS-2$
	}

	public Object createFromString(String literal) {
		EClassifier elementType = fMyType.getElementType();
		if (!(elementType instanceof EDataType)) {
			throw new IllegalArgumentException("Invalid datatype: " + elementType.getName()); //$NON-NLS-1$
		}

		literal = literal.trim();
		if (literal.length() > 0 && literal.charAt(0) == '[') {
			if (literal.charAt(literal.length()-1) != ']') {
				throw new IllegalArgumentException("Invalid datatype: " + elementType.getName()); //$NON-NLS-1$
			}
			literal = literal.substring(1, literal.length()-1);
		}

		// support nested declarations
		ConversionUtils.setupConversionDelegate(elementType);
		
		List<Object> result = Utils.createList();
		for (int i = 0; i < literal.length(); ++i) {
			int endIndex = extractCollectionElement(literal, i);
			Object value = ConversionUtils.createFromString(elementType, unescapeString(literal.substring(i, endIndex)).trim());
			result.add(value);
			i = endIndex;
		}

		if (fMyType instanceof ListType) {
			return result;
		}
		if (fMyType instanceof SetType) {
			return CollectionUtil.createNewSet(result);
		}
		if (fMyType instanceof OrderedSetType) {
			return CollectionUtil.createNewOrderedSet(result);
		}
		if (fMyType instanceof SequenceType) {
			return CollectionUtil.createNewSequence(result);
		}
		if (fMyType instanceof BagType) {
			return CollectionUtil.createNewBag(result);
		}

		throw new IllegalArgumentException("Invalid datatype: " + fMyType.getName()); //$NON-NLS-1$
	}

	private static String escapeCollectionElem(String literal) {
		StringBuilder result = new StringBuilder(literal.length());
		for (int i = 0; i < literal.length(); ++i) {
			char ch = literal.charAt(i);
			switch (ch) {
			case '\\':
			case ',':
			case '[':
			case ']':
				result.append('\\');
			default:
				result.append(ch);
			}
		}		
		return result.toString();
	}

	static String unescapeString(String literal) {
		char[] convtBuf = new char[literal.length()];
		int outLen = 0;
		for (int i = 0; i < literal.length(); ++i) {
			char ch = literal.charAt(i);
			switch (ch) {
			case '\\':
				++i;
				if (i < literal.length()) {
					convtBuf[outLen] = literal.charAt(i);
				}
				break;
			default:
				convtBuf[outLen] = ch;
			}
			++outLen;
		}		
		return new String(convtBuf, 0, outLen);
	}

	private static int extractCollectionElement(String literal, int startIndex) {
		int level = 0;
		for (int i = startIndex; i < literal.length(); ++i) {
			switch (literal.charAt(i)) {
			case '\\':
				++i;
				break;
			case '[':
				++level;
				break;
			case ']':
				--level;
				break;
			case ',':
				if (level == 0) {
					return i;
				}
				break;
			}
		}		
		return literal.length();
	}

}
