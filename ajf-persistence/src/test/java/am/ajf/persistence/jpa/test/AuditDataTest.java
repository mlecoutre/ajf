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

import am.ajf.injection.UserTransactionProducer;
import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.api.CrudServiceBD;
import am.ajf.persistence.jpa.impl.CrudProvider;
import am.ajf.persistence.jpa.impl.DummyCrudService;
import am.ajf.persistence.jpa.test.harness.ModelAudit;
import am.ajf.persistence.jpa.test.helper.DBHelper;

@RunWith(Arquillian.class)
public class AuditDataTest {
	
	@Inject
	private CrudServiceBD<ModelAudit, Long> service;	
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(EntityManagerProvider.class)	
				.addClasses(CrudServiceBD.class)
				.addClasses(DummyCrudService.class)
				.addClasses(CrudProvider.class)
				.addClasses(UserTransactionProducer.class)
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
		DBHelper.executeSQLInTransaction("default", "DELETE FROM ModelAudit");
	}

	@Test
	public void testAddingDatas() {
		service.save(new ModelAudit("nicolas"));
		List<ModelAudit> res = service.find(ModelAudit.FIND_BY_NAME, "nicolas");
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());
		
		ModelAudit data = res.get(0);
		Assert.assertEquals("A", data.getAuditData().getRecordStatus());
	}
	
	@Test
	public void testUpdatingDatas() {
		//adding a data
		service.save(new ModelAudit("nicolas"));		
		List<ModelAudit> res = service.find(ModelAudit.FIND_BY_NAME, "nicolas");		
		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());
		
		//modifing the added data
		ModelAudit data = res.get(0);
		data.setName("vincent");
		service.save(data);
		
		res = service.find(ModelAudit.FIND_BY_NAME, "vincent");
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());
		data = res.get(0);
		
		Assert.assertEquals(2, data.getAuditData().getRecordVersion());				
	}
	
}
