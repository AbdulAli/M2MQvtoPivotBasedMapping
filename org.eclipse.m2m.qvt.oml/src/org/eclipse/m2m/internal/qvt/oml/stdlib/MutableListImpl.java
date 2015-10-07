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
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.m2m.qvt.oml.util.MutableList;

/**
 * Standard Library mutable list implementation.
 * 
 * @author dvorak
 */
public class MutableListImpl<E> extends ArrayList<E> implements MutableList<E> {

	private static final long serialVersionUID = 3379624015755914365L;

	
	public MutableListImpl() {
		super();
	}

	public MutableListImpl(Collection<? extends E> c) {
		super(c);
	}

	public MutableListImpl(int initialCapacity) {
		super(initialCapacity);
	}

	public String joinfields(String sep, String begin, String end) {
		StringBuilder result = new StringBuilder();
		result.append(begin);
		
		int pos = 0;
		for (E nextElement : this) {
			if(pos++ > 0) {
				result.append(sep);
			}
			result.append(String.valueOf(nextElement));
		}
		
		result.append(end);
		return result.toString();
	}

	public void append(E element) {
		this.add(element);
	}
	
	public void insertAt(E element, int at) {
		add(at - 1, element);
	}
	
	public void prepend(E element) {
		this.add(0, element);
	}
}
