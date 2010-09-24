package ajf.persistence.listeners;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import ajf.datas.AuditDataContext;
import ajf.persistence.AuditData;
import ajf.persistence.Auditable;


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
			
			ajf.datas.AuditData audit = AuditDataContext.getAuditData();
			String user = null;
			if (null != audit) {
				user = audit.get(ajf.datas.AuditData.KEY_USERID);				
			}
			if (null == user)
				 user = "Anonymous";
			
			Auditable auditableBean = (Auditable) bean;
			AuditData auditData = auditableBean.getAuditData();
			
			if (null == auditData) {
				// instanciate and attach the AuditData
				auditData = new AuditData();
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
