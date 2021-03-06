package am.ajf.core.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassUtils {

	private static final String SERVICE = "Service";
	private static final String BD = "BD";
	private static final String SERVICE_BD = "ServiceBD";
	private static final String DAO = "DAO";
	private static final String CORE_DAO = ".core.dao.";
	private static final String LIB_MODEL = ".lib.model.";

	private static final String LIB_SERVICES = ".lib.services.";
	private static final String CORE_SERVICES = ".core.services.";

	private static final String POLICY = "Policy";
	private static final String LIB_BUSINESS = ".lib.business.";
	private static final String CORE_BUSINESS = ".core.business.";

	private static final String UI_CONTROLLER = ".web.controllers.";
	private static final String MBean = "MBean";
	
	/**
	 * Default constructor
	 */
	private ClassUtils() {
		super();
	}

	/**
	 * Utility method used to fetch Class list based on a package name.
	 * 
	 * @param packageName
	 *            (should be the package containing your annotated beans.
	 */
	public static List<Class<?>> getClasses(String packageName) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = packageName.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		}
		catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (fileName.endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.'
							+ fileName.substring(0, fileName.length() - 6)));
				}
			}
		}
		else {
			throw new ClassNotFoundException(packageName
					+ " is not a valid package");
		}

		return classes;
	}

	/*
	 * DAO in [project].core.dao.[Entity]DAO Entity in
	 * [project].lib.model.[Entity]
	 */
	public static String processEntityClassName(Class<?> daoClass) {

		String entityClassName = daoClass.getName()
				.replace(CORE_DAO, LIB_MODEL);
		// remove ~DAO
		entityClassName = entityClassName.substring(0,
				entityClassName.length() - 3);
		return entityClassName;
	}

	/*
	 * Entity in [project].lib.model.[Entity] DAO in
	 * [project].core.dao.[Entity]DAO
	 */
	public static String processDAOClassName(Class<?> entityClass) {

		String daoClassName = entityClass.getName()
				.replace(LIB_MODEL, CORE_DAO);
		// add ~DAO
		daoClassName = daoClassName.concat(DAO);

		return daoClassName;

	}

	/*
	 * Service Interface in [project].lib.services.[ServiceName]ServiceBD
	 * Service Impl in [project].core.services.[ServiceName]Service Service
	 * Interface in [project].lib.business.[ServiceName]BD Service Impl in
	 * [project].core.business.[ServiceName]Policy
	 */
	public static String processServiceImplName(Class<?> serviceClass) {

		String serviceName = serviceClass.getName();

		String serviceImplName = "";
		if (serviceName.contains(LIB_SERVICES)) {
			serviceImplName = serviceName.replace(LIB_SERVICES, CORE_SERVICES);
			// remove ~BD
			serviceImplName = serviceImplName.substring(0,
					serviceImplName.length() - 2);
			return serviceImplName;
		}

		if (serviceName.contains(LIB_BUSINESS)) {
			serviceImplName = serviceName.replace(LIB_BUSINESS, CORE_BUSINESS);
			// remove ~BD and add ~Policy
			serviceImplName = serviceImplName.substring(0,
					serviceImplName.length() - BD.length()).concat(POLICY);
			return serviceImplName;
		}

		return null;

	}
	
	public static String processServiceInterfaceName(Class<?> serviceClass) {

		String serviceName = serviceClass.getName();

		String serviceInterfaceName = "";
		if (serviceName.contains(CORE_SERVICES)) {
			serviceInterfaceName = serviceName.replace(CORE_SERVICES, LIB_SERVICES);
			// add ~BD
			serviceInterfaceName = serviceInterfaceName.concat(BD);
			return serviceInterfaceName;
		}

		if (serviceName.contains(CORE_BUSINESS)) {
			serviceInterfaceName = serviceName.replace(CORE_BUSINESS, LIB_BUSINESS);
			// remove ~Policy and add ~BD
			serviceInterfaceName = serviceInterfaceName.substring(0,
					serviceInterfaceName.length() - POLICY.length()).concat(BD);
			return serviceInterfaceName;
		}

		return null;

	}

	/**
	 * test is the specified interface is DAO
	 * 
	 * @param serviceClass
	 * @return boolean value if class is a DAO
	 */
	public static boolean isDAO(Class<?> serviceClass) {
		if (!serviceClass.isInterface()) return false;
		return serviceClass.getName().contains(CORE_DAO);
	}

	/**
	 * test is the specified interface is a technical or business Service
	 * 
	 * @param serviceClass
	 * @return
	 */
	public static boolean isPolicyOrServiceInterface(Class<?> serviceClass) {
		if (!serviceClass.isInterface()) 
			return false;
		
		boolean res = false;
		
		res = isServiceInterface(serviceClass);
		if (res)
			return true;

		res = isPolicyInterface(serviceClass);
		return res;
		
	}

	public static boolean isServiceInterface(Class<?> serviceClass) {
		String serviceName = serviceClass.getName();
		if (serviceName.contains(LIB_SERVICES)) {
			// end with ~ServiceBD
			return serviceName.endsWith(SERVICE_BD);
		}
		return false;
	}
	
	public static boolean isPolicyInterface(Class<?> serviceClass) {
		String serviceName = serviceClass.getName();
		if (serviceName.contains(LIB_BUSINESS)) {
			// end with ~ServiceBD
			return serviceName.endsWith(BD);
		}
		return false;
	}
	
	public static boolean isPolicyOrServiceImpl(Class<?> serviceClass) {
		if (serviceClass.isInterface()) 
			return false;
		/*
		if (Modifier.isAbstract(serviceClass.getModifiers()))
			return false;
		*/
		
		boolean res = false;
		
		res = isServiceImpl(serviceClass);
		if (res)
			return true;

		res = isPolicyImpl(serviceClass);
		return res;
		
	}

	public static boolean isPolicyImpl(Class<?> serviceClass) {
		boolean res = false;
		String serviceName = serviceClass.getName();
		if (serviceName.contains(CORE_BUSINESS)) {
			// end with ~Policy
			res = serviceName.endsWith(POLICY);
		}
		return res;
	}

	public static boolean isServiceImpl(Class<?> serviceClass) {
		boolean res = false;
		String serviceName = serviceClass.getName();
		if (serviceName.contains(CORE_SERVICES)) {
			// end with ~Service
			res = serviceName.endsWith(SERVICE);
		}
		return res;
	}

	/**
	 * Check if class is a web controller, means, in the .web.controllers package
	 * and if is terminated by MBean
	 * @param serviceClass class to test
	 * @return boolean value
	 */
	public static boolean isWebController(Class<?> serviceClass) {
		boolean res = false;
		String serviceName = serviceClass.getName();
		if (serviceName.contains(UI_CONTROLLER)) {
			
			res = serviceName.endsWith(MBean);
		}
		return res;
	}
	
	/**
	 * add class in the class loader
	 * @param className full class name to load
	 * @return loaded class
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		return cld.loadClass(className);
	}

	/**
	 * 
	 * @return the caller class name
	 */
	public static String giveCurrentClassName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerClassName = stack[1].getClassName();
		return callerClassName;
	}

	/**
	 * 
	 * @return the caller simple class name
	 */
	public static String getSimpleClassName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerClassName = stack[1].getClassName();
		int pos = callerClassName.lastIndexOf(".");
		if (pos > 0) {
			callerClassName = callerClassName.substring(pos + 1);
		}
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
	 * @param className
	 * @return the requested class
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String className) throws ClassNotFoundException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		Class<?> clazz = classLoader.loadClass(className);
		return clazz;
	}

}
