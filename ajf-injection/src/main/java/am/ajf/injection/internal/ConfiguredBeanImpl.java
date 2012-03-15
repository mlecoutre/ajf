package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Qualifier;

import org.slf4j.Logger;

import am.ajf.core.beans.BeansManager;
import am.ajf.core.beans.ExtendedBeanDeclaration;
import am.ajf.core.logger.LoggerFactory;

public class ConfiguredBeanImpl<T> implements Bean<T> {

	//private final BeanFactory beanFactory;
	
	private final AnnotatedType<T> at;
	private final InjectionTarget<T> it;
	private final Class<?> parentClass;
	private final Class<T> beanClass;
	private final Class<?> interfaceClass;
	private final String beanName;
	private final boolean isDefault;
	private final ExtendedBeanDeclaration beanDeclaration;

	private final Logger logger = LoggerFactory.getLogger(ConfiguredBeanImpl.class);
	
	/** Begin : Constructor **/
		
	public ConfiguredBeanImpl(ExtendedBeanDeclaration beanDeclaration, 
			String beanName, boolean isDefault, 
			Class<?> parentClass, Class<T> beanClass, Class<?> pInterface,
			AnnotatedType<T> at, InjectionTarget<T> it) {
		
		this.beanDeclaration = beanDeclaration;
		this.beanName = beanName;
		this.isDefault = isDefault;
		this.parentClass = parentClass;
		this.beanClass = beanClass;
		this.interfaceClass = pInterface;
		this.at = at;
		this.it = it;
				
	}
	
	/** End : Constructor **/
	
	/** Begin : Overrides **/
	
	@Override
	public T create(CreationalContext<T> ctx) {
		
		logger.trace("Creation of Bean from configuration '"+beanName+"'.");
		
		T instance = it.produce(ctx);
        it.inject(instance, ctx);
        
        try {
        	BeansManager.initBean(instance, beanDeclaration);
		} catch (Exception e) {
			logger.error("Unable to initialize bean attributes for bean: " + beanClass.getName());
		}
        
        it.postConstruct(instance);
        
        ctx.push(instance);
        
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
		return null; //beanClass.getSimpleName();
	}
	
	@Override
	public Set<Annotation> getQualifiers() {
		Set<Annotation> qualifiers = new HashSet<Annotation>();
		
		Set<Annotation> annotations = at.getAnnotations();
		if ((null != annotations) && (!annotations.isEmpty())) {
			Iterator<Annotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				Annotation annotation = annotationIterator.next();
				
				// @Default and @Any are also qualifiers
				if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
					qualifiers.add(annotation);
					continue;
				}
				
				if (annotation.annotationType().equals(Alternative.class)) {
					qualifiers.add(annotation);
					continue;
				}	
				
			}
		}
		
		return qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		
		Set<Annotation> annotations = at.getAnnotations();
		if ((null != annotations) && (!annotations.isEmpty())) {
			Iterator<Annotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				Annotation annotation = annotationIterator.next();
				if (annotation.annotationType().isAnnotationPresent(NormalScope.class)) {
					return annotation.annotationType();
				}
			}
		}
		
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
        types.add(parentClass);
        types.add(interfaceClass);
        types.add(Object.class);
        //TODO: could add all the interfaces
        return types;
	}

	@Override
	public boolean isAlternative() {		
		return (!isDefault);
	}

	@Override
	public boolean isNullable() {		
		return false;
	}	
	
	/** End : Overrides **/

}
