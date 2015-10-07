package org.eclipse.m2m.qvt.oml.pivot.mapping.test;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.m2m.qvt.oml.mapping.util.QvtOperationalMappingArgumentsContainer;
import org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util.FileOperationsUtil;
import org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util.TraditionalToPivotMappingVisitor;
import org.eclipse.m2m.qvt.oml.pivot.mapping.mapping.util.TraditionalToPivotMappingVisitorImpl;
import org.eclipse.ocl.utilities.Visitor;
import org.eclipse.qvto.examples.pivot.qvtoperational.OperationalTransformation;
import org.eclipse.qvto.examples.pivot.qvtoperational.QVTOperationalPackage;
import org.junit.Test;

import junit.framework.TestCase;
import rdb.RdbPackage;
import simpleuml.SimpleumlPackage;

/**
 * 
 * @author AbdulAli
 *
 */
@SuppressWarnings("rawtypes")
public class ExecuteMappingTest<U extends Visitor> extends TestCase {

	private static final String qvtoFileUri = "C:\\Users\\AbdulAli\\git\\M2MPivotBasedMappingf\\org.eclipse.m2m.qvt.oml.pivot.mapping\\Example\\Simpleuml_To_Rdb.qvto";
	private static final String inUri = "C:\\Users\\AbdulAli\\git\\M2MPivotBasedMappingf\\org.eclipse.m2m.qvt.oml.pivot.mapping\\Example\\pim.simpleuml";

	@Test
	public void testMapping() {
		collectMappingArguments();
		try {
			// create Visitor for traditional object mapping
			TraditionalToPivotMappingVisitor traditionalVisitor = new TraditionalToPivotMappingVisitorImpl(
					QvtOperationalMappingArgumentsContainer.getInstance().getQvtOperationalFileEnv(),
					QvtOperationalMappingArgumentsContainer.getInstance().getQvtOperationalEvaluationEnv());

			org.eclipse.qvto.examples.pivot.qvtoperational.OperationalTransformation pivotOperationalTransformation = (OperationalTransformation) QvtOperationalMappingArgumentsContainer
					.getInstance().getOperationalTransformation().accept(traditionalVisitor);

			// Convert Pivot based Transformation to XML
			FileOperationsUtil.writePivotQVTOperationToXML(pivotOperationalTransformation, "pivotBasedTransformation");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 *             used to parse XMI meta-model instance
	 */
	protected XMIResource loadResources() throws IOException {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		EPackage.Registry.INSTANCE.put(ExpressionsPackage.eNS_URI, ExpressionsPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(QVTOperationalPackage.eNS_URI, QVTOperationalPackage.eINSTANCE);

		XMIResource resource = new XMIResourceImpl(URI.createURI("transformation.xmi"));
		resource.load(null);

		return resource;
	}

	/**
	 * 
	 * programmatic execution for collecting arguments
	 */
	protected void collectMappingArguments() {
		EPackage.Registry.INSTANCE.put(SimpleumlPackage.eNS_URI, SimpleumlPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(RdbPackage.eNS_URI, RdbPackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

		// Refer to an existing transformation via URI
		URI transformationURI = URI.createFileURI(qvtoFileUri);
		// create executor for the given transformation
		TransformationExecutor executor = new TransformationExecutor(transformationURI);

		// define the transformation input
		// Remark: we take the objects from a resource, however
		// a list of arbitrary in-memory EObjects may be passed
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());

		Resource inResource = resourceSet.getResource(URI.createFileURI(inUri), true);
		// Resource inResource = getResourceFromUri(inUri);
		EList<EObject> inObjects = inResource.getContents();

		// create the input extent with its initial contents
		ModelExtent input = new BasicModelExtent(inObjects);
		// create an empty extent to catch the output
		ModelExtent output = new BasicModelExtent();

		// setup the execution environment details ->
		// configuration properties, logger, monitor object etc.
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.setConfigProperty("keepModeling", true);

		// run the transformation assigned to the executor with the given
		// input and output and execution context -> ChangeTheWorld(in, out)
		// Remark: variable arguments count is supported
		executor.execute(context, input, output);
		System.out.println("Transformation executed.");
	}

}
