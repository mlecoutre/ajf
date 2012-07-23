package am.ajf.security.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author U002617
 */
public class UserPrincipalFilter implements Filter {

	/**
	 * 
	 */
	public UserPrincipalFilter() {
		super();
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// store the remote user principal
		HttpServletRequest req = (HttpServletRequest) request;
		am.ajf.security.Principal.setUserPrincipal(req.getUserPrincipal()); 
		
		// invoke j_security_check
		chain.doFilter(request, response);
		
		// clear the principal
		am.ajf.security.Principal.setUserPrincipal(null);		

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// initialize method	
	}

	@Override
	public void destroy() {
		// destroy method		
	}

}
