/*******************************************************************************
 * Copyright (c) 2007, 2015 Borland Software Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bugs 302594, 310991, 397959, 425069, 475123
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import static org.eclipse.ocl.utilities.UMLReflection.SAME_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.compiler.BlackboxUnitResolver;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImportKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.Library;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModuleImport;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.m2m.internal.qvt.oml.stdlib.QVTUMLReflection;
import org.eclipse.ocl.LookupException;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.lpg.ProblemHandler;
import org.eclipse.ocl.lpg.ProblemHandler.Severity;
import org.eclipse.ocl.options.ParsingOptions;
import org.eclipse.ocl.types.VoidType;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;

import lpg.runtime.ParseErrorCodes;


/**
 * @since 2.0
 */
public abstract class QvtEnvironmentBase extends EcoreEnvironment implements QVTOEnvironment {
	
	public static class CollisionStatus {
		public static final int ALREADY_DEFINED = 1;		
		public static final int VIRTUAL_METHOD_RETURNTYPE = 2;
		public static final int OVERRIDES = 3;		
		
		private int fKind;
		private EOperation fOperation;		
		
		CollisionStatus(EOperation operation, int kind) {
			if(kind != ALREADY_DEFINED && kind != VIRTUAL_METHOD_RETURNTYPE &&
				kind != OVERRIDES) {
				throw new IllegalArgumentException("illegal collision kind"); //$NON-NLS-1$
			}
			
			fKind = kind;
			fOperation = operation;			
		}
		
		public int getCollisionKind() {
			return fKind;
		}
		
		public EOperation getOperation() {
			return fOperation;
		}
	}	
	
	/**
	 * Special prefix for generated identifiers
	 */
	public static final String GENERATED_NAME_SPECIAL_PREFIX = "$"; //$NON-NLS-1$
	
    private static final String TEMPORARY_NAME_GENERATOR_UNIQUE_PREFIX = GENERATED_NAME_SPECIAL_PREFIX + "temp_"; //$NON-NLS-1$
    private int myTemporaryNameGeneratorInt = 0;
    /*
     * List of declared variables and implicit variables, including "self".
     * Implicit variables are generated when there is an iterator without any
     * iteration variable specified.
     */
    private List<org.eclipse.ocl.ecore.Variable> myImplicitVars = new LinkedList<org.eclipse.ocl.ecore.Variable>();

    private QVTUMLReflection fQVUMLReflection;
	private List<QvtEnvironmentBase> fByAccess;
	private List<QvtEnvironmentBase> fByExtension;
	private List<QvtEnvironmentBase> fAllExtendedModuleEnvs;
	private Map<URI, Set<String>> fImportedNativeLibs;

	protected QvtEnvironmentBase(QvtEnvironmentBase parent) {
		super(parent);
		setOption(ParsingOptions.USE_BACKSLASH_ESCAPE_PROCESSING, true);
	}

	@SuppressWarnings("deprecation")
	protected QvtEnvironmentBase(EPackage.Registry reg, Resource resource) {
		super(reg, resource);
		setOption(ParsingOptions.USE_BACKSLASH_ESCAPE_PROCESSING, true);
	}

	@SuppressWarnings("deprecation")
	protected QvtEnvironmentBase(EPackage.Registry reg) {
		super(reg);
		setOption(ParsingOptions.USE_BACKSLASH_ESCAPE_PROCESSING, true);
	}
	
	public abstract Module getModuleContextType();	
	
	public void addImplicitVariableForProperties(String name, Variable<EClassifier, EParameter> elem) {
        getUMLReflection().setName(elem, name);
	    addedVariable(name, elem, false);
	}
	
    @Override
    protected void addedVariable(String name, Variable<EClassifier, EParameter> elem, boolean isExplicit) {
    	org.eclipse.ocl.ecore.Variable elemVar = (org.eclipse.ocl.ecore.Variable) elem;    	
        if(!isExplicit) {
            myImplicitVars.add(elemVar);
        }
        
        if(elemVar instanceof VarParameter == false && elemVar.eContainer() == null) {
            if(getContextOperation() instanceof ImperativeOperation) {
                ImperativeOperation imperativeOperation = (ImperativeOperation) getContextOperation();
                if (imperativeOperation.getBody() != null) {
                    imperativeOperation.getBody().getVariable().add((org.eclipse.ocl.ecore.Variable)elemVar);
                } else {
                    super.addedVariable(name, elemVar, isExplicit);
                }
            } else {
                super.addedVariable(name, elemVar, isExplicit);
            }
        } 
    }
    
