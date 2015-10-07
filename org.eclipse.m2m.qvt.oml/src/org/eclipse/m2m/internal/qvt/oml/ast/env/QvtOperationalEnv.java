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
 *     Christopher Gerking - Bug 390088
 *     Alex Paperno - bugs 416584, 401521, 403440, 424584
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtMessage;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.HiddenElementAdapter;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.MappingsMapKey;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalTypesUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ValidationMessages;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ResolveInExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.ocl.EnvironmentFactory;
import org.eclipse.ocl.LookupException;
import org.eclipse.ocl.TypeChecker;
import org.eclipse.ocl.TypeResolver;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreFactory;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.lpg.AbstractLexer;
import org.eclipse.ocl.lpg.AbstractParser;
import org.eclipse.ocl.lpg.AbstractProblemHandler;
import org.eclipse.ocl.lpg.ProblemHandler;
import org.eclipse.ocl.lpg.ProblemHandler.Phase;
import org.eclipse.ocl.lpg.ProblemHandler.Severity;
import org.eclipse.ocl.parser.AbstractOCLAnalyzer;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;


public class QvtOperationalEnv extends QvtEnvironmentBase { //EcoreEnvironment {

	public static final String THIS = "this"; //$NON-NLS-1$	
	public static final String MAIN = "main"; //$NON-NLS-1$	
	public static final String TAG_ALIAS = "alias"; //$NON-NLS-1$		
	public static final String METAMODEL_COMPLIANCE_KIND_STRICT = "strict"; //$NON-NLS-1$
	
    private EPackage.Registry myPackageRegistry;    
	private final Set<QvtMessage> myWarningSet = new LinkedHashSet<QvtMessage>(2);
	private final Set<QvtMessage> myErrorSet = new LinkedHashSet<QvtMessage>(2);
	private boolean myCheckForDuplicateErrors;
	private QvtCompilerOptions myCompilerOptions;
	private boolean myParentLocal = false;
	private boolean myIsWithinInitMappingSection = false;
	
	private final Map<String, ModelType> myModelTypeRegistry;
	
    private final Map<MappingsMapKey, List<MappingOperation>> myMappingsMap = new HashMap<MappingsMapKey, List<MappingOperation>>();
    private final Map<ResolveInExp, MappingsMapKey> myResolveInExps = new HashMap<ResolveInExp, MappingsMapKey>();
    
    private final LookupPackageableElementDelegate<EClassifier> LOOKUP_CLASSIFIER_DELEGATE = new LookupPackageableElementDelegate<EClassifier>() {
        public EClassifier lookupPackageableElement(List<String> names) {
            return QvtOperationalEnv.super.lookupClassifier(names);
        }
    };
    

    private final LookupPackageableElementDelegate<EPackage> LOOKUP_PACKAGE_DELEGATE = new LookupPackageableElementDelegate<EPackage>() {
        public EPackage lookupPackageableElement(List<String> names) {
            return QvtOperationalEnv.super.lookupPackage(names);
        }
    };
	
	protected QvtOperationalEnv(QvtOperationalEnv parent) {
		super(parent);
		
		if(parent == null) {
			throw new IllegalArgumentException("Non-null parent QVT environment expected"); //$NON-NLS-1$
		}
		
		myCheckForDuplicateErrors = false;
		
		myModelTypeRegistry = parent.myModelTypeRegistry;
		myCompilerOptions = parent.myCompilerOptions;
	}
	
	protected QvtOperationalEnv(EPackage.Registry packageRegistry, Resource resource) {
		super(packageRegistry, resource);
		myPackageRegistry = packageRegistry;
		myModelTypeRegistry = new LinkedHashMap<String, ModelType>(1);
	}
	
	/**
	 * This operation declares the parameters of the given operation
	 */
	@Override
	protected void setContextOperation(EOperation operation) {
		super.setContextOperation(operation);
		
		defineOperationParameters(operation);
	}
			
	@Override
	protected TypeResolver<EClassifier, EOperation, EStructuralFeature> createTypeResolver(Resource resource) {
		return new QvtTypeResolverImpl(this, resource);
	}
	
	@Override
	protected TypeChecker<EClassifier, EOperation, EStructuralFeature> createTypeChecker() {
		return new TypeCheckerImpl(this);
	}
	
