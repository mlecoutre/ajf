package ajf.persistence.jpa.impl;

import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.annotation.StoredProcedure;

public class StoredProcedureMethodGenerator implements MethodGenerator {
	
	
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean canImplement(CtMethod method) {		
		return method.hasAnnotation(StoredProcedure.class);
	}

	@Override
	public StringBuffer generateBodyFor(CtMethod method) throws ClassNotFoundException, NotFoundException, CannotCompileException {
		ClassPool pool = ClassPool.getDefault();		
		Object[] annotations = method.getAnnotations();
		StoredProcedure storedProcedure = (StoredProcedure)annotations[0];
		Object[][] pAnnotations = method.getParameterAnnotations();
		Object[] pTypes = method.getParameterTypes();
		CtClass listCtClass = pool.get("java.util.List");
		boolean isResList = method.getReturnType().subtypeOf(listCtClass);
		if (pAnnotations.length != pTypes.length) {
			throw new IllegalArgumentException("method "+method.getLongName()+" dont have annotations on all parameters");
		}
		
		Class<?> workClass = generateHibernateWorkerForMethod(method, isResList, storedProcedure, pTypes.length);
		
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("  logger.debug(\"launching stored procedure "+storedProcedure.name()+"\");\n");
		
		if (isResList) {
			body.append("  java.util.List res = new java.util.ArrayList();\n");
		} else {
			body.append("  "+storedProcedure.resultClass().getName()+" res = new "+storedProcedure.resultClass().getName()+"();\n");
		}		
		body.append("  javax.persistence.EntityManager em = emf.createEntityManager();\n");
		body.append("  org.hibernate.Session session = (org.hibernate.Session) em.getDelegate();\n");
		for (int i=0 ; i < pTypes.length ; i++) {
			body.append("  logger.debug(\"launching stored procedure with param "+(i+1)+" : \"+$"+(i+1)+");\n");
		}
		body.append("  org.hibernate.jdbc.Work worker = new "+workClass.getName()+"(res"+generateArgListCall(pTypes.length)+");\n");
		body.append("  session.doWork(worker);\n");			
		body.append("  return res;\n");
		body.append("}\n");
		return body;
	}
	
	private Class<?> generateHibernateWorkerForMethod(CtMethod method, boolean isResList, StoredProcedure storedProcedure, int nbArgs) throws NotFoundException, CannotCompileException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass("HibernateJdbcWork$"+method.getDeclaringClass().getSimpleName()+"$"+method.getName()+generateClassSuffix());
		CtClass cin = pool.get("org.hibernate.jdbc.Work");
		cc.setInterfaces(new CtClass[] {cin});
		
		CtField cLogger = CtField.make("private final transient org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());", cc);		
		cc.addField(cLogger);
		cc.addField(CtField.make("private Object res;", cc));
		for (int i=0 ; i < nbArgs ; i++) {
			cc.addField(CtField.make("private Object arg"+i+";", cc));
		}
		
		
		StringBuffer constructorBody = new StringBuffer();
		constructorBody.append("public "+cc.getName()+"(Object res"+generateConstructorArgListCall(nbArgs)+")\n");
		constructorBody.append("{\n");
		constructorBody.append("this.res = res;\n");
		for (int i=0 ; i < nbArgs ; i++) {
			constructorBody.append("logger.debug(\"Setting parameter in constructor arg"+i+" :\"+arg"+i+");\n");
			constructorBody.append("this.arg"+i+" = arg"+i+";\n");
		}
		constructorBody.append("}\n");
		CtConstructor constructor = CtNewConstructor.make(constructorBody.toString(), cc);		
		cc.addConstructor(constructor);
		
				
		CtMethod workMethod = null;
		for (CtMethod m : cc.getMethods()) {
			if ("execute".equals(m.getName())) {
				workMethod = new CtMethod(m, cc, null);
				break;
			}
		}
		if (workMethod == null) {
			throw new IllegalStateException("Method 'execute' not found on org.hibernate.jdbc.Work ! Check your hibernate version.");
		}
		
		StringBuffer body = new StringBuffer();
		
		logger.debug("generate worker for : " + cc.getName());
		
