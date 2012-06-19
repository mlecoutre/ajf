package am.ajf.core.util;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.forge.utils.EclipseUtils;

/**
 * Tests dealing with the EclipseUtils utilities class tham aims to create all
 * the Eclipse files needed in order to import a project in Eclipse
 * 
 * @author E019851
 * 
 */
public class EclipseUtilsTest {

	// private static final String MY_TEST_REPO =
	// "C:/AJF-Forge-TestDirectory-tmp";
	private static File myDirectory;

	// All the project type
	private static List<String> projectTypeList = new ArrayList<String>();

	@Test
	public void generateEclipseProjectFileTest() {

		File returnedFile = null;
		try {
			returnedFile = EclipseUtils.generateEclipseProjectFile(
					"myprojectName", myDirectory.getAbsolutePath());
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

	}

	@Test
	public void generateClassPathFileTest() {

		// We make the test for EACH project type, in order to test all the
		// possibilities
		for (String projectType : projectTypeList) {

			// Flag for exception
			boolean errorOccured = false;
			// File returned
			File myFile = null;

			try {

				myFile = EclipseUtils.generateClassPathFile(
						myDirectory.getAbsolutePath(), projectType);

			} catch (Exception e) {
				errorOccured = true;
			}

			assertNotNull("Returned file should not be null.", myFile);
			assertTrue("An exception has bee thrown by the test", !errorOccured);

		}

	}

	@BeforeClass
	public static void createTestDirecotry() {

		// My project types list
		projectTypeList.add(PROJECT_TYPE_UI);
		projectTypeList.add(PROJECT_TYPE_WS);
		projectTypeList.add(PROJECT_TYPE_PARENT);
		projectTypeList.add(PROJECT_TYPE_LIB);
		projectTypeList.add(PROJECT_TYPE_EAR);
		projectTypeList.add(PROJECT_TYPE_CORE);
		projectTypeList.add(PROJECT_TYPE_CONFIG);

		// Create directory for the whole test suite
		myDirectory = new File(FileUtils.getTempDirectoryPath().concat(
				"/eclipseUtilsTest"));

		if (!myDirectory.exists()) {
			myDirectory.mkdirs();
		}

		System.out
				.println("**INFO : Test suite EclipseUtils executed in temp directory :"
						.concat(myDirectory.getAbsolutePath()));

	}

	/**
	 * Clean the file system from the files generated for testing. Delete the
	 * test directory and all it's descendents
	 */
	@AfterClass
	public static void cleanFiles() {

		if (myDirectory.exists()) {

			try {
				FileUtils.deleteDirectory(myDirectory);
			} catch (IOException e) {
				System.out.println("**ERROR while deleting temp repo : "
						+ myDirectory.getAbsolutePath());
			}

		}
		myDirectory = null;
	}
}
