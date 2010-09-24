package ajf.injection.injectors;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import com.google.inject.MembersInjector;

public class EntityManagerMembersInjector<T> implements MembersInjector<T> {

	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	private final Field field;
	private final String persistenceUnitName;

	public EntityManagerMembersInjector(Field field) {
		this.field = field;
		
		PersistenceContext pCtx = field.getAnnotation(PersistenceContext.class);
		
		String puName = pCtx.unitName();
		if (null == puName) puName = DEFAULT_PERSISTENCE_UNIT_NAME;
		this.persistenceUnitName = puName;
		
		field.setAccessible(true);
	}

	public void injectMembers(T t) {
		try {
			EntityManagerFactory emFactory = Persistence
					.createEntityManagerFactory(persistenceUnitName);
			EntityManager em = emFactory.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			field.set(t, em);
		}
		catch (Throwable e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
}
