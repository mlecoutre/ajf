package am.ajf.core.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import am.ajf.forge.util.UIProjectUtils;

public class UIProjectUtilsTest {

	private static final String TEMP_WEBAPP_PATH = "C:/temp-forge";
	private static File myTempDir = new File(TEMP_WEBAPP_PATH);

	@Before
	public void init() {

		if (!myTempDir.exists()) {
			myTempDir.mkdirs();
		}
		
	}

	@Test
	public void unzipWebResourceTest() throws IOException {

		boolean isSucceed = UIProjectUtils.unzipFile("UIResources.zip",
				myTempDir);

		assertTrue(isSucceed);
	}

	// @After
	public void clean() {

		if (myTempDir.exists()) {

			try {
				FileUtils.forceDelete(myTempDir);
			} catch (IOException e) {
				System.out
						.println("Error occured when deleting temprary directory");
			}

		}

		myTempDir = null;
	}

}
