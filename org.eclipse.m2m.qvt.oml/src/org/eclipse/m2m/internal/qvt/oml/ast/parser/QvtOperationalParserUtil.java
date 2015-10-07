/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 289982
 *     Alex Paperno - bugs 424584
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNode;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTSyntheticNodeAccess;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtEnvironmentBase;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.cst.ConstructorCS;
import org.eclipse.m2m.internal.qvt.oml.cst.ImportCS;
import org.eclipse.m2m.internal.qvt.oml.cst.LibraryCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingDeclarationCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingExtensionCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingExtensionKindCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingMethodCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingModuleCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingQueryCS;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingRuleCS;
import org.eclipse.m2m.internal.qvt.oml.cst.ModelTypeCS;
import org.eclipse.m2m.internal.qvt.oml.cst.ModulePropertyCS;
import org.eclipse.m2m.internal.qvt.oml.cst.ScopedNameCS;
import org.eclipse.m2m.internal.qvt.oml.cst.TransformationHeaderCS;
import org.eclipse.m2m.internal.qvt.oml.cst.UnitCS;
import org.eclipse.m2m.internal.qvt.oml.emf.util.EmfUtil;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ReturnExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.Typedef;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.cst.CollectionTypeCS;
import org.eclipse.ocl.cst.PathNameCS;
import org.eclipse.ocl.cst.PrimitiveTypeCS;
import org.eclipse.ocl.cst.SimpleNameCS;
import org.eclipse.ocl.cst.TupleTypeCS;
import org.eclipse.ocl.cst.TypeCS;
import org.eclipse.ocl.cst.VariableCS;
import org.eclipse.ocl.cst.impl.CSTNodeImpl;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.CollectionType;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.types.BagType;
import org.eclipse.ocl.types.OrderedSetType;
import org.eclipse.ocl.types.PrimitiveType;
import org.eclipse.ocl.types.SequenceType;
import org.eclipse.ocl.types.SetType;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.PredefinedType;
import org.eclipse.ocl.utilities.UMLReflection;


public class QvtOperationalParserUtil {
	
	private static final String NAMESPACE_SEPARATOR = "."; //$NON-NLS-1$
	
	public static final String QVT_NAMESPACE_URI = "http://www.eclipse.org/m2m/1.0.0/QVT"; //$NON-NLS-1$
	private static final String QVT_IS_ABSTRACT = "abstract"; //$NON-NLS-1$
	private static final String QVT_IS_STATIC = "static"; //$NON-NLS-1$	
	
	private static final String QVT_INIT_EXPRESSION_URI = QVT_NAMESPACE_URI + "/initExp"; //$NON-NLS-1$

	public static final String QVT_AUTOGEN_MODELPARAM_EXPRESSION_URI = QVT_NAMESPACE_URI + "/autoModelParam"; //$NON-NLS-1$
	
	private static final String QVT_DEPRECATED = "deprecated"; //$NON-NLS-1$
	private static final String QVT_UNSUPPORTED = "unsupported"; //$NON-NLS-1$	

	private QvtOperationalParserUtil() {
	}

	public static EClassifier getContextualType(ImperativeOperation operation) {
		VarParameter context = operation.getContext();
		return context != null ? context.getEType() : null;		
	}
	
	public static boolean isContextual(ImperativeOperation operation) {
		return getContextualType(operation) != null;
	}

	public static String getMappingStringRepresentation(MappingMethodCS operationCS) {
		MappingDeclarationCS mappingDeclarationCS = operationCS.getMappingDeclarationCS();
		if(mappingDeclarationCS != null) {
			StringBuilder buf = new StringBuilder();
			if(mappingDeclarationCS.getContextType() != null) {
				buf.append(QvtOperationalParserUtil.getStringRepresentation(mappingDeclarationCS.getContextType()));
				buf.append(EmfUtil.PATH_SEPARATOR);
			}
			if(mappingDeclarationCS.getSimpleNameCS() != null) {				
				buf.append(mappingDeclarationCS.getSimpleNameCS().getValue());
			}
			return buf.toString();
		}
		
		return ""; //$NON-NLS-1$
	}
	
