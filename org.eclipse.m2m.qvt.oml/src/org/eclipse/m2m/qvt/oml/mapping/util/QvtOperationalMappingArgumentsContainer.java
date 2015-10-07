package org.eclipse.m2m.qvt.oml.mapping.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEnvFactory;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalFileEnv;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.ocl.EvaluationVisitor;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;

public class QvtOperationalMappingArgumentsContainer {
	
	private static QvtOperationalMappingArgumentsContainer operationalMappingArgumentsContainer = new QvtOperationalMappingArgumentsContainer();

	private QvtOperationalEnvFactory qvtOperationalEnvFactory; 
	private OperationalTransformation operationalTransformation;
	private QvtOperationalFileEnv qvtOperationalFileEnv;
	private QvtOperationalEvaluationEnv qvtOperationalEvaluationEnv;
	
	private QvtOperationalMappingArgumentsContainer()
	{
		
	}
	
	public EvaluationVisitor<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> createEvaluationVisitor()
	{
		// creates QvtOperationalEvaluationVisitorImpl
		if(qvtOperationalEnvFactory != null)
		{
			return qvtOperationalEnvFactory.createEvaluationVisitor(qvtOperationalFileEnv, qvtOperationalEvaluationEnv, null);
		}
		
		return null;
	}
	
	public QvtOperationalEnvFactory getQvtOperationalEnvFactory() {
		return qvtOperationalEnvFactory;
	}

	public void setQvtOperationalEnvFactory(QvtOperationalEnvFactory qvtOperationalEnvFactory) {
		this.qvtOperationalEnvFactory = qvtOperationalEnvFactory;
	}

	public OperationalTransformation getOperationalTransformation() {
		return operationalTransformation;
	}

	public void setOperationalTransformation(OperationalTransformation operationalTransformation) {
		this.operationalTransformation = operationalTransformation;
	}

	public QvtOperationalFileEnv getQvtOperationalFileEnv() {
		return qvtOperationalFileEnv;
	}

	public void setQvtOperationalFileEnv(QvtOperationalFileEnv qvtOperationalFileEnv) {
		this.qvtOperationalFileEnv = qvtOperationalFileEnv;
	}

	public QvtOperationalEvaluationEnv getQvtOperationalEvaluationEnv() {
		return qvtOperationalEvaluationEnv;
	}

	public void setQvtOperationalEvaluationEnv(QvtOperationalEvaluationEnv qvtOperationalEvaluationEnv) {
		this.qvtOperationalEvaluationEnv = qvtOperationalEvaluationEnv;
	}

	public static QvtOperationalMappingArgumentsContainer getInstance()
	{
		if(operationalMappingArgumentsContainer==null)
		{
			operationalMappingArgumentsContainer = new QvtOperationalMappingArgumentsContainer();
		}
		return operationalMappingArgumentsContainer;
	}
	
}
