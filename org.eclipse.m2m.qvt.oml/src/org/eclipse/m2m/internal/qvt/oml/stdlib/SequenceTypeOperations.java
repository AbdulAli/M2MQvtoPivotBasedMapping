/*******************************************************************************
 * Copyright (c) 2014, 2015 S.Boyko and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Boyko - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QVTOEnvironment;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;


public class SequenceTypeOperations extends AbstractContextualOperations {

	private SequenceTypeOperations(AbstractQVTStdlib library, EClassifier contextType) {
		super(library, contextType);		
	}

	public static AbstractContextualOperations[] getAllOperations(AbstractQVTStdlib library) {
		QVTOEnvironment environment = library.getEnvironment();
		return new AbstractContextualOperations[] {
			new SequenceTypeOperations(library, environment.getOCLStandardLibrary().getSequence()),
		};		
	}	
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getEnvironment().getOCLStandardLibrary();
		EClassifier sequenceOfT2 = TypeUtil.resolveSequenceType(getStdlib().getEnvironment(), oclStdlib.getT2());
		EClassifier sequenceOfCommonT = TypeUtil.resolveSequenceType(getStdlib().getEnvironment(), getStdlib().getCommonT());
		
		return new OperationProvider[] {
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=467600
			//
			new OperationProvider(SetTypeOperations.UNION, PredefinedType.UNION_NAME, new String[] {"s"}, //$NON-NLS-1$
					sequenceOfCommonT, sequenceOfT2),
			new OperationProvider(SetTypeOperations.INCLUDING, PredefinedType.INCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					sequenceOfCommonT, oclStdlib.getT2()),
			new OperationProvider(SetTypeOperations.EXCLUDING, PredefinedType.EXCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getSequence(), oclStdlib.getT2()),

			new OperationProvider(OrderedSetTypeOperations.APPEND, PredefinedType.APPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					sequenceOfCommonT, oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.PREPEND, PredefinedType.PREPEND_NAME, new String[] {"object"}, //$NON-NLS-1$
					sequenceOfCommonT, oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.INSERT_AT, PredefinedType.INSERT_AT_NAME, new String[] {"index", "object"}, //$NON-NLS-1$ //$NON-NLS-2$
					sequenceOfCommonT, oclStdlib.getInteger(), oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.INDEX_OF, PredefinedType.INDEX_OF_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getInteger(), oclStdlib.getT2()),
			new OperationProvider(OrderedSetTypeOperations.REVERSE, OrderedSetTypeOperations.REVERSE_NAME, oclStdlib.getSequence()),
		};
	}

}
