package ajf.injection.injectors;

import java.lang.reflect.Field;

import ajf.persistence.DAO;
import ajf.persistence.DAOFactory;

import com.google.inject.MembersInjector;

public class DAOMembersInjector<T> implements MembersInjector<T> {

	private final Field field;

	public DAOMembersInjector(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

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
		try {
			DAO.class.cast(t);
			// t is a DAO, and it's not possible to inject a DAO in a DAO
			throw new RuntimeException(
					"Injection exception while trying to inject "
							+ field.getType().getName() + " in "
							+ t.getClass().getName() + "#" + field.getName());
		}
		catch (ClassCastException e) {
			// Nothing to do, it's normal
		}

		try {
			Object daoImpl = DAOFactory.getDAO(field.getType());
			field.set(t, daoImpl);
		}
		catch (Throwable e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
}