    @Override
    protected void removedVariable(String name, Variable<EClassifier, EParameter> variable, boolean isExplicit) {
        if(!isExplicit) {
            myImplicitVars.remove(variable);
        }

        super.removedVariable(name, variable, isExplicit);
    }
    
    public Collection<org.eclipse.ocl.ecore.Variable> getImplicitVariables() {
        return Collections.unmodifiableCollection(myImplicitVars);
    }
    
    protected Variable<EClassifier, EParameter> localLookupImplicitSourceForOperation(String name, List<? extends TypedElement<EClassifier>> args) {
    	return super.lookupImplicitSourceForOperation(name, args);
    }
        
	@Override
	public Variable<EClassifier, EParameter> lookupImplicitSourceForOperation(String name, List<? extends TypedElement<EClassifier>> args) {
    	try {
    		return tryLookupImplicitSourceForOperation(name, args);
    	} catch(LookupException e) {
    		List<?> ambiguousMatches = e.getAmbiguousMatches();
    		@SuppressWarnings("unchecked")
			Variable<EClassifier, EParameter> result = ambiguousMatches.isEmpty() ? null : (Variable<EClassifier, EParameter>)ambiguousMatches.get(0);
			return result;
    	}
	}
    
	public Variable<EClassifier, EParameter> tryLookupImplicitSourceForOperation(String name, List<? extends TypedElement<EClassifier>> args) throws LookupException {
		Variable<EClassifier, EParameter> result = super.lookupImplicitSourceForOperation(name, args);
		
		if(result == null) {
			QvtEnvironmentBase rootEnv = getRootEnv();
			if(rootEnv != this) {
				// TODO - why not to call the root directly, do we want others to override?
				return this.getInternalParent().lookupImplicitSourceForOperation(name, args);
			}
			
			for (QvtEnvironmentBase nextExtendedEnv : rootEnv.getAllExtendedModules()) {
				result = nextExtendedEnv.localLookupImplicitSourceForOperation(name, args);
				if(result != null) {
					return result;
				}
			}
			
			for (QvtEnvironmentBase nextAccessedEnv : rootEnv.getImportsByAccess()) {
				Module importedModule = nextAccessedEnv.getModuleContextType();
				if(importedModule instanceof Library) {
					// there is a single default instance for libraries 
					// this cannot be done transformations which has an explicit instance					
					result = nextAccessedEnv.localLookupImplicitSourceForOperation(name, args);
					if(result != null) {
						break;
					}
				}
			}			
		}
		
		return result;
	}
	
    // implements the interface method
    private Variable<EClassifier, EParameter> lookupImplicitSourceForPropertyInternal(String name) {
        Variable<EClassifier, EParameter> vdcl;
        
        for (int i = myImplicitVars.size() - 1; i >= 0; i--) {
            vdcl = myImplicitVars.get(i);
            EClassifier owner = vdcl.getType();
            
            if (owner != null) {
                EStructuralFeature property = safeTryLookupPropertyInternal(owner, name);          
                if (property != null) {
                	// in case of extended modules, module properties are distributed across multiple owners without explicit supertyping
					// => accept only if property is actually available on the owner, i.e. if the actual property owner is an explicit supertype of the owner
                	EClassifier actualPropertyOwner = getUMLReflection().getOwningClassifier(property);
                	if (TypeUtil.compatibleTypeMatch(this, owner, actualPropertyOwner)) {
                		return vdcl;
                	}
                }
            }

        }
        
        // try the "self" variable, last
        vdcl = getSelfVariable();
        if (vdcl != null) {
            EClassifier owner = vdcl.getType();
            if (owner != null) {
                EStructuralFeature property = safeTryLookupPropertyInternal(owner, name);
                if (property != null) {
                    return vdcl;
                }
            }
        }
        
        return null;

    }
    
    /**
     * Wrapper for the "try" operation that doesn't throw, but just returns the
     * first ambiguous match in case of ambiguity.
     */
    private EStructuralFeature safeTryLookupPropertyInternal(EClassifier owner, String name) {
        EStructuralFeature result = null;
        
        try {
            result = tryLookupProperty(owner, name);
        } catch (LookupException e) {
            if (!e.getAmbiguousMatches().isEmpty()) {
                result = (EStructuralFeature) e.getAmbiguousMatches().get(0);
            }
        }
        
        return result;
    }
    
