package am.ajf.transaction.test;


import javax.inject.Inject;

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
import am.ajf.transaction.test.harness.JPAQueriesOnDBH2Policy;
import am.ajf.transaction.test.harness.ModelRB;
import am.ajf.transaction.test.helper.DBHelper;
import am.ajf.transaction.test.helper.EntityManagerProvider;

@RunWith(Arquillian.class)
public class DualDbTransactionTest {	
	
	@Inject
	private JPAQueriesOnDBH2Policy cut;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(TransactionExtension.class)
				.addClasses(TransactionInterceptor.class)
				.addClasses(UserTransactionProducer.class)
				.addClasses(JPAQueriesOnDBH2Policy.class)
				.addAsManifestResource("META-INF/beans.xml",
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}
	
	@Before
	public void setUp() throws Exception {
		DBHelper.insertNames(EntityManagerProvider.getEntityManagerFactoryH21(), 1, "nicolas", "vincent");
		DBHelper.insertNames(EntityManagerProvider.getEntityManagerFactoryH22(), 2, "matthieu", "robert", "salvatore");
	}

	@After
	public void tearDown() throws Exception {
		DBHelper.cleanDB34();
	}
	
	@Test
	public void testSimpleMultiBaseSelect() throws IllegalArgumentException, IllegalAccessException {
		ModelRB models = cut.findAllModels();
		
		Assert.assertNotNull(models);
		Assert.assertNotNull(models.getModels1());
		Assert.assertNotNull(models.getModels2());
		
		Assert.assertEquals(2, models.getModels1().size());
		Assert.assertEquals(3, models.getModels2().size());
	}
	
	@Test
	public void testInsertMultiBase() throws IllegalArgumentException, IllegalAccessException  {
		ModelRB models = cut.findAllModels();
		
		Assert.assertNotNull(models);
		Assert.assertNotNull(models.getModels1());
		Assert.assertNotNull(models.getModels2());
		Assert.assertEquals(2, models.getModels1().size());
		Assert.assertEquals(3, models.getModels2().size());
		
		cut.insert4And4Models();
		
		ModelRB modelsAfter = cut.findAllModels();
		
		Assert.assertNotNull(modelsAfter);
		Assert.assertNotNull(modelsAfter.getModels1());
		Assert.assertNotNull(modelsAfter.getModels2());
		Assert.assertEquals(6, modelsAfter.getModels1().size());
		Assert.assertEquals(7, modelsAfter.getModels2().size());
	}
	
	@Test
	public void testInsertMultiBaseExceptionInMiddle() throws Exception  {
		ModelRB models = cut.findAllModels();
		
		Assert.assertNotNull(models);
		Assert.assertNotNull(models.getModels1());
		Assert.assertNotNull(models.getModels2());
		Assert.assertEquals(2, models.getModels1().size());
		Assert.assertEquals(3, models.getModels2().size());
		
		Exception ex = null;
		try {
			cut.insert4ModelsWithErrorThen4();
		} catch (Exception e) {
			ex = e;
		}
		
		Assert.assertNotNull(ex);
		
		ModelRB modelsAfter = cut.findAllModels();
		
		Assert.assertNotNull(modelsAfter);
		Assert.assertNotNull(modelsAfter.getModels1());
		Assert.assertNotNull(modelsAfter.getModels2());
		Assert.assertEquals(2, modelsAfter.getModels1().size());
		Assert.assertEquals(3, modelsAfter.getModels2().size());
	}
	
	@Test
	public void testInsertMultiBaseExceptionAtTheEnd() throws Exception  {
		ModelRB models = cut.findAllModels();
		
		Assert.assertNotNull(models);
		Assert.assertNotNull(models.getModels1());
		Assert.assertNotNull(models.getModels2());
		Assert.assertEquals(2, models.getModels1().size());
		Assert.assertEquals(3, models.getModels2().size());
		
		Exception ex = null;
		try {
			cut.insert4And4ModelsThenError();
		} catch (Exception e) {
			ex = e;
		}
		
		Assert.assertNotNull(ex);
		
		ModelRB modelsAfter = cut.findAllModels();
		
		Assert.assertNotNull(modelsAfter);
		Assert.assertNotNull(modelsAfter.getModels1());
		Assert.assertNotNull(modelsAfter.getModels2());
		Assert.assertEquals(2, modelsAfter.getModels1().size());
		Assert.assertEquals(3, modelsAfter.getModels2().size());
	}
	
	
}
