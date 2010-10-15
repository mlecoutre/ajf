package ajf.persistence;

import static ajf.utils.BeanUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
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

import ajf.persistence.annotations.AutoCommit;
import ajf.utils.helpers.XMLHelper;

public class JpaDAOProxy implements InvocationHandler {

	public final static String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	private final static Class<?> BASE_PERSISTENCE_DAO_CLASS = BaseJpaDAOImpl.class;
	private final static String BASE_PERSISTENCE_DAO = BASE_PERSISTENCE_DAO_CLASS.getName();
	
	private final static Logger logger = LoggerFactory.getLogger(JpaDAOProxy.class);
	
	private static Object token = new Object();

	// the persistence unit Map
	private static Map<String, PersistenceUnitDesc> puMap = null;
	// the generated methods
	private static Set<String> generatedMethods = null;
	// the DAO methods Map
	private static Map<String, Map<String, Method>> daosMap = null;
	// The DAO delegates Map
	private static Map<Class<?>, Class<?>> daoDelegatesMap = null;

	// the proxy instance datas 
	private String persistenceUnitName;
	private boolean inJTA = true;
		
	private Class<?> requestedDAO = null;
	private JpaDAO basePersistenceDAOImpl = null;
	private String persistenceDAODelegate = null;
	private Class<?> daoDelegateClass = null;
	private JpaDAO persistenceDAODelegateImpl = null;
	
	private EntityManager entityManager = null;

	/**
	 * 
	 * @param daoClass
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public JpaDAOProxy(Class<?> daoClass)
			throws InstantiationException, IllegalAccessException, ParserConfigurationException, SAXException, IOException {

		// check init
		init();
		
		// resolve the DAO Delegate Class
		resolveDaoDelegate(daoClass);

		// load the persistence unit infos
		resolvePersitenceUnitInfos(daoClass);
		
		// get the corresponding EntityManager
		entityManager = EntityManagerProvider.getEntityManager(persistenceUnitName);

		// get a new persistence base dao impl instance
		basePersistenceDAOImpl = (JpaDAO) instanciate(BASE_PERSISTENCE_DAO_CLASS);
		basePersistenceDAOImpl.setEntityManager(entityManager);

		// get a new persistence dao delegate impl instance
		persistenceDAODelegateImpl = null;
		if (null != daoDelegateClass) {
			persistenceDAODelegateImpl = (JpaDAO) instanciate(daoDelegateClass);
			persistenceDAODelegateImpl.setEntityManager(entityManager);
		}
		
	}

	private void resolveDaoDelegate(Class<?> daoClass) {
		// set the requested DAO
		this.requestedDAO = daoClass;

		// resolve the DAO Delegate
		if (daoDelegatesMap.containsKey(daoClass)) {
			this.daoDelegateClass = daoDelegatesMap.get(daoClass);
		}
		else {
			// check if a DAO delegate exist
			String daoDelegateClassName = buildDaoDelegateClassName(daoClass);
			try {
				// find the DAO delegate class
				this.daoDelegateClass = Thread.currentThread()
						.getContextClassLoader()
						.loadClass(daoDelegateClassName);
				this.persistenceDAODelegate = daoDelegateClassName;
			}
			catch (Exception e) {
				this.daoDelegateClass = null;
			}
			daoDelegatesMap.put(daoClass, this.daoDelegateClass);
		}

		// check if the requested DAO is already registered
		registerDAO(daoClass);
		if (null != daoDelegateClass) 
			registerDAO(daoDelegateClass);
	}

	/**
	 * 
	 * @param requestedDAO
	 * @return
	 */
	private void resolvePersitenceUnitInfos(Class<?> requestedDAO) {
		// retrieve the persistence unit
		PersistenceUnitDesc puDesc = puMap.get(requestedDAO.getName());
		if (null == puDesc) {
			throw new NullPointerException("Unable to find persistence unit informations for DAO " + requestedDAO.getName() + ".");
		}
		this.persistenceUnitName = puDesc.getName();
		this.inJTA = "JTA".equalsIgnoreCase(puDesc.getTransactionType());		
	}
	