	@Override
	public Variable<EClassifier, EParameter> lookupImplicitSourceForProperty(String name) {
		Variable<EClassifier, EParameter> result = lookupImplicitSourceForPropertyInternal(name);
		if(result == null) {
			QvtEnvironmentBase rootEnv = getRootEnv();
			if(rootEnv != this) {
				return this.getInternalParent().lookupImplicitSourceForProperty(name);
			}
			
			for (QvtEnvironmentBase nextSiblingEnv : rootEnv.getImportsByExtends()) {
				result = nextSiblingEnv.lookupImplicitSourceForProperty(name);
				if(result != null) {
					return result;
				}
			}
			
			for (QvtEnvironmentBase nextSiblingEnv : rootEnv.getImportsByAccess()) {
				Module importedModule = nextSiblingEnv.getModuleContextType();
				if(importedModule instanceof Library) {
					// there is a single default instance for libraries 
					// this cannot be done transformations which has an explicit instance
					result = nextSiblingEnv.lookupImplicitSourceForProperty(name);
					if(result != null) {
						break;
					}
				}
			}			
		}
		return result;
		
	}

	// FIXME - refactore this out
	final QvtTypeResolverImpl getQVTTypeResolver() {
		return (QvtTypeResolverImpl)getTypeResolver();
	}
	
	@Override
	public QVTOTypeResolver getTypeResolver() {
		return (QVTOTypeResolver)super.getTypeResolver();
	}
	
	public QVTOStandardLibrary getQVTStandardLibrary() {
		return QvtOperationalStdLibrary.INSTANCE;
	}
		
	@Override
	public List<EOperation> getAdditionalOperations(EClassifier classifier) {
		
		if (classifier instanceof VoidType<?>) {
			List<EOperation> result = new ArrayList<EOperation>();

			getAllContextualOperations(result, this);
			
			for (QvtEnvironmentBase nextImportedEnv : getImportsByExtends()) {
				getAllContextualOperations(result, nextImportedEnv);
			}
			return result;
		}
		
		if(classifier instanceof org.eclipse.ocl.ecore.CollectionType) {
			org.eclipse.ocl.ecore.CollectionType collectionType = (org.eclipse.ocl.ecore.CollectionType) classifier;			
			List<EOperation> result = new ArrayList<EOperation>();
			getLocalAdditionalCollectionOperations(collectionType, result);
			
			// look for imported collection operations
			for (QvtEnvironmentBase nextImportedEnv : getImportsByExtends()) {
				nextImportedEnv.getLocalAdditionalCollectionOperations(collectionType, result);
			}
			
			for (QvtEnvironmentBase nextImportedEnv : getImportsByAccess()) {
				nextImportedEnv.getLocalAdditionalCollectionOperations(collectionType, result);
			}			
			
			return result;
		}
		
		return super.getAdditionalOperations(classifier);
	}

	private void getAllContextualOperations(List<EOperation> result, QvtEnvironmentBase env) {
		if (env.getModuleContextType() == null) {
			return;
		}
		for (EOperation operation : env.getModuleContextType().getEOperations()) {
			if (operation instanceof ImperativeOperation) {
				ImperativeOperation imperative = (ImperativeOperation) operation;
				if (QvtOperationalParserUtil.isContextual(imperative)) {
					result.add(imperative);
				}
			}
		}
	}
	
