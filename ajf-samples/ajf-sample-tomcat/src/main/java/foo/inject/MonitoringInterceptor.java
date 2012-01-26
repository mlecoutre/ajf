package foo.inject;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Monitored
@Interceptor
public class MonitoringInterceptor {
	
	public MonitoringInterceptor() {
		super();
	}
	
	@AroundInvoke
	public Object monitor(InvocationContext ctx) throws Throwable {
		
		Class<? extends Object> targetClass = ctx.getTarget().getClass();
		String serviceName = targetClass.getName();
		String operation = ctx.getMethod().getName();
		
		System.out.println(">> ".concat(serviceName).concat("#").concat(operation).concat("(...)"));
		long start = System.currentTimeMillis();
		
		try {
			Object res = ctx.proceed();
			return res;
		}
		finally {
			long end = System.currentTimeMillis();
			long ellapsed = end - start;
			System.out.println("<< ".concat(serviceName).concat("#").concat(operation).concat("(...) in ").concat(""+ellapsed).concat(" ms."));			
		}
	}
	
}
