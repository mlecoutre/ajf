package am.ajf.showcase.web.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroTest {

	private static Logger logger = LoggerFactory.getLogger(ShiroTest.class);

	@Test
	public void testAuthentication() {
		// Using the IniSecurityManagerFactory, which will use the an INI file
		// as the security file.
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				"L:\\data\\workspaces\\ws_02\\ajf-showcase-simple\\src\\test\\resources\\shiro.ini");

		// Setting up the SecurityManager...
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "psw");
		Subject user = SecurityUtils.getSubject();
		user.login(token);

		logger.info("User is authenticated:  " + user.isAuthenticated());
	}

	@Test
	public void testArmony() {
		// Using the IniSecurityManagerFactory, which will use the an INI file
		// as the security file.
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				"L:\\data\\workspaces\\ws_02\\ajf-showcase-simple\\src\\test\\resources\\armony.ini");

		// Setting up the SecurityManager...
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken("e010925",
				"websphere30!");
		Subject user = SecurityUtils.getSubject();
		user.login(token);

		logger.info("User is authenticated:  " + user.isAuthenticated());
	}
}