	private void getLocalAdditionalCollectionOperations(org.eclipse.ocl.ecore.CollectionType collectionType, List<EOperation> result) {
//		OCLStandardLibrary<EClassifier> oclstdlib = getOCLStandardLibrary();
//		
//		EcorePackage typePackage = EcorePackage.eINSTANCE;
//
//		EClass metaType = collectionType.eClass();
//		EClassifier genericBaseType = null;
//		
//		if(metaType == typePackage.getCollectionType() && collectionType != oclstdlib.getCollection()) {
//			genericBaseType = oclstdlib.getCollection();
//		} else if(metaType == typePackage.getBagType() && collectionType != oclstdlib.getBag()) {
//			genericBaseType = oclstdlib.getBag();
//		} else if(metaType == typePackage.getSequenceType() && collectionType != oclstdlib.getSequence()) {
//			genericBaseType = oclstdlib.getSequence();
//		} else if(metaType == typePackage.getSetType() && collectionType != oclstdlib.getSet()) {
//			genericBaseType = oclstdlib.getSet();
//		} else if(metaType == typePackage.getOrderedSetType() && collectionType != oclstdlib.getOrderedSet()) {
//			genericBaseType = oclstdlib.getOrderedSet();
//		} else if(metaType == ImperativeOCLPackage.eINSTANCE.getListType() && collectionType != getQVTStandardLibrary().getList()) {
//			genericBaseType = getQVTStandardLibrary().getList();
//		} else if(metaType == ImperativeOCLPackage.eINSTANCE.getDictionaryType() && collectionType != getQVTStandardLibrary().getDictionary()) {
//			genericBaseType = getQVTStandardLibrary().getDictionary();
//		}
		
		QvtTypeResolverImpl thisResolver = getQVTTypeResolver();
//		if(genericBaseType != null) {
//			thisResolver.getLocalCollectionAdditionalOperations((CollectionType)genericBaseType, result, false);
//		}

		thisResolver.getLocalCollectionAdditionalOperations(collectionType, result, true);
		
//		Collection<EClassifier> allParents = OCLStandardLibraryUtil.getAllSupertypes(this, collectionType);
//		for (EClassifier general : allParents) {
//			org.eclipse.ocl.ecore.CollectionType generalCollection = (org.eclipse.ocl.ecore.CollectionType) general; 
//			thisResolver.getLocalCollectionAdditionalOperations(generalCollection, result, false);
//		}
//		
//		if(metaType == ImperativeOCLPackage.eINSTANCE.getListType()) {
//			// process the CollectionType super type
//			// TODO - better to have MDT OCL to support #getAllSupertypes(...) operation in TypeChecker
//			thisResolver.getLocalCollectionAdditionalOperations((CollectionType)oclstdlib.getCollection(), result, false);			
//		}
	}
	
	@Override
	public UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> getUMLReflection() {
		Internal<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> parent = getInternalParent();
		if(parent != null) {
			return parent.getUMLReflection();
		}
		
		if(fQVUMLReflection == null) {
			fQVUMLReflection = new QVTUMLReflection(super.getUMLReflection());
		}
		
		return fQVUMLReflection;
	}
	
	public void close() {
        if (getInternalParent() == null) {
        	if (fQVUMLReflection != null) {
        		fQVUMLReflection.close();
        	}
        	if (this != QvtOperationalStdLibrary.INSTANCE.getEnvironment()) {
        		QvtOperationalStdLibrary.INSTANCE.getEnvironment().close();
        	}
        }
	}
	
	public final void addImport(ImportKind kind, QvtEnvironmentBase importedEnv) {
		QvtEnvironmentBase rootEnv = getRootEnv();
		if(rootEnv != this) {
			// propagate to the top level parent
			rootEnv.addImport(kind, importedEnv);
			return;
		}
		
		if(importedEnv == null || importedEnv == this || isOneOfParents(importedEnv)) {
			throw new IllegalArgumentException("Illegal import environment: " + String.valueOf(importedEnv)); //$NON-NLS-1$
		}
		
		List<QvtEnvironmentBase> container;
		if(kind == ImportKind.ACCESS) {
			if(fByAccess == null) {
				fByAccess = new UniqueEList<QvtEnvironmentBase>();
			}
			container = fByAccess;
		} else {
			if(fByExtension == null) {
				fByExtension = new UniqueEList<QvtEnvironmentBase>();
			}
			container = fByExtension;
			fAllExtendedModuleEnvs = null;			
		}
		fImportedNativeLibs = null;

		assert container != null;
		container.add(importedEnv);
		// reset cached all extended modules
	}
	
	public final List<QvtEnvironmentBase> getImportsByAccess() {
		QvtEnvironmentBase rootEnv = getRootEnv();
		if(rootEnv != this) {
			return rootEnv.getImportsByAccess();
		}
		
		return fByAccess != null ? fByAccess : Collections.<QvtEnvironmentBase>emptyList();		
	}
	
