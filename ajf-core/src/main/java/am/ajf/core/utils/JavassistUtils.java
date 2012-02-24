package am.ajf.core.utils;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

/**
 * This helper class contain commonly used methods to manipulate
 * Javaassist component that doesnt exist in the existing Javaassist helper.
 * 
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class JavassistUtils {
	
	/**
	 * Generate a SLF4J Logger field that can be used in your Javassist class. 
	 * 
	 * @param cc the class that will contain the field
	 * @return the newly created logger instance
	 * @throws CannotCompileException
	 */
	public static CtField createLogger(CtClass cc) throws CannotCompileException {
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
}
