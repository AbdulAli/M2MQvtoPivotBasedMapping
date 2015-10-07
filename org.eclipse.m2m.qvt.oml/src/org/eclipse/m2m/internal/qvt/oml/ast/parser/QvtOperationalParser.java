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
 *     Alex Paperno - bugs 416584
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.io.Reader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTParsingOptions;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompilerMessages;
import org.eclipse.m2m.internal.qvt.oml.cst.CSTFactory;
import org.eclipse.m2m.internal.qvt.oml.cst.UnitCS;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.AbstractQVTParser;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.QVTOLexer;
import org.eclipse.m2m.internal.qvt.oml.cst.parser.QVTOParser;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;

import com.ibm.icu.lang.UCharacter;


public class QvtOperationalParser {
	
	public QvtOperationalParser() {
	}
	
	public static QVTOLexer createLexer(final Reader is, final String name, QvtOperationalEnv env) throws ParserException {
		QVTOLexer lexer = new QVTOLexer(env, correctLineBreaks(new OCLInput(is)), name, 4);
		return lexer;
	}
	
	public UnitCS parse(final Reader is, final String name, QvtOperationalEnv env) {
		UnitCS result = null;
		int sourceLength = 0;
		// Note:
		// Adding compilation unit here to support multiple top-level elements
		// Before they get supported already by the raw parser, 
		// the compiler and the rest of the tooling can adopt it
		try {			
			QVTOLexer lexer = createLexer(is, name, env);

			myParser = new RunnableQVTParser(lexer);		
			myParser.enableCSTTokens(Boolean.TRUE.equals(env.getValue(QVTParsingOptions.ENABLE_CSTMODEL_TOKENS)));
			
			lexer.lexer(myParser.getIPrsStream());
			result = (UnitCS) myParser.runParser(-1);
			sourceLength = lexer.getILexStream().getStreamLength();
		}
		catch (ParserException ex) {
			env.reportError(ex.getLocalizedMessage(), 0, 0);
		}

		if (result == null) {
			if (!env.hasErrors()) {
				env.reportError(NLS.bind(
						CompilerMessages.moduleTransformationExpected, new Object[] { name }),0, 0);
			}
			result = CSTFactory.eINSTANCE.createUnitCS();
		}
		result.setStartOffset(0);
		result.setEndOffset(sourceLength);			
		
		return result;
	}

	/*
	public Module analyze(AbstractQVTParser parser, final MappingModuleCS moduleCS, ExternalUnitElementsProvider importResolver, ResourceSet resSet, QvtOperationalFileEnv env, QvtCompilerOptions options) {
		Module module = null;
	
		env.setQvtCompilerOptions(options);
		try {
			QvtOperationalVisitorCS visitor = new QvtOperationalVisitorCS(parser, options);
			module = visitor.visitMappingModule(moduleCS, importResolver.getImporter(), env, importResolver, resSet);
		} catch (SemanticException e) {
			env.reportError(e.getLocalizedMessage(), 0, 0);
		}
		
		if (module != null && options.isReportErrors()) {
			env.setCheckForDuplicateErrors(true);
			QvtOperationalValidationVisitor validation = new QvtOperationalValidationVisitor(env);
			validation.visitModule(module);
			env.setCheckForDuplicateErrors(false);
		}
		
		return module;
	}
	*/
		
	
	private class RunnableQVTParser extends QVTOParser {
		public RunnableQVTParser(QVTOLexer lexStream) {
			super(lexStream);
		}
		
		public EObject runParser(int max_error_count) throws ParserException {
			return parser(max_error_count);
		}
		
//		@Override
//		protected void OnParseError(BadParseException e) {
//			super.OnParseError(e);
//		}
//		
//		@Override
//		public void reportError(int errorCode, int leftToken, int rightToken, String tokenText) {
//			super.reportError(errorCode, leftToken, rightToken, tokenText);
//		}
		
		// FIXME - OCL 1.2 migration, workaround for ArrayIndexOutBounds
		@Override
		public String computeInputString(int left, int right) {
			char[] chars = getIPrsStream().getInputChars();
			
			if(right < left) {
				right = left;
			}
			
			if(right >= chars.length) {
				right = chars.length - 1;
			}

			StringBuffer result = new StringBuffer(right - left + 1);
			
			if (chars.length > 0) {
				for (int i = left; i <= right; i++) {
					if (chars[i] == '\t') {
						result.append(' ');
					} else if (chars[i] == '\n' || chars[i] == '\r' || chars[i] == '\f') {
						if (i > 0) {
							if (!UCharacter.isWhitespace(chars[i-1])) {
								result.append(' ');
							}
						}
					} else {
						result.append(chars[i]);
					}

				}
			}
			return result.toString();
		}		
	}
	    
    public AbstractQVTParser getParser() {
		return myParser;
	}
    
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=259486
    private static char[] correctLineBreaks(OCLInput input) throws ParserException {
    	char[] contents = input.getContentAsString().toCharArray();
		for (int i = 0; i < contents.length; i++) {
			char c = contents[i];
			// check for single '\n' characters when MAC line breaks are used
			if(c == '\r') {
				// possibly the MAC new line char CR, check if not followed by LF, 
				// as CR/LF is the newline on windows
				int nextPos = i + 1;
				if(nextPos == contents.length || contents[nextPos] != '\n') {				
					// we found a single CR, consider it a line separator
					contents[i] = '\n';
				}
			}
		}
		return contents;
	}
    	
    private RunnableQVTParser myParser;
}