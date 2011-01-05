package ajf.utils;

import static ajf.injection.DependenciesInjector.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author vincent claeysen
 * 
 */
public abstract class BeanUtils {

	/**
	 * 
	 * @param clazz
	 * @return a Map of all public methods
	 */
	public static Map<String, Method> listMethodsAsMap(Class<?> clazz) {

		Map<String, Method> daoMethodsMap = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		if (null != methods) {
			for (Method method : methods) {
				if (!Object.class.equals(method.getDeclaringClass()))
					daoMethodsMap.put(method.getName(), method);
			}
		}
		return daoMethodsMap;
	}

	/**
	 * 
	 * @param clazz
	 * @return a new initialized instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instanciate(Class<?> clazz)
			throws InstantiationException, IllegalAccessException {

		// call the default constructor
		Object bean = clazz.newInstance();
		// inject dependencies
		inject(bean);

		return (T) bean;

	}

	/**
	 * 
	 * @return the caller class name
	 */
	public static String getClassName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerClassName = stack[1].getClassName();
		return callerClassName;
	}

	/**
	 * 
	 * @return the caller method name
	 */
	public static String getMethodName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerMethodName = stack[1].getMethodName();
		return callerMethodName;
	}

}
