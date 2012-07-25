package am.ajf.showcase.integration;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TemplatePageUnavailableTest extends SeleneseTestCase {
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*pifirefox", "http://localhost:8080/");
        selenium.start();
    }

    @Test
    public void testTemplatePageUnavailable() throws Exception {
        //check a non existing page. the framework should redirect to a page where the message
        // "Resource not found is displayed"
        selenium.open("/ajf-showcase-simple/unavailablePage.jsp");
        assertTrue(selenium.isTextPresent("Resource not Found"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
