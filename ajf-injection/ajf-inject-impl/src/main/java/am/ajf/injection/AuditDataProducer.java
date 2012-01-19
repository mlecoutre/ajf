package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;

public class AuditDataProducer {

	public AuditDataProducer() {
		super();
	}
	
	@Produces
	public AuditData produceAuditData(InjectionPoint ip) {
		AuditData auditData = AuditDataContext.getAuditData();
		return auditData;
	}
	
	/*
	@Produces
	public EditableAuditData produceEditableAuditData(InjectionPoint ip) {
		EditableAuditData auditData = (EditableAuditData) AuditDataContext.getAuditData();
		return auditData;
	}
	*/

}
