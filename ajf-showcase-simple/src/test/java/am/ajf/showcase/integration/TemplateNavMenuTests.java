package am.ajf.showcase.integration;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class TemplateNavMenuTests extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*pifirefox", "http://localhost:8080/");
		selenium.start();
	}

	@Test
	public void testTemplateNavMenuTests() throws Exception {
		selenium.open("/ajf-showcase-simple/index.jsf");
		selenium.click("id=nav_menu:j_idt34");
		assertTrue(selenium.isTextPresent("A propos du projet"));
		selenium.click("css=span.ui-icon.ui-icon-closethick");
		selenium.click("id=nav_menu:j_idt32");
		assertTrue(selenium.isTextPresent("Logger management"));
		selenium.click("//div[@id='myLoggerDialog']/div/a/span");
//		selenium.click("id=nav_menu:linkHelp");
//		selenium.waitForPopUp("helpWindow", "30000");
//		selenium.selectWindow("name=helpWindow");
//		assertTrue(selenium.isTextPresent("Help summary"));
//		selenium.selectWindow("null");
		selenium.click("id=nav_menu:linkContact");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Contats"));
		selenium.click("id=nav_menu:linkFAQ");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("FAQs"));
		selenium.click("link=Showcase");
		selenium.waitForPageToLoad("30000");
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
