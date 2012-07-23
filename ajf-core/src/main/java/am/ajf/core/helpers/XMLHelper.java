package am.ajf.core.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;

public abstract class XMLHelper {

	/**
	 * @param is
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getDocument(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		return getDocument(is, false);
	}

	/**
	 * @param is
	 * @param namespaceAware
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getDocument(InputStream is, boolean namespaceAware)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setExpandEntityReferences(true);
		factory.setNamespaceAware(namespaceAware); // never forget this!
		factory.setIgnoringComments(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new BlankEntityResolver());
		Document doc = builder.parse(is);
		return doc;

	}

	public static String getSubElementValue(Element parentElem, String elemName) {

		String res = null;

		NodeList subNodes = parentElem.getElementsByTagName(elemName);
		if ((subNodes != null) && (subNodes.getLength() > 0)) {
			Node subNode = ((Element) subNodes.item(0)).getFirstChild();
			if (subNode instanceof Text)
				res = ((Text) subNode).getNodeValue();
		}

		return res;
	}
	
	public static Element getSubElement(Element parentElem, String elemName) {

		NodeList subNodes = parentElem.getElementsByTagName(elemName);
		if ((subNodes != null) && (subNodes.getLength() > 0)) {
			Node subNode = ((Element) subNodes.item(0));
			return (Element) subNode;
		}

		return null;
	}
	
	public static String getElementValue(Element elem) {

		String res = null;

		Node subNode = elem.getFirstChild();
		if (subNode instanceof Text)
			res = ((Text) subNode).getNodeValue();

		return res;
	}

	public static Collection<String> getSubElementValues(Element node,
			String subElementName) {

		Collection<String> res = new HashSet<String>();

		if (node == null)
			return res;

		NodeList elemList = node.getElementsByTagName(subElementName);
		if ((null != elemList) && (elemList.getLength() > 0)) {

			for (int i = 0; i < elemList.getLength(); i++) {
				Element child = (Element) elemList.item(i);

				String value = getElementValue(child);
				if (!Strings.isNullOrEmpty(value)) {
					value = value.trim();
					if (value.length() > 0)
						res.add(value);
				}

			}

		}

		return res;
	}
	
	
}
