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
package org.eclipse.m2m.qvt.oml.util;

import java.util.List;

/**
 * A mutable list type, representing the QVT Stdlib list type in runtime.
 * 
 * @author dvorak
 * @since 2.0
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface MutableList<E> extends List<E> {
	
	/**
	 * @since 3.5
	 */
	String SINGLETON_NAME = "List"; //$NON-NLS-1$
	
	/** 
	 * Adds a value at the end of the mutable list.
	 * 
	 * @param element the element object to be added
	 */
	void append(E element);

	/**
	 * Adds element at the beginning of the mutable list.
	 * 
	 * @param element the element object to be prepended
	 */
	void prepend(E element);

	/**
	 * Adds element at the given position.
	 * 
	 * @param element
	 * @param at
	 *            the 1-based position (in compliance with OCL convention).
	 */	
	void insertAt(E element, int at);

	/**
	 * Creates a string separated by sep and delimited with begin and end
	 * strings.
	 * 
	 * @param sep
	 *            separator string
	 * @param begin
	 *            starting delimiter
	 * @param end
	 *            ending delimiter
	 * @return the composed string
	 */
	String joinfields(String sep, String begin, String end);
}
