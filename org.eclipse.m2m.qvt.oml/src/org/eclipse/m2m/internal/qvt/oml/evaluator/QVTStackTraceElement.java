/*******************************************************************************
 * Copyright (c) 2008, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.qvt.oml.ExecutionStackTraceElement;


public final class QVTStackTraceElement implements ExecutionStackTraceElement  {
	
    private String fModuleName;
    private String fOperationName;
    private String fUnitName;
    private int    fLineNum;

    /**
     * Creates a stack trace element representing the specified execution
     * point.
     * @throws NullPointerException if <tt>fModuleName</tt> or
     *         <tt>fOperationName</tt> is null
     * @since 1.5
     */
    public QVTStackTraceElement(String moduleName, String operationName, String unitQName, int lineNumber) {
        if (moduleName == null) {
            throw new NullPointerException("Module name is null"); //$NON-NLS-1$
        }
        
        if (operationName == null) {
            throw new NullPointerException("operation name is null"); //$NON-NLS-1$
        }
 
		this.fModuleName = moduleName;
		this.fOperationName = operationName;
		this.fUnitName = unitQName;
		this.fLineNum = lineNumber > 0 ? lineNumber : -1;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ExecutionStackTraceElement#getUnitName()
	 */
    public String getUnitName() {
        return fUnitName;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ExecutionStackTraceElement#getLineNumber()
	 */
    public int getLineNumber() {
        return fLineNum;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ExecutionStackTraceElement#getModuleName()
	 */
    public String getModuleName() {
        return fModuleName;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.evaluator.ExecutionStackTraceElement#getOperationName()
	 */
    public String getOperationName() {
        return fOperationName;
    }
    
    @Override
	public String toString() {
    	StringBuilder buf = new StringBuilder();
    	buf.append(getModuleName()).append(EmfUtil.PATH_SEPARATOR).append(getOperationName());
    	if(fUnitName != null) {
    		buf.append('(').append(fUnitName).append(':');    		
    		if(fLineNum > 0) {
    			buf.append(fLineNum);
    		} else {
    			buf.append("<Unknown>"); //$NON-NLS-1$
    		}
    		buf.append(')');
    	} else {
    		buf.append("(Unknown Source)"); //$NON-NLS-1$
    	}
    	
        return buf.toString();
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof QVTStackTraceElement)) {
			return false;
		}
		QVTStackTraceElement e = (QVTStackTraceElement) obj;
		return e.fModuleName.equals(fModuleName) && e.fLineNum == fLineNum
				&& eq(fOperationName, e.fOperationName)
				&& eq(fUnitName, e.fUnitName);
	}

    @Override
	public int hashCode() {
		int result = 31 * fModuleName.hashCode() + fOperationName.hashCode();
		result = 31 * result + (fUnitName == null ? 0 : fUnitName.hashCode());
		result = 31 * result + fLineNum;
		return result;
	}
    
	private static boolean eq(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
}
