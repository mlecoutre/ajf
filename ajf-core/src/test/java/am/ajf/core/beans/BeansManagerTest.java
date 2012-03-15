package am.ajf.core.beans;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import am.ajf.core.mail.MailSender;

public class BeansManagerTest {

	public BeansManagerTest() {
		super();
	}
	
	@Test
	public void testGetBeanDeclarations() throws Exception {
		
		Class<MyService> beanClass = MyService.class;
		
		Map<String, Set<ExtendedBeanDeclaration>> beanDeclarations = BeansManager.getBeanDeclarations(beanClass);
		
		assertNotNull(beanDeclarations);
		
		Set<Entry<String, Set<ExtendedBeanDeclaration>>> entries = beanDeclarations.entrySet();
		for (Iterator<Entry<String, Set<ExtendedBeanDeclaration>>> iterator = entries.iterator(); iterator.hasNext();) {
			Entry<String, Set<ExtendedBeanDeclaration>> entry = (Entry<String, Set<ExtendedBeanDeclaration>>) iterator
					.next();
			System.out.println(entry.getKey());
			Collection<ExtendedBeanDeclaration> values = entry.getValue();
			for (ExtendedBeanDeclaration beanDeclar : values) {
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
		
		MyService myService = BeansManager.getDefaultBean(MyService.class);
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
		
		MailSender myService = BeansManager.getDefaultBean(MailSender.class);
		assertNotNull(myService);
		
		System.out.println(myService);		
	}
	
	
}
