package ajf.persistence.jpa.impl;

import javax.inject.Named;

import ajf.persistence.jpa.ClassMatcher;

@Named
public class ExtensionClassMatcher implements ClassMatcher {

	/**
	 * @todo Check the package name also.
	 */
	public boolean isServiceClass(Class<?> clazz) {
		return clazz.getName().endsWith("Service");
	}

	/**
	 * @todo Check the package name also.
	 */
	public boolean isServiceInterface(Class<?> clazz) {
		return clazz.getName().endsWith("ServiceBD");
	}
}
