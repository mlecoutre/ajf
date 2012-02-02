package am.ajf.remoting.procs.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.utils.JavassistUtils;
import am.ajf.injection.ClassGenerationException;
import am.ajf.injection.ImplementationHandler;
import am.ajf.remoting.AnnotationHelper;
import am.ajf.remoting.ConfigurationException;
import am.ajf.remoting.SimpleFieldNamesMapper;
import am.ajf.remoting.procs.annotation.StoredProcedure;


public class StoredProcedureImplHandler implements ImplementationHandler {

	private ClassPool pool;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public StoredProcedureImplHandler() {		
		super();
		pool = ClassPool.getDefault();
	}
	
	
	@Override
	public boolean canHandle(Method method) {
		return method.isAnnotationPresent(StoredProcedure.class);
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass,
			Class<?> interfaceClass, List<Method> methods) throws ClassGenerationException {
		Class<?> clazz;
		try {
			CtClass cc = null;
			
			// Manage the cases where superClass is null and co
			cc = JavassistUtils.initClass(superClass, interfaceClass, pool);
			
			//Add the class attributes (logger, Datasource, ...)			
			cc.addField(JavassistUtils.createLogger(cc));
			//TODO Datasource could be injected in Remote class annotation setup
			//so, validation occur at deploy time in opposition
			//of execution time if we used a manual jndi lookup
			//cc.addField(createDataSource(cc));
			
			//generate each method
			for (Method method : methods) {
				CtClass declaringClass = pool.get(method.getDeclaringClass().getName());
				//TODO handle overloading
				CtMethod ctmethod = declaringClass.getDeclaredMethod(method.getName());			
				CtMethod newCtm = new CtMethod(ctmethod, cc, null);
				StringBuffer methodBody = generateBodyFor(method);
				logger.debug("Method ("+method.getName()+") generated :\n" + methodBody.toString());				
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
		} catch (ConfigurationException e) {
			throw new ClassGenerationException("Configuration error generating the class : ", e);
		}
		
		return clazz;
	}	

	/**
	 * 
	 * @param method
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws ConfigurationException
	 */
	public StringBuffer generateBodyFor(Method method) throws ClassNotFoundException, NotFoundException, CannotCompileException, ConfigurationException {		
		//Retrieve the parameters needed to launch the stored procedure 		
		Object[] annotations = method.getAnnotations();
		StoredProcedure storedProcedure = (StoredProcedure)annotations[0];
		Object[][] pAnnotations = method.getParameterAnnotations();
		Object[] pTypes = method.getParameterTypes();
		String jndi = AnnotationHelper.getJndiInfo(method);
		boolean isResList = List.class.isAssignableFrom(method.getReturnType());
		//logger.debug(method.getReturnType().getTypeParameters()[0].toString());
		//logger.debug(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].toString());
		Class<?> resultType;
		if (isResList) {
			Type listType = method.getGenericReturnType();
			if (listType instanceof ParameterizedType) {
				Type subType = ((ParameterizedType) listType).getActualTypeArguments()[0];
				if (subType instanceof Class<?>) {
					resultType = (Class<?>) subType;
				} else {
					throw new ConfigurationException("The method ("+method.getName()+") doesnt define a parameterized List of Beans. List of native types are nor supported.");
				}
			} else {
				throw new ConfigurationException("The method ("+method.getName()+") doesnt define a parameterized List as the return type. You need to define the type of your list content.");
			}			
		} else {			
			resultType = method.getReturnType();
		}
		Class<?> mapperClass = storedProcedure.mapper();
		if (mapperClass == null) {
			mapperClass = findMapperFor(resultType);
		}
		
		
		//Verify what we can on the parameters
		if (pAnnotations.length != pTypes.length) {
			throw new IllegalArgumentException("method "+method.getName()+" dont have annotations on all parameters");
		}
						
		//Starting the generation
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("  logger.debug(\"launching stored procedure "+storedProcedure.name()+"\");\n");
		body.append("  Object[] parameters = new Object["+pTypes.length+"];\n");	
		for (int i = 0 ; i < pTypes.length ; i++) {
			body.append("  parameters["+i+"] = $"+(i+1)+";\n");			
		}
		body.append("  return ");
		if (isResList) {
			body.append(      "(java.util.List)");
		} else {
			if (Void.class.equals(method.getReturnType())) {
				// no casting
			} else {
				body.append(      "("+resultType.getName()+")"); 
			}			
		}
		body.append(      " am.ajf.remoting.procs.impl.StoredProcedureHelper.callStoredProcedure(");
		body.append(      "\""+jndi+"\", ");
		body.append(      "\""+storedProcedure.name()+"\", ");
		body.append(      Void.class.equals(method.getReturnType())+", ");
		body.append(      isResList+", ");
		body.append(      mapperClass.getName()+".class, ");
		body.append(      resultType.getName()+".class, ");
		body.append(      "parameters");		
		body.append(      ");\n");
		body.append("}\n");
		return body;		
	}
	
	/**
	 * TODO implement the hibernate mapper (think @Embeddable)
	 * @param resultType
	 * @return
	 */
	private Class<?> findMapperFor(Class<?> resultType) {
		if (resultType.isAnnotationPresent(Entity.class)) {
			return SimpleFieldNamesMapper.class;
			//return HibernateMapper.class;
		} else {
			return SimpleFieldNamesMapper.class;
		}		
	}

}
