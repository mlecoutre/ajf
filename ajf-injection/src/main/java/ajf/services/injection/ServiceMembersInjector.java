package ajf.services.injection;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.services.ServiceLocator;

import com.google.inject.MembersInjector;

public class ServiceMembersInjector<T> implements MembersInjector<T> {
	
	private final static Logger logger = LoggerFactory.getLogger(ServiceMembersInjector.class);

	private final Field field;

	public ServiceMembersInjector(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	public void injectMembers(T t) {
		
		try {
			Object serviceImpl = ServiceLocator.getService(field.getType());
			this.field.set(t, serviceImpl);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
