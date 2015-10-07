/*******************************************************************************
 * Copyright (c) 2008, 2014 Borland Software Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 289982, 427237
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.blackbox.java;

import org.eclipse.m2m.internal.qvt.oml.NLS;

public class JavaBlackboxMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.m2m.internal.qvt.oml.blackbox.java.JavaBlackboxMessages"; //$NON-NLS-1$
	
	public static String BlackboxUnitLoadFailed;

	public static String FirstContextualOperationParameterRequired;
	public static String QvtoContextParameterRequired;
	public static String ConstructorRequiresContextualOperation;
	public static String TransformationRequiresContextlessOperation;

	public static String InvalidJavaClassForModule;

	public static String LoadModuleDiagnostics;

	public static String LoadOperationDiagnostics;
	public static String MethodInvocationError;

	public static String ModuleJavaClassNotFound;
	public static String ModuleJavaClassNotLoadable;

	public static String UnitAlreadyRegisteredContributionIgnored;

	public static String UnresolvedMetamodelURI;

	public static String UnresolvedOclTypeForJavaType;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JavaBlackboxMessages.class);
	}

	private JavaBlackboxMessages() {
	}
}
