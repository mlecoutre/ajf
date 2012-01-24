package am.ajf.core.datas;

import static am.ajf.core.utils.StringUtils.buildString;

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
	 * @param messagePattern
	 * @param arguments
	 */
	public static void audit(String messagePattern, Object... arguments) {
		String message = buildString(messagePattern, arguments);
		if (null == message)
			return;
		auditLogger.trace(message);
	}
	
}
