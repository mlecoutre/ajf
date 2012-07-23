package am.ajf.persistence.jpa;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceConnectionFactory implements
		PersistenceConnectionFactory {

	private final String dataSourceName;

	transient private DataSource dataSource = null;

	public DataSourceConnectionFactory(String dataSourceName) {
		super();
		this.dataSourceName = dataSourceName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public synchronized Connection getNewConnection() throws NamingException, SQLException {

		if (null == dataSource) {
			// new context
			Context ctx = new InitialContext();
			// lookup and register the DataSource 
			Object dt = ctx.lookup(dataSourceName);
			dataSource = (DataSource) dt;
			// close the context
			ctx.close();
			
		}
		
		Connection con = dataSource.getConnection();
		return con;
		
	}

}
