/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation and others.
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.UMLReflection;


public class IntermediatePropertyHierarchy {
	
	private QvtOperationalEnv fEnv;
	private Map<String, List<Entry>> fData;
	
	public IntermediatePropertyHierarchy(Module module, QvtOperationalEnv env) {
		if(env == null) {
			throw new IllegalArgumentException();
		}
		fEnv = env;
		readHierarchy(module);
	}
	
	public boolean hasHierarchyClashes(ContextualProperty property) {
		if(fData == null || fData.isEmpty()) {
			return false;
		}
		
		List<Entry> entryList = fData.get(property.getName());
		if(entryList == null) {
			return false;
		}
		
		for (Entry entry : entryList) {
			Boolean status = entry.isClashingWith(property);
			if(Boolean.TRUE.equals(status)) {
				return true;
			}
		}
		return false;
	}
	
	private Map<String, List<Entry>> getData() {
		if(fData == null) {
			fData = new HashMap<String, List<Entry>>();
		}
		return fData;
	}
	
	private void readHierarchy(Module module) {
		for (ModuleImport moduleImport : module.getModuleImport()) {
			Module importedModule = moduleImport.getImportedModule();
			if(importedModule != null) {
				readHierarchy(importedModule);
			}
		}
		
		for (EStructuralFeature nextFeature : module.getEAllStructuralFeatures()) {
			if(nextFeature instanceof ContextualProperty) {
				ContextualProperty ctxProp = (ContextualProperty) nextFeature;
				if(ctxProp.getContext() == null) {
					continue;
				}
				String name = ctxProp.getName();
				List<Entry> entryList = getData().get(name);
				if(entryList == null) {
					entryList = new LinkedList<Entry>();
					getData().put(name, entryList);
				}
				
				boolean joined = false;
				for (Entry entry : entryList) {
					if(entry.addIfApplicable(ctxProp)) {
						joined = true;
						break;
					}
				}
				
				if(!joined) {
					Entry newEntry = new Entry();
					entryList.add(newEntry);					
					newEntry.addIfApplicable(ctxProp);
				}
			}
		}
	}
	
	private boolean isPartOfHierarchy(ContextualProperty c1, ContextualProperty c2) {
		if(c1.getContext() != null && c2.getContext() != null) {						
			int rel = TypeUtil.getRelationship(fEnv, c1.getContext(), c2.getContext());
			return (rel & UMLReflection.RELATED_TYPE) != 0;
		}
		return false;
	}

	
	private class Entry {
		private List<ContextualProperty> hierarchy;
		
		boolean addIfApplicable(ContextualProperty property) {
			if(hierarchy == null) {
				hierarchy = new LinkedList<ContextualProperty>();
				hierarchy.add(property);
				return true;
			} 

			for (ContextualProperty contextualProperty : hierarchy) {
				if(property != contextualProperty && isPartOfHierarchy(contextualProperty, property)) {
					hierarchy.add(property);
					return false;
				}
			}
			
			return false;
		}
		
		Boolean isClashingWith(ContextualProperty property) {
			if(hierarchy == null || hierarchy.isEmpty() || !hierarchy.contains(property)) {
				return null;
			}
			
			return hierarchy.size() > 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append('[');
			if(hierarchy != null) {
				for (ContextualProperty prop : hierarchy) {					
					if(prop.getContext() != null) {
						buf.append(prop.getContext().getName());
					}
					buf.append(',');
				}
			}
			buf.append(']');
			return buf.toString();
		}
	}
	
}
