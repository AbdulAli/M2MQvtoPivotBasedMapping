/*******************************************************************************
 * Copyright (c) 2009, 2010 Borland Software Corporation and others.
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

import java.util.Iterator;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.lpg.AbstractFormattingHelper;
import org.eclipse.ocl.utilities.PredefinedType;
import org.eclipse.ocl.utilities.UMLReflection;

public class FormattingHelperImpl extends AbstractFormattingHelper {

	public static final FormattingHelperImpl INSTANCE = new FormattingHelperImpl();
	
	private boolean fIncludeParameterName;
	
	public String formatOperationSignature(EOperation operation, UMLReflection<?, EClassifier, EOperation, ?, ?, EParameter, ?, ?, ?, ?> uml) {
		StringBuilder result = new StringBuilder();
		
		EClassifier owner = uml.getOwningClassifier(operation);
		if (owner != null && owner instanceof Module == false) {
			result.append(formatQualifiedName(owner));
			result.append(getSeparator());
		}

		result.append(formatName(operation));
		result.append('(');

		for (Iterator<EParameter> iter = uml.getParameters(operation).iterator(); iter.hasNext();) {
			EParameter next = iter.next();
			if (uml.getOCLType(next) != null) {
				result.append(getTypedElementSignature(next, uml));
			}

			if (iter.hasNext()) {
				result.append(", "); //$NON-NLS-1$
			}
		}

		EClassifier returnType = uml.getOCLType(operation);
		if (returnType == null) {
			result.append(')');
		} else {
			result.append(") : "); //$NON-NLS-1$
			if (isStdLibType(returnType)) {
				result.append(uml.getName(returnType));
			} else {
				result.append(uml.getQualifiedName(returnType));
			}
		}

		EPackage moduleOwner = QvtOperationalParserUtil.getOwningModule(operation);
		if (moduleOwner != null) {
			result.append(" - "); //$NON-NLS-1$
			result.append(uml.getQualifiedName(moduleOwner));
		}

		return result.toString();
	}

	String getTypedElementSignature(ETypedElement typedElement, UMLReflection<?, EClassifier, EOperation, ?, ?, EParameter, ?, ?, ?, ?> uml) {
		StringBuilder result = new StringBuilder();
		if(fIncludeParameterName) {
			result.append(uml.getName(typedElement));
			result.append(" : "); //$NON-NLS-1$
		}

		EClassifier eType = uml.getOCLType(typedElement);
		if (eType != null) {
			if (isStdLibType(eType)) {
				result.append(uml.getName(eType));
			} else {
				result.append(uml.getQualifiedName(eType));
			}
		}

		return result.toString();
	}

	static boolean isStdLibType(EClassifier type) {
		return type instanceof PredefinedType<?>;
	}
}
