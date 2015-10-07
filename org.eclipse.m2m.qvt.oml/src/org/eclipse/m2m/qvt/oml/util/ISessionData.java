/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.util;


/**
 * @noimplement
 * @noextend
 * @since 3.0
 */
public interface ISessionData {

	interface Entry<T> {
		T defaultValue();
    }

	/**
	 * @throws ClassCastException
	 *             if the stored value is not of the type specified for the
	 *             given data entry
	 */
	<T> T getValue(Entry<T> entry);
    
    <T> void setValue(Entry<T> key, T value);
       

    class SimpleEntry<T> implements Entry<T> {

		private T fDefaultVal;
    	    	
    	public SimpleEntry() {
    		super();
    	}
    	
    	public SimpleEntry(T defaultVal) {
			fDefaultVal = defaultVal;
		}
    	
    	public T defaultValue() {    	
    		return fDefaultVal;
    	}    	
    }
    
	class NamedEntry<T> implements Entry<T> {
		
		private String fName;
		private T fDefaultVal;
		
		public NamedEntry(String name) {
			this(name, null);
		}
		
		public NamedEntry(String name, T defaultValue) {
			if(name == null) throw new IllegalArgumentException();
			fName = name; 
		}
		
		public T defaultValue() {		
			return fDefaultVal;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof NamedEntry<?>) {
				@SuppressWarnings("unchecked")				
				NamedEntry<T> namedEntry = (NamedEntry<T>) obj;
				return fName.equals(namedEntry.fName);
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {		
			return fName.hashCode();
		}
		
		@Override
		public String toString() {
			return fName;
		}
	}	
}