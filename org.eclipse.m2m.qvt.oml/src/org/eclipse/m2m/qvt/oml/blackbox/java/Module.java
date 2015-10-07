/*******************************************************************************
 * Copyright (c) 2008, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 326871
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.blackbox.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated Java class is a QVT <code>Module</code>.
 * @since 3.4
 */
@Retention(RetentionPolicy.RUNTIME)	
@Target(ElementType.TYPE)
public @interface Module {
		
	/**
	 * The package namespace URIs accessed by the signature types of the annotated operation.
	 * @return the package namespace URIs
	 */
	String[] packageURIs() default {};

}