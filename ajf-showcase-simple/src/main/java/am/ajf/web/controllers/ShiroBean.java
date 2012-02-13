package am.ajf.web.controllers;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@SessionScoped
public class ShiroBean implements Serializable {

	private static final transient Logger log = LoggerFactory
			.getLogger(ShiroBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

	public ShiroBean() {
		super();
	}

	public void doLogin() {

		log.debug("doLogin " + username);
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password);
		Subject user = SecurityUtils.getSubject();
		user.login(token);
		log.debug("is Authenticated: " + user.isAuthenticated());

	}
	
	public String doLogout() {
		Subject currentSubject = SecurityUtils.getSubject();
		log.debug("logout");
		currentSubject.logout();
		return "/index.jsf";
	}

	public boolean isAllowed(String role) {
		Subject currentSubject = SecurityUtils.getSubject();
		boolean isAllowed = currentSubject.hasRole("admin");

		log.debug(currentSubject.getPrincipal() + " for role " + role + " : "
				+ isAllowed);
		return isAllowed;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
