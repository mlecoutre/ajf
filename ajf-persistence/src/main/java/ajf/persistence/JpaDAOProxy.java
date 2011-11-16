package ajf.persistence;

import static ajf.utils.ClassUtils.listMethodsAsMap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ajf.persistence.exception.PersistenceLayerException;
import ajf.persistence.utils.PersistenceUtils;
import ajf.utils.BeanUtils;
import ajf.utils.helpers.XMLHelper;

public class JpaDAOProxy implements InvocationHandler {

	public final static String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	public final static String JTA = "JTA";

	private final static Class<?> BASE_PERSISTENCE_DAO_CLASS = BaseJpaDAOImpl.class;
	
	private final static Logger logger = LoggerFactory
			.getLogger(JpaDAOProxy.class);

	private static Object token = new Object();

	// the generated methods
	private static Set<String> generatedMethods = null;
	// the simple generated methods list
	private static List<String> simpleGeneratedMethodsList = null;

	// the DAO methods Map
	// private static Map<String, Map<String, Method>> daosMap = null;
	private static Map<String, Method> basePersistenceDAOMethodsMap = null;
	private static Map<String, DAOMetadata> daosMetadatasMap = null;

	private DAOMetadata daoMetadata = null;

	// the proxy instance datas
	private String persistenceUnitName;
	private boolean inJTA = true;

	private Class<?> entityClass = null;

	private Class<?> requestedDAO = null;
	private JpaDAO basePersistenceDAOImpl = null;
	private JpaDAO persistenceDAODelegateImpl = null;

	/* only for JPA 2 */
	// private boolean detachEntities = false;
	
	private boolean autoCommit = false;

	/**
	 * 
	 * @param daoClass
	 * @throws Exception 
	 */
	public JpaDAOProxy(Class<?> daoClass) throws Exception {

		// check init
		checkInit();

		this.daoMetadata = daosMetadatasMap.get(daoClass.getName());
		
		if (null == this.daoMetadata) 
			throw new NullPointerException("Unable to find DAO Metadatas.");
		
		this.requestedDAO = this.daoMetadata.getDaoClass();
		this.entityClass = this.daoMetadata.getEntityClass();

		PersistenceUnitDesc puDesc = this.daoMetadata.getPersistenceUnitDesc();
		this.persistenceUnitName = puDesc.getName();
		// if in JTA, obtain the transaction from JNDI with java:comp/UserTransaction
		this.inJTA = JTA.equalsIgnoreCase(puDesc.getTransactionType());

		// get a new persistence base dao impl instance
		this.basePersistenceDAOImpl = (JpaDAO) BeanUtils.newInstance(BASE_PERSISTENCE_DAO_CLASS);

		// get a new persistence dao delegate impl instance
		this.persistenceDAODelegateImpl = null;
		if (null != this.daoMetadata.getDaoDelegateClass()) {
			persistenceDAODelegateImpl = (JpaDAO) BeanUtils.newInstance(this.daoMetadata
					.getDaoDelegateClass());
		}

	}

