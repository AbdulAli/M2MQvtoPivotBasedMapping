/*******************************************************************************
 * Copyright (c) 2008, 2010 Borland Software Corporation and others.
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

import lpg.runtime.IntSegmentedTuple;

import org.eclipse.m2m.internal.qvt.oml.common.util.LineNumberProvider;
import org.eclipse.ocl.lpg.AbstractLexer;

/**
 * Implements quick search of a line number for a given position within a string
 */
class BasicLineNumberProvider implements LineNumberProvider {
    
	private int[] fLineEnds;
	
	BasicLineNumberProvider(int[] lineEnds) {
		fLineEnds = lineEnds.clone();
	}
	
    BasicLineNumberProvider(AbstractLexer lexer) {
    	
		IntSegmentedTuple lineOffsets = lexer.getILexStream().getLineOffsets();
		int[] lines = new int[lineOffsets.size()];
		
		boolean containsNegative = false;
		boolean isSorted = true;
		
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lineOffsets.get(i);
			
    		if(lines[i] < 0) {
    			containsNegative = true;
    		}
    		
    		if(i == lines.length - 1) {
    			// last index, we must have been sorted
    			break;
    		}
    		
    		int next = lineOffsets.get(i + 1);
    		if(lines[i] < next == false) {
    			isSorted = false;
    			break;
    		}
		}
		
    	if(!isSorted) {
    		Arrays.sort(lines);
    	}
    	
    	if(containsNegative) {
    		int negativeCount = 0;
    		for (int i = 0; i < lines.length; i++) {
				if(lines[i] < 0) {
					negativeCount++;
				} else {
					// we are sorted, can leave
					break;
				}
			}
    		
    		int[] newLines = new int[lines.length - negativeCount];
    		System.arraycopy(lines, negativeCount, newLines, 0, lines.length - negativeCount);
    		lines = newLines;
    	}
		
        fLineEnds = lines;
    }
    
    int[] lineBreakOffsets() {
    	return fLineEnds.clone();
    }
    
    public int getLineEnd(int lineNumber) {
        return getLineEndInt(lineNumber - 1); 
    }
    
    public int getLineCount() {
        return fLineEnds.length;
    }
    
    public int getLineNumber(int offset) {
        if (offset < 0) {
            return -1;
        }
        return getLineNumberInt(offset) + 1;
    }
    
    /* Internal implementation: zero-based line numeration */
    private int getLineEndInt(int lineNumber) {
    	if(lineNumber >= 0 && lineNumber < fLineEnds.length) { 
    		return fLineEnds[lineNumber];
    	}
    	
    	return -1;
    }

    public int getLineNumberInt(int offset) {
        int index = searchLineForOffset(offset);
        return index < 0 ? -index : index == 0 ? 1 : index;    	
    }

    private int searchLineForOffset(int offset) {
        int low = 0;
        int high = fLineEnds.length;
        
        while (high > low)
        {
            int mid = (high + low) / 2,
                mid_element = fLineEnds[mid];
            if (offset == mid_element)
                 return mid;
            else if (offset < mid_element)
                 high = mid;
            else low = mid + 1;
        }

        return -low;
    }
}
