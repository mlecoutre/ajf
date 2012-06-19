package am.ajf.forge.helpers;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocitySlf4JLogSystem implements LogChute {

	private final static Logger logger = LoggerFactory
			.getLogger(VelocitySlf4JLogSystem.class);

	@Override
	public void init(RuntimeServices rs) throws Exception {
		// Nothing to do
	}

	@Override
	public void log(int level, String message) {
		switch (level) {
		case LogChute.WARN_ID:
			logger.warn(message);
			break;
		case LogChute.INFO_ID:
			logger.info(message);
			break;
		case LogChute.TRACE_ID:
			logger.trace(message);
			break;
		case LogChute.ERROR_ID:
			logger.error(message);
			break;
		case LogChute.DEBUG_ID:
		default:
			logger.debug(message);
			break;
		}
	}

	@Override
	public void log(int level, String message, Throwable t) {
		switch (level) {
		case LogChute.WARN_ID:
			logger.warn(message, t);
			break;
		case LogChute.INFO_ID:
			logger.info(message, t);
			break;
		case LogChute.TRACE_ID:
			logger.trace(message, t);
			break;
		case LogChute.ERROR_ID:
			logger.error(message, t);
			break;
		case LogChute.DEBUG_ID:
		default:
			logger.debug(message, t);
			break;
		}
	}

	@Override
	public boolean isLevelEnabled(int level) {
		switch (level) {
		case LogChute.DEBUG_ID:
			return logger.isDebugEnabled();
		case LogChute.INFO_ID:
			return logger.isInfoEnabled();
		case LogChute.TRACE_ID:
			return logger.isTraceEnabled();
		case LogChute.WARN_ID:
			return logger.isWarnEnabled();
		case LogChute.ERROR_ID:
			return logger.isErrorEnabled();
		default:
			return true;
		}
	}

}
