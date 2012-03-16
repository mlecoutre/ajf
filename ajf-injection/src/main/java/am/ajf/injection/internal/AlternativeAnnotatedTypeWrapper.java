package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AlternativeAnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> annotatedType;
	private final Default defaultAnnotation;
	private final Alternative alternativeAnnotation;
	
	public AlternativeAnnotatedTypeWrapper(AnnotatedType<T> annotatedType) {
		this.annotatedType = annotatedType;
		
		this.defaultAnnotation = new Default() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Default.class;
			}
		};
		
		this.alternativeAnnotation = new Alternative() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Alternative.class;
			}
		};
		
	}
	
	@Override
	public Type getBaseType() {
		return annotatedType.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return annotatedType.getTypeClosure();
	}

	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		if (annotationType.equals(Default.class)) {
			return null;
		}
		if (annotationType.equals(Alternative.class)) {
			return (T) alternativeAnnotation;
		}
		return annotatedType.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {
		Set<Annotation> annotations = annotatedType.getAnnotations();
		
		for (Annotation annotation : annotations) {
			System.out.println(annotation.annotationType().getName());
		}
		
		annotations.remove(defaultAnnotation);
		annotations.add(alternativeAnnotation);
		
		for (Annotation annotation : annotations) {
			System.out.println(annotation.annotationType().getName());
		}
		
		return annotations;
	}

	@Override
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationType) {
		if (annotationType.equals(Default.class)) {
			return false;
		}
		if (annotationType.equals(Alternative.class)) {
			return true;
		}
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
	public Set<AnnotatedMethod<? super T>> getMethods() {
		return annotatedType.getMethods();
	}

}
