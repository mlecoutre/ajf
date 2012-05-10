package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Local;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

public class EjbBeanImpl<T> implements Bean<T> {

	private static final Logger logger = LoggerFactory.getLogger(EjbBeanImpl.class);
	
	private InjectionTarget<T> it;	
	private Class<?> interfaceClass;	
	
	public EjbBeanImpl(InjectionTarget<T> it, Class<?> beanClass) {
		this.it = it;
		
		Class<?>[] interfaces = beanClass.getInterfaces();
		for (Class<?> in : interfaces) {
			if (in.isAnnotationPresent(Local.class)) {
				this.interfaceClass = in;
			}
		}				
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T create(CreationalContext<T> ctx) {		
		try {			
			InitialContext ic = new InitialContext();
			//This is WAS7 specifics TODO make it better !
			T instance = (T) ic.lookup("ejblocal:"+interfaceClass.getName());
			it.inject(instance, ctx);
	        it.postConstruct(instance);
	        return instance;
		} catch (NamingException e) {
			logger.error("Error looking up the EJB : "+interfaceClass.getName(), e);			
		}
		
        return null;        
	}

	@Override
	public void destroy(T instance, CreationalContext<T> ctx) {
		it.preDestroy(instance);
        it.dispose(instance);
        ctx.release();
		
	}

	@Override
	public Class<?> getBeanClass() {
		return interfaceClass;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return it.getInjectionPoints();
	}

	@Override
	public String getName() {		
		return interfaceClass.getCanonicalName();
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Annotation> getQualifiers() {
		Set<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add( new AnnotationLiteral<Default>() {} );
        qualifiers.add( new AnnotationLiteral<Any>() {} );
        return qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {		
		return ApplicationScoped.class;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<Type>();
        types.add(interfaceClass);
        types.add(Object.class);
        return types;
	}

	@Override
	//TODO Some implementations CAN be alternatives
	public boolean isAlternative() {		
		return false;
	}

	@Override
	public boolean isNullable() {		
		return false;
	}
	
	

}
