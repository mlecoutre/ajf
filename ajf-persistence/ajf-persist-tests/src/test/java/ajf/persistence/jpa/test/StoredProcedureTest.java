package ajf.persistence.jpa.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
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

import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.harness.Model1;
import ajf.persistence.jpa.test.harness.ModelCrud;
import ajf.persistence.jpa.test.harness.ModelSp;
import ajf.persistence.jpa.test.harness.StoredProcedureNoImplServiceBD;

@RunWith(Arquillian.class)
public class StoredProcedureTest {
	
	@Inject
	private StoredProcedureNoImplServiceBD storedProcedureNoImpl;
	
	@Inject
	private EntityManagerFactory emf;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(StoredProcedureNoImplServiceBD.class)		
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
        
        em.persist(new Model1("nicolas"));
        em.persist(new Model1("vincent"));
        em.persist(new Model1("matthieu"));
        
        Session hSession = (Session) em.getDelegate();
        hSession.doWork(new Work() {
			
			@Override
			public void execute(Connection con) throws SQLException {				
				Statement stmt1 = con.createStatement();
				String sp1 = "CREATE PROCEDURE ZZTESTNOPARAM()\n"+
							 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
						     " BEGIN ATOMIC\n"+
							 "  DECLARE result CURSOR FOR SELECT id, name FROM model1;\n"+
						     "  OPEN result;\n"+
							 " END";
				stmt1.executeUpdate(sp1);
				stmt1.close();
				
				Statement stmt2 = con.createStatement();
				String sp2 = "CREATE PROCEDURE ZZTESTWITHPARAM(pName VARCHAR(50))\n"+
							 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
							 " BEGIN ATOMIC\n"+
							 "  DECLARE result CURSOR FOR SELECT id, name FROM model1 m WHERE m.name = pName;\n"+
							 "   OPEN result;\n"+
							 " END";
				stmt2.executeUpdate(sp2);
				stmt2.close();
				
				Statement stmt3 = con.createStatement();
				String sp3 = "CREATE PROCEDURE ZZTESTONEWITHPARAM(pName VARCHAR(50))\n"+
							 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
							 " BEGIN ATOMIC\n"+
						 	 "  DECLARE result CURSOR FOR SELECT id, name FROM model1 m WHERE m.name = pName;\n"+
						 	 "  OPEN result;\n"+
							 " END";
				stmt3.executeUpdate(sp3);
				stmt3.close();										
				
			}
		});
        em.getTransaction().commit();
        em.close();
        
	}

	@After
	public void tearDown() throws Exception {
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Model1> list = em.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list) {
        	em.remove(model);
        }
        Session hSession = (Session) em.getDelegate();
        hSession.doWork(new Work() {
			
			@Override
			public void execute(Connection con) throws SQLException {				
				Statement stmt1 = con.createStatement();
				String sp1 = "DROP PROCEDURE IF EXISTS ZZTESTNOPARAM";
				stmt1.executeUpdate(sp1);
				stmt1.close();
				
				Statement stmt2 = con.createStatement();
				String sp2 = "DROP PROCEDURE IF EXISTS ZZTESTWITHPARAM";
				stmt2.executeUpdate(sp2);
				stmt2.close();
				
				Statement stmt3 = con.createStatement();
				String sp3 = "DROP PROCEDURE IF EXISTS ZZTESTONEWITHPARAM";
				stmt3.executeUpdate(sp3);
				stmt3.close();										
				
			}
		});
        em.getTransaction().commit();
        em.close();	
	}

	@Test
	public void testStoredProcedureMultipleResultsNoArgsWithNoImpl() {	        
		List<ModelSp> res = storedProcedureNoImpl.findAllModels();
		Assert.assertNotNull(res);
		Assert.assertEquals(3, res.size());		
	}	
	
	@Test
	public void testStoredProcedureMultipleResultsOneArgsWithNoImpl() {
		Object obj = storedProcedureNoImpl.findAllModelsByName("nicolas");
		List<ModelSp> res = (List<ModelSp>) obj;
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());		
	}	
	
	@Test
	public void testStoredProcedureOneResultsOneArgsWithNoImpl() {	        
		ModelSp res = storedProcedureNoImpl.findOneModelByName("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals("nicolas", res.getName());		
	}	
	
}
