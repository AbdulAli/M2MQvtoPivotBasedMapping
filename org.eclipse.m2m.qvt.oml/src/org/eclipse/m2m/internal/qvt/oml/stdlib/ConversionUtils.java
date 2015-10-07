package org.eclipse.m2m.internal.qvt.oml.stdlib;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.types.PrimitiveType;

public class ConversionUtils {
	
	private ConversionUtils()
	{}

	public static Object createFromString(final EClassifier type, final String stringValue) {
		if (stringValue == null) {
			return null;
		}
		
        if (type instanceof DictionaryType) {
        	return new ConversionDictionary((DictionaryType)type).createFromString(stringValue);
        }
        if (type instanceof CollectionType) {
        	return new ConversionCollection((CollectionType)type).createFromString(stringValue);
        }
        
        // QVT primitive type
        // FIXME - should rather used primitive type singletons from the Standard library
        try {
	        if (isIntegerType(type)) {
	            return new Integer(stringValue);
	        } 
	        if (isRealType(type)) {
	            return new Double(stringValue);
	        }
        } catch (NumberFormatException e) {
		     throw new IllegalArgumentException("Invalid value: '" + stringValue + "' for datatype: "+type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
        
        if (isStringType(type)) {
            return new String(stringValue);
        } 
        
        if (isBooleanType(type)) {
            return Boolean.valueOf(stringValue);
        } 
        // Enumeration
        if (type instanceof EDataType) {
        	if(type.getEPackage() != null && type.getEPackage().getEFactoryInstance() != null) {
	            Object value = type.getEPackage().getEFactoryInstance().createFromString((EDataType) type, stringValue);
	            if (value != null) {
	            	return value;
	            }
			}
        }
        
        throw new IllegalArgumentException("Invalid value: '" + stringValue + "' for datatype: "+type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setupConversionDelegate(EClassifier type) {
    	if (type instanceof DictionaryType) {
    		EDataType.Internal internalType = (EDataType.Internal) type;
    		if (internalType.getConversionDelegate() == null) {
    			internalType.setConversionDelegate(new ConversionDictionary((DictionaryType)type));
    		}
    	}
    	else if (type instanceof CollectionType) {
    		EDataType.Internal internalType = (EDataType.Internal) type;
    		if (internalType.getConversionDelegate() == null) {
    			internalType.setConversionDelegate(new ConversionCollection((CollectionType)type));
    		}
        }
	}

	private static boolean isRealType(final EClassifier type) {
		return type instanceof org.eclipse.ocl.ecore.PrimitiveType && PrimitiveType.REAL_NAME.equals(((org.eclipse.ocl.ecore.PrimitiveType) type).getName());
	}

	private static boolean isIntegerType(final EClassifier type) {
		return type instanceof org.eclipse.ocl.ecore.PrimitiveType && PrimitiveType.INTEGER_NAME.equals(((org.eclipse.ocl.ecore.PrimitiveType) type).getName());
	}

	private static boolean isBooleanType(final EClassifier type) {
		return type instanceof org.eclipse.ocl.ecore.PrimitiveType && PrimitiveType.BOOLEAN_NAME.equals(((org.eclipse.ocl.ecore.PrimitiveType) type).getName());
	}

	static boolean isStringType(final EClassifier type) {
		return type instanceof org.eclipse.ocl.ecore.PrimitiveType && PrimitiveType.STRING_NAME.equals(((org.eclipse.ocl.ecore.PrimitiveType) type).getName());
	}
	
	
}