	public static String getStringRepresentation(PathNameCS pathName, String pathSeparator) {
		return getStringRepresentation(pathName.getSimpleNames(), pathSeparator);
	}
	
	public static String getStringRepresentation(PathNameCS pathName) {
		return getStringRepresentation(pathName.getSimpleNames(), EmfUtil.PATH_SEPARATOR);
	}
	
	
	public static String getStringRepresentation(List<SimpleNameCS> pathName, String pathSeparator) {
		StringBuffer buffer = null;
		for (SimpleNameCS element : pathName) {
			if (buffer != null) {
				buffer.append(pathSeparator);
			} else {
				buffer = new StringBuffer();
			}
			buffer.append(element.getValue());
		}
		return buffer == null ? "" : buffer.toString(); //$NON-NLS-1$
	}

	public static String getStringRepresentation(ScopedNameCS scopedNameCS) {
		StringBuilder buf = new StringBuilder();
		if(scopedNameCS.getTypeCS() != null) {
			buf.append(getStringRepresentation(scopedNameCS.getTypeCS()));
			buf.append(EmfUtil.PATH_SEPARATOR);
		}
		
		if(scopedNameCS.getName() != null) {			
			buf.append(scopedNameCS.getName());			
		}
		return buf.toString();
	}
	
	public static String getStringRepresentation(TypeCS typeCS) {
		if (typeCS instanceof PrimitiveTypeCS) {
			return ((PrimitiveTypeCS) typeCS).getValue();
		}
		else if (typeCS instanceof PathNameCS) {
			return getStringRepresentation((PathNameCS) typeCS, EmfUtil.PATH_SEPARATOR);
		}
		else if (typeCS instanceof CollectionTypeCS) {
			return ((CollectionTypeCS) typeCS).getCollectionTypeIdentifier().getName() 
					+ "(" + getStringRepresentation(((CollectionTypeCS) typeCS).getTypeCS()) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (typeCS instanceof TupleTypeCS) {
			StringBuffer result = new StringBuffer();
			for (VariableCS var : ((TupleTypeCS) typeCS).getVariables()) {
				if (result.length() > 0) {
					result.append(","); //$NON-NLS-1$
				}
				result.append(var.getName());
				result.append(":"); //$NON-NLS-1$
				result.append(getStringRepresentation(var.getTypeCS()));
			}
			return TupleType.SINGLETON_NAME + "(" + result.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return ""; //$NON-NLS-1$
	}
	
	public static void setInitExpression(EStructuralFeature moduleFeature, OCLExpression<EClassifier> expression) {
		if(expression == null) {
			// possible NPE caused by parsing errors => no AST expression produce			
			return; 
		}
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QVT_INIT_EXPRESSION_URI);
		annotation.getContents().add(expression);
		moduleFeature.getEAnnotations().add(annotation);
	}
	
	@SuppressWarnings("unchecked")
	public static OCLExpression<EClassifier> getInitExpression(EStructuralFeature feature) {
		EAnnotation annotation = feature.getEAnnotation(QVT_INIT_EXPRESSION_URI);		
		if(annotation != null) {
			for (EObject referredObj : annotation.getContents()) {
				if(referredObj instanceof OCLExpression) {
					return (OCLExpression<EClassifier>)referredObj;
				}
			}
		}
		return null; 
	}
		
	public static Module getOwningModule(ImperativeOperation operation) {
		if(operation.getEContainingClass() instanceof Module) {			
			return (Module) operation.getEContainingClass();
		}
		return null; 
	}

	public static Module getOwningModule(EOperation operation) {		
		EClassifier owner = operation.getEContainingClass();
		if(owner == null) {
			return null;
		}
		
		// check for operations directly owned by a QVT moduleAST
		if(owner instanceof Module) {
			return (Module) owner;
		}
		
		EPackage owningPackage = owner.getEPackage();
		if(owningPackage == null) {
			return null;
		}
		
		if(owner instanceof Typedef) {
			if(owningPackage instanceof Module) {
				return (Module) owningPackage;						
			} 

			// our QVT AST meta-model contains the additional operations via
			// typedef stored directly in the owning moduleAST 
			// Additionally, support the nested 'additions' package as implemented by MDT OCL					
			EPackage superOwner = owningPackage.getESuperPackage();
			if(superOwner instanceof Module) {
				return (Module) superOwner;
			} 
		} 
		
		if(owningPackage instanceof Module) {
			// handles cases like plain EOperation on Model class in Stdlib
			return (Module)owningPackage;
		}
		
		return null; 
	}
	
	public static List<EOperation> getOwnedOperations(Module module) {
		List<EOperation> result = new ArrayList<EOperation>(module.getEOperations().size());
		for (EOperation operation : module.getEOperations()) {
			if(operation instanceof ImperativeOperation) {
				result.add(operation);
			}
		}
		return result;
	}
	
	public static void collectAllImports(CompiledUnit unit, Set<CompiledUnit> result) {		
		for (CompiledUnit importedUnit : unit.getCompiledImports()) {			
			if (!result.contains(importedUnit)) {				
				collectAllImports(importedUnit, result);				
			}
			
			result.add(importedUnit);			
		}
	}	

	public static void collectAllImports(Module module, Set<Module> result) {
		collectAllImportsByKind(module, result, null);
	}

	public static Set<Module> collectAllImportsByKind(Module module, Set<Module> result, ImportKind importKind) {
		if (result == null) {
			result = new HashSet<Module>();
		}
		for (ModuleImport imp : module.getModuleImport()) {
			if (imp == null || imp.getImportedModule() == null) {
				continue;
			}
			if ((importKind == null) || (imp.getKind() == importKind)) {
				if (!result.contains(imp.getImportedModule())) {
					collectAllImportsByKind(imp.getImportedModule(), result, importKind);
				}
				result.add(imp.getImportedModule());
			}
		}
		return result;
	}

	/**
	 * Remark: accepts null types, but always returns <code>false</code> if any
	 * of the type argument is <code>null</code>
	 */
	public static boolean isAssignableToFrom(
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, 
			EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env, 
			EClassifier variableType, EClassifier initialiserType) {
		if (variableType == null)
			return false;
		if (initialiserType == null)
			return false;
		
		// handle primitive types
		if (variableType == env.getOCLStandardLibrary().getUnlimitedNatural()) {
			// FIXME - we should not have any special handling of OCL types
			// See related, https://bugs.eclipse.org/bugs/show_bug.cgi?id=260403		
			if (initialiserType == env.getOCLStandardLibrary().getInteger()) {
				return true;
			}
		}
		
		// FIXME - better be handled in OCL itself
		// check was added due to https://bugs.eclipse.org/bugs/show_bug.cgi?id=275824
		if (variableType instanceof EDataType && initialiserType instanceof EDataType && variableType instanceof PredefinedType<?> == false) {
			if (((EDataType) variableType).getInstanceClass() == ((EDataType) initialiserType).getInstanceClass()) {
				return true;
			}
			if (initialiserType instanceof EEnum &&
					initialiserType == ExtendedMetaData.INSTANCE.getBaseType((EDataType) variableType )) {
				return true;
			}
		}
		
		return (TypeUtil.getRelationship(env, variableType, initialiserType) & UMLReflection.SUPERTYPE) != 0;
	}

	/**
	 * Get the moduleAST simple name as the last element of possible qualified
	 * name.
	 * 
	 * @return the name string or empty string, of the moduleAST name sequence is
	 *         empty
	 */
	public static String getMappingModuleSimpleName(TransformationHeaderCS headerCS) {
		EList<SimpleNameCS> moduleName = headerCS.getPathNameCS().getSimpleNames();
		if (moduleName.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		return moduleName.get(moduleName.size()-1).getValue();
	}
	
	public static boolean hasSimpleName(TransformationHeaderCS headerCS) {
		if(headerCS.getPathNameCS() != null) {
			return headerCS.getPathNameCS().getSimpleNames().size() <= 1;
		}
		return false;
	}	
	
	public static String getMappingModuleQualifiedName(TransformationHeaderCS headerCS) {
		String namespace = getMappingModuleNamespace(headerCS);
		if(namespace == null || namespace.length() == 0) {
			return getMappingModuleSimpleName(headerCS);
		}
		return namespace + NAMESPACE_SEPARATOR + getMappingModuleSimpleName(headerCS);
	}
	
	/**
	 * @return dot separated namespace or empty string.
	 */
	public static String getMappingModuleNamespace(TransformationHeaderCS headerCS) {
		StringBuilder unitNamespace = new StringBuilder();
		EList<SimpleNameCS> moduleName = headerCS.getPathNameCS().getSimpleNames();
		if (moduleName.size() > 1) {
			for (int i = 0, sz = moduleName.size(); i < sz - 1; i++) {
				if (i > 0) {
					unitNamespace.append(NAMESPACE_SEPARATOR);
				}
				unitNamespace.append(moduleName.get(i));
			}
		}
		return unitNamespace.toString();
	}

	public static boolean isTypeCast(final EOperation operation) {
		if (operation != null) {
			return PredefinedType.OCL_AS_TYPE_NAME.equals(operation.getName());
		}
		return false;
	}

	// FIXME - to be removed => use getRelationShip(t1, t2) operation
	public static boolean isIncorrectCast(final EClassifier sourceType, final EClassifier targetType) {
		if (sourceType == null || targetType == null) {
			return false; // error should be reported in this case, not
			// warning
		}
		if (sourceType instanceof PrimitiveType
				&& PrimitiveType.REAL_NAME.equals(((PrimitiveType<?>) sourceType).getName())) {
			return targetType instanceof PrimitiveType
					&& PrimitiveType.INTEGER_NAME.equals(((PrimitiveType<?>) targetType).getName());
		}
		if (sourceType instanceof SetType) {
			return !(targetType instanceof SetType);
		}
		if (sourceType instanceof BagType) {
			return !(targetType instanceof BagType);
		}
		if (sourceType instanceof SequenceType) {
			return !(targetType instanceof SequenceType);
		}
		if (sourceType instanceof OrderedSetType) {
			return !(targetType instanceof OrderedSetType || targetType instanceof SetType);
		}
		return false;
	}

	public static boolean validateAssignment(boolean isProperty, String leftName, EClassifier leftType,
			EClassifier rightType, boolean isIncremental, CSTNode lValueCS, CSTNode rValueCS,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, 
			EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env) {
		
		if (isIncremental) {
			if (leftType instanceof CollectionType == false) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.SemanticUtil_3, new Object[] { leftName }), lValueCS);
				return false;
			}

			EClassifier baseType = ((CollectionType) leftType).getElementType();
			EClassifier actualType = rightType;
			if (actualType instanceof CollectionType) {
				actualType = ((CollectionType) actualType).getElementType();
			}

			if (!QvtOperationalParserUtil.isAssignableToFrom(env, baseType, actualType)) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.SemanticUtil_5, 
						new Object[] { leftName, QvtOperationalTypesUtil.getTypeFullName(baseType),
							QvtOperationalTypesUtil.getTypeFullName(actualType) }), rValueCS);
				return false;
			}
		}
		else if (isProperty && leftType instanceof CollectionType) {
			EClassifier baseType = ((CollectionType) leftType).getElementType();
			EClassifier actualType = rightType;
			if (actualType instanceof CollectionType) {
				actualType = ((CollectionType) actualType).getElementType();
			}

			if (!QvtOperationalParserUtil.isAssignableToFrom(env, baseType, actualType)) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.SemanticUtil_5, 
						new Object[] { leftName, QvtOperationalTypesUtil.getTypeFullName(baseType),
							QvtOperationalTypesUtil.getTypeFullName(actualType) }), rValueCS);
				return false;
			}
		}
		else {
			if (!QvtOperationalParserUtil.isAssignableToFrom(env, leftType, rightType)) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.SemanticUtil_8, new Object[] { leftName,
				        QvtOperationalTypesUtil.getTypeFullName(leftType),
				        QvtOperationalTypesUtil.getTypeFullName(rightType) }), rValueCS);
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the given variable is allowed to be modified, reports an error in
	 * case it is not.
	 * 
	 * @param variable
	 *            the variable to be assigned a value or indirectly modified through
	 *            an owned property
	 * @param varNodeCS
	 *            the pathname representing the left side of an assignment. It can be a simple name
	 *            representing a variable direct access or a path navigating to owned property.
	 * @param varPathNamePropertyASTopt AST property element for a property if any available as the target for modification  
	 *             
	 * @return <code>true</code> if it can be modified, <code>false</code>
	 *         otherwise.
	 */
	public static boolean validateVariableModification(Variable<EClassifier, EParameter> variable,
			CSTNode varNodeCS, EStructuralFeature varPathNamePropertyASTopt, 
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, 
			EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env,
			boolean isDirectModification) {
		EParameter representedParameter = variable.getRepresentedParameter();
		if (representedParameter instanceof VarParameter) {
			VarParameter parameter = (VarParameter) representedParameter;
			// detect whether an [inout] parameter variable is to be assigned a new value
			boolean isInsideMapping = env.getContextOperation() instanceof MappingOperation;
			boolean isDirectInoutModification = parameter.getKind() == DirectionKind.INOUT && isDirectModification && isInsideMapping;
			boolean isDirectOutModificationOutsideInit = parameter.getKind() == DirectionKind.OUT && isDirectModification && isInsideMapping 
					&& (env instanceof QvtOperationalEnv) && !((QvtOperationalEnv)env).isWithinInitMappingSection();
			boolean isContextualPropertyAccessed = varPathNamePropertyASTopt instanceof ContextualProperty;
			
			if(isDirectInoutModification) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.QvtOperationalParserUtil_inoutParamAssignmentError, parameter.getName()),
						varNodeCS);
				return false;
			}
			
			if(isDirectOutModificationOutsideInit) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.QvtOperationalParserUtil_outParamAssignmentError, parameter.getName()),
						varNodeCS);
				return false;
			}

			if (parameter.getKind() != DirectionKind.OUT && parameter.getKind() != DirectionKind.INOUT && isContextualPropertyAccessed == false) {
				QvtOperationalUtil.reportError(env, NLS.bind(ValidationMessages.inputParameterModificationError, variable.getName()),
						varNodeCS);
				return false;
			}
		}
		return true;
	}

	/**
	 * Finds the first imperative operation named <code>main</code>.
	 * <p>
	 * Note: This method isolates the caller from the legacy QVT AST model incompatibility, 
	 * allowing a mapping to be the entry operation, while the OMG specification requires
	 * EntryOperation only. 
	 */
	public static ImperativeOperation getMainOperation(Module module) {
		if(module.getEntry() != null) {
			return module.getEntry();
		}
		
		for (Iterator<EOperation> ruleIt = module.getEOperations().iterator(); ruleIt.hasNext();) {
			EOperation nextOperation = ruleIt.next();
			
			if (QvtOperationalEnv.MAIN.equals(nextOperation.getName()) &&
				nextOperation instanceof ImperativeOperation) {
				return (ImperativeOperation)nextOperation;
			}
		}

		return null;
	}

	public static boolean isOverloadableMapping(final EOperation op, final QvtOperationalEnv env) {
		EObject context = op.eContainer();
		if (env.getUMLReflection().getOwningClassifier(op) instanceof Module) {
			// no context to overload
			return false;
		}
		return context instanceof EClass;
	}
	
	public static String safeGetMappingQualifiedName(QvtOperationalEnv env, ImperativeOperation operation) {
		if(operation != null) {
			StringBuilder buf = new StringBuilder();			
			EClassifier ctxType = getContextualType(operation);
			
			if(ctxType != null) {
				buf.append(safeGetQualifiedName(env, ctxType));
				buf.append(EmfUtil.PATH_SEPARATOR);
			}
			
			if(operation.getName() != null) {
				buf.append(operation.getName());
			}
			
			return buf.toString();			
		}
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Null-safe variant of {@link UMLReflection#getQualifiedName(Object)}
	 * operation.
	 * 
	 * @param type
	 *            a classifier that may be <code>null</code>
	 * @param defaultValue
	 *            the default value if a non-null can not be derived
	 * @return the name, or <code>defaultValue</code> if a non-null name can not be
	 *         derived
	 */	
	public static String safeGetQualifiedName(QvtOperationalEnv env, EClassifier type, String defaultValue) {
		if(type == null) {
			return defaultValue;
		}
		
		String result = defaultValue;
		if(type.getEPackage() == null) {
			result = env.getUMLReflection().getName(type);
		}
		else {
			try {
				result = env.getUMLReflection().getQualifiedName(type);
			} catch(RuntimeException e) {
				result = env.getUMLReflection().getName(type);
			}
		}

		return result != null ? result : defaultValue;
	}
	
	/**
	 * Null-safe variant of {@link UMLReflection#getQualifiedName(Object)}
	 * operation.
	 * 
	 * @param type
	 *            a classifier that may be <code>null</code>
	 * @return the name, or empty string if a non-null name can not be derived
	 */		
	public static String safeGetQualifiedName(QvtOperationalEnv env, EClassifier type) {
		return safeGetQualifiedName(env, type, ""); //$NON-NLS-1$
	}
	
	public static <T> T findParentElement(ReturnExp returnExp, Class<T> type) {
		T result = null;
		EObject parent = returnExp.eContainer();
		while(parent != null) {			
			if(type.isInstance(parent)) {
				result = (T) type.cast(parent);
				break;
			}
			parent = parent.eContainer();
		}
		return result;
	}

	public static void markAsUnsupported(EModelElement element, String reason) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QVT_NAMESPACE_URI);
		annotation.getDetails().put(QVT_UNSUPPORTED, reason);
		element.getEAnnotations().add(annotation);
	}

	public static boolean isUnsupported(EModelElement element) {
		EAnnotation annotation = element.getEAnnotation(QVT_NAMESPACE_URI);
		if(annotation != null) {
			return annotation.getDetails().containsKey(QVT_UNSUPPORTED);
		}
		
		return false;		
	}
	
	public static String getUnsupportedReason(EModelElement element) {
		EAnnotation annotation = element.getEAnnotation(QVT_NAMESPACE_URI);
		if(annotation != null) {
			return annotation.getDetails().get(QVT_UNSUPPORTED);
		}
		return null;		
	}
	
	public static void markAsDeprecated(EModelElement element) {
		markAsDeprecated(element, null);
	}
	
	public static void markAsDeprecated(EModelElement element, String replacingReferenceOpt) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QVT_NAMESPACE_URI);
		annotation.getDetails().put(QVT_DEPRECATED, replacingReferenceOpt);
		element.getEAnnotations().add(annotation);
	}
	
	
	public static boolean isDeprecated(EModelElement element) {
		EAnnotation annotation = element.getEAnnotation(QVT_NAMESPACE_URI);
		return annotation != null && annotation.getDetails().containsKey(QVT_DEPRECATED);
	}
	
	public static String getDeprecatedBy(EModelElement element) {
		EAnnotation annotation = element.getEAnnotation(QVT_NAMESPACE_URI);
		if(annotation != null) {
			return annotation.getDetails().get(QVT_DEPRECATED);
		}		
		return null;
	}
	
	static void markAsAbstractOperation(ImperativeOperation operation) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QVT_NAMESPACE_URI);
		annotation.getDetails().put(QVT_IS_ABSTRACT, Boolean.toString(true));
		operation.getEAnnotations().add(annotation);
	}
	
	static boolean isAbstractOperation(ImperativeOperation operation) {
		EAnnotation annotation = operation.getEAnnotation(QVT_NAMESPACE_URI);
		if(annotation != null) {
			String value = annotation.getDetails().get(QVT_IS_ABSTRACT);
			return Boolean.valueOf(value);
		}
		return false;
	}
	
	public static void markAsStaticOperation(EOperation operation) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(QVT_NAMESPACE_URI);
		annotation.getDetails().put(QVT_IS_STATIC, Boolean.toString(true));
		operation.getEAnnotations().add(annotation);
	}
	
	public static boolean isStaticOperation(EOperation operation) {
		EAnnotation annotation = operation.getEAnnotation(QVT_NAMESPACE_URI);
		if(annotation != null) {
			String value = annotation.getDetails().get(QVT_IS_STATIC);
			return Boolean.valueOf(value);
		}
		return false;
	}
	
	public static CSTNode getPropertyProblemNode(EStructuralFeature feature, QvtOperationalEnv env) {
		CSTNode cstNode = env.getASTMapping(feature);
		if(cstNode instanceof ModulePropertyCS) {
			CSTNode nameCS = ((ModulePropertyCS)cstNode).getSimpleNameCS();
			if(nameCS != null) {
				cstNode = nameCS;
			}
		}
		return cstNode;
	}
	
	public static CSTNode getMethodNameProblemNodeCS(MappingMethodCS methodCS) {
		MappingDeclarationCS mappingDeclCS = methodCS.getMappingDeclarationCS();
		if(mappingDeclCS != null) {
			final int pos[] = {mappingDeclCS.getStartOffset(), mappingDeclCS.getEndOffset()};
			
			if(mappingDeclCS.getContextType() != null) {
				pos[0] = mappingDeclCS.getContextType().getStartOffset();
				pos[1] = mappingDeclCS.getContextType().getEndOffset();
			}
			
			if(mappingDeclCS.getSimpleNameCS() != null) {
				pos[1] = mappingDeclCS.getSimpleNameCS().getEndOffset();
			}
			
			return new CSTNodeImpl() {
				@Override
				public int getStartOffset() {
					return pos[0];
				}
				@Override
				public int getEndOffset() {
					return pos[1];
				}
			};
		}
		return methodCS;
	}
	
	public static CSTNode getMethodHeaderProblemNodeCS(MappingMethodCS methodCS) {
		MappingDeclarationCS mappingDeclCS = methodCS.getMappingDeclarationCS();
		if(mappingDeclCS != null) {
			final int pos[] = {mappingDeclCS.getStartOffset(), mappingDeclCS.getEndOffset()};
			
			if(mappingDeclCS.getContextType() != null) {
				pos[1] = mappingDeclCS.getContextType().getEndOffset();
			}
			if(mappingDeclCS.getSimpleNameCS() != null) {
				pos[1] = mappingDeclCS.getSimpleNameCS().getEndOffset();
			}
			if(!mappingDeclCS.getParameters().isEmpty()) {
				pos[1] = mappingDeclCS.getParameters().get(mappingDeclCS.getParameters().size()-1).getEndOffset();
			}
			if(!mappingDeclCS.getResult().isEmpty()) {
				pos[1] = mappingDeclCS.getResult().get(mappingDeclCS.getResult().size()-1).getEndOffset();
			}
			
			return new CSTNodeImpl() {
				@Override
				public int getStartOffset() {
					return pos[0];
				}
				@Override
				public int getEndOffset() {
					return pos[1];
				}
			};
		}
		return methodCS;
	}
	
	public static <T> Collection<T> selectDuplicateQualifiers(List<T> elements) {
		Set<T> result = null;
		for (T nextQualifier : elements) {
			if(Collections.frequency(elements, nextQualifier) > 1) {
				if(result == null) {
					result = new HashSet<T>(2);   
				}
				
				result.add(nextQualifier);
			}
		} 
		return (result != null) ? result : Collections.<T>emptySet();
	}
	
	public static Module createModule(MappingModuleCS moduleCS) {
        String name = null;
        TransformationHeaderCS headerCS = moduleCS.getHeaderCS();
		if(headerCS != null && headerCS.getPathNameCS() != null) {
        	EList<SimpleNameCS> sequenceOfNames = headerCS.getPathNameCS().getSimpleNames();
        	if(!sequenceOfNames.isEmpty()) {
        		name = sequenceOfNames.get(0).getValue();
        	}
        }
		
        Module module;
        if(moduleCS instanceof LibraryCS) {
        	module = QvtOperationalStdLibrary.createLibrary(name);
        } else {
        	module = QvtOperationalStdLibrary.INSTANCE.createTransformation(name);
        }
 
        moduleCS.setAst(module);
        
        if(headerCS != null) {
        	ASTSyntheticNode astNode = ASTSyntheticNodeAccess.createASTNode(module);
        	astNode.setStartPosition(headerCS.getStartOffset());
        	astNode.setEndPosition(headerCS.getEndOffset());
        }
        		
        return module;
	}

	public static List<ImportCS> getImports(UnitCS unitCS) {
		List<ImportCS> allImports = new ArrayList<ImportCS>();
		allImports.addAll(unitCS.getImports());
		
		for (MappingModuleCS nextModule : unitCS.getModules()) {
			allImports.addAll(nextModule.getImports());	
		}
		return allImports;		
	}

	public static List<ModelTypeCS> getModelTypes(UnitCS unitCS) {
		List<ModelTypeCS> modelTypes = new ArrayList<ModelTypeCS>();
		
		modelTypes.addAll(unitCS.getModelTypes());
		
		for (MappingModuleCS nextModule : unitCS.getModules()) {
			modelTypes.addAll(nextModule.getMetamodels());	
		}
		
		return modelTypes;		
	}	
	
	public static String wrappInSeeErrorLogMessage(String message) {
		return NLS.bind(ValidationMessages.QvtOperationalVisitorCS_SeeErrorLogForDetails, message);
	}
	
	public static Variable<EClassifier, EParameter> getThisVariable(Module module) {
		for (Variable<EClassifier, EParameter> var : module.getOwnedVariable()) {
			if(QvtOperationalEnv.THIS.equals(var.getName())) {
				return var;
			}
		}
		return null;
	}

    public static boolean isExtendingEnv(QvtEnvironmentBase env, Module extended) {
    	if(extended == null) {
    		throw new IllegalArgumentException();
    	}
    	
    	for (QvtEnvironmentBase extEnv : env.getAllExtendedModules()) {
			if(extended ==  extEnv.getModuleContextType()) {
				return true; 
			}
		}
    	
    	return false;
    }

	public static List<String> getSequenceOfNames(List<SimpleNameCS> names) {
		List<String> result = new ArrayList<String>(names.size());
		for (SimpleNameCS nameCS : names) {
			result.add(nameCS.getValue());
		}
		return result;
	}
	
	public static boolean hasOperationBody(MappingMethodCS methodCS) {
		return
			(methodCS instanceof MappingRuleCS  && ((MappingRuleCS) methodCS).getMappingBody() != null) ||
			(methodCS instanceof ConstructorCS  && ((ConstructorCS) methodCS).getBody() != null) ||
			(methodCS instanceof MappingQueryCS && ((MappingQueryCS) methodCS).getBody() != null) ||
			(methodCS instanceof ConstructorCS  && ((ConstructorCS) methodCS).getBody() != null);
	}
	
	public static boolean isDisjunctiveMappingOperation(MappingMethodCS methodCS) {
		for (MappingExtensionCS extensionCS : methodCS.getMappingDeclarationCS().getMappingExtension()) {
			if(extensionCS.getKind() == MappingExtensionKindCS.DISJUNCTS) {
				return true;
			}
		}
		
		return false;
	}

}