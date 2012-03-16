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
import am.ajf.core.logger.LoggerFactory;
import am.ajf.injection.AuditDataProducer;
import am.ajf.injection.CacheInterceptor;
import am.ajf.injection.CacheProducer;
import am.ajf.injection.ConfigurationProducer;
import am.ajf.injection.LoggerProducer;
import am.ajf.injection.MonitoringInterceptor;
import am.ajf.injection.PropertyProducer;
import am.ajf.injection.annotation.Bean;
import foo.beans.MyService;

@RunWith(Arquillian.class)
public class SimpleBeanInjectionTest {

	private Logger logger = LoggerFactory
			.getLogger(SimpleBeanInjectionTest.class);

	@Inject
	private MyService myDefaultService;

	@Inject
	@Bean
	private MyService myBeanService;

	@Inject
	@Bean("myNamedBean")
	private MyService myNamedBeanService;

	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(ConfigurationProducer.class)
				//.addClasses(MailSenderProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addClasses(CacheInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}

	public SimpleBeanInjectionTest() {
		super();
	}

	@Test
	public void testDefaultBeanInjection() {
		assertNotNull(myDefaultService);
		logger.info("Default Bean injected.");
		myDefaultService.doSomething();
		logger.info(myDefaultService.toString());
	}

	@Test
	public void testBeanInjection() {
		assertNotNull(myBeanService);
		logger.info("Default Bean injected.");
		myBeanService.doSomething();
		logger.info(myBeanService.toString());
	}

	@Test
	public void testNamedBeanInjection() {
		assertNotNull(myNamedBeanService);
		logger.info("Named Bean injected.");
		myBeanService.doSomething();
		logger.info(myNamedBeanService.toString());
	}

}
