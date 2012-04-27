package am.ajf.remoting.ejb.impl;

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
import am.ajf.remoting.ejb.annotation.RemoteEJB;


public class RemoteEJBImplHandler implements ImplementationHandler {

	private ClassPool pool;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public RemoteEJBImplHandler() {		
		super();
		pool = ClassPool.getDefault();
	}
	
	
	@Override
	public boolean canHandle(Method method) {
		return method.isAnnotationPresent(RemoteEJB.class);
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass,
			Class<?> interfaceClass, List<Method> methods) throws ClassGenerationException {
		Class<?> clazz;
		try {
			CtClass cc = null;			
			
			// Manage the cases where superClass is null and co
			cc = JavassistUtils.initClass(superClass, interfaceClass, pool, "RemoteEJB");
			logger.trace("Generate class for remoting ejb call : "+cc.getSimpleName());
			
			//Add the class attributes (logger, Datasource, ...)			
			cc.addField(JavassistUtils.createLoggerField(cc));			
			
			//generate each method
			for (Method method : methods) {
				CtClass declaringClass = pool.get(method.getDeclaringClass().getName());
				//TODO handle overloading
				CtMethod ctmethod = declaringClass.getDeclaredMethod(method.getName());			
				CtMethod newCtm = new CtMethod(ctmethod, cc, null);
				StringBuilder methodBody = generateBodyFor(method);
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
	 * Generate the body method for the RemoteEJB call.
	 * 
	 * @param method
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws ConfigurationException
	 */
	public StringBuilder generateBodyFor(Method method) throws ClassNotFoundException, NotFoundException, CannotCompileException, ConfigurationException {		
		//Retrieve the parameters needed to launch the stored procedure 		
		Object[] annotations = method.getAnnotations();
		RemoteEJB remoteEJB = (RemoteEJB)annotations[0];		
		Object[] pTypes = method.getParameterTypes();
		String jndi = AnnotationHelper.getJndiInfo(method);		
						
		//Starting the generation
		StringBuilder body = new StringBuilder();
		body.append("{\n");
		body.append("  logger.debug(\"calling remote EJB : "+remoteEJB.value()+"\");\n");
		body.append("  Object remoteRef = am.ajf.remoting.ejb.impl.RemoteEJBHelper.getEJBRef(\""+jndi+"\", \""+remoteEJB.value()+"\");\n");
		body.append("  "+remoteEJB.name().getName()+" remote = ("+remoteEJB.name().getName()+") javax.rmi.PortableRemoteObject.narrow(remoteRef, "+remoteEJB.name().getName()+".class);\n");
		if (!Void.TYPE.equals(method.getReturnType())) {
			body.append("  "+method.getReturnType().getName()+" res =");
		}
		body.append("  remote."+method.getName()+"("+generateParams(pTypes.length)+");\n");
		if (!Void.TYPE.equals(method.getReturnType())) {
			body.append("  return res;\n");
		}
		body.append("}\n");
		
		return body;		
	}


	/**
	 * Generate the n * '$i' params for the remote ejb call
	 * @param length nb of params for the ejb method
	 * @return
	 */
	private String generateParams(int length) {
		StringBuilder buffer = new StringBuilder();
		for (int i=1 ; i <= length ; i = i + 1) {
			if (i==1) {
				buffer.append("$").append(i);
			} else {
				buffer.append(",$").append(i);
			}
		}
		return buffer.toString();		
	}


}
