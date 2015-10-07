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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.MetamodelRegistry;

/**
 * FIXME - this class extracts the logic of resolving workspace located metamodels, 
 * formerly injected into QVT Environments implementation.
 * A temporary workaround, to be revisited.
 */
class MetamodelResolutionHelper {

	/**
	 * Registers metamodel for use with this environment.
	 * 
	 * @return the metamodel package denoted by the given <code>URI</code> or
	 *         <code>null</code> if no package was resolved
	 */
	static List<EPackage> registerMetamodel(QvtOperationalEnv qvtEnv, String metamodelUri, List<String> path) {
		EPackage.Registry registry = qvtEnv.getFactory().getEPackageRegistry();
        List<EPackage> metamodels = new ArrayList<EPackage>(1);
        
		
	    List<EPackage> desc = Collections.emptyList();            
        if (metamodelUri != null && path.isEmpty()) {
            EPackage ePackage = registry.getEPackage(metamodelUri);
            if(ePackage != null) {                	
            	desc = Collections.singletonList(ePackage);
            } else {
            	ePackage = MetamodelRegistry.tryLookupEmptyRootPackage(metamodelUri, registry);
            	if(ePackage != null) {                	
                	desc = Collections.singletonList(ePackage);
                }
           }
        } else {
            desc = MetamodelRegistry.resolveMetamodels(registry, path);
        }
                    
		for(EPackage model : desc) {							        	
            // register meta-model for EClassifier lookup
        	if (model.getNsURI() == null) {
				model = EmfUtil.getRootPackage(model);
        	}
        	
        	metamodels.add(model);
        	if(metamodelUri != null) {
        		qvtEnv.getEPackageRegistry().put(metamodelUri, model);
        	}
        }
		
		return metamodels;
	}	
}
