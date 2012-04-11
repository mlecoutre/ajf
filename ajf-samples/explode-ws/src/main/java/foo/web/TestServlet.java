package foo.web;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import am.ajf.core.utils.BeanUtils;
import foo.service.AService;

public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	@Inject
//	private ASimpleService aSimpleService;
	
	public TestServlet() {
		super();
	}
	
	@PostConstruct
	public void initialize() {
		System.out.println("<< Servlet @PostConstruct invoked.");
		BeanUtils.initialize(this);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("<< Servlet Init invoked.");
	}


	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		
//		try {
//			System.out.println("Invoke Service CDI provided");
//			String aString = aSimpleService.sayHello("vincent");
//			System.out.println("Return: " + aString);
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
//		}
		
		try {
			System.out.println("Invoke EJB");
			Context ctx = new InitialContext();
			AService aService = (AService) ctx.lookup("ejblocal:foo.service.AService");
			String res = aService.doSomething("marcher");
			System.out.println("Return: " + res);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}

	}

	
	
	

}
