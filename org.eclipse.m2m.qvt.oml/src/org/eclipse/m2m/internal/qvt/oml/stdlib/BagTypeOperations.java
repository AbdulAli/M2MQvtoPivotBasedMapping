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


public class BagTypeOperations extends AbstractContextualOperations {

	private BagTypeOperations(AbstractQVTStdlib library, EClassifier contextType) {
		super(library, contextType);		
	}

	public static AbstractContextualOperations[] getAllOperations(AbstractQVTStdlib library) {
		QVTOEnvironment environment = library.getEnvironment();
		return new AbstractContextualOperations[] {
			new BagTypeOperations(library, environment.getOCLStandardLibrary().getBag()),
		};		
	}	
	
	@Override
	protected OperationProvider[] getOperations() {
		OCLStandardLibrary<EClassifier> oclStdlib = getStdlib().getEnvironment().getOCLStandardLibrary();
		EClassifier setOfT2 = TypeUtil.resolveSetType(getStdlib().getEnvironment(), oclStdlib.getT2());
		EClassifier bagOfT2 = TypeUtil.resolveBagType(getStdlib().getEnvironment(), oclStdlib.getT2());
		EClassifier bagOfCommonT = TypeUtil.resolveBagType(getStdlib().getEnvironment(), getStdlib().getCommonT());
		
		return new OperationProvider[] {
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=467600
			//
			new OperationProvider(SetTypeOperations.UNION, PredefinedType.UNION_NAME, new String[] {"set"}, //$NON-NLS-1$
					bagOfCommonT, setOfT2),
			new OperationProvider(SetTypeOperations.UNION, PredefinedType.UNION_NAME, new String[] {"bag"}, //$NON-NLS-1$
					bagOfCommonT, bagOfT2),

			new OperationProvider(SetTypeOperations.INTERSECTION, PredefinedType.INTERSECTION_NAME, new String[] {"set"}, //$NON-NLS-1$
					oclStdlib.getSet(), setOfT2),
			new OperationProvider(SetTypeOperations.INTERSECTION, PredefinedType.INTERSECTION_NAME, new String[] {"bag"}, //$NON-NLS-1$
					oclStdlib.getBag(), bagOfT2),

			new OperationProvider(SetTypeOperations.INCLUDING, PredefinedType.INCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					bagOfCommonT, oclStdlib.getT2()),
			new OperationProvider(SetTypeOperations.EXCLUDING, PredefinedType.EXCLUDING_NAME, new String[] {"object"}, //$NON-NLS-1$
					oclStdlib.getBag(), oclStdlib.getT2()),
		};
	}

}
