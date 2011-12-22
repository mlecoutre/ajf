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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.namedquery.harness.Model1;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryNoImplPolicy;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryNoImplServiceBD;
import ajf.persistence.jpa.test.namedquery.harness.NamedQueryWithImplServiceBD;

public class NamedQueryTest {

	private ContainerLifecycle container;
	private BeanManager beanManager;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private NamedQueryNoImplServiceBD namedQueryNoImpl;
	private NamedQueryWithImplServiceBD namedQueryWithImpl;
	
	@Before
	public void setUp() throws Exception {		
		WebBeansContext currentInstance = WebBeansContext.currentInstance();
        container = currentInstance.getService(ContainerLifecycle.class);
        container.startApplication(null);
        beanManager = container.getBeanManager();
        logger.info("Bean that exists in the test :");		
		//Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class);
		for (Bean<?> bean : allBeans) {
			logger.info(bean.getBeanClass().getName());
			logger.info("  types      : " + bean.getTypes());
			logger.info("  qualifiers : " + bean.getQualifiers());
		}           
                
        EntityManager em = EntityManagerProvider.getEntityManager("default");
        em.getTransaction().begin();
        em.persist(new Model1("nicolas"));
        em.persist(new Model1("vincent"));                
	}

	@After
	public void tearDown() throws Exception {
		EntityManagerProvider.getEntityManager("default").getTransaction().rollback();
		EntityManagerProvider.closeAll();
		container.stopApplication(null);		
	}

	@Test
	public void testNamedQueryWithNoImpl() {	
		Set<Bean<?>> beans = beanManager.getBeans(NamedQueryNoImplServiceBD.class, new AnnotationLiteral<Any>() {});
        Bean<?> bean = beans.iterator().next();
        namedQueryNoImpl = NamedQueryNoImplServiceBD.class.cast(bean.create(null));
        
		List<Model1> res = namedQueryNoImpl.findAllModelsByName("nicolas");
		Assert.assertNotNull(res);
		Assert.assertEquals(1, res.size());		
	}
	
	@Test
	public void testNamedQueryWithImpl() {	
		Set<Bean<?>> beans = beanManager.getBeans(NamedQueryWithImplServiceBD.class, new AnnotationLiteral<Any>() {});
		Bean<?> bean = beans.iterator().next();        
        namedQueryWithImpl = NamedQueryWithImplServiceBD.class.cast(bean.create(null));
        
		List<Model1> res1 = namedQueryWithImpl.findAllModelsByName("nicolas");		
		Assert.assertNotNull(res1);
		Assert.assertEquals(1, res1.size());			
		
		List<Model1> res2 = namedQueryWithImpl.findManualQuery();		
		Assert.assertNotNull(res2);
		Assert.assertEquals(0, res2.size());		
	}
	
}
