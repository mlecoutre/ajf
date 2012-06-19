package am.ajf.core.util;

import static am.ajf.forge.lib.ForgeConstants.UI_MAIN_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.UI_TEST_RESOURCES;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.forge.utils.ExtractionUtils;

public class ExtractionUtilsTest {

	private static File myTempDir;

	@BeforeClass
	public static void init() {
		myTempDir = new File(FileUtils.getTempDirectoryPath().concat(
				"/UIProjectUtils"));
		if (!myTempDir.exists()) {

			myTempDir.mkdirs();

		}

		System.out.println("**INFO : Temp dir for test suite: "
				+ myTempDir.getAbsolutePath());

	}

	@AfterClass
	public static void clean() {

		try {
			FileUtils.deleteDirectory(myTempDir);
		} catch (IOException e) {
			System.out
					.println("ERROR occured when deleting temprary directory : "
							+ myTempDir.getAbsolutePath());
		}

	}

	@Test
	public void unzipWebResourceTest() throws IOException {

		boolean isSucceed = false;
		isSucceed = ExtractionUtils.unzipFile(UI_MAIN_RESOURCES, myTempDir);

		assertTrue(isSucceed);

		isSucceed = false;
		isSucceed = ExtractionUtils.unzipFile(UI_TEST_RESOURCES, myTempDir);

		assertTrue(isSucceed);
	}

}
