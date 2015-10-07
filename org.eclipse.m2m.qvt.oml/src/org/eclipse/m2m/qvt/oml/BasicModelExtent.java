/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EObject;

/**
 * Simple in-memory model extent implementation.
 * 
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BasicModelExtent implements ModelExtent {

	private List<EObject> fContents;

	/**
	 * Construct an empty extent, typically used as <code>[out]</code> model
	 * parameter.
	 */
	public BasicModelExtent() {
		fContents = new UniqueEList<EObject>();
	}

	/**
	 * Construct new extent with initial contents
	 * 
	 * @param contents
	 *            the elements to be the contents of the created extent.
	 */
	public BasicModelExtent(List<? extends EObject> contents) {
		this();
		setContents(contents);
	}

	/**
	 * Add a single element to this extent.
	 * 
	 * @param element
	 *            non-null element
	 */
	public void add(EObject element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		fContents.add(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ModelExtent#getContents()
	 */
	public List<EObject> getContents() {
		return Collections.unmodifiableList(fContents);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2m.qvt.oml.ModelExtent#setContents(java.util.List)
	 */
	public void setContents(List<? extends EObject> contents) {
		if (contents == null || contents.contains(null)) {
			throw new IllegalArgumentException();
		}

		fContents.clear();
		fContents.addAll(contents);
	}
}
