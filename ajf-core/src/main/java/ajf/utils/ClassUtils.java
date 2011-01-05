package ajf.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassUtils {

	/**
	 * Utility method used to fetch Class list based on a package name.
	 * 
	 * @param packageName
	 *            (should be the package containing your annotated beans.
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getClasses(String packageName) throws Exception {
		List<Class> classes = new ArrayList<Class>();
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
		} catch (NullPointerException x) {
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
		} else {
			throw new ClassNotFoundException(packageName
					+ " is not a valid package");
		}

		return classes;
	}
	
	/*
	 * DAO in [project].core.dao.[Entity]DAO
	 * Entity in [project].lib.model.[Entity]
	 */
	public static String processEntityClassName(Class<?> daoClass) {
		
		String entityClassName = daoClass.getName().replace(".core.dao.", ".lib.model.");
		// remove ~DAO
		entityClassName = entityClassName.substring(0, entityClassName.length()-3);
		return entityClassName;
	}
	
	/*
	 * Entity in [project].lib.model.[Entity]
	 * DAO in [project].core.dao.[Entity]DAO
	 */
	public static String processDAOClassName(Class<?> entityClass) {
		
		String daoClassName = entityClass.getName().replace(".lib.model.", ".core.dao.");
		// add ~DAO
		daoClassName = daoClassName.concat("DAO");
		
		return daoClassName;
		
	}
	
	/*
	 * Service Interface in [project].lib.services.[ServiceName]ServiceBD
	 * Service Impl in [project].core.services.[ServiceName]Service
	 */
	public static String processServiceClassName(Class<?> serviceClass) {
		
		String serviceClassName = serviceClass.getName().replace(".lib.services.", ".core.services.");
		// remove ~BD
		serviceClassName = serviceClassName.substring(0, serviceClassName.length()-2);
		
		return serviceClassName;
		
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		return cld.loadClass(className);
	}
	
	
	
}
