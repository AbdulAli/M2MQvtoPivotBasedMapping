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
 *     Christopher Gerking - bugs 289982, 427237
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.blackbox.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated Java operation is a QVT operation, typically
 * owned by a <code>Module</code> (<code>Library</code>,
 * <code>OperationalTransformation</code>).
 * 
 * @see Kind
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)	
@Target(ElementType.METHOD)
public @interface Operation {
	/**
	 * Indicates the <code>kind</code> of the annotated operation with respect
	 * to semantics in the QVT Abstract Syntax meta-model.
	 * 
	 * @see Operation
	 */
	public enum Kind {
		/**
		 * The operation is defined as <code>helper</code>, an imperative operation with possible side-effect.  
		 */
		HELPER,
		/**
		 * The operation is defined as<code>query</code>, a side-effect free imperative operation.  
		 */				
		QUERY,
		/**
		 * The operation is defined as operational<code>mapping</code>.  
		 * @since 3.4
		 */				
		MAPPING,
		/**
		 * The operation is defined as contextual<code>constructor</code>.  
		 * @since 3.4
		 */				
		CONSTRUCTOR,
		/**
		 * The operation is defined as operational<code>transformation</code>.  
		 * @since 3.4
		 */				
		TRANSFORMATION,
		/**
		 *  The operation is defined as normal operation. If the associated operation is defined as 
		 *  <code>contextual</code>, the Typedef class is used to represent it as additional operation
		 *  in the owning <code>Module</code> class.
		 */
		OPERATION
	};

	/**
	 * Indicates whether the associated operation is <code>contextual</code> or
	 * not.
	 * 
	 * @return <code>true</code> if the associated operation is
	 *         <code>contextual</code>, otherwise <code>false</code>.
	 */
	boolean contextual() default false;		
	
	/**
	 * Indicates whether the associated operation would receive execution 
	 * context (of <code>org.eclipse.m2m.qvt.oml.util.IContext</code> class) as the first parameter.
	 * 
	 * @return <code>true</code> if the associated operation receives execution context as the first
	 *         parameter, otherwise <code>false</code> (default value).
	 * @since 3.0
	 */
	boolean withExecutionContext() default false;
	
	/**
	 * Indicates the operation kind specified for the annotated Java operation 
	 * @return the kind object
	 */
	Kind kind() default Kind.HELPER;

	/**
	 * The textual description for the annotated operation, typically representing the detail
	 * semantics information, useful for a tooling support.
	 * @return the String object (empty string if nothing has been explicitly specified)
	 */
	String description() default "";
	
}