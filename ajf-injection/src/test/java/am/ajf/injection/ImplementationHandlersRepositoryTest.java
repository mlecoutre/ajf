package am.ajf.injection;

import java.lang.reflect.Constructor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import am.ajf.injection.servicehandlers.SimpleImplHandler;
import am.ajf.injection.servicehandlers.SimpleServiceBD;

public class ImplementationHandlersRepositoryTest {

	public ImplementationHandlersRepository cut;

	@Before
	public void setUp() throws Exception {
		cut = new ImplementationHandlersRepository();
	}

	@After
	public void tearDown() throws Exception {
		cut = null;
	}

	@Test
	public void testAddHandler() throws Exception {
		cut.addHandler(SimpleImplHandler.class);
		cut.completeScan();
		
		Assert.assertNotNull(cut.getHandlers());
		Assert.assertEquals(1, cut.getHandlers().size());		
	}

	@Test
	public void testIsHandler() {
		Assert.assertTrue(cut.isHandler(SimpleImplHandler.class));
	}

	@Test
	public void testBuildImplFor() throws Exception {
		cut.addHandler(SimpleImplHandler.class);
		cut.completeScan();
		Class<?> impl = cut.buildImplFor(SimpleServiceBD.class, null);
		
		//ugly but only for testing (issue with inner class and default constructor)
		Constructor<?> c = impl.getDeclaredConstructors()[0];
		
		SimpleServiceBD instance = (SimpleServiceBD) c.newInstance(new SimpleImplHandler());
		String result = instance.doSomething();
		
		Assert.assertEquals("result", result);
	}

}
