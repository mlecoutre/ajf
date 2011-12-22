package ajf.persistence.jpa.impl;

import java.lang.reflect.Field;
import java.util.UUID;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.ImplementationGenerator;
import ajf.persistence.jpa.annotation.NamedQuery;
import ajf.persistence.jpa.annotation.QueryParam;

@Named
public class JavaassistImplementationGenerator implements ImplementationGenerator {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	private ClassPool pool;
	
	public JavaassistImplementationGenerator() {
		pool = ClassPool.getDefault();
	}
	
	private String generateClassSuffix() {
		return "_$ajf$$javaassist$Proxy";
	}
	
	public Class<?> createImpl(Class<?> serviceBD, Class<?> serviceImpl) throws CannotCompileException, NotFoundException, ClassNotFoundException {		
		CtClass cc;
		CtClass cin = pool.get(serviceBD.getName());
		if (serviceImpl == null) {// no impl, so impl the interface
			cc = pool.makeClass(serviceBD.getName() + generateClassSuffix());		
			cc.setInterfaces(new CtClass[] {cin});
		} else { //extend the provided client impl
			cc = pool.makeClass(serviceImpl.getName() + generateClassSuffix());		
			CtClass cim = pool.get(serviceImpl.getName());
			cc.setSuperclass(cim);
		}
		
		CtField cLogger = CtField.make("private final transient org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());", cc);		
		cc.addField(cLogger);
		
		for (CtMethod method : cc.getMethods()) {
			CtMethod newCtm = new CtMethod(method, cc, null);			
			if (isImplNotNeeded(method)) {
				logger.debug("dont generate method : "+method.getLongName()+ " : "+ Modifier.toString(method.getModifiers())+" : "+method.getDeclaringClass().getName());
			} else {
				logger.debug("generate method : "+method.getLongName()+ " : "+ Modifier.toString(method.getModifiers())+" : "+method.getDeclaringClass().getName());
				//logger.debug(newCtm.getLongName() + " : "+ Modifier.toString(method.getModifiers()));
				//logger.debug(method.getSignature());
				method.setModifiers(Modifier.clear(method.getModifiers(), Modifier.ABSTRACT));
				if (method.getReturnType().equals(CtPrimitiveType.voidType)) {
					newCtm.setBody("{ logger.fatal(\"method "+method.getLongName()+" is not yet implemented:\"); }");
				} else {
					Object[] annotations = method.getAnnotations();
					NamedQuery namedQuery = (NamedQuery)annotations[0];
					Object[][] pAnnotations = method.getParameterAnnotations();
					Object[] pTypes = method.getParameterTypes();
					if (pAnnotations.length != pTypes.length) {
						throw new IllegalArgumentException("method "+method.getLongName()+" dont have annotations on all parameters");
					}					
					
					StringBuffer body = new StringBuffer();
					body.append("{\n");
					body.append("  logger.debug(\"launching query "+namedQuery.name()+"\");\n");
					body.append("  javax.persistence.EntityManager em = ajf.persistence.jpa.EntityManagerProvider.getEntityManager(\"default\");\n");
					body.append("  javax.persistence.Query query = em.createNamedQuery(\""+namedQuery.name()+"\");\n");
					for (int i = 0 ; i < pTypes.length ; i++) {
						QueryParam param = null;
						for (int j = 0 ; j < pAnnotations[i].length ; j++) {
							if (QueryParam.class.isAssignableFrom(pAnnotations[i][j].getClass())) {
								param = (QueryParam) pAnnotations[i][j];
							}
						}
						if (param == null) {
							throw new IllegalArgumentException("method "+method.getLongName()+" dont have annotation '@QueryParam' on parameter : "+i);
						}
						body.append("  query = query.setParameter(\""+param.value()+"\", $"+(i+1)+");\n");
					}
					body.append("  return query.getResultList();\n");
					body.append("}\n");
					logger.debug("Generated method "+method.getName()+" body : \n"+body.toString());
					newCtm.setBody(body.toString());
				}
				cc.addMethod(newCtm);			
			}	
		}
		return cc.toClass();
	}

