package am.ajf.web.controllers;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import am.ajf.core.logger.LoggerFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

/**
 * HelpMBeanTest
 * 
 * @author E010925
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacesContext.class)
public class HelpMBeanTest {

	private final HelpMBean helpMBean = new HelpMBean();

	private final Logger logger = LoggerFactory.getLogger(HelpMBeanTest.class);

	@Mock
	private ExternalContext mockExternalContext;

	@Mock
	private FacesContext mockFacesContext;

	// @Mock
	// private UIViewRoot mockUIViewRoot;

	/**
	 * initialize test
	 */
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockStatic(FacesContext.class);
		helpMBean.logger = this.logger;

		when(FacesContext.getCurrentInstance()).thenReturn(mockFacesContext);
		// when(mockFacesContext.getViewRoot()).thenReturn(mockUIViewRoot);

		when(mockFacesContext.getExternalContext()).thenReturn(
				mockExternalContext);
		// when(mockFacesContext.getExternalContext().getRequestContextPath())
		// .thenReturn("/ajf-showcase-simple");
		//
		String sthgThatExist = this.getClass().getClassLoader()
				.getResource(".").getFile().toString();
		when(
				mockExternalContext
						.getRealPath("/shared/help/showcase/layout.xhtml"))
				.thenReturn(sthgThatExist);
		when(
				mockExternalContext
						.getRealPath("/shared/help/employeeManagement/hireEmployee.xhtml"))
				.thenReturn("fileDoesNotExist");

		when(
				mockExternalContext
						.getRealPath("/shared/help/employeeManagement/index.xhtml"))
				.thenReturn(sthgThatExist);

		when(mockExternalContext.getRealPath("/shared/help/index.xhtml"))
				.thenReturn(sthgThatExist);
	}

	/**
	 * testDisplayContextualHelp
	 */
	@Test
	public void testDisplayContextualHelp() {
		logger.debug("testDisplayContextualHelp: ");

		helpMBean.setContext("/ajf-showcase-simple");
		helpMBean.setViewId("/employeeManagement/hireEmployee.xhtml");
		String res = helpMBean.displayContextualHelp();
		Assert.assertTrue(
				"employeeManagement has only one index page for all pages",
				"/ajf-showcase-simple/shared/help/employeeManagement/index.xhtml"
						.equals(res));

		helpMBean.setViewId("/showcase/layout.xhtml");
		res = helpMBean.displayContextualHelp();
		Assert.assertTrue("showcase define one help page per page",
				"/ajf-showcase-simple/shared/help/showcase/layout.xhtml"
						.equals(res));

		helpMBean.setViewId("/unknownFunction/layout.xhtml");
		res = helpMBean.displayContextualHelp();
		Assert.assertTrue("showcase define one help page per page",
				"/ajf-showcase-simple/shared/help/index.xhtml".equals(res));
	}

	/**
	 * testExtractPalasFunction
	 */
	@Test
	public void testExtractPalasFunction() {

		String sampleViewId = "/employeeManagement/toto.xhtml";
		String res = helpMBean.extractPalasFunction(sampleViewId);
		Assert.assertTrue("/employeeManagement".equals(res));

		sampleViewId = "/employeeManagement/subcategory/toto.xhtml";
		res = helpMBean.extractPalasFunction(sampleViewId);
		Assert.assertTrue("Sub categories of p+ function should be allowed",
				"/employeeManagement/subcategory".equals(res));

	}
}
