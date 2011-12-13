package ajf.persistence.jpa.test.namedquery;

import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.Logger;

import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.namedquery.harness.Model1;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryNoImplPolicy;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryNoImplServiceBD;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryWithImplServiceBD;

public class NamedQueryTest {

	private ContainerLifecycle container;
	private NamedQueryNoImplPolicy namedQueryNoImplPolicy;
	private NamedQueryWithImplServiceBD namedQueryWithImplBD;
	
	@Before
	public void setUp() throws Exception {		
		WebBeansContext currentInstance = WebBeansContext.currentInstance();
        container = currentInstance.getService(ContainerLifecycle.class);
        container.startApplication(null);
        BeanManager beanManager = container.getBeanManager();
        System.out.println("Bean that exists in the test :");		
		//Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class);
		for (Bean<?> bean : allBeans) {
			System.out.println(bean.getBeanClass().getName() + " (" +bean.getTypes()+ ") : "+bean.getQualifiers());
		}
        
        
        
        Set<Bean<?>> beans = beanManager.getBeans(NamedQueryNoImplPolicy.class, new AnnotationLiteral<Any>() {});
        Bean<?> bean = beans.iterator().next();
        namedQueryNoImplPolicy = NamedQueryNoImplPolicy.class.cast(bean.create(null));
        beans = beanManager.getBeans(NamedQueryWithImplServiceBD.class, new AnnotationLiteral<Any>() {});
        bean = beans.iterator().next();
        namedQueryWithImplBD = NamedQueryWithImplServiceBD.class.cast(bean.create(null));
        
                
        EntityManager em = EntityManagerProvider.getEntityManager("default");
        em.getTransaction().begin();
        em.persist(new Model1("nicolas"));
        em.persist(new Model1("vincent"));                
	}

	@After
	public void tearDown() throws Exception {
		//EntityManager em = ;
		EntityManagerProvider.getEntityManager("default").getTransaction().rollback();
		EntityManagerProvider.closeAll();
		container.stopApplication(null);
		
	}

	@Test
	public void testNamedQueryWithNoImpl() {		
		List<Model1> res = namedQueryNoImplPolicy.findAllModelsByName("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());		
	}
	
	@Test
	@Ignore
	public void testNamedQueryWithImpl() {		
		List<Model1> res = namedQueryWithImplBD.findAllModelsByName("nicolas");		
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());								
	}
	
	@Test
	@Ignore
	public void testNamedQueryWithImplNormalMethod() {		
		List<Model1> res = namedQueryWithImplBD.findManualQuery();		
		Assert.assertNotNull(res);
		Assert.assertEquals(0, res.size());								
	}
	
}
