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
package org.eclipse.m2m.internal.qvt.oml;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2m.internal.qvt.oml.common.CommonPlugin;
import org.eclipse.m2m.internal.qvt.oml.common.MDAConstants;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.QvtKeywords;

import com.ibm.icu.lang.UCharacter;

/**
 * Provides validation functionality for QVT elements identifiers.
 */
public class QvtNamesChecker {

	public static final String QNAME_DELIMITER = "."; //$NON-NLS-1$
	
	private static final String ADJACENT_DELIMITERS_ERROR = QNAME_DELIMITER + QNAME_DELIMITER;	

	private static final IStatus invalidCUFileNameStatus = CommonPlugin.createStatus(IStatus.ERROR, 
			NLS.bind(Messages.QvtNamesChecker_InvalidCUnitName, 
					new String[] { MDAConstants.QVTO_FILE_EXTENSION }), null);
	
	
	private QvtNamesChecker() {
		super();
	}
			
	public static boolean isValidCompilationUnitFileName(String fileName) {
		return validateCompilationUnitFileName(fileName).isOK();
	}
	
	public static IStatus validateCompilationUnitFileName(String fileName) {
		IStatus result = Status.OK_STATUS;

		boolean isBlank = fileName == null || fileName.trim().length() == 0;
		if(isBlank || !fileName.endsWith(MDAConstants.QVTO_FILE_EXTENSION_WITH_DOT) || fileName.startsWith(".")) { //$NON-NLS-1$
			return invalidCUFileNameStatus;
		}

		return result;
	}
	
	/**
	 * Get the name of the main module for the given compilation unit file name.
	 * <p>
	 * @param compilationUnitFileName a valid compilation unit file name
	 * 
	 * @return the module name, representing the give file name without the file name extension.
	 */
	public static String getCompilationUnitMainModuleName(String compilationUnitFileName) {
		int lastPos = compilationUnitFileName.lastIndexOf(MDAConstants.QVTO_FILE_EXTENSION_WITH_DOT);
		if(lastPos > 0) {
			return compilationUnitFileName.substring(0, lastPos);
		}		
		return ""; //$NON-NLS-1$
	}

