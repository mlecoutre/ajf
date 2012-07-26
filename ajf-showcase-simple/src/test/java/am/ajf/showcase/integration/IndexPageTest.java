package am.ajf.showcase.integration;

import am.ajf.core.logger.LoggerFactory;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;

/**
 * First integration test implementation
 */
public class IndexPageTest extends SeleneseTestCase {

    private final Logger logger = LoggerFactory
            .getLogger(IndexPageTest.class);

    @Before
    public void setUp() throws Exception {
        logger.debug("Start the tomcat instance for integration tests...");
        selenium = new DefaultSelenium("localhost", 4444, "*pifirefox", "http://localhost:8080/");
        selenium.start();
        // setUp("http://localhost:8080/ajf-showcase-simple/");

    }

    @Test
    /**
     * Only check is the index page exists and display the "news" part.
     */
    public void testIndexPageResult() throws Exception {
      selenium.open("/ajf-showcase-simple/index.jsf");
       //selenium.waitForPageToLoad("30000");
       // logger.debug(selenium.getBodyText());
        assertTrue(selenium.isTextPresent("News"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
