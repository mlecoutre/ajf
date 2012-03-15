package am.ajf.core.utils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

/**
 * This helper class contain commonly used methods to manipulate
 * Javaassist component that doesnt exist in the existing Javaassist helper.
 * 
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class JavassistUtils {
	
	
	
	private JavassistUtils() {
		super();
	}

	/**
	 * Generate a SLF4J Logger field that can be used in your Javassist class. 
	 * 
	 * @param cc the class that will contain the field
	 * @return the newly created logger instance
	 * @throws CannotCompileException
	 */
	public static CtField createLoggerField(CtClass cc) throws CannotCompileException {
		CtField cLogger = CtField.make("private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("+cc.getName()+".class);", cc);
		//CtField cLogger = CtField.make("private final transient org.slf4j.Logger logger = am.ajf.core.logger.LoggerFactory.getLogger(this.getClass());", cc);				
		return cLogger;
	}

	/**
	 * Create a new Class, that extends superClass if it's not null and that
	 * implement the given interface 
	 * 
	 * @param superClass can be null
	 * @param interfaceClass can't be null
	 * @param pool 
	 * @return the newly created class
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public static CtClass initClass(Class<?> superClass, Class<?> interfaceClass, ClassPool pool, String namingHint)
			throws NotFoundException, CannotCompileException {

		//init the classloader this might need to be optimized
		pool.insertClassPath(new ClassClassPath(interfaceClass));
		if (superClass != null) {// Fix NPE on 18653
			pool.insertClassPath(new ClassClassPath(superClass));
		}

		CtClass cc;
		CtClass cin = pool.get(interfaceClass.getName());
		if (superClass == null) {// no impl, so impl the interface
			cc = pool.makeClass(interfaceClass.getName() + "_$ajf$javassist$"+namingHint+"Service");		
			cc.setInterfaces(new CtClass[] {cin});
		} else { //extend the provided client impl
			cc = pool.makeClass(superClass.getName() + "$"+namingHint+"Service");		
			CtClass cim = pool.get(superClass.getName());
			cc.setSuperclass(cim);
			//to fix 19258, so it is easier to get the implemented interface 
			//whatever deep in the handler hierarchy you are.
			cc.setInterfaces(new CtClass[] {cin});
		}
		return cc;
	}	
	
	public static CtClass addAnnotation(ClassPool pool, CtClass cc, 
			Class<?> newAnnotation, Map<String, MemberValue> membersValueMap) throws NotFoundException {
		
		pool.insertClassPath(new ClassClassPath(newAnnotation));
		
		ClassFile classFile = cc.getClassFile();
		ConstPool constPool = classFile.getConstPool();
		
		String attributeName = AnnotationsAttribute.visibleTag;
		AnnotationsAttribute attrAnnotations = (AnnotationsAttribute) classFile.getAttribute(attributeName);
		if (null == attrAnnotations) {
			attrAnnotations = new AnnotationsAttribute(constPool, attributeName);
		}
			
		Annotation anno = new Annotation(constPool, pool.get(newAnnotation.getName()));
		
		// add annotation attributes
		if ((null != membersValueMap) && (!membersValueMap.isEmpty())) {
			Set<Entry<String, MemberValue>> entries = membersValueMap.entrySet();
			Iterator<Entry<String, MemberValue>> entriesIterator = entries.iterator();
			while (entriesIterator.hasNext()) {
				Entry<String, MemberValue> entry = entriesIterator.next();
				anno.addMemberValue(entry.getKey(), entry.getValue() );
			}
		}
		
		// add the new annotation
		attrAnnotations.addAnnotation(anno);
			
		// set annotations
		classFile.addAttribute(attrAnnotations);
		
		return cc;
			
	}
	
	public static CtClass addAnnotation(ClassPool pool, CtClass cc, 
			Class<?> newAnnotation) throws NotFoundException {
		
		return addAnnotation(pool, cc, newAnnotation, null);
		
	}
	
	public static CtMethod copyAnnotations(CtMethod srcMethod, CtMethod targetMethod) {
		
		MethodInfo mi = srcMethod.getMethodInfo();
		
		try {
			AnnotationsAttribute visible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.visibleTag);
			//for adding methods annotations
			targetMethod.getMethodInfo().addAttribute(visible);
		} catch (Exception e) {
			// Nothing to do
		}
		
		/**
		try {
			AnnotationsAttribute invisible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.invisibleTag);
			//for adding methods annotations
			targetMethod.getMethodInfo().addAttribute(invisible);
		} catch (Exception e) {
			// Nothing to do
		}
		**/
		
		return targetMethod;
		
	}
	
	public static CtClass copyAnnotations(CtClass srcClass, CtClass targetClass) {
		
		String attributeName = AnnotationsAttribute.visibleTag;
		
		try {
			ClassFile srcClassFile = srcClass.getClassFile();
			AnnotationsAttribute srcAttrAnnotations = (AnnotationsAttribute) srcClassFile.getAttribute(attributeName);
			
			ClassFile targetClassFile = targetClass.getClassFile();
			AnnotationsAttribute targetAttrAnnotations = (AnnotationsAttribute) targetClassFile.getAttribute(attributeName);
			if (null == targetAttrAnnotations) {
				targetAttrAnnotations = new AnnotationsAttribute(targetClassFile.getConstPool(), attributeName);
			}
					
			if (null != srcAttrAnnotations) {
				Annotation[] annotations = srcAttrAnnotations.getAnnotations();
				for (Annotation srcAnnotation : annotations) {
					String annotationType = srcAnnotation.getTypeName();
					
					Annotation targetAnnotation = targetAttrAnnotations.getAnnotation(annotationType);
					if (null == targetAnnotation) {
						targetAttrAnnotations.addAnnotation(srcAnnotation);
					}
				}
			}
			
			// set annotations
			targetClassFile.addAttribute(targetAttrAnnotations);
			
		} catch (Exception e) {
			// Nothing to do
		}
		
		
		
		/*
		try {
			AnnotationsAttribute visible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.visibleTag);
			//for adding methods annotations
			targetMethod.getMethodInfo().addAttribute(visible);
		} catch (Exception e) {
			// Nothing to do
		}
		*/
		
		/**
		try {
			AnnotationsAttribute invisible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.invisibleTag);
			//for adding methods annotations
			targetMethod.getMethodInfo().addAttribute(invisible);
		} catch (Exception e) {
			// Nothing to do
		}
		**/
		
		return targetClass;
		
	}
	
	/*
	public static void displayAnnotations(CtMethod ctSuperClassMethod) {
		MethodInfo mi = ctSuperClassMethod.getMethodInfo();
	
		try {
			AnnotationsAttribute visible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.visibleTag);
			for (Annotation ann : visible.getAnnotations())
			{
			     System.out.println("@" + ann.getTypeName());
			}
		} catch (Exception e) {
			// Nothing to do
		}
		
		try {
			AnnotationsAttribute invisible = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.invisibleTag);
			for (Annotation ann : invisible.getAnnotations())
			{
			     System.out.println("@" + ann.getTypeName());
			}
		} catch (Exception e) {
			// Nothing to do
		}			
		
	}
	*/

	protected static String generateDelegateBodyFor(Method method) {
		
		// Starting the generation
		
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("\treturn super.").append(method.getName()).append("($$);\n");
		body.append("}\n");
		
		return body.toString();		
	}
	
	public static CtMethod buildDelegateMethod(Class<?> superClass, Method methodFromInterface, ClassPool pool, CtClass cc) 
			throws NotFoundException, CannotCompileException {
		
		pool.insertClassPath(new ClassClassPath(superClass));
		pool.insertClassPath(new ClassClassPath(methodFromInterface.getDeclaringClass()));
		
		CtClass ctMethodInterfaceDeclaringClass = pool.get(methodFromInterface.getDeclaringClass().getName());
		CtMethod ctMethodFromInterface = ctMethodInterfaceDeclaringClass.getDeclaredMethod(methodFromInterface.getName());	
		
		CtClass ctSuperClass = pool.get(superClass.getName());
		CtMethod ctSuperClassMethod = ctSuperClass.getDeclaredMethod(methodFromInterface.getName(), ctMethodFromInterface.getParameterTypes());
		
		CtMethod newCtm = CtNewMethod.copy(ctSuperClassMethod, cc, null);
		copyAnnotations(ctSuperClassMethod, newCtm);
						
		String methodBody = JavassistUtils.generateDelegateBodyFor(methodFromInterface);
		newCtm.setBody(methodBody);
		
		return newCtm;
		
	}
	
}
