package am.ajf.persistence.jpa.test;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.impl.NamedQueryImplHandler;
import am.ajf.persistence.jpa.test.harness.Model1;
import am.ajf.persistence.jpa.test.harness.NamedQueryNoImplServiceBD;
import am.ajf.persistence.jpa.test.harness.NamedQueryWithImplService;
import am.ajf.persistence.jpa.test.harness.NamedQueryWithImplServiceBD;
import am.ajf.persistence.jpa.test.helper.DBHelper;

@RunWith(Arquillian.class)
public class NamedQueryTest {
	
	@Inject
	private NamedQueryNoImplServiceBD namedQueryNoImpl;
	@Inject
	private NamedQueryWithImplServiceBD namedQueryWithImpl;	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(NamedQueryNoImplServiceBD.class)
				.addClasses(NamedQueryWithImplServiceBD.class)
				.addClasses(EntityManagerProvider.class)				
				.addClasses(NamedQueryWithImplService.class)
				.addClasses(NamedQueryImplHandler.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {
		DBHelper.executeSQLInTransaction("default", "INSERT INTO model1 VALUES (0,'nicolas')");
		DBHelper.executeSQLInTransaction("default", "INSERT INTO model1 VALUES (1,'vincent')");
	}

	@After
	public void tearDown() throws Exception {
		DBHelper.executeSQLInTransaction("default", "DELETE FROM model1");
	}

	@Test
	public void testNamedQueryWithNoImpl() {	        
		List<Model1> res = namedQueryNoImpl.findAllModelsByName("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());		
	}
	
	@Test
	public void testNamedQueryWithImpl() {	        
		List<Model1> res1 = namedQueryWithImpl.findAllModelsByName("nicolas");		
		Assert.assertNotNull(res1);
		Assert.assertEquals(1, res1.size());			
		
		List<Model1> res2 = namedQueryWithImpl.findManualQuery();		
		Assert.assertNotNull(res2);
		Assert.assertEquals(0, res2.size());		
	}
	
}
