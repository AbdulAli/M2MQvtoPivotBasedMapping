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
 *     Alex Paperno - bug 413131
 *     Christopher Gerking - bug 433919
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import static org.eclipse.ocl.utilities.UMLReflection.SAME_TYPE;
import static org.eclipse.ocl.utilities.UMLReflection.STRICT_SUBTYPE;
import static org.eclipse.ocl.utilities.UMLReflection.STRICT_SUPERTYPE;
import static org.eclipse.ocl.utilities.UMLReflection.SUBTYPE;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ValidationMessages;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.stdlib.QVTUMLReflection;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeOCLPackage;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.ocl.AbstractTypeChecker;
import org.eclipse.ocl.AmbiguousLookupException;
import org.eclipse.ocl.LookupException;
import org.eclipse.ocl.ecore.EcorePackage;
import org.eclipse.ocl.ecore.TemplateParameterType;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.AnyType;
import org.eclipse.ocl.types.CollectionType;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.types.VoidType;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;

class TypeCheckerImpl extends AbstractTypeChecker<EClassifier, EOperation, EStructuralFeature, EParameter> {
	
	/**
	 * Enumeration to indicate relationship between operation, parameters in 
	 * terms of being more|less specific.
	 */
	private static enum Relation {
		MORE,
		LESS,
		IDENTICAL,
		AMBIGUOUS,
		UNRELATED
	}
	
	private final GenericsResolver fGenericResolver;
	private final OCLStandardLibrary<EClassifier> fOCLStdlib;
	
	TypeCheckerImpl(QVTOEnvironment env) {
		super(env);
		fGenericResolver = new GenericsResolver(env);
		fOCLStdlib = getEnvironment().getOCLStandardLibrary();
	}
	
	@Override
	protected QVTOEnvironment getEnvironment() {
		return (QVTOEnvironment)super.getEnvironment();
	}
	
	public EOperation findMostSpecificOperationMatching(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args) throws LookupException {
		if (args == null) {
			args = Collections.emptyList();
		}

		UMLReflection<?, EClassifier, EOperation, EStructuralFeature, ?, EParameter, ?, ?, ?, ?> uml = getEnvironment().getUMLReflection();
		Set<EOperation> operations = new LinkedHashSet<EOperation>(getOperations(owner));
		List<EOperation> matches = null;

		for (EOperation oper : operations) {
			if (owner instanceof VoidType<?> && oper instanceof ImperativeOperation) {
				if (QvtOperationalParserUtil.getContextualType((ImperativeOperation) oper) != owner) {
					continue;
				}
			}
			if (name.equals(uml.getName(oper))
				&& matchArgs(owner, uml.getParameters(oper), args)) {

				if (matches == null) {
					// assume a small number of redefinitions
					matches = new UniqueEList<EOperation>(3);
				}

				matches.add(oper);
			}
		}

		if (matches != null) {
			if (matches.size() == 1) { 
				return matches.get(0);
			} else if (!matches.isEmpty()) {
				return getMostSpecificOperation(matches, args);
			}
		}

		// special handling for null and invalid values, whose types conform
		// to all others
		if ((owner == fOCLStdlib.getOclVoid()) || (owner == fOCLStdlib.getOclInvalid())) {
			return findOperationForVoidOrInvalid(owner, name, args);
		}

		return null;
	}			
	
	@Override
	public EStructuralFeature findAttribute(EClassifier owner, String name) {
		
		EStructuralFeature property = owner instanceof EClass ? ((EClass) owner).getEStructuralFeature(name) : null;
		if (property != null && TypeUtil.compatibleTypeMatch(getEnvironment(), owner, getUMLReflection().getOwningClassifier(property))) {
			return property;
		}
		
		property = super.findAttribute(owner, name);
		if(property == null) {
			// check for a renamed property
			EStructuralFeature aliasedProperty = getEnvironment().lookupPropertyAlias(owner, name);
			if(aliasedProperty != null) {
				String originalName = aliasedProperty.getName();
				return super.findAttribute(owner, originalName);				
			}
		}
		
		return property;
	}
	
