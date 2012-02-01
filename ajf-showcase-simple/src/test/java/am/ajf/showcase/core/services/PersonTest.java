package am.ajf.showcase.core.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import ajf.logger.injection.InjectLogger;
import ajf.testing.junit.DITestRunnner;
import am.ajf.showcase.lib.model.Person;

@RunWith(DITestRunnner.class)
public class PersonTest {

	@InjectLogger
	Logger log;

	@PersistenceUnit(name = "default")
	private EntityManagerFactory emf;

	private static IDatabaseConnection connection;
	private static IDataSet dataset;

	private static String _dbFile = "dataset.xml";
	// private static String _driverClass =
	// "org.apache.derby.jdbc.EmbeddedDriver";
	private static String _jdbcConnection = "jdbc:derby:showcase";

	@BeforeClass
	public static void initEntityManager() throws Exception {

		try {

			connection = getConnection();
			// connection = new DatabaseConnection(((EntityManagerImpl)
			//
			// (em.getDelegate())).getServerSession().getAccessor().getConnection());
			dataset = new FlatXmlDataSet(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(_dbFile));

			DatabaseOperation.CLEAN_INSERT.execute(connection, dataset);
		} catch (Throwable th) {

			th.printStackTrace();
		}
	}

	@AfterClass
	public static void closeEntityManager() throws Exception {

		connection.close();
	}

	@Test
	public void testAddPerson() {
		log.debug("testAddPerson");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Person person = new Person();
		person.setFirstname("firstname");
		person.setLastname("lastname");
		person.setBirthday(new Date());
		person.setSex('M');
		em.persist(person);

		tx.commit();

	}

	@Test
	public void testFindall() {
		log.debug("testFindall");
		EntityManager em = emf.createEntityManager();
		@SuppressWarnings("unchecked")
		List<Person> persons = em.createNamedQuery(Person.FIND_ALL)
				.getResultList();
		for (Person p : persons) {
			log.debug("> " + p);
		}

	}

	public static IDatabaseConnection getConnection()
			throws ClassNotFoundException, DatabaseUnitException, SQLException {
		// database connection
		// Class driverClass = Class.forName(_driverClass);
		Connection jdbcConnection = DriverManager
				.getConnection(_jdbcConnection);
		return new DatabaseConnection(jdbcConnection);
	}
}
