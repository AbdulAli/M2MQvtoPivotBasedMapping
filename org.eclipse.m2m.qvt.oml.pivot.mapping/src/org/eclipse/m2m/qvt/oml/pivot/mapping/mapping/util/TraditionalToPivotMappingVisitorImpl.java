package org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.evaluator.QvtOperationalEvaluationVisitorImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.EntryOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Helper;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.ocl.pivot.Operation;
import org.eclipse.qvto.examples.pivot.qvtoperational.QVTOperationalFactory;
import org.eclipse.qvto.examples.pivot.qvtoperational.utilities.QVTOperationalToStringVisitor;

public class TraditionalToPivotMappingVisitorImpl extends QvtOperationalEvaluationVisitorImpl
		implements TraditionalToPivotMappingVisitor {

	private org.eclipse.qvto.examples.pivot.qvtoperational.OperationalTransformation pivotOperationalTransformation;

	public TraditionalToPivotMappingVisitorImpl(QvtOperationalEnv env, QvtOperationalEvaluationEnv evalEnv) {
		super(env, evalEnv);
		pivotOperationalTransformation = QVTOperationalFactory.eINSTANCE.createOperationalTransformation();
	}

	@Override
	public Object visitHelper(Helper helper) {
		return QVTOperationalUtil.createHelper(helper, null, ImperativeOperationTypes.PIVOT_HELPER, null);
	}

	@Override
	public Object visitMappingOperation(MappingOperation mappingOperation) {
		return QVTOperationalUtil.createMappingOperation(mappingOperation, null,
				ImperativeOperationTypes.TRADITIONAL_MAPPING_OPERATION, null);
	}

	@Override
	public Object visitEntryOperation(EntryOperation entryOperation) {
		org.eclipse.qvto.examples.pivot.qvtoperational.EntryOperation pivotEntryOperation = QVTOperationalUtil
				.createEntryOperation(entryOperation, null, ImperativeOperationTypes.PIVOT_ENTRY_OPERATIONS, null);
		pivotOperationalTransformation.setEntry(pivotEntryOperation);
		return pivotEntryOperation;
	}

	@Override
	public Object visitModule(Module module) {
		java.util.@NonNull List<Operation> pivotOperations = pivotOperationalTransformation.getOwnedOperations();
		EList<EOperation> list = module.getEOperations();
		for (EOperation eOperation : list) {
			Operation tempOperation = (Operation) ((ImperativeOperation) eOperation).accept(this);
			//Adding by reference into pivot OperationalTransformation
			pivotOperations.add(tempOperation);
		}
		//pivotOperationalTransformation.accept(new QVTOperationalToStringVisitor(new StringBuilder()));
		return pivotOperationalTransformation;
	}

}
