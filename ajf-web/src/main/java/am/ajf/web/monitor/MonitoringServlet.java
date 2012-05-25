package am.ajf.web.monitor;

import am.ajf.core.logger.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * Default Monitoring Servlet Delegate
 * 
 * @author u002617
 */
public class MonitoringServlet extends HttpServlet {

	private static HttpServlet monitoringServlet = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The response content type: text/html
	 */
	private static final String CONTENT_TYPE = "text/html";
	private static final String DEFAULT_MONITORING_SERVLET = "ajf.monitoring.web.WASMonitoringServlet";
	private static final long MBYTES = 1024 * 1024;
	private static final String MONITORING_SERVLET_CLASS_PARAMETER = "MonitoringServletClass";

	private static final String STATUS_OK = "OK";

	private Logger logger = LoggerFactory.getLogger(MonitoringServlet.class);

	/**
	 * Default constructor
	 */
	public MonitoringServlet() {
		super();
	}

	/**
	 * Return "OK".
	 * 
	 * @param request
	 *            a <code>HttpServletRequest</code> value
	 * @param response
	 *            a <code>HttpServletResponse</code> value
	 * @throws ServletException
	 *             if an error occurs
	 * @throws IOException
	 *             if an error occurs
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (null != monitoringServlet) {
			try {
				monitoringServlet.service(request, response);
				return;
			} catch (Exception e) {
				logger.error("Monitoring delegate failure", e);
			}
		}

		PrintWriter out = response.getWriter();
		response.setContentType(CONTENT_TYPE);

		/* Partie Declarative */
		String parameter = request.getQueryString();
		String result = STATUS_OK;

		/* Partie Executive */
		// Test le mode de la servlet (Surveillance ou Informations)
		if (parameter != null) {
			if ((parameter.indexOf("monitor") < 0)
					|| (parameter.indexOf("news") >= 0)) {
				// Mode Information
				result = doModeInformation();
			}
		}
		out.write(result);
		out.flush();

	}

	/**
	 * @return HTML page to display
	 */
	protected String doModeInformation() {

		/* Partie Declarative */
		StringBuffer result = new StringBuffer(
				"<HTML><HEAD><TITLE>Monitoring Servlet</TITLE></HEAD>")
				.append("<BODY><CENTER><P><H1>Monitoring Servlet</H1></P><BR><P><TABLE>");
		result.append("<TR><TD bgcolor='#00FF00'><P align ='center'>").append(
				"<B>&nbspJava Virtual Machine&nbsp</B><BR><SMALL>");

		result.append("Total Memory: ")
				.append(Runtime.getRuntime().totalMemory() / MBYTES)
				.append(" Mo<BR>");
		result.append("Free Memory: ")
				.append(Runtime.getRuntime().freeMemory() / MBYTES)
				.append(" Mo<BR>");
		result.append("Max Memory: ")
				.append(Runtime.getRuntime().maxMemory() / MBYTES)
				.append(" Mo<BR>");

		result.append("</SMALL></P></TD></TR>");
		result.append("</TABLE></P><P><B>Contact: </B>");
		result.append(getInitParameter("contact"));
		result.append("</P></CENTER></BODY></HTML>");

		return result.toString();

	}

	/**
	 * Initialize HttpServlet
	 * 
	 * @param config
	 *            ServletConfig
	 * @throws ServletException
	 *             ServletException
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 * 
	 */
	public void init(ServletConfig config) throws ServletException {

		String className = DEFAULT_MONITORING_SERVLET;

		String param = config
				.getInitParameter(MONITORING_SERVLET_CLASS_PARAMETER);
		if ((null != param) && (!param.trim().isEmpty())) {
			try {
				Class.forName(param);
				className = param;
			} catch (ClassNotFoundException e) {
				logger.error(
						"Unable to find class ".concat(className).concat(
								" for Monitoring Delegate"), e);
			}
		}

		try {
			monitoringServlet = (HttpServlet) Class.forName(className)
					.newInstance();
			monitoringServlet.init(config);
			logger.info("Delegate Monitoring to ".concat(className));
		} catch (Exception e) {
			logger.error(
					"Delegate Monitoring to ".concat(className).concat(
							" failure"), e);
		}

	}

}