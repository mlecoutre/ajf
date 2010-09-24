package ajf.datas;

public abstract class AuditDataContext {

	private static ThreadLocal<AuditData> contextData = new ThreadLocal<AuditData>();
	
	/**
	 * 
	 * @param auditData
	 */
	public static void initContextData(AuditData auditData) {
		contextData.set(auditData);
	}

	/**
	 * 
	 * @return
	 */
	public static AuditData getAuditData() {
		return (AuditData) contextData.get();
	}
	
}
