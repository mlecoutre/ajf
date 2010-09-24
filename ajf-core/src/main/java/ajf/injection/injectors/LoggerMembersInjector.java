package ajf.injection.injectors;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

public class LoggerMembersInjector<T> implements MembersInjector<T> {

	private final Field field;

	public LoggerMembersInjector(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	public void injectMembers(T t) {
		
		try {
			Logger logger = LoggerFactory.getLogger(t.getClass());
			field.set(t, logger);
		}
		catch (Throwable e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
}
