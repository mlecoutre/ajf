package am.ajf.persistence.jpa;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import am.ajf.core.logger.LoggerFactory;

import com.google.common.base.Strings;

public class PersistenceConnectionFactoryBuilder {
	
	private static final String JAVAX_PERSISTENCE_JDBC_PASSWORD = "javax.persistence.jdbc.password";
	private static final String JAVAX_PERSISTENCE_JDBC_USER = "javax.persistence.jdbc.user";
	private static final String JAVAX_PERSISTENCE_JDBC_URL = "javax.persistence.jdbc.url";
	private static final String JAVAX_PERSISTENCE_JDBC_DRIVER = "javax.persistence.jdbc.driver";

	private static final String[] JDBC_PROPERTIES = new String[] {
			JAVAX_PERSISTENCE_JDBC_DRIVER, JAVAX_PERSISTENCE_JDBC_URL,
			JAVAX_PERSISTENCE_JDBC_USER, JAVAX_PERSISTENCE_JDBC_PASSWORD };
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceConnectionFactoryBuilder.class);

	private PersistenceConnectionFactoryBuilder() {
		super();
	}

	/**
	 * 
	 * @param puNode
	 *            <jta-data-source>jdbc/hsql-1</jta-data-source>
	 *            or
	 *            <non-jta-data-source></non-jta-data-source>
	 *            or 
	 *            <properties>
	 *            	<property name="javax.persistence.jdbc.driver"
	 *            		value="org.apache.derby.jdbc.EmbeddedDriver" />
	 *            	<property name="javax.persistence.jdbc.url"
	 *            		value="jdbc:derby:target/ajf-core;create=true" />
	 *            	<property name="javax.persistence.jdbc.user" 
	 *            		value="dcdbuser" />
	 *            	<property name="javax.persistence.jdbc.password"
	 *            		value="Passworddcdb00" />
	 * 
	 * @return a PersistenceConnectionFactory
	 */
	public static PersistenceConnectionFactory build(Node puNode) {

		PersistenceConnectionFactory persistenceConnectionFactory = null;

		Element puElem = (Element) puNode;

		// the puName
		String puName = puElem.getAttribute("name");

		// is it a JTA DataSource ?
		NodeList jtaDSList = puElem.getElementsByTagName("jta-data-source");
		if ((null != jtaDSList) && (jtaDSList.getLength() > 0)) {
			String dataSourceName = jtaDSList.item(0).getFirstChild()
					.getNodeValue();
			if (!Strings.isNullOrEmpty(dataSourceName)) {
				logger.info(String.format("PersistenceUnit '%s' served by JTA DataSource '%s'.", puName, dataSourceName));
				persistenceConnectionFactory = new DataSourceConnectionFactory(
					dataSourceName);
				return persistenceConnectionFactory;
			}
		}

		// is it a non-JTA DataSource ?
		NodeList nonJTADSList = puElem
				.getElementsByTagName("non-jta-data-source");
		if ((null != nonJTADSList) && (nonJTADSList.getLength() > 0)) {
			String dataSourceName = nonJTADSList.item(0).getFirstChild()
					.getNodeValue();
			if (!Strings.isNullOrEmpty(dataSourceName)) {
				logger.info(String.format("PersistenceUnit '%s' served by Non-JTA DataSource '%s'.", puName, dataSourceName));
				persistenceConnectionFactory = new DataSourceConnectionFactory(
					dataSourceName);
				return persistenceConnectionFactory;
			}
		}

		// or a direct connection
		NodeList subNodes = puElem.getElementsByTagName("properties");
		if ((null != subNodes) && (subNodes.getLength() > 0)) {

			Collection<String> keys = Arrays.asList(JDBC_PROPERTIES);
			Map<String, String> propertiesMap = new HashMap<String, String>();

			Element props = (Element) subNodes.item(0);
			NodeList nodes = props.getElementsByTagName("property");
			if ((null != nodes) && (nodes.getLength() > 0)) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Element node = (Element) nodes.item(i);
					String pName = getAttributeValue(node, "name");
					if (keys.contains(pName)) {
						String pValue = getAttributeValue(node, "value");
						if (!Strings.isNullOrEmpty(pValue))
							propertiesMap.put(pName, pValue);
					}
				}
			}

			String jdbcDriver = propertiesMap
					.get(JAVAX_PERSISTENCE_JDBC_DRIVER);
			if (null == jdbcDriver) {
				logger.warn(String.format("the property '%s' for PersistenceUnit '%s' can not be found.", JAVAX_PERSISTENCE_JDBC_DRIVER, puName));
				return null;
			}
			String jdbcUrl = propertiesMap.get(JAVAX_PERSISTENCE_JDBC_URL);
			if (null == jdbcUrl) {
				logger.warn(String.format("the property '%s' for PersistenceUnit '%s' can not be found.", JAVAX_PERSISTENCE_JDBC_URL, puName));
				return null;
			}
			String jdbcUser = propertiesMap.get(JAVAX_PERSISTENCE_JDBC_USER);
			String jdbcPassword = propertiesMap.get(JAVAX_PERSISTENCE_JDBC_PASSWORD);
			
			logger.trace(String.format("PersistenceUnit '%s' served by Direct JDBC, with URL: '%s'.", puName, jdbcUrl));
			persistenceConnectionFactory = new DirectConnectionFactory(
					jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
			return persistenceConnectionFactory;

		}

		return persistenceConnectionFactory;

	}

	private static String getAttributeValue(Element elem, String attrName) {

		if (null == elem)
			return null;

		String res = elem.getAttribute(attrName);

		if (null == res)
			return null;

		res = res.trim();

		return res;
	}

}
