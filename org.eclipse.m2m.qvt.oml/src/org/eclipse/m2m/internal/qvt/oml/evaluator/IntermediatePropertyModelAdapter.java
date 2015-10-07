/*******************************************************************************
 * Copyright (c) 2008, 2010 Borland Software Corporation and others.
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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.cst.adapters.AbstractGenericAdapter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;

/**
 * @author sboyko
 *
 */
public class IntermediatePropertyModelAdapter extends AbstractGenericAdapter<IntermediatePropertyModelAdapter> {
	
	private IntermediatePropertyModelAdapter() {
	}
	
    public boolean isAdapterForType(Object type) {
        return type == IntermediatePropertyModelAdapter.class;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntermediatePropertyModelAdapter;
    }

    @Override
    public int hashCode() {
        return IntermediatePropertyModelAdapter.class.hashCode();
    }

    public static ShadowEntry getPropertyHolder(EObject moduleInstance, ContextualProperty property, Object ownerInstance) {
    	IntermediatePropertyModelAdapter adapter = (IntermediatePropertyModelAdapter) EcoreUtil.getAdapter(moduleInstance.eAdapters(),
    			IntermediatePropertyModelAdapter.class);
    	if (adapter == null) {
    		adapter = new IntermediatePropertyModelAdapter();
    		moduleInstance.eAdapters().add(adapter);
    	}
    	
    	return adapter.getPropertyHolder(property, ownerInstance);
    }
    
    private ShadowEntry getPropertyHolder(ContextualProperty property, Object ownerInstance) {
    	ShadowEntry runtimeShadow = myProp2HolderMap.get(property);
    	if(runtimeShadow == null) {
    		runtimeShadow = new ShadowEntry(myPropShadowHelper.createShadowProperty(property));
    		myProp2HolderMap.put(property, runtimeShadow);
    	}
		
		return runtimeShadow;
	}
    
    private static Object getInitialValue(EStructuralFeature feature, QvtOperationalEvaluationEnv evalEnv) {
		//return myInitExpression.accept(evalVisitor);
    	return EvaluationUtil.createInitialValue(feature.getEType(), QvtOperationalStdLibrary.INSTANCE.getOCLStdLib(), evalEnv);
    }

	private final Map<ContextualProperty, ShadowEntry> myProp2HolderMap = new IdentityHashMap<ContextualProperty, ShadowEntry>(2);
	private final RuntimePropertyShadowHelper myPropShadowHelper = new RuntimePropertyShadowHelper();
	

	public static class ShadowEntry {
		private EStructuralFeature myFeature;
		private Map<Object, EObject> myOwner2ShadowMap = new IdentityHashMap<Object, EObject>();
		
		private ShadowEntry(EStructuralFeature runtimeFeature) {
			assert runtimeFeature != null;
			myFeature = runtimeFeature;
		}
		
		public EStructuralFeature getProperty() {
			return myFeature;
		}
		
		public EObject getPropertyRuntimeOwner(Object shadowedInstance, QvtOperationalEvaluationEnv evalEnv) {
			EObject owner = myOwner2ShadowMap.get(shadowedInstance);
			if (owner == null) {
				EClass ownerClass = myFeature.getEContainingClass();
				EFactory eFactory = ownerClass.getEPackage().getEFactoryInstance();		
				owner = eFactory.create(ownerClass);
				myOwner2ShadowMap.put(shadowedInstance, owner);				

		    	Object initialValue = getInitialValue(myFeature, evalEnv);
		    	if (initialValue != null) {
		    		owner.eSet(myFeature, initialValue);
		    	}												
			}
			return owner;
		}
	}
	/* https://bugs.eclipse.org/bugs/show_bug.cgi?id=279251 */
    public static void cleanup(Module module) {
    	cleanup(module, new HashSet<Module>());
    }

    private static void cleanup(Module module, Set<Module> processed) {
		Adapter adapter = EcoreUtil.getExistingAdapter(module, IntermediatePropertyModelAdapter.class);
		if(adapter != null) {
			module.eAdapters().remove(adapter);
		}
		
		processed.add(module);
		
    	for(ModuleImport nextImport : module.getModuleImport()) {
    		Module importedModule = nextImport.getImportedModule();
    		if(!processed.contains(importedModule)) {
    			cleanup(importedModule, processed);
    		}
    	}
    }
}
