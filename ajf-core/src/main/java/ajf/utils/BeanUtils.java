package ajf.utils;

import static ajf.injection.DependenciesInjector.inject;

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
	public static Object instanciate(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		
		// call the default constructor
		Object bean = clazz.newInstance();
		// inject dependencies
		inject(bean);
		
		return bean;
		
	}

}
