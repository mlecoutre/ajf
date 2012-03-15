package am.ajf.persistence.jpa.impl;

import java.lang.reflect.Method;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.utils.JavassistUtils;
import am.ajf.injection.ClassGenerationException;
import am.ajf.injection.api.ImplementationHandler;
import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.EntityManagerProvider.TransactionType;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;
import am.ajf.persistence.jpa.api.CrudServiceBD;

/**
 * ImplHandler to generate crud method of an interface
 * 
 * @author Nicolas Radde (E016696)
 */
public class CrudImplHandler extends AbstractPersistenceImplHandler 
								implements ImplementationHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CrudImplHandler.class);
	private static final String FIND_METHOD = "find"; 
	private static final String SAVE_METHOD = "save";
	private static final String REMOVE_METHOD = "remove";
	private static final String DELETE_METHOD = "delete";
	private static final String FETCH_METHOD = "fetch"; 

	public CrudImplHandler() {
		super();
	}	
	
	@Override
	public boolean canHandle(Method method) {
		boolean isCrudClass = method.getDeclaringClass().equals(CrudServiceBD.class);
		return isCrudClass;
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass,
			Class<?> interfaceClass, List<Method> methods) throws ClassGenerationException{
		logger.trace("Generate Class for : "+interfaceClass.getSimpleName());
		Class<?> clazz = null;
		
		try {
			CtClass cc = null;
			
			// Manage the cases where superClass is null and co
			cc = JavassistUtils.initClass(superClass, interfaceClass, pool, "Crud");
			
			//Add the class attributes (logger, EntityManagerFactory, @PersitenceUnit...)
			CtField f1 = JavassistUtils.createLoggerField(cc);
			cc.addField(f1);
			CtField f2 = createEntityManager(cc);
			logger.trace(f2.toString());
			cc.addField(f2);
			CtField f3 = createClassName(cc);
			logger.trace(f3.toString());
			cc.addField(f3);
			
			//generate a default constructor that init the entityClass field at runtime
			// see http://blog.xebia.com/2009/02/07/acessing-generic-types-at-runtime-in-java/
			cc.addConstructor(createDefaultConstructor(cc));
						
			//generate each method
			for (Method method : methods) {
				CtClass declaringClass = pool.get(method.getDeclaringClass().getName());
				//TODO handle overloading
				CtMethod ctmethod = declaringClass.getDeclaredMethod(method.getName());			
				CtMethod newCtm = new CtMethod(ctmethod, cc, null);
				StringBuffer methodBody = generateBodyFor(method);
				logger.trace("Generate Method for "+method.getName()+"\n"+methodBody.toString());
				newCtm.setBody(methodBody.toString());
				cc.addMethod(newCtm);			
			}
			
			clazz = cc.toClass();
		
		} catch (NotFoundException e) {
			throw new ClassGenerationException("Impossible to find the class : ", e); 
		} catch (CannotCompileException e) {
			throw new ClassGenerationException("Impossible to compile code : ", e);
		} catch (ClassNotFoundException e) {
			throw new ClassGenerationException("Impossible to find the class : ", e);
		}
		
		return clazz;
	}	

	public StringBuffer generateBodyFor(Method method) throws ClassNotFoundException, NotFoundException {
		StringBuffer body = new StringBuffer();
		Class<?> clazz = method.getDeclaringClass();
		String puName = "default";
		
		if (clazz.isAnnotationPresent(PersistenceUnit.class)) {
			PersistenceUnit puAnn = (PersistenceUnit) clazz.getAnnotation(PersistenceUnit.class);
			puName = puAnn.value();
			
		}
		
		TransactionType transactionType = EntityManagerProvider.getTransactionType(puName);
		
		body.append("{\n");
		
		if (FIND_METHOD.equals(method.getName())) {			
			body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.find(em, $1, $2);\n");		
		} else if (SAVE_METHOD.equals(method.getName())) {
			if (transactionType.equals(TransactionType.JTA)) {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.save(false, em, $1);\n");
			} else {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.save(true, em, $1);\n");
			}
		} else if (REMOVE_METHOD.equals(method.getName())) {
			if (transactionType.equals(TransactionType.JTA)) {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.remove(false, em, $1);\n");
			} else {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.remove(true, em, $1);\n");
			}
		} else if (DELETE_METHOD.equals(method.getName())) {
			if (transactionType.equals(TransactionType.JTA)) {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.delete(false, em, entityClass, $1);\n");
				//body.append("  return true;\n");
			} else {
				body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.delete(true, em, entityClass, $1);\n");
				//body.append("  return true;\n");
			}
		} else if (FETCH_METHOD.equals(method.getName())) {			
			body.append("  return am.ajf.persistence.jpa.impl.BasicImplCrudDbService.fetch(em, entityClass, $1);\n");
			//body.append("  return null;\n");
		} else {
			throw new IllegalStateException("The method "+method.getName()+" doesnt have an automatic implementation, but should.");
		}
		
		body.append("}\n");
				
		return body;
	}
	
	protected CtField createClassName(CtClass cc)
			throws CannotCompileException, ClassNotFoundException,
			NotFoundException {
		CtField cEntityClass = CtField.make("private transient Class entityClass;", cc);
		return cEntityClass;
	}
	
	private CtConstructor createDefaultConstructor(CtClass cc) throws CannotCompileException {
		StringBuffer method = new StringBuffer();
		method.append("public "+cc.getSimpleName()+"() {\n");
		method.append("  super();\n");
		method.append("  logger.trace(\"Initializing CrudWrapper for :\"+getClass());\n");
		method.append("  java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) am.ajf.persistence.jpa.impl.BasicImplCrudDbService.getCrudInterface(getClass());\n");
		method.append("  this.entityClass = (Class) pt.getActualTypeArguments()[0];\n");
		method.append("}\n");				
		CtConstructor constructor = CtNewConstructor.make(method.toString(), cc);
		return constructor;
	}

}
