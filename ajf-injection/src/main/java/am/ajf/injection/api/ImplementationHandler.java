package am.ajf.injection.api;

import java.lang.reflect.Method;
import java.util.List;

import am.ajf.injection.ClassGenerationException;

/**
 * Interface for Handlers used by ajf-injection to auto generate methods
 * for business interfaces without implementation.
 * 
 * 
 * @author Nicolas Radde (E016696)
 */
public interface ImplementationHandler {
	
	/**
	 * Compute if the handler can implement the given method.
	 * This usually involved checking the method name against a pattern or
	 * the presence of an annotation on the method and/or class. 
	 * 
	 * @param method that need to be handled
	 * @return if the method can be implementated by this handler
	 */
	boolean canHandle(Method method);

	/**
	 * Implement the given list of methods in the return class.
	 * The returned class MUST do the following :
	 * <ul>
	 * 		<li>Implement the given Interface</li>
	 * 		<li>Extend the given superClass if it is not null</li>
	 * </ul>
	 * 
	 * The returned class CAN :
	 * <ul>
	 * 		<li>Be abstract, if the handler dont implements all methods of the interface</li>
	 * </ul>
	 * 
	 * @param superClass can be null
	 * @param interfaceClass cant be null
	 * @param methods the methods to implements
	 * @return a new Class that followed the given rules
	 */
	Class<?> implementMethodsFor(Class<?> superClass, Class<?> interfaceClass, List<Method> methods)
				throws ClassGenerationException;
	
}
