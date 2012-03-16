package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Qualifier;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;

import am.ajf.core.beans.BeansManager;
import am.ajf.core.beans.LifecycleAware;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.injection.annotation.DefaultBean;

public class BeanImpl<T> implements Bean<T> {

	private final Class<T> beanClass;
	private final Class<T> beanInterface;

	private final AnnotatedType<T> at;
	private final InjectionTarget<T> it;

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory
			.getLogger(ConfiguredBeanImpl.class);

	/** Begin : Constructor **/

	public BeanImpl(Class<T> beanClass, Class<T> beanInterface,
			AnnotatedType<T> at, InjectionTarget<T> it) {

		this.beanClass = beanClass;
		this.beanInterface = beanInterface;

		this.at = at;
		this.it = it;
	}

	/** End : Constructor **/

	/** Begin : Overrides **/

	@Override
	public T create(CreationalContext<T> ctx) {

		T instance = it.produce(ctx);
		it.inject(instance, ctx);

		it.postConstruct(instance);

		if (instance instanceof LifecycleAware) {
			((LifecycleAware) instance).start();
		}

		ctx.push(instance);

		return instance;
	}

	@Override
	public void destroy(T instance, CreationalContext<T> ctx) {

		if (instance instanceof LifecycleAware) {
			((LifecycleAware) instance).stop();
		}

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
		return null;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		Set<Annotation> qualifiers = new HashSet<Annotation>();
		
		Set<Annotation> annotations = at.getAnnotations();
		if ((null != annotations) && (!annotations.isEmpty())) {
			Iterator<Annotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				
				Annotation annotation = annotationIterator.next();
				if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
					qualifiers.add(annotation);
					continue;
				}

			}
		}
				
		qualifiers.add(new Any() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Any.class;
			}
		});
		
		// add @DefaultBean
		qualifiers.add(new DefaultBean() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return DefaultBean.class;
			}
		});
		
		qualifiers.add(new am.ajf.injection.annotation.Bean() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return am.ajf.injection.annotation.Bean.class;
			}
			
			@Override
			public String value() {
				return BeansManager.DEFAULT_BEAN_NAME;
			}
		});
		
		// add @Default
		qualifiers.add(new Default() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Default.class;
			}
		});		
		
		return qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {

		Set<Annotation> annotations = at.getAnnotations();
		if ((null != annotations) && (!annotations.isEmpty())) {
			Iterator<Annotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				Annotation annotation = annotationIterator.next();
				if (annotation.annotationType().isAnnotationPresent(
						NormalScope.class)) {
					return annotation.annotationType();
				}
			}
		}

		return Dependent.class;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<Type>();
		types.add(beanClass);

		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(beanClass);
		for (Class<?> interf : interfaces) {
			types.add(interf);
		}

		/*
		List<Class<?>> superClasses = ClassUtils.getAllSuperclasses(beanClass);
		for (Class<?> superClass : superClasses) {
			types.add(superClass);
		}
		*/

		types.remove(beanInterface);
		List<Class<?>> excludedInterfaces = ClassUtils
				.getAllInterfaces(beanInterface);
		for (Class<?> interf : excludedInterfaces) {
			types.remove(interf);
		}

		types.add(Object.class);

		return types;
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	/** End : Overrides **/

}
