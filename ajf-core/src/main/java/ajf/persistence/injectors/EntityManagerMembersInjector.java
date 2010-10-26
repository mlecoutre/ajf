package ajf.persistence.injectors;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.EntityManagerProvider;

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
			if (null == puName) {
				puName = pCtx.unitName();
			}
		}
		
		if (null == puName) {
			if (field.isAnnotationPresent(PersistenceUnit.class)) {
				PersistenceUnit pUnit = field.getAnnotation(PersistenceUnit.class);
				puName= pUnit.name();
				if (null == puName) {
					puName = pUnit.unitName();
				}
			}
		}
		
		if (null == puName) 
			puName = DEFAULT_PERSISTENCE_UNIT_NAME;
		this.persistenceUnitName = puName;
		
		field.setAccessible(true);
		
	}

	public void injectMembers(T t) {
		try {
			EntityManager em = EntityManagerProvider.getEntityManager(this.persistenceUnitName);
			this.field.set(t, em);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
