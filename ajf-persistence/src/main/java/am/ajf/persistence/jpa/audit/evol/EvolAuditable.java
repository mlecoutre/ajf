package am.ajf.persistence.jpa.audit.evol;


public interface EvolAuditable {

	/**
	 * @return the auditData
	 */
	public EvolAuditDataComponent getAuditData();

	/**
	 * @param auditData the auditData to set
	 */
	public void setAuditData(EvolAuditDataComponent auditData);
		
}
