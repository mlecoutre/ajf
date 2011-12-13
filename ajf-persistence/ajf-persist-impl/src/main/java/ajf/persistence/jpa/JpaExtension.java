package ajf.persistence.jpa;

//import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.annotation.NamedQuery;
import ajf.persistence.jpa.annotation.QueryParam;

public class JpaExtension implements Extension {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Class<?>, Boolean> haveAnImplementation;
	public static java.lang.annotation.Annotation ANNOTATION_DEFAULT = new AnnotationLiteral<Default>() {};
	public static java.lang.annotation.Annotation ANNOTATION_ANY = new AnnotationLiteral<Any>() {};
	private ClassPool pool;
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {		
		logger.debug("beginning the scanning process for extension JpaExtension");
		pool = ClassPool.getDefault();				
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		logger.debug("scanning type: "
				+ pat.getAnnotatedType().getJavaClass().getName());
		//init the dependency tree
		if (haveAnImplementation == null) {
			haveAnImplementation = new HashMap<Class<?>, Boolean>();
		}
		
		//process the interfaces
		//init the interface at false if it doesnt exist (the interface was scanned before the impl)
		if (pat.getAnnotatedType().getJavaClass().getName().endsWith("ServiceBD")) {
			logger.debug("adding classloader for : "+pat.getAnnotatedType().getJavaClass());
			//TODO application classes and extension classes might not be on same class loader (Tomcat, WAS)
			pool.insertClassPath(new ClassClassPath(pat.getAnnotatedType().getJavaClass()));
			if (!haveAnImplementation.containsKey(pat.getAnnotatedType().getJavaClass())) {
				haveAnImplementation.put(pat.getAnnotatedType().getJavaClass(), false);				
			}
		}
		//process the implementations
		//for each impl found, set the interface as having an impl.
		if (pat.getAnnotatedType().getJavaClass().getName().endsWith("Service")) {
			for (Class<?> in : pat.getAnnotatedType().getJavaClass().getInterfaces()) {
				if (in.getName().endsWith("ServiceBD")) {					
					haveAnImplementation.put(in, true);
				}
			}
		}
	}
	
	public <T> void ProcessInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		logger.debug("			injection target: "
				+ pit.getAnnotatedType().getJavaClass().getName());
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {
		logger.debug("			bean : "
				+ pb.getBean().getBeanClass().getName());
	}
	
	
	

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		logger.debug("finished the scanning process for extension JpaExtension, printing all beans found :");
		Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});
		for (Bean<?> bean : allBeans) {
			logger.debug("   bean : " + bean.getBeanClass().getName());
		}
		logger.debug("dump the dep graph");
		
		for (Class<?> in : haveAnImplementation.keySet()) {
			logger.debug(in.getName() + " : "+haveAnImplementation.get(in));
			if (!haveAnImplementation.get(in)) {
				Class<?> impl = createImpl(pool, in);
				 //use this to read annotations of the class
		        AnnotatedType<?> at = beanManager.createAnnotatedType(impl); 
		        //use this to instantiate the class and inject dependencies
		        final InjectionTarget<?> it = beanManager.createInjectionTarget(at);	
		        abd.addBean(createBeanFromImpl(pool, impl,in, it));				
			}
		}
		
	}
	
	private Class<?> createImpl(ClassPool pool, Class<?> serviceBD) throws CannotCompileException, NotFoundException, ClassNotFoundException {
		//ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass(serviceBD.getName() + "_" + UUID.randomUUID().toString().replace("-", ""));
		
		CtClass ci = pool.get(serviceBD.getName());
		cc.setInterfaces(new CtClass[] {ci});
		
		CtField cLogger = CtField.make("private final transient Logger logger = LoggerFactory.getLogger(this.getClass());", cc);
		//CtField em = CtField.make("private javax.persistence.EntityManager em;", cc);		
		//AnnotationsAttribute attribute = new AnnotationsAttribute(cc.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
		//Annotation pcAnnotation = new Annotation("javax.persistence.PersistenceContext",cc.getClassFile().getConstPool());
		//Annotation pcAnnotation = new Annotation("javax.inject.Inject",cc.getClassFile().getConstPool());		
		//attribute.addAnnotation(pcAnnotation);
		//em.getFieldInfo().addAttribute(attribute);
		
		cc.addField(cLogger);
		
		for (CtMethod method : ci.getMethods()) {
			CtMethod newCtm = new CtMethod(method, cc, null);
			if (Modifier.isAbstract(method.getModifiers())) { 
				logger.debug(newCtm.getLongName() + " : "+ Modifier.toString(method.getModifiers()));
				logger.debug(method.getSignature());
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
					body.append("  logger.debug(\"launching query "+namedQuery.name()+"\");");
					body.append("  javax.persistence.EntityManager em = ajf.persistence.jpa.EntityManagerProvider.getEntityManager(\"default\");");
					body.append("  javax.persistence.Query query = em.createNamedQuery(\""+namedQuery.name()+"\");");
					for (int i = 0 ; i < pTypes.length ; i++) {
						QueryParam param = (QueryParam) pAnnotations[i][0];						
						body.append("  query = query.setParameter(\""+param.value()+"\", $"+(i+1)+");");
					}
					body.append("  return query.getResultList();");
					body.append("}\n");
					logger.debug("method "+method.getName()+" body : "+body.toString());
					newCtm.setBody(body.toString());
				}
				cc.addMethod(newCtm);			
			}	
		}
		return cc.toClass();
	}
	
	private Bean<?> createBeanFromImpl(ClassPool pool, Class<?> impl, Class<?> in, final InjectionTarget<?> it) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException {
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
	

}
