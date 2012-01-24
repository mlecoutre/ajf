package am.ajf.persistence.jpa.impl;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import am.ajf.persistence.jpa.CrudDbService;
import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.EntityManagerProvider.TransactionType;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;

public class CrudDbMethodGenerator implements MethodGenerator {

	private static final String CLASS_CRUD_IMPL = "am.ajf.persistence.jpa.impl.BasicImplCrudDbService";
	private static final String FIND_METHOD = "find"; 
	private static final String SAVE_METHOD = "save";
	private static final String REMOVE_METHOD = "remove";
	private static final String DELETE_METHOD = "delete";
	private static final String FETCH_METHOD = "fetch"; 	
	
	@Override
	public boolean canImplement(CtMethod method) {		
		return method.getDeclaringClass().getName().equals(CrudDbService.class.getName());
	}

	@Override
	public StringBuffer generateBodyFor(CtMethod method) throws ClassNotFoundException, NotFoundException {
		StringBuffer body = new StringBuffer();
		CtClass cc = method.getDeclaringClass();
		String puName = "default";
		
		if (cc.hasAnnotation(PersistenceUnit.class)) {
			PersistenceUnit puAnn = (PersistenceUnit) cc.getAnnotation(PersistenceUnit.class);
			puName = puAnn.value();
			
		}
		
		TransactionType transactionType = EntityManagerProvider.getTransactionType(puName);
		
		body.append("{\n");
		
		if (FIND_METHOD.equals(method.getName())) {			
			body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.find(emf, $1, $2);\n");		
		} else if (SAVE_METHOD.equals(method.getName())) {
			if (transactionType.equals(TransactionType.JTA)) {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.save(false, emf, $1);\n");
			} else {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.save(true, emf, $1);\n");
			}
		} else if (REMOVE_METHOD.equals(method.getName())) {
			if (transactionType.equals(TransactionType.JTA)) {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.remove(false, emf, $1);\n");
			} else {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.remove(true, emf, $1);\n");
			}
		} else if (DELETE_METHOD.equals(method.getName())) {
			body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.delete(emf, $1);\n");
		} else if (FETCH_METHOD.equals(method.getName())) {			
			body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.fetch(emf, $1);\n");
		} else {
			throw new IllegalStateException("The method "+method.getName()+" doesnt have an automatic implementation, but should.");
		}
		
		body.append("}\n");
				
		return body;
	}

}
