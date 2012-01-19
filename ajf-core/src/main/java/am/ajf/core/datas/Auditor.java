package am.ajf.core.datas;

import java.text.MessageFormat;

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
		auditLogger.info(message);
	}
	
	/**
	 * log audit message at INFO level
	 * @param messagePattern
	 * @param arguments
	 */
	public static void audit(String messagePattern, Object... arguments) {
		if (null == messagePattern)
			return;
		String message = MessageFormat.format(messagePattern, arguments);
		auditLogger.info(message);
	}
	
}