	@Override
	public boolean isStandardLibraryFeature(EClassifier owner, Object feature) {
		return false == feature instanceof ImperativeOperation;
		
//		if(feature instanceof EOperation && isQVTOperation((EOperation) feature)) {
//			return false;
//		}
//		
//		return super.isStandardLibraryFeature(owner, feature);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected CollectionType<EClassifier, EOperation> resolveCollectionType(CollectionKind kind,
			EClassifier elementType) {
		return (CollectionType<EClassifier, EOperation>) TypeUtil.resolveCollectionType(
			getEnvironment(), kind, elementType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TupleType<EOperation, EStructuralFeature> resolveTupleType(
			EList<? extends TypedElement<EClassifier>> parts) {
		return (TupleType<EOperation, EStructuralFeature>) TypeUtil.resolveTupleType(getEnvironment(), parts);
	}
	
	@Override
	protected EClassifier resolve(EClassifier type) {		
		return TypeUtil.resolveType(getEnvironment(), type);
	}
	
	@Override
	protected EClassifier resolveGenericType(EClassifier owner, EClassifier paramType, EClassifier argType) {		
		if(paramType instanceof TemplateParameterType) {
			if(owner != null && owner.eClass() == ImperativeOCLPackage.eINSTANCE.getDictionaryType()) {
				DictionaryType dictionaryType = (DictionaryType) owner;
				if(getQVTEnvironment().getQVTStandardLibrary().getKeyT() == paramType) {
					return dictionaryType.getKeyType();
				}
			}

			return argType;
		}
		
		EClassifier result = super.resolveGenericType(owner, paramType, argType);

		// MDT OCL takes OclVoid element type as applicable for any type, which causes
		// problems to QVT mutable collection types. Consequently, List(OclVoid) should 
		// allow successful parsing of list->append('foo') 
		EClassifier listMetaType = ImperativeOCLPackage.eINSTANCE.getListType();		
		if(owner != null && owner.eClass() == listMetaType) { 
			if(fOCLStdlib.getT() == result) {
				// super implementation resolved OclVoid to oclstdlib::T				
				org.eclipse.ocl.ecore.CollectionType collectionType = (org.eclipse.ocl.ecore.CollectionType) owner;
				if(collectionType.getElementType() == fOCLStdlib.getOclVoid()) {				
					return fOCLStdlib.getOclVoid(); 
				}
			}
		}
		
		return result;
	}

	@Override
	public EClassifier getResultType(Object problemObject,
			EClassifier owner, EOperation operation,
			List<? extends TypedElement<EClassifier>> args) {

		if(isQVTOperation(operation)) { 
			return fGenericResolver.resolveOperationReturnType(owner, operation, args);				
		}
		
		return super.getResultType(problemObject, owner, operation, args);
	}
		
	@Override
	public int getRelationship(EClassifier type1, EClassifier type2) {
		QVTUMLReflection qvtUml = (QVTUMLReflection) getUMLReflection();
		
		Integer integer = qvtUml.getCachedRelationship(type1, type2);
		if (integer != null) {
			return integer;
		}
		
		int result = getRelationshipImpl(type1, type2);
		qvtUml.putCachedRelationship(type1, type2, result);
		
		return result;
	}
	
	private int getRelationshipImpl(EClassifier type1, EClassifier type2) {
		// check for identity before any further analysis
		if(type1 == type2) {
			return UMLReflection.SAME_TYPE;
		}
		if (type1 == null) {
			return UMLReflection.UNRELATED_TYPE;
		}

		if (type1 instanceof AnyType) {
			return type2 instanceof AnyType
				? SAME_TYPE : STRICT_SUPERTYPE;
		} else if (type2 instanceof AnyType) {
			return STRICT_SUBTYPE;
		}
		
		boolean isList1 = type1 instanceof ListType;
		boolean isList2 = type2 instanceof ListType;
		
		if(isList1 || isList2) {
			// handle list types
			if(isList1 && isList2) {
				ListType list1 = (ListType) type1;
				ListType list2 = (ListType) type2;
				return getRelationship(list1.getElementType(), list2.getElementType());
			} else if(!isList1 && type1 instanceof CollectionType<?, ?>) {
				@SuppressWarnings("unchecked")
				CollectionType<EClassifier, EOperation> col1 = (CollectionType<EClassifier, EOperation>) type1;

				if(col1.eClass() == EcorePackage.eINSTANCE.getCollectionType() &&
					TypeUtil.compatibleTypeMatch(getEnvironment(), 
						((ListType)type2).getElementType(), col1.getElementType())) {
					return UMLReflection.STRICT_SUPERTYPE;
				}				

				if(col1.eClass() == EcorePackage.eINSTANCE.getSequenceType() &&
						TypeUtil.compatibleTypeMatch(getEnvironment(), 
							((ListType)type2).getElementType(), col1.getElementType())) {
					return UMLReflection.SUPERTYPE;
				}				
			} else if(!isList2 && type2 instanceof CollectionType<?, ?>) {
				@SuppressWarnings("unchecked")
				CollectionType<EClassifier, EOperation> col2 = (CollectionType<EClassifier, EOperation>) type2;

				if(col2.eClass() == EcorePackage.eINSTANCE.getCollectionType() &&
					TypeUtil.compatibleTypeMatch(getEnvironment(), 
						((ListType)type1).getElementType(), col2.getElementType())) {
					return UMLReflection.STRICT_SUBTYPE;
				}				

				if(col2.eClass() == EcorePackage.eINSTANCE.getSequenceType() &&
						TypeUtil.compatibleTypeMatch(getEnvironment(), 
							((ListType)type1).getElementType(), col2.getElementType())) {
					return UMLReflection.SUBTYPE;
				}				
			}
			
			if(isVoidOrInvalid(type1) || isVoidOrInvalid(type2)) {
				return super.getRelationship(type1, type2);
			}
			
			return UMLReflection.UNRELATED_TYPE;
		}

		boolean isDict1 = type1 instanceof DictionaryType;
		boolean isDict2 = type2 instanceof DictionaryType;
		if(isDict1 || isDict2) {		
			if(isDict1 && isDict2) {
				DictionaryType dict1 = (DictionaryType) type1;
				DictionaryType dict2 = (DictionaryType) type2;			
				int elementRelShip = getRelationship(dict1.getElementType(), dict2.getElementType());
				int keyRelShip = getRelationship(dict1.getKeyType(), dict2.getKeyType());
				
				if(elementRelShip != keyRelShip) {
					if((keyRelShip & UMLReflection.SUBTYPE) != 0 && 
						(elementRelShip & UMLReflection.SUBTYPE) != 0) {						
						return UMLReflection.STRICT_SUBTYPE;
					} 
					else if((keyRelShip & UMLReflection.SUPERTYPE) != 0 && 
							(elementRelShip & UMLReflection.SUPERTYPE) != 0) {
						return UMLReflection.STRICT_SUPERTYPE;						
					}
					
					return elementRelShip;
				} 
					
				return elementRelShip;
				
			} else if(!isDict1) {
				if(type1.eClass() == EcorePackage.eINSTANCE.getCollectionType()) {
					@SuppressWarnings("unchecked")
					CollectionType<EClassifier, EOperation> col1 = (CollectionType<EClassifier, EOperation>) type1;
					if(TypeUtil.compatibleTypeMatch(getEnvironment(), ((DictionaryType)type2).getElementType(), col1.getElementType())) {
						return UMLReflection.STRICT_SUPERTYPE;
					}
				} 
				
				if(isVoidOrInvalid(type1)) {
					return super.getRelationship(type1, type2);
				}			
				// Note: Object can be super the super type not, which is not supported yet
				return UMLReflection.UNRELATED_TYPE;
				
			} else if(!isDict2) {
				if(type2.eClass() == EcorePackage.eINSTANCE.getCollectionType()) {
					@SuppressWarnings("unchecked")
					CollectionType<EClassifier, EOperation> col2 = (CollectionType<EClassifier, EOperation>) type1;
					if(TypeUtil.compatibleTypeMatch(getEnvironment(), ((DictionaryType)type1).getElementType(), col2.getElementType())) {
						return UMLReflection.STRICT_SUBTYPE;
					}
				}
				
				if(isVoidOrInvalid(type2)) {
					return super.getRelationship(type1, type2);
				}				
				// Note: Object can be super the super type not, which is not supported yet
				return UMLReflection.UNRELATED_TYPE;				
			}			
		}		
		
		
		boolean isTuple1 = type1 instanceof TupleType;
		boolean isTuple2 = type2 instanceof TupleType;
		if (isTuple1 && isTuple2) {
			int currentRelation = 0;
			int matchingFeaturesCount = 0;
			@SuppressWarnings("unchecked")
			EList<EStructuralFeature> features1 = ((TupleType<EOperation, EStructuralFeature>)type1).oclProperties();
			@SuppressWarnings("unchecked")
			EList<EStructuralFeature> features2 = ((TupleType<EOperation, EStructuralFeature>)type2).oclProperties();
			for (EStructuralFeature feature1 : features1) {
				EStructuralFeature feature2 = findAttribute(type2, feature1.getName());
				if (feature2 == null) {
					return UMLReflection.UNRELATED_TYPE;
				}
				
				int partRelShip = getRelationship(feature1.getEType(), feature2.getEType());
				
				if (partRelShip == UMLReflection.SAME_TYPE) {
					// Tuple relation type not affected
				}
				else if (partRelShip == UMLReflection.RELATED_TYPE) {
					currentRelation = UMLReflection.RELATED_TYPE;
				}
				else if ((partRelShip & UMLReflection.STRICT_SUBTYPE) != 0) {
					if ((currentRelation & UMLReflection.STRICT_SUPERTYPE) != 0) {
						currentRelation = UMLReflection.RELATED_TYPE;
					}
					else {
						currentRelation |= partRelShip;
					}
				}
				else if ((partRelShip & UMLReflection.STRICT_SUPERTYPE) != 0) {
					if ((currentRelation & UMLReflection.STRICT_SUBTYPE) != 0) {
						currentRelation = UMLReflection.RELATED_TYPE;
					}
					else {
						currentRelation |= partRelShip;
					}
				}
				else { // partRelShip == UMLReflection.UNRELATED_TYPE
					return UMLReflection.UNRELATED_TYPE;
				}
				
				matchingFeaturesCount ++;
			}
			
			if (matchingFeaturesCount < features1.size() || features1.size() != features2.size()) {
				return UMLReflection.UNRELATED_TYPE;
			}

			return currentRelation == 0 ? UMLReflection.SAME_TYPE : currentRelation;
		}	
		
		return super.getRelationship(type1, type2);
	}
	
	public boolean compatibleTypeMatch(EClassifier type1, EClassifier type2) {
		switch (getRelationship(type1, type2)) {
		case SAME_TYPE:
		case STRICT_SUBTYPE:
		case SUBTYPE:
			return true;
		default:
			return false;
		}
	}
	
	EOperation getMostSpecificOperation(List<EOperation> matchingOpers, List<? extends TypedElement<EClassifier>> args) throws LookupException {
		if(matchingOpers == null || matchingOpers.isEmpty()) {
			return null; 
		}
		
		List<EOperation> ambiguous = null;		
		EOperation mostSpecific = null;
		int i = 0;
		
		for (EOperation nextOper : matchingOpers) {							
			if(i++ == 0) {
				mostSpecific = nextOper;
				continue;
			}

			Relation nextOperRelation = getMoreSpecificByParameters(mostSpecific, nextOper, args);
			if(nextOperRelation == null) {
				// no way how to figure due to possible parsing problems
				continue;
			}
			else if(nextOperRelation == Relation.IDENTICAL) {
				// try to select the most specific owner type
				Relation ownerRelation = getMoreSpecificType(
					getUMLReflection().getOwningClassifier(mostSpecific),
					getUMLReflection().getOwningClassifier(nextOper));
				
				if(Relation.IDENTICAL == ownerRelation) {
					EOperation overriding = selectOverridingOperation(mostSpecific, nextOper);
					if(overriding != null) {
						// the overriding operation is definitely more specific then the overridden
						mostSpecific = overriding;
					} else {
						Module resolvingModule = getEnvironment().getModuleContextType();
						if(resolvingModule != null) {
							Module owningModule = QvtOperationalParserUtil.getOwningModule(mostSpecific);
							Module nextOwningModule = QvtOperationalParserUtil.getOwningModule(nextOper);
							
							if(owningModule == null || nextOwningModule == null) {
								// operation defined in meta-model or OCL standard library
								// attempt to select the more specific on from the remaining operations
								//continue;
								if(owningModule != null) {
									mostSpecific = nextOper;
								}
								// set as ambiguous to be processed bellow
								nextOperRelation = Relation.AMBIGUOUS;
								
							} else if(owningModule != resolvingModule && nextOwningModule != resolvingModule) {
								// both coming from imported modules => no rule for choosing one
								// set as ambiguous to be processed bellow
								nextOperRelation = Relation.AMBIGUOUS;
							} else if(nextOwningModule == resolvingModule) {
								// select the operation from the resolving module									
								// Note: we know here that the other module is the imported one
								mostSpecific = nextOper;
							}
						}
					}
				} else if(Relation.LESS == ownerRelation) {	
					mostSpecific = nextOper;
				} else	if(ownerRelation == null) {
					continue;
				} 
				// Note: if the current is more specific, we keep it as the last specific found
				
			} else if(nextOperRelation == Relation.LESS) {
				// the last found is less specific, promote the next found as more specific
				mostSpecific = nextOper;
			} else if(nextOperRelation == Relation.MORE) {
				// keep the last as more specific
			} 
			
			if(nextOperRelation == Relation.AMBIGUOUS) {
				if(ambiguous == null) {
					ambiguous = new UniqueEList<EOperation>();
				}
				ambiguous.add(mostSpecific);
				ambiguous.add(nextOper);
			} 
		}


		if(ambiguous != null) {
			throw new AmbiguousLookupException(ValidationMessages.AmbiguousOperationLookup, ambiguous);
		}

		return (mostSpecific != null) ? mostSpecific : matchingOpers.get(0);
	}
	
	private static EOperation selectOverridingOperation(EOperation o1, EOperation o2) {
		if(o1 instanceof ImperativeOperation) {
			ImperativeOperation imp1 = (ImperativeOperation) o1;
			if(o2 != null && imp1.getOverridden() == o2) {
				return o1;
			}
		}
		
		if(o2 instanceof ImperativeOperation) {
			ImperativeOperation imp2 = (ImperativeOperation) o2;
			if(o1 != null && imp2.getOverridden() == o1) {
				return o2;
			}
		}
		return null;
	}
	
	/*
	 * Determines which operation is the more specific, taking into account the actual arguments
	 * passed to the operation call 
	 */
	private Relation getMoreSpecificByParameters(EOperation o1, EOperation o2, List<? extends TypedElement<EClassifier>> args) {
		if(o1 == o2) {
			return Relation.IDENTICAL;
		}
		
		EList<EParameter> params1 = o1.getEParameters();
		EList<EParameter> params2 = o2.getEParameters();
		assert params1.size() == params2.size();

		// handle operations with no parameters first  
		if(params1.isEmpty()) {
			return Relation.IDENTICAL;
		}

		Relation resultParamRel = null;		
		int i = 0;
		
		for (EParameter nextParam : params1) {
			EParameter nextParam2 = params2.get(i);
			
			Relation nextParamRel = getMoreSpecificType(nextParam.getEType(), nextParam2.getEType());			
			if(nextParamRel == null) {
				// unable to figure out, most likely based on parsing errors
				// => propagate the undefined status
				return null;
			} 
			else if(nextParamRel == Relation.UNRELATED) {
				TypedElement<EClassifier> actualArg = args.get(i);
				if(fOCLStdlib.getOclVoid() == actualArg.getType() || 
						fOCLStdlib.getOclInvalid() == actualArg.getType()) {
					// unrelated parameter types but both matching the given argument (void, invalid)
					// => we can't make a more specific decision
					return Relation.AMBIGUOUS;
				} else {
					// possible parsing errors
					return null;
				}
			}


			if(i++ == 0) {
				// first iteration to setup the initial result 
				resultParamRel = nextParamRel;				
				continue;
			}
									
			// once the previous parameter check marked an operation as the more-(less) specific, 
			// the next parameter must be at least identical or more-(less) specific again
			// otherwise we can't unambiguously select the more specific one
			if(resultParamRel != nextParamRel) { 
				if(resultParamRel != Relation.IDENTICAL && nextParamRel != Relation.IDENTICAL) {
					// neither the last found nor the next parameter is identical, 
					// IOW has changed in terms of MORE|LESS => breaking the previous parameter relation
					return Relation.AMBIGUOUS;
				}				
			}
		}

		return resultParamRel;
	}
	
	/*
	 * Determines the relation between the given types
	 */
	private Relation getMoreSpecificType(EClassifier t1, EClassifier t2) {
		if(t1 == null || t2 == null) {
			// Note: be tolerant to possible parsing errors, may be unresolved (null)
			return null;
		}

		int relationship = getRelationship(t1, t2);
		switch (relationship) {
		case UMLReflection.STRICT_SUBTYPE:
			return Relation.MORE;
		case UMLReflection.STRICT_SUPERTYPE:
			return Relation.LESS;
		case UMLReflection.SAME_TYPE:
			return Relation.IDENTICAL;
		}
		
		return Relation.UNRELATED;
	}

	private EOperation findOperationForVoidOrInvalid(EClassifier owner, String name,
			List<? extends TypedElement<EClassifier>> args) throws LookupException {

		EOperation result = null;

		if (args.size() == 1) {
			EClassifier argType = args.get(0).getType();

			if (argType != owner && argType != null) {
				// let us search the type of the argument to determine whether
				// we can find this operation
				result = findMostSpecificOperationMatching(argType, name, args);
			}
		}

		return result;
	}	
		
	private boolean isQVTOperation(EOperation operation) {
		return QvtOperationalParserUtil.getOwningModule(operation) != null;
	}
	
	private boolean isVoidOrInvalid(EClassifier type1) {
		return type1 == fOCLStdlib.getOclVoid() || type1 == fOCLStdlib.getOclInvalid();
	}
	
	private QVTOEnvironment getQVTEnvironment() {
		return (QVTOEnvironment) getEnvironment();
	}
}