/*******************************************************************************
 * Copyright (c) 2007, 2010 Borland Software Corporation and others.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.ast.env.TupleFactory;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.ocl.ecore.TupleType;

/**
 * @since 2.0
 */
public class ModuleInstanceFactory extends EFactoryImpl {

	public interface PostCreateHandler {
		void created(ModuleInstance moduleInstance);
	}

	private TupleFactory fTupleFactory;
	private List<PostCreateHandler> fPostCreateHandlers = Collections.emptyList();

	public ModuleInstanceFactory() {
		fTupleFactory = new TupleFactory();
	}
	
	public void addPostCreateHandler(PostCreateHandler postCreateHandler) {
		if (postCreateHandler == null) {
			throw new IllegalArgumentException();
		}

		if (fPostCreateHandlers.isEmpty()) {
			fPostCreateHandlers = new LinkedList<PostCreateHandler>();
		}

		fPostCreateHandlers.add(postCreateHandler);
	}

	@Override
	public EObject create(EClass eClass) {
		if (getEPackage() != eClass.getEPackage() || eClass.isAbstract()) {
			if(eClass instanceof TupleType) {
				return fTupleFactory.create(eClass); 
			} 
			else if (eClass instanceof Module == false) {
				// relax the constraint for Module being also a package
				throw new IllegalArgumentException(
						"The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return basicCreate(eClass);
	}

	@Override
	protected EObject basicCreate(EClass eClass) {
		if (eClass instanceof Module) {
			Module module = (Module) eClass;
			Map<Module, OperationOverrideMap> overrideMap = OperationOverrideMap.create(module);

			HashMap<Module, ModuleInstance> instanceMap = new HashMap<Module, ModuleInstance>(3);
			basicCreateModuleInstance(QvtOperationalStdLibrary.INSTANCE.getStdLibModule(), instanceMap, overrideMap);
			
			ModuleInstance result = createModuleInstance(module, instanceMap, overrideMap);			

			return result;
		} else if(eClass instanceof ModelType) {
			return new ModelInstanceImpl((ModelType) eClass, new ModelParameterExtent());
		} else if(eClass instanceof TupleType) {
			return TupleFactory.createTuple(eClass);
		}

		return super.basicCreate(eClass);
	}

	protected final ModuleInstance createModuleInstance(Module module, Map<Module, ModuleInstance> instanceMap, Map<Module, OperationOverrideMap> overrideMap) {
		ModuleInstance moduleInstance = instanceMap.get(module);
		if (moduleInstance == null) {
			moduleInstance = basicCreateModuleInstance(module, instanceMap, overrideMap);
			for (ModuleImport moduleImport : module.getModuleImport()) {
				Module importedModule = moduleImport.getImportedModule();
				if(moduleImport.getKind() == ImportKind.EXTENSION || importedModule instanceof Library) {
					// create only instances of extended modules and implicit accessed library singletons 
					createModuleInstance(importedModule, instanceMap, overrideMap);					
				}
			}
		}

		return moduleInstance;
	}
	

	protected final ModuleInstanceImpl basicCreateModuleInstance(Module module, Map<Module, ModuleInstance> instanceMap, Map<Module, OperationOverrideMap> overrideMap) {
		ModuleInstanceImpl moduleInstance;
		if(module instanceof OperationalTransformation) {
			moduleInstance = new TransformationInstanceImpl((OperationalTransformation) module);			
		} else {
			moduleInstance = new ModuleInstanceImpl(module);
		}
		moduleInstance.setInstanceMap(instanceMap);
		instanceMap.put(module, moduleInstance);
		
		if(overrideMap != null) {
			moduleInstance.setOverrideMap(overrideMap);
		}
		
		ModuleInstanceFactory factory = this; 
		if(module.getEFactoryInstance() instanceof ModuleInstanceFactory) {
			factory = (ModuleInstanceFactory) module.getEFactoryInstance();
			factory.notifyModuleCreated(moduleInstance);
		}
		
		if(factory != this) {
			this.notifyModuleCreated(moduleInstance);
		}
		
		return moduleInstance;
	}

	private void notifyModuleCreated(ModuleInstanceImpl moduleInstance) {
		if(fPostCreateHandlers != null) {
			for (PostCreateHandler handler : fPostCreateHandlers) {
				handler.created(moduleInstance);
			}
		}
	}
	
}
