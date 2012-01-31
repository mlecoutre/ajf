package am.ajf.persistence.jpa.impl;

import java.lang.reflect.Method;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import am.ajf.core.utils.JavassistUtils;
import am.ajf.injection.ClassGenerationException;
import am.ajf.injection.ImplementationHandler;
import am.ajf.persistence.jpa.annotation.NamedQuery;
import am.ajf.persistence.jpa.annotation.QueryParam;

public class NamedQueryImplHandler extends AbstractPersistenceImplHandler implements ImplementationHandler {

	
	public NamedQueryImplHandler() {
		super();
	}
	
	
	@Override
	public boolean canHandle(Method method) {
		return method.isAnnotationPresent(NamedQuery.class);
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass,
			Class<?> interfaceClass, List<Method> methods) throws ClassGenerationException {
		Class<?> clazz = null;
		
		try {
			CtClass cc = null;
			
			// Manage the cases where superClass is null and co
			cc = JavassistUtils.initClass(superClass, interfaceClass, pool);
			
			//Add the class attributes (logger, EntityManagerFactory, @PersitenceUnit...)
			cc.addField(JavassistUtils.createLogger(cc));
			cc.addField(createEntityManagerFactory(cc));
			
			//generate each method
			for (Method method : methods) {
				CtClass declaringClass = pool.get(method.getDeclaringClass().getName());
				//TODO handle overloading
				CtMethod ctmethod = declaringClass.getDeclaredMethod(method.getName());			
				CtMethod newCtm = new CtMethod(ctmethod, cc, null);
				StringBuffer methodBody = generateBodyFor(method);
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
		Object[] annotations = method.getAnnotations();
		NamedQuery namedQuery = (NamedQuery)annotations[0];
		Object[][] pAnnotations = method.getParameterAnnotations();
		Object[] pTypes = method.getParameterTypes();
		if (pAnnotations.length != pTypes.length) {
			throw new IllegalArgumentException("method "+method.getName()+" dont have annotations on all parameters");
		}					
		
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("  logger.debug(\"launching query "+namedQuery.name()+"\");\n");
		body.append("  javax.persistence.EntityManager em = emf.createEntityManager();\n");
		body.append("  javax.persistence.Query query = em.createNamedQuery(\""+namedQuery.name()+"\");\n");
		for (int i = 0 ; i < pTypes.length ; i++) {
			QueryParam param = null;
			for (int j = 0 ; j < pAnnotations[i].length ; j++) {
				if (QueryParam.class.isAssignableFrom(pAnnotations[i][j].getClass())) {
					param = (QueryParam) pAnnotations[i][j];
				}
			}
			if (param == null) {
				throw new IllegalArgumentException("method "+method.getName()+" dont have annotation '@QueryParam' on parameter : "+i);
			}
			body.append("  query = query.setParameter(\""+param.value()+"\", $"+(i+1)+");\n");
		}
		body.append("  return query.getResultList();\n");
		body.append("}\n");
		return body;
	}

}
