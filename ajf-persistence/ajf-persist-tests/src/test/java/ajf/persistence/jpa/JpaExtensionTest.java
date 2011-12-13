package ajf.persistence.jpa;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JpaExtensionTest {

	private ContainerLifecycle container;
	private SimpleInjectedClass sic;
	
	@Before
	public void setUp() throws Exception {		
		WebBeansContext currentInstance = WebBeansContext.currentInstance();
        container = currentInstance.getService(ContainerLifecycle.class);
        container.startApplication(null);
        BeanManager beanManager = container.getBeanManager();		        
        Set<Bean<?>> beans = beanManager.getBeans(SimpleInjectedClass.class);
        Bean<?> bean = beans.iterator().next();
        sic = SimpleInjectedClass.class.cast(bean.create(null));
	}

	@After
	public void tearDown() throws Exception {
		container.stopApplication(null);
	}

	@Test
	public void testExtensionCreation() {		
		System.out.println(sic.doSomethingOnInjected());
	}

}