		body.append("{\n");
		body.append("  java.sql.PreparedStatement pstmt = $1.prepareCall(\"CALL "+storedProcedure.name()+"("+generateQuestionMarks(nbArgs)+")\");\n");
		for (int i = 0 ; i < nbArgs ; i++) {
			body.append("  logger.debug(\"Setting parameter arg"+i+": \"+arg"+i+");\n");
			body.append("  pstmt.setObject("+(i+1)+",arg"+i+");\n");
		}		
		if (CtClass.voidType.equals(method.getReturnType())) {
			body.append("  pstmt.execute();\n");		
		} else {
			body.append("  java.sql.ResultSet rs = pstmt.executeQuery();\n");
			body.append("  java.sql.ResultSetMetaData metadata = rs.getMetaData();\n");
			body.append("  java.util.ArrayList colNames = new java.util.ArrayList();\n");
			body.append("  for (int i=0 ; i < metadata.getColumnCount() ; i++) {\n");
			body.append("    colNames.add(metadata.getColumnName(i+1).toLowerCase());\n");
			body.append("    logger.debug(\"Adding col in cache : \"+metadata.getColumnName(i+1).toLowerCase());\n");
			body.append("  }\n");			
			body.append("  while(rs.next()) {\n");
			if (isResList) {
				body.append("    "+storedProcedure.resultClass().getName()+" obj = new "+storedProcedure.resultClass().getName()+"();\n");
			} else {
				body.append("    "+storedProcedure.resultClass().getName()+" obj = ("+storedProcedure.resultClass().getName()+")this.res;\n");
			}
			for (Method methodRes : storedProcedure.resultClass().getMethods()) {
				String field = extractFieldFromMethod(methodRes);				
				if (field != null) {
					String setterType = methodRes.getParameterTypes()[0].getName();
					body.append("    logger.debug(\"setting field : +"+field+"\");\n");										
					body.append("    if(colNames.contains(\""+field+"\")) {\n");
					body.append("      obj."+methodRes.getName()+"( ("+setterType+") rs.getObject(\""+field+"\") );\n");
					body.append("      logger.debug(\"field found : \"+rs.getObject(\""+field+"\"));\n");
					body.append("    } else {\n");
					body.append("      obj."+methodRes.getName()+"( null );\n");
					body.append("    }\n");
					
				}
				
			}
			if (isResList) { 
				body.append("    ((java.util.List)this.res).add(obj);\n");
			}
			body.append("  }\n");
			
		}		
		body.append("  pstmt.close();\n");		
		body.append("}\n");
		
		logger.debug("\n"+body.toString());
		
		workMethod.setBody(body.toString());
		
		cc.addMethod(workMethod);
		
		return cc.toClass();
	}
	
	private String generateClassSuffix() {
		return "_$ajf$javaassist$";
	}
	
	
	/**
	 * Generate question marks for a stored prcedure call
	 * 
	 * @param number
	 * @return
	 */
	private String generateQuestionMarks(int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0 ; i < number ; i++) {
			if (i==0) {
				buffer.append("?");
			} else {
				buffer.append(",?");
			}
		}
		return buffer.toString();
	}
	
	/**
	 * Generate the arg list for the worker creation
	 * 
	 * @param number
	 * @return
	 */
	private String generateArgListCall(int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0 ; i < number ; i++) {			
			buffer.append(",$"+(i+1));			
		}
		return buffer.toString();
	}
	
	/**
	 * Generate the Worker constructor arg list
	 * 
	 * @param number
	 * @return
	 */
	private String generateConstructorArgListCall(int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0 ; i < number ; i++) {			
			buffer.append(", Object arg"+i);			
		}
		return buffer.toString();
	}
	
	/**
	 * Extract the field name from the setter methods.
	 * This method return null if the method is not a setter.
	 * 
	 * @param method
	 * @return
	 */
	private String extractFieldFromMethod(Method method) {
		String res = null;
		if (method.getName().startsWith("set")) {
			res = method.getName().substring(3);
			String firstChar = res.substring(0, 1);
			res = firstChar.toLowerCase() + res.substring(1);
		}
		return res;
	}
}
