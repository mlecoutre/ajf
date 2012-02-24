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
import am.ajf.persistence.jpa.JpaExtension;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;
import am.ajf.persistence.jpa.test.harness.ManualService;
import am.ajf.persistence.jpa.test.harness.ManualServiceBD;
import am.ajf.persistence.jpa.test.harness.ModelManual;
import am.ajf.persistence.jpa.test.helper.DBHelper;

@RunWith(Arquillian.class)
@PersistenceUnit("jpa2")
public class ManualQueryTest {

	@Inject
	private ManualServiceBD manualService;
	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(ManualServiceBD.class)
				.addClasses(ManualService.class)
				.addClasses(JpaExtension.class)
				.addClasses(EntityManagerProvider.class)								
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {			                        		
	}

	@After
	public void tearDown() throws Exception {
		DBHelper.executeSQLInTransaction("jpa2", "DELETE FROM ModelManual");
	}

	@Test
	public void testFindManualQuery() {	  
		manualService.insertNew(new ModelManual("Doc", "nain"));
		manualService.insertNew(new ModelManual("Sneezy", "nain"));
		manualService.insertNew(new ModelManual("Sleepy", "nain"));
		manualService.insertNew(new ModelManual("Grumpy", "nain"));
		manualService.insertNew(new ModelManual("Happy", "nain"));
		manualService.insertNew(new ModelManual("Bashful", "nain"));
		manualService.insertNew(new ModelManual("Dopey", "nain"));
		
		List<ModelManual> res = manualService.findByNameOrderBy("nain", "firstName");
		
		Assert.assertNotNull(res);
		Assert.assertEquals(7, res.size());
		Assert.assertEquals("Bashful", res.get(0).getFirstName());
		Assert.assertEquals("Sneezy", res.get(6).getFirstName());
	}
	
	@Test
	public void testNewManualQuery() {	        
		manualService.insertNew(new ModelManual("Snow", "White"));
		List<ModelManual> res = manualService.findByNameOrderBy("White", "firstName");
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());
		Assert.assertEquals("Snow", res.get(0).getFirstName());		
	}
	
}