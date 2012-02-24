package am.ajf.injection;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import am.ajf.core.services.exceptions.BusinessLayerException;
import am.ajf.core.services.exceptions.ServiceLayerException;

import foo.errorhandling.UnknownType;
import foo.lib.business.MyBD;
import foo.lib.services.MyServiceBD;
import foo.web.controllers.MyMBean;

@RunWith(Arquillian.class)
public class ErrorHandlingTest {

	@Inject
	private Logger logger;

	@Inject
	private MyServiceBD myService;

	@Inject
	MyBD myPolicyBD;

	@Inject
	MyMBean myMBean;

	@Inject
	UnknownType unknownType;

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addPackages(true, "foo.core")
				.addPackages(true, "foo.errorhandling")
				.addPackages(true, "foo.lib")
				.addPackages(true, "foo.services")
				.addPackages(true, "foo.web")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addClass(ErrorHandlingTest.class)
				.addClass(ErrorHandlingInterceptor.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}

	@Test
	public void testUnknownTypeWhenOK() throws Exception {
		// should log arithmetic exception and throw it.
		String result = unknownType
				.testMBeanErrorHandling(UnknownType.ErrorHandlingCase.OK);
		org.junit.Assert.assertTrue("Everything should be fine.",
				"ok".equals(result));

	}

	@Test(expected = ArithmeticException.class)
	public void testUnknownTypeWhenError() throws Exception {
		// should log arithmetic exception and throw it.
		unknownType.testMBeanErrorHandling(UnknownType.ErrorHandlingCase.ERROR);

	}

	@Test
	public void testServiceErrorHandlingWhenOK() throws Exception {
		logger.debug(" * testErrorHandling SERVICE OK");
		String result = myService
				.myErrorHandlingMethod(MyServiceBD.ErrorHandlingCase.OK);
		org.junit.Assert.assertTrue("Everything should be fine.",
				"ok".equals(result));
	}

	@Test(expected = ServiceLayerException.class)
	public void testServiceErrorHandlingWhenError() throws Exception {
		logger.debug(" * testErrorHandling SERVICE ERROR");
		myService.myErrorHandlingMethod(MyServiceBD.ErrorHandlingCase.ERROR);
	}

	@Test
	public void testPolicyErrorHandlingWhenOK() throws Exception {
		logger.debug(" * testErrorHandling POLICY OK");
		String result = myPolicyBD.testErrorHandling(MyBD.ErrorHandlingCase.OK);
		org.junit.Assert.assertTrue("Everything should be fine.",
				"ok".equals(result));
	}

	@Test(expected = BusinessLayerException.class)
	public void testPolicyErrorHandlingWhenError() throws Exception {
		logger.debug(" * testErrorHandling POLICY ERROR");
		myPolicyBD.testErrorHandling(MyBD.ErrorHandlingCase.ERROR);
	}
	
	/*
	 * not easy to test Face error handling as FaceContext is a static method
	 * we consider it's work :) 
	 */
	
	//
	// @Test
	// public void testMBeanErrorHandlingWhenOK() throws Exception {
	// logger.debug(" * testErrorHandling MBEAN OK");
	// String result = myMBean
	// .testMBeanErrorHandling(MyMBean.ErrorHandlingCase.ERROR);
	// org.junit.Assert.assertTrue("Everything should be fine.",
	// "ok".equals(result));
	// }
	//
	// public void testMBeanErrorHandlingWhenError() throws Exception {
	// logger.debug(" * testErrorHandling MBEAN ERROR");
	// myMBean.testMBeanErrorHandling(MyMBean.ErrorHandlingCase.ERROR);
	// }

}
