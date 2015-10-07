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
 *     Alex Paperno - bug 424584
 *     Christopher Gerking - bug 446375
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnv;
import org.eclipse.m2m.internal.qvt.oml.cst.MappingExtensionKindCS;
import org.eclipse.m2m.internal.qvt.oml.expressions.DirectionKind;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.VarParameter;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.UMLReflection;

class MappingExtensionHelper {

	private static final class DisjunctValidator extends ExtensionValidator {
		private DisjunctValidator(MappingOperation operation,
				List<MappingOperation> extendedOperations,
				MappingExtensionKindCS extensionKind) {
			super(operation, extendedOperations, extensionKind);
		}

		@Override
		List<MappingSourceReference> getMappingSourceRefs() {
			return getSrcAdapter().getDisjunctReferences();
		}

		@Override
		boolean isConformantForExtension(MappingOperation extendingOper, MappingOperation extendedOper, QvtOperationalEnv env) {
			return isDisjunctCompatible(extendingOper, extendedOper, env); 
		}

		@Override
		boolean validate(QvtOperationalEnv env) {
			boolean result = true; 
			MappingBody body = (MappingBody)fOperation.getBody();

			if (body != null) {
				boolean isEmptyBody = body.getContent().isEmpty() ||
						(body.getContent().size() == 1
							&& body.getContent().get(0).getStartPosition() == body.getStartPosition()
							&& body.getContent().get(0).getEndPosition() == body.getEndPosition());
				boolean nonEmptyMapping = !isEmptyBody
						|| !body.getInitSection().isEmpty()
						|| !body.getEndSection().isEmpty();
				
				if(nonEmptyMapping) {
					env.reportWarning(NLS.bind(ValidationMessages.MappingExtension_disjunctingMappingBodyNotExecuted, 
						QvtOperationalParserUtil.safeGetMappingQualifiedName(env, fOperation)),
						body.getStartPosition(), body.getEndPosition());
				}
			}
			
			result &= reportInvalidExtensionsInDisjunctingMapping(env, fOperation.getInherited(), MappingExtensionKindCS.INHERITS);
			result &= reportInvalidExtensionsInDisjunctingMapping(env, fOperation.getMerged(), MappingExtensionKindCS.MERGES);
			
			result &= reportInvalidOutParameters(env);
			
			return result && super.validate(env);
		}

		private boolean reportInvalidOutParameters(QvtOperationalEnv env) {
			boolean result = true;
			int pos = 0;
			for (MappingOperation extended : fOperation.getDisjunct()) {
				Iterator<EParameter> itParams = fOperation.getEParameters().iterator();
				Iterator<EParameter> itExtendedParams = extended.getEParameters().iterator();
				while (itParams.hasNext()) {

					MappingParameter mappingParam = (MappingParameter) itParams.next();
					if (mappingParam.getKind() != DirectionKind.OUT) {
						continue;
					}

					if (!itExtendedParams.hasNext()) {
						break;
					}

					if (itExtendedParams.next().getEType() != mappingParam.getEType()) {
						result = false;
						ExtensionSourceRefAdapter adapter = getSrcAdapter();
						if (adapter != null) {
							int startOffset = extended.getStartPosition();
							int endOffset = extended.getEndPosition();
							MappingSourceReference ref = safeGetSourceRef(adapter.getDisjunctReferences(), pos);
							if (ref != null) {
								startOffset = ref.getStartOffset();
								endOffset = ref.getEndOffset();
							}

							env.reportError(
									NLS.bind(
										ValidationMessages.MappingExtension_illegalOutParamDisjunctingMapping,
										new Object[] { 
												QvtOperationalParserUtil.safeGetMappingQualifiedName(env, extended),
												mappingParam.getName(),
												QvtOperationalParserUtil.safeGetMappingQualifiedName(env, fOperation)
										}
									),
									startOffset, endOffset);
						}
					}
				}
				
				pos++;
			}
			return result;
		}

