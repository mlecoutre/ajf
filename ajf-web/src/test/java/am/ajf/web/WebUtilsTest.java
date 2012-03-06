package am.ajf.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Locale;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

/**
 * WebUtilsTest
 * 
 * @author E010925
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacesContext.class)
public class WebUtilsTest {

	private static final String AJF_WEB_MESSAGES = "ajf.web.messages";
	private static final String I18N_MY_KEY = "my.key";

	private static final String I18N_MY_KEY2 = "my.key2";

	// public static Logger log = LoggerFactory.getLogger(WebUtilsTest.class);

	@Mock
	private Exception exception;

	@Mock
	private Logger logger;

	@Mock
	private Application mockApplication;

	// @Mock
	// private ELContext mockELContext;

	@Mock
	private ExternalContext mockExternalContext;

	@Mock
	private FacesContext mockFacesContext;

	@Mock
	private UIViewRoot mockUIViewRoot;

	// @Mock
	// private ExpressionFactory expressionFactory;
	//
	// @Mock
	// private ValueExpression mockValueExpression;
	//
	// private String elExpression = "#{testExpression}";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockStatic(FacesContext.class);
		// mockStatic(WebUtils.class);

		when(FacesContext.getCurrentInstance()).thenReturn(mockFacesContext);

		when(mockFacesContext.getViewRoot()).thenReturn(mockUIViewRoot);
		when(mockUIViewRoot.getLocale()).thenReturn(Locale.ENGLISH);
		// when(mockFacesContext.getELContext()).thenReturn(mockELContext);
		when(mockFacesContext.getApplication()).thenReturn(mockApplication);
		when(mockApplication.getMessageBundle()).thenReturn(AJF_WEB_MESSAGES);

		// WebUtils.setContext(mockFacesContext);

		// when(mockApplication.getExpressionFactory()).thenReturn(
		// expressionFactory);
		// when(
		// expressionFactory.createValueExpression(mockELContext,
		// elExpression, Object.class)).thenReturn(
		// mockValueExpression);
		// when(mockValueExpression.getValue(mockELContext)).thenReturn(2l);
	}

	@Test
	public void testGenerateLabelValue() {
		System.out.println("> testGenerateLabelValue");
		String result = WebUtils.generateLabelValue("first.name");
		System.out.println(" * " + result);
		assertTrue("First name".equals(result));
		result = WebUtils.generateLabelValue("firstName");
		System.out.println(" * " + result);
		assertTrue("First name".equals(result));
	}

	@Test
	public void testGetFieldLabel() {
		System.out.println("> testGetFieldLabel");

		// test 1
		String translation = WebUtils.getFieldLabel(I18N_MY_KEY);
		System.out.println("  1) " + translation);
		assertNotNull("translation ", translation);

		// test 2
		translation = WebUtils.getFieldLabel(I18N_MY_KEY2);
		assertTrue("second call to getLabel() method ",
				"Mini2 EN".equals(translation));
		System.out.println("  2) " + translation);

		// MockContext.registerMock(ConfigurationItemTypeDAO.class,
		// mockedCitDAO);
	}

	@Test
	public void testGivePropertyValue() {

		String notExistingKey = "not.existing.key";

		// test normal behavior
		System.out.println("> testGivePropertyValue");
		String value = WebUtils.givePropertyValue("mykey",
				"configuration.properties");
		assertTrue("hello, world".equals(value));

		// test not existing configuration file
		value = WebUtils.givePropertyValue(notExistingKey,
				"notexistingfile.properties");
		assertTrue(notExistingKey.equals(value));

		// test not existing key
		value = WebUtils.givePropertyValue(notExistingKey,
				"configuration.properties");
		assertTrue(notExistingKey.equals(value));

	}

	@Test
	public void testHandleError() {
		System.out.println("> testHandleError");
		Mockito.when(exception.getMessage()).thenReturn("exception message");

		WebUtils.handleError(exception, logger, "error message");

		// verify if we are passed in the error method once
		verify(logger, times(1)).error(any(String.class), any(Exception.class));

	}
}
