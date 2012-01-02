package ajf.persistence.jpa.test;

import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.harness.Model1;
import ajf.persistence.jpa.test.harness.ModelCrud;
import ajf.persistence.jpa.test.harness.NamedQueryNoImplPolicy;
import ajf.persistence.jpa.test.harness.NamedQueryNoImplServiceBD;
import ajf.persistence.jpa.test.harness.NamedQueryWithImplService;
import ajf.persistence.jpa.test.harness.NamedQueryWithImplServiceBD;
import ajf.persistence.jpa.test.harness.SimpleCrudServiceBD;

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
				.addClasses(NamedQueryWithImplService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {				                
        EntityManager em = EntityManagerProvider.getEntityManager("default");
        em.getTransaction().begin();
        em.persist(new Model1("nicolas"));
        em.persist(new Model1("vincent"));                
	}

	@After
	public void tearDown() throws Exception {
		EntityManagerProvider.getEntityManager("default").getTransaction().rollback();
		EntityManagerProvider.closeAll();		
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
