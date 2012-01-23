package am.ajf.injection;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.core.ApplicationContext;
import foo.lib.services.MyServiceBD;

@RunWith(Arquillian.class)
public class InjectionTestInt {
	
	@Inject
	private MyServiceBD myService; 
	
	@BeforeClass
	public static void setUpClass() {
		
		ApplicationContext.init();
		ApplicationContext.getConfiguration().setThrowExceptionOnMissing(false);
		
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true, "am.ajf.injection", "foo.lib", "foo.core")
				.addClasses(MonitoringInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}

	public InjectionTestInt() {
		super();
	}
	
	@Test
	public void testStandardInjection() {
		
		// Given
		String firstParam = "arg";
		
		// When
		String res = myService.myFirstOperation(firstParam);
		
		// Then
		Assert.assertNotNull("Injection failed", res);
		
	}

}
