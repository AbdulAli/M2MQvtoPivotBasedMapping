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
package org.eclipse.m2m.internal.qvt.oml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.emf.common.EMFPlugin;

public class NLS {
	
	private static IMessages messagesImpl = (EMFPlugin.IS_ECLIPSE_RUNNING) ? new OSGI_NLS() : new Default_NLS();
	
	protected NLS() {
		super();
	}
	
	public static String bind(String message, Object arg) {
		return messagesImpl.bind(message, arg);
	}
	
	public static String bind(String message, Object arg1, Object arg2) {
		return messagesImpl.bind(message, arg1, arg2);
	}
	
	public static String bind(String message, Object[] args) {
		return messagesImpl.bind(message, args);
	}		
	
	public static void initializeMessages(String bundleName, Class<?> clazz) {
		if(EMFPlugin.IS_ECLIPSE_RUNNING) {
			org.eclipse.osgi.util.NLS.initializeMessages(bundleName, clazz);
		} else {
			standaloneInitializeMessages(bundleName, clazz);
		}			
	}
	
	private static void standaloneInitializeMessages(String bundleName, Class<?> clazz) {
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
		int publicStatic = Modifier.PUBLIC | Modifier.STATIC;
				
		if (bundle != null) {
			Field[] fields = clazz.getDeclaredFields();
			
			for (int i = 0; i < fields.length; i++) {
				Field next = fields[i];
				
				if (((next.getModifiers() & publicStatic) == publicStatic)
						&& (next.getType() == String.class)) {
					String name = next.getName();
					
					try {
						try {
							next.set(null, bundle.getString(name));
						} catch (MissingResourceException e) {
							// just use its own name, then
							next.set(null, "Missing message for key: " + name); //$NON-NLS-1$
						}
					} catch (Exception e) {
						// oh, well.  Can't set a value for this one
					}
				}
			}
		}
	}		
	
	private interface IMessages {
		
		String bind(String message, Object arg);
		String bind(String message, Object arg1, Object arg2);
		String bind(String message, Object[] args);
	}

	private static class Default_NLS implements IMessages {
		Default_NLS() {
			super();
		}
		
		public String bind(String message, Object arg) {
			return MessageFormat.format(message, new Object[] {arg});
		}

		public String bind(String message, Object arg1, Object arg2) {
			return MessageFormat.format(message, new Object[] {arg1, arg2});
		}

		public String bind(String message, Object[] args) {
			return MessageFormat.format(message, args);
		}
	}
	
	private static class OSGI_NLS implements IMessages {
		public OSGI_NLS() {
			super();
		}

		public String bind(String message, Object arg) {
			return org.eclipse.osgi.util.NLS.bind(message, arg);
		}

		public String bind(String message, Object arg1, Object arg2) {
			return org.eclipse.osgi.util.NLS.bind(message, arg1, arg2);
		}

		public String bind(String message, Object[] args) {
			return org.eclipse.osgi.util.NLS.bind(message, args);
		}
	}		
}