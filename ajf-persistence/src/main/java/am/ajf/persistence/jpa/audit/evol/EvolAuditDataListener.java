package am.ajf.persistence.jpa.audit.evol;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import am.ajf.core.ApplicationContext;
import am.ajf.core.datas.AuditData;
import am.ajf.core.datas.AuditDataContext;


public class EvolAuditDataListener {

	/**
	 * 
	 */
	public EvolAuditDataListener() {
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
		
		if (bean instanceof EvolAuditable) {
			
			AuditData audit = AuditDataContext.getAuditData();
			String user = null;
			if (null != audit) {
				user = String.valueOf(audit.get(AuditData.KEY_USERID));				
			}
			if (null == user)
				 user = "Anonymous";
			
			EvolAuditable auditableBean = (EvolAuditable) bean;
			EvolAuditDataComponent auditData = auditableBean.getAuditData();
			String applicationName = ApplicationContext.getApplicationName();			
			if (applicationName == null ) {
				applicationName = "web";
			} else {
				if (applicationName.length() > 8) {
					applicationName = applicationName.substring(0,7);
				}
			}
			
			if (null == auditData) {
				// instanciate and attach the AuditData
				auditData = new EvolAuditDataComponent();
				auditableBean.setAuditData(auditData);
				// initialize the AuditData
				auditData.setCreateDate(new Date());
				auditData.setCreateUser(user);
				auditData.setUpdateDate(null);
				auditData.setUpdateUser(null);
				auditData.setRecordStatus("A");				
				auditData.setCreationFct(applicationName);
				auditData.setRecordVersion(1);
			}
			else {
				auditData.setUpdateDate(new Date());
				auditData.setUpdateUser(user);
				auditData.setUpdateFct(applicationName);
				auditData.setRecordVersion(auditData.getRecordVersion()+1);
			}
		}
	}
}
