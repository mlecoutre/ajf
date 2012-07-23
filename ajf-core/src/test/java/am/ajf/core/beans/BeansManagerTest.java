package am.ajf.core.beans;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;

import org.junit.Test;

import am.ajf.core.mail.MailSender;

public class BeansManagerTest {

	public BeansManagerTest() {
		super();
	}
	
	@Test
	public void testGetBeanDefinition() throws Exception {
		
		Class<MyService> beanClass = MyService.class;
		
		Map<String, Set<BeanDefinition>> beanDeclarations = BeansManager.getBeanDefinitions(beanClass);
		
		assertNotNull(beanDeclarations);
		
		Set<Entry<String, Set<BeanDefinition>>> entries = beanDeclarations.entrySet();
		for (Iterator<Entry<String, Set<BeanDefinition>>> iterator = entries.iterator(); iterator.hasNext();) {
			Entry<String, Set<BeanDefinition>> entry = (Entry<String, Set<BeanDefinition>>) iterator
					.next();
			System.out.println(entry.getKey());
			Collection<BeanDefinition> values = entry.getValue();
			for (BeanDefinition beanDeclar : values) {
				System.out.println("\t"+beanDeclar.toString());
				try {
					Object bean = BeansManager.getBean(beanDeclar);
					System.out.println("\t"+bean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

	@Test
	public void testGetDefaultBean() throws Exception {
		
		MyService myService = BeansManager.getBean(MyService.class);
		assertNotNull(myService);
		
		System.out.println(myService);
		
	}
	
	@Test
	public void testGetProfileBean() throws Exception {
		
		System.setProperty(BeansManager.BEAN_PROFILES_KEY, "test");
		MyService myService = BeansManager.getBean(MyService.class);
		
		System.clearProperty(BeansManager.BEAN_PROFILES_KEY);
		
		assertNotNull(myService);
		
		System.out.println(myService);
		
	}
	
	@Test
	public void testGetBean() throws Exception {
		
		MyService myService = BeansManager.getBean(MyService.class, "test");
		assertNotNull(myService);
		
		System.out.println(myService);
		
	}
	
	@Test(expected=NamingException.class)
	public void testGetNamedBean() throws Exception {
		
		MyService myService = BeansManager.getBean(MyService.class, "named");
		assertNotNull(myService);
		
		System.out.println(myService);
		
	}
	
	@Test
	public void testGetBeans() throws Exception {
		
		Set<Class<?>> beansSet = BeansManager.getBeans();
		assertNotNull(beansSet);

		System.out.println(beansSet);
		
	}
	
	@Test
	public void testGetMailSender() throws Exception {
		
		MailSender myService = BeansManager.getBean(MailSender.class);
		assertNotNull(myService);
		
		System.out.println(myService);		
	}
	
	
}
