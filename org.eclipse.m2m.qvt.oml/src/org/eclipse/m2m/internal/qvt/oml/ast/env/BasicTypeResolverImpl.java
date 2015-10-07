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
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.DictionaryType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeOCLFactory;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeOCLPackage;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ListType;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.Typedef;
import org.eclipse.ocl.AbstractTypeResolver;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.AnyType;
import org.eclipse.ocl.types.CollectionType;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.UMLReflection;


/**
 * Type resolver defining non-imperative operations in QVT style 
 */
class BasicTypeResolverImpl
	extends AbstractTypeResolver<EPackage, EClassifier, EOperation, EStructuralFeature, EParameter> {

	BasicTypeResolverImpl(QvtEnvironmentBase env) {
		super(env);
	}
	
	BasicTypeResolverImpl(QvtEnvironmentBase env, Resource resource) {
		super(env, resource);
	}
    
	@Override
	public EClassifier resolve(EClassifier type) {
		if(type instanceof ListType) {
			return resolveListType(((ListType)type).getElementType());
		} else if(type instanceof DictionaryType) {
			DictionaryType dictType = (DictionaryType) type;
			return resolveDictionaryType(dictType.getKeyType(), dictType.getElementType());
		}
		
		return super.resolve(type);
	}
	
	public ListType resolveListType(EClassifier elementType) {
		ListType listType = findListType(elementType);
		if(listType == null) {
			// add this to QVT environment
			ListType newList = QvtOperationalStdLibrary.INSTANCE.getStdlibFactory().createList(elementType);
			getCollectionPackage().getEClassifiers().add(newList);
			return newList;
		}
		
		return listType;
	}
	
	public DictionaryType resolveDictionaryType(EClassifier keyType, EClassifier elementType) {
		DictionaryType dictionaryType = findDictionaryType(keyType, elementType);
		if(dictionaryType == null) {
			DictionaryType newDictionary = QvtOperationalStdLibrary.INSTANCE
					.getStdlibFactory().createDictionary(keyType, elementType);
			
			getCollectionPackage().getEClassifiers().add(newDictionary);
			return newDictionary;
		}
		
		return dictionaryType;
	}	

	private ListType findListType(EClassifier elementType) {
		for (EClassifier next : getEnvironment().getUMLReflection().getClassifiers(getCollectionPackage())) {
	        if (next instanceof ListType) {
				ListType type = (ListType) next;
				if (exactCollectionTypeMatch(type.getElementType(), elementType)) {
					return type;
				}
	        }
		}
		
		return null;		
	}

	private DictionaryType findDictionaryType(EClassifier keyType, EClassifier valueType) {
		for (EClassifier next : getEnvironment().getUMLReflection().getClassifiers(getCollectionPackage())) {
	        if (next instanceof DictionaryType) {
	        	DictionaryType type = (DictionaryType) next;				
				if (exactCollectionTypeMatch(type.getKeyType(), keyType)) {
					if (exactCollectionTypeMatch(type.getElementType(), valueType)) {
						return type;
					}
				}
	        }
		}
		
		return null;		
	}	
	
	@Override
	protected CollectionType<EClassifier, EOperation> findCollectionType(CollectionKind kind, EClassifier elementType) {
		for (EClassifier next : getEnvironment().getUMLReflection().getClassifiers(getCollectionPackage())) {
	        if (next instanceof CollectionType<?, ?>) {
	        	if(next.eClass() == ImperativeOCLPackage.eINSTANCE.getDictionaryType() ||
	        		next.eClass() == ImperativeOCLPackage.eINSTANCE.getListType()) {
	        		// Note: QVT have unknown CollectionKind
	        		continue;
	        	}
	        
				@SuppressWarnings("unchecked")
				CollectionType<EClassifier, EOperation> type = (CollectionType<EClassifier, EOperation>) next;
				if (type.getKind() != kind) {
					continue;
				}
				
				if (exactCollectionTypeMatch(type.getElementType(), elementType)) {
					return type;
				}
	        }
		}
		
		return null;
	}
	
	private boolean exactCollectionTypeMatch(EClassifier type1, EClassifier type2) {
		if (TypeUtil.exactTypeMatch(getEnvironment(), type1, type2)) {
			if (type1 instanceof AnyType<?> && type2 instanceof AnyType<?>) {
				if (((AnyType<?>) type1).getName().equals(((AnyType<?>) type2).getName())) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}

	
    @Override
	protected Resource createResource() {
		// Note: we need Ecore resource to serialize EAnnotations properly
		return new EcoreResourceFactoryImpl().createResource(URI.createURI("ocl:///qvto.env.ecore")); //$NON-NLS-1$
	}
        
    @Override
    protected EPackage createTuplePackage() {
        EPackage result = super.createTuplePackage();
// Note: tuples supported by the central Module factory
//        if(result instanceof Module == false) {
//        	result.setEFactoryInstance(new TupleFactory());
//        }
        return result;
    }
    
    @Override
    protected EPackage createPackage(String name) {    	
    	
        QvtEnvironmentBase env = (QvtEnvironmentBase)getEnvironment();
        Module module = env.getModuleContextType();
    	if(module != null) {
    		return module;
    	}
    	
    	EPackage result = EcoreFactory.eINSTANCE.createEPackage();
    	result.setName(name);    		
		getResource().getContents().add(result);
        return result;
    }
    
    @Override
	protected EPackage findPackage(String name) {
		EPackage result = null;
		
		for (EObject o : getResource().getContents()) {
			if (o instanceof EPackage) {
				EPackage epkg = (EPackage) o;				
				if (name.equals(epkg.getName())) {
					if(epkg instanceof Module) {
						// go on looking up, as this was 
						continue;
					}
										
					result = epkg;
					break;
				}
			}
		}
		
		return result;
	}
    
    @Override
    protected void addClassifier(EPackage pkg, EClassifier classifier) {
        pkg.getEClassifiers().add(classifier);
    }
    
    @Override
    protected void addOperation(EClassifier owner, EOperation operation) {
        ((EClass) owner).getEOperations().add(operation);
    }
    
    @Override
    protected void addProperty(EClassifier owner, EStructuralFeature property) {
        ((EClass) owner).getEStructuralFeatures().add(property);
    }
	
	List<EOperation> getAllCompatibleAdditionalOperations(org.eclipse.ocl.ecore.CollectionType type) {
//		List<EOperation> result = null;
//        if (hasAdditionalFeatures()) {
//            EPackage pkg = getAdditionalFeaturesPackage();            
//            if (pkg != null) {
//        		UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, ?, EParameter, ?, ?, ?, ?> uml = getEnvironment().getUMLReflection();
//				List<EClassifier> shadowOwners = uml.getClassifiers(pkg);
//				for (EClassifier next : shadowOwners) {
//        			EClassifier shadowedType = getShadowedClassifier(next);
//
//        			if (shadowedType instanceof org.eclipse.ocl.ecore.CollectionType &&
//    					TypeUtil.compatibleTypeMatch(getEnvironment(), type, shadowedType)) {
//    					
//    					if(result == null) {
//    						result = new ArrayList<EOperation>();
//    					}
//        				result.addAll(uml.getOperations(next));
//        			}
//        		}
//            }
//        }
//        
//		return (result != null) ? Collections.<EOperation>unmodifiableList(result) : ECollections.<EOperation>emptyEList();

        if (hasAdditionalFeatures()) {
        	UMLReflection<EPackage, EClassifier, EOperation, EStructuralFeature, ?, EParameter, ?, ?, ?, ?> uml = getEnvironment().getUMLReflection();
        	EClassifier eClassifier = ShadowClassAdapter.getShadowToBaseMap(getAdditionalFeaturesPackage()).get(type);
        	return uml.getOperations(eClassifier);
        }
        return ECollections.<EOperation>emptyEList();
	}    
    
    @Override
	protected EClass createShadowClass(EClassifier type) {
		Typedef typeDef = ImperativeOCLFactory.eINSTANCE.createTypedef();
		typeDef.setName(type.getName());
		typeDef.setBase(type);
		
		EPackage pkg = getAdditionalFeaturesPackage();
		ShadowClassAdapter.getShadowToBaseMap(pkg).put(type, typeDef);
		
		return typeDef;
	}
    
    @Override
    protected EClassifier getShadowedClassifier(EClassifier shadow) {
        if (shadow instanceof Typedef) {
        	Typedef typedef = (Typedef) shadow;        	
            return typedef.getBase(); 
        }
        
        return null;
	}
        
    
	@Override
	protected EClassifier findShadowClass(EClassifier type) {
		if (!hasAdditionalFeatures()) {
			return null;
		}
		
        EPackage pkg = getAdditionalFeaturesPackage();
		if (type instanceof TupleType) {
			for (EClassifier shadowedClassifier : ShadowClassAdapter.getShadowToBaseMap(pkg).keySet()) {
				if (TypeUtil.exactTypeMatch(getEnvironment(), type, shadowedClassifier)) {
					return ShadowClassAdapter.getShadowToBaseMap(pkg).get(shadowedClassifier);
				}
			}
		}
        return ShadowClassAdapter.getShadowToBaseMap(pkg).get(type);
	}
	
	
	private static class ShadowClassAdapter extends AdapterImpl {
		
		final Map<EClassifier, EClassifier> fShadowToBase = new LinkedHashMap<EClassifier, EClassifier>();

		Map<EClassifier, EClassifier> getShadowToBaseMap() {
			return fShadowToBase;
		}
		
		@Override
		public boolean isAdapterForType(Object type) {
			if(ShadowClassAdapter.class.equals(type)) {
				return true;
			}
			return super.isAdapterForType(type);
		}
		
		public static Map<EClassifier, EClassifier> getShadowToBaseMap(EPackage pkg) {
			Adapter adapter = EcoreUtil.getAdapter(pkg.eAdapters(), ShadowClassAdapter.class);
			if (adapter instanceof ShadowClassAdapter) {
				return ((ShadowClassAdapter) adapter).getShadowToBaseMap();
			}

			ShadowClassAdapter shadowClassAdapter = new ShadowClassAdapter();
			pkg.eAdapters().add(shadowClassAdapter);
			return shadowClassAdapter.getShadowToBaseMap();
		}
	}
	
}
