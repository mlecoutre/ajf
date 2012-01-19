package am.ajf.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationTest {
	
	private final Logger logger = LoggerFactory.getLogger(AuthenticationTest.class);
	
	public AuthenticationTest() {
		super();
	}
	
	@Before
	public void setUp() {
		
		// instanciate shiro SecurityManager
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        // register the new SecurityManager
        SecurityUtils.setSecurityManager(securityManager);
    		
	}
	
	@Test
	public void testLogin() throws Exception{
		
		Subject currentSubject = SecurityUtils.getSubject();
		
		// has to authenticate the subject
        if (!currentSubject.isAuthenticated()) {
			UsernamePasswordToken authToken = new UsernamePasswordToken("u002617", "myPassword");
			authToken.setRememberMe(true);
			currentSubject.login(authToken);
		}
		
        logger.info("User: {}", currentSubject.getPrincipal().toString());
                
        boolean isAdmin = currentSubject.hasRole("admin");
        logger.info("Is admin: {}", isAdmin);
        
        boolean isAuthorizedOnWelcome = currentSubject.isPermitted("welcome");
        logger.info("Is authorized on 'welcome': {}", isAuthorizedOnWelcome);
        
		//Logout the subject
		currentSubject.logout();
		
	}
	

}
