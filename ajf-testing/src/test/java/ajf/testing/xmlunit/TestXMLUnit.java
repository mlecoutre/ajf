package ajf.testing.xmlunit;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Before;
import org.junit.Test;

public class TestXMLUnit {
	
	private XMLTestCase xmlTestCase = null;
	
	@Before
	public void setUp() {
		XMLUnitHelper.initialize();
		xmlTestCase = XMLUnitHelper.newTestCase();
	}

	@Test
	public void testEquals() throws Exception {
		String myControlXML = "<msg><uuid>2376</uuid></msg>";
		String myTestXML = "<msg><uuid>2376</uuid></msg>";
		xmlTestCase.assertXMLEqual(myControlXML, myTestXML);
	}
	
	@Test
	public void testNotEquals() throws Exception {
		String myControlXML = "<msg><uuid>2376</uuid></msg>";
		String myTestXML = "<msg><userid>2376</userid></msg>";
		xmlTestCase.assertXMLNotEqual(myControlXML, myTestXML);
	}
	
}
