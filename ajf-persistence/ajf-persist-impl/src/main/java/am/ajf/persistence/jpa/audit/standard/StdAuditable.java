package am.ajf.persistence.jpa.audit.standard;


public interface StdAuditable {

	/**
	 * @return the auditData
	 */
	public StdAuditDataComponent getAuditData();

	/**
	 * @param auditData the auditData to set
	 */
	public void setAuditData(StdAuditDataComponent auditData);
		
}
