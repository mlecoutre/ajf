package am.ajf.persistence.jpa.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
import javassist.bytecode.annotation.StringMemberValue;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.ImplementationGenerator;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;

@Named
public class JavaassistImplementationGenerator implements ImplementationGenerator {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	private ClassPool pool;
	private List<MethodGenerator> methodGenerators;
	
	public JavaassistImplementationGenerator() {
		pool = ClassPool.getDefault();
		
		//The order matter
		methodGenerators = new ArrayList<MethodGenerator>();
		methodGenerators.add(new NamedQueryMethodGenerator());
		methodGenerators.add(new CrudDbMethodGenerator());
		methodGenerators.add(new StoredProcedureMethodGenerator());
	}
	
	private String generateClassSuffix() {
		return "_$ajf$javaassist$proxy$";
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
		PersistenceUnit puAnn = null;
		if (cc.hasAnnotation(PersistenceUnit.class)) {
			puAnn = (PersistenceUnit) cc.getAnnotation(PersistenceUnit.class);
		}
		
		CtField cLogger = CtField.make("private final transient org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());", cc);		
		cc.addField(cLogger);
		
		CtField cEmf = CtField.make("private transient javax.persistence.EntityManagerFactory emf;", cc);
		AnnotationsAttribute attribute = new AnnotationsAttribute(cc.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
		Annotation injectAnnotation = new Annotation(cc.getClassFile().getConstPool(), ClassPool.getDefault().get("javax.inject.Inject"));
		Annotation puAnnotation = new Annotation(cc.getClassFile().getConstPool(), ClassPool.getDefault().get("am.ajf.persistence.jpa.annotation.PersistenceUnit"));
		StringMemberValue mv = (StringMemberValue) Annotation.createMemberValue(cc.getClassFile().getConstPool(), pool.get("java.lang.String"));
		if (puAnn != null) {			
			mv.setValue(puAnn.value());			
		} else {			
			mv.setValue("default");			
		}
		puAnnotation.addMemberValue("name", mv);
		attribute.addAnnotation(injectAnnotation);
		attribute.addAnnotation(puAnnotation);
		cEmf.getFieldInfo().addAttribute(attribute);
		cc.addField(cEmf);
		
		for (CtMethod method : cc.getMethods()) {
			CtMethod newCtm = new CtMethod(method, cc, null);			
			if (isImplNotNeeded(method)) {
				logger.debug("dont generate method : "+method.getLongName()+ " : "+ Modifier.toString(method.getModifiers())+" : "+method.getDeclaringClass().getName());
			} else {
				logger.debug("generate method : "+method.getLongName()+ " : "+ Modifier.toString(method.getModifiers())+" : "+method.getDeclaringClass().getName());
				//logger.debug(newCtm.getLongName() + " : "+ Modifier.toString(method.getModifiers()));
				//logger.debug(method.getSignature());
				
				//verify the method declaration is valid (will throw an exception)
				checkMethodDeclaration(newCtm);
				
				StringBuffer body = null; 
				
				//test all the generators in order, and take the first availaible one
				boolean generatorFound = false;
				for (MethodGenerator generator : methodGenerators) {
					if (generator.canImplement(method)) {
						body = generator.generateBodyFor(method);
						if (body == null) {
							throw new IllegalArgumentException("Error generating method body for "+method.getName() + " in : "+generator.getClass().getSimpleName());
						}
						generatorFound = true;
						break;
					}
				}
				if (!generatorFound) {
					throw new IllegalArgumentException("Error generating method body for "+method.getName() + ". No generator found.");
				}
								
				
				logger.debug("Generated method "+method.getName()+" body : \n"+body.toString());
				newCtm.setBody(body.toString());
					
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
	
	/**
	 * Perform routine check on method structure.
	 * If the check doesnt pass, the method will throw an exception.
	 * 
	 * @param method
	 * @throws NotFoundException
	 */
	private void checkMethodDeclaration(CtMethod method) throws NotFoundException {
		if (method.getReturnType().equals(CtPrimitiveType.voidType)) {
			throw new IllegalArgumentException("Method "+method.getName() +" of class " +method.getDeclaringClass().getName() 
												+ " cant return void !");
		}
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
		String getQualifiers = "public java.util.Set getQualifiers() {java.util.Set qualifiers = new java.util.HashSet();qualifiers.add(am.ajf.persistence.jpa.JpaExtension.ANNOTATION_DEFAULT);qualifiers.add(am.ajf.persistence.jpa.JpaExtension.ANNOTATION_ANY);return qualifiers;}";
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
