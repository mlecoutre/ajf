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
	
	/**
	 * find the entityClass
	 * @param requestedDAO
	 * @return the corresponding entity class 
	 * @throws ClassNotFoundException
	 */
	public static Class<?> resolveEntityClass(Class<?> daoClass)
			throws ClassNotFoundException {
		String entityClassName = processEntityClassName(daoClass);
		// retrieve the entity class name
		Class<?> entityClass = Thread.currentThread()
				.getContextClassLoader().loadClass(entityClassName);
		return entityClass;
	}

	
	
	/*
	 * DAO in [project].core.dao.[Entity]DAO
	 * Entity in [project].lib.model.[Entity]
	 */
	public static String processEntityClassName(Class<?> daoClass) {
		
		// replace ".core.dao" by ".lib.model" 
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
		
		// replace ".core.dao" by ".lib.model" 
		String daoClassName = entityClass.getName().replace(".lib.model.", ".core.dao.");
		// add ~DAO
		daoClassName = daoClassName.concat("DAO");
		
		return daoClassName;
		
	}
	
}
