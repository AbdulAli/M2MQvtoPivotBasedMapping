/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Alex Paperno - bug 419299 
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib.model;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory.ExceptionClassInstance;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QVTStackTraceElement;
import org.eclipse.m2m.internal.qvt.oml.stdlib.AbstractQVTStdlib;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeOCLFactory;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.m2m.qvt.oml.util.Dictionary;
import org.eclipse.m2m.qvt.oml.util.MutableList;

public class StdlibFactory {
	
	private StdlibPackage fStdlibPackage;
	
	public StdlibFactory(StdlibPackage stdlibPackage) {
		if(stdlibPackage == null) {
			throw new IllegalArgumentException("null stdlib package"); //$NON-NLS-1$
		}
		
		fStdlibPackage = stdlibPackage;
	}
	
	public DictionaryType createDictionary(EClassifier keyType, EClassifier elementType) {
		DictionaryType dictType = ImperativeOCLFactory.eINSTANCE.createDictionaryType();
		dictType.setKeyType(keyType);
		dictType.setElementType(elementType);
		dictType.setName(getTypeName(dictType));
		// Note: instanceClass is transient, will not be loaded from deserialization 
		dictType.setInstanceClass(Dictionary.class);		
		return dictType;
	}
	
	public ListType createList(EClassifier elementType) {
		ListType listType = ImperativeOCLFactory.eINSTANCE.createListType();
		listType.setElementType(elementType);
		listType.setName(getTypeName(listType));
		// Note: instanceClass is transient, will not be loaded from deserialization		
		listType.setInstanceClass(MutableList.class);		
		return listType;
	}
	
	public StatusInstance createSuccess() {
		Status status = new Status(true, null);
		status.eSetClass(fStdlibPackage.getStatusClass());
		return status;
	}
	
	public StatusInstance createFailure(ExceptionInstance exception) {
		Status status = new Status(false, exception);
		status.eSetClass(fStdlibPackage.getStatusClass());
		return status;
	}
	
	public ExceptionInstance createException(EClass exceptionClass, String argument, List<QVTStackTraceElement> stackElements) {
		ExceptionClassInstance exception = new ExceptionClassInstance(exceptionClass);
		exception.setArgument(argument);
		exception.setStackElements(Collections.unmodifiableList(stackElements));
		return exception;
	}	
	
	private static class Status extends EObjectImpl implements StatusInstance {
		private boolean fIsSuccess;
		private ExceptionInstance fException;
		
		private Status(boolean isSuccess, ExceptionInstance exception) {
			fIsSuccess = isSuccess;
			fException = exception;
		}
		
		public boolean succeeded() {		
			return fIsSuccess;
		}

		public boolean failed() {		
			return !succeeded();
		}
		
		public ExceptionInstance raisedException() {
			return fException;
		}
		
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(); 
			buf.append(fIsSuccess ? "SUCCESS" : "FAILED"); //$NON-NLS-1$ //$NON-NLS-2$
			if(fException != null) {
				buf.append(" raised: (").append(fException).append(')'); //$NON-NLS-1$
			}
			return buf.toString();
		}
	}

	private static String getTypeName(ListType type) {
		StringBuffer nameBuf = new StringBuffer();
		nameBuf.append(MutableList.SINGLETON_NAME);
		nameBuf.append('(');

		EClassifier elementType = type.getElementType();
		String elementTypeName;
		if (elementType != null) {
			elementTypeName = elementType.getName();
		} else {
			elementTypeName = ""; //$NON-NLS-1$
		}

		nameBuf.append(elementTypeName);
		nameBuf.append(')');

		return nameBuf.toString();		
	}
	
	private static String getTypeName(DictionaryType dictionaryType) {
		StringBuilder nameBuf = new StringBuilder();
		nameBuf.append(Dictionary.SINGLETON_NAME);
		nameBuf.append('(');
		EClassifier keyType = dictionaryType.getKeyType();
		EClassifier elementType = dictionaryType.getElementType();		
		
		if(keyType != null) {
			nameBuf.append(keyType.getName());		
		} else {
			nameBuf.append(AbstractQVTStdlib.NULL_NAME);
		}
		
		nameBuf.append(',').append(' ');
		
		if(elementType != null) {
			nameBuf.append(elementType.getName());		
		} else {
			nameBuf.append(AbstractQVTStdlib.NULL_NAME);			
		}

		nameBuf.append(')');		
		return nameBuf.toString();
	}
}
