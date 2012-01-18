package am.ajf.transaction.test.helper;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class EntityManagerProvider {

	private static EntityManagerFactory emf1;
	private static EntityManagerFactory emf2;
	private static EntityManagerFactory emf3;
	private static EntityManagerFactory emf4;
	private static boolean datasourcesLoaded = false;
	
	public static EntityManagerFactory getEntityManagerFactory1() throws IllegalArgumentException, IllegalAccessException {
		//for (Field field : Status.class.getFields()) {
		//	System.out.println(field.getName() + " : " + field.getInt(null));
		//}
		
		setupDataSources();
			
		if (emf1 == null) {
			emf1 = Persistence.createEntityManagerFactory("hsql-1");
		}
		return emf1;
	}
	
	public static EntityManagerFactory getEntityManagerFactory2() {
		setupDataSources();
		if (emf2 == null) {
			emf2 = Persistence.createEntityManagerFactory("hsql-2");
		}
		return emf2;
	}
	
	public static EntityManagerFactory getEntityManagerFactoryH21() {
		setupDataSources();
		if (emf3 == null) {
			emf3 = Persistence.createEntityManagerFactory("h2-1");
		}
		return emf3;
	}
	
	public static EntityManagerFactory getEntityManagerFactoryH22() {
		setupDataSources();
		if (emf4 == null) {
			emf4 = Persistence.createEntityManagerFactory("h2-2");
		}
		return emf4;
	}
	
	
	public static void setupDataSources() {
		if (!datasourcesLoaded) {										
			PoolingDataSource ds1 = new PoolingDataSource();            
			//XA driver of HSQL is broken so we use the LastResourceCommit XA Wrapper 
			//from bitronix
			//ds1.setClassName("org.hsqldb.jdbc.pool.JDBCXADataSource");			  
			ds1.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");		
			ds1.setUniqueName("jdbc/hsql-1");
			ds1.setMaxPoolSize(5);
			ds1.setAllowLocalTransactions(true);
			ds1.getDriverProperties().setProperty("user", "sa");                                 
			ds1.getDriverProperties().setProperty("password", "");                             
			ds1.getDriverProperties().setProperty("url", "jdbc:hsqldb:mem:unit-testing-transaction-1");
			ds1.getDriverProperties().setProperty("driverClassName", "org.hsqldb.jdbcDriver");
			ds1.init();
			
			PoolingDataSource ds2 = new PoolingDataSource();                                         
			//ds2.setClassName("org.hsqldb.jdbc.pool.JDBCXADataSource");
			ds2.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");	
			ds2.setUniqueName("jdbc/hsql-2");
			ds2.setMaxPoolSize(5);
			ds2.setAllowLocalTransactions(true);
			ds2.getDriverProperties().setProperty("user", "sa");                                 
			ds2.getDriverProperties().setProperty("password", "");                             
			ds2.getDriverProperties().setProperty("url", "jdbc:hsqldb:mem:unit-testing-transaction-2");
			ds2.getDriverProperties().setProperty("driverClassName", "org.hsqldb.jdbcDriver");
			ds2.init();
			
			PoolingDataSource ds3 = new PoolingDataSource();                                         
			ds3.setClassName("org.h2.jdbcx.JdbcDataSource");	
			ds3.setUniqueName("jdbc/h2-1");
			ds3.setMaxPoolSize(5);
			ds3.setAllowLocalTransactions(true);
			ds3.getDriverProperties().setProperty("user", "sa");                                 
			ds3.getDriverProperties().setProperty("password", "sa");                             
			ds3.getDriverProperties().setProperty("URL", "jdbc:h2:mem:unit-testing-transaction-3;DB_CLOSE_DELAY=-1");		
			ds3.init();
			
			PoolingDataSource ds4 = new PoolingDataSource();                                         
			ds4.setClassName("org.h2.jdbcx.JdbcDataSource");	
			ds4.setUniqueName("jdbc/h2-2");
			ds4.setMaxPoolSize(5);
			ds4.setAllowLocalTransactions(true);
			ds4.getDriverProperties().setProperty("user", "sa");                                 
			ds4.getDriverProperties().setProperty("password", "sa");                             
			ds4.getDriverProperties().setProperty("URL", "jdbc:h2:mem:unit-testing-transaction-4;DB_CLOSE_DELAY=-1");		
			ds4.init();
			
			datasourcesLoaded = true;
		}
	}
	
}