		private boolean reportInvalidExtensionsInDisjunctingMapping(QvtOperationalEnv env, EList<MappingOperation> extendedOperations, MappingExtensionKindCS kind) {
			boolean result = true;
			int pos = 0;
			for (MappingOperation extended : extendedOperations) {
				result = false;
				ExtensionSourceRefAdapter adapter = getSrcAdapter();				
				if(adapter != null) {
					int startOffset = extended.getStartPosition();
					int endOffset = extended.getEndPosition();							
					MappingSourceReference ref = null;
					if(kind == MappingExtensionKindCS.INHERITS) {
						ref = safeGetSourceRef(adapter.getInheritReferences(), pos++);
					} else if(kind == MappingExtensionKindCS.MERGES) {
						ref = safeGetSourceRef(adapter.getMergeReferences(), pos++);
					}
					
					if(ref != null) {
						startOffset = ref.getStartOffset();
						endOffset = ref.getEndOffset();
					}
					
					env.reportError(NLS.bind(ValidationMessages.MappingExtension_illegalExtensionKindOnDisjunctingMapping,
						new Object[] {
							QvtOperationalParserUtil.safeGetMappingQualifiedName(env, fOperation),
							kind.getName(),
							QvtOperationalParserUtil.safeGetMappingQualifiedName(env, extended),
						}), startOffset, endOffset);
				}
			}
			
			return result;
		}
	}

	private static class MappingSourceReference {
		private int startOffset;		
		private int endOffset;		

		private MappingSourceReference(int startOffset, int endOffset) {
			this.startOffset = startOffset;
			this.endOffset = endOffset;			
		}		
		
		public int getStartOffset() { return startOffset; }
		public int getEndOffset() { return endOffset; }
	}
	
	private static class ExtensionSourceRefAdapter extends AdapterImpl {
		List<MappingSourceReference> inheritRefs;
		List<MappingSourceReference> disjunctRefs;
		List<MappingSourceReference> mergeRefs;
				
		ExtensionSourceRefAdapter() {
			super();
		}
		
		void addDisjunct(MappingOperation mapping, CSTNode sourceRef) {
			if(disjunctRefs == null) {
				disjunctRefs = new ArrayList<MappingSourceReference>();
			}
			addRef(mapping, sourceRef, disjunctRefs);
		}

		void addInherit(MappingOperation mapping, CSTNode sourceRef) {
			if(inheritRefs == null) {
				inheritRefs = new ArrayList<MappingSourceReference>();
			}
			addRef(mapping, sourceRef, inheritRefs);
		}

		void addMerge(MappingOperation mapping, CSTNode sourceRef) {
			if(mergeRefs == null) {
				mergeRefs = new ArrayList<MappingSourceReference>();
			}
			addRef(mapping, sourceRef, mergeRefs);
		}		
	
		@Override
		public boolean isAdapterForType(Object type) {
			if(type instanceof Class<?>) {
				return ExtensionSourceRefAdapter.class.isAssignableFrom((Class<?>)type);
			}
			return super.isAdapterForType(ExtensionSourceRefAdapter.class);
		}
		
		static void addRef(MappingOperation mapping, CSTNode sourceRef, List<MappingSourceReference> refList) {
			refList.add(new MappingSourceReference(sourceRef.getStartOffset(), sourceRef.getEndOffset()));			
		}

		public List<MappingSourceReference> getInheritReferences() {
			return inheritRefs;
		}
		
		public List<MappingSourceReference> getDisjunctReferences() {
			return disjunctRefs;
		}

		public List<MappingSourceReference> getMergeReferences() {
			return mergeRefs;
		}
	}
	
	private static abstract class ExtensionValidator {
		MappingOperation fOperation;
		List<MappingOperation> fExtendedOpers;
		ExtensionSourceRefAdapter fAdapter;
		boolean shouldUseSourceAdapter;
		MappingExtensionKindCS fExtensionKind;
		
		ExtensionValidator(MappingOperation operation, List<MappingOperation> extendedOperations, MappingExtensionKindCS extensionKind) {
			fOperation = operation;
			fExtendedOpers = extendedOperations;
			fExtensionKind = extensionKind;
			shouldUseSourceAdapter = true;			
		}
		
		ExtensionSourceRefAdapter getSrcAdapter() {
			if(fAdapter == null) {
				fAdapter = MappingExtensionHelper.getSourceRefAdapter(fOperation);				
				if(getMappingSourceRefs().size() != fExtendedOpers.size()) {
					// TODO - inconsistent mapping source reference adapter constructed by the parser, use trace, etc. 
					// Resolved by problem attached to the mapping operation AST
					shouldUseSourceAdapter = false;					
				}
			}
			
			return shouldUseSourceAdapter ? fAdapter : null;
		}

