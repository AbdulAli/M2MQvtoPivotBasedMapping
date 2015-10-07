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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNode;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNodeAccess;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.IModuleSourceInfo;
import org.eclipse.m2m.internal.qvt.oml.common.util.LineNumberProvider;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.utilities.ASTNode;

class DefaultExtensionDecoder implements ExeXMIExtensionEncoder  {
	
	private static final String OFFSET_DELIMITER = ","; //$NON-NLS-1$

	private static final NodeHelper AST_HELPER = new ASTNodeHelper();
	
	private static final NodeHelper AST_SYNTHETIC_HELPER = new SyntheticNodeHelper();


	DefaultExtensionDecoder() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMIExtensionEncoder#decodeOffsets(org.eclipse.emf.ecore.resource.Resource, java.lang.String)
	 */
	public void decodeASTNodeOffsets(Resource res, String data) throws CorruptedExtensionData {
 		StringTokenizer tokenizer = new StringTokenizer(data, OFFSET_DELIMITER);
		
		TreeIterator<EObject> nextRootContentsIt = res.getAllContents();		
		int prevStartPos = 0;
		
		while (nextRootContentsIt.hasNext()) {
			EObject next = nextRootContentsIt.next();
			NodeHelper nodeHelper = getNodeHelper(next, false);		
			if(nodeHelper == null) {
				continue;
			}
			
			int startPos = -1;
			int endPos = -1;			
			for(int i = 0; i < 2; i++) {
				int sign = 1;
				if (tokenizer.hasMoreTokens()) {
					String nextToken = tokenizer.nextToken();
										
					char firstChar = nextToken.charAt(0);					
					if(firstChar == '-') {
						// be fault tolerant to negative offsets (undefined by QVTO parsing)
						nextToken = nextToken.substring(1);
						sign = -1;
					} else if(firstChar == '.') {
						continue;
					}
					
					int nextInt;
					try {
						nextInt = (sign * Integer.parseInt(nextToken, 16)) + prevStartPos;
					} catch(NumberFormatException e) {						
						throw new CorruptedExtensionData(NLS.bind("Invalid offset format ({0})", e.toString())); //$NON-NLS-1$
					}
						
					if(i == 0) {
						startPos = nextInt;
						prevStartPos = nextInt;
					} else {
						endPos = nextInt;
					}
				} else {
					throw new CorruptedExtensionData("Missing offset data for existing AST Node"); //$NON-NLS-1$
				}
			}
			
			if(startPos >= 0 && endPos >= 0) {
				nodeHelper.setPositions(next, startPos, endPos);
			}			
		}
		
		if(tokenizer.hasMoreTokens()) {
			throw new CorruptedExtensionData("Too many offsets data existing AST Nodes"); //$NON-NLS-1$
		}		
	}
		
 	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMIExtensionEncoder#encodeOffsets(org.eclipse.emf.ecore.resource.Resource)
	 */
	public String encodeASTNodeOffsets(Resource res) {
		StringBuilder buf = new StringBuilder(1024);
		int prevStartPos = 0;
		int count = 0;
		int[] startEndPositions = new int[2];
		
		for(TreeIterator<EObject> nextRootContentsIt = res.getAllContents(); nextRootContentsIt.hasNext();) {
			EObject next = nextRootContentsIt.next();			
			NodeHelper nodeHelper = getNodeHelper(next, true);			
			if(nodeHelper == null) {
				continue;
			}
			
			if(count > 0) {
				buf.append(OFFSET_DELIMITER);
			}
			
			nodeHelper.getPositions(next, startEndPositions);
			int startPos = startEndPositions[0];
			int endPos = startEndPositions[1];				
			
			if(startPos >= 0 && endPos >=0) {
				int len = endPos - startPos; 
				int prevOffsetDelta = startPos - prevStartPos;

				encodeInt(prevOffsetDelta, buf);
				buf.append(OFFSET_DELIMITER);
				encodeInt(len, buf);
				
				prevStartPos = startPos;
			} else {
				// we do not case about meaningless settings but tolerate them ...
				buf.append('.').append(OFFSET_DELIMITER).append('.');
			}
			
			count++;
		}
		
		return buf.toString();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMIExtensionEncoder#decodeLineBreakOffsets(java.lang.String)
	 */
	public int[] decodeLineBreakOffsets(String encodedOffsetData) throws CorruptedExtensionData {
		StringTokenizer tokenizer = new StringTokenizer(encodedOffsetData, OFFSET_DELIMITER);
		int[] result = null;
		int i = 0;
		int prevOffset = 0;
		while(tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			if(result == null) {
				// read first integer as the count value				
				int count;
				try {
					count = Integer.parseInt(nextToken, 16);
				} catch(NumberFormatException e) {
					throw new CorruptedExtensionData(NLS.bind("Invalid offset format ({0})", e.toString())); //$NON-NLS-1$
				}
					
				result = new int[count];
				continue;
			}
			
			int nextOffset = Integer.parseInt(nextToken, 16);
			result[i++] = prevOffset + nextOffset;
			prevOffset = nextOffset;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMIExtensionEncoder#encodeLineBreakOffsets(org.eclipse.emf.ecore.resource.Resource)
	 */
	public String encodeLineBreakOffsets(Resource res) {
		int[] lineOffsets = null;
		for (EObject nextRoot : res.getContents()) {
			if(nextRoot instanceof Module) {
				IModuleSourceInfo srcInfo = ASTBindingHelper.getModuleSourceBinding((Module) nextRoot);
				if(srcInfo != null) {
					LineNumberProvider lineNumProvider = srcInfo.getLineNumberProvider();
					if(lineNumProvider instanceof BasicLineNumberProvider) {
						BasicLineNumberProvider basicProvider = (BasicLineNumberProvider) lineNumProvider;
						lineOffsets = basicProvider.lineBreakOffsets();
					}
				}
			}
		}
		
		if(lineOffsets == null) {
			return ""; //$NON-NLS-1$
		}
		
		StringBuilder buf = new StringBuilder(1024);
		// write offset count first
		buf.append(Integer.toHexString(lineOffsets.length)).append(OFFSET_DELIMITER);
		int count = 0;
		int prevOffset = 0;
		for (int offset : lineOffsets) {
			if(count > 0) {
				buf.append(OFFSET_DELIMITER);
			}
			
			offset = offset - prevOffset;
			buf.append(Integer.toHexString(offset));
			prevOffset = offset;
			count++;
		}
		
		return buf.toString();
	}
		
	private static NodeHelper getNodeHelper(EObject astElement, boolean encodingPhase) {
		if(astElement instanceof ASTNode) {
			return AST_HELPER;
		} else if(synteticASTTypes.contains(astElement.eClass())) {
			if(ASTSyntheticNodeAccess.getASTNode(astElement) == null) {
				// FIXME - issue a warn TRACE if supported synthetic node type is not set during encoding
				ASTSyntheticNodeAccess.createASTNode(astElement);
			}
			
			return AST_SYNTHETIC_HELPER;			
		}
				
		return null;
	}
	
	private static final Set<EClass> synteticASTTypes = new HashSet<EClass>(Arrays.asList(
			EcorePackage.eINSTANCE.getEAttribute(),
			EcorePackage.eINSTANCE.getEReference(),
			EcorePackage.eINSTANCE.getEClass(),
			ExpressionsPackage.eINSTANCE.getLibrary(),
			ExpressionsPackage.eINSTANCE.getOperationalTransformation(),
			ExpressionsPackage.eINSTANCE.getContextualProperty()
	));
		
	private void encodeInt(int value, StringBuilder buf) {
		if(value < 0) {
			// specially encode negatives to avoid NFE from hex int parsing
			buf.append('-');
			value = -value;
		}
		buf.append(Integer.toHexString(value));
	}
	
	private interface NodeHelper {
		Character getPrefixChar();
		void setPositions(EObject node, int startPos, int endPos);
		void getPositions(EObject node, int[] startEndPositions);
	}
	
	
	private static class SyntheticNodeHelper implements NodeHelper {
		static final Character PREFIX = new Character('+');
		
		public Character getPrefixChar() {
			return PREFIX;
		};
		
		public void getPositions(EObject node, int[] startEndPositions) {
			ASTSyntheticNode syntheticNode = ASTSyntheticNodeAccess.getASTNode(node);
			startEndPositions[0] = syntheticNode.getStartPosition();
			startEndPositions[1] = syntheticNode.getEndPosition();
		}
	
		public void setPositions(EObject node, int startPos, int endPos) {
			ASTSyntheticNode syntheticNode = ASTSyntheticNodeAccess.getASTNode(node);
			syntheticNode.setStartPosition(startPos);
			syntheticNode.setEndPosition(endPos);
		}
	}

	
	private static class ASTNodeHelper implements NodeHelper {
		
		public Character getPrefixChar() {
			return null;
		}		
		
		public void getPositions(EObject node, int[] startEndPositions) {
			ASTNode astNode = (ASTNode) node;
			startEndPositions[0] = astNode.getStartPosition();
			startEndPositions[1] = astNode.getEndPosition();
		}
	
		public void setPositions(EObject node, int startPos, int endPos) {
			ASTNode astNode = (ASTNode) node;
			astNode.setStartPosition(startPos);
			astNode.setEndPosition(endPos);
		}
	}	
}
