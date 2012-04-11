package foo.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import am.ajf.core.utils.BeanUtils;
import foo.service.AService;
import foo.service.ASimpleService;

@Stateless(name = "ATestServiceBean")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AServiceBean implements AService {

	@Resource
	private SessionContext context;
	
	@Inject
	private ASimpleService aSimpleService;
	
	public AServiceBean() {
		super();
	}

	@PostConstruct
	public void initialize() {
		System.out.println("<< EJB @PostConstruct invoked.");
		System.out.println("Context: " + context);
		try {
			System.out.println("Inject Dependencies");
			BeanUtils.initialize(this);
			System.out.println("Inject Dependencies: Done.");
			
			System.out.println("Context: " + context);
			
		} catch (Exception e) {
			System.out.println("Inject Dependencies: Exception");
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	@Override
	public String doSomething(String action) {
		
		System.out.println("<< EJB doSomething invoked.");
		
		if (null != aSimpleService) {
			try {
				System.out.println("Invoke Service CDI provided");
				String aString = aSimpleService.sayHello("vincent");
				System.out.println("Return: " + aString);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		
		return action + " Done.";
	}

}
