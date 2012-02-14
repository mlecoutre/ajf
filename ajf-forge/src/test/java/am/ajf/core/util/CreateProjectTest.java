package am.ajf.core.util;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.xml.stream.FactoryConfigurationError;

import org.junit.After;
import org.junit.Test;

import am.ajf.forge.util.EclipseUtils;

public class CreateProjectTest {

	public static final String MY_TEST_REPO = "C:/AJF-Forge-Test-tmp";

	File myDirectory;

	@Test
	public void generateEclipseProjectFileTest() {

		myDirectory = new File(MY_TEST_REPO);

		if (!myDirectory.exists()) {
			myDirectory.mkdirs();
		}

		File returnedFile = null;
		try {
			returnedFile = EclipseUtils.generateEclipseProjectFile(
					"myprojectName", MY_TEST_REPO);
		} catch (FactoryConfigurationError e) {
			System.out
					.println("generateEclipseProjectFileTest - Error occured : "
							+ e.toString());
		} catch (Exception e) {
			System.out
					.println("generateEclipseProjectFileTest - Error occured : "
							+ e.toString());
		}

		assertNotNull(
				"Error occured during the Eclipse .project file generation",
				returnedFile);

		/*
		 * Remove the generated file (to clean)
		 */
		if (null != returnedFile) {

			returnedFile.delete();
			myDirectory.delete();

			returnedFile = null;
			myDirectory = null;

		}

	}

	@Test
	public void generateEclipseMavenPrefFileTest() {

		myDirectory = new File(MY_TEST_REPO);

		if (!myDirectory.exists()) {
			myDirectory.mkdirs();
		}

		File returnedFile = null;
		try {
			returnedFile = EclipseUtils
					.generateEclipseMavenPrefFile(MY_TEST_REPO);
		} catch (Exception e) {
			System.out
					.println("generateEclipseMavenPrefFileTest - Error occured : "
							+ e.toString());
		}

		assertNotNull("Error occured while generating eclipse maven pref file",
				returnedFile);

		/*
		 * Remove the generated file (to clean)
		 */
		if (null != returnedFile) {

			returnedFile.delete();
			myDirectory.delete();

			returnedFile = null;
			myDirectory = null;

		}

	}

	/**
	 * Clean the file system from the files generated for testing
	 */
	@After
	public void cleanFiles() {

		myDirectory = new File(MY_TEST_REPO);

		if (myDirectory.exists()) {

			// Delete childrens
			File[] listFiles = myDirectory.listFiles();
			for (File myFile : listFiles) {
				myFile.delete();
			}
			myDirectory.delete();
		}
		myDirectory = null;

	}
}
