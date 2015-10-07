/*******************************************************************************
 * Copyright (c) 2014 Borland Software Corporation and others.
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

import java.util.Set;


/**
 * Unit resolver dedicated for walking along hierarchy of unit resolvers.
 * Unlike <code>UnitResolver</code> it provides the ability to control nodes of resolver's hierarchy.   
 * 
 * @author bsm
 * @see    org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolver
 *
 */
public interface RecursiveUnitResolver {

	UnitProxy resolveUnit(String qualifiedName, Set<RecursiveUnitResolver> accessedParents);
}
