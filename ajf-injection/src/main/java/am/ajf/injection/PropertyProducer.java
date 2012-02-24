package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.logger.LoggerFactory;

public class PropertyProducer {

	private final Logger logger = LoggerFactory
			.getLogger(PropertyProducer.class);

	public PropertyProducer() {
		super();
	}

	@Produces
	public String produceProperty(InjectionPoint ip) throws Throwable {

		if (!ip.getAnnotated().isAnnotationPresent(Property.class)) {
			String who = ip.getMember().getDeclaringClass().getName()
					.concat("#").concat(ip.getMember().getName());
			throw new RuntimeException("The field injection for '".concat(who)
					.concat("' has to be qualified with the annotation @")
					.concat(Property.class.getName()));
		}

		Property propertyAnnotation = ip.getAnnotated().getAnnotation(
				Property.class);
		String key = propertyAnnotation.value();
		try {
			String value = ApplicationContext.getConfiguration().getString(key,
					propertyAnnotation.defaultValue());
			return value;
		}
		catch (Throwable e) {
			logger.error("Unable to get the value for property '".concat(key)
					.concat("'."), e);
			throw e;
		}

	}

}