	/**
	* Gets the package registry used in this environment EClassifier lookup.
	*/
	public EPackage.Registry getEPackageRegistry() {
		if(myPackageRegistry != null) {
			return myPackageRegistry;   
		}
		
		EPackage.Registry registry;
		if(getInternalParent() == null) {
			if(getFactory() != null) {
				registry = getFactory().getEPackageRegistry();
			} else {
				myPackageRegistry = registry = createDefaultPackageRegistry();				
			}
		} else {
			QvtOperationalEnv parentEnv = (QvtOperationalEnv) getInternalParent();
			registry = parentEnv.getEPackageRegistry();
		}
		
		return registry;
	}
	
    public Map<String, ModelType> getModelTypeRegistry() {
        return myModelTypeRegistry;
    }
	
    @Override
	public QvtOperationalEnvFactory getFactory() {
		EnvironmentFactory<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> 
			superFactory = super.getFactory();
		
		if(superFactory instanceof QvtOperationalEnvFactory == true) {			
			// I have an explicitly assigned creating factory already
			return (QvtOperationalEnvFactory) superFactory;
		}
		// no factory or the default Ecore factory instantiated by OCL environment
		QvtOperationalEnvFactory qvtFactory = new QvtOperationalEnvFactory(getEPackageRegistry());
		setFactory(qvtFactory);
		return qvtFactory;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void setFactory(EnvironmentFactory<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral,
			EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> factory) {
		if(factory instanceof QvtOperationalEnvFactory == false) {
			throw new IllegalArgumentException("QVT EnvFactory required"); //$NON-NLS-1$
		}
		
		super.setFactory(factory);
	}
	
	public void setParentLocal() {
		myParentLocal = true;
	}
	 
	public void enterInitMappingSection() {
		myIsWithinInitMappingSection = true;
	}

	public void exitInitMappingSection() {
		myIsWithinInitMappingSection = false;
	}

	public boolean isWithinInitMappingSection() {
		return myIsWithinInitMappingSection;
	}
	
	@Override
    public Variable<EClassifier, EParameter> lookupLocal(String name) {
        // support operation parameters whose names need to be escaped in OCL
        Variable<EClassifier, EParameter> result = super.lookupLocal(name);
        
        if (result == null && myParentLocal) {
            result = getInternalParent().lookupLocal(name);
        }
        
        return result;
    }	

	public EStructuralFeature lookupPropertyAlias(EClassifier owner, String aliasName) {
		Module module = getModuleContextType();
		if(module != null) {
			for (EAnnotation nextTag : module.getOwnedTag()) {
				String newName = nextTag.getDetails().get(TAG_ALIAS);
				EList<EObject> references = nextTag.getReferences();
				EObject element = references.isEmpty() ? null : references.get(0);
				if(aliasName.equals(newName) && element instanceof EStructuralFeature) {
					if(TypeUtil.compatibleTypeMatch(this, owner, getUMLReflection().getOwningClassifier(element))) {
						return (EStructuralFeature)element;						
					}					
				}
			}
		}
		
		return null;
	}	
			
	@Override
	public EOperation tryLookupOperation(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args) throws LookupException {
        EOperation result = doLookupOperation(owner, name, args);
        
        if ((result == null) && AbstractOCLAnalyzer.isEscaped(name)) {
            result = doLookupOperation(owner, AbstractOCLAnalyzer.unescape(name), args);
        }
        
        return result;
	}
	
	private EOperation doLookupOperation(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args) throws LookupException {
		if (owner == null) {
			Variable<EClassifier, EParameter> vdcl = lookupImplicitSourceForOperation(name, args);
			if (vdcl == null) {
				return null;
			}
			
			owner = vdcl.getType();
		}

		TypeChecker<EClassifier, EOperation, EStructuralFeature> typeChecker = getTypeChecker();
		if(typeChecker instanceof TypeCheckerImpl) {
			return ((TypeCheckerImpl)typeChecker).findMostSpecificOperationMatching(owner, name, args);
		}
		
		return TypeUtil.findOperationMatching(this, owner, name, args);
	}
	
    public List<MappingOperation> lookupMappingOperations(EClassifier owner, String name) {
        if (owner == null) {
            owner = getModuleContextType();
            if(owner == null) {
            	return Collections.emptyList();
            }            
        }

        UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> uml = getUMLReflection();
        List<EOperation> operations = TypeUtil.getOperations(this, owner);
        List<MappingOperation> result = new ArrayList<MappingOperation>();
		for (EOperation operation : operations) {
		    if (uml.getName(operation).equals(name) && QvtOperationalUtil.isMappingOperation(operation)) {
		        result.add((MappingOperation)operation);
		    }
		}

        return result;
    }

    public EOperation tryLookupConstructorOperation(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args) throws LookupException {
    	List<EOperation> operations = lookupConstructorOperations(owner, name, args);
    	if(operations == null || operations.isEmpty()) {
    		return null;
    	}

    	TypeChecker<EClassifier, EOperation, EStructuralFeature> typeChecker = getTypeChecker();
    	if(typeChecker instanceof TypeCheckerImpl) {
			TypeCheckerImpl specificChecker = (TypeCheckerImpl) typeChecker;
			return specificChecker.getMostSpecificOperation(operations, args);
    	}
    	
    	// fall back implementation in case some overrides #createTypeChecker() 
    	// first pass with strict matching (like in doFindCollidingOperation() and QvtOperationalEnv::lookupOperation() for mappings)
    	for (EOperation op : operations) {
	        List<EParameter> params = op.getEParameters();

	        boolean isMatched = true;
	        for (int i = 0, n = params.size(); i < n; ++i) {
				if (!TypeUtil.exactTypeMatch(this, getUMLReflection().getOCLType(args.get(i)),
						getUMLReflection().getOCLType(params.get(i)))) {
					isMatched = false;
					break;
				}
	        }
	        if (isMatched) {
	        	return op;
	        }
    	}

    	// second pass with conformance matching (like in AbstractEnvironment::lookupOperation() for operations)
    	for (EOperation op : operations) {
			if (getTypeChecker().matchArgs(owner, op.getEParameters(), args)) {
	        	return op;
	        }
    	}

    	return null;
    }
    
    private List<EOperation> lookupConstructorOperations(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args) {
        if (owner == null) {
            owner = getModuleContextType();
            if(owner == null) {
            	return Collections.emptyList();
            }
        }

        UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> uml = getUMLReflection();
        TypeChecker<EClassifier, EOperation, EStructuralFeature> typeChecker = getTypeChecker();
		
        List<EOperation> operations = typeChecker.getOperations(owner);
        List<EOperation> result = new ArrayList<EOperation>(2);

        for (EOperation operation : operations) {
		    if (uml.getName(operation).equals(name)		    		
		    		&& QvtOperationalUtil.isConstructorOperation(operation)
		    		&& typeChecker.matchArgs(owner, uml.getParameters(operation), args)) {
		    	
		    	EClassifier nextOwner = uml.getOwningClassifier(operation);
		    	// for constructors consider only identical types  
		    	if(owner == nextOwner) {
		    		result.add(operation);
		    	}
		    }
		}

        return result;
    }
    
	public void reportError(String message, int startOffset, int endOffset) {
		if ((myCompilerOptions != null) && !myCompilerOptions.isReportErrors()) {
			return;
		}
				
		QvtOperationalEnv parent = this;
		while (parent.getInternalParent() != null) {
			parent = (QvtOperationalEnv) parent.getInternalParent();
		}
		if (parent instanceof QvtOperationalModuleEnv && ((QvtOperationalModuleEnv)parent).getFileParent() != null) {
			parent = ((QvtOperationalModuleEnv)parent).getFileParent();
		}
		
		boolean foundSameLocation = false;
		int msgLength = endOffset-startOffset+1;
		if (myCheckForDuplicateErrors) {
			for (QvtMessage msg : parent.myErrorSet) {
				if (msg.getOffset() == startOffset && msg.getLength() == msgLength) {
					foundSameLocation = true;
					break;
				}
			}
		}
		
		if (!foundSameLocation) {
			parent.myErrorSet.add(new QvtMessage(message, QvtMessage.SEVERITY_ERROR, startOffset, msgLength, getLineNum(parent, startOffset)));
		}
	}

	public void reportWarning(String message, int startOffset, int endOffset) {
        if ((myCompilerOptions != null) && !myCompilerOptions.isReportErrors()) {
			return;
		}
		QvtOperationalEnv parent = this;
		while (parent.getInternalParent() != null) {
			parent = (QvtOperationalEnv) parent.getInternalParent();
		}
		if (parent instanceof QvtOperationalModuleEnv && ((QvtOperationalModuleEnv)parent).getFileParent() != null) {
			parent = ((QvtOperationalModuleEnv)parent).getFileParent();
		}
		parent.myWarningSet.add(new QvtMessage(message, QvtMessage.SEVERITY_WARNING, startOffset, endOffset-startOffset+1, getLineNum(parent, startOffset)));

		// TODO #199408  Use traces in QVTParser instead of System.xxx output facilities
		//System.err.println("Warning: " + message + ", Pos: " + startOffset + "-" + endOffset);
	}
	
	public void reportError(String message, CSTNode node) {
		int startOffset = (node != null) ? node.getStartOffset() : 0;
		int endOffset = (node != null) ? node.getEndOffset() : 0;
		reportError(message, startOffset, endOffset);
	}

	public void reportWarning(String message, CSTNode node) {
		int startOffset = (node != null) ? node.getStartOffset() : 0;
		int endOffset = (node != null) ? node.getEndOffset() : 0;
		reportWarning(message, startOffset, endOffset);
	}

	public boolean hasErrors() {
		return myErrorSet != null && myErrorSet.isEmpty() == false;
	}
	
	public boolean hasWarnings() {
		return myWarningSet != null && myWarningSet.isEmpty() == false;
	}	
		
	public List<QvtMessage> getErrorsList() {
		return new ArrayList<QvtMessage>(myErrorSet);
	}

	public List<QvtMessage> getWarningsList() {
		return new ArrayList<QvtMessage>(myWarningSet);
	}
	
	public List<QvtMessage> getAllProblemMessages() {
		if(hasErrors() || hasWarnings()) {
			List<QvtMessage> result = new ArrayList<QvtMessage>();
			result.addAll(getErrorsList());
			result.addAll(getWarningsList());			
			return result;
		}
		
		return Collections.emptyList();
	}	
	
	public void clearProblems() {
		if(myErrorSet != null) {
			myErrorSet.clear();
		}
		
		if(myWarningSet != null) {
			myWarningSet.clear();
		}
	}		
			
	/**
	 * Register given modeltype in the Environment. Modeltype's registry is used in
	 * override {@link #lookupPackage(List)} and {@link #lookupClassifier(List)}
	 * @return the previously registered model-type if any, otherwise <code>null</code>
	 */
	public ModelType registerModelType(ModelType modelType) {
		return myModelTypeRegistry.put(modelType.getName(), modelType);
	}

	public ModelType getModelType(String name) {
		return myModelTypeRegistry.get(name);
	}
	
	@Override
	public Variable<EClassifier, EParameter> lookupImplicitSourceForOperation(
			String name, List<? extends TypedElement<EClassifier>> args) {
		// propagate implict source lookup to parent, allowing to reach the module-wide 'this'
		Variable<EClassifier, EParameter> result = super.lookupImplicitSourceForOperation(name, args);
		Variable<EClassifier, EParameter> tentativeResult = result;
		// check if implicit source results in self variable, try lookup for implicit this as a higher precedence
		// Remark: validation should report the problem about call on 'self' using implicit source			
		if((result == null || SELF_VARIABLE_NAME.equals(result.getName())) && getInternalParent() != null) {
			result = getInternalParent().lookupImplicitSourceForOperation(name, args);
			if(tentativeResult != null && result == null) {
				result = tentativeResult;
			}
		}
		
		return result;
	}
	
	@Override
	public Variable<EClassifier, EParameter> lookupImplicitSourceForProperty(String name) {
		Variable<EClassifier, EParameter> implicitSource = super.lookupImplicitSourceForProperty(name);
		Variable<EClassifier, EParameter> tentativeResult = implicitSource;
		// check if implicit source results in self variable, try lookup for implicit this as a higher precedence
		// Remark: validation should report the problem about call on 'self' using implicit source
		if((implicitSource == null || SELF_VARIABLE_NAME.equals(implicitSource.getName())) && getInternalParent() != null) {
			implicitSource = getInternalParent().lookupImplicitSourceForProperty(name);
			if(tentativeResult != null && implicitSource == null) {
				implicitSource = tentativeResult;
			}
		}			

		return implicitSource;
	}
	
	@Override
	public EClassifier lookupClassifier(List<String> names) {
		if(names.isEmpty()) {
			return null;
		}
		String firstName = names.get(0);
		
		if (names.size() == 1) {
			// Unqualified type. lookup rules:
			// - Firstly a type definition existing at the level of the current module (a transformation or a library) is searched.
			// - If not found, all the packages of the model types declared in the module are recursively visited to found a type with the same name.  
			
			// TODO support intermediate classes (hosted in implicit '_INTERMEDIATE' package)
			
			Module moduleContextType = getModuleContextType();
			if(moduleContextType != null && firstName.equals(moduleContextType.getName())) {
				return moduleContextType;
			}
			
			for (QvtEnvironmentBase nextImported : getRootEnv().getImportsByExtends()) {
				Module importedModule = nextImported.getModuleContextType();
				if(importedModule != null && firstName.equals(importedModule.getName())) {
					return importedModule;
				}				
			}
			
			for (QvtEnvironmentBase nextImported : getRootEnv().getImportsByAccess()) {
				Module importedModule = nextImported.getModuleContextType();
				if(importedModule != null && firstName.equals(importedModule.getName())) {
					return importedModule;
				}				
			}			
			
			if (myModelTypeRegistry.containsKey(firstName)) {
				return myModelTypeRegistry.get(firstName);
			}

			for (ModelType modelType : myModelTypeRegistry.values()) {
				EClassifier lookupClassifier = doLookupModeltypeClassifier(modelType, names);
				if (lookupClassifier != null) {
					return lookupClassifier;
				}
			}
		}
		
		if (names.size() > 1) {
			// Qualified type 
			// It is possible to either qualify the type name with a model type or a package name
			if(names.size() == 2) {
				EClassifier stdType = QvtOperationalStdLibrary.INSTANCE.lookupClassifier(names);
				if(stdType != null) {
					return stdType;
				}
			}
			
			if (myModelTypeRegistry.containsKey(firstName)) {
				EClassifier lookupClassifier = doLookupModeltypeClassifier(
						myModelTypeRegistry.get(firstName), names.subList(1, names.size()));
				if (lookupClassifier != null) {
					return lookupClassifier;
				}
			}
		}
		
		EClassifier result = super.lookupClassifier(names);
		return (result != null) ? result : QvtOperationalStdLibrary.INSTANCE.lookupClassifier(names);
	}
		
	@Override
	public EPackage lookupPackage(List<String> path) {
        if (path.size() > 1) {
            // Qualified type 
            // It is possible to either qualify the type name with a model type or a package name

            if (myModelTypeRegistry.containsKey(path.get(0))) {
                EPackage lookupPackage = doLookupModeltypePackage(
                        myModelTypeRegistry.get(path.get(0)), path.subList(1, path.size()));
                if (lookupPackage != null) {
                    return lookupPackage;
                }
            }
        } else if(path.size() == 1) {
        	EPackage stdPackage = QvtOperationalStdLibrary.INSTANCE.getStdLibModule();
        	if(stdPackage.getName().equals(path.get(0))) {
        		return stdPackage;
        	}
        }

        return super.lookupPackage(path);
	}

    private EPackage doLookupModeltypePackage(ModelType modelType, List<String> path) {
        return lookupPackageableElement(modelType, path, LOOKUP_PACKAGE_DELEGATE);
    }
    
    private EClassifier doLookupModeltypeClassifier(ModelType modelType, List<String> path) {
		return lookupPackageableElement(modelType, path, LOOKUP_CLASSIFIER_DELEGATE);
	}
	
    private <T extends EObject> T lookupPackageableElement(ModelType modelType, List<String> path, LookupPackageableElementDelegate<T> lookupPackageableElementDelegate) {
        EPackage oldContext = super.getContextPackage();
        T result = null;
        List<EPackage> metamodels = modelType.getMetamodel();
        for (EPackage pkg : metamodels) {
            super.setContextPackage(pkg);
            result = lookupPackageableElementDelegate.lookupPackageableElement(path);
            EObject eContainer = result;
            for (int i = 0, n = path.size(); i < n; ++i) {
            	if (eContainer == null) {
            		break;
            	}
            	eContainer = eContainer.eContainer();
            }
            if (eContainer != pkg) {
                result = null;
            }
            if (result != null) {
                break;
            }
        }
        super.setContextPackage(oldContext);
        
        return result;
    }
    
    @Override
	public EOperation defineOperation(EClassifier owner, String name, EClassifier type, 
			List<org.eclipse.ocl.expressions.Variable<EClassifier, EParameter>> params,
			Constraint constraint) {
		EOperation result = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEOperation();
		
		result.setName(name);
		result.setEType((type == null) ? getOCLStandardLibrary().getOclVoid() : type);
		
		for (Variable<EClassifier, EParameter> next : params) {
			EParameter param = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEParameter();
			param.setName(next.getName());
			param.setEType((next.getType() == null)? getOCLStandardLibrary().getOclVoid() : next.getType());
			
			result.getEParameters().add(param);
		}

		if(owner == getModuleContextType()) {
			getModuleContextType().getEOperations().add(result);
		} else {
			addHelperOperation(owner, result);
		}
		
		return result;
	}
        
	public ImperativeOperation defineImperativeOperation(ImperativeOperation operation, boolean isMappingOperation,
			boolean isCheckDuplicates) {
		EClassifier ownerType = QvtOperationalParserUtil.getContextualType(operation);
		boolean isContextual = true;
		if (ownerType == null) {
			isContextual = false;
			ownerType = getModuleContextType();
		}
		
		boolean isError = false;
		ImperativeOperation newOperation = operation;
		CollisionStatus collidingOperStatus = findCollidingOperation(ownerType, newOperation);
		
		if(collidingOperStatus != null) {
			EOperation collidingOper = collidingOperStatus.getOperation();
			
			if(collidingOperStatus.getCollisionKind() == CollisionStatus.ALREADY_DEFINED) {
				isError = true;
				HiddenElementAdapter.markAsHidden(operation);
				reportError(NLS.bind(ValidationMessages.SemanticUtil_0, new Object[] {
								operation.getName(), ownerType.getName() }),
								operation.getStartPosition(), operation.getEndPosition());
			} 
			else if(collidingOperStatus.getCollisionKind() == CollisionStatus.OVERRIDES) {
				if(collidingOper instanceof ImperativeOperation) {
					// only imperative operations can be overridden
					EClassifier overriddenReturnType = collidingOper.getEType(); 
					EClassifier newReturnType = newOperation.getEType();
					if(newReturnType != null &&  overriddenReturnType != null) {
						if(TypeUtil.compatibleTypeMatch(this, newReturnType, overriddenReturnType)) {
							newOperation.setOverridden((ImperativeOperation)collidingOper);
						} else {
							isError = true;
							reportError(NLS.bind(ValidationMessages.OperationOverrideWithInvalidReturnType, new Object[] {
									getUMLReflection().getName(operation),
									getUMLReflection().getName(collidingOper.getEContainingClass()) }),
									operation.getStartPosition(), operation.getEndPosition());								
						}
					}
				}
				
				if(!isError) {
					Module owningModule = QvtOperationalParserUtil.getOwningModule(collidingOper);
					if(owningModule == null || owningModule == getQVTStandardLibrary().getStdLibModule()) {
						reportWarning(NLS.bind(ValidationMessages.HidingStdlibOperationDiscouraged, operation.getName()),
								operation.getStartPosition(), operation.getEndPosition());
					}
				}
			}
			else if(collidingOperStatus.getCollisionKind() == CollisionStatus.VIRTUAL_METHOD_RETURNTYPE) {
				isError = true;
				HiddenElementAdapter.markAsHidden(operation);				
				reportError(NLS.bind(ValidationMessages.ReturnTypeMismatch,  
						operation.getName(), QvtOperationalTypesUtil.getTypeFullName(collidingOperStatus.getOperation().getEType())), 
						operation.getStartPosition(), operation.getEndPosition());
			} else {
				assert false;
			}
		} 
		
		if(isContextual && !isError) {
			getTypeResolver().resolveAdditionalOperation(ownerType, newOperation);
		}
		
		getModuleContextType().getEOperations().add(newOperation);
		
		return newOperation;
	}
	
	@Override
	public Module getModuleContextType() {
		return getInternalParent() instanceof QvtOperationalEnv ? ((QvtOperationalEnv)getInternalParent()).getModuleContextType() : null;
	}	

	
	private void defineParameterVar(EParameter parameter) {		
		Variable<EClassifier, EParameter> var;
		if(parameter instanceof VarParameter) {
			var = (VarParameter) parameter;
		} else {
			var = EcoreFactory.eINSTANCE.createVariable();
			var.setName(parameter.getName());
			var.setType(parameter.getEType());
			var.setRepresentedParameter(parameter);
		}
		
		addElement(parameter.getName(), var, true);
	}
	
	private void defineOperationParameters(EOperation operation) {
		final ImperativeOperation imperativeOperation = (operation instanceof ImperativeOperation) ? (ImperativeOperation) operation : null; 
		final boolean isMapping = operation instanceof MappingOperation;
		final boolean hasMultipleResultParams = (imperativeOperation != null) ? imperativeOperation.getResult().size() > 1 : false;
		
		if(imperativeOperation != null && QvtOperationalParserUtil.isContextual(imperativeOperation)) {
			VarParameter context = imperativeOperation.getContext();
			assert context != null;
			addElement(context.getName(), context, false);
		}
		
		for (EParameter parameter : operation.getEParameters()) {
			defineParameterVar(parameter);
		}
		
		if(isMapping || hasMultipleResultParams) {
			assert imperativeOperation != null;
			for (VarParameter parameter : imperativeOperation.getResult()) {
		        defineParameterVar(parameter);
			}
		}
	}

	public void setQvtCompilerOptions(QvtCompilerOptions options) {
	    myCompilerOptions = options;
	}
	
	public void setCheckForDuplicateErrors(boolean checkForDuplicateErrors) {
		myCheckForDuplicateErrors = checkForDuplicateErrors;
	}
	
	public void registerMappingOperation(MappingOperation operation) {
	    if (getInternalParent() != null) {
	        ((QvtOperationalEnv) getInternalParent()).registerMappingOperation(operation);
	    } else {
			EClassifier ownerType = QvtOperationalParserUtil.getContextualType(operation);
			if (ownerType == null) {
				ownerType = getUMLReflection().getOwningClassifier(operation);
				if(ownerType == null) {
	            	return;
	            }
			}
	    	
            MappingsMapKey key = new MappingsMapKey(ownerType, operation.getName());
            List<MappingOperation> sameNameAndContextOperations = myMappingsMap.get(key);
            if (sameNameAndContextOperations == null) {
                sameNameAndContextOperations = new ArrayList<MappingOperation>();
                myMappingsMap.put(key, sameNameAndContextOperations);
            }
            sameNameAndContextOperations.add(operation);
	    }
	}
	
	public void registerResolveInExp(ResolveInExp resolveInExp, EClassifier referredMappingContextType, String mappingName) {
        if (getInternalParent() != null) {
            ((QvtOperationalEnv) getInternalParent()).registerResolveInExp(resolveInExp, referredMappingContextType, mappingName);
        } else {
            myResolveInExps.put(resolveInExp, new MappingsMapKey(referredMappingContextType, mappingName));
        }
	}
	
	public void resolveResolveInExpInMappings() {
	    if (getInternalParent() == null) {
	        for (Map.Entry<ResolveInExp, MappingsMapKey> entry : myResolveInExps.entrySet()) {
	            MappingsMapKey mappingsMapKey = entry.getValue();
	            List<MappingOperation> sameNameAndContextOperations = myMappingsMap.get(mappingsMapKey);
	            ResolveInExp resolveInExp = entry.getKey();
	            if (sameNameAndContextOperations != null) {
	                for (MappingOperation mappingOperation : sameNameAndContextOperations) {
	                    if(resolveInExp.getInMapping() == null) {
	                    	// Keep only the first occurence found
	                    	resolveInExp.setInMapping(mappingOperation);
	                    }
	                }
	            }
	            // assert inMapping has been set, otherwise parse error should have been reported
	            assert resolveInExp.getInMapping() != null;
	        }
	    } else {
	    	((QvtOperationalEnv)getInternalParent()).resolveResolveInExpInMappings();
	    }
	}
	
	@Override
	protected ProblemHandler createDefaultProblemHandler(AbstractParser parser) {
		return new AbstractProblemHandler(parser) {
			@Override
			public void handleProblem(Severity problemSeverity, Phase processingPhase, String problemMessage,					
					String processingContext, int startOffset, int endOffset) {
				boolean allowCsUnboundValidationProblems = false;
				if(isMDTOCLCompatibilityFalseProblem(allowCsUnboundValidationProblems, problemSeverity, 
						processingPhase, problemMessage, processingContext, startOffset, endOffset)) {
					// Remark: Not a real problem we can handle now, but we keep in mind ;) 
					return;
				}
				
				if(problemSeverity == Severity.INFO || problemSeverity == Severity.OK || problemSeverity == Severity.WARNING) {
					reportWarning(problemMessage, startOffset, endOffset);
				} else {
					reportError(problemMessage, startOffset, endOffset);					
				}
			}
		};
	}
		
	@Override
	public void analyzerError(String problemMessage, String problemContext, Object problemObject) {
		CSTNode cstNode = getASTMapping(problemObject);
		int startOffset = cstNode != null ? cstNode.getStartOffset() : -1;
		int endOffset = cstNode != null ? cstNode.getEndOffset() : -1;
		
		if(cstNode == null && problemObject instanceof ASTNode) {
			ASTNode astNode = (ASTNode) problemObject;
			startOffset = astNode.getStartPosition();
			endOffset = astNode .getEndPosition();
		}
		
		analyzerError(problemMessage, problemContext, startOffset, endOffset);
	}
	
	@Override
	public void initASTMapping(Object astNode, CSTNode cstNode) {	
		if(astNode instanceof ASTNode) {
			ASTNode castNode = (ASTNode) astNode;
			if(castNode.getEndPosition() < 0) {
				castNode.setStartPosition(cstNode.getStartOffset());
				castNode.setEndPosition(cstNode.getEndOffset());				
			}
		}
		super.initASTMapping(astNode, cstNode);
	}
	
	@Override
	public void close() {
    	setParser(null);
    	setProblemHandler(null);
    	setASTNodeToCSTNodeMap(null);
    	
    	super.close();
	}
	
	private static int getLineNum(QvtOperationalEnv env, int startOffset) {
		if(startOffset < 0) {
			return -1;
		}
		AbstractParser parser = env.getParser();
		if(parser != null) {
			AbstractLexer lexer = parser.getLexer();
			if(lexer != null) {
				if(startOffset <= lexer.getILexStream().getStreamLength()) {
					try {
						return lexer.getILexStream().getLineNumberOfCharAt(startOffset);	
					} catch (RuntimeException e) {
						// TODO - add trace
						// do nothing, the line number just not available
					}					
				}
			}
		}
		return -1;
	}
	
	private static EPackage.Registry createDefaultPackageRegistry() {
		return new EPackageRegistryImpl();
	}	
	
	/**
	 * This operation indicates whether the given problem is a real QVT domain
	 * problem or a compatibility with MDT OCL to solve, thus not a problem to be propagated.
	 * For example, AST validation problem with not source text location, error conditions
	 * that are valid in QVT, etc.
	 * <p> 
	 * @param allowCsUnboundValidationProblems <code>false</code> indicates that all OCL AST 
	 * validation problems without CST binding will be recognized as a false compatibility problem   
	 */
    public static boolean isMDTOCLCompatibilityFalseProblem(
    		boolean allowCsUnboundValidationProblems,
			Severity problemSeverity, Phase processingPhase, String problemMessage,
			String processingContext, int startOffset, int endOffset) {
    	
		// FIXME - filter out those MDT OCL validation problems we can not handle yet   
		if (Phase.VALIDATOR == processingPhase && startOffset == -1 && endOffset == -1
			&& !allowCsUnboundValidationProblems) {
			return true;
		}
    	
		// FIXME - workaround until fixed [https://bugs.eclipse.org/bugs/show_bug.cgi?id=244144] 
		if("collectionTypeResultTypeOf".equals(processingContext)) { //$NON-NLS-1$
			return true;
		}
		
		return false;
    }
 
    
    private interface LookupPackageableElementDelegate<T> {
        public T lookupPackageableElement(List<String> names);
    };
}