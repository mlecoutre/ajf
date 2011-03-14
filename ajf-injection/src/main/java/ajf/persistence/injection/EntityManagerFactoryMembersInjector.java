package ajf.persistence.injection;

import java.lang.reflect.Field;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.EntityManagerProvider;

import com.google.inject.MembersInjector;

public class EntityManagerFactoryMembersInjector<T> implements
		MembersInjector<T> {

	private final static Logger logger = LoggerFactory
			.getLogger(EntityManagerFactoryMembersInjector.class);
	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";

	private final Field field;
	private final String persistenceUnitName;

	public EntityManagerFactoryMembersInjector(Field field) {

		this.field = field;

		// resolve the persistenceunitName
		String puName = null;
		if (field.isAnnotationPresent(PersistenceUnit.class)) {
			PersistenceUnit pUnit = field.getAnnotation(PersistenceUnit.class);
			puName = pUnit.name();
			if ((null == puName) || (0 == puName.length())) {
				puName = pUnit.unitName();
			}
		}

		if (null == puName) puName = DEFAULT_PERSISTENCE_UNIT_NAME;
		this.persistenceUnitName = puName;

		this.field.setAccessible(true);

	}

	public void injectMembers(T t) {
		try {
			EntityManagerFactory em = EntityManagerProvider
					.getEntityManagerFactory(this.persistenceUnitName);
			this.field.set(t, em);
		}
		catch (Throwable e) {
			logger.error(
					"Exception while injecting field : " + this.field.getName(),
					e);
			throw new RuntimeException(e);
		}
	}
}
