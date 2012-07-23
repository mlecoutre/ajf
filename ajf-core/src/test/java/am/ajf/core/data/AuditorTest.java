package am.ajf.core.data;

import static am.ajf.core.datas.Auditor.audit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;
import am.ajf.core.datas.EditableAuditData;

public class AuditorTest {

	public AuditorTest() {
		super();
	}
	
	@BeforeClass
	public static void setUpClass() {
		// Nothing to do
	}
	
	@Before
	public void setUp() {
		EditableAuditData data = (EditableAuditData) AuditDataContext.initContextData();
		data.put(AuditData.KEY_USERID, "u002617");
	}
	
	@After
	public void tearDown() {
		// Nothing to do
	}
	
	@Test
	public void testAudit() {
		audit("get bilan resource");
	}
	
	@Test
	public void testParameterizedMessage() {
		audit("get resource: %s", "myResource");
	}
	
}
