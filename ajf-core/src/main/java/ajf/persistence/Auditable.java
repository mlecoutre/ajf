package ajf.persistence;


public interface Auditable {

	/**
	 * @return the auditData
	 */
	public AuditDataComponent getAuditData();

	/**
	 * @param auditData the auditData to set
	 */
	public void setAuditData(AuditDataComponent auditData);
		
}
