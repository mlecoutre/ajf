package ajf.persistence.injection;

import java.lang.reflect.Field;

import ajf.services.injection.ServiceMembersInjector;

public class DAOMembersInjector<T> extends ServiceMembersInjector<T> {
	
	public DAOMembersInjector(Field field) {
		super(field);
	}

	/**
	public void injectMembers(T t) {
		
		// is the field type extends DAO
		try {
			field.getType().asSubclass(DAO.class);
		}
		catch (ClassCastException e) {
			// file is not a DAO
			throw new RuntimeException(
					"The field '" + t.getClass().getName() + "#" + field.getName() + "' must be a DAO");
		}
		
		// is not already in a DAO
//		
//		try {
//			DAO.class.cast(t);
//			// t is a DAO, and it's not possible to inject a DAO in a DAO
//			throw new RuntimeException(
//					"Injection exception while trying to inject "
//							+ field.getType().getName() + " in "
//							+ t.getClass().getName() + "#" + field.getName());
//		}
//		catch (ClassCastException e) {
//			// Nothing to do, it's normal
//		}
//		
		
		try {
			Object serviceImpl = ServiceLocator.getService(field.getType());
			this.field.set(t, serviceImpl);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
	**/

}
