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
package org.eclipse.m2m.internal.qvt.oml.expressions.util;

import org.eclipse.m2m.internal.qvt.oml.expressions.Constructor;
import org.eclipse.m2m.internal.qvt.oml.expressions.ConstructorBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.util.ImperativeOCLVisitor;


public interface QVTOperationalVisitor<T> extends ImperativeOCLVisitor<T> {

	T visitModule(Module module);

	T visitLibrary(Library library);

	T visitModuleImport(ModuleImport moduleImport);

	T visitContextualProperty(ContextualProperty contextualProperty);

	T visitImperativeOperation(ImperativeOperation imperativeOperation);

	T visitVarParameter(VarParameter varParameter);

	T visitOperationBody(OperationBody operationBody);

	T visitMappingOperation(MappingOperation mappingOperation);

	T visitHelper(Helper helper);

	T visitMappingBody(MappingBody mappingBody);

	T visitMappingCallExp(MappingCallExp mappingCallExp);

    T visitResolveExp(ResolveExp resolveExp);

    T visitResolveInExp(ResolveInExp resolveInExp);

    T visitObjectExp(ObjectExp objectExp);

	T visitModelType(ModelType modelType);

	T visitEntryOperation(EntryOperation entryOperation);

	T visitConstructor(Constructor constructor);

	T visitConstructorBody(ConstructorBody constructorBody);
	
} // ExtendedVisitor
