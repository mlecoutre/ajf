package am.ajf.injection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import am.ajf.core.ApplicationContext;

/**
 * Test the repository management and especially the addService method.
 * 
 * @author Nicolas Radde (E016696) 
 */
public class ImplementationsRepositoryTest {
	
	public ImplementationsRepository cut;

	@Before
	public void setUp() throws Exception {
		cut = new ImplementationsRepository();
	}

	@After
	public void tearDown() throws Exception {
		cut = null;
	}

	/**
	 * Test adding the impl first.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddServiceFirst() throws Exception {
		cut.addService(SimpleTestService.class);
		cut.completeScan();
		
		Assert.assertNotNull(cut.getInterfaces());
		Assert.assertEquals(1, cut.getInterfaces().size());
		Assert.assertNotNull(cut.getServicesForInterface(SimpleTestServiceBD.class));
		Assert.assertEquals(1, cut.getServicesForInterface(SimpleTestServiceBD.class).size());		
	}	
	
	/**
	 * Test adding the interface before the impl.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddInterfaceFirst() throws Exception {
		cut.addService(SimpleTestServiceBD.class);
		cut.addService(SimpleTestService.class);
		cut.completeScan();
		
		Assert.assertNotNull(cut.getInterfaces());
		Assert.assertEquals(1, cut.getInterfaces().size());
		Assert.assertNotNull(cut.getServicesForInterface(SimpleTestServiceBD.class));
		Assert.assertEquals(1, cut.getServicesForInterface(SimpleTestServiceBD.class).size());		
	}	
	
	/**
	 * Test adding a sub service class first
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddSubServiceClassFirst() throws Exception {
		cut.addService(SubInheritence1TestService.class);
		cut.addService(Inheritence1TestService.class);
		cut.completeScan();
		
		Assert.assertNotNull(cut.getInterfaces());
		Assert.assertEquals(1, cut.getInterfaces().size());
		Assert.assertNotNull(cut.getServicesForInterface(InheritenceTestServiceBD.class));
		Assert.assertEquals(1, cut.getServicesForInterface(InheritenceTestServiceBD.class).size());
		Assert.assertEquals(SubInheritence1TestService.class, cut.getServicesForInterface(InheritenceTestServiceBD.class).get(0));
	}	
	
	/**
	 * Test adding the subservice class after it's parent
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddSubServiceClassLast() throws Exception {
		cut.addService(Inheritence1TestService.class);
		cut.addService(SubInheritence1TestService.class);
		cut.completeScan();
		
		Assert.assertNotNull(cut.getInterfaces());
		Assert.assertEquals(1, cut.getInterfaces().size());
		Assert.assertNotNull(cut.getServicesForInterface(InheritenceTestServiceBD.class));
		Assert.assertEquals(1, cut.getServicesForInterface(InheritenceTestServiceBD.class).size());
		Assert.assertEquals(SubInheritence1TestService.class, cut.getServicesForInterface(InheritenceTestServiceBD.class).get(0));
	}	
	
	/**
	 * Test adding the multiples impls to the same interface.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddMultipleImpl() throws Exception {
		cut.addService(Multi1TestService.class);
		cut.addService(Multi2TestService.class);		
		cut.completeScan();
		
		Assert.assertNotNull(cut.getInterfaces());
		Assert.assertEquals(1, cut.getInterfaces().size());
		Assert.assertNotNull(cut.getServicesForInterface(MultiTestServiceBD.class));
		Assert.assertEquals(2, cut.getServicesForInterface(MultiTestServiceBD.class).size());		
	}	

	/**
	 * Test a interface with implementation and one without.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsImplemented() throws Exception {
		cut.addService(SimpleTestService.class);
		cut.addService(SimpleInterfaceTestServiceBD.class);
		cut.completeScan();
		
		Assert.assertFalse(cut.isImplemented(SimpleInterfaceTestServiceBD.class));
		Assert.assertTrue(cut.isImplemented(SimpleTestServiceBD.class));		
	}

	@Test
	public void testIsService() {
		Assert.assertTrue(cut.isService(SimpleTestService.class));
		Assert.assertFalse(cut.isService(NotImplServiceF.class));
	}

	@Test
	public void testIsServiceInterface() {
		Assert.assertTrue(cut.isServiceInterface(SimpleInterfaceTestServiceBD.class));
		Assert.assertFalse(cut.isServiceInterface(NotInterfaceServiceBP.class));
	}
	
	
	//Helper class for test	
	public interface SimpleInterfaceTestServiceBD {}
	
	public interface NotInterfaceServiceBP {}
	public class NotImplServiceF implements NotInterfaceServiceBP {}
	
	public interface SimpleTestServiceBD {}
	public class SimpleTestService implements SimpleTestServiceBD {}
	
	public interface MultiTestServiceBD {}
	public class Multi1TestService implements MultiTestServiceBD {}
	public class Multi2TestService implements MultiTestServiceBD {}	
	
	public interface InheritenceTestServiceBD {}
	public class Inheritence1TestService implements InheritenceTestServiceBD {}
	public class SubInheritence1TestService extends Inheritence1TestService implements InheritenceTestServiceBD{}
	public class Inheritence2TestService implements InheritenceTestServiceBD {}
	
	

}
