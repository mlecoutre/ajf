package ajf.persistence.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ajf.persistence.jpa.annotation.PersistenceUnit;

/**
 * Factory and Producer (in CDI meaning) of JPA EntityManager and EntityManager Factory.
 * 
 * @author Nicolas Radde (E016696)
 * @author Vincent Claeysen (U002617)
 *
 */
@Named
@ApplicationScoped
public class EntityManagerProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(EntityManagerProvider.class);
	
	private static final Map<String, TransactionType> persistenceUnitsTransactions = new ConcurrentHashMap<String, EntityManagerProvider.TransactionType>();
	private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<String, EntityManagerFactory>();
	
	public EntityManagerProvider() {
		super();
	}

	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager createEntityManager(String persistenceUnitName) {

		EntityManagerFactory emFactory = createEntityManagerFactory(persistenceUnitName);
		EntityManager em = emFactory.createEntityManager();
		return em;
		
	}
	
	/**
	 * 
	 * @return the only configured persistenceUnit in the application
	 */	 
	public static EntityManager createEntityManager() {

		EntityManager em = createEntityManager(null);
		return em;
		
	}

	/**
	 * 
	 * @return the default persistenceUnit name 
	 */
	public static String getDefaultPersistenceUnitName() {
		Set<String> puNames = getPersistenceUnitNames();
		if (null == puNames) {
			throw new NullPointerException("Impossible to access the file META-INF/persistence.xml. Check it exist in your application.");
		}
		if (puNames.size() > 1) {
			throw new IllegalStateException("Check the file META-INF/persistence.xml. Check it is not empty in your application.");
		}
		
		String persistenceUnitName = puNames.iterator().next();
		return persistenceUnitName;
	}
	
	/**
	 * Produce a EntityManagerFactory based on the annotation on the injection point.
	 * Since injecting in ApplicationScope is impossible, this method keep a local cache
	 * to only instanciate each factory only once.
	 * 
	 * EntityManager produce is Thread Safe. 
	 * 
	 * @param ip the InjectionPoint where the EntityManagerFactory is injected from. 
	 * @return a Thread Safe singleton like version of the selected EntityManagerFactory 
	 */
	@Produces
	@Named
	public EntityManagerFactory produceEntityManagerFactory(InjectionPoint ip) {
		PersistenceUnit pu = ip.getMember().getDeclaringClass().getAnnotation(PersistenceUnit.class);
		
		String persistenceUnitName = null;
		if (pu != null) {
			persistenceUnitName = pu.value();
		}
		
		EntityManagerFactory emFactory = createEntityManagerFactory(persistenceUnitName);
		return emFactory;		
	}

	/**
	 * 
	 * @param puName
	 * @return a Thread Safe singleton like version of the selected EntityManagerFactory 
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
		if (null == persistenceUnitName) {
			persistenceUnitName = getDefaultPersistenceUnitName();
		}
		
		EntityManagerFactory emFactory = emfs.get(persistenceUnitName);
		if (emFactory == null) {
			synchronized (emfs) {
				emFactory = emfs.get(persistenceUnitName);
				if (null == emFactory) {
					emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
					emfs.put(persistenceUnitName, emFactory);
					logger.info("Entity Manager Factory ("+persistenceUnitName+") : loaded successfully");
				}
			}
		}
		return emFactory;
	}
	
	/**
	 * @return a Thread Safe singleton like version of the selected EntityManagerFactory 
	 */
	public static EntityManagerFactory createEntityManagerFactory() {
		return createEntityManagerFactory(null);
	}
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return the peristenceUnit TransactionType
	 */
	public static TransactionType getTransactionType(String persistenceUnitName) {
		if (persistenceUnitsTransactions.isEmpty()) {
			loadPersistenceXml();
		}
		return persistenceUnitsTransactions.get(persistenceUnitName);
		
	}
	
	/**
	 * 
	 * @return the peristenceUnit Names
	 */
	public static Set<String> getPersistenceUnitNames() {
		if (persistenceUnitsTransactions.isEmpty()) {
			loadPersistenceXml();
		}
		return persistenceUnitsTransactions.keySet();	
	}

	
	/**
	 * Load the persistence.xml file informations
	 */
	private static synchronized void loadPersistenceXml() {
		
		if (!persistenceUnitsTransactions.isEmpty())
			return;
		
		InputStream is = EntityManagerProvider.class.getClassLoader().getResourceAsStream("META-INF/persistence.xml");
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			//BufferedReader br = new BufferedReader( new InputStreamReader( is ));
			NodeList puNodes = doc.getElementsByTagName("persistence-unit");
			for (int i = 0 ; i < puNodes.getLength() ; i++) {
				Node puNode = puNodes.item(i);
				NamedNodeMap attributes = puNode.getAttributes();			
				String puName = attributes.getNamedItem("name").getNodeValue();
				
				String tranTypeAsString = attributes.getNamedItem("transaction-type").getNodeValue();
				if ("RESOURCE_LOCAL".equals(tranTypeAsString)) {
					persistenceUnitsTransactions.put(puName, TransactionType.LOCAL);
				} else if ("JTA".equals(tranTypeAsString)) {
					persistenceUnitsTransactions.put(puName, TransactionType.JTA);
				} else {
					throw new IllegalArgumentException("A persistence-unit should have transaction of type JTA or RESOURCE_LOCAL. Check your persistence.xml file.");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Impossible to access the file META-INF/persistence.xml. Check it exist in your application.", e);				
		} catch (SAXException e) {
			throw new IllegalStateException("Impossible to parse the file META-INF/persistence.xml. Check it is valid in your application.", e);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Impossible to parse the file META-INF/persistence.xml. Check it is valid in your application.", e);
		}
	}
		
	/**
	 * Transaction type possibles
	 * 
	 * @author Nicolas Radde (E016696)
	 *
	 */
	public enum TransactionType {
		JTA, 
		LOCAL
	}	

}
