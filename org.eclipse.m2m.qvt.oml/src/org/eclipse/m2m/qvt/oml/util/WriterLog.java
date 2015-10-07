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

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;

/**
 * A basic logger backed-up with {@link Writer}. 
 * 
 * @author dvorak
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class WriterLog implements Log {
	
	private static final String LINE_SEP = System.getProperty("line.separator"); //$NON-NLS-1$
		
	private Writer fWriter;	
	private String fRecordSeparator;
	private boolean fFlush;
	private boolean fErrorLogged;
	
	/**
	 * Constructs a log for the given writer object.
	 * 
	 * @param writer
	 *            the writer object to receive the log record data
	 * @throws IllegalArgumentException
	 *             if the passed <code>writer</code> is <code>null</code>
	 */
	public WriterLog(Writer writer) {
		this(writer, true);
	}
	
	/**
	 * Constructs a log for the given writer object.
	 * 
	 * @param writer
	 *            the writer object to receive the log record data
	 * @param flush
	 *            if <code>true</code>, the writer gets flushed with every
	 *            record logged. If set to <code>false</code>, the flushing
	 *            behavior is dependent on the writer implementation used
	 * @throws IllegalArgumentException
	 *             if the passed <code>writer</code> is <code>null</code>
	 * @since 3.0
	 */
	public WriterLog(Writer writer, boolean flush) {
		if(writer == null) {
			throw new IllegalArgumentException("null writer"); //$NON-NLS-1$
		}
		
		fWriter = writer; 
		fRecordSeparator = LINE_SEP;
		fErrorLogged = false;
		fFlush = flush;
	}	
	
	protected final Writer getWriter() {
		return fWriter;
	}
	
	public void log(int level, String message, Object param) {
		String recordStr = MessageFormat.format("Level {0} - {1}, data: {2}", level, message, String.valueOf(param)); //$NON-NLS-1$
		logRecord(recordStr);
	}
	
	public void log(int level, String message) {
		String recordStr = MessageFormat.format("Level {0} - {1}", level, message); //$NON-NLS-1$
		logRecord(recordStr);
	}

	public void log(String message, Object param) {
		String recordStr = MessageFormat.format("{0}, data: {1}", message, String.valueOf(param)); //$NON-NLS-1$
		logRecord(recordStr);
	}		
	
	public void log(String message) {
		logRecord(message);
	}
	
	private void logRecord(String recordStr) {
		try {
			fWriter.write(recordStr);
			fWriter.write(fRecordSeparator);
			if(fFlush) {
				fWriter.flush();
			}
		} catch (IOException e) {
			if(!fErrorLogged) {
				QvtPlugin.error(e);
				fErrorLogged = true;
			}
		}
	}	
}
