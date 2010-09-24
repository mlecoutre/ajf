package ajf.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BlankEntityResolver implements EntityResolver {

	private static byte[] blank = "".getBytes();

	public BlankEntityResolver() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId)
		throws SAXException, IOException {
			ByteArrayInputStream bais =
				new ByteArrayInputStream(blank);
			return new InputSource(bais);
	}

}
