/*******************************************************************************
 * Copyright (c) 2009, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.util;

import java.util.Collection;

import org.eclipse.m2m.internal.qvt.oml.stdlib.DictionaryImpl;
import org.eclipse.m2m.internal.qvt.oml.stdlib.MutableListImpl;

/**
 * @author dvorak
 * @since 2.0
 */
public final class Utils {
	
	private Utils() {
		super();
	}
	
	public static <KeyT, T> Dictionary<KeyT, T> createDictionary() {
		return new DictionaryImpl<KeyT, T>();
	}
	
	/**
	 * @since 3.5
	 */
	public static <KeyT, T> Dictionary<KeyT, T> createDictionary(Collection<? extends KeyT> c) {
		DictionaryImpl<KeyT, T> dict = new DictionaryImpl<KeyT, T>();
		for (KeyT o : c) {
			dict.put(o, null);
		}
		return dict;
	}
	
	public static <T> MutableList<T> createList() {
		return new MutableListImpl<T>();
	}
	
	/**
	 * @since 3.5
	 */
	public static <T> MutableList<T> createList(Collection<? extends T> c) {
		return new MutableListImpl<T>(c);
	}
	
}
