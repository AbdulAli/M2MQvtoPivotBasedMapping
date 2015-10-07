package org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ocl.pivot.Type;
import org.eclipse.ocl.pivot.internal.utilities.PivotObjectImpl;
import org.eclipse.ocl.pivot.library.LibraryFeature;
import org.eclipse.qvto.examples.pivot.qvtoperational.EntryOperation;
import org.eclipse.qvto.examples.pivot.qvtoperational.Helper;
import org.eclipse.qvto.examples.pivot.qvtoperational.MappingOperation;
import org.eclipse.qvto.examples.pivot.qvtoperational.QVTOperationalFactory;

public class QVTOperationalUtil extends org.eclipse.ocl.pivot.utilities.PivotUtil {
	
	public static @NonNull Helper createHelper(@NonNull EOperation traditionalHelper, @NonNull Type type, @Nullable String implementationClass, @Nullable LibraryFeature implementation) {

		Helper pivotHelper = QVTOperationalFactory.eINSTANCE.createHelper();
		pivotHelper.setName(traditionalHelper.getName());
		// TODO: traditionalHelper EType needs to be converted to pivotHelper Type
		pivotHelper.setType(type);
		pivotHelper.setImplementationClass(implementationClass);
		pivotHelper.setImplementation(implementation);
		((PivotObjectImpl)pivotHelper).setESObject(traditionalHelper);
		return pivotHelper;
	}
	public static @NonNull MappingOperation createMappingOperation(@NonNull EOperation traditionalMappingOperation, @NonNull Type type, @Nullable String implementationClass, @Nullable LibraryFeature implementation) 
	{
		MappingOperation pivotMappingOperation = QVTOperationalFactory.eINSTANCE.createMappingOperation();
		pivotMappingOperation.setName(traditionalMappingOperation.getName());
		// TODO: traditionalMappingOperation EType needs to be converted to pivotMappingOperation Type
		pivotMappingOperation.setType(type);
		pivotMappingOperation.setImplementationClass(implementationClass);
		pivotMappingOperation.setImplementation(implementation);
		((PivotObjectImpl)pivotMappingOperation).setESObject(traditionalMappingOperation);
		return pivotMappingOperation;
	}
	
	public static @NonNull EntryOperation createEntryOperation(@NonNull EOperation traditionalEntryOperation, @NonNull Type type, @Nullable String implementationClass, @Nullable LibraryFeature implementation) 
	{
		EntryOperation pivotEntryOperation= QVTOperationalFactory.eINSTANCE.createEntryOperation();
		pivotEntryOperation.setName(traditionalEntryOperation.getName());
		// TODO: traditionalEntryOperation EType needs to be converted to pivotEntryOperation Type
		pivotEntryOperation.setType(type);
		pivotEntryOperation.setImplementationClass(implementationClass);
		pivotEntryOperation.setImplementation(implementation);
		((PivotObjectImpl)pivotEntryOperation).setESObject(traditionalEntryOperation);
		
		return pivotEntryOperation;
		
	}
	
	
}
