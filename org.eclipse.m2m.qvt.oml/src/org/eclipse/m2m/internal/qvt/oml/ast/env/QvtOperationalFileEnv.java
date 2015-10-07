/*******************************************************************************
 * Copyright (c) 2007 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Alex Paperno - bugs 416584
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.ArrayList;
import java.util.List;

import lpg.runtime.ParseErrorCodes;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

public class QvtOperationalFileEnv extends QvtOperationalModuleEnv {

	private final URI myFile;    
	private List<QvtOperationalModuleEnv> innerEnvs;
	
	protected QvtOperationalFileEnv(final URI uri, EPackage.Registry registry) {
		// TODO - revisit the null resource passed
		super(new EPackageRegistryImpl(registry), null);
		this.innerEnvs = new ArrayList<QvtOperationalModuleEnv>(1);

		if(uri == null) {
			throw new IllegalArgumentException("Non-null uri required"); //$NON-NLS-1$
		}
		
		myFile = uri;
		
    	QvtOperationalStdLibrary.INSTANCE.importTo(this);        
	}
	
	public List<QvtOperationalModuleEnv> getInnerEnvironments() {
		return innerEnvs;
	}

	public void addInnerEnvironment(QvtOperationalModuleEnv env) {
		innerEnvs.add(env);
	}
	

	public URI getFile() {
        return myFile;
    }
	    	    
    @Override
    public String toString() {    
    	return  "QVTOEnv:" + myFile.toString(); //$NON-NLS-1$
    }
    
    @Override
    public void parserError(int errorCode, int leftToken, int rightToken, String tokenText) {
    	if (tokenText.equals("\"qvtErrorToken\"")) { //$NON-NLS-1$
    		if (errorCode == ParseErrorCodes.SUBSTITUTION_CODE) {
    			errorCode = ParseErrorCodes.INVALID_TOKEN_CODE;
    		}
    	}
    	super.parserError(errorCode, leftToken, rightToken, tokenText);
    }
    
}
