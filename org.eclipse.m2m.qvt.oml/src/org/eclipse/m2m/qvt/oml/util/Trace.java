/*******************************************************************************
 * Copyright (c) 2015 Borland Software Corporation and others.
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * This class is intended to manipulate with org.eclipse.m2m.internal.qvt.oml.trace.Trace object.
 * 
 * @author sboyko
 * @since 3.5
 */
public class Trace {
	
	public static Trace createEmptyTrace() {
		return new Trace(Collections.<EObject>emptyList());
	}
	
	public Trace(List<? extends EObject> traceContent) {
		fTraceContent = new LinkedList<EObject>(traceContent);
	}
	
	public List<EObject> getTraceContent() {
		return Collections.unmodifiableList(fTraceContent);
	}
	
	public void setTraceContent(List<? extends EObject> traceContent) {
		fTraceContent.clear();
		fTraceContent.addAll(traceContent);
	}
	
	private final List<EObject> fTraceContent;

}
