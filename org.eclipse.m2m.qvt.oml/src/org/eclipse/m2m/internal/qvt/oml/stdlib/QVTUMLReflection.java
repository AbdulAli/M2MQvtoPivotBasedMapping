/*******************************************************************************
 * Copyright (c) 2008, 2015 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 302594, 310991
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.stdlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.HiddenElementAdapter;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.common.project.Pair;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeOCLPackage;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.Typedef;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;

public class QVTUMLReflection 
		implements UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, 
								EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> 
{	
	private UMLReflection<EPackage, EClassifier, EOperation, 
					EStructuralFeature, EEnumLiteral, EParameter, EObject, 
					CallOperationAction, SendSignalAction, Constraint> fUmlReflection;
	
	private final Map<Object, EClassifier> fTypeCache = new IdentityHashMap<Object, EClassifier>();
	private final Map<Pair<EClassifier, EClassifier>, Integer> fRelationCache = new HashMap<Pair<EClassifier,EClassifier>, Integer>();
			
	public QVTUMLReflection(UMLReflection<EPackage, EClassifier, EOperation, 
			EStructuralFeature, EEnumLiteral, EParameter, EObject, 
			CallOperationAction, SendSignalAction, Constraint> umlReflection) {
		
		if(umlReflection == null) {
			throw new IllegalArgumentException();
		}
		
		fUmlReflection = umlReflection;
	}
	
	private QvtOperationalStdLibrary getStdLibrary() {
		return QvtOperationalStdLibrary.INSTANCE;
	}
	
	public static boolean isModelTypeInstance(EClassifier eClassifier) {
		return (eClassifier instanceof ModelType) || eClassifier == QvtOperationalStdLibrary.INSTANCE.getModelClass();
	}

	public static boolean isModuleInstance(EClassifier eClassifier) {
		return (eClassifier instanceof Module);
	}
	
	public static boolean isUserModelElement(EClassifier classifier) {
		if(classifier instanceof EClass) {
			EClass eClass = (EClass) classifier;
			
			if (IntermediateClassFactory.isIntermediateClass(eClass)) {
				return true;
			}
			
			EClass metaClass = eClass.eClass();
			EPackage stdlibPackage = QvtOperationalStdLibrary.INSTANCE.getStdLibModule();
			
			return false == (eClass.getEPackage() == stdlibPackage || 
					metaClass.getEPackage() == stdlibPackage ||
					metaClass.getEPackage() == ExpressionsPackage.eINSTANCE ||
					metaClass == ImperativeOCLPackage.eINSTANCE.getTypedef());
		}
		
		return false;
	}

	public EClassifier getCommonSuperType(EClassifier type1, EClassifier type2) {	
		EClassifier result = fUmlReflection.getCommonSuperType(type1, type2);
		if(result == null) {		
			if(type1 == getStdLibrary().getElementType() && isUserModelElement(type2)) {
				return type1;
			} else if(type2 == getStdLibrary().getElementType() && isUserModelElement(type1)) {
				return type2;
			}
		}
		return result;
	}
	
	public Collection<? extends EClassifier> getAllSupertypes(EClassifier classifier) {
		Collection<? extends EClassifier> result = fUmlReflection.getAllSupertypes(classifier);
		if(isUserModelElement(classifier)) {
			// considered to be a model element
			ArrayList<EClassifier> allSuperTupes = new ArrayList<EClassifier>(result.size() + 1);
			allSuperTupes.addAll(result);
			allSuperTupes.add(QvtOperationalStdLibrary.INSTANCE.getElementType());
		}
		return result;
	}
	
	public int getRelationship(EClassifier type1, EClassifier type2) {
		Pair<EClassifier, EClassifier> pair = new Pair<EClassifier, EClassifier>(type1, type2);
		Integer integer = fRelationCache.get(pair);
		if (integer != null) {
			return integer;
		}

		int result = fUmlReflection.getRelationship(type1, type2);
		EClassifier element = getStdLibrary().getElementType();
		if(type1 != type2) {
			if(type1 == element && isUserModelElement(type2)) {
				fRelationCache.put(pair, UMLReflection.STRICT_SUPERTYPE);
				return UMLReflection.STRICT_SUPERTYPE;
			}
			if(type2 == element && isUserModelElement(type1)) {
				fRelationCache.put(pair, UMLReflection.STRICT_SUBTYPE);
				return UMLReflection.STRICT_SUBTYPE;
			}			
		}
		
		fRelationCache.put(pair, result);
		return result; 
	}
	
	public List<EOperation> getOperations(EClassifier classifier) {
		if(classifier instanceof Module) {
			List<EOperation> operations = new ArrayList<EOperation>();
			for (EOperation nextOperation : ((Module)classifier).getEAllOperations()) {
				if(nextOperation instanceof ImperativeOperation) {
					ImperativeOperation operation = (ImperativeOperation) nextOperation;
					if(operation.getContext() != null) {
						continue;
					}
				}
				operations.add(nextOperation);
			}
			return operations;
		}
		
		List<EOperation> result = fUmlReflection.getOperations(classifier);
		if(isUserModelElement(classifier)) {
			List<EOperation> elementOpers = fUmlReflection.getOperations(QvtOperationalStdLibrary.INSTANCE.getElementType());			
			List<EOperation> tmp = result;
			result = new ArrayList<EOperation>(result.size() + elementOpers.size());
			result.addAll(tmp);
 			result.addAll(elementOpers);
		}
		return result;
	}	

	public EClassifier asOCLType(EClassifier modelType) {
		return fUmlReflection.asOCLType(modelType);
	}

	public CallOperationAction createCallOperationAction(EOperation operation) {
		return fUmlReflection.createCallOperationAction(operation);
	}

	public Constraint createConstraint() {
		return fUmlReflection.createConstraint();
	}

	public ExpressionInOCL<EClassifier, EParameter> createExpressionInOCL() {
		return fUmlReflection.createExpressionInOCL();
	}

	public EOperation createOperation(String name, EClassifier resultType, List<String> paramNames, List<EClassifier> paramTypes) {
		return fUmlReflection.createOperation(name, resultType, paramNames, paramTypes);
	}

	public EStructuralFeature createProperty(String name, EClassifier resultType) {
		return fUmlReflection.createProperty(name, resultType);
	}

	public SendSignalAction createSendSignalAction(EClassifier signal) {
		return fUmlReflection.createSendSignalAction(signal);
	}

	public EClassifier getAssociationClass(EStructuralFeature property) {
		return fUmlReflection.getAssociationClass(property);
	}

	public List<EStructuralFeature> getAttributes(EClassifier classifier) {
		List<EStructuralFeature> result = new ArrayList<EStructuralFeature>(fUmlReflection.getAttributes(classifier));
		
		// extract features of extended modules, which are no explicit supertypes (fixed by bugs 302594/310991)	
		if (classifier instanceof Module) {
    		Module module = (Module) classifier;
        	
	    	for (ModuleImport imp : module.getModuleImport()) {
				if (imp.getKind() == ImportKind.EXTENSION) {
					Module extendedModule = imp.getImportedModule();
					
					List<EStructuralFeature> attributes = getAttributes(extendedModule);
					attributes.removeAll(result);
					result.addAll(attributes);
				}
			}
		}
		
		if(classifier instanceof Module || IntermediateClassFactory.isIntermediateClass(classifier)) {
			List<EStructuralFeature> nonContextuals = new ArrayList<EStructuralFeature>(result != null ? result.size() : 5);
			for (EStructuralFeature nextFeature : result) {
				if(nextFeature instanceof ContextualProperty == false && 
					HiddenElementAdapter.isMarkedAsHidden(nextFeature) == false) {
					nonContextuals.add(nextFeature);
				}
			}
			result = nonContextuals;
		}
		return result;
	}

	public List<EClassifier> getClassifiers(EPackage pkg) {
		return fUmlReflection.getClassifiers(pkg);
	}

	public List<? extends EObject> getConstrainedElements(Constraint constraint) {
		return fUmlReflection.getConstrainedElements(constraint);
	}

	public void addConstrainedElement(Constraint constraint, EObject constrainedElement) {
		fUmlReflection.addConstrainedElement(constraint, constrainedElement);
	}
	
	public Constraint getConstraint(ExpressionInOCL<EClassifier, EParameter> specification) {
		return fUmlReflection.getConstraint(specification);
	}

	public String getConstraintName(Constraint constraint) {
		return fUmlReflection.getConstraintName(constraint);
	}

	public String getDescription(Object namedElement) {
		return fUmlReflection.getDescription(namedElement);
	}

	public EClassifier getEnumeration(EEnumLiteral enumerationLiteral) {
		return fUmlReflection.getEnumeration(enumerationLiteral);
	}

	public EEnumLiteral getEnumerationLiteral(EClassifier enumerationType, String literalName) {
		return fUmlReflection.getEnumerationLiteral(enumerationType, literalName);
	}

	public List<EEnumLiteral> getEnumerationLiterals(EClassifier enumerationType) {
		return fUmlReflection.getEnumerationLiterals(enumerationType);
	}

	public List<EStructuralFeature> getMemberEnds(EClassifier associationClass) {
		return fUmlReflection.getMemberEnds(associationClass);
	}

	public String getName(Object namedElement) {
		if(namedElement == null) {
			return "<null>"; //$NON-NLS-1$
		}
		return fUmlReflection.getName(namedElement);
	}

	public List<EPackage> getNestedPackages(EPackage pkg) {
		return fUmlReflection.getNestedPackages(pkg);
	}

	public EPackage getNestingPackage(EPackage pkg) {
		return fUmlReflection.getNestingPackage(pkg);
	}

	public EClassifier getOCLType(Object metaElement) {
		EClassifier eClassifier = fTypeCache.get(metaElement);
		if (eClassifier != null) {
			return eClassifier;
		}
		EClassifier oclType = fUmlReflection.getOCLType(metaElement);
		fTypeCache.put(metaElement, oclType);
		return oclType;
	}

	public EOperation getOperation(CallOperationAction callOperationAction) {
		return fUmlReflection.getOperation(callOperationAction);
	}

	public EClassifier getOwningClassifier(Object feature) {
		if(feature instanceof ImperativeOperation) {
			ImperativeOperation imperativeOperation = (ImperativeOperation) feature;
			VarParameter context = imperativeOperation.getContext();
			if(context != null && context.getEType() != null) {
				return context.getEType();
			}
		} else if(feature instanceof EOperation) {
			EClass containingClass = ((EOperation) feature).getEContainingClass();
			if(isTypedef(containingClass)) {
				Typedef typeDef = (Typedef) containingClass;
				return typeDef.getBase();
			}
		} else if(feature instanceof ContextualProperty) {
			ContextualProperty contextualProperty = (ContextualProperty)feature;
			return contextualProperty.getContext();
		}
		return fUmlReflection.getOwningClassifier(feature);
	}

	public EPackage getPackage(EClassifier classifier) {
		return fUmlReflection.getPackage(classifier);
	}

	public List<EParameter> getParameters(EOperation operation) {
		return fUmlReflection.getParameters(operation);
	}

	public String getQualifiedName(Object namedElement) {
		return fUmlReflection.getQualifiedName(namedElement);
	}

	public List<EStructuralFeature> getQualifiers(EStructuralFeature property) {
		return fUmlReflection.getQualifiers(property);
	}

	public EClassifier getSignal(SendSignalAction sendSignalAction) {
		return fUmlReflection.getSignal(sendSignalAction);
	}

	public List<EClassifier> getSignals(EClassifier owner) {
		return fUmlReflection.getSignals(owner);
	}

	public ExpressionInOCL<EClassifier, EParameter> getSpecification(Constraint constraint) {
		return fUmlReflection.getSpecification(constraint);
	}

	public String getStereotype(Constraint constraint) {
		return fUmlReflection.getStereotype(constraint);
	}

	public Object getStereotypeApplication(Object baseElement, EClassifier stereotype) {
		return fUmlReflection.getStereotypeApplication(baseElement, stereotype);
	}

	public boolean isAssociationClass(EClassifier type) {
		return fUmlReflection.isAssociationClass(type);
	}

	public boolean isClass(Object metaElement) {
		return fUmlReflection.isClass(metaElement);
	}

	public boolean isClassifier(Object metaElement) {
		return fUmlReflection.isClassifier(metaElement);
	}

	public boolean isComparable(EClassifier type) {
		return fUmlReflection.isComparable(type);
	}

	public boolean isDataType(Object metaElement) {
		return fUmlReflection.isDataType(metaElement);
	}

	public boolean isEnumeration(EClassifier type) {
		return fUmlReflection.isEnumeration(type);
	}

	public boolean isMany(Object metaElement) {
		return fUmlReflection.isMany(metaElement);
	}

	public boolean isOperation(Object metaElement) {
		return fUmlReflection.isOperation(metaElement);
	}

	public boolean isProperty(Object metaElement) {
		return fUmlReflection.isProperty(metaElement);
	}

	public boolean isQuery(EOperation operation) {
		return fUmlReflection.isQuery(operation);
	}

	public boolean isStatic(Object feature) {
		if(feature instanceof EOperation) {
			return QvtOperationalParserUtil.isStaticOperation((EOperation) feature);
		}
		return false;
	}

	public boolean isStereotype(EClassifier type) {
		return fUmlReflection.isStereotype(type);
	}

	public void setConstraintName(Constraint constraint, String name) {
		fUmlReflection.setConstraintName(constraint, name);
	}

	public void setName(TypedElement<EClassifier> element, String name) {
		fUmlReflection.setName(element, name);
	}

	public void setSpecification(Constraint constraint, ExpressionInOCL<EClassifier, EParameter> specification) {
		fUmlReflection.setSpecification(constraint, specification);
	}

	public void setStereotype(Constraint constraint, String stereotype) {
		fUmlReflection.setStereotype(constraint, stereotype);
	}

	public void setType(TypedElement<EClassifier> element, EClassifier type) {
		fUmlReflection.setType(element, type);
	}
	
	private static boolean isTypedef(EClassifier eClassifier) {
		if(eClassifier != null && eClassifier instanceof Typedef) {
			Typedef typeDef = (Typedef) eClassifier;
			return typeDef.getBase() != null;
		}
		
		return false;
	}

	public boolean isConstraint(Object metaElement) {
		return fUmlReflection.isConstraint(metaElement);
	}

	public boolean isPackage(Object metaElement) {
		return fUmlReflection.isPackage(metaElement);
	}

	public boolean setIsStatic(Object feature, boolean isStatic) {
		return fUmlReflection.setIsStatic(feature, isStatic);
	}
	
	public Integer getCachedRelationship(EClassifier type1, EClassifier type2) {
		return fRelationCache.get(new Pair<EClassifier, EClassifier>(type1, type2));
	}
	
	public void putCachedRelationship(EClassifier type1, EClassifier type2, int relationship) {
		fRelationCache.put(new Pair<EClassifier, EClassifier>(type1, type2), relationship);
	}
	
	public void close() {
		fRelationCache.clear();
		fTypeCache.clear();
	}
	
}
