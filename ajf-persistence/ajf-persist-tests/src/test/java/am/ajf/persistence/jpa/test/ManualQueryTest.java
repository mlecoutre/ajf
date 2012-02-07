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
import am.ajf.persistence.jpa.test.harness.ManualService;
import am.ajf.persistence.jpa.test.harness.ManualServiceBD;
import am.ajf.persistence.jpa.test.harness.Model1;
import am.ajf.persistence.jpa.test.harness.ModelManual;

@RunWith(Arquillian.class)
public class ManualQueryTest {
	
	@Inject
	private EntityManagerFactory emf;

	@Inject
	private ManualServiceBD manualService;
	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(ManualServiceBD.class)
				.addClasses(ManualService.class)
				.addClasses(EntityManagerProvider.class)								
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {				                        
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new ModelManual("Doc", "nain"));
        em.persist(new ModelManual("Sneezy", "nain"));
        em.persist(new ModelManual("Sleepy", "nain"));
        em.persist(new ModelManual("Grumpy", "nain"));
        em.persist(new ModelManual("Happy", "nain"));
        em.persist(new ModelManual("Bashful", "nain"));
        em.persist(new ModelManual("Dopey", "nain"));
        
        
        List<Model1> list = em.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list) {
        	System.out.println("Inserted1 : ("+model.getId()+", "+model.getName()+")");
        }
        em.getTransaction().commit();
        System.out.println("After commit, new emf.");
        //EntityManager em2 = EntityManagerProvider.createEntityManager("default");
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        List<Model1> list2 = em2.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list2) {
        	System.out.println("Inserted2 : ("+model.getId()+", "+model.getName()+")");
        }
	}

	@After
	public void tearDown() throws Exception {
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Model1> list = em.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list) {
        	em.remove(model);
        }
        em.getTransaction().commit();
        em.close();
	}

	@Test
	public void testFindManualQuery() {	        
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