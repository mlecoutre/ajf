package ajf.services;

import ajf.utils.BeanUtils;
import ajf.utils.ClassUtils;


public abstract class ServiceFactory {

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<?> serviceClass) throws Exception {

		// is the DAO mocked
		Object mock = MockContext.lookup(serviceClass); 
		if (null != mock) {
			return  (T) mock;
		}
		
		String serviceImplClassName = ClassUtils.processServiceClassName(serviceClass);
		Class<?> serviceImplClass = ClassUtils.loadClass(serviceImplClassName);
		Object serviceImpl = BeanUtils.newInstance(serviceImplClass);
		
		return (T) serviceImpl;
		

	}

}
