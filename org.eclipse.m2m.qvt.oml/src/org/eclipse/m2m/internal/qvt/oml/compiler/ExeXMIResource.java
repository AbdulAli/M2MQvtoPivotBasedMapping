/*******************************************************************************
 * Copyright (c) 2009 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.IModuleSourceInfo;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalStdLibrary;
import org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMIExtensionEncoder.CorruptedExtensionData;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.xml.sax.helpers.DefaultHandler;

public class ExeXMIResource extends XMIResourceImpl implements Resource {
	
	public static class Factory implements Resource.Factory {

		public Factory() {
			super();
		}

		public Resource createResource(URI uri) {
			return new ExeXMIResource(uri);
		}
	}
	
	
	private class _XMISave extends XMISaveImpl {
		
		private ExeXMIExtensionEncoder fExtensionDecoder;
		
		private _XMISave(XMLHelper helper) {
			super(helper);
			// Note : set here what is the current decoder to use
			fExtensionDecoder = new DefaultExtensionDecoder();
		}

		public Object writeTopObjects(List<? extends EObject> contents)
		  {
		    if (!toDOM)
		    {
		      doc.startElement(XMI_TAG_NS);
		      Object mark = doc.mark();
		      
		      // Write QVTO debugging info extension data
		      writeExtension();
		      //

		      for (int i = 0, size = contents.size(); i < size; i++)
		      {
		        EObject top = contents.get(i);
		        EClass eClass = top.eClass();
		        if (extendedMetaData == null || featureTable.getDocumentRoot(eClass.getEPackage()) != eClass)
		        {
		          String name = helper.getQName(eClass);
		          doc.startElement(name);
		          root = top;
		          saveElementID(top);
		        }
		        else
		        {
		          doc.startElement(null);
		          root = top;
		          saveFeatures(top);
		          doc.addLine();
		        }
		      }

		      doc.endElement();
		      return mark;
		    }
		    else
		    {
		      // create dummy documentElement
		      currentNode = document.createElementNS(XMIResource.XMI_URI, XMI_TAG_NS);
		      document.appendChild(currentNode);
		      if (!contents.isEmpty())
		      {
		        EObject top = contents.get(0);
		        EClass eClass = top.eClass();
		        helper.populateNameInfo(nameInfo, eClass);
		        if (extendedMetaData == null || extendedMetaData.getDocumentRoot(eClass.getEPackage()) != eClass)
		        {
		          currentNode = currentNode.appendChild(document.createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName()));
		          handler.recordValues(currentNode, null, null, top);
		          root = top;
		          saveElementID(top);
		        }
		        else
		        {
		          root = top;
		          currentNode = currentNode.appendChild(document.createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName()));
		          saveFeatures(top);
		        }
		      }
		      return null;
		    }
		  }

		@Override
		protected boolean writeTopElements(EObject top) {			
			if(this.xmlResource.getContents().size() == 1) {
				 writeExtension();
			}
			
			return super.writeTopElements(top);
		}

		private void writeExtension() {
			doc.startElement(XMI_EXTENSION_ELEMENT);		 
			 doc.addAttribute(EXTENDER_ATTR, EXTENDER_ATTR_VALUE);
			 
			 URI sourceURI = getSourceURI();
			 if(sourceURI != null) {
				 doc.startElement(SOURCE_URI_ELEMENT);
				 doc.endContentElement(sourceURI.toString());
			 }
			 
			 doc.startElement(OFFSETS_ELEMENT);
			 doc.endContentElement(fExtensionDecoder.encodeASTNodeOffsets(this.xmlResource));
			 
			 doc.startElement(LINE_BREAKS_ELEMENT);
			 doc.endContentElement(fExtensionDecoder.encodeLineBreakOffsets(this.xmlResource));
			 doc.endElement();
		}

		private URI getSourceURI() {
			IModuleSourceInfo srcInfo = getSourceInfo(); 
			if(srcInfo != null) {
				URI sourceURI = srcInfo.getSourceURI();							
				return sourceURI.deresolve(this.xmlResource.getURI());
			}
			return null;
		}
	}
	
	private class _XMIHandler extends SAXXMIHandler {
		
		private ExeXMIExtensionEncoder fExtensionDecoder;
		private URI fSourceURI;
		private String fLineBreakOffsets;
		private String fEncodedOffsets;
		private int fExtensionColumnNum;
		private int fExtensionLineNum;

		_XMIHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
			super(xmiResource, helper, options);
		}

		protected void processElement(String name, String prefix, String localName) {
			if (XMI_EXTENSION.equals(localName) && XMIResource.XMI_URI.equals(helper.getURI(prefix))) {
				if (attribs != null && EXTENDER_ATTR_VALUE.equals(attribs.getValue(EXTENDER_ATTR))) {
					fExtensionColumnNum = getColumnNumber();
					fExtensionLineNum = getLineNumber();
					// Note:
					// use the initial decoder provided, here we should decide based on an version id of
					// the extension, which decoder to use, WBN
					fExtensionDecoder = new DefaultExtensionDecoder();
					
					types.push(QVT_EXTENSION_TYPE);
				} else {
					types.push(ERROR_TYPE);
				}
			} else if (types.peek() == QVT_EXTENSION_TYPE) {
				text = new StringBuffer();
			} else {
				super.processElement(name, prefix, localName);
			}
		}

		@Override
		public void endElement(String uri, String localName, String name) {
			if (types.peek() == QVT_EXTENSION_TYPE) {
				if (name.equals(SOURCE_URI_ELEMENT)) {
					String sourceURIStr = text.toString();
					URI sourceURI = null;
					try {
						if (sourceURIStr != null) {
							sourceURI = URI.createURI(sourceURIStr);
							URI qvtoXmiURI = this.resourceURI;
							fSourceURI = sourceURI.resolve(qvtoXmiURI);
						}
					} catch (IllegalArgumentException e) {
						// do nothing sourceURI not set						
					}
				} else if (name.equals(OFFSETS_ELEMENT)) {
					if (text != null) {
						fEncodedOffsets = text.toString();						
					}
				} else if (name.equals(LINE_BREAKS_ELEMENT)) {					
					fLineBreakOffsets = text.toString();
				} else if (name.equals(XMI_EXTENSION_ELEMENT)) {
					types.pop();
				}
			} else {
				super.endElement(uri, localName, name);
			}
		}
		
		@Override
		public void endDocument() {
			if(fEncodedOffsets != null) {				
				try {
					fExtensionDecoder.decodeASTNodeOffsets(this.xmlResource, fEncodedOffsets);
				} catch(CorruptedExtensionData e) {
					this.xmlResource.getWarnings().add(createDiagnostic(e.toString()));
				}
				fEncodedOffsets = null;
			}
			
			if(fLineBreakOffsets != null && fSourceURI != null) {
				try {
					int[] lineBreakOffsets = fExtensionDecoder.decodeLineBreakOffsets(fLineBreakOffsets);		
					addSourceInfo(fSourceURI, lineBreakOffsets);
				} catch(CorruptedExtensionData e) {
					this.xmlResource.getWarnings().add(createDiagnostic(e.toString()));
				}
				fLineBreakOffsets = null;
			}

			super.endDocument();
		}
		
		private Diagnostic createDiagnostic(final String message) {
			return new Diagnostic() {
				
				public String getMessage() {
					return message;
				}
				
				public String getLocation() {
					return xmlResource.getURI().toString();
				}
				
				public int getLine() {
					return fExtensionLineNum;
				}
				
				public int getColumn() {
					return fExtensionColumnNum;
				}
			};
		}
	}
	
	
	private class _XMIHelper extends XMIHelperImpl {

		_XMIHelper(XMLResource resource) {
			super(resource);
		}

		@Override
		public String getHREF(EObject obj) {
			Resource objResource = obj.eResource();
			if (objResource != null && this.resource != objResource) {
				URI savedURI = getPackage2HREFMap().get(objResource.getURI());
				if (savedURI != null) {
					URI href = savedURI.appendFragment(objResource.getURIFragment(obj));
					return href.toString();
				}
			}

			return super.getHREF(obj);
		}
	}

	
	private static final String QVT_EXTENSION_TYPE = "qvtoExtension"; //$NON-NLS-1$	
	
	private static final String XMI_EXTENSION_ELEMENT = "xmi:Extension"; //$NON-NLS-1$	
	
	private static final String EXTENDER_ATTR_VALUE = ExpressionsPackage.eNS_URI + "/debugInfo"; //$NON-NLS-1$	
	
	private static final String EXTENDER_ATTR = "extender"; //$NON-NLS-1$	
	
	private static final String OFFSETS_ELEMENT = "offsets"; //$NON-NLS-1$	
	
	private static final String LINE_BREAKS_ELEMENT = "lineBreaks"; //$NON-NLS-1$	
	
	private static final String SOURCE_URI_ELEMENT = "sourceURI"; //$NON-NLS-1$
		
	// instance fields

	private Map<URI, URI> fPackage2HREF;

	
	public ExeXMIResource(URI uri) {
		super(uri);
		// init stlib -> register package
		QvtOperationalStdLibrary.INSTANCE.getStdLibModule();

		setEncoding("UTF-8"); //$NON-NLS-1$

		getDefaultSaveOptions().put(
				XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
		getDefaultSaveOptions().put(XMLResource.OPTION_LINE_WIDTH, 80);
		getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER,
				new URIHandlerImpl.PlatformSchemeAware());
	}
	


	@Override
	protected boolean useIDs() {
		return eObjectToIDMap != null || idToEObjectMap != null;
	}

	@Override
	protected XMLSave createXMLSave() {
		return new _XMISave(createXMLHelper());
	}

	@Override
	protected XMLLoad createXMLLoad() {
		return new XMILoadImpl(createXMLHelper()) {
			  @Override
			  protected DefaultHandler makeDefaultHandler()
			  {
			    return new _XMIHandler(resource, (XMLHelper)helper, options);
			  }			
		};
	}

	protected XMLHelper createXMLHelper() {
		return new _XMIHelper(this);
	}

	private Map<URI, URI> getPackage2HREFMap() {
		if (fPackage2HREF == null) {
			if (resourceSet != null && resourceSet.getPackageRegistry() != null) {
				Registry packageRegistry = resourceSet.getPackageRegistry();
				for (Map.Entry<String, Object> entry : packageRegistry
						.entrySet()) {
					Object value = entry.getValue();
					if (value instanceof EPackage) {
						EPackage ePackage = (EPackage) value;
						String nsURI = ePackage.getNsURI();
						Resource ePackageResource = ePackage.eResource();
						// be defensive, anyone can put anything into the
						// package registry
						if (nsURI != null && ePackageResource != null) {
							URI packageResourceURI = ePackageResource.getURI();
							if (packageResourceURI != null
									&& (packageResourceURI.isPlatform() || packageResourceURI
											.isFile())) {
								if (fPackage2HREF == null) {
									fPackage2HREF = new HashMap<URI, URI>();
								}

								URI registeredURI = URI.createURI(nsURI, false);
								fPackage2HREF.put(packageResourceURI,
										registeredURI);
							}
						}
					}
				}
			}
		}

		if (fPackage2HREF == null) {
			// mark as already initialized
			fPackage2HREF = Collections.emptyMap();
		}

		return fPackage2HREF;
	}
	
	IModuleSourceInfo getSourceInfo() {
		for (EObject nextRoot : getContents()) {
			if(nextRoot instanceof Module) {
				return ASTBindingHelper.getModuleSourceBinding((Module)nextRoot);
			}
		}
		
		return null;
	}
	
	private void addSourceInfo(URI sourceURI, int[] lineBreakOffsets) {
		BasicLineNumberProvider lineNumProvider = new BasicLineNumberProvider(lineBreakOffsets);				
		for (EObject nextRoot :getContents()) {
			if(nextRoot instanceof Module) {
				ASTBindingHelper.createModuleSourceBinding(nextRoot, sourceURI, lineNumProvider);
			}
		}
	}
}