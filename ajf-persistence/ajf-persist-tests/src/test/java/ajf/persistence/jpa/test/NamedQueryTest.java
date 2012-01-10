package ajf.persistence.jpa.test;

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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.harness.Model1;
import ajf.persistence.jpa.test.harness.NamedQueryNoImplServiceBD;
import ajf.persistence.jpa.test.harness.NamedQueryWithImplService;
import ajf.persistence.jpa.test.harness.NamedQueryWithImplServiceBD;

@RunWith(Arquillian.class)
public class NamedQueryTest {
	
	@Inject
	private NamedQueryNoImplServiceBD namedQueryNoImpl;
	@Inject
	private NamedQueryWithImplServiceBD namedQueryWithImpl;
	@Inject
	private EntityManagerFactory emf;
	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(NamedQueryNoImplServiceBD.class)
				.addClasses(NamedQueryWithImplServiceBD.class)
				.addClasses(EntityManagerProvider.class)				
				.addClasses(NamedQueryWithImplService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {				                
        //EntityManager em = EntityManagerProvider.createEntityManager("default");
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Model1("nicolas"));
        em.persist(new Model1("vincent"));
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
