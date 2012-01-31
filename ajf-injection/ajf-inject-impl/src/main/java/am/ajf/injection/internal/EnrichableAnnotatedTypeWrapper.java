package am.ajf.injection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javassist.Modifier;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class EnrichableAnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> annotatedType;
	
	private Map<Class<? extends Annotation>, Annotation> annotationsMap = 
		new HashMap<Class<? extends Annotation>,Annotation>();
	private Set<Annotation> annotations = null;
	
	private Map<String, Map<Class<? extends Annotation>, Annotation>> methodsAnnotationsMap = 
		new HashMap<String, Map<Class<? extends Annotation>, Annotation>>();
	private Set<AnnotatedMethod<? super T>> annotatedMethods = null;
	
	public EnrichableAnnotatedTypeWrapper(AnnotatedType<T> annotatedType) {
		this.annotatedType = annotatedType;
		this.annotations = new HashSet<Annotation>(annotatedType.getAnnotations());
		this.annotatedMethods = annotatedType.getMethods();
	}
	
	/**
	 * add annotation to method
	 * @param method
	 * @param annotationType
	 * @param annotation
	 */
	public void addMethodAnnotation(Method method, Class<? extends Annotation> annotationType, Annotation annotation) {
		
		String methodSignature = processMethodSignature(method);
		
		Map<Class<? extends Annotation>, Annotation> methodAnnotationsMap = null;
		if (methodsAnnotationsMap.containsKey(methodSignature)) {
			methodAnnotationsMap = methodsAnnotationsMap.get(methodSignature);
		} else {
			methodAnnotationsMap = new HashMap<Class<? extends Annotation>, Annotation>();
			methodsAnnotationsMap.put(methodSignature, methodAnnotationsMap);
		}
		
		methodAnnotationsMap.put(annotationType, annotation);
		
	}
	
	/**
	 * add annotation to type
	 * @param annotationType
	 * @param annotation
	 */
	public void addAnnotation(Class<? extends Annotation> annotationType, Annotation annotation) {
		
		annotationsMap.put(annotationType, annotation);
		
	}
	
	/**
	 * process the new annotatedMethods
	 */
	public void processAnnotatedMethods() {
		
		if (methodsAnnotationsMap.isEmpty()) 
			return;
		
		Set<AnnotatedMethod<? super T>> result = new HashSet<AnnotatedMethod<? super T>>();

		for (AnnotatedMethod<? super T> annotatedMethod : annotatedType
				.getMethods()) {

			AnnotatedMethod<? super T> annotMethod = (AnnotatedMethod<? super T>) annotatedMethod;
			Method javaMethod = annotatedMethod.getJavaMember();
			
			EnrichableAnnotatedMethodWrapper<T> wrappedAnnotatedMethod = null;
			
			if (Modifier.isPublic(javaMethod.getModifiers())) {
				String methodSignature = processMethodSignature(javaMethod);
				
				if (methodsAnnotationsMap.containsKey(methodSignature)) {
					Map<Class<? extends Annotation>, Annotation> annotationsMap = 
						methodsAnnotationsMap.get(methodSignature);
					
					wrappedAnnotatedMethod = new EnrichableAnnotatedMethodWrapper<T>(annotatedMethod);
					Set<Entry<Class<? extends Annotation>, Annotation>> entries = annotationsMap.entrySet();
					for (Entry<Class<? extends Annotation>, Annotation> entry : entries) {
						wrappedAnnotatedMethod.addAnnotation(entry.getKey(), entry.getValue());
					}
					wrappedAnnotatedMethod.processAnnotations();
					
				}				

			}
			
			if (null == wrappedAnnotatedMethod) {
				result.add(annotMethod);
			} else {
				result.add(wrappedAnnotatedMethod);
			}

		}
		
		this.annotatedMethods = result;
		
	}
	
	/**
	 * process the type annotations
	 */
	public void processAnnotations() {
		annotations.addAll(annotationsMap.values());
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	private String processMethodSignature(Method method) {
		String methodSignature = method.getName().concat(Arrays.toString( method.getParameterTypes()));
		return methodSignature;
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
	@SuppressWarnings({ "hiding", "unchecked" })
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		if (annotationsMap.containsKey(annotationType)) {
			return (T) annotationsMap.get(annotationType);
		}
		return annotatedType.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationType) {
		return annotationsMap.containsKey(annotationType) ? true : annotatedType
				.isAnnotationPresent(annotationType);
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
		return annotatedMethods;
	}

}
