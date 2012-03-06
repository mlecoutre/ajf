package am.ajf.persistence.jpa;

import java.sql.Connection;

public interface PersistenceConnectionFactory {
	
	/**
	 * get a Connection on the target database
	 * @return a SQL connection
	 * @throws Exception 
	 */
	Connection get() throws Exception;
	
}
