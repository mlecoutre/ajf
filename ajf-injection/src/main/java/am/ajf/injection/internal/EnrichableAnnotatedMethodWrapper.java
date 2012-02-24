package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class EnrichableAnnotatedMethodWrapper<T> implements AnnotatedMethod<T> {

	private final AnnotatedMethod<T> annotatedMethod;

	private Map<Class<? extends Annotation>, Annotation> annotationsMap = new HashMap<Class<? extends Annotation>, Annotation>();
	private Set<Annotation> annotations = null;

	@SuppressWarnings("unchecked")
	public EnrichableAnnotatedMethodWrapper(
			AnnotatedMethod<? super T> annotMethod) {
		this.annotatedMethod = (AnnotatedMethod<T>) annotMethod;
		this.annotations = new HashSet<Annotation>(annotMethod.getAnnotations());
	}

	/**
	 * add annotation
	 * @param key
	 * @param value
	 */
	public void addAnnotation(Class<? extends Annotation> key, Annotation value) {
		annotationsMap.put(key, value);
	}

	/**
	 * process annotations collection
	 */
	public void processAnnotations() {
		annotations.addAll(annotationsMap.values());
	}

	@Override
	public List<AnnotatedParameter<T>> getParameters() {
		return annotatedMethod.getParameters();
	}

	@Override
	public boolean isStatic() {
		return annotatedMethod.isStatic();
	}

	@Override
	public AnnotatedType<T> getDeclaringType() {
		return annotatedMethod.getDeclaringType();
	}

	@Override
	public Type getBaseType() {
		return annotatedMethod.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return annotatedMethod.getTypeClosure();
	}

	@Override
	public Method getJavaMember() {
		return annotatedMethod.getJavaMember();
	}

	@Override
	@SuppressWarnings({ "hiding", "unchecked" })
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		if (annotationsMap.containsKey(annotationType)) {
			return (T) annotationsMap.get(annotationType);
		}
		return annotatedMethod.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationType) {
		return annotationsMap.containsKey(annotationType) ? true : annotatedMethod
				.isAnnotationPresent(annotationType);
	}

}
