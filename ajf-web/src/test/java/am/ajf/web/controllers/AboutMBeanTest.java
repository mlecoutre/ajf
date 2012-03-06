package am.ajf.web.controllers;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * AboutMBeanTest
 * 
 * @author E010925
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacesContext.class)
public class AboutMBeanTest {

	@Mock
	private ExternalContext mockExternalContext;

	@Mock
	private FacesContext mockFacesContext;

	@Mock
	private ServletContext mockServletContext;

	private final AboutMBean aboutMBean = new AboutMBean();

	/**
	 * initialize test
	 */
	@Before
	public void init() {

		String root = this.getClass().getClassLoader().getResource(".")
				.getFile().toString();

		MockitoAnnotations.initMocks(this);
		mockStatic(FacesContext.class);

		when(FacesContext.getCurrentInstance()).thenReturn(mockFacesContext);
		// when(mockFacesContext.getViewRoot()).thenReturn(mockUIViewRoot);

		when(mockFacesContext.getExternalContext()).thenReturn(
				mockExternalContext);

		when(mockExternalContext.getContext()).thenReturn(mockServletContext);

		when(mockServletContext.getRealPath("/")).thenReturn(root);
	}

	/**
	 * test the recuperation of values
	 */
	@Test
	public void testInit() {
		aboutMBean.init();
		String title = aboutMBean.getImplementationTitle();
		String vendor = aboutMBean.getImplementationVendor();
		String version = aboutMBean.getImplementationVersion();

		Assert.assertTrue("Attribute from manifest should be extracted",
				((title != null) & (vendor != null)) && (version != null));
	}
}
