package am.ajf.web.controllers;

import am.ajf.web.controllers.SecurityMBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SecurityBeanTest
 * 
 * @author E010925
 * 
 */
public class SecurityMBeanTest {

	private SecurityMBean securityBean = new SecurityMBean();

	private Logger logger = LoggerFactory.getLogger(SecurityMBeanTest.class);

	/**
	 * Default constructor
	 */
	public SecurityMBeanTest() {
		super();
	}

	/**
	 * Test valid and invalid authentication authentication
	 * 
	 * @throws Exception
	 *             on error
	 */
	/*
	@Test
	public void testDoLoginOk() throws Exception {
		logger.debug("testDoLogin");
		securityBean.setUsername("u002617");
		securityBean.setPassword("myPassword");
		String res = securityBean.doLogin();
		Assert.assertTrue(
				"Result with u002617/myPassword should return the standard JSF outcome",
				"/index".equals(res));

		boolean isLogged = securityBean.getIsLogIn();
		Assert.assertTrue(
				"securityBean.getIsLogIn should return true for u002617",
				isLogged);

		boolean isAllowed = securityBean.isAllowed("admin");
		Assert.assertTrue(
				"securityBean.isAllowed should return true for admin role",
				isAllowed);

//		isAllowed = securityBean.isAllowed("admin, expertUser");
//		Assert.assertTrue(
//				"securityBean.isAllowed should return true for admin,expertUser  roles",
//				isAllowed);

		res = securityBean.doLogout();
		Assert.assertTrue(
				"securityBean.doLogout should redirect the default outcome",
				"/index".equals(res));

	}
	*/
	
	/*
	@Test
	public void testDoLoginFailed() throws Exception {
		logger.debug("testDoLogin");

		securityBean.setUsername("unknownUser");
		securityBean.setPassword("invalidPwd");
		String res = securityBean.doLogin();
		Assert.assertTrue(
				"Result with unknownUser/invalidPwd should return  the error JSF outcome",
				"/ajf/errors/accessDenied".equals(res));

		boolean isLogged = securityBean.getIsLogIn();
		Assert.assertTrue(
				"securityBean.getIsLogIn should return false for unknownUser",
				!isLogged);

		boolean isAllowed = securityBean.isAllowed("admin");
		Assert.assertTrue(
				"securityBean.isAllowed should return false for admin role",
				!isAllowed);

		// isAllowed = securityBean.isAllowed("anonymous");
		// Assert.assertTrue(
		// "securityBean.isAllowed should return false for anonymous role",
		// isAllowed);
	}
	*/
}
