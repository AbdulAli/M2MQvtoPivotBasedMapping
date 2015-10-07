package org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util;

import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitor;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;

public interface TraditionalToPivotMappingVisitor extends QvtOperationalEvaluationVisitor {
	
	public Object visitHelper(Helper helper);

	public Object visitMappingOperation(MappingOperation mappingOperation);

	public Object visitEntryOperation(EntryOperation entryOperation);

	public Object visitModule(Module module);
}
