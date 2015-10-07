/*******************************************************************************
 * Copyright (c) 2007, 2009 Borland Software Corporation and others.
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



/**
 * @author feldman
 */
public class QvtMessage {
	
	public static final int SEVERITY_ERROR = 2; //IMarker.SEVERITY_ERROR;
	
	public static final int SEVERITY_WARNING = 1; //IMarker.SEVERITY_WARNING;
	
	public QvtMessage(final String message, final int severity, final int offset, final int length, int lineNum) {
		myMessage = message;
		mySeverity = severity;
		myPos = new TextPositionsImpl(offset, length);
		this.myLineNum = lineNum;
	}

	public QvtMessage(final String message, final int offset, final int length) {
		this(message, SEVERITY_ERROR, offset, length, -1);
	}
	
	public QvtMessage(final String message) {
		this(message, 0, 0);
	}
	
	public int getOffset() {
		return myPos.getOffset();
	}

	public int getLength() {
		return myPos.getLength();
	}

	public String getMessage() {
		return myMessage;
	}
	
	public int getSeverity() {
		return mySeverity;
	}
	
	public int getLineNum() {
		return myLineNum;
	}
	
	@Override
	public String toString() {
		return (getLineNum() > 0 ? "Line " + getLineNum() + ':' : myPos.toString()) + " " + myMessage; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof QvtMessage == false) {
			return false;
		}
		
		QvtMessage other = (QvtMessage)o;
		
		if(mySeverity != other.mySeverity) {
			return false;
		}
		
		if (myMessage == null ? other.myMessage != null : !myMessage.equals(other.myMessage)) {
			return false;
		}
		
		if (myPos == null ? other.myPos != null : !myPos.equals(other.myPos)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash*37 + mySeverity;
		
		if (myMessage != null) {
			hash = hash*37 + myMessage.hashCode();
		}
		
		if (myPos != null) {
			hash = hash*37 + myPos.hashCode();
		}
		
		return hash;
	}

	private class TextPositionsImpl {

		public TextPositionsImpl(final int offset, final int length) {
			myOffset = offset;
			myLength = length;
		}
		
		public int getOffset() {
			return myOffset;
		}
		
		public int getLength() {
			return myLength;
		}
		
		@Override
		public String toString() {
			return "[" + myOffset + ":" + (myOffset + myLength) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof TextPositionsImpl == false) {
				return false;
			}
			
			TextPositionsImpl other = (TextPositionsImpl) o;
			return myOffset == other.myOffset && myLength == other.myLength;
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash*37 + myOffset;
			hash = hash*37 + myLength;
			
			return hash;
		}
		
		private final int myOffset;
		private final int myLength;
	}
	
	private final int myLineNum;
	private final int mySeverity;
	private final String myMessage;
	private final TextPositionsImpl myPos;
}