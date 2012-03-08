package am.ajf.core.datas;


import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

/**
 * Class to write Audit informations
 * @author vincent
 */
public class Auditor {
	
	private final static Logger auditLogger = LoggerFactory.getLogger("audit");
	
	private Auditor() {
		super();
	}

	/**
	 * log audit message at INFO level
	 * @param message
	 */
	public static void audit(String message) {
		if (null == message)
			return;
		auditLogger.trace(message);
	}
	
	/**
	 * log audit message at INFO level
	 * @param messageFormat as String.format
	 * @param args the messageFormat arguments
	 */
	public static void audit(String messageFormat, Object... args) {
		if (null == messageFormat)
			return;
		String message = String.format(messageFormat, args);
		auditLogger.trace(message);
	}
	
}
