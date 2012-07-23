package am.ajf.naming;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class SimpleGenericBeanFactory implements ObjectFactory {
	
	private final Logger logger = Logger.getLogger(SimpleGenericBeanFactory.class
			.getName());

	public SimpleGenericBeanFactory() {
		super();
	}

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {

		// Customize the bean properties from our attributes
		Reference ref = (Reference) obj;
		
		String beanTypeName = ref.getClassName();
		try {
			@SuppressWarnings("unused")
			// just for checking the bean type class
			Class<?> beanType = Class.forName(beanTypeName);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		
		String beanClassName = null;
		Class<?> beanClass = null;
				
		Map<String, Object> valuesMap = new HashMap<String, Object>();
		Enumeration<?> addrs = ref.getAll();
		while (addrs.hasMoreElements()) {
			RefAddr addr = (RefAddr) addrs.nextElement();
		
			String pName = addr.getType();
			Object pValue = addr.getContent();
			
			if ("class".equals(pName)) {
				beanClassName = (String) pValue;
				beanClass = Class.forName(beanClassName);
			}
			else {
				valuesMap.put(pName, pValue);
			}
		
		}
		
		Object bean = instanciateBean(beanClass);
		initializeBean(bean, beanClass, valuesMap);
				
		// Return the customized instance
		return bean;

	}

	protected Object instanciateBean(Class<?> beanClass)
			throws InstantiationException, IllegalAccessException {
		Object bean = beanClass.newInstance();
		return bean;
	}

	protected void initializeBean(Object bean, Class<?> beanClass,
			Map<String, Object> valuesMap) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		if ((null != propertyDescriptors) && (propertyDescriptors.length > 0)){
			
			for (PropertyDescriptor pDesc : propertyDescriptors) {
				
				String pName = pDesc.getName();
				Method writeMethod = pDesc.getWriteMethod();
				
				if ((null != writeMethod) && (valuesMap.containsKey(pName))) {
					Object value = valuesMap.get(pName);
					try {
						writeMethod.invoke(bean, value);
					} catch (Exception e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}					
				}
			}
			
		}
	}

}
