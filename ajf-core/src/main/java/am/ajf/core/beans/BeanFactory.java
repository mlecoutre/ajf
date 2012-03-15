package am.ajf.core.beans;

import org.apache.commons.configuration.beanutils.BeanHelper;

import am.ajf.core.utils.BeanUtils;
import am.ajf.core.utils.ClassUtils;

import com.google.common.base.Strings;

public class BeanFactory {
	
	public static final BeanFactory DEFAULT = new BeanFactory();

	public BeanFactory() {
		super();
	}

	protected Object createBeanInstance(Class<?> beanClass, ExtendedBeanDeclaration data)
			throws Exception {
					
		return createBeanInstance(data);
		
	}

	/**
	 * 
	 * @param data
	 * @return a new bean instance
	 * @throws ClassNotFoundException
	 */
	protected Object createBeanInstance(ExtendedBeanDeclaration beanDeclaration)
			throws ClassNotFoundException {
		
		Class<?> beanClazz = getBeanClass(beanDeclaration);
		return BeanUtils.newInstance(beanClazz);
		
	}

	public Class<?> getBeanClass(ExtendedBeanDeclaration beanDeclaration)
			throws ClassNotFoundException {
	
		Class<?> beanClazz = null;
		String beanClassName = beanDeclaration.getBeanClassName();
		if (!Strings.isNullOrEmpty(beanClassName)) {
			beanClazz = ClassUtils.getClass(beanClassName);
		}
		if (null == beanClazz) {
			beanClazz = beanDeclaration.getDefaultBeanClass();
		}
		return beanClazz;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Object createBean(ExtendedBeanDeclaration data)
			throws Exception {
		
		Object result = createBeanInstance(data);
        initBean(result, data);
        return result;
		
	}

	public void initBean(Object beanInstance, ExtendedBeanDeclaration data)
			throws Exception {
		BeanHelper.initBean(beanInstance, data);
	}	
	
}
