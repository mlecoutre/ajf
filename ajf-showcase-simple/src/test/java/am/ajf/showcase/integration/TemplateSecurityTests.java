package am.ajf.showcase.integration;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TemplateSecurityTests extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*pifirefox", "http://localhost:8080/");
		selenium.start();
	}

    @Test
	public void testTemplateSecurityTests() throws Exception {
		selenium.open("/ajf-showcase-simple/index.jsf");
		selenium.type("id=InLoginForm:username", "unknown_user");
		selenium.type("id=InLoginForm:password", "badPassword");
		selenium.click("id=InLoginForm:inSubmitButton");
		assertTrue(selenium.isTextPresent("Authentication failure."));
		selenium.open("/ajf-showcase-simple/employeemanagement/listEmployees.xhtml");
		assertTrue(selenium.isTextPresent("Login page"));
		selenium.type("id=j_username", "unknown_user");
		selenium.type("id=j_password", "badPassword");
		selenium.click("id=inSubmitButton");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("La connexion a échoué !"));
        //TODO WILL FAILS IF DIFFERENT FROM FRENCH LANGUAGE
		selenium.click("link=Réessayez.");
		selenium.waitForPageToLoad("30000");
		/*
		selenium.type("id=InLoginForm:username", "e010925");

		selenium.type("id=InLoginForm:password", "webSphere35!");
		selenium.click("id=InLoginForm:inSubmitButton");
		assertTrue(selenium.isTextPresent("Personel"));
	*/
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
