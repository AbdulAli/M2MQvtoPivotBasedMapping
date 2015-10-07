package org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.qvto.examples.pivot.qvtoperational.QVTOperationalPackage;

public class FileOperationsUtil {

	/**
	 * 
	 * @return
	 * @throws IOException
	 * used to parse XMI meta-model instance
	 */
	public static XMIResource loadXMLResources(String fileName) throws IOException {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		EPackage.Registry.INSTANCE.put(ExpressionsPackage.eNS_URI, ExpressionsPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(QVTOperationalPackage.eNS_URI, QVTOperationalPackage.eINSTANCE);
		XMIResource resource = new XMIResourceImpl(URI.createURI(fileName));
		resource.load(null);
		return resource;
	}
	
	public static void writeTraditionalQVTOperationToXML(OperationalTransformation transformation, String filename) throws IOException{
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createFileURI(filename+".xmi"));
		// add the root object to the resource 
		resource.getContents().add(transformation); 
		// serialize resource you can specify also serialization 
		resource.save(null);
	}
	
	public static void writePivotQVTOperationToXML(org.eclipse.qvto.examples.pivot.qvtoperational.OperationalTransformation transformation, String filename) throws IOException{
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createFileURI(filename+".xmi"));
		// add the root object to the resource 
		resource.getContents().add(transformation); 
		// serialize resource you can specify also serialization 
		resource.save(null);
	}
	
	
}
