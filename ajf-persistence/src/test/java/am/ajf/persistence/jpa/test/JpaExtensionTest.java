package am.ajf.persistence.jpa.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.persistence.jpa.test.harness.SimpleInjectedBean;
import am.ajf.persistence.jpa.test.harness.SimpleInjectedClass;

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
