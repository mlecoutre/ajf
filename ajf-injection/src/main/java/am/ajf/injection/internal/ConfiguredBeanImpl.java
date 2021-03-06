package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Qualifier;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;

import am.ajf.core.Service;
import am.ajf.core.beans.BeanDefinition;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.injection.annotation.DefaultProfile;

/**
 * @author U002617
 * 
 */
public class ConfiguredBeanImpl<T> implements Bean<T> {

	private final String beanName;
	private final Class<T> beanClass;
	private final boolean isDefaultBean;
	private final BeanDefinition beanDefinition;

	private final AnnotatedType<T> at;
	private final InjectionTarget<T> it;

	private final Logger logger = LoggerFactory
			.getLogger(ConfiguredBeanImpl.class);

	/** Begin : Constructor **/

	public ConfiguredBeanImpl(String beanName, Class<T> beanClass,
			boolean isDefaultBean, BeanDefinition beanDefinition,
			AnnotatedType<T> at, InjectionTarget<T> it) {

		this.beanName = beanName;
		this.beanClass = beanClass;
		this.isDefaultBean = isDefaultBean;
		this.beanDefinition = beanDefinition;

		this.at = at;
		this.it = it;
	}

	/** End : Constructor **/

	/** Begin : Overrides **/

	@SuppressWarnings("unchecked")
	@Override
	public T create(CreationalContext<T> ctx) {

		logger.trace("Creation of Bean from configuration '" + beanName + "'.");

		T instance = null;
		try {
			instance = (T) beanDefinition.getBeanFactory().resolve(
					beanDefinition);
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return instance;
		}

		if (null == instance) {
			instance = it.produce(ctx);
		}
		
		it.inject(instance, ctx);
		if (null != beanDefinition) {
			try {
				beanDefinition.getBeanFactory().initialize(instance,
						beanDefinition);
			} catch (Exception e) {
				logger.error("Unable to initialize bean attributes for bean: "
						+ beanClass.getName());
			}
		}

		it.postConstruct(instance);
		if (instance instanceof Service) {
			((Service) instance).start();
		}

		ctx.push(instance);

		return instance;
	}

	@Override
	public void destroy(T instance, CreationalContext<T> ctx) {

		it.preDestroy(instance);
		if (instance instanceof Service) {
			((Service) instance).stop();
		}
		
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
		return null; // beanClass.getSimpleName();
	}

	@Override
	public Set<Annotation> getQualifiers() {
		Set<Annotation> qualifiers = new HashSet<Annotation>();

		Set<Annotation> annotations = at.getAnnotations();
		if ((null != annotations) && (!annotations.isEmpty())) {
			Iterator<Annotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				Annotation annotation = annotationIterator.next();

				// and @Any are also qualifiers
				if (annotation.annotationType().isAnnotationPresent(
						Qualifier.class)) {
					if (!Default.class.equals(annotation.annotationType())) {
						qualifiers.add(annotation);
					}
					continue;
				}

			}
		}

		// if not default Bean
		if (!isDefaultBean) {
			qualifiers.add(new Alternative() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return Alternative.class;
				}
			});
		}

		qualifiers.add(new Any() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Any.class;
			}
		});

		if (isDefaultBean) {

			// add @DefaultBean
			qualifiers.add(new DefaultProfile() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return DefaultProfile.class;
				}
			});

			// add @Default
			qualifiers.add(new Default() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return Default.class;
				}
			});

		}

		qualifiers.add(new am.ajf.injection.annotation.Profile() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return am.ajf.injection.annotation.Profile.class;
			}

			@Override
			public String value() {
				return beanName;
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

		return ApplicationScoped.class;
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

		List<Class<?>> superClasses = ClassUtils.getAllSuperclasses(beanClass);
		for (Class<?> superClass : superClasses) {
			types.add(superClass);
		}

		types.add(Object.class);

		return types;
	}

	@Override
	public boolean isAlternative() {
		return (!isDefaultBean);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	/** End : Overrides **/

}