		boolean validate(QvtOperationalEnv env) {
			boolean isValid = true;			
			for(int i = 0; i < fExtendedOpers.size(); i++) {
				MappingOperation extended = fExtendedOpers.get(i);
				boolean isNonConformantSignature = !isConformantForExtension(fOperation, extended, env);
				isValid &= !isNonConformantSignature;

				if(isNonConformantSignature) {
					int startOffset = fOperation.getStartPosition();
					int endOffset = fOperation.getEndPosition();
					ExtensionSourceRefAdapter adapter = getSrcAdapter();
					if(adapter != null) {
						MappingSourceReference ref = safeGetSourceRef(i);
						if(ref != null) {
							startOffset = ref.getStartOffset();
							endOffset = ref.getEndOffset();
						}
					}

					String errMessage = NLS.bind(ValidationMessages.MappingExtension_nonConformantSignatureForMappingExtension, new Object[] { 
						QvtOperationalParserUtil.safeGetMappingQualifiedName(env, extended), fExtensionKind,						
						QvtOperationalParserUtil.safeGetMappingQualifiedName(env, fOperation)
						 
					});
					env.reportError(errMessage, startOffset, endOffset);
				}		
			}
			
			return isValid;
		}

		static MappingSourceReference safeGetSourceRef(List<MappingSourceReference> refList, int pos) {
			return (pos >= refList.size()) ? null : refList.get(pos); 
		}
		
		MappingSourceReference safeGetSourceRef(int pos) {
			return safeGetSourceRef(getMappingSourceRefs(), pos); 
		}
		
		abstract List<MappingSourceReference> getMappingSourceRefs();
		abstract boolean isConformantForExtension(MappingOperation extendingOper, MappingOperation extendedOper, QvtOperationalEnv env);		
	}
	
	
	private static ExtensionSourceRefAdapter attachSourceRefAdapter(MappingOperation mapping) {
		ExtensionSourceRefAdapter adapter = new ExtensionSourceRefAdapter();
		mapping.eAdapters().add(adapter);
		return adapter;
	}
	
	public static void bind2SourceElement(MappingOperation extendedOperation, CSTNode sourceRefElement, MappingExtensionKindCS extensionKindCS) {
		ExtensionSourceRefAdapter adapter = getSourceRefAdapter(extendedOperation);
		if(adapter == null) {
			adapter = attachSourceRefAdapter(extendedOperation);
		}
		
		if(extensionKindCS == MappingExtensionKindCS.INHERITS) {
			adapter.addInherit(extendedOperation, sourceRefElement);
		} else if(extensionKindCS == MappingExtensionKindCS.DISJUNCTS) {
			adapter.addDisjunct(extendedOperation, sourceRefElement);
		} else if(extensionKindCS == MappingExtensionKindCS.MERGES) {
			adapter.addMerge(extendedOperation, sourceRefElement);
		} else {
			assert false : "Uknown extension kind"; //$NON-NLS-1$
		}
	}
	
	private static ExtensionSourceRefAdapter getSourceRefAdapter(MappingOperation mapping) {
		return (ExtensionSourceRefAdapter)EcoreUtil.getExistingAdapter(mapping, ExtensionSourceRefAdapter.class);
	}
		
	public static boolean validate(MappingOperation operation, QvtOperationalEnv env) {
		boolean isValid = true;
		if(!operation.getInherited().isEmpty()) {
			isValid &= new ExtensionValidator(operation, operation.getInherited(), MappingExtensionKindCS.INHERITS) {
				@Override
				List<MappingSourceReference> getMappingSourceRefs() {
					return getSrcAdapter().getInheritReferences();
				}
				@Override
				boolean isConformantForExtension(MappingOperation extendingOper, MappingOperation extendedOper, QvtOperationalEnv env) {
					return isInheritCompatible(extendingOper, extendedOper, env);
				}
			}.validate(env);
		}
		
		if(!operation.getDisjunct().isEmpty()) {
			isValid &= new DisjunctValidator(operation, operation.getDisjunct(), MappingExtensionKindCS.DISJUNCTS).validate(env);
		}

		if(!operation.getMerged().isEmpty()) {
			isValid &= new ExtensionValidator(operation, operation.getMerged(), MappingExtensionKindCS.MERGES) {
				@Override
				List<MappingSourceReference> getMappingSourceRefs() {
					return getSrcAdapter().getMergeReferences();
				}
				@Override
				boolean isConformantForExtension(MappingOperation extendingOper, MappingOperation extendedOper, QvtOperationalEnv env) {
					return isMergeCompatible(extendingOper, extendedOper, env);
				}				
			}.validate(env);
		}

		return isValid;
	}
		
