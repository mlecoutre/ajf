package am.ajf.core.helpers;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
		
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setExpandEntityReferences(true);
		factory.setNamespaceAware(namespaceAware); // never forget this!
		factory.setIgnoringComments(false);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new BlankEntityResolver());
		Document doc = builder.parse(is);
		return doc;
		
	}

}