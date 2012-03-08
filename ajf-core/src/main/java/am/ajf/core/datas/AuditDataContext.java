package am.ajf.core.datas;

public class AuditDataContext {
	
	private static ThreadLocal<AuditData> contextData = new ThreadLocal<AuditData>();
	
	private AuditDataContext() {
		super();
	}

	/**
	 * 
	 * @param auditData
	 */
	public static AuditData initContextData(AuditData auditData) {
		contextData.set(auditData);
		return auditData;
	}

	/**
	 * 
	 * @param auditData
	 */
	public static AuditData initContextData() {
		return initContextData(new DefaultAuditData());
	}

	
	/**
	 * 
	 * @return
	 */
	public static AuditData getAuditData() {
		AuditData auditData = (AuditData) contextData.get();
		if (null == auditData)
			initContextData();
		return (AuditData) contextData.get();
	}
	
}
