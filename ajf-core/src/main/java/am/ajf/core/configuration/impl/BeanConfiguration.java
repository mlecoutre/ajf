package am.ajf.core.configuration.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.configuration.AbstractConfiguration;

import am.ajf.core.configuration.resolver.ELStyleResolver;

public class BeanConfiguration extends AbstractConfiguration {

	Map<String, Object> map = new HashMap<String, Object>();

	private final static ELStyleResolver defaultResolver = new ELStyleResolver();
	PropertyUtilsBean propUtils = new PropertyUtilsBean();
	
	public BeanConfiguration() {
		super();
		propUtils.setResolver(defaultResolver);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(String key) {
		//String baseKey = resolver.first(key);
		String baseKey = defaultResolver.first(key);
		return map.containsKey(baseKey);
	}

	@Override
	public Object getProperty(String key) {
		Object result = null;
		try {
			result = propUtils.getProperty(map, key);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return result;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator getKeys() {
		return map.keySet().iterator();
	}

	@Override
	protected void addPropertyDirect(String key, Object value) {
		map.put(key, value);
	}

}
