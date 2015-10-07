/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 431082
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.library;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.qvt.oml.util.EvaluationMonitor;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.m2m.qvt.oml.util.ISessionData;
import org.eclipse.m2m.qvt.oml.util.Log;
import org.eclipse.m2m.qvt.oml.util.Trace;

/**
 * @noextend
 */
@SuppressWarnings("deprecation")
public class Context implements IContext {

    private final Map<String, Object>  myConfiguration;
    private SessionDataImpl myData;

    private Log myLog;
    private IProgressMonitor myMonitor;
    private Trace myTrace;
    
    public Context() {
    	myConfiguration = new HashMap<String, Object>();
    	myLog = Log.NULL_LOG;
		myMonitor = new NullProgressMonitor();
		myTrace = Trace.createEmptyTrace();
		myData = new SessionDataImpl();
    }

    public void setProgressMonitor(IProgressMonitor monitor) {
    	if(monitor == null) {
    		throw new IllegalArgumentException("Non-null monitor required"); //$NON-NLS-1$
    	}

		this.myMonitor = monitor;
	}
    
    public IProgressMonitor getProgressMonitor() {    
    	return myMonitor;
    }
    
    /**
     * @deprecated Use getProgressMonitor() method
     */
    public EvaluationMonitor getMonitor() {    
    	return EvaluationMonitor.EvaluationMonitorWrapper.convert(myMonitor);
    }
    
    public void setLog(Log log) {
    	if(log == null) {
    		throw new IllegalArgumentException("Non-null logger required"); //$NON-NLS-1$
    	}
		this.myLog = log;
	}
    
    public Log getLog() {    	
    	return myLog;
    }
            
    public Trace getTrace() {    	
    	return myTrace;
    }
            
    public void setSessionData(ISessionData sessionData) {    
    	myData = new SessionDataImpl((SessionDataImpl) sessionData);
    }
    
    public ISessionData getSessionData() {    
    	return myData;
    }
    
    public Map<String, Object> getConfigProperties() {    
    	return Collections.unmodifiableMap(myConfiguration);
    }
    
    public Object getConfigProperty(String name) {
    	if(name == null) {
    		throw new IllegalArgumentException("null config property name"); //$NON-NLS-1$
    	}
    	return myConfiguration.get(name);
    }
    
    public void setConfigProperty(String name, Object value) {    
    	if(name == null) {
    		throw new IllegalArgumentException("null config property name"); //$NON-NLS-1$
    	}
    	myConfiguration.put(name, value);
    }
    
            
    public static class SessionDataImpl implements ISessionData {
    	
    	private final Map<Entry<Object>, Object> fData;
    		
    	public SessionDataImpl(Map<Entry<Object>, Object> data) {
    		fData = data;
    	}
    	
    	SessionDataImpl() {
    		this(new HashMap<Entry<Object>, Object>());
    	}
    	
    	SessionDataImpl(SessionDataImpl sessionData) {
    		this(new HashMap<Entry<Object>, Object>(sessionData.fData));
    	}
    	
        @SuppressWarnings("unchecked")
    	public <T> T getValue(Entry<T> entry) {
        	if(fData.containsKey(entry)) {
        		return (T) fData.get(entry);
        	}
        	return entry.defaultValue();
        }
        
        @SuppressWarnings("unchecked")
		public <T> void setValue(Entry<T> key, T value) {
        	fData.put((Entry<Object>) key, value);
        }
    }    
}