package am.ajf.core.beans;

import java.io.Serializable;

import org.apache.commons.configuration.Configuration;
import org.w3c.dom.Node;

import am.ajf.core.beans.api.BeanFactory;
import am.ajf.core.utils.BeanUtils;

public class BeanDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int BEAN_ORDINAL_DEFAULT_VALUE = 1;

	private final String beanProfile;
	private final String beanJNDI;
	private final Class<?> beanClass;
	
		private Class<?> beanFactoryClass = null;
	private BeanFactory beanFactory = null;
	
	private final int beanOrdinal;

	private final Configuration beanConfiguration;
	
	private Node settingsNode = null;

	public BeanDefinition(String beanProfile, Class<?> beanClass,
			String beanJNDI,
			int beanOrdinal, Class<?> beanFactoryClass,
			Configuration beanConfiguration) {
		super();
		
		this.beanProfile = beanProfile;
		this.beanClass = beanClass;
		this.beanJNDI = beanJNDI;
		this.beanOrdinal = beanOrdinal;
		intiBeanFactory(beanFactoryClass);
		this.beanConfiguration = beanConfiguration;
	}

	protected void intiBeanFactory(Class<?> beanFactoryClass) {
		this.beanFactoryClass = beanFactoryClass;
		this.beanFactory = null;
		
		if (null != this.beanFactoryClass) {
			this.beanFactory = (BeanFactory) BeanUtils.newInstance(beanFactoryClass);
		}
		if (null == this.beanFactory) {
			this.beanFactory = new DefaultBeanFactoryImpl();
		}
	}

	public BeanDefinition(String beanProfile, Class<?> beanClass, String beanJNDI,
			Class<?> beanFactoryClass, Configuration beanConfiguration) {
		super();
		this.beanProfile = beanProfile;
		this.beanClass = beanClass;
		this.beanJNDI = beanJNDI;
		this.beanOrdinal = BEAN_ORDINAL_DEFAULT_VALUE;
		intiBeanFactory(beanFactoryClass);
		this.beanConfiguration = beanConfiguration;
	}

	public String getBeanProfile() {
		return beanProfile;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public Class<?> getBeanFactoryClass() {
		return beanFactoryClass;
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public int getBeanOrdinal() {
		return beanOrdinal;
	}
	
	public Configuration getConfiguration() {
		return beanConfiguration;
	}

	public Node getSettingsNode() {
		return settingsNode;
	}

	public void setSettingsNode(Node settingsNode) {
		this.settingsNode = settingsNode;
	}
	
	public String getBeanJNDI() {
		return beanJNDI;
	}
	
	public boolean isNamed() {
		return ((null != beanJNDI) && (0 < beanJNDI.length()));
	}

	@Override
	public String toString() {
		return String
				.format("BeanDefinition [beanProfile=%s, beanClass=%s, beanJNDI=%s, beanFactoryClass=%s, beanOrdinal=%s]",
						beanProfile, beanClass, beanJNDI, beanFactoryClass,
						beanOrdinal);
	}
			
}
