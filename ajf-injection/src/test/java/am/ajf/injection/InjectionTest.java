package am.ajf.injection;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.cache.CacheManagerFactory;
import am.ajf.core.utils.BeanUtils;
import am.ajf.injection.events.BasicEventImpl;
import foo.core.services.MyService;
import foo.lib.services.MyServiceBD;

@RunWith(Arquillian.class)
public class InjectionTest {

	@Inject
	private Logger logger;
	
	@Inject 
	private javax.enterprise.event.Event<BasicEventImpl> event;
	
	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}
	
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(ConfigurationProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}
	
	public InjectionTest() {
		super();
	}
			
	@Test
	public void testLoggerInjection() {
		
		assertNotNull(logger);
		logger.info("Logger injected.");
		
	}
	
	@Test
	public void testFireEvent() {
	
		assertNotNull(event);	
		
		event.fire(new BasicEventImpl("myEvent"));
		
		logger.info("AuditData injected.");
		
	}
	
	@Test
	public void testServiceInjection() {
		
		
		CacheManagerFactory.getFirstCacheManager();
		
		MyServiceBD svc = BeanUtils.newInstance(MyServiceBD.class);
		
		assertNotNull(svc);
		
		String res = svc.myFirstOperation("vincent", "toto");
		
		assertNotNull(res);
		
		MyServiceBD svc2 = BeanUtils.newInstance(MyServiceBD.class);
		res = svc2.myFirstOperation("vincent", "toto");
		
	}
	
	@Test
	public void testServiceImplInjection() {
		
		MyServiceBD svc = BeanUtils.newInstance(MyService.class);
		
		assertNotNull(svc);
		
		String res = svc.myFirstOperation("vincent", "toto");
		
		assertNotNull(res);
				
	}
	
}
