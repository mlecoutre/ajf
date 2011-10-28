package ajf.testing.xmlunit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUnitHelper {
	
	private XMLUnitHelper() {
		super();
	}

	public static XMLTestCase newTestCase() {
		
		XMLTestCase xmlTestCase = new XMLTestCase() {};
		return xmlTestCase;
		
	}

	public static void initialize() {
		
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setTestEntityResolver(new EntityResolver() {
			
			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				String empty = "";
				ByteArrayInputStream bais =
					new ByteArrayInputStream(empty.getBytes(Charset.forName("UTF-8")));
				return new InputSource(bais);
			}
		});
		
	}

}
