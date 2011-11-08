package ajf.datas;

public class AuditDataContext {
	
	private static ThreadLocal<AuditData> contextData = new ThreadLocal<AuditData>();
	
	private AuditDataContext() {
		super();
	}

	/**
	 * 
	 * @param auditData
	 */
	public static void initContextData(AuditData auditData) {
		contextData.set(auditData);
	}

	/**
	 * 
	 * @param auditData
	 */
	public static void initContextData() {
		contextData.set(new DefaultAuditData());
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