	public List<QvtEnvironmentBase> getAllExtendedModules() {
		QvtEnvironmentBase rootEnv = getRootEnv();
		if(rootEnv != this) {
			return rootEnv.getAllExtendedModules();
		}
		
		if(fAllExtendedModuleEnvs == null) {
			LinkedHashSet<QvtEnvironmentBase> result = new LinkedHashSet<QvtEnvironmentBase>();			
			List<QvtEnvironmentBase> importsByExtends = getImportsByExtends();
			for (QvtEnvironmentBase nextImportedEnv : importsByExtends) {
				result.add(nextImportedEnv);
				result.addAll(nextImportedEnv.getAllExtendedModules());
			} 
			// safety check for the case somebody in the hierarchy tries to extend us 
			result.remove(this);
			
			fAllExtendedModuleEnvs = Collections.unmodifiableList(new ArrayList<QvtEnvironmentBase>(result));			
		}
		
		return fAllExtendedModuleEnvs;
	}
	
	public final List<QvtEnvironmentBase> getImportsByExtends() {
		QvtEnvironmentBase rootEnv = getRootEnv();
		if(rootEnv != this) {
			return rootEnv.getImportsByExtends();
		}
		
		return fByExtension != null ? fByExtension : Collections.<QvtEnvironmentBase>emptyList();	
	}
	
	public Map<URI, Set<String>> getImportedNativeLibs() {
		QvtEnvironmentBase rootEnv = getRootEnv();
		if(rootEnv != this) {
			return rootEnv.getImportedNativeLibs();
		}
		
		if(fImportedNativeLibs == null) {
	    	Collection<QvtEnvironmentBase> imports = new LinkedHashSet<QvtEnvironmentBase>();
	    	imports.addAll(getImportsByExtends());
	    	imports.addAll(getImportsByAccess());    	
	    	
	    	Map<URI, Set<String>> result = new LinkedHashMap<URI, Set<String>>(imports.size());
			for (QvtEnvironmentBase sibling : imports) {
    			Module module = sibling.getModuleContextType();
    			if (module == null || module.eResource() == null) {
    				continue;
    			}
    			URI uri = module.eResource().getURI();
    			if (!BlackboxUnitResolver.isBlackboxUnitURI(uri)) {
    				continue;
    			}
    			
    			Set<String> names = result.get(uri);
    			if (names == null) {
    				names = new LinkedHashSet<String>();
    				result.put(uri, names);
    			}
    			names.add(module.getName());
			}    	    	
			
			fImportedNativeLibs = Collections.unmodifiableMap(result);			
		}
		
		return fImportedNativeLibs;
	}
		
	protected final CollisionStatus findCollidingOperation(EClassifier ownerType, ImperativeOperation operation) {
		return doFindCollidingOperation(ownerType, operation);
	}
	
