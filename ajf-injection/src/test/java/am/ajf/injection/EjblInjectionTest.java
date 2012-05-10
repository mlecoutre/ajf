package am.ajf.injection;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.core.ApplicationContext;
import foo.core.services.MyEjbService;
import foo.lib.services.MyEjbServiceBD;


@RunWith(Arquillian.class)
public class EjblInjectionTest {
	
	@Inject 
	private MyEjbServiceBD ejb;
	
	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}
	
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClasses(MyEjbService.class, MyEjbServiceBD.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}
	
	public EjblInjectionTest() {
		super();
	}
			
	/**
	 * Test the ejb was injected as an EJB and not by CDI
	 */
	@Test
	public void testEjbInjection() {		
		Assert.assertNotNull(ejb);
		Assert.assertEquals("Hello me !", ejb.hello("me"));
		Assert.assertEquals("fake-ic", ejb.getCreatedBy());				
	}
	
}