	public static boolean isMergeCompatible(MappingOperation mergingOperation, MappingOperation mergedOperation, QvtOperationalEnv env) {
		return isCaller2CalleeCompatible(mergedOperation, mergingOperation, env);
	}
	
	public static boolean isDisjunctCompatible(MappingOperation disjunctingOperation, MappingOperation disjunctedOperation, QvtOperationalEnv env) {
		return isCaller2CalleeCompatible(disjunctingOperation, disjunctedOperation, env);
	}
	
	public static boolean isInheritCompatible(MappingOperation inheritingOperation, MappingOperation intheritedOperation, QvtOperationalEnv env) {
		return isCaller2CalleeCompatible(intheritedOperation, inheritingOperation, env);
	}
	
	public static Collection<MappingOperation> checkForExtensionCycle(MappingOperation extendingOperation) {
		// TODO - investigate possible rules for checking cyclic reused mapping references
		return Collections.emptyList();
	}
	
	static boolean isCaller2CalleeCompatible(MappingOperation extendingOper, MappingOperation extendedOper, QvtOperationalEnv env) {
 		if(extendingOper.getEParameters().size() != extendedOper.getEParameters().size()) {
			return false;
		}
		
		EClassifier ctx1 = QvtOperationalParserUtil.getContextualType(extendingOper);
		EClassifier ctx2 = QvtOperationalParserUtil.getContextualType(extendedOper);		
		if(ctx1 == null || ctx2 == null) {
			if(ctx1 != ctx2) {
				return false;
			}
		} else if(!isAssignableTo(ctx2, ctx1, env)) {
			return false;
		}		
		
		if(!isParameterListCaller2CalleeCompatible(extendingOper.getEParameters(), extendedOper.getEParameters(), env)) {
			return false;
		}
		
		if(!isParameterListCaller2CalleeCompatible(extendingOper.getResult(), extendedOper.getResult(), env)) {
			return false;
		}
		
		if(extendingOper.getEType() == null || extendedOper.getEType() == null) {
			return false;
		}
		
		return extendingOper.getResult().size() == 1 || isAssignableTo(extendingOper.getEType(), extendedOper.getEType(), env);
	}
		
	private static boolean isParameterListCaller2CalleeCompatible(EList<? extends EParameter> callerParams, EList<? extends EParameter> calleeParams, QvtOperationalEnv env) {
		if(callerParams.size() != calleeParams.size()) {
			return false;
		}
		
		for(int i = 0; i < calleeParams.size(); i++) {
			EParameter callerPar = callerParams.get(i);			
			EParameter calleePar = calleeParams.get(i);

			boolean isAssignable = isAssignableTo(calleePar.getEType(), callerPar.getEType(), env);
			boolean isDirectionOK = true;
			if(calleePar instanceof VarParameter) {
				if(callerPar instanceof VarParameter) {
					isDirectionOK = isDirectionKindCaller2CalleeCompatible(
							((VarParameter)callerPar).getKind(), ((VarParameter)calleePar).getKind());
				} else {
					return false;
				}
			}
			
			if(!isAssignable || !isDirectionOK) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isDirectionKindCaller2CalleeCompatible(DirectionKind callerKind, DirectionKind calleeKind) {
		if(callerKind == calleeKind) {
			return true;
		}

		if(callerKind == DirectionKind.IN) {
			return calleeKind == DirectionKind.INOUT || calleeKind == DirectionKind.IN;			
		} else if(callerKind == DirectionKind.INOUT) {
			return calleeKind == DirectionKind.INOUT;
		}
		return false;
	}
	
	private static boolean isAssignableTo(EClassifier sourceType, EClassifier targetType, QvtOperationalEnv env) {
		return (TypeUtil.getRelationship(env, sourceType, targetType) & UMLReflection.SUBTYPE) != 0;   
	}
}
