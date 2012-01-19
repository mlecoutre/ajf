package am.ajf.persistence.listeners;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;
import am.ajf.persistence.AuditDataComponent;
import am.ajf.persistence.Auditable;


public class AuditDataListener {

	/**
	 * 
	 */
	public AuditDataListener() {
		super();
	}

	@PrePersist
	public void setAuditData(Object bean) {
		processAuditData(bean);
	}
	
	@PreUpdate
	public void updateAuditData(Object bean) {
		processAuditData(bean);		
	}
	
	/**
	 * process the entity audit data
	 * @param bean
	 */
	private void processAuditData(Object bean) {
		
		if (bean instanceof Auditable) {
			
			AuditData audit = AuditDataContext.getAuditData();
			String user = null;
			if (null != audit) {
				user = String.valueOf(audit.get(AuditData.KEY_USERID));				
			}
			if (null == user)
				 user = "Anonymous";
			
			Auditable auditableBean = (Auditable) bean;
			AuditDataComponent auditData = auditableBean.getAuditData();
			
			if (null == auditData) {
				// instanciate and attach the AuditData
				auditData = new AuditDataComponent();
				auditableBean.setAuditData(auditData);
				// initialize the AuditData
				auditData.setCreateDate(new Date());
				auditData.setCreateUser(user);
				auditData.setUpdateDate(null);
				auditData.setUpdateUser(null);
			}
			else {
				auditData.setUpdateDate(new Date());
				auditData.setUpdateUser(user);
			}
		}
	}
}
