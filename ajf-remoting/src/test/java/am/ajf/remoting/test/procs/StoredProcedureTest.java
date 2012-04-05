package am.ajf.remoting.test.procs;

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

import am.ajf.remoting.procs.impl.StoredProcedureHelper;
import am.ajf.remoting.procs.impl.StoredProcedureImplHandler;
import am.ajf.remoting.test.procs.harness.FindModelResult;
import am.ajf.remoting.test.procs.harness.ModelSp;
import am.ajf.remoting.test.procs.harness.StoredProcedureNoImplServiceBD;
import am.ajf.remoting.test.procs.helper.DBHelper;

@RunWith(Arquillian.class)
public class StoredProcedureTest {
	
	@Inject
	private StoredProcedureNoImplServiceBD storedProcedureNoImpl;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(StoredProcedureNoImplServiceBD.class)
				.addClasses(StoredProcedureImplHandler.class)
				.addClasses(StoredProcedureHelper.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}
	
	@Before
	public void setUp() throws Exception {		
		DBHelper.setupDataSources();		
		DBHelper.executeSQLInTransaction("create table model (id INT, name VARCHAR(50));");
		DBHelper.executeSQLInTransaction(
				"insert into model values (1, 'nicolas');",
				"insert into model values (2, 'vincent');",
				"insert into model values (3, 'matthieu');"
		);
		DBHelper.executeSQLInTransaction(
				"CREATE PROCEDURE ZZTESTNOPARAM()\n"+
						 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
					     " BEGIN ATOMIC\n"+
						 "  DECLARE result CURSOR FOR SELECT id, name FROM model;\n"+
					     "  OPEN result;\n"+
						 " END",
				 "CREATE PROCEDURE ZZTESTWITHPARAM(IN PNAME VARCHAR(50))\n"+
						 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
						 " BEGIN ATOMIC\n"+
						 "  DECLARE result CURSOR FOR SELECT id, name FROM model m WHERE m.name = pName;\n"+
						 "   OPEN result;\n"+
						 " END",
				 "CREATE PROCEDURE ZZTESTONEWITHPARAM(IN PNAME VARCHAR(50))\n"+
						 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
						 " BEGIN ATOMIC\n"+
					 	 "  DECLARE result CURSOR FOR SELECT id, name FROM model m WHERE m.name = pName;\n"+
					 	 "  OPEN result;\n"+
						 " END",
				 "CREATE PROCEDURE ZZTESTOUTWITHRESULT(IN PNAME VARCHAR(50), OUT STATUS VARCHAR(50))\n"+
						 " MODIFIES SQL DATA DYNAMIC RESULT SETS 1\n"+
						 " BEGIN ATOMIC\n"+
					 	 "  DECLARE result CURSOR FOR SELECT id, name FROM model m WHERE m.name = pName;\n"+
					 	 "  OPEN result;\n"+
						 "  SET STATUS = 'OK';"+					 	 
						 " END"
		);                 		        
	}

	@After
	public void tearDown() throws Exception {		
		DBHelper.executeSQLInTransaction(
				"DROP PROCEDURE IF EXISTS ZZTESTNOPARAM",
				"DROP PROCEDURE IF EXISTS ZZTESTONEWITHPARAM",
				"DROP PROCEDURE IF EXISTS ZZTESTWITHPARAM",
				"DROP PROCEDURE IF EXISTS ZZTESTOUTWITHRESULT"
		);
		DBHelper.executeSQLInTransaction("drop table model");
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
		@SuppressWarnings("unchecked") List<ModelSp> res = (List<ModelSp>) obj;
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());		
	}	
	
	@Test
	public void testStoredProcedureOneResultsOneArgsWithNoImpl() {	        
		ModelSp res = storedProcedureNoImpl.findOneModelByName("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals("nicolas", res.getName());		
	}	
	
	@Test
	public void testStoredProcedureOutParamsWithResultSet() {	        
		FindModelResult res = storedProcedureNoImpl.findModelByNameWithStatus("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals("OK", res.getStatus());
		Assert.assertNotNull(res.getResult());
		Assert.assertEquals(1, res.getResult().size());
		Assert.assertEquals("nicolas", res.getResult().get(0).getName());		
	}
	
}
