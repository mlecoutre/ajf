package am.ajf.remoting.procs.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.utils.JavassistUtils;
import am.ajf.injection.ClassGenerationException;
import am.ajf.injection.api.ImplementationHandler;
import am.ajf.remoting.AnnotationHelper;
import am.ajf.remoting.ConfigurationException;
import am.ajf.remoting.SimpleFieldNamesMapper;
import am.ajf.remoting.procs.annotation.In;
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
			cc = JavassistUtils.initClass(superClass, interfaceClass, pool, "StoredProcedures");
			logger.trace("Generate class for remoting stored procedure : "+cc.getSimpleName());
			
			//Add the class attributes (logger, Datasource, ...)			
			cc.addField(JavassistUtils.createLoggerField(cc));
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
				logger.trace("Method ("+method.getName()+") generated :\n" + methodBody.toString());				
				newCtm.setBody(methodBody.toString());
				cc.addMethod(newCtm);			
			}
			
			clazz = cc.toClass();
			clazz.getConstructors();
			
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
	 * Generate the body method for the Storedprocedure call.
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
		
		//Compute the informations on the result based on its type
		ResultInfo rInfo = new ResultInfo(method.getReturnType(), method.getGenericReturnType());				
				
		
		Class<?> mapperClass = storedProcedure.mapper();
		if (mapperClass == null) {
			mapperClass = findMapperFor(rInfo.getResultTrueType());
		}
		
		
		//Verify what we can on the parameters
		boolean parametersAreNamed = verifyParametersDefinition(method, pAnnotations, pTypes, rInfo.isResultWrapped());
						
		//Starting the generation
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("  logger.debug(\"launching stored procedure "+storedProcedure.name()+"\");\n");
		int sizeOfParamsArray = 0;
		if (parametersAreNamed) {
			sizeOfParamsArray = (pTypes.length+rInfo.getOutParameters().size()) * 2;
		} else {
			sizeOfParamsArray = pTypes.length;
		}
		body.append("  Object[] parameters = new Object["+sizeOfParamsArray+"];\n");
		
		//append the in params values
		if (parametersAreNamed) {
			for (int i = 0 ; i < pTypes.length*2 ; i = i + 2) {				
				body.append("  parameters["+i+"] = \""+findInParamName(method.getParameterAnnotations(), i/2)+"\";\n");
				body.append("  parameters["+(i+1)+"] = $"+(i/2+1)+";\n");
			}
		} else {
			for (int i = 0 ; i < pTypes.length ; i++) {
				body.append("  parameters["+i+"] = $"+(i+1)+";\n");			
			}
		}
		
		//append the out params names
		for (int i = pTypes.length*2 ; i < sizeOfParamsArray ; i = i + 2) {
			body.append("  parameters["+i+"] = \""+rInfo.getOutParameters().get((i-pTypes.length*2)/2)+"\";\n");
			body.append("  parameters["+(i+1)+"] = \"<not-initialized>\";\n");
		}
		
		
		body.append(  "  java.util.Map res = ");		
		body.append(      " am.ajf.remoting.procs.impl.StoredProcedureHelper.callStoredProcedure(");
		body.append(      "\""+jndi+"\", ");
		body.append(      "\""+storedProcedure.name()+"\", ");
		body.append(      rInfo.isResultNull()+", ");
		body.append(      rInfo.isResultList()+", ");
		body.append(      rInfo.isResultWrapped()+", ");
		body.append(      "\""+rInfo.getResultWrappedName()+"\", ");
		body.append(      parametersAreNamed+", ");
		body.append(      mapperClass.getName()+".class, ");
		body.append(      rInfo.getResultTrueType().getName()+".class, ");
		body.append(      pTypes.length+", ");
		body.append(      "parameters");		
		body.append(      ");\n");
		body.append("\n");
		
		//If it is a Wrapped Object we use BeanUtils to copy the OUT and Result
		if (rInfo.isResultWrapped()) {
			body.append("  ").append(method.getReturnType().getName())
				.append(" obj = am.ajf.core.utils.BeanUtils.newInstance(")
				.append(method.getReturnType().getName()).append(".class")
				.append(");\n");
			body.append("  org.apache.commons.beanutils.BeanUtils.populate(obj, res);\n");
			body.append("  return ("+method.getReturnType().getName()+") obj;\n");
		} else if (rInfo.isResultList()) {
			body.append("  return (java.util.List) res.get(\"")
				.append(StoredProcedureHelper.SP_RESULT_KEY)
				.append("\");\n");
		} else {
			if (rInfo.isResultNull()) {
				// no casting
			} else {
				body.append("  return (")
					.append(rInfo.getResultTrueType().getName())
					.append(") res.get(\"")
					.append(StoredProcedureHelper.SP_RESULT_KEY)
					.append("\");\n");
 
			}			
		}
		body.append("}\n");
		return body;		
	}

	/**
	 * Get the value of the In annotation indexed by the selected parameter
	 * 
	 * @param parameterAnnotations
	 * @param index
	 * @return
	 * @throws ConfigurationException 
	 */
	private String findInParamName(Annotation[][] parameterAnnotations, int index) throws ConfigurationException {
		for (Annotation annotation : parameterAnnotations[index]) {
			if (In.class.equals(annotation.annotationType())) {
				return ((In) annotation).value();
			}
		}
		throw new ConfigurationException("The parameter ("+index+") doesnt have a IN annotation");
	}


	/**
	 * Check the paraemters annotations and return if the method should
	 * use named params or ordinal params.
	 * The mothod will output an Exception if the user is trying to call
	 * a SP with OUT parameters but not all IN params have an annotations.
	 * Puting IN annotations on only a few parameter will fail also.
	 * 
	 * @param method
	 * @param pAnnotations
	 * @param pTypes
	 */
	private boolean verifyParametersDefinition(Method method,
			Object[][] pAnnotations, Object[] pTypes, boolean isResWrapped) throws ConfigurationException {
		if (pAnnotations.length != pTypes.length) {
			throw new IllegalArgumentException("method "+method.getName()+" dont have annotations on all parameters");
		}		
		int numberOfInAnnotations = 0;
		for (Object[] annotationsAsObject : pAnnotations) {
			for (Object annotationAsObject : annotationsAsObject) {
				Annotation annotation = (Annotation) annotationAsObject;				
				if (In.class.equals(annotation.annotationType())) {
					numberOfInAnnotations++;
				}
			}			
		}
		
		//If all In params have an annotation, we will use named parameters and it is valid (in all cases)
		if (numberOfInAnnotations == pTypes.length) {
			return true;
		//If we have no annotations on IN params, but there is no OUT params, then we will use ordinal params
		} else if (numberOfInAnnotations == 0 && !isResWrapped) {
			return false;
		//Everything else is not valid
		} else {
			throw new ConfigurationException("You need to put @In annotations on all your parameters or on none. If your stored procedure have @Out parameters, then you need to provide @In annotations as well for your parameters");
		}
	}
	
	/**
	 * TODO implement the jpa mapper (think @Embeddable
	 * to declare all the jpa annotations without the need
	 * for corresponding table in the db)
	 * @param resultType
	 * @return
	 */
	private Class<?> findMapperFor(Class<?> resultType) {
		/*
		if (resultType.isAnnotationPresent(Embeddable.class)) {
			return SimpleFieldNamesMapper.class;
			//return JpaMapper.class;
		} else {
			return SimpleFieldNamesMapper.class;
		}
		*/
		return SimpleFieldNamesMapper.class;
				
	}

}