	/*
	public boolean isDetachEntities() {
		return detachEntities;
	}

	public void setDetachEntities(boolean detachEntities) {
		this.detachEntities = detachEntities;
	}
	*/

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	
	/**
	 * 
	 * @param requestedDAO
	 * @param requestedMethod
	 * @param args
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Object[] processParameters(Class<?> requestedDAO,
			Class<?> entityClass, String requestedMethod, Method invokedMethod,
			Object[] args) throws ClassNotFoundException {

		Object[] params = args;

		if (simpleGeneratedMethodsList.contains(requestedMethod)) {
			params = new Object[] { args[0] };
		} else {
			if (requestedMethod.endsWith("ByPrimaryKey")) {
				params = new Object[] { entityClass, args[0] };
			} else {
				if ("findAll".equals(requestedMethod)) {
					params = new Object[] { entityClass };
				} else {
					String[] argParamsNames = null;
					if (this.daoMetadata.getDaoMethodsNammedParametersMap()
							.containsKey(invokedMethod.getName())) {
						argParamsNames = this.daoMetadata
								.getDaoMethodsNammedParametersMap().get(
										invokedMethod.getName());
					}
					params = new Object[] { entityClass, requestedMethod, args,
							argParamsNames };
				}

			}
		}
		return params;
	}

	/**
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void loadPersistence() throws ParserConfigurationException,
			SAXException, IOException {

		// find the persistence.xml file
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("META-INF/persistence.xml");

		// parse the file
		Document doc = XMLHelper.getDocument(is);
		Element root = (Element) doc.getFirstChild();

		// the current classLoader
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		// instanciate the DAOs Metadatas Map
		daosMetadatasMap = new HashMap<String, DAOMetadata>();

		NodeList nodes = root.getElementsByTagName("persistence-unit");
		if ((null != nodes) && (nodes.getLength() > 0)) {
			for (int index = 0; index < nodes.getLength(); index++) {
				Element puElem = (Element) nodes.item(index);
				String puName = puElem.getAttribute("name");
				String puTx = puElem.getAttribute("transaction-type");

				PersistenceUnitDesc puDesc = new PersistenceUnitDesc(puName,
						puTx);

				NodeList classes = puElem.getElementsByTagName("class");
				if ((null != classes) && (classes.getLength() > 0)) {
					for (int i = 0; i < classes.getLength(); i++) {
						String className = classes.item(i).getFirstChild()
								.getNodeValue();

						try {

							// check entity class
							Class<?> entityClass = classLoader
									.loadClass(className);

							// initialize the metadatas for the DAO
							DAOMetadata daoMetadata = new DAOMetadata(puDesc,
									entityClass);
							// register the DAO Metadatas
							daosMetadatasMap.put(daoMetadata.getDaoClass()
									.getName(), daoMetadata);

						} catch (ClassNotFoundException e) {
							// Entity not Found
						}

					}
				}

			}
		}
	}

	/**
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * 
	 */
	private void checkInit() throws ParserConfigurationException, SAXException,
			IOException {

		synchronized (token) {

			if (null == daosMetadatasMap) {

				// load the JPA persistence.xml
				loadPersistence();

				// register the BasePersistenceDAOImpl methods
				basePersistenceDAOMethodsMap = listMethodsAsMap(BaseJpaDAOImpl.class);
				// register the DAO methods
				generatedMethods = basePersistenceDAOMethodsMap.keySet();

				simpleGeneratedMethodsList = new ArrayList<String>();
				simpleGeneratedMethodsList.add("add");
				simpleGeneratedMethodsList.add("create");
				simpleGeneratedMethodsList.add("remove");
				simpleGeneratedMethodsList.add("delete");
				simpleGeneratedMethodsList.add("update");

			}

		}

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// get the corresponding EntityManager
		EntityManager entityManager = EntityManagerProvider
				.getEntityManager(this.persistenceUnitName);

		// set the current EntityManager
		basePersistenceDAOImpl.setEntityManager(entityManager);
		if (null != persistenceDAODelegateImpl)
			persistenceDAODelegateImpl.setEntityManager(entityManager);

		// the requested method name
		String requestedMethodName = method.getName();

		// the invocation parameters
		Object daoImpl = null;
		Object[] params = args;
		Method methodToInvoke = null;

		// the invocation result
		Object result = null;

		// for the settings setFirstResult(...) setMaxResults(...)
		if (requestedMethodName.startsWith("set")) {

			/*
			if ("setDetachEntities".equals(requestedMethodName)) {
				setDetachEntities((Boolean) args[0]);
			}
			*/
			if ("setAutoCommit".equals(requestedMethodName)) {
				setAutoCommit((Boolean) args[0]);
			}

			if (null != persistenceDAODelegateImpl) {
				daoImpl = persistenceDAODelegateImpl;
				methodToInvoke = this.daoMetadata.getDaoDelegateMethodsMap()
						.get(requestedMethodName);
				// methodToInvoke = daosMap.get(persistenceDAODelegateClassName)
				// .get(requestedMethodName);
				if (null != methodToInvoke) {
					result = methodToInvoke.invoke(daoImpl, args);
				}
			}

			// the DAO impl to invoke
			daoImpl = basePersistenceDAOImpl;
			methodToInvoke = basePersistenceDAOMethodsMap
					.get(requestedMethodName);
			if (null != methodToInvoke) {
				result = methodToInvoke.invoke(daoImpl, args);
			}

			return null;

		}

		// is the method in the delegate
		boolean inDAODelegate = false;
		if (null != persistenceDAODelegateImpl) {
			// get the requested method
			methodToInvoke = this.daoMetadata.getDaoDelegateMethodsMap().get(
					requestedMethodName);
			if (null != methodToInvoke) {
				daoImpl = persistenceDAODelegateImpl;
				inDAODelegate = true;
			}
		}

		// process the generated methods in the baseJpaDAOImpl
		if (null == methodToInvoke) {

			// the DAO impl to invoke
			daoImpl = basePersistenceDAOImpl;

			// is it a standard method
			if (generatedMethods.contains(requestedMethodName)) {
				// get the requested method
				methodToInvoke = basePersistenceDAOMethodsMap
						.get(requestedMethodName);
				// process the parameters
				params = processParameters(requestedDAO, entityClass,
							requestedMethodName, method, args);
			} else {
				// process the query name
				String queryName = entityClass.getSimpleName().concat(".")
						.concat(requestedMethodName);
				// process the parameters
				params = processParameters(requestedDAO, entityClass,
							queryName, method, args);

				// resolve the method to invoke
				if (requestedMethodName.startsWith("findSingleResult")) {
					// get the requested method
					methodToInvoke = basePersistenceDAOMethodsMap
							.get("findSingleResultQuery");
				} else {
					if (requestedMethodName.startsWith("find")) {
						// get the requested method
						methodToInvoke = basePersistenceDAOMethodsMap
								.get("findQuery");
					} else {
						// get the requested method
						methodToInvoke = basePersistenceDAOMethodsMap
								.get("executeQuery");
					}
				}

			}
		}

		if (null == methodToInvoke) {
			throw new NullPointerException("The requested method '"
					+ requestedMethodName + "' is not implemented.");
		}

		try {
			if ((!inJTA) && (autoCommit))
				entityManager.getTransaction().begin();

			Long start = System.currentTimeMillis();

			result = methodToInvoke.invoke(daoImpl, params);

			Long duration = System.currentTimeMillis() - start;
			logger.debug("Invoking '{}', during {} ms.",
					this.requestedDAO.getSimpleName() + "#" + methodToInvoke.getName(), duration);

			if ((!inJTA) && (autoCommit))
				entityManager.getTransaction().commit();

			// manage entities detachment
			/*
			if ((null != result) && detachEntities) {
				if (result instanceof List<?>) {
					if (!((List<?>) result).isEmpty()) {
						Object refBean = ((List<?>) result).get(0);
						try {
							refBean.getClass().asSubclass(entityClass);
							for (Iterator<?> iterator = ((List<?>) result)
									.iterator(); iterator.hasNext();) {
								Object bean = iterator.next();
								// detach the bean
								entityManager.detach(bean);
							}
						} catch (Throwable e) {
							// Nothing to do
						}
					}
				} else {
					try {
						result.getClass().asSubclass(entityClass);
					} catch (Throwable e) {
						// Nothing to do
					}
					// detach the bean
					entityManager.detach(result);
				}
			}
			*/

		}
		catch (Throwable e) {
			if ((!inJTA) && (autoCommit))
				entityManager.getTransaction().rollback();
			if (!(e instanceof PersistenceLayerException)) {
				PersistenceUtils.handlerError(logger, e.getMessage(), e);
			}
			else {
				throw e;
			}
		}

		if (!inDAODelegate) {
			if (method.getReturnType().equals(Void.TYPE)) {
				result = null;
			} else {
				if (method.getReturnType().getName().equals("boolean")) {
					result = (result != null);
				}
			}
		}
		return result;

	}

}
