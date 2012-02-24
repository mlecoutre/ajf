package am.ajf.injection;

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
import am.ajf.injection.implhandlers.ComplexService;
import am.ajf.injection.implhandlers.ComplexServiceBD;
import am.ajf.injection.implhandlers.FirstImplHandler;
import am.ajf.injection.implhandlers.SecondImplHandler;
import am.ajf.injection.implhandlers.SimpleServiceBD;

@RunWith(Arquillian.class)
public class ImplementationHandlersTest {
	
	@Inject
	private SimpleServiceBD simpleService; 
	
	@Inject
	private ComplexServiceBD complexService; 
	
	@BeforeClass
	public static void setUpClass() {		
		ApplicationContext.init();				
	}

	/*
	@Deployment(name = "dep1", order = 1)
	public static JavaArchive createTestArchive1() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test1.jar")				
				.addClasses(FirstServiceHandler.class)
				.addClasses(SimpleServiceBD.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}
	*/
	
	@Deployment/*(name = "dep2", order = 2)*/
	public static JavaArchive createTestArchive2() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test2.jar")				
				.addClasses(FirstImplHandler.class)
				.addClasses(SecondImplHandler.class)
				.addClasses(ComplexServiceBD.class)
				.addClasses(ComplexService.class)
				.addClasses(SimpleServiceBD.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"));
		return archive;
	}

	public ImplementationHandlersTest() {
		super();
	}
	
	@Test
	//@OperateOnDeployment("dep1")
	public void testSimpleImplHandler() {		
		String res = simpleService.doSomething();
		
		Assert.assertNotNull(res);
		Assert.assertEquals("result", res);		
	}
	
	@Test
	//@OperateOnDeployment("dep2")
	public void testComplexImplHandler() {		
		String res = complexService.doSomething();		
		Assert.assertNotNull(res);
		Assert.assertEquals("result", res);		
		
		String res2 = complexService.doSomethingElse();		
		Assert.assertNotNull(res2);
		Assert.assertEquals("result-2", res2);
		
		String res3 = complexService.doSomethingManually();		
		Assert.assertNotNull(res3);
		Assert.assertEquals("result-3", res3);
		
	}

}
