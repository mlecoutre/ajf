package am.ajf.injection.beans;

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
import am.ajf.core.beans.annotation.Bean;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.injection.AuditDataProducer;
import am.ajf.injection.CacheProducer;
import am.ajf.injection.LoggerProducer;
import am.ajf.injection.PropertyProducer;
import foo.beans.MyService;

@RunWith(Arquillian.class)
public class BeanInjectionTest {

	private Logger logger = LoggerFactory.getLogger(BeanInjectionTest.class);
	
	@Inject @Bean("default")
	private MyService myService;
	
	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo.beans")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				//.addClasses(MonitoringInterceptor.class)
				//.addClasses(TransactionInterceptor.class)
				//.addClasses(CacheInterceptor.class)
				//.addClasses(ErrorHandlingInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}
	

	public BeanInjectionTest() {
		super();
	}
	
	@Test
	public void testBeanInjection() {
		assertNotNull(myService);
		
		logger.info("Bean injected.");
	}

}
