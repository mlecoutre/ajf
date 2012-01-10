package ajf.persistence.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
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
	
	private static final transient Logger logger = LoggerFactory.getLogger(EntityManagerProvider.class);	
	private static Map<String, TransactionType> persistenceUnitsTransactions;
	private static Map<String, EntityManagerFactory> emfs;
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager createEntityManager(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		
		EntityManager em = emFactory.createEntityManager();
		return em;
		
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
	public EntityManagerFactory createEntityManagerFactory(InjectionPoint ip) {
		PersistenceUnit pu = ip.getAnnotated().getAnnotation(PersistenceUnit.class);
		String puName = "default";
		if (pu != null) {
			puName = pu.name();
		}
		if (emfs == null) {
			emfs = new HashMap<String, EntityManagerFactory>();
		}
		EntityManagerFactory emFactory = emfs.get(puName);
		if (emFactory == null) {
			emFactory = Persistence.createEntityManagerFactory(puName);
			emfs.put(puName, emFactory);
			logger.info("Entity Manager Factory ("+puName+") : loaded successfully");
		}
		return emFactory;		
	}
	
	public static TransactionType getTransactionType(String persistenceUnitName) {
		if (persistenceUnitsTransactions == null) {
			loadPersistenceXml();
		}
		return persistenceUnitsTransactions.get(persistenceUnitName);
		
	}
	
	public static Set<String> getPersistenceUnitNames() {
		if (persistenceUnitsTransactions == null) {
			loadPersistenceXml();
		}
		return persistenceUnitsTransactions.keySet();	
	}

	
	/**
	 * Load the persistence.xml file informations
	 */
	private static void loadPersistenceXml() {
		persistenceUnitsTransactions = new HashMap<String, TransactionType>();
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
