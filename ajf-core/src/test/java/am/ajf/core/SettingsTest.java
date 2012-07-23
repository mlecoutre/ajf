/**
 * 
 */
package am.ajf.core;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import am.ajf.core.helpers.XMLHelper;

/**
 * @author U002617
 *
 */
public class SettingsTest {

	/**
	 * 
	 */
	@Test
	public void testLoadSettings4TheFuture() 
			throws Exception {
		
		InputStream inputStream = this.getClass()
				.getClassLoader().getResourceAsStream("settings.xml");
		Document doc = XMLHelper.getDocument(inputStream);
		inputStream.close();
		
		Element docRoot = (Element) doc.getFirstChild();
		
		String appName = XMLHelper.getSubElementValue(docRoot,
				"application-name");
		System.out.println(appName);
		
		Element appPropertiesElem = XMLHelper.getSubElement(docRoot, "properties");
		Map<String, String> appPropertiesMap = getProperties(appPropertiesElem);
		
		Element configsElem = XMLHelper.getSubElement(docRoot, "configurations");
		if (null != configsElem) {
			NodeList configNodes = configsElem.getElementsByTagName("configuration");
			for (int i = 0; i < configNodes.getLength(); i++) {
				Element configElem = (Element) configNodes.item(i);
				String configId = configElem.getAttribute("id");
				System.out.println(">>Config #" + configId);
				Map<String, String> configPropertiesMap = getProperties(configElem);
			}
		}

		Element beansElem = XMLHelper.getSubElement(docRoot, "beans");
		if (null != beansElem) {
			NodeList beanNodes = beansElem.getElementsByTagName("bean");
			for (int i = 0; i < beanNodes.getLength(); i++) {
				Element beanElem = (Element) beanNodes.item(i);
				String interfaceClassName = beanElem.getAttribute("interface");
				String beanRoleName = beanElem.getAttribute("role");
				String configurations = beanElem.getAttribute("configurations");
				System.out.println(interfaceClassName + " as #" + beanRoleName);
				System.out.println(configurations);
				String beanClassName = XMLHelper.getSubElementValue(beanElem, "bean-class");
				System.out.println(beanClassName);
				Element propertiesElem = XMLHelper.getSubElement(beanElem, "properties");
				Map<String, String> propertiesMap = getProperties(propertiesElem);
				Element settingElem = XMLHelper.getSubElement(beanElem, "settings");
			}
		}
		
	}

	public Map<String, String> getProperties(Element propertiesElem) {
		Map<String,String> propertiesMap = new LinkedHashMap<String, String>();
		NodeList subNodes = propertiesElem.getChildNodes();
		for (int i = 0; i < subNodes.getLength(); i++) {
			Node subNode = subNodes.item(i);
			if (subNode instanceof Element) {
				Element subElem = (Element) subNode;
				String key = subElem.getNodeName();
				String value = XMLHelper.getElementValue(subElem);
				System.out.println(key + " = " + value);
				propertiesMap.put(key, value);
			}
		}
		return propertiesMap;
	}

}
