package ajf.injection;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import ajf.persistence.injection.DAOMembersInjector;
import ajf.persistence.injection.EntityManagerMembersInjector;
import ajf.persistence.injection.InjectDAO;
import ajf.services.injection.InjectService;
import ajf.services.injection.ServiceMembersInjector;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class InjectionTypeListener implements TypeListener {
	
	private static TypeListener instance = new InjectionTypeListener();
	
	private InjectionTypeListener() {
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
					&& (field.isAnnotationPresent(PersistenceContext.class)
					|| field.isAnnotationPresent(PersistenceUnit.class))) {
				typeEncounter.register(new EntityManagerMembersInjector<T>(field));
			}
			
			if (field.isAnnotationPresent(InjectDAO.class)) {
				typeEncounter.register(new DAOMembersInjector<T>(field));
			}

			if (field.isAnnotationPresent(InjectService.class)) {
				typeEncounter.register(new ServiceMembersInjector<T>(field));
			}
			
			
		}

	}

}
