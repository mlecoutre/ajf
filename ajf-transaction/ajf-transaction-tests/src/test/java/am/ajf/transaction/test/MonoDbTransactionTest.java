package am.ajf.transaction.test;


import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.transaction.TransactionExtension;
import am.ajf.transaction.TransactionInterceptor;
import am.ajf.transaction.UserTransactionProducer;
import am.ajf.transaction.test.harness.JPAQueriesOnDBHsqlPolicy;
import am.ajf.transaction.test.harness.Model1;
import am.ajf.transaction.test.harness.Model2;
import am.ajf.transaction.test.helper.DBHelper;
import am.ajf.transaction.test.helper.EntityManagerProvider;

@RunWith(Arquillian.class)
public class MonoDbTransactionTest {	
	
	
	@Inject
	private JPAQueriesOnDBHsqlPolicy cut;
	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(TransactionExtension.class)
				.addClasses(TransactionInterceptor.class)
				.addClasses(UserTransactionProducer.class)
				.addClasses(JPAQueriesOnDBHsqlPolicy.class)
				.addClasses(Model1.class)
				.addClasses(Model2.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}	
	
	@Before
	public void setUp() throws Exception {
		DBHelper.insertNames(EntityManagerProvider.getEntityManagerFactory1(), 1, "nicolas", "vincent");
	}

	@After
	public void tearDown() throws Exception {
		DBHelper.cleanDB12();
	}
	
	/**
	 * Test a simple select on the database.
	 * Nothing exceptional should happen here. However, if it fails, it can detect
	 * bad configurations issues. 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testSimpleSelect() throws IllegalArgumentException, IllegalAccessException {
		List<Model1> models = cut.findAllModel();
		
		Assert.assertNotNull(models);
		Assert.assertEquals(2, models.size());
	}
	
	/**
	 * Test inserting 4 objects and check the result.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testInsertAlone() throws IllegalArgumentException, IllegalAccessException {
		List<Model1> modelsBefore = cut.findAllModel();
		
		Assert.assertNotNull(modelsBefore);
		Assert.assertEquals(2, modelsBefore.size());
		
		cut.insert4Models();
		
		List<Model1> modelsAfter = cut.findAllModel();
		
		Assert.assertNotNull(modelsAfter);
		Assert.assertEquals(6, modelsAfter.size());
	}
	
	/**
	 * Test inserting 4 objects with an exception and check the result.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testInsertWithError() throws IllegalArgumentException, IllegalAccessException {
		List<Model1> modelsBefore = cut.findAllModel();
		
		Assert.assertNotNull(modelsBefore);
		Assert.assertEquals(2, modelsBefore.size());
		
		Exception ex = null;
		try {
			cut.insert4ModelsWithError();
		} catch (Exception e) {
			ex = e;
		}
		
		Assert.assertNotNull(ex);
		
		List<Model1> modelsAfter = cut.findAllModel();
		
		Assert.assertNotNull(modelsAfter);
		Assert.assertEquals(2, modelsAfter.size());
	}
	
	
}
