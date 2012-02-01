package am.ajf.core.utils;

import javassist.CannotCompileException;
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
		CtField cLogger = CtField.make("private final transient org.slf4j.Logger logger = am.ajf.core.logger.LoggerFactory.getLogger(this.getClass());", cc);				
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
	public static CtClass initClass(Class<?> superClass, Class<?> interfaceClass, ClassPool pool)
			throws NotFoundException, CannotCompileException {
				CtClass cc;
				CtClass cin = pool.get(interfaceClass.getName());
				if (superClass == null) {// no impl, so impl the interface
					cc = pool.makeClass(interfaceClass.getName() + generateClassSuffix());		
					cc.setInterfaces(new CtClass[] {cin});
				} else { //extend the provided client impl
					cc = pool.makeClass(superClass.getName() + generateClassSuffix());		
					CtClass cim = pool.get(superClass.getName());
					cc.setSuperclass(cim);
				}
				return cc;
			}

	private static String generateClassSuffix() {
		return "_$ajf$javaassist$proxy$Service";
	}

}
