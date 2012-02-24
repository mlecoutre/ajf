package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

public class ServiceBeanImpl<T> implements Bean<T> {

	private InjectionTarget<T> it;
	private Class<T> beanClass;
	private Class<?> interfaceClass;
	
	public ServiceBeanImpl(InjectionTarget<T> it, Class<T> beanClass, Class<?> pInterface) {
		this.it = it;
		this.beanClass = beanClass;
		this.interfaceClass = pInterface;
	}
	
	@Override
	public T create(CreationalContext<T> ctx) {
		T instance = it.produce(ctx);
        it.inject(instance, ctx);
        it.postConstruct(instance);
        return instance;
	}

	@Override
	public void destroy(T instance, CreationalContext<T> ctx) {
		it.preDestroy(instance);
        it.dispose(instance);
        ctx.release();
		
	}

	@Override
	public Class<?> getBeanClass() {
		return beanClass;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return it.getInjectionPoints();
	}

	@Override
	public String getName() {		
		return beanClass.getCanonicalName();
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
        types.add(beanClass);
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
