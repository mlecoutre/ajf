package am.ajf.injection.internal;

import static am.ajf.core.utils.JavassistUtils.addAnnotation;
import static am.ajf.core.utils.JavassistUtils.buildDelegateMethod;
import static am.ajf.core.utils.JavassistUtils.copyAnnotations;
import static am.ajf.core.utils.JavassistUtils.initClass;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.injection.ClassGenerationException;
import am.ajf.injection.annotation.Bean;

public class BeanBuilder {
	
	public static final BeanBuilder DEFAULT = new BeanBuilder();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ClassPool pool;
		
	public BeanBuilder() {
		super();
		pool = ClassPool.getDefault();
	}
	
	
	
	public Class<?> implementSubClassFor(Class<?> superClass, Class<?> interfaceClass, 
			String classRole, boolean isDefault) throws ClassGenerationException {
		Class<?> clazz;
		try {
			
			pool.insertClassPath(new ClassClassPath(superClass));
			pool.insertClassPath(new ClassClassPath(interfaceClass));
			
			CtClass targetClass = initClass(superClass, interfaceClass, pool, classRole);
			logger.info(String.format("Generate Class: %s", targetClass.getName()));

			annotateClass(isDefault, classRole, targetClass);
			
			CtClass srcClass = pool.get(superClass.getName());
			copyAnnotations(srcClass, targetClass);
			
			//generate delegate for each method 
			Method[] methods = interfaceClass.getMethods();
			for (Method method : methods) {
				
				CtMethod newCtm = buildDelegateMethod(superClass, method, pool, targetClass);
				targetClass.addMethod(newCtm);
				
			}			
			
			clazz = targetClass.toClass();
			
		} catch (NotFoundException e) {
			throw new ClassGenerationException("Impossible to find the class : ", e); 
		} catch (CannotCompileException e) {
			throw new ClassGenerationException("Impossible to compile code : ", e);
		} 
		
		return clazz;
	}

	protected CtClass annotateClass(boolean isDefault, String classRole, CtClass cc)
			throws NotFoundException {
		
		ClassFile classFile = cc.getClassFile();
		ConstPool constPool = classFile.getConstPool();
		
		// add @Default annotation if required
		if (isDefault) {
			//addAnnotation(pool, cc, Default.class);
		}
		else {
			// add @Alternative annotation
			addAnnotation(pool, cc, Alternative.class);
		}
		
		// add @Any annotation
		addAnnotation(pool, cc, Any.class);
	
		// add @Bean(...) annotation
		
		// add member values
		MemberValue beanMemberValue = new StringMemberValue(classRole, constPool);
		
		Map<String, MemberValue> membersMap = new HashMap<String, MemberValue>();
		membersMap.put("value", beanMemberValue);
		
		addAnnotation(pool, cc, Bean.class, membersMap);
		
		return cc;
	
	}	
	
}
