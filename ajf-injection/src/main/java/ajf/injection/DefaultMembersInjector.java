package ajf.injection;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.DAO;
import ajf.persistence.DAOFactory;
import ajf.services.ServiceFactory;

import com.google.inject.MembersInjector;

public class DefaultMembersInjector<T> implements MembersInjector<T> {
	
	private final static Logger logger = LoggerFactory.getLogger(DefaultMembersInjector.class);

	private final Field field;

	public DefaultMembersInjector(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	public void injectMembers(T t) {
		
		// is the field type extends DAO
		try {
			field.getType().asSubclass(DAO.class);
			Object daoImpl = DAOFactory.getDAO(field.getType());
			this.field.set(t, daoImpl);
		}
		catch (ClassCastException e) {
			// field is not a DAO
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
		
		// is the field a service
		try {
			Object serviceImpl = ServiceFactory.getService(field.getType());
			this.field.set(t, serviceImpl);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
		
		
	}
}
