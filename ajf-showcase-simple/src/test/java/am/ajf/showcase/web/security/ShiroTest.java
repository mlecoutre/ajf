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
		String file = this.getClass().getResource("/shiro.ini").getFile();
		logger.debug(file);
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				file);

		// Setting up the SecurityManager...
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "psw");
		Subject user = SecurityUtils.getSubject();
		user.login(token);

		logger.info("User is authenticated:  " + user.isAuthenticated());
	}

	//@Test TODO does not work TODAY
	public void testArmony() {
		// Using the IniSecurityManagerFactory, which will use the an INI file
		// as the security file.

		String file = this.getClass().getResource("/armony.ini").getFile();
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				file);

		// Setting up the SecurityManager...
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken("was-reader",
				"re@dPwd0!");
		Subject user = SecurityUtils.getSubject();
		user.login(token);

		logger.info("User is authenticated:  " + user.isAuthenticated());
	}
}