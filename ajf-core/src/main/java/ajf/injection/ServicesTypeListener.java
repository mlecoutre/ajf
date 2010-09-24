package ajf.injection;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ajf.annotations.InjectLogger;
import ajf.injection.injectors.DAOMembersInjector;
import ajf.injection.injectors.EntityManagerMembersInjector;
import ajf.injection.injectors.LoggerMembersInjector;
import ajf.persistence.annotations.InjectDAO;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ServicesTypeListener implements TypeListener {
	
	private static TypeListener instance = new ServicesTypeListener();
	
	private ServicesTypeListener() {
		super();
	}

	/**
	 * 
	 * @return the implementation
	 */
	public static TypeListener getInstance() {
		return instance;
	}

	public <T> void hear(TypeLiteral<T> typeLiteral,
			TypeEncounter<T> typeEncounter) {

		for (Field field : typeLiteral.getRawType().getDeclaredFields()) {

			if (field.getType() == EntityManager.class
					&& field.isAnnotationPresent(PersistenceContext.class)) {
				typeEncounter.register(new EntityManagerMembersInjector<T>(field));
			}
			
			if (field.isAnnotationPresent(InjectDAO.class)) {
				typeEncounter.register(new DAOMembersInjector<T>(field));
			}
			
			if (field.isAnnotationPresent(InjectLogger.class)) {
				typeEncounter.register(new LoggerMembersInjector<T>(field));
			}
			
		}

	}

}
