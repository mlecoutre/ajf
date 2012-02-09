package am.ajf.persistence.jpa.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;

/**
 * <p>
 * Bean for creating and disposing of EntityManager.
 * This is what makes an EntityManager injectable with @Inject
 * The format for injecting an EntityManager is :
 * </p>
 * <code>@Inject @PersistenceUnit(<name>) EntityManager</code>
 * <p>or</p>
 * <code>@Inject EntityManager</code>
 * 
 * <p>It is request scoped.</p>
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class EntityManagerBean implements Bean<EntityManager> {

	private static final Logger logger = LoggerFactory.getLogger(EntityManagerBean.class);
	
	//private InjectionTarget<EntityManager> it;
	private String persistenceUnit;
	
	public EntityManagerBean(String persistenceUnit/*, InjectionTarget<EntityManager> it*/) {
		//this.it = it;
		this.persistenceUnit = persistenceUnit;
	}	
	
	@Override
	public EntityManager create(CreationalContext<EntityManager> ctx) {
		logger.trace("Creation of EntityManager ("+persistenceUnit+")) : Thread ("+Thread.currentThread().getId()+")");
		EntityManager em = EntityManagerProvider.createEntityManager(persistenceUnit);
		ctx.push(em);
        //it.inject(em, ctx);
        //it.postConstruct(em);
        return em;
	}

	@Override
	public void destroy(EntityManager em, CreationalContext<EntityManager> ctx) {
		//This line is why this class exist on the first place
		//The EntityManager will be close then disposed when the Request is finished
		em.close();
		
		//it.preDestroy(em);
        //it.dispose(em);
        ctx.release();        
        logger.trace("Disposed of EntityManager ("+persistenceUnit+")) : Thread ("+Thread.currentThread().getId()+")");
	}

	@Override
	public Class<EntityManager> getBeanClass() {
		return EntityManager.class;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
		//return it.getInjectionPoints();
	}

	@Override
	public String getName() {
		if (persistenceUnit != null) {
			return "EntityManager-"+persistenceUnit;
		} else {
			return "EntityManager";
		}
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Annotation> getQualifiers() {
		Set<Annotation> qualifiers = new HashSet<Annotation>();
		if (EntityManagerProvider.getDefaultPersistenceUnitName().equals(persistenceUnit)) {
			qualifiers.add( new AnnotationLiteral<Default>() {} );
		}
        qualifiers.add( new AnnotationLiteral<Any>() {} );
        
        //This test handle the default persistence unit case
        if (persistenceUnit != null) {
	        qualifiers.add( new PersistenceUnit() {
	
				@Override
				public Class<? extends Annotation> annotationType() {				
					return PersistenceUnit.class;
				}
	
				@Override
				public String value() {				
					return persistenceUnit;
				}			
			});
        }
        return qualifiers;
	}

	/**
	 * The EntityManager injected is RequestScoped !
	 */
	@Override
	public Class<? extends Annotation> getScope() {
		return RequestScoped.class;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<Type>();
        types.add(EntityManager.class);        
        types.add(Object.class);
        return types;
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	
	
}
