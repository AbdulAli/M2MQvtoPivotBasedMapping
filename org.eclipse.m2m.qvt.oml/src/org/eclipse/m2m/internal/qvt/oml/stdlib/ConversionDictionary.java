/*******************************************************************************
 * Copyright (c) 2009, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alex Paperno
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.m2m.qvt.oml.util.Dictionary;
import org.eclipse.m2m.qvt.oml.util.Utils;

/**
 * @since 3.4
 */
public class ConversionDictionary implements ConversionDelegate {

	final private DictionaryType fMyType;

	public ConversionDictionary(DictionaryType type) {
		this.fMyType = type;
	}
	
	public String convertToString(Object value) {
		EClassifier keyType = fMyType.getKeyType();
		EClassifier elementType = fMyType.getElementType();
		if (!(value instanceof Dictionary<?, ?>) 
				|| !(keyType instanceof EDataType)
				|| !(elementType instanceof EDataType)) {
			return ""; //$NON-NLS-1$
		}
		
		// support nested declarations
		ConversionUtils.setupConversionDelegate(keyType);
		ConversionUtils.setupConversionDelegate(elementType);
		
		StringBuilder result = new StringBuilder();
		@SuppressWarnings("unchecked")
		Dictionary<Object, Object> dictionary = (Dictionary<Object, Object>)value;
		for (Object key : dictionary.keys()) {
			if (result.length() > 0) {
				result.append(", "); //$NON-NLS-1$
			}
			String data = keyType.getEPackage().getEFactoryInstance().convertToString((EDataType)keyType, key);
	        if (ConversionUtils.isStringType(keyType)) {
	        	data = escapeDictionaryElem(data);
	        }
	        result.append(data);
	        result.append("="); //$NON-NLS-1$
			data = elementType.getEPackage().getEFactoryInstance().convertToString((EDataType)elementType, dictionary.get(key));
	        if (ConversionUtils.isStringType(elementType)) {
	        	data = escapeDictionaryElem(data);
	        }
	        result.append(data);
		}		
		return result.insert(0, "[").append("]").toString();  //$NON-NLS-1$  //$NON-NLS-2$
	}

	public Object createFromString(String literal) {
		EClassifier keyType = fMyType.getKeyType();
		EClassifier elementType = fMyType.getElementType();
		if (!(keyType instanceof EDataType)) {
			throw new IllegalArgumentException("Invalid datatype: " + keyType.getName()); //$NON-NLS-1$
		}
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
		ConversionUtils.setupConversionDelegate(keyType);
		ConversionUtils.setupConversionDelegate(elementType);
		
		Dictionary<Object, Object> result = Utils.createDictionary();		
		for (int i = 0; i < literal.length(); ++i) {
			int endIndex = extractDictionaryKey(literal, i);
			Object key = ConversionUtils.createFromString(keyType, ConversionCollection.unescapeString(literal.substring(i, endIndex)).trim());
			i = endIndex+1;
			endIndex = extractDictionaryValue(literal, i);
			Object value = ConversionUtils.createFromString(elementType, ConversionCollection.unescapeString(literal.substring(i, endIndex)).trim());
			i = endIndex;
			result.put(key, value);
		}
		return result;
	}

	private static String escapeDictionaryElem(String literal) {
		StringBuilder result = new StringBuilder(literal.length());
		for (int i = 0; i < literal.length(); ++i) {
			char ch = literal.charAt(i);
			switch (ch) {
			case '\\':
			case ',':
			case '=':
			case '[':
			case ']':
				result.append('\\');
			default:
				result.append(ch);
			}
		}		
		return result.toString();
	}

	private static int extractDictionaryKey(String literal, int startIndex) {
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
			case '=':
				if (level == 0) {
					return i;
				}
				break;
			}
		}		
		throw new IllegalArgumentException("Invalid dictionary key: " + literal.substring(startIndex)); //$NON-NLS-1$
	}

	private static int extractDictionaryValue(String literal, int startIndex) {
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
