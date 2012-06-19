package am.ajf.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.java.JavaResource;
import org.junit.Test;

import am.ajf.forge.core.generators.templates.McrGenerationTemplate;
import am.ajf.forge.helpers.JavaHelper;

public class JavaHelperTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testRetrieveAttributeList() throws URISyntaxException,
			FileNotFoundException {

		File javaClass = new File(JavaHelperTest.class.getClassLoader()
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

	@Test
	public void testParseJavaTxtClass() throws Exception {

		JavaClass javaclass = JavaParser.create(JavaClass.class)
				.setPackage("am.voila.test").setName("myClass");

		List<String> uts = new ArrayList<String>();
		uts.add("myAction1");
		uts.add("myAction2");
		uts.add("myAction3");

		Method<JavaClass> myMethod = null;
		for (String ut : uts) {

			// myMethod = (Method<JavaClass>) javaclass.addMethod();
			JavaHelper javahelper = new JavaHelper();

			// get Java Class template as String and parse it to java
			JavaClass temp = (JavaClass) JavaParser.parse(javahelper
					.getJavaClassAsString("TestJavaClassStringTemplate.zip",
							"testJavaClassTexte.txt").replace("deletePerson",
							ut));

			myMethod = temp.getMethods().get(0);
			// System.out.println(myMethod.toString());
			Method<JavaClass> myMethod2 = javaclass.addMethod("public void "
					+ ut + "(" + WordUtils.capitalize(ut) + "PB "
					+ WordUtils.uncapitalize(ut) + "pb) {}");
			myMethod2.addThrows(Exception.class);
			myMethod2.setReturnType(WordUtils.capitalize(ut) + "RB");
			myMethod2.setBody(myMethod.getBody());

		}

		System.out.println(javaclass.toString());
		System.out.println("Size:" + javaclass.toString().length());

		// Assertions
		int supposedSize = 1802;
		assertFalse("Generated java class should not be empty", javaclass
				.toString().isEmpty());
		assertTrue("Generated Java class should be sized of" + supposedSize
				+ " but is :" + javaclass.toString().length(), javaclass
				.toString().length() == supposedSize);
	}

	@Test
	public void testParseJavaFromStream() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File(FileUtils.getTempDirectoryPath()
				+ "/ajf-forge/testParseJavaFromStream.tmp");
		projectManagement.buildManagedBeanMethod(myFile, "myFunction",
				"Person", "myUT", "am.ajf.lib.dto");

		// parse as java class (containing one method, which
		// is the method we want)
		JavaClass temp = (JavaClass) JavaParser.parse(myFile);

		// Delete temp file
		FileUtils.forceDelete(myFile.getParentFile());

		System.out.println(temp.toString());
	}

	@Test
	public void testAddMethodToJavaClass() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File(FileUtils.getTempDirectoryPath()
				+ "/ajf-forge/testParseJavaFromStream.tmp");
		projectManagement.buildManagedBeanMethod(myFile, "myFunction",
				"Person", "myUT", "am.ajf.lib.dto");

		// parse as java class (containing one method, which
		// is the method we want)
		JavaClass temp = (JavaClass) JavaParser.parse(myFile);

		// We use the METHOD 0 (first method of the class)
		Method<JavaClass> myMethod = temp.getMethods().get(0);

		JavaClass javaclass = JavaParser.create(JavaClass.class)
				.setPackage("am.voila.test").setName("myClass");

		// // Create method in managedBean class beeing updated
		Method<JavaClass> myMethod2 = javaclass.addMethod(
				"public void voila() {" + myMethod.getBody() + "}").addThrows(
				Exception.class);

		myMethod2.setBody(myMethod.getBody());

		System.out.println(javaclass.toString());
		// System.out.println(javaclass.toString());
	}
}