	/**
	 * 
	 * @param daoClass
	 * @return the DAO Delegate Class name
	 */
	private String buildDaoDelegateClassName(Class<?> daoClass) {
		String daoDelegateClassName = daoClass.getName().concat("Delegate");
		return daoDelegateClassName;
	}

	/**
	 * 
	 * @param requestedDAO
	 */
	private void registerDAO(Class<?> requestedDAO) {
		synchronized (token) {
			Map<String, Method> daoMethodsMap = daosMap.get(requestedDAO
					.getName());
			// in this case, register the DAO
			if (null == daoMethodsMap) {
				// list the DAO methods
				daoMethodsMap = listMethodsAsMap(requestedDAO);
				// register the DAO methods
				daosMap.put(requestedDAO.getName(), daoMethodsMap);
			}
		}
	}

	/**
	 * 
	 * @param requestedDAO
	 * @param requestedMethod
	 * @param args
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Object[] processParameters(Class<?> requestedDAO, Class<?> entityClass, 
			String requestedMethod, Object[] args)
			throws ClassNotFoundException {

		Object[] params = args;
		if (requestedMethod.endsWith("ByPrimaryKey")) {
			params = new Object[] {entityClass, args[0]};
		}
		else {
			
			if ("findAll".equals(requestedMethod)) {
				params = new Object[] {entityClass};
			}
			else {
				// then, try for named query
				params = new Object[] {entityClass, requestedMethod, args};
			}
			
		}
		return params;
	}

	private Class<?> resolveEntityClass(Class<?> requestedDAO)
			throws ClassNotFoundException {
		String entityClassName = processEntityClassName(requestedDAO);
		// retrieve the entity class name
		Class<?> entityClass = Thread.currentThread()
				.getContextClassLoader().loadClass(entityClassName);
		return entityClass;
	}

	private String processEntityClassName(Class<?> requestedDAO) {
		// add the package
		String entityClassName = requestedDAO.getPackage().getName();
		// replace dao by model in the package name
		entityClassName = entityClassName.substring(0,
				entityClassName.length() - 3).concat("model.");
		// add the DAO class name
		entityClassName = entityClassName.concat(requestedDAO
				.getSimpleName());
		// remove DAO
		entityClassName = entityClassName.substring(0,
				entityClassName.length() - 3);
		return entityClassName;
	}

	/**
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void loadPersistence() throws ParserConfigurationException, SAXException, IOException {
		
		// find the persistence.xml file
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("META-INF/persistence.xml");

		// parse the file
		Document doc = XMLHelper.getDocument(is);
		Element root = (Element) doc.getFirstChild();
		
		// the current classLoader
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		// instanciate the puMap
		puMap = new HashMap<String, PersistenceUnitDesc>();
		
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
							Class<?> entityClass = classLoader.loadClass(className);
							
							// process candidate DAO className
							String daoCandidate = entityClass.getPackage().getName();
							daoCandidate = daoCandidate.substring(0, daoCandidate.length()-5);
							daoCandidate = daoCandidate.concat("dao.");
							daoCandidate = daoCandidate.concat(entityClass.getSimpleName()).concat("DAO");
							
							// check the DAO class
							classLoader.loadClass(daoCandidate);
														
							puDesc.addClass(className);
							
							// register the puDesc for the DAO candidate
							puMap.put(daoCandidate, puDesc);
							
						}
						catch (ClassNotFoundException e) {
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
	private void init() throws ParserConfigurationException, SAXException, IOException {

		synchronized (token) {

			if (null == daosMap) {
				
				// load the JPA persistence.xml
				loadPersistence();

				// register the BasePersistenceDAOImpl
				Map<String, Method> baseDAOMethodsMap = listMethodsAsMap(BaseJpaDAOImpl.class);
				// register the DAO methods
				generatedMethods = baseDAOMethodsMap.keySet();
				
				daosMap = new HashMap<String, Map<String, Method>>();
				daosMap.put(BASE_PERSISTENCE_DAO, baseDAOMethodsMap);

				// initialized the daoDelegatesMap
				daoDelegatesMap = new HashMap<Class<?>, Class<?>>();

			}

		}

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// the requested method name
		String requestedMethod = method.getName();

		// the invocation parameters
		Object daoImpl = null;
		Object[] params = args;
		Method methodToInvoke = null;
				
		// the invocation result
		Object result = null;
		
		// for the settings setFirstResult(...) setMaxResults(...)
		if (requestedMethod.startsWith("set")) {
			
			if (null != persistenceDAODelegate) { 
				daoImpl = persistenceDAODelegateImpl;
				methodToInvoke = daosMap.get(persistenceDAODelegate).get(
						requestedMethod);
				if (null != methodToInvoke) {
					result = methodToInvoke.invoke(daoImpl, args);
				}
			}
			
			// the DAO impl to invoke
			daoImpl = basePersistenceDAOImpl;
			methodToInvoke = daosMap.get(BASE_PERSISTENCE_DAO).get(
						requestedMethod);
			if (null != methodToInvoke) {
				result = methodToInvoke.invoke(daoImpl, args);
			}
			
			return null;
			
		}
		
		
		// is the method in the delegate
		if (null != persistenceDAODelegate) {
			// get the requested method
			methodToInvoke = daosMap.get(persistenceDAODelegate).get(
					requestedMethod);
			if (null != methodToInvoke)
				daoImpl = persistenceDAODelegateImpl;
		}
		
		// process the generated methods in the baseJpaDAOImpl 
		if (null == methodToInvoke) {

			// the DAO impl to invoke
			daoImpl = basePersistenceDAOImpl;
			// resolve the corresponding entity class
			Class<?> entityClass = resolveEntityClass(requestedDAO);
			
			// is it a standard method
			if (generatedMethods.contains(requestedMethod)) {
				// get the requested method
				methodToInvoke = daosMap.get(BASE_PERSISTENCE_DAO).get(
						requestedMethod);
				// process the parameters
				params = processParameters(requestedDAO, entityClass, requestedMethod, args);
			}
			else {
				// process the query name
				String queryName = entityClass.getSimpleName().concat(".").concat(requestedMethod);
				// process the parameters
				params = processParameters(requestedDAO, entityClass, queryName, args);
				
				// resolve the method to invoke
				if (requestedMethod.startsWith("findSingleResult")) {
					// get the requested method
					methodToInvoke = daosMap.get(BASE_PERSISTENCE_DAO).get(
						"findSingleResultQuery");
				}
				else {
					if (requestedMethod.startsWith("find")) {
						// get the requested method
						methodToInvoke = daosMap.get(BASE_PERSISTENCE_DAO).get(
							"findQuery");
					}
					else {
						// get the requested method
						methodToInvoke = daosMap.get(BASE_PERSISTENCE_DAO).get(
							"executeQuery");
					}
				}
				
			}
		}
		
		if (null == methodToInvoke) {
			throw new NullPointerException("The requested method '" + requestedMethod + "' is not implemented.");
		}
				
		// is auto-commit
		boolean autoCommit = true;
		if (method.isAnnotationPresent(AutoCommit.class)) {
			autoCommit = method.getAnnotation(AutoCommit.class).value();
		}
		// is auto-commit overrided 
		if (methodToInvoke.isAnnotationPresent(AutoCommit.class)) {
			autoCommit = methodToInvoke.getAnnotation(AutoCommit.class).value();
		}

		try {
			if ((!inJTA) && (autoCommit)) 
				entityManager.getTransaction().begin();
			
			Long start = System.currentTimeMillis();
			 
			result = methodToInvoke.invoke(daoImpl, params);
			
			Long duration = System.currentTimeMillis() - start;
			logger.info("Invoking '{}', during {} ms.", methodToInvoke.getName(), duration);
			
			if ((!inJTA) && (autoCommit)) 
				entityManager.getTransaction().commit();
		}
		catch (Throwable e) {
			if ((!inJTA) && (autoCommit)) 
				entityManager.getTransaction().rollback();
			throw e;
		}
		return result;

	}

}
