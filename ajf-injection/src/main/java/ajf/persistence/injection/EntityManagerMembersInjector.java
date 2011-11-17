package ajf.persistence.injection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

public class EntityManagerMembersInjector<T> implements MembersInjector<T> {

	private final static Logger logger = LoggerFactory.getLogger(EntityManagerMembersInjector.class);
	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	private final Field field;
	private final String persistenceUnitName;
	private final Map<String, String> persistencePropertiesMap = new HashMap<String, String>();
	
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
		
			PersistenceProperty properties[] = pCtx.properties();  
			if ((null != properties) && (properties.length > 0)) {
				for (int i = 0; i < properties.length; i++) {
					PersistenceProperty persistenceProperty = properties[i];
					persistencePropertiesMap.put(persistenceProperty.name(), persistenceProperty.value());
				}
			}
			
		}
		if (null == puName) 
			puName = DEFAULT_PERSISTENCE_UNIT_NAME;
		this.persistenceUnitName = puName;
		
		this.field.setAccessible(true);
		
	}

	public void injectMembers(T t) {
		try {
			EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName, persistencePropertiesMap);
			EntityManager em = emFactory.createEntityManager();
			this.field.set(t, em);
		}
		catch (Throwable e) {
			logger.error("Exception while injecting field : " + this.field.getName(), e);
			throw new RuntimeException(e);
		}
	}
}
