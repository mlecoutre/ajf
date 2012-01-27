package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMethodWrapper<T> implements AnnotatedMethod<T> {

	private final AnnotatedMethod<T> annotMethod;
	
	private final Class<? extends Annotation> annotationClassToAdd;
	private final Annotation annotationInstanceToAdd;
	
	public AnnotatedMethodWrapper(AnnotatedMethod<T> annotMethod,
			Class<? extends Annotation> annotationClassToAdd,
			Annotation annotationInstanceToAdd) {
		this.annotMethod = annotMethod;
		
		this.annotationClassToAdd = annotationClassToAdd;
		this.annotationInstanceToAdd = annotationInstanceToAdd;
	}

	@Override
	public List<AnnotatedParameter<T>> getParameters() {
		return annotMethod.getParameters();
	}

	@Override
	public boolean isStatic() {
		return annotMethod.isStatic();
	}

	@Override
	public AnnotatedType<T> getDeclaringType() {
		return annotMethod.getDeclaringType();
	}

	@Override
	public Type getBaseType() {
		return annotMethod.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return annotMethod.getTypeClosure();
	}

	@Override
	public Method getJavaMember() {
		return annotMethod.getJavaMember();
	}

	@Override
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {

		if (annotationType.equals(annotationClassToAdd)) {
			return (T) annotationInstanceToAdd;
		}

		return annotMethod.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {

		Set<Annotation> result = new HashSet<Annotation>(
				annotMethod.getAnnotations());
		result.add(annotationInstanceToAdd);

		return result;
	}

	@Override
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationType) {

		return annotationType.equals(annotationClassToAdd) ? true : annotMethod
				.isAnnotationPresent(annotationType);

	}

}
