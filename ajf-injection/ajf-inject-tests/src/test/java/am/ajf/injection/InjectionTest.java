package am.ajf.injection;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.datas.AuditData;
import am.ajf.core.logger.LoggerFactory;
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
	private javax.enterprise.event.Event<BasicEventImpl> event; 
			
	@BeforeClass
	public static void setUpClass() {
		
		LoggerFactory.getDelegate();
		
	}
	
	@Before
	public void setUp() throws Exception {
						
		/* turn OFF throwing exception when a configuration key is missing */
		ApplicationContext.getConfiguration().setThrowExceptionOnMissing(false);
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		BeanUtils.terminate();
		
	}
			
	@Test
	public void testSelfInjection() {
		
		BeanUtils.initialize(this);
		
		assertNotNull(logger);
		
		logger.info("Injected.");
		
	}
	
	@Test
	public void testFireEvent() {
	
		BeanUtils.initialize(this);
		
		assertNotNull(event);	
		
		event.fire(new BasicEventImpl("myEvent"));
		
	}
	
	@Test
	public void testServiceInjection() {
		
		MyServiceBD svc = BeanUtils.newInstance(MyServiceBD.class);
		
		String res = svc.myFirstOperation("vincent");
		
		assertNotNull(res);
		
	}
	
	@Test
	public void testServiceImplInjection() {
		
		MyServiceBD svc = BeanUtils.newInstance(MyService.class);
		
		String res = svc.myFirstOperation("vincent");
		
		assertNotNull(res);
		
	}
	
}
