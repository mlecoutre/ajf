package ajf.persistence;


public interface Auditable {

	/**
	 * @return the auditData
	 */
	public AuditData getAuditData();

	/**
	 * @param auditData the auditData to set
	 */
	public void setAuditData(AuditData auditData);
		
}
