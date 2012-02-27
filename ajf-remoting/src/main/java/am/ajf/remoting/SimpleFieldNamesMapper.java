package am.ajf.remoting;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFieldNamesMapper extends EditableMapper implements Mapper {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Object map(Map<String, Object> data) {
		Object obj = null;
		try {
			obj = am.ajf.core.utils.BeanUtils.newInstance(getEntity());
			BeanUtils.populate(obj, data);
		} catch (IllegalAccessException e) {
			logger.warn("Mapping impossible, error populating the bean : "+getEntity().getName(), e);
		} catch (InvocationTargetException e) {
			logger.warn("Mapping impossible, error populating the bean : "+getEntity().getName(), e);
		} catch (Exception e) {
			logger.warn("Mapping impossible, error instanciating the bean : "+getEntity().getName(), e);
		}
		return obj;
	}
}