	/**
	 * Check if this method need an impl
	 * exclude if :
	 * - method is not abstract (have an client impl)
	 * - method is from Object class
	 * This mean if a client provide an impl for an annotated method, the generation will
	 * not occur.
	 * 
	 * @param method
	 * @return
	 */
	private boolean isImplNotNeeded(CtMethod method) {
		// exclude class from Object (wait, non overrided equals, ...)
		boolean res = "java.lang.Object".equals(method.getDeclaringClass().getName());
		res = res || !Modifier.isAbstract(method.getModifiers());
		return res;
	}
	
	
	public Bean<?> createBeanFromImpl(Class<?> impl, Class<?> in, final InjectionTarget<?> it) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException {
		//ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass("Bean_"+impl.getName() + "_" + UUID.randomUUID().toString().replace("-", ""));
		CtClass cib = pool.get("javax.enterprise.inject.spi.Bean");
		//CtClass cib = pool.get("");
		
		
		cc.setInterfaces(new CtClass[] {cib});
		cc.addField(CtField.make("public javax.enterprise.inject.spi.InjectionTarget it;", cc));
		
		
		String getBeanClass = "public Class getBeanClass() { return "+impl.getCanonicalName()+".class; }";
		//String getBeanClass = "public Class getBeanClass() { return .class; }";
		cc.addMethod(CtMethod.make(getBeanClass, cc));
		String getInjectionPoints = "public java.util.Set getInjectionPoints() {return it.getInjectionPoints();}";
		cc.addMethod(CtMethod.make(getInjectionPoints, cc));
		String getName = "public String getName() {return \"" + impl.getCanonicalName() + "\";}";
		cc.addMethod(CtMethod.make(getName, cc));	
		String getQualifiers = "public java.util.Set getQualifiers() {java.util.Set qualifiers = new java.util.HashSet();qualifiers.add(ajf.persistence.jpa.JpaExtension.ANNOTATION_DEFAULT);qualifiers.add(ajf.persistence.jpa.JpaExtension.ANNOTATION_ANY);return qualifiers;}";
		cc.addMethod(CtMethod.make(getQualifiers, cc));
		String getScope = "public Class getScope() {return javax.enterprise.context.RequestScoped.class;}";
		cc.addMethod(CtMethod.make(getScope, cc));        
		String getStereotypes = "public java.util.Set getStereotypes() {return java.util.Collections.emptySet();}";
		cc.addMethod(CtMethod.make(getStereotypes, cc));
		String getTypes = "public java.util.Set getTypes() {java.util.Set types = new java.util.HashSet();types.add("+impl.getName()+".class);types.add("+in.getName()+".class);types.add(Object.class);return types;}";
		cc.addMethod(CtMethod.make(getTypes, cc));
		String isAlternative = "public boolean isAlternative() {return false;}";
		cc.addMethod(CtMethod.make(isAlternative, cc));
		String isNullable = "public boolean isNullable() {return false;}";
		cc.addMethod(CtMethod.make(isNullable, cc));
		String create = "public Object create(javax.enterprise.context.spi.CreationalContext ctx) {Object instance = it.produce(ctx);it.inject(instance, ctx);it.postConstruct(instance);return instance;}";
		cc.addMethod(CtMethod.make(create, cc));
		String destroy = "public void destroy(Object instance, javax.enterprise.context.spi.CreationalContext ctx) {it.preDestroy(instance);it.dispose(instance);ctx.release();}";
		cc.addMethod(CtMethod.make(destroy, cc));
		
		// Use reflection as there is nothing else to init the bean InjectionTarget. 
		// This should be only executed on Bean object creation which occur once per scope.
		Object obj = cc.toClass().newInstance();
		logger.debug("Bean class : " + obj.getClass());
		for (Field f : obj.getClass().getFields()) {
			logger.debug("field "+f.getName()+": "+f.getType());
		}
		obj.getClass().getField("it").set(obj, it);
		return (Bean<?>) obj;
	}

	/**
	 * @todo Maintain a pool of class to only add the needed classloader.
	 */
	public void addClasspathFor(Class<?> clazz) {
		pool.insertClassPath(new ClassClassPath(clazz));		
	}
	
}
