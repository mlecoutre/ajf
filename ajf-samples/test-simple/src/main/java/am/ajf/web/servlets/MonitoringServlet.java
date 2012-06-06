package am.ajf.web.servlets;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class MonitoringServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MonitoringServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

			try {
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				
				Set<ObjectInstance> set = mBeanServer.queryMBeans(new ObjectName("java.lang:type=Memory"), null);
				ObjectInstance oi = set.iterator().next();
				
				Object attrValue = mBeanServer.getAttribute(oi.getObjectName(), "HeapMemoryUsage");
				if(  attrValue instanceof CompositeData )  {
					System.out.println(((CompositeData)attrValue).get("used").toString());
				}
				/*
				 * http://stackoverflow.com/questions/7571160/custom-mbean-in-tomcat-cannot-find-javaurlcontextfactory-when-creating-initial
				 * http://tomcat.apache.org/tomcat-7.0-doc/mbeans-descriptor-howto.html
				 * https://blogs.oracle.com/lmalventosa/entry/jmx_authentication_authorization
				 */
				// for remote
				/*
				 * 2020 or 9012
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://{remote ip/localhost}:2020/jmxrmi");
			      JMXConnector jmxc = JMXConnectorFactory.connect(url);
			      MBeanServerConnection server = jmxc.getMBeanServerConnection();
			      Object o = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
			      CompositeData cd = (CompositeData) o;
			      System.out.println(cd.get("used"));
				*/
				
				//tomcat.jdbc:name="jdbc/dcDB",context=/test-simple,type=ConnectionPool,host=defaulthost,class=org.apache.tomcat.jdbc.pool.DataSource
				Set<ObjectInstance> dataSourcesSet = mBeanServer.queryMBeans(new ObjectName("tomcat.jdbc:type=ConnectionPool,*"), null);
				for (ObjectInstance objectInstance : dataSourcesSet) {
					ObjectName objectName = objectInstance.getObjectName();
					Object attributeValue = mBeanServer.getAttribute(objectName, "Url");
					System.out.println(attributeValue);
				}
				
				
				Context ctx = new InitialContext();
				DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/dcDB");
				Connection conn = dataSource.getConnection();
				
				conn.close();
								
				System.out.println("Something");
								
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	
	
}
