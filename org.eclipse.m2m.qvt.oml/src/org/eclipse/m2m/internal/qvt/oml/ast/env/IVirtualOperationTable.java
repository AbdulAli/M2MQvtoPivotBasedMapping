/*******************************************************************************
 * Copyright (c) 2007, 2013 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;

public interface IVirtualOperationTable {

	/**
	 * Provides access to operation virtual table
	 */
	interface Access {

		Access INSTANCE = new Access() {		
			public IVirtualOperationTable getVirtualTable(EOperation operation) {
				VirtualTableAdapter adapter = VirtualTableAdapter.getAdapter(operation);
				if(adapter != null) {
					return adapter.getVirtualTable();
				}
				return null;
			}
		};

		/**
		 * Gets virtual table for the given operation.
		 * 
		 * @param operation
		 *            the operation for which the table is to be retrieved
		 * @return the table or <code>null</code> if no table is available
		 */
		IVirtualOperationTable getVirtualTable(EOperation operation);
	}
	
	/**
	 * Performs actual operation lookup according to virtual semantics for the
	 * given actual type.
	 * <p>
	 * The lookup is performed based from formalOperation operation resolved in operation call 
	 * at compilation time, resulting in runtime dispatched operation for the actual type.
	 * 
	 * @param actualContextType
	 *            actual context type whose instance is to be used for operation
	 *            call dispatch. It is resolved at runtime and might be
	 *            different from the owning type resolved at compilation time
	 * @param env
	 *            parsing environment used for type, operation resolution
	 * @param evalEnv TODO
	 * @return the resulting operation or <code>null</code> if no one was
	 *         found.
	 * @see #addOperationInModule(EOperation, Module)
	 */
	public abstract EOperation lookupActualOperation(
			EClassifier actualContextType,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			InternalEvaluationEnv evalEnv);

	/**
	 * Performs actual operation lookup according to virtual semantics for the
	 * given actual type, and limits operations in a scope.
	 * <p>
	 * <p>
	 * The lookup is performed based from formalOperation operation resolved in operation call 
	 * at compilation time, resulting in runtime dispatched operation for the actual type.
	 * 
	 * @param actualContextType
	 *            actual context type whose instance is to be used for operation
	 *            call dispatch. It is resolved at runtime and might be
	 *            different from the owning type resolved at compilation time
	 * @param env
	 *            parsing environment used for type, operation resolution
	 * @param scope
	 *            the module in which the resulting operation was manifested as
	 *            visible by previous call to
	 *            {@link #addOperationInModule(EOperation, Module)}
	 * @param evalEnv TODO
	 * @return the resulting operation or <code>null</code> if no one was
	 *         found.
	 * @see #addOperationInModule(EOperation, Module)
	 */
	public abstract EOperation lookupActualOperation(
			EClassifier actualContextType,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			Module scope, InternalEvaluationEnv evalEnv);

}
