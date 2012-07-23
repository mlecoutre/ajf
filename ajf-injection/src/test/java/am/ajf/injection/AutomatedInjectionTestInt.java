package am.ajf.injection;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.cache.Cache;
import am.ajf.core.datas.AuditData;
import am.ajf.injection.annotation.Property;
import foo.lib.services.MyServiceBD;

@RunWith(Arquillian.class)
public class AutomatedInjectionTestInt {
	
	@Inject
	private Logger logger;
	
	@Inject
	private AuditData auditData;
	
	@Inject 
	private Cache defaultCache;
	
	@Inject @am.ajf.injection.annotation.Cache(cacheManagerName="simple", cacheName="default")
	private Cache cache;

	@Inject
	private Configuration appConfiguration;
	
	@Inject @Property(value="application.name", defaultValue="")
	private String property;
	
	@Inject
	private Instance<MyServiceBD> myServiceFactory;
		
	@Inject
	private MyServiceBD myService1;
	
	@Inject
	private MyServiceBD myService2;
	
	@Inject
	private BeanManager beanManager;
	
	@BeforeClass
	public static void setUpClass() {
		
		ApplicationContext.init();
		
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo.lib", "foo.core")
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

	public AutomatedInjectionTestInt() {
		super();
	}
	
	@Test
	public void testBeanManagerInjection() {
		
		assertNotNull(beanManager);
		logger.info("BeanManager injected.");
		
	}
	
	@Test
	public void testLoggerInjection() {
		
		assertNotNull(logger);
		logger.info("Logger injected.");
		
	}
	
	@Test
	public void testAuditDataInjection() {
		
		assertNotNull(auditData);
		logger.info("AuditData injected.");
				
	}
	
	@Test
	public void testDefaultCacheInjection() {
		
		assertNotNull(defaultCache);
		logger.info("Default Cache injected.");
				
	}
	
	@Test
	public void testAnotatedCacheInjection() {
		
		assertNotNull(cache);
		logger.info("Annotated Cache injected.");
				
	}
	
	@Test
	public void testConfigurationInjection() {
		
		assertNotNull(appConfiguration);
		logger.info("Configuration injected.");
				
	}
	
	@Test
	public void testPropertyInjection() {
		
		assertNotNull(property);
		logger.info("Property injected.");
				
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
