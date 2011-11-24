package ajf.injection;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;

import ajf.logger.injection.InjectLogger;
import ajf.logger.injection.LoggerMembersInjector;
import ajf.persistence.injection.DAOMembersInjector;
import ajf.persistence.injection.EntityManagerFactoryMembersInjector;
import ajf.persistence.injection.EntityManagerMembersInjector;
import ajf.persistence.injection.InjectDAO;
import ajf.services.injection.InjectService;
import ajf.services.injection.ServiceMembersInjector;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

@SuppressWarnings("deprecation")
public class InjectionTypeListener implements TypeListener {
	
	public InjectionTypeListener() {
		super();
	}
	
	public <T> void hear(TypeLiteral<T> typeLiteral,
			TypeEncounter<T> typeEncounter) {

		for (Field field : typeLiteral.getRawType().getDeclaredFields()) {

			if (field.getType() == Logger.class
					&& field.isAnnotationPresent(InjectLogger.class)) {
				typeEncounter.register(new LoggerMembersInjector<T>(field));
			}
			
			if (field.isAnnotationPresent(InjectService.class)) {
				typeEncounter.register(new ServiceMembersInjector<T>(field));
			}
			
			if (field.getType() == EntityManager.class
					&& field.isAnnotationPresent(PersistenceContext.class)) {					
				typeEncounter.register(new EntityManagerMembersInjector<T>(field));
			}
			
			if (field.getType() == EntityManagerFactory.class
					&& field.isAnnotationPresent(PersistenceUnit.class)) {
				typeEncounter.register(new EntityManagerFactoryMembersInjector<T>(field));
			}
			
			if (field.isAnnotationPresent(InjectDAO.class)) {
				typeEncounter.register(new DAOMembersInjector<T>(field));
			}
						
		}

	}

}
