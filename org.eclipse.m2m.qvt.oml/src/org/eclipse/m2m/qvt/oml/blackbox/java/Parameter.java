/*******************************************************************************
 * Copyright (c) 2008, 2010 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.blackbox.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated Java parameter element is parameter of QVT
 * operation.
 * 
 * @see Operation
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

	/**
	 * The parameter name in the namespace of its owning QVT operation. It
	 * enables to associate a name to a parameter of Java
	 * {@linkplain java.lang.reflect.Method Method} that is available in
	 * runtime, typically useful in tooling support. Otherwise, a unique
	 * synthetic name is generated and to be set in the QVT Abstract Syntax.
	 */
	String name();

	/**
	 * Gets the textual description for the annotated parameter, typically
	 * representing the detail semantics information, useful for a tooling
	 * support.
	 * 
	 * @return the String object (empty string if nothing has been explicitly
	 *         specified)
	 */
	String description() default "";
}