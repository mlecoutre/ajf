package ajf.persistence.injection;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

public class EntityManagerMembersInjector<T> implements MembersInjector<T> {

	private final static Logger logger = LoggerFactory.getLogger(EntityManagerMembersInjector.class);
	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	private final Field field;
	private final String persistenceUnitName;

	public EntityManagerMembersInjector(Field field) {
		
		this.field = field;
		
		// resolve the persistenceunitName
		String puName = null;
		if (field.isAnnotationPresent(PersistenceContext.class)) {
			PersistenceContext pCtx = field.getAnnotation(PersistenceContext.class);
			puName= pCtx.name();
			if ((null == puName) || (0 == puName.length())) {
				puName = pCtx.unitName();
			}
		}
		if (null == puName) 
			puName = DEFAULT_PERSISTENCE_UNIT_NAME;
		this.persistenceUnitName = puName;
		
		this.field.setAccessible(true);
		
	}

	public void injectMembers(T t) {
		try {
			EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
			EntityManager em = emFactory.createEntityManager();
			this.field.set(t, em);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
