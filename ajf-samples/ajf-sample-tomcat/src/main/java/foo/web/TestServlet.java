package foo.web;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import foo.inject.Property;
import foo.lib.MyService;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	MyService myService;
	
	@Inject @Property("aProperty")
	String aProvidedString;
	
	/**
	 * Default constructor.
	 */
	public TestServlet() {
		super();
	}

	@PostConstruct
	public void initialize() {
		System.out.println("Initialized");		
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println(aProvidedString);
		String res = myService.sayHello("Vincent");
		System.out.println(res);
		
	}

}
