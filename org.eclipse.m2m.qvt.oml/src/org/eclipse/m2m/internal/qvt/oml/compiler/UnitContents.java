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
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

public interface UnitContents {

	interface CSTContents extends UnitContents {
		Reader getContents() throws IOException;
	}
	
	interface ModelContents extends UnitContents {
		
		/**
		 *  Problems from last attempt to load contents or <code>null</code>
		 *  if all was OK.
		 * @return the problem diagnostic or <code>null</code>
		 */
		Diagnostic getProblems();

		// TODO - pass options in a map for more flexibility
		List<EObject> loadElements(EPackage.Registry packageRegistry);
	}
}
