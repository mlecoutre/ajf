package am.ajf.injection.internal;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

public class EJBHelper {

	private EJBHelper() {
		super();
	}
	
	public static <T> boolean addVeto(Class<T> javaClass, ProcessAnnotatedType<T> pat) {
		if (javaClass.isAnnotationPresent(Stateless.class) || 
			javaClass.isAnnotationPresent(Stateful.class)) {
			pat.veto();
			return true;
		}
		return false;
	}

	
	
}
