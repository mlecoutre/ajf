package am.ajf.injection.beans;

import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
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
import am.ajf.injection.AuditDataProducer;
import am.ajf.injection.CacheInterceptor;
import am.ajf.injection.CacheProducer;
import am.ajf.injection.ConfigurationProducer;
import am.ajf.injection.LoggerProducer;
import am.ajf.injection.MonitoringInterceptor;
import am.ajf.injection.PropertyProducer;
import am.ajf.injection.annotation.Profile;
import foo.beans.MyService;

@RunWith(Arquillian.class)
public class BeanInjectionTest {

	@Inject
	private Logger logger;
	
	@Inject 
	private MyService myDefaultService;
	
	@Inject @Default
	private MyService myExplictDefaultService;
	
	@Inject @Profile
	private MyService myDefaultQualifiedService;
	
	@Inject @Profile("myNamedBean")
	private MyService myQualifiedService;
	
	@Inject @Any
	private Instance<MyService> myServiceInstances;
	
	@Inject
	private BeanManager beanManager;
	
	@BeforeClass
	public static void setUpClass() {
		ApplicationContext.init();
	}

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo.beans", "am.ajf.core")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(ConfigurationProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addClasses(CacheInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}
	

	public BeanInjectionTest() {
		super();
	}
	
	@Test
	public void testFindBeans() {
		
		assertNotNull(beanManager);
		
		Annotation anyAnnotation = new Any() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Any.class;
			}
		};
		
		Set<javax.enterprise.inject.spi.Bean<?>> beans = beanManager.getBeans(MyService.class, anyAnnotation);
		Iterator<javax.enterprise.inject.spi.Bean<?>> iterator = beans.iterator();
		while (iterator.hasNext()) {
			javax.enterprise.inject.spi.Bean<?> bean = iterator.next();
			logger.info(String.format("Find Bean: %s", bean.getName()));
		}
		
		logger.info("BeanManager injected.");
		
	}
	
	@Test
	public void testFindInstances() {
		assertNotNull(myServiceInstances);
		
		Iterator<MyService> iterator = myServiceInstances.iterator();
		while (iterator.hasNext()) {
			MyService myService = iterator.next();
			logger.info(String.format("Find Instance: %s", myService.toString()));
		}
		
		logger.info("Bean Instances injected.");
	}
	
	@Test
	public void testQualifiedBeanInjection() {
		assertNotNull(myQualifiedService);
		logger.info("Qualified Bean injected.");
		myQualifiedService.doSomething();
		logger.info(myQualifiedService.toString());
	}
	
	@Test
	public void testDefaultBeanInjection() {
		assertNotNull(myDefaultService);
		logger.info("Default Bean injected.");
		myDefaultService.doSomething();
		logger.info(myDefaultService.toString());
	}
	
	@Test
	public void testExplicitDefaultBeanInjection() {
		assertNotNull(myExplictDefaultService);
		logger.info("Explicit Default Bean injected.");
		myExplictDefaultService.doSomething();
		logger.info(myExplictDefaultService.toString());
	}
	
	@Test
	public void testDefaultQualifiedBeanInjection() {
		assertNotNull(myDefaultQualifiedService);
		logger.info("Default Qualified Bean injected.");
		myDefaultQualifiedService.doSomething();
		logger.info(myDefaultQualifiedService.toString());
	}

}
