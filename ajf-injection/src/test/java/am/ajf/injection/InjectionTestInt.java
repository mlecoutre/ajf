package am.ajf.injection;

import javax.enterprise.inject.Instance;
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
	private Instance<MyServiceBD> myServiceFactory; 
	
	@Inject
	private MyServiceBD myService1;
	
	@Inject
	private MyServiceBD myService2;
	
	@BeforeClass
	public static void setUpClass() {
		
		ApplicationContext.init();
		
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true/*, "am.ajf.injection"*/, "foo.lib", "foo.core")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}

	public InjectionTestInt() {
		super();
	}
	
	@Test
	public void testServiceInjection() {
		
		// Given
		String firstParam = "firstParam";
		String secondParam = "secondParam";
		
		// When
		MyServiceBD myService = myServiceFactory.get();
		String res = myService.myFirstOperation(firstParam, secondParam);
		
		myService = myServiceFactory.get();
		res = myService.myFirstOperation(firstParam, secondParam);
		
		res = myService1.myFirstOperation(firstParam, secondParam);
		
		res = myService2.myFirstOperation(firstParam, secondParam);
		
		// Then
		Assert.assertNotNull("Injection failed", res);
		
	}

}
