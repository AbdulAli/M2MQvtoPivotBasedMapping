/**
 * Copyright (c) 2009 Borland Software Corp.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Shatalin (Borland) - initial API and implementation
 */
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class EPackageRegistryBasedURIResourceMap extends HashMap<URI, Resource> {

	private static final long serialVersionUID = -3716798352459745813L;

	private URIConverter uriConverter;

	private SAXParser parser;

	public EPackageRegistryBasedURIResourceMap(URIConverter uriConverter) {
		this.uriConverter = uriConverter;
	}

	@Override
	public Resource get(Object key) {
		if (containsKey(key)) {
			return super.get(key);
		}
		if (key instanceof URI) {
			initializePlatformPluginToRegistryMapEntry((URI) key);
		}
		return super.get(key);
	}

	private void initializePlatformPluginToRegistryMapEntry(URI uri) {
		if (!uri.isPlatformPlugin()) {
			return;
		}
		try {
			Handler handler = new Handler();
			try {
				getParser().parse(new InputSource(uriConverter.createInputStream(uri)), handler);
			} catch (SAXException e) {
				if (handler.getNsURI() != null && EPackage.Registry.INSTANCE.getEPackage(handler.getNsURI()) != null) {
					put(uri, EPackage.Registry.INSTANCE.getEPackage(handler.getNsURI()).eResource());
				}
			}
		} catch (IOException e) {
			/* Skipping */
		} catch (ParserConfigurationException e) {
			QvtPlugin.error(e);
		}
	}

	private SAXParser getParser() throws ParserConfigurationException, SAXException {
		if (parser == null) {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			parser = parserFactory.newSAXParser();
		}
		return this.parser;
	}

	private class Handler extends DefaultHandler {

		private String nsURI;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ("EPackage".equals(localName) && "http://www.eclipse.org/emf/2002/Ecore".equals(uri)) { //$NON-NLS-1$ //$NON-NLS-2$
				nsURI = attributes.getValue("nsURI"); //$NON-NLS-1$
			}
			throw new SAXException("Done"); //$NON-NLS-1$
		}

		public String getNsURI() {
			return nsURI;
		}

	}

}
