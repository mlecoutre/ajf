package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import am.ajf.core.ApplicationContext;

public class PropertyProducer {

	public PropertyProducer() {
		super();
	}

	@Produces
	public String produceProperty(InjectionPoint ip) {
		if (!ip.getAnnotated().isAnnotationPresent(Property.class)) {
			String who = ip.getMember().getDeclaringClass().getName()
					.concat("#").concat(ip.getMember().getName());
			throw new RuntimeException("The field injection for '".concat(who)
					.concat("' has to be qualified with the annotation @")
					.concat(Property.class.getName()));
		}
		String key = ip.getAnnotated().getAnnotation(Property.class).value();
		String value = ApplicationContext.getConfiguration().getString(key);
		return value;
	}

}
