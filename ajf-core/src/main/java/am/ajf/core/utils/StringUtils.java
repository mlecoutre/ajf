package am.ajf.core.utils;

import java.text.MessageFormat;

public class StringUtils {

	private StringUtils() {
		super();
	}
	
	/**
	 * 
	 * @param messagePattern
	 * @param arguments
	 * @return a formatted string
	 */
	public static String format(String messagePattern, Object... arguments) {
		if (null == messagePattern)
			return null;
		String message = MessageFormat.format(messagePattern, arguments);
		return message;
	}

}
