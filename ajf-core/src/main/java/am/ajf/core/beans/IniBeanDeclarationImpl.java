package am.ajf.core.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertyConverter;

import am.ajf.core.configuration.ConfigurationHelper;

public class IniBeanDeclarationImpl implements ExtendedBeanDeclaration {

	 private static final int BEAN_ORDINAL_DEFAULT_VALUE = 1;

	/** Constant for the prefix of reserved attributes. */
    public static final String RESERVED_PREFIX = "config-";
    
    /** Constant for the bean class attribute. */
    public static final String BEAN_ORDINAL = RESERVED_PREFIX + "ordinal";

    /** Constant for the bean class attribute. */
    public static final String BEAN_CLASS = RESERVED_PREFIX + "class";

    /** Constant for the bean factory attribute. */
    public static final String BEAN_FACTORY = RESERVED_PREFIX + "factory";

    /** Constant for the bean factory parameter attribute. */
    public static final String FACTORY_PARAM = RESERVED_PREFIX + "factoryParam";
    
    private final Configuration contextConfiguration;
    
    private final Configuration configuration;
    private final String sectionName;
    
    private final Class<?> defaultBeanClass;
	
    public IniBeanDeclarationImpl(Class<?> defaultBeanClass, Configuration contextConfiguration, Configuration iniConfiguration,
			String iniSectionName) {
		super();
		this.defaultBeanClass = defaultBeanClass;
		this.contextConfiguration = contextConfiguration;
		this.configuration = iniConfiguration;
		this.sectionName = iniSectionName;
	}
    
    public Configuration getContextConfiguration() {
		return contextConfiguration;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public String getSectionName() {
		return sectionName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see am.ajf.core.beanutils.ExtendedBeanDeclaration#getDefaultBeanClass()
	 */
	@Override
	public Class<?> getDefaultBeanClass() {
		return defaultBeanClass;
	}

	@Override
	public String getBeanFactoryName() {
		 return getConfiguration().getString(BEAN_FACTORY);
	}

	@Override
	public Object getBeanFactoryParameter() {
		return getConfiguration().getString(FACTORY_PARAM);
	}

	@Override
	public String getBeanClassName() {
		return getConfiguration().getString(BEAN_CLASS);
	}
	
	/*
	 * (non-Javadoc)
	 * @see am.ajf.core.beanutils.ExtendedBeanDeclaration#getBeanOrdinal()
	 */
	@Override
	public int getBeanOrdinal() {
		
		try {
			return getConfiguration().getInt(BEAN_ORDINAL, BEAN_ORDINAL_DEFAULT_VALUE);
		} catch (Exception e) {
			return 1;
		}
	}
	

	@Override
	public Map<String, Object> getBeanProperties() {
		
		Map<String, Object> props = new HashMap<String, Object>();
        
		//SubnodeConfiguration subConfig = configuration.getSection(sectionName);
		//replaced by configuration
		
		Configuration compositeConfiguration = configuration; 
		if (null != contextConfiguration) {
			compositeConfiguration = ConfigurationHelper.mergeConfigurations(contextConfiguration, configuration);
		}
		
		for (Iterator<String> iterator = configuration.getKeys(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			 if (!isReservedKey(key)) {
				 props.put(key, interpolate(configuration.getList(key), compositeConfiguration));
			 }
		}
		
        return props;
        		
	}

	private Object interpolate(List<Object> lov, Configuration contextConfiguration) {
		
		if (null == lov)
			return null;
		if (lov.isEmpty())
			return null;
		
		List<Object> interpolatedLov = new ArrayList<Object>();
		for (Object value : lov) {
			Object interpolatedObject = PropertyConverter.interpolate(value, (AbstractConfiguration) contextConfiguration);
			interpolatedLov.add(interpolatedObject);
		}
		
		return interpolatedLov;
		
	}

	private boolean isReservedKey(String key) {
		return key.startsWith(RESERVED_PREFIX);
	}

	@Override
	public Map<String, Object> getNestedBeanDeclarations() {
		return null;
	}

	@Override
	public String toString() {
		return "IniBeanDeclarationImpl [getBeanOrdinal()=" + getBeanOrdinal() + "]";
	}
	
	
	

}
