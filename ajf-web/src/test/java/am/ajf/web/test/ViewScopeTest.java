package am.ajf.web.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.web.context.ViewContext;
import am.ajf.web.context.ViewContextExtension;
import am.ajf.web.test.harness.ViewScopeMBean;

@Ignore
@RunWith(Arquillian.class)
public class ViewScopeTest {

	@Inject
	private ViewScopeMBean mbean;


	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(ViewScopeMBean.class)				
				.addClasses(ViewContext.class)
				.addClasses(ViewContextExtension.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void tearDown() throws Exception {		
		
	}
	
	@Test
	public void testViewScopeCreation() {
		//TODO
	}


}
