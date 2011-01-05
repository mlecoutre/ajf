package ajf.persistence;

import static ajf.utils.BeanUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ajf.persistence.annotations.Param;
import ajf.utils.ClassUtils;

public class DAOMetadata {

	private PersistenceUnitDesc persistenceUnitDesc = null;

	private Class<?> entityClass = null;

	private Class<?> daoClass = null;
	private Map<String, String[]> daoMethodsNammedParametersMap = null;

	private Class<?> daoDelegateClass = null;
	private Map<String, Method> daoDelegateMethodsMap = null;

	public DAOMetadata(PersistenceUnitDesc persistenceUnitDesc,
			Class<?> entityClass) throws ClassNotFoundException {
		super();

		this.persistenceUnitDesc = persistenceUnitDesc;
		this.entityClass = entityClass;

		initialize();

	}

	/**
	 * 
	 * @param daoClass
	 * @return
	 */
	private String buildDaoDelegateClassName(Class<?> daoClass) {
		String daoDelegateClassName = daoClass.getName().concat("Delegate");
		return daoDelegateClassName;
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 */
	private void initialize() throws ClassNotFoundException {

		// the current classLoader
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		// process candidate DAO className
		String daoCandidate = ClassUtils.processDAOClassName(entityClass);

		// check the DAO class
		this.daoClass = classLoader.loadClass(daoCandidate);

		// visit and register the DAO methods parameters
		visitDAOMethodsNammedParameters(this.daoClass);

		// resolve the DAODelegate class name
		String daoDelegateClassName = buildDaoDelegateClassName(daoClass);
		try {
			// check if a DAO delegate exist
			this.daoDelegateClass = classLoader.loadClass(daoDelegateClassName);
			// list the DAO delegate methods
			this.daoDelegateMethodsMap = listMethodsAsMap(this.daoDelegateClass);
		} catch (Exception e) {
			this.daoDelegateClass = null;
		}

	}

	/**
	 * 
	 */
	private void visitDAOMethodsNammedParameters(Class<?> daoClass) {
		
		this.daoMethodsNammedParametersMap = new HashMap<String, String[]>();

		for (Method daoMethod : daoClass.getMethods()) {

			if (null != daoMethod.getParameterTypes()) {
				int numParams = daoMethod.getParameterTypes().length;
				if (numParams > 0) {

					List<String> argNames = new ArrayList<String>();

					Annotation[][] paramsAnnotations = daoMethod
							.getParameterAnnotations();
					if (paramsAnnotations.length >= numParams) {
						for (int idx = 0; idx < numParams; idx++) {
							Annotation[] paramAnnotations = paramsAnnotations[idx];
							boolean foundParamAnnoation = false;
							for (int j = 0; j < paramAnnotations.length; j++) {
								Annotation annota = paramAnnotations[j];
								if (annota.annotationType().equals(Param.class)) {
									argNames.add(((Param) annota).value());
									foundParamAnnoation = true;
									break;
								}
							}
							if (!foundParamAnnoation) {
								argNames = null;
								break;
							}
						}

					}
					String[] paramNames = null;
					if ((null != argNames) && (!argNames.isEmpty())) {
						paramNames = argNames.toArray(new String[0]);
						this.daoMethodsNammedParametersMap.put(
								daoMethod.getName(), paramNames);
					}

				}
			}

		}
	}

	/**
	 * @return the persistenceUnitDesc
	 */
	public PersistenceUnitDesc getPersistenceUnitDesc() {
		return persistenceUnitDesc;
	}

	/**
	 * @return the entityClass
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * @return the daoClass
	 */
	public Class<?> getDaoClass() {
		return daoClass;
	}

	/**
	 * @return the daoMethodsNammedParameters
	 */
	public Map<String, String[]> getDaoMethodsNammedParametersMap() {
		return daoMethodsNammedParametersMap;
	}

	/**
	 * @return the daoDelegateClass
	 */
	public Class<?> getDaoDelegateClass() {
		return daoDelegateClass;
	}

	/**
	 * @return the daoDelegateMethods
	 */
	public Map<String, Method> getDaoDelegateMethodsMap() {
		return daoDelegateMethodsMap;
	}

}
