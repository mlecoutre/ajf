package ajf.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.configuration.AbstractConfiguration;

public class BeanConfiguration extends AbstractConfiguration {

	Map<String, Object> map = new HashMap<String, Object>();

	public static final ELResolver resolver = new ELResolver();
	PropertyUtilsBean propUtils = new PropertyUtilsBean();

	public BeanConfiguration() {
		super();
		propUtils.setResolver(resolver);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(String key) {
		String baseKey = resolver.first(key);
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

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getKeys() {
		return map.keySet().iterator();
	}

	@Override
	protected void addPropertyDirect(String key, Object value) {
		map.put(key, value);
	}

}
