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
package org.eclipse.m2m.qvt.oml.util;

import java.io.StringWriter;

/**
 * A convenience implementation of a String content based log, mainly
 * suitable for testing and debugging purposes.
 * 
 * @author dvorak
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class StringBufferLog extends WriterLog {

	private final StringWriter fStrWriter;
	
	/**
	 * Constructs a default log object.
	 */
	public StringBufferLog() {
		this(1024);
	}

	/**
	 * Constructs a log object of the given initialize size.
	 * 
	 * @param initialsize
	 *            the initial size of the string buffer associated with this log
	 */	
	public StringBufferLog(int initialsize) {
		super(new StringWriter(initialsize));
		
		fStrWriter = (StringWriter) getWriter(); 
	}
	
	public String getContents() {
		return fStrWriter.getBuffer().toString();
	}
}
