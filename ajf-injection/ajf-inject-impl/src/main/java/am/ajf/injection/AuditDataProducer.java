package am.ajf.injection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;

//NRE : Changed because the implicit scope is @Dependent on the produce
//method, the scope of the producer object dont need to be request scoped.
@ApplicationScoped 
public class AuditDataProducer {
	
	public AuditDataProducer() {
		super();
	}
	
	@Produces
	public AuditData produceAuditData() {
		AuditData auditData = AuditDataContext.getAuditData();
		return auditData;
	}
	

}
