package am.ajf.core.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.forge.utils.VelocityBuilder;

public class VelocityBuilderTest {

	static Logger logger = LoggerFactory.getLogger(VelocityBuilderTest.class);

	private static final String EXISTING_TEMPLATE = "site.xml.vm";
	private static final String NOT_EXISTING_TEMPLATE = "notExistingTemplate.vm";

	@Test
	public void testVelocityFullSuccess() throws IOException {

		logger.debug("START testVelocityFullSuccess");

		String myAppliName = "myAppliName";

		StringWriter bodyContent = testSiteVelocity(myAppliName, true, true);

		assertTrue("Result should contain the 'report' part.", bodyContent
				.toString().contains("reports"));
		assertTrue(
				"Result should contain the dynamic part containing appli name.",
				bodyContent.toString().contains(myAppliName));

		logger.debug("END testVelocityFullSuccess");

	}

	@Test
	public void testVelocityWithoutMenusSuccess() throws IOException {

		logger.debug("START testVelocityWithoutMenusSuccess");

		String myAppliName = "myAppliName";
		StringWriter bodyContent = testSiteVelocity(myAppliName, true, false);

		assertTrue("Result should contain the 'report' part.", bodyContent
				.toString().contains("reports"));
		assertFalse(
				"Result should NOT contain the dynamic part containing appli name.",
				bodyContent.toString().contains(myAppliName));

		logger.debug("END testVelocityWithoutMenusSuccess");

	}

	@Test
	public void testVelocityWithoutReportSuccess() throws IOException {

		logger.debug("START testVelocityWithoutReportSuccess");

		String myAppliName = "myAppliName";

		StringWriter bodyContent = testSiteVelocity(myAppliName, false, true);

		assertFalse("Result should NOT contain the 'report' part.", bodyContent
				.toString().contains("reports"));
		assertTrue(
				"Result should contain the dynamic part containing appli name.",
				bodyContent.toString().contains(myAppliName));

		logger.debug("END testVelocityWithoutReportSuccess");

	}

	@Test
	public void testVelocityWithoutAnythingSuccess() throws IOException {

		logger.debug("START testVelocityWithoutAnythingSuccess");

		String myAppliName = "myAppliName";
		StringWriter bodyContent = testSiteVelocity(myAppliName, false, false);

		assertFalse("Result should NOT contain the 'report' part.", bodyContent
				.toString().contains("reports"));
		assertFalse(
				"Result should NOT contain the dynamic part containing appli name.",
				bodyContent.toString().contains(myAppliName));

		logger.debug("END testVelocityWithoutAnythingSuccess");

	}

	@Test
	public void testVelocityMergeInFile() throws IOException {

		File mytargetFile = new File(FileUtils.getTempDirectoryPath().concat(
				"myFile.tmp"));

		VelocityBuilder velocityBuilder = new VelocityBuilder();
		VelocityContext context = velocityBuilder.getContext();

		context.put("appliName", "myAppliName");

		context.put("isReports", true);
		context.put("isParentProject", true);

		velocityBuilder
				.merge(EXISTING_TEMPLATE, mytargetFile.getAbsolutePath());

		logger.debug("total file space : " + mytargetFile.length());

		assertTrue("File should contain some characters",
				mytargetFile.length() > 100);

		mytargetFile.delete();
		mytargetFile = null;

	}

	@Test(expected = ResourceNotFoundException.class)
	public void testVelocityWrongTemplate() throws IOException {

		logger.debug("START testVelocityWrongTemplate");

		VelocityBuilder velocityBuilder = new VelocityBuilder();
		@SuppressWarnings("unused")
		VelocityContext context = velocityBuilder.getContext();
		StringWriter bodyContent = new StringWriter();
		velocityBuilder.merge(NOT_EXISTING_TEMPLATE, bodyContent);

		logger.debug("END testVelocityWrongTemplate");

	}

	/**
	 * Quick test of template for site.xml. Input 2 booleans to test appearance
	 * of different parts in the file
	 * 
	 * @param isReports
	 * @param isParentProject
	 * @return
	 * @throws IOException
	 */
	private StringWriter testSiteVelocity(String appliname, boolean isReports,
			boolean isParentProject) throws IOException {

		VelocityBuilder velocityBuilder = new VelocityBuilder();
		VelocityContext context = velocityBuilder.getContext();

		context.put("appliName", appliname);
		context.put("isReports", isReports);
		context.put("isParentProject", isParentProject);

		StringWriter bodyContent = new StringWriter();
		velocityBuilder.merge(EXISTING_TEMPLATE, bodyContent);

		return bodyContent;
	}
}
