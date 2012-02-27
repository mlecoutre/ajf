package am.ajf.remoting.test.procs.helper;

import java.sql.Connection;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class DBHelper {
	
	private static boolean datasourcesLoaded;
	
	public static void setupDataSources() {
		if (!datasourcesLoaded) {	
			PoolingDataSource ds = new PoolingDataSource();
			ds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");		
			ds.setUniqueName("java:comp/env/jdbc/remoting");
			ds.setMaxPoolSize(5);
			ds.setAllowLocalTransactions(true);
			ds.getDriverProperties().setProperty("user", "sa");                                 
			ds.getDriverProperties().setProperty("password", "");                             
			ds.getDriverProperties().setProperty("url", "jdbc:hsqldb:mem:unit-testing-remoting");
			ds.getDriverProperties().setProperty("driverClassName", "org.hsqldb.jdbcDriver");
			ds.init();
			datasourcesLoaded = true;
		}
	}
	
	public static void executeSQLInTransaction(String... sqls) throws Exception {
		InitialContext ctx = new InitialContext();
		UserTransaction utx = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
		utx.begin();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/remoting");
		Connection con = ds.getConnection();
		for (String sql : sqls) {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}
		con.close();
		utx.commit();		
	}

}
