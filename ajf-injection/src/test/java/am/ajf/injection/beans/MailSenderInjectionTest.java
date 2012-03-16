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
import am.ajf.core.mail.MailSender;
import am.ajf.core.mail.impl.SimpleMailSenderImpl;
import am.ajf.injection.AuditDataProducer;
import am.ajf.injection.CacheInterceptor;
import am.ajf.injection.CacheProducer;
import am.ajf.injection.ConfigurationProducer;
import am.ajf.injection.LoggerProducer;
import am.ajf.injection.MonitoringInterceptor;
import am.ajf.injection.PropertyProducer;
import am.ajf.injection.annotation.Bean;
import am.ajf.injection.annotation.DefaultBean;

@RunWith(Arquillian.class)
public class MailSenderInjectionTest {

	private Logger logger = LoggerFactory
			.getLogger(MailSenderInjectionTest.class);

	@Inject
	private MailSender myDefaultService;
	
	@Inject
	private SimpleMailSenderImpl myDefaultServiceImpl;
	
	@Inject
	private AnotherMailSenderImpl anOtherServiceImpl;
	
	@Inject
	@Bean
	private MailSender myImplicitBeanService;
	
	@Inject
	@DefaultBean
	private MailSender myExplicitDefaultBeanService;
	
	@Inject
	@Bean("toto")
	private MailSender myNamedBeanService;
	
	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addPackages(true, "am.ajf.core")
				.addClasses(AnotherMailSenderImpl.class)
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

	public MailSenderInjectionTest() {
		super();
	}

	@Test
	public void testDefaultMailSenderInjection() {
		assertNotNull(myDefaultService);
		logger.info("Default MailSender injected.");
		logger.info(myDefaultService.toString());
	}
	
	@Test
	public void testDefaultMailSenderImplInjection() {
		assertNotNull(myDefaultServiceImpl);
		logger.info("MailSender Impl injected.");
		logger.info(myDefaultServiceImpl.toString());
	}
	
	@Test
	public void testAnotherMailSenderImplInjection() {
		assertNotNull(anOtherServiceImpl);
		logger.info("Another MailSender Impl injected.");
		logger.info(anOtherServiceImpl.toString());
	}

	@Test
	public void testImplicitDefaultMailSenderBeanInjection() {
		assertNotNull(myImplicitBeanService);
		logger.info("Default Implicit MailSender Bean injected.");
		logger.info(myImplicitBeanService.toString());
	}

	@Test
	public void testExplicitDefaultMailSenderBeanInjection() {
		assertNotNull(myExplicitDefaultBeanService);
		logger.info("Default Explicit MailSender Bean injected.");
		logger.info(myExplicitDefaultBeanService.toString());
	}
	
	@Test
	public void testNamedMailSenderBeanInjection() {
		assertNotNull(myNamedBeanService);
		logger.info("Named MailSender Bean injected.");
		logger.info(myNamedBeanService.toString());
	}


}
