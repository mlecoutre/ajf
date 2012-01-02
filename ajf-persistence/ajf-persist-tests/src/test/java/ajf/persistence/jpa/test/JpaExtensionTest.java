package ajf.persistence.jpa.test;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.test.harness.ModelCrud;
import ajf.persistence.jpa.test.harness.SimpleCrudServiceBD;
import ajf.persistence.jpa.test.harness.SimpleInjectedBean;
import ajf.persistence.jpa.test.harness.SimpleInjectedClass;

@RunWith(Arquillian.class)
public class JpaExtensionTest {
	
	@Inject
	private SimpleInjectedClass sic;

	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(SimpleInjectedClass.class)
				.addClasses(SimpleInjectedBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml", 
						ArchivePaths.create("persistence.xml"));
	}

	@Test
	public void testExtensionCreation() {		
		System.out.println(sic.doSomethingOnInjected());
	}

}