	/**
	 * Extracts the qualifying part of the given qualified identifier.
	 * <p>
	 * 
	 * @param qualifiedIdentifier
	 *            a valid qualified identifier
	 * 
	 * @return the qualifying part is the string up-to the last '.' separated
	 *         segment of the given qualified identifier.
	 *         <p>
	 *         Note: If the qualified identifier is a simple identifier, which
	 *         also represents a valid qualified identifier, empty string representing
	 *         the default name-space is returned.
	 * 
	 * @throws IllegalArgumentException
	 *             in case that the passed <code>qualifiedIdentifier</code> is
	 *             not valid
	 * 
	 * @see #validateQualifiedIdentifier(String, String)
	 */	
	public static String extractQualifyingIdentifierPart(String qualifiedIdentifier) {
		if(!validateQualifiedIdentifier(qualifiedIdentifier, "").isOK()) {//$NON-NLS-1$
			throw new IllegalArgumentException("Invalid qualifiedIdentifier"); //$NON-NLS-1$
		}

		int pos = qualifiedIdentifier.lastIndexOf(QNAME_DELIMITER);
		if(pos >= 0) {
			return qualifiedIdentifier.substring(0, pos);
		}
		
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Extracts the local identifier from the given qualified identifier.
	 * <p>
	 * 
	 * @param qualifiedIdentifier
	 *            a valid qualified identifier
	 * 
	 * @return the local identifier as the last '.' separated segment of the
	 *         given qualified identifier.
	 *         <p>
	 *         Note: If the qualified identifier is a simple identifier, which
	 *         also represents a valid qualified identifier, the passed value is
	 *         returned.
	 * 
	 * @throws IllegalArgumentException
	 *             in case that the passed <code>qualifiedIdentifier</code> is
	 *             not valid
	 * 
	 * @see #validateQualifiedIdentifier(String, String)
	 */
	public static String extractLocalIdentifierPart(String qualifiedIdentifier) {
		if(!validateQualifiedIdentifier(qualifiedIdentifier, "").isOK()) {//$NON-NLS-1$
			throw new IllegalArgumentException("Invalid qualifiedIdentifier"); //$NON-NLS-1$
		}
		
		String[] segments = getQualifiedNameSegments(qualifiedIdentifier);
		assert segments.length > 0;
		
		return segments[segments.length - 1];
	}
	
	public static IStatus validateQualifiedIdentifier(String qualifiedIdentifier, String identifierKindName) {
		IStatus result = Status.OK_STATUS;
		if(qualifiedIdentifier == null || qualifiedIdentifier.trim().length() == 0) {
			String message = Messages.QvtNamesChecker_blankIdentifierError;
			return CommonPlugin.createStatus(IStatus.ERROR, NLS.bind(message, identifierKindName), null);
		}
		
		String[] segments = getQualifiedNameSegments(qualifiedIdentifier);
		if(segments.length == 0 || 
			qualifiedIdentifier.startsWith(QNAME_DELIMITER) || // wrong '.' position
			qualifiedIdentifier.endsWith(QNAME_DELIMITER) || // wrong '.' position
			qualifiedIdentifier.indexOf(ADJACENT_DELIMITERS_ERROR) >= 0 // empty segment name detection 
		) {
			String message = Messages.QvtNamesChecker_invalidQualifiedIdentifierError;
			return CommonPlugin.createStatus(IStatus.ERROR, NLS.bind(message, qualifiedIdentifier, identifierKindName), null);
		}

		for (String segmentName : segments) {
			// check for segment invalid characters
			IStatus segmentStatus = validateIdentifier(segmentName, Messages.QvtNamesChecker_namespaceIndentifierKindName);
			if (!segmentStatus.isOK()) {
				return segmentStatus;
			}
		}
		
		return result;
	}
	
	public static IStatus validateNamespaceSimpleIdenfier(String name) {
		return validateIdentifier(name, Messages.QvtNamesChecker_namespaceIndentifierKindName);
	}
	
	public static IStatus validateNamespaceQualifiedIdentifier(String name) {
		return validateQualifiedIdentifier(name, Messages.QvtNamesChecker_namespaceIndentifierKindName);
	}	

	public static IStatus validateQvtModuleIdentifier(String name) {
		return validateIdentifier(name, Messages.QvtNamesChecker_moduleIndentifierKindName);
	}
		
	public static IStatus validateIdentifier(String name, String identifierKindName) {
		if (name == null || name.trim().length() == 0) {
			return CommonPlugin.createStatus(IStatus.ERROR, NLS.bind(Messages.QvtNamesChecker_blankIdentifierError, identifierKindName), null);
		} 
	
		for (int i = 0; i < name.length(); ++i) {
			char c = name.charAt(i);
			// For now, check just as a Java identifier as the QVT specification is not clear about this
			if (!UCharacter.isJavaIdentifierPart(name.charAt(i))) {
				String[] args = new String[] { identifierKindName, name, String.valueOf(c) }; 
				String message = NLS.bind(Messages.QvtNamesChecker_illegalCharInIdentifierError, args);
				return CommonPlugin.createStatus(IStatus.ERROR, message, null); 
			}
		}
		
		// check for reserved words
		if(QvtKeywords.isKeyword(name)) {
			String message = NLS.bind(Messages.QvtNamesChecker_reservedQVTWord, name);
			return CommonPlugin.createStatus(IStatus.ERROR, message, null);
		}
	
		return Status.OK_STATUS;
	}
	
	/**
	 * Get the name segments of the given qualified-name.
	 * <p>
	 * Note: No kind of validation is performed by this method, simply tokens
	 * delimited by '<code>.</code>' are returned.
	 * 
	 * @param qualifiedName
	 *            a valid qualified identifier in the form of sequence of simple
	 *            identifiers separated by '<code>.</code>'
	 * 
	 * @return array of name segments composing the given qualified name
	 */
	public static String[] getQualifiedNameSegments(String qualifiedName) {
		StringTokenizer tokenizer = new StringTokenizer(qualifiedName, QNAME_DELIMITER);
		int segmentCount = tokenizer.countTokens();
		String[] segments = new String[segmentCount];
		
		for (int i = 0; i < segmentCount; i++) {
			segments[i] = tokenizer.nextToken();
		}
		
		return segments;
	}	
}
