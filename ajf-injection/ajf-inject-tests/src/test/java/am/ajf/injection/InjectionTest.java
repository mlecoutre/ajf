package am.ajf.injection;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManagerFactory;
import am.ajf.core.datas.AuditData;
import am.ajf.core.utils.BeanUtils;
import am.ajf.injection.events.BasicEventImpl;
import foo.core.services.MyService;
import foo.lib.services.MyServiceBD;

public class InjectionTest {

	@Inject
	private Logger logger;
	
	@Inject
	private AuditData auditData;
	
	@Inject
	private Cache cache;
	
	@Inject 
	private javax.enterprise.event.Event<BasicEventImpl> event;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		/* turn OFF throwing exception when a configuration key is missing */
		ApplicationContext.getConfiguration().setThrowExceptionOnMissing(false);
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		BeanUtils.terminate();
	}
	
	public InjectionTest() {
		super();
	}
	
	@Before
	public void setUp() {
		BeanUtils.initialize(this);
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
	public void testCacheInjection() {
		
		assertNotNull(cache);
		
		logger.info("Cache injected.");
				
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
