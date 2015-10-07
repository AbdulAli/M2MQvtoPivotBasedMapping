/*******************************************************************************
 * Copyright (c) 2007, 2014 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.common.util.LineNumberProvider;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingModuleCS;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.CatchExp;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.ecore.Variable;
import org.eclipse.ocl.utilities.ASTNode;

public class ASTBindingHelper {
	
	public static <T> T getAST(CSTNode node, Class<T> type) {
		if(type == null) {
			throw new IllegalArgumentException();
		}
		
		Object astObj = node.getAst();
		return (type.isInstance(astObj)) ? type.cast(astObj) : null;
	}	
	
	public static void createModuleSourceBinding(EObject target, URI sourceURI, LineNumberProvider lineNumberProvider) {
		target.eAdapters().add(new ModuleSourceAdapter(sourceURI, lineNumberProvider));
	}
				
	public static IModuleSourceInfo getModuleSourceBinding(Module astModule) {
		return (IModuleSourceInfo)EcoreUtil.getExistingAdapter(astModule, ModuleSourceAdapter.class);
	}
		
	public static void createModuleBinding(MappingModuleCS cstModule, Module astModule, QvtOperationalModuleEnv env, URI unitURI) {
		ASTAdapter<ASTNode> astAdapter = new ModuleASTAdapter(cstModule, astModule, env, unitURI);
		astModule.eAdapters().add(astAdapter);	
		cstModule.eAdapters().add(astAdapter);		
	}
	
    public static URI resolveModuleFile(EObject cstModule) {
        ModuleASTAdapter moduleASTAdapter = getModuleASTAdapter(cstModule);
        if (moduleASTAdapter != null) {
            return moduleASTAdapter.getModuleFile();           
        }
        return null;
    }

    public static QvtOperationalModuleEnv resolveModuleEnvironment(EObject eObj) {
        ModuleASTAdapter moduleASTAdapter = getModuleASTAdapter(eObj);
        if (moduleASTAdapter != null) {
            return (QvtOperationalModuleEnv) moduleASTAdapter.getEnvironment();           
        }
		List<ASTAdapter<Object>> adapters = getASTBindings(eObj);
		for (ASTAdapter<Object> nextAdapter : adapters) {
			if(nextAdapter.getEnvironment() instanceof QvtOperationalModuleEnv) {
				return (QvtOperationalModuleEnv) nextAdapter.getEnvironment();
			}
		}
        return null;
    }

	public static void createCST2ASTBinding(CSTNode cstNode, ASTNode astNode) {
		createCST2ASTBinding(cstNode, astNode, true, null);
	}
	
	public static void createCST2ASTBinding(
			CSTNode cstNode,
			ASTNode astNode,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env) {
		createCST2ASTBinding(cstNode, astNode, true, env);
	}
	
	public static void createCST2ASTBinding(
			CSTNode cstNode,
			ASTNode astNode,
			boolean isBidirectional,
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env) {
		ASTAdapter<ASTNode> astAdapter = new ASTAdapter<ASTNode>(cstNode, astNode, (EcoreEnvironment)env);
		if(isBidirectional) {
			astNode.eAdapters().add(astAdapter);
		}
		cstNode.eAdapters().add(astAdapter);
	}
	
	public static <AST> void createCST2ASTBindingUnidirectional(CSTNode cstNode, AST astNode) {
		ASTAdapter<AST> astAdapter = new ASTAdapter<AST>(cstNode, astNode, null);
		cstNode.eAdapters().add(astAdapter);
	}	

	public static ASTNode resolveASTNode(CSTNode cstNode) {
		return resolveASTNode(cstNode, ASTNode.class);
	}
	
	public static <AST> AST resolveASTNode(CSTNode cstNode, Class<AST> astType) {
		return firstASTNodeOfType(getASTBindings(cstNode), astType);
	}
	
	public static final ASTNode resolveEnclosingASTNode(CSTNode cstNode) {
	    while (cstNode != null) {
	        ASTNode astNode = resolveASTNode(cstNode);
	        if (astNode != null) {
	            return astNode;
	        }
	        cstNode = (CSTNode) cstNode.eContainer();
	    };
	    return null;
	}
	
	public static <AST extends EObject, T extends CSTNode> T resolveCSTNode(AST astNode, Class<T> cstType) {
		return firstCSTNodeOfType(getASTBindings(astNode), cstType);
	}
	
	public static CSTNode resolveCSTNode(ASTNode astNode) {
		return resolveCSTNode(astNode, CSTNode.class);
	}
	
	public static EcoreEnvironment resolveEnvironment(ASTNode astNode) {
		EObject processedNode = astNode;
		EcoreEnvironment env = localResolveEnvironment(astNode);
		
		while(env == null && processedNode.eContainer() != null) {
			processedNode = processedNode.eContainer();
			if (processedNode instanceof ASTNode) {
				env = localResolveEnvironment((ASTNode) processedNode);
			}
		}
				
		return env;
	}
	
	private static <A> EcoreEnvironment localResolveEnvironment(ASTNode astNode) {
		List<ASTAdapter<A>> adapters = getASTBindings(astNode);
		for (ASTAdapter<A> nextAdapter : adapters) {
			if(nextAdapter.getEnvironment() != null) {
				return nextAdapter.getEnvironment();
			}
		}
		return null;
	}
	
	private static <A, T extends A> T firstASTNodeOfType(List<ASTAdapter<A>> objects, Class<T> type) {
		for (ASTAdapter<A> nextAST : objects) {
			if(type.isInstance(nextAST.getASTNode())) {
				return type.cast(nextAST.getASTNode());
			}
		}
		return null;
	}
	
	private static <A, T extends CSTNode> T firstCSTNodeOfType(List<ASTAdapter<A>> objects, Class<T> type) {
		for (ASTAdapter<A> nextCST : objects) {
			if(type.isInstance(nextCST.getCSTNode())) {
				return type.cast(nextCST.getCSTNode());
			}
		}
		return null;
	}
	
	private static ModuleASTAdapter getModuleASTAdapter(EObject target) {
        List<ModuleASTAdapter> adapters = getASTBindings(target, ModuleASTAdapter.class);
        if (!adapters.isEmpty()) {
            ModuleASTAdapter moduleAdapter = (ModuleASTAdapter) adapters.get(0);
            return moduleAdapter;
        }
        return null;
	}
	
	@SuppressWarnings("unchecked")
	static <AST> List<ASTAdapter<AST>> getASTBindings(EObject target) {
		return getASTBindings(target, ASTAdapter.class);
	}
	
	static <A, T extends ASTAdapter<A>> List<T> getASTBindings(EObject target, Class<T> adapterType) {
		List<T> result = Collections.emptyList();
		
		for (Adapter nextAdapter : target.eAdapters()) {
			if(adapterType.isInstance(nextAdapter)) {
				if(result.isEmpty()) {
					result = new ArrayList<T>(3);
				}
				
				result.add(adapterType.cast(nextAdapter));
			}
		}

		return result;
	}

	private static class ASTAdapter<A> extends AdapterImpl {
		private A fAstNode;
		private CSTNode fCstNode;		
		private EcoreEnvironment fEnv;		
		
		
		ASTAdapter(CSTNode cstNode, A astNode, EcoreEnvironment env) {
			if(astNode == null || cstNode == null) {
				throw new IllegalArgumentException();
			}
			
			this.fAstNode = astNode;
			this.fCstNode = cstNode;
			this.fEnv = env;
		}
		
		public A getASTNode() {
			return fAstNode;
		}
		
		public CSTNode getCSTNode() {
			return fCstNode;
		}
		
		public EcoreEnvironment getEnvironment() {
			return fEnv;
		}		
	}
	
	private static class ModuleASTAdapter extends ASTAdapter<ASTNode> {
		
		private URI uri;
		
		protected ModuleASTAdapter(CSTNode cstNode, ASTNode astNode,
				QvtOperationalModuleEnv env, URI unitURI) {
			super(cstNode, astNode, env);
			
			this.uri = unitURI;
		}

		public URI getModuleFile() {
			return uri;
		}
	}

	private static class ModuleSourceAdapter extends AdapterImpl implements IModuleSourceInfo {
		private URI fSourceURI;
		private LineNumberProvider fLineNumProvider;
		
		protected ModuleSourceAdapter(URI sourceURI, LineNumberProvider lineNumberProvider) {
			if(sourceURI == null || lineNumberProvider == null) {
				throw new IllegalArgumentException();
			}
			fSourceURI = sourceURI;
			fLineNumProvider = lineNumberProvider;
		}
		
		public URI getSourceURI() {
			return fSourceURI;
		}
				
		public LineNumberProvider getLineNumberProvider() {
			return fLineNumProvider; 
		}
		
		@Override
		public boolean isAdapterForType(Object type) {
			return type == ModuleSourceAdapter.class;
		}
	}
	
	public static void setEnvironment(EObject topLevelElement, EcoreEnvironment elementEnv) {
		Adapter adapter = EcoreUtil.getAdapter(topLevelElement.eAdapters(), elementEnv.getClass());
		if(adapter != null) {
			topLevelElement.eAdapters().remove(adapter);
		}
		
		topLevelElement.eAdapters().add(new EnvAdapter(elementEnv));
	}
	
	public static <T extends EcoreEnvironment> T getEnvironment(EObject topLevelElement, Class<T> type) {
		Adapter adapter = EcoreUtil.getAdapter(topLevelElement.eAdapters(), type);
		if(adapter instanceof EnvAdapter) {
			EnvAdapter envAdapter = (EnvAdapter) adapter;
			return envAdapter.getEnvironment(type);
		}	

		return null;
	}
		
	private static class EnvAdapter extends AdapterImpl {
		
		private EcoreEnvironment fEnv;

		EnvAdapter(EcoreEnvironment env) {
			assert env != null;
			fEnv = env;
		}

		@Override
		public boolean isAdapterForType(Object type) {
			if(type instanceof Class<?>) {
				Class<?> clazz = (Class<?>) type;
				return clazz.isInstance(fEnv);
			}
			
			return false;
		}
		
		<T extends EcoreEnvironment> T getEnvironment(Class<T> type) {
			if(isAdapterForType(type)) {
				return type.cast(fEnv);
			}
			
			return null;
		}
	}
	
	public static void setCatchVariable(CatchExp catchExp, Variable catchVar) {
		Adapter adapter = EcoreUtil.getAdapter(catchExp.eAdapters(), CatchVariableAdapter.class);
		if(adapter != null) {
			catchExp.eAdapters().remove(adapter);
		}
		
		catchExp.eAdapters().add(new CatchVariableAdapter(catchVar));
	}
	
	public static Variable getCatchVariable(CatchExp catchExp) {
		Adapter adapter = EcoreUtil.getAdapter(catchExp.eAdapters(), CatchVariableAdapter.class);
		if(adapter instanceof CatchVariableAdapter) {
			return ((CatchVariableAdapter) adapter).getCatchVar();
		}
		return null;
	}
	
	private static class CatchVariableAdapter extends AdapterImpl {
		
		private final Variable catchVariable;
		
		CatchVariableAdapter(Variable var) {
			catchVariable = var;
		}
		
		Variable getCatchVar() {
			return catchVariable;
		}
		
		@Override
		public boolean isAdapterForType(Object type) {
			return type == CatchVariableAdapter.class;
		}
	}
}
