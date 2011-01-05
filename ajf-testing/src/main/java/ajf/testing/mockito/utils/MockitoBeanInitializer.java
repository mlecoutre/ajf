package ajf.testing.mockito.utils;

import org.mockito.MockitoAnnotations;

import ajf.utils.BeanInitializer;

public class MockitoBeanInitializer 
	implements BeanInitializer {
	
	private final static BeanInitializer beanInstance = new MockitoBeanInitializer(); 

	/**
	 * Default constructor 
	 */
	public MockitoBeanInitializer() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanInitializer#initialize(java.lang.Object)
	 */
	public void initialize(Object beanInstance) {
		MockitoAnnotations.initMocks(beanInstance);
	}

	@Override
	public BeanInitializer getInstance() {
		return beanInstance;
	}
	
}
