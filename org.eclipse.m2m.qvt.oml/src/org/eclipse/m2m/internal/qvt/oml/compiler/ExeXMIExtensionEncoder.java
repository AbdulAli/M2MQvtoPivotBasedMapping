/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import org.eclipse.emf.ecore.resource.Resource;

interface ExeXMIExtensionEncoder {

	class CorruptedExtensionData extends Exception {

		private static final long serialVersionUID = -4761439926243962452L;

		public CorruptedExtensionData(String message) {
			super(message);		
		}		
	}
	
	
	int[] decodeLineBreakOffsets(String encodedOffsetData) throws CorruptedExtensionData;

	String encodeLineBreakOffsets(Resource res);

	void decodeASTNodeOffsets(Resource res, String data) throws CorruptedExtensionData;

	String encodeASTNodeOffsets(Resource res);

}