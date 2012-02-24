package am.ajf.persistence.jpa.test.helper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import am.ajf.persistence.jpa.EntityManagerProvider;

public class DBHelper {
		
	
	public static void executeSQLInTransaction(String pu, final String... sqls) throws Exception {
		EntityManagerFactory emf = EntityManagerProvider.createEntityManagerFactory(pu);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Session session = (Session) em.getDelegate(); 
		session.doWork(new Work() {			
			@Override
			public void execute(Connection con) throws SQLException {
				for (String sql : sqls) {
					Statement stmt = con.createStatement();
					stmt.executeUpdate(sql);
					stmt.close();
				}				
			}
		});
		em.getTransaction().commit();		
	}

}
