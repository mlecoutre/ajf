package ajf.logger.injection;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

public class LoggerMembersInjector<T> implements MembersInjector<T> {
	
	private final static Logger logger = LoggerFactory.getLogger(LoggerMembersInjector.class);

	private final Field field;

	public LoggerMembersInjector(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	public void injectMembers(T t) {
		
		try {
			Object serviceImpl = LoggerFactory.getLogger(t.getClass());
			this.field.set(t, serviceImpl);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
