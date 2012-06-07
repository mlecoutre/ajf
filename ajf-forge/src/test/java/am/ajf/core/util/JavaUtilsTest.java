package am.ajf.core.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.java.JavaResource;
import org.junit.Test;

import am.ajf.forge.util.JavaHelper;

public class JavaUtilsTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testRetrieveAttributeList() throws URISyntaxException,
			FileNotFoundException {

		File javaClass = new File(JavaUtilsTest.class.getClassLoader()
				.getResource("Person.java").toURI());
		ResourceFactory factory = new ResourceFactory();
		JavaSource javaSource = new JavaResource(factory, javaClass)
				.getJavaSource();

		JavaHelper javaUtils = new JavaHelper();
		List<String> myAttributes = javaUtils.retrieveAttributeList(javaSource);

		// Data that should be returned
		List<String> shouldBeReturnedList = new ArrayList<String>();
		shouldBeReturnedList.add("personid");
		shouldBeReturnedList.add("firstname");
		shouldBeReturnedList.add("lastname");
		shouldBeReturnedList.add("birthday");
		shouldBeReturnedList.add("sex");

		for (String returnedAttribute : shouldBeReturnedList) {
			assertTrue("Attribute list should contain " + returnedAttribute,
					myAttributes.contains(returnedAttribute));
		}

	}

}
