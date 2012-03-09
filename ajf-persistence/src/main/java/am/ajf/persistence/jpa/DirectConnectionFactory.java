package am.ajf.persistence.jpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DirectConnectionFactory implements PersistenceConnectionFactory {

	private final String driverName;
	private final String url;
	
	private final String user;
	private final String password;

	public DirectConnectionFactory(String driverName, String url, String user,
			String password) {

		super();

		this.driverName = driverName;
		this.url = url;
		this.user = user;
		this.password = password;

	}

	public String getUrl() {
		return url;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public Connection getConnection() throws ClassNotFoundException, SQLException {

		// can throw ClassNotFoundException
		Class.forName(getDriverName());

		// can throw SQLException
		Connection con = DriverManager.getConnection(getUrl(), getUser(),
				getPassword());
		return con;

	}

}
