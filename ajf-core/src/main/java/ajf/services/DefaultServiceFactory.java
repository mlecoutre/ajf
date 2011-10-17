package ajf.services;

import ajf.utils.BeanUtils;
import ajf.utils.ClassUtils;

public class DefaultServiceFactory implements ServiceFactory {

	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<?> serviceClass) throws Exception {

		String serviceImplClassName = ClassUtils
				.processServiceClassName(serviceClass);

		Class<?> serviceImplClass = ClassUtils.loadClass(serviceImplClassName);
		Object serviceImpl = BeanUtils.getInstance().newInstance(serviceImplClass);

		return (T) serviceImpl;

	}

	public boolean accept(Class<?> serviceClass) {
		return ClassUtils.isService(serviceClass);
	}
	
	public int getPriorityLevel() {
		return 0;
	}

}