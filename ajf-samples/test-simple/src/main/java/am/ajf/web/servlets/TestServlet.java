package am.ajf.web.servlets;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.realm.GenericPrincipal;

public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("Is secure: " + req.isSecure());
		System.out.println("RemoteUser: " + req.getRemoteUser());
		
		Principal userPrincipal = req.getUserPrincipal();
		System.out.println("UserPrincipal class: " + userPrincipal.getClass().getName());
		System.out.println("UserPrincipal: " + userPrincipal);
		
		System.out.println("Is Admin ? " + req.isUserInRole("admin"));
		System.out.println("Is IT-User ? " + req.isUserInRole("IT-User"));
		
		/*
		HttpSession session = req.getSession();
		if (null != session) {
			
			Enumeration<?> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				Object value = session.getAttribute(name);
				System.out.println(name + " = " + value);
			}
			
			
			Subject subject = (Subject) session.getAttribute("javax.security.auth.subject");
			System.out.println(subject);
		}
		*/
		
		// only if java 2 is activated
		AccessControlContext acc = null;
		try {
			acc = (AccessControlContext) System.getSecurityManager().getSecurityContext();
			Subject subject = Subject.getSubject(acc);
			System.out.println("Subject: " + subject);
			
			Object p = subject.getPrincipals(GenericPrincipal.class);
			System.out.println("Generic Principal: " + p);
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		//session.invalidate();
				
	}

}
