package am.ajf.persistence.jpa.test;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
import am.ajf.persistence.jpa.impl.CrudImplHandler;
import am.ajf.persistence.jpa.test.harness.ModelCrud;
import am.ajf.persistence.jpa.test.harness.SimpleCrudServiceBD;

@RunWith(Arquillian.class)
public class CrudTest {

	@Inject
	private SimpleCrudServiceBD crudService;
	
	@Inject
	private EntityManagerFactory emf;


	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(SimpleCrudServiceBD.class)
				.addClasses(EntityManagerProvider.class)
				.addClasses(ModelCrud.class)
				.addClasses(CrudImplHandler.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() {		
	}
	
	@After
	public void tearDown() {		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<ModelCrud> list = em.createQuery("SELECT m FROM ModelCrud m").getResultList();
        for (ModelCrud model : list) {
        	em.remove(model);
        }
        em.getTransaction().commit();
        em.close();
	}

	/**
	 * Create a new Model and save it with the save method
	 * The resulting object should have an ID, and the name should be the same.
	 */
	@Test
	public void testSaveNew() {
		ModelCrud model = crudService.save(new ModelCrud("Nicolas"));
		Assert.assertNotNull(model);
		Assert.assertNotNull(model.getId());
		Assert.assertEquals("Nicolas", model.getName());
	}
	
	/**
	 * Create a simple object then modify it with the save method. 
	 * Id should be the same, and the name should change.
	 */
	@Test
	public void testSaveExisting() {
		ModelCrud model = crudService.save(new ModelCrud("Nicolas"));
		Long id = model.getId();
		
		Assert.assertNotNull(model);
		
		model.setName("Vincent");
		ModelCrud model1 = crudService.save(model);
		
		Assert.assertNotNull(model1);
		Assert.assertEquals(id, model1.getId());
		Assert.assertEquals("Vincent", model1.getName());		
	}
	
	@Test
	public void testNamedQueryNoArgs() {
		ModelCrud model1 = crudService.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudService.save(new ModelCrud("Nicolas"));
		ModelCrud model3 = crudService.save(new ModelCrud("Vincent"));					
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		Assert.assertNotNull(model3);				
		
		List<ModelCrud> models = crudService.find(ModelCrud.FIND_ALL);
		
		Assert.assertNotNull(models);
		Assert.assertEquals(3, models.size());				
	}
	
	@Test
	public void testNamedQueryWithArgs() {
		ModelCrud model1 = crudService.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudService.save(new ModelCrud("Nicolas"));
		ModelCrud model3 = crudService.save(new ModelCrud("Vincent"));					
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		Assert.assertNotNull(model3);				
		
		List<ModelCrud> models = crudService.find(ModelCrud.FIND_BY_NAME, "Vincent");
		
		Assert.assertNotNull(models);
		Assert.assertEquals(1, models.size());				
	}
	
	/*
	@Test
	public void testFetch() {
		ModelCrud model1 = crudService.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudService.save(new ModelCrud("Vincent"));
		Long id = model1.getId();
		
		Assert.assertNotNull(model1);			
		
		ModelCrud model = crudService.fetch(id);
		
		Assert.assertNotNull(model);
		Assert.assertEquals("Matthieu", model.getName());				
	}
	*/
	
	@Test
	public void testRemove() {
		ModelCrud model1 = crudService.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudService.save(new ModelCrud("Vincent"));
		Long id = model1.getId();
		
		Assert.assertNotNull(model1);			
		
		boolean isRemoveOk = crudService.remove(model1);
		
		Assert.assertTrue(isRemoveOk);
		Assert.assertNotNull(model1.getId()); //This is normal as the object was not refreshed !
		
		List<ModelCrud> models = crudService.find(ModelCrud.FIND_ALL);		
		
		Assert.assertNotNull(models);
		Assert.assertEquals(1, models.size());
		
	}
	
	/*
	@Test
	public void testDelete() {
		ModelCrud model1 = crudService.save(new ModelCrud("Matthieu"));
		ModelCrud model2 = crudService.save(new ModelCrud("Vincent"));
		Long id = model1.getId();
		
		Assert.assertNotNull(model1);
		Assert.assertNotNull(model2);
		
		boolean isRemoveOk = crudService.delete(id);
		
		Assert.assertTrue(isRemoveOk);
		Assert.assertNotNull(model1.getId()); //This is normal as the object was not refreshed !
		
		ModelCrud model = crudService.fetch(id);
		
		Assert.assertNull(model);
	}
	*/

}
