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
 *     Christopher Gerking - bug 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.evaluator.EvaluationUtil;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.ocl.util.TypeUtil;

public class OperationMatcher {
	
	private OperationMatcher() {
	}
	
	public static boolean matchOperation(QvtOperationalEnv env, ImperativeOperation imperativeOp, EOperation libraryOp) {
		
		if (!libraryOp.getName().equals(imperativeOp.getName())) {
			return false;
		}
		
		EClassifier contextType = QvtOperationalParserUtil.getContextualType(imperativeOp);
		EClassifier owner = env.getUMLReflection().getOwningClassifier(libraryOp);
		if (contextType == null) {
			if (false == owner instanceof Library) {
				return false;
			}
		}
		else {
			if (!TypeUtil.exactTypeMatch(env, contextType, owner)) {
				return false;
			}
		}
		
		if (imperativeOp.getEParameters().size() != libraryOp.getEParameters().size()) {
			return false;
		}
		for (int i = 0, in = imperativeOp.getEParameters().size(); i < in; ++i) {
			if (!TypeUtil.exactTypeMatch(env, imperativeOp.getEParameters().get(i).getEType(), libraryOp.getEParameters().get(i).getEType())) {
				return false;
			}
		}
		
		if (imperativeOp.getResult().size() == 0) {
			if (libraryOp.getEType() != env.getOCLStandardLibrary().getOclVoid()) {
				return false;
			}
		}
		else {
			if (!TypeUtil.exactTypeMatch(env, libraryOp.getEType(), imperativeOp.getResult().get(0).getEType())) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean matchOperation(QvtOperationalEnv env, OperationalTransformation trans, EOperation libraryOp) {
		
		if (!libraryOp.getName().equals(trans.getName())) {
			return false;
		}
		
		List<ModelParameter> expectedParams = EvaluationUtil.getBlackboxSignature(trans);
		
		if (expectedParams.size() != libraryOp.getEParameters().size()) {
			return false;
		}
		for (int i = 0, in = expectedParams.size(); i < in; ++i) {
			if (!TypeUtil.compatibleTypeMatch(env, expectedParams.get(i).getEType(), libraryOp.getEParameters().get(i).getEType())) {
				return false;
			}
		}
				
		if (libraryOp.getEType() != env.getOCLStandardLibrary().getOclVoid()) {
			return false;
		}
		
		return true;
	}

}
