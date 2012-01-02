package ajf.persistence.jpa.impl;

import java.util.List;

import ajf.persistence.jpa.CrudDbService;
import ajf.persistence.jpa.annotation.NamedQuery;
import ajf.persistence.jpa.annotation.QueryParam;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class CrudDbMethodGenerator implements MethodGenerator {

	private static final String CLASS_CRUD_IMPL = "ajf.persistence.jpa.impl.BasicImplCrudDbService";
	private static final String FIND_METHOD = "find"; 
	private static final String SAVE_METHOD = "save";
	private static final String REMOVE_METHOD = "remove";
	private static final String DELETE_METHOD = "delete";
	private static final String FETCH_METHOD = "fetch"; 	
	
	
	public boolean canImplement(CtMethod method) {		
		return method.getDeclaringClass().getName().equals(CrudDbService.class.getName());
	}

	public StringBuffer generateBodyFor(CtMethod method) throws ClassNotFoundException, NotFoundException {
		StringBuffer body = new StringBuffer(); 
		
		body.append("{\n");
		
		if (FIND_METHOD.equals(method.getName())) {			
			body.append("  return ajf.persistence.jpa.impl.BasicImplCrudDbService.find($1, $2);\n");		
		} else if (SAVE_METHOD.equals(method.getName())) {
			body.append("  return ajf.persistence.jpa.impl.BasicImplCrudDbService.save($1);\n");
		} else if (REMOVE_METHOD.equals(method.getName())) {
			body.append("  return ajf.persistence.jpa.impl.BasicImplCrudDbService.remove($1);\n");
		} else if (DELETE_METHOD.equals(method.getName())) {
			body.append("  return ajf.persistence.jpa.impl.BasicImplCrudDbService.delete($1);\n");
		} else if (FETCH_METHOD.equals(method.getName())) {			
			body.append("  return ajf.persistence.jpa.impl.BasicImplCrudDbService.fetch($1);\n");
		} else {
			throw new IllegalStateException("The method "+method.getName()+" doesnt have an automatic implementation, but should.");
		}
		
		body.append("}\n");
				
		return body;
	}

}
