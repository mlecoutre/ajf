package am.ajf.core;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationContextTest {

	public ApplicationContextTest() {
		super();
	}
	
	@Test
	public void testConfigKey() {
		
		ApplicationContext.getConfiguration().setThrowExceptionOnMissing(false);
		String value = ApplicationContext.getConfiguration().getString("myKey");
		Assert.assertNotNull("'myKey' is not initialized.", value);
		
	}
	
	@Test
	public void testLogDir() {
		
		File logDir = ApplicationContext.getLogDir();
		Assert.assertNotNull("logDir is not initialized.", logDir);
		
	}
	
	@Test
	public void testWorkingDir() {
		
		File workingDir = ApplicationContext.getWorkingDir();
		Assert.assertNotNull("workingDir is not initialized.", workingDir);
		
	}

}
