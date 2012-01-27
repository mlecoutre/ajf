package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javassist.Modifier;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> annotatedType;
	private final Collection<Method> serviceOperations;
	private Class<? extends Annotation> annotationClass;
	private Annotation annotationInstance;

	public AnnotatedTypeWrapper(AnnotatedType<T> annotatedType,
			Collection<Method> serviceOperations,
			Class<? extends Annotation> annotationClass,
			Annotation annotationInstance) {
		this.annotatedType = annotatedType;
		this.serviceOperations = serviceOperations;
		this.annotationClass = annotationClass;
		this.annotationInstance = annotationInstance;
	}

	@Override
	public Type getBaseType() {
		return annotatedType.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return annotatedType.getTypeClosure();
	}

	@Override
	@SuppressWarnings("hiding")
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return annotatedType.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return annotatedType.getAnnotations();
	}

	@Override
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationType) {
		return annotatedType.isAnnotationPresent(annotationType);
	}

	@Override
	public Class<T> getJavaClass() {
		return annotatedType.getJavaClass();
	}

	@Override
	public Set<AnnotatedConstructor<T>> getConstructors() {
		return annotatedType.getConstructors();
	}

	@Override
	public Set<AnnotatedField<? super T>> getFields() {
		return annotatedType.getFields();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<AnnotatedMethod<? super T>> getMethods() {

		Set<AnnotatedMethod<? super T>> result = new HashSet<AnnotatedMethod<? super T>>();

		for (AnnotatedMethod<? super T> annotatedMethod : annotatedType
				.getMethods()) {

			AnnotatedMethod<T> annotMethod = (AnnotatedMethod<T>) annotatedMethod;

			Method javaMethod = annotatedMethod.getJavaMember();
			String javaMethodParams = Arrays.toString(javaMethod
					.getParameterTypes());

			if (Modifier.isPublic(javaMethod.getModifiers())) {

				AnnotatedMethod<? super T> wrappedAnnotatedMethod = null;

				for (Method svcMethod : serviceOperations) {
					if (javaMethod.getName().equals(svcMethod.getName())) {
						String svcMethodParams = Arrays.toString(svcMethod
								.getParameterTypes());
						if (javaMethodParams.equals(svcMethodParams)) {

							wrappedAnnotatedMethod = new AnnotatedMethodWrapper<T>(
									annotMethod,
									annotationClass, annotationInstance);

						}
					}
				}

				if (null == wrappedAnnotatedMethod) {
					result.add(annotMethod);
				}
				else {
					result.add(wrappedAnnotatedMethod);
				}

			}

		}

		return result;
	}
	
}
