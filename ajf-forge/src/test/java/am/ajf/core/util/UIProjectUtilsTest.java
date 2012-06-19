package am.ajf.core.util;

import static org.junit.Assert.*;

import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

import am.ajf.forge.utils.UIProjectUtils;

public class UIProjectUtilsTest {

	@Test
	public void testCreateJavaClass() {

		JavaClass javaClass = UIProjectUtils.createjavaManagedBeanClass(
				"am.test.packlage", "myClassname");

		assertNotNull("Generated Java class should not be null", javaClass);
		assertTrue("Generated Java class's name should not be null",
				null != javaClass.getName());
		assertTrue("Generated Java class should not be empty", !javaClass
				.getName().isEmpty());

		javaClass = null;
	}
}
