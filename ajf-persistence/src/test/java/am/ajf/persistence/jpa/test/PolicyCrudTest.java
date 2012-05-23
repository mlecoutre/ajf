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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.injection.UserTransactionProducer;
import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.api.CrudBD;
import am.ajf.persistence.jpa.api.CrudServiceBD;
import am.ajf.persistence.jpa.impl.CrudImplHandler;
import am.ajf.persistence.jpa.impl.CrudProvider;
import am.ajf.persistence.jpa.test.harness.ModelCrud;
import am.ajf.persistence.jpa.test.helper.DBHelper;

/**
 * TODO The ignore can be removed when :
 * - the test plumbing is added for transactional tests (JTA)
 * - PersistenceUnits are allowed for PolicyCrud and ServiceCrud
 * 
 * @author Nicolas Radde (E016696)
 */
@RunWith(Arquillian.class)
@Ignore
public class PolicyCrudTest {

	@Inject
	private CrudBD<ModelCrud, Long> crudPolicy;


	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")				
				.addClasses(EntityManagerProvider.class)
				.addClasses(ModelCrud.class)
				.addClasses(CrudImplHandler.class)
				.addClasses(CrudBD.class)
				.addClasses(CrudProvider.class)
				.addClasses(CrudServiceBD.class)	
				.addClasses(UserTransactionProducer.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() {		
	}
	
	@After
	public void tearDown() throws Exception {		
		DBHelper.executeSQLInTransaction("default", "DELETE FROM ModelCrud");
	}

	/**
	 * Create a new Model and save it with the save method
	 * The resulting object should have an ID, and the name should be the same.
	 * @throws Throwable 
	 */
	@Test
	public void testSaveNew() throws Throwable {
		ModelCrud model = crudPolicy.save(new ModelCrud("Nicolas"));
		Assert.assertNotNull(model);
		Assert.assertNotNull(model.getId());
		Assert.assertEquals("Nicolas", model.getName());
	}
	
	/**
	 * Create a simple object then modify it with the save method. 
	 * Id should be the same, and the name should change.
	 * @throws Throwable 
	 */
	@Test
	public void testSaveExisting() throws Throwable {
		ModelCrud model = crudPolicy.save(new ModelCrud("Nicolas"));
		Long id = model.getId();
		
		Assert.assertNotNull(model);
		
		model.setName("Vincent");
		ModelCrud model1 = crudPolicy.save(model);
		
		Assert.assertNotNull(model1);
		Assert.assertEquals(id, model1.getId());
		Assert.assertEquals("Vincent", model1.getName());		
	}
	
	@Test
	public void testNamedQueryNoArgs() throws Throwable {
		ModelCrud model1 = crudPolicy.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudPolicy.save(new ModelCrud("Nicolas"));
		ModelCrud model3 = crudPolicy.save(new ModelCrud("Vincent"));					
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		Assert.assertNotNull(model3);				
		
		List<ModelCrud> models = crudPolicy.find(ModelCrud.FIND_ALL);
		
		Assert.assertNotNull(models);
		Assert.assertEquals(3, models.size());				
	}
	
	@Test
	public void testNamedQueryWithArgs() throws Throwable {
		ModelCrud model1 = crudPolicy.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudPolicy.save(new ModelCrud("Nicolas"));
		ModelCrud model3 = crudPolicy.save(new ModelCrud("Vincent"));					
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		Assert.assertNotNull(model3);				
		
		List<ModelCrud> models = crudPolicy.find(ModelCrud.FIND_BY_NAME, "Vincent");
		
		Assert.assertNotNull(models);
		Assert.assertEquals(1, models.size());				
	}
	
	@Test
	public void testMiddlePageWithArgs() throws Throwable {
		for (int i=0 ; i < 10 ; i++) {
			crudPolicy.save(new ModelCrud("Matthieu"));
		}		
		crudPolicy.save(new ModelCrud("Nicolas"));
		crudPolicy.save(new ModelCrud("Vincent"));								
		
		List<ModelCrud> models = crudPolicy.page(ModelCrud.FIND_BY_NAME, 4, 2, "Matthieu");
		
		Assert.assertNotNull(models);
		Assert.assertEquals(2, models.size());				
	}
	
	@Test
	public void testCountWithArgs() throws Throwable {
		for (int i=0 ; i < 10 ; i++) {
			crudPolicy.save(new ModelCrud("Matthieu"));
		}		
		crudPolicy.save(new ModelCrud("Nicolas"));
		crudPolicy.save(new ModelCrud("Vincent"));								
		
		long size = crudPolicy.count(ModelCrud.COUNT_BY_NAME, "Matthieu");
				
		Assert.assertEquals(10, size);				
	}
	
	@Test
	public void testFetch() throws Throwable {
		ModelCrud model1 = crudPolicy.save(new ModelCrud("Matthieu"));
		Long id = model1.getId();
		
		Assert.assertNotNull(model1);			
		
		ModelCrud model = crudPolicy.fetch(id);
		
		Assert.assertNotNull(model);
		Assert.assertEquals("Matthieu", model.getName());				
	}
	
	
	@Test
	public void testRemove() throws Throwable {
		ModelCrud model1 = crudPolicy.save(new ModelCrud("Matthieu"));
		crudPolicy.save(new ModelCrud("Vincent"));
		
		Assert.assertNotNull(model1);			
		
		boolean isRemoveOk = crudPolicy.remove(model1);
		
		Assert.assertTrue(isRemoveOk);
		Assert.assertNotNull(model1.getId()); //This is normal as the object was not refreshed !
		
		List<ModelCrud> models = crudPolicy.find(ModelCrud.FIND_ALL);		
		
		Assert.assertNotNull(models);
		Assert.assertEquals(1, models.size());
		
	}
		
	@Test
	public void testDelete() throws Throwable {
		ModelCrud model1 = crudPolicy.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudPolicy.save(new ModelCrud("Vincent"));
		Long id = model1.getId();
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		
		boolean isRemoveOk = crudPolicy.delete(id);
		
		Assert.assertTrue(isRemoveOk);
		Assert.assertNotNull(model1.getId()); //This is normal as the object was not refreshed !
		
		ModelCrud model = crudPolicy.fetch(id);
		
		Assert.assertNull(model);
	}
	

}