	private EOperation findMatchingFromExtended(Module extending, EOperation operation) {
		Library stdLibModule = getQVTStandardLibrary().getStdLibModule();
		
		EOperation result = null;
		for (ModuleImport nextImport : extending.getModuleImport()) {
			Module nextImportedModule = nextImport.getImportedModule();
			if(nextImportedModule == stdLibModule) {
				// no imperative operation declaration in stdlib
				continue;
			}
			
			EList<EOperation> importedOpers = nextImportedModule.getEOperations();			
			String name = operation.getName();
			if(name != null) {
				for (EOperation nextImportedOper : importedOpers) {				
					if(name.equals(nextImportedOper.getName()) && 
						matchParameters(nextImportedOper, operation)) {
						
						// accept imported operation only if it has the same context type (fixed by bug 397959)
						if (nextImportedOper instanceof ImperativeOperation &&
							operation instanceof ImperativeOperation &&
							!matchContext((ImperativeOperation) nextImportedOper, (ImperativeOperation) operation)) {
								continue;
						}
					
						return nextImportedOper;
					}
				}
			}
			
			result = findMatchingFromExtended(nextImportedModule, operation);
			if(result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	private CollisionStatus doFindCollidingOperation(EClassifier ownerType, ImperativeOperation operation) {
		CollisionStatus result = null;
		EClassifier definingModule = getModuleContextType();
        String operationName = getUMLReflection().getName(operation);
        Set<EOperation> operations = new LinkedHashSet<EOperation>(TypeUtil.getOperations(this, ownerType));
        
        boolean isContextual = !(ownerType == definingModule);
        if(!isContextual) {
    		EOperation overridden = findMatchingFromExtended(getModuleContextType(), operation);
    		if(overridden != null) {
    			operations.add(overridden);
    		}
        } else {        
	        // collect additional operations defined for sub-types of the checked owner type,
	        // Note: those from super-types are included by MDT OCL TypeUtil.getOperations(...);
	        // => union forms the whole scope for potentially virtually called operation;
	        // all fAdditionalTypes ever defined goes through this check, so all applicable get into VTABLEs
	        getQVTTypeResolver().collectAdditionalOperationsInTypeHierarchy(ownerType, true, operations);
	    }
        
        // filter overridden operations
        Collection<EOperation> overrideCandidates = QvtOperationalUtil.filterOverriddenOperations(operations);
        
		for (EOperation next : operations) {
			if ((next != operation) && 
					(getUMLReflection().getName(next).equals(operationName) && 
							matchParameters(next, operation))) {				
				EClassifier nextOwner = getUMLReflection().getOwningClassifier(next);
				if(nextOwner == null) {
					// be tolerant to partially parsed operations 
					continue;
				}
				
				if(isContextual) {
					int rel = TypeUtil.getRelationship(this, ownerType, nextOwner); 
					if((rel != UMLReflection.SAME_TYPE) && (UMLReflection.RELATED_TYPE & rel) != 0) {
						// context types are different but part of a common type hierarchy
						EClassifier ret1 = next.getEType(); 
						EClassifier ret2 = operation.getEType();
						if(ret1 != null && ret2 != null && TypeUtil.getRelationship(this, ret1, ret2) != SAME_TYPE) {
							if(QvtOperationalEnv.MAIN.equals(operationName)) {
								// clashes with main(..) are handled separately
								return null;
							}
							// report ill-formed return type for virtual operations
							return new CollisionStatus(next, CollisionStatus.VIRTUAL_METHOD_RETURNTYPE);
						}

						// assemble virtual table info
						if(QvtOperationalUtil.isImperativeOperation(operation) && QvtOperationalUtil.isImperativeOperation(next)) {
							VirtualTable sourceOperVtable = getVirtualTable(operation);
							sourceOperVtable.addOperation(next);
							// do not virtualize operation from the importing module in the imported module for import by access
							if(!isImportedByAccess(next)) {
								VirtualTable targetOperVtable = getVirtualTable(next);
								targetOperVtable.addOperation(operation);
							}
						}

					}
				}
				
				if(ownerType == nextOwner || !isContextual) {
					if(definingModule != next.getEContainingClass()) {
						// we try to override operation only from extended modules
						if(isImportedByExtends(next) && overrideCandidates.contains(next)) {
							result = new CollisionStatus(next, CollisionStatus.OVERRIDES);
						}
					} else {
						return new CollisionStatus(next, CollisionStatus.ALREADY_DEFINED);
					}
				}
			} // end of matching operation processing
		}
				
		return result;
	}
	
	private boolean isImportedByAccess(EOperation operation) {
		Module definingModule = QvtOperationalParserUtil.getOwningModule(operation);
		for (QvtEnvironmentBase nextImport : getImportsByAccess()) {
			if(definingModule == nextImport.getModuleContextType()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isImportedByExtends(EOperation operation) {
		Module definingModule = QvtOperationalParserUtil.getOwningModule(operation);
		if(definingModule == null) {
			return false;
		}
		
		for (QvtEnvironmentBase nextImport : getAllExtendedModules()) {
			if(definingModule == nextImport.getModuleContextType()) {
				return true;
			}
		}
		return false;
	}

	private VirtualTable getVirtualTable(EOperation operation) {
		return VirtualTableAdapter.getAdapter(operation, true).getVirtualTable();
	}
	
	/**
	 * Performs name ignoring match on given parameters.
	 */
	private boolean matchParameters(EOperation a, EOperation b) {
		List<EParameter> aparms = getUMLReflection().getParameters(a);
		List<EParameter> bparms = getUMLReflection().getParameters(b);
		
		if (aparms.size() == bparms.size()) {
			int count = aparms.size();
			
			for (int i = 0; i < count; i++) {
				EParameter aparm = aparms.get(i);
				EParameter bparm = bparms.get(i);
				
				if (!TypeUtil.exactTypeMatch(this, getUMLReflection().getOCLType(aparm), getUMLReflection().getOCLType(bparm))) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Performs name ignoring match on given context types.
	 */
	private boolean matchContext(ImperativeOperation a, ImperativeOperation b) {
		
		return getTypeChecker().exactTypeMatch(
								getUMLReflection().getOCLType(a.getContext()),
								getUMLReflection().getOCLType(b.getContext()));
	}
	
	
	private boolean isOneOfParents(EcoreEnvironment env) {
		for (EcoreEnvironment parent = (EcoreEnvironment)env.getInternalParent(); parent != null; 
			parent = (EcoreEnvironment)parent.getInternalParent()) {
			if(parent == env) {
				return true;
			}
		}
		return false;
	}
	
	protected QvtEnvironmentBase getRootEnv() {
		QvtEnvironmentBase root = this;
		while(root.getInternalParent() instanceof QvtEnvironmentBase) {
			root = (QvtEnvironmentBase) root.getInternalParent();			
		}
		return root;
	}
	
    public String generateTemporaryName() {
        QvtEnvironmentBase rootEnv = getRootEnv();
        String name;
        do {
            name = rootEnv.generateTemporaryNameInternal();
        } while (lookup(name) != null);
        return name;
    }

    
    public boolean isTemporaryElement(String name) {
        return (name != null) && name.startsWith(TEMPORARY_NAME_GENERATOR_UNIQUE_PREFIX);
    }
    
    private String generateTemporaryNameInternal() {
        myTemporaryNameGeneratorInt++;
        return TEMPORARY_NAME_GENERATOR_UNIQUE_PREFIX + myTemporaryNameGeneratorInt;
    }

    @SuppressWarnings("restriction")
	@Override
	public void parserError(int errorCode, int leftToken, int rightToken, String tokenText) {
		ProblemHandler problemHandler = getProblemHandler();
		if (problemHandler == null) {
            return;
        }
		int leftTokenLoc = (leftToken > rightToken ? rightToken : leftToken);
		int rightTokenLoc = rightToken;
		int startOffset = getParser().getIPrsStream().getStartOffset(leftTokenLoc);
		int endOffset = getParser().getIPrsStream().getEndOffset(rightTokenLoc);
		int line = leftTokenLoc >= 0 ? getParser().getIPrsStream().getLine(leftTokenLoc) : -1;
		String message;
        if (line <= 0) {
        	message = org.eclipse.ocl.internal.l10n.OCLMessages.InvalidOCL_ERROR_;
		} else {
			String locInfo = ""; //$NON-NLS-1$
			String messageTemplate = ProblemHandler.ERROR_MESSAGES[errorCode].substring(4);
			String inputText = '"' + getParser().computeInputString(startOffset, endOffset) + '"';
			switch (errorCode) {
				case ParseErrorCodes.EOF_CODE:
				case ParseErrorCodes.MISPLACED_CODE:
				case ParseErrorCodes.DELETION_CODE:
				case ParseErrorCodes.INVALID_TOKEN_CODE:
					message = org.eclipse.ocl.internal.l10n.OCLMessages.bind(
						messageTemplate,
						locInfo,
						inputText);
					break;
	
				case ParseErrorCodes.MERGE_CODE:
				case ParseErrorCodes.BEFORE_CODE:
				case ParseErrorCodes.INSERTION_CODE:
				case ParseErrorCodes.SUBSTITUTION_CODE: // includes SECONDARY_CODE
					message = org.eclipse.ocl.internal.l10n.OCLMessages.bind(
						messageTemplate,
						new Object[]{
							locInfo,
							tokenText,
							inputText
						});
					break;
					
				case ParseErrorCodes.SCOPE_CODE:
					if (leftToken != rightToken) {
						message = org.eclipse.ocl.internal.l10n.OCLMessages.bind(messageTemplate, locInfo, tokenText);
						problemHandler.parserProblem(Severity.ERROR, message, null, startOffset, getParser().getIPrsStream().getEndOffset(leftTokenLoc));
					}
					startOffset = getParser().getIPrsStream().getStartOffset(rightTokenLoc);
	
				default:
					message = org.eclipse.ocl.internal.l10n.OCLMessages.bind(messageTemplate, locInfo, tokenText);
					break;
			}
		}
		problemHandler.parserProblem(Severity.ERROR, message, null, startOffset, endOffset);
		
	}
}
