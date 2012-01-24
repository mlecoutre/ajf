package am.ajf.core.data;

import static am.ajf.core.datas.Auditor.audit;

import org.junit.Before;
import org.junit.Test;

import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;
import am.ajf.core.datas.EditableAuditData;

public class AuditorTest {

	public AuditorTest() {
		super();
	}
	
	@Before
	public void setUp() {
		EditableAuditData data = (EditableAuditData) AuditDataContext.getAuditData();
		data.put(AuditData.KEY_USERID, "u002617");
	}
	
	@Test
	public void testAudit() {
		audit("get bilan resource");
	}
	
	@Test
	public void testParameterizedMessage() {
		audit("get resource: {0}", "myResource");
	}
	
	
}
