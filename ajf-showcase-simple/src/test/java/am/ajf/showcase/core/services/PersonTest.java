package am.ajf.showcase.core.services;

import static org.junit.Assert.assertTrue;

import am.ajf.injection.AuditDataProducer;
import am.ajf.injection.CacheInterceptor;
import am.ajf.injection.CacheProducer;
import am.ajf.injection.ConfigurationProducer;
import am.ajf.injection.ErrorHandlingInterceptor;
import am.ajf.injection.LoggerProducer;
import am.ajf.injection.MonitoringInterceptor;
import am.ajf.injection.PropertyProducer;
import am.ajf.injection.TransactionInterceptor;
import am.ajf.showcase.lib.model.Person;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

/**
 * PersonTest
 * 
 * @author E010925
 * 
 */
@RunWith(Arquillian.class)
public class PersonTest {

	private static IDatabaseConnection dbUnitConn;
	// private static IDataSet dataset;

	private static final String DBUNIT_FILE = "dataset.xml";

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	/**
	 * Arquillian deployer
	 * 
	 * @return archive
	 */
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addPackages(true, "am.ajf.showcase")
				.addClasses(AuditDataProducer.class)
				.addClasses(LoggerProducer.class)
				.addClasses(CacheProducer.class)
				.addClasses(PropertyProducer.class)
				.addClasses(ConfigurationProducer.class)
				.addClasses(MonitoringInterceptor.class)
				.addClasses(TransactionInterceptor.class)
				.addClasses(CacheInterceptor.class)
				.addClasses(ErrorHandlingInterceptor.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml",
						ArchivePaths.create("persistence.xml"));

	}

	/**
	 * initTable
	 * 
	 * @throws Exception
	 */
	@Before
	public void initTable() throws Exception {

		try {
			Session session = ((Session) em.getDelegate());
			session.doWork(new Work() {

				@Override
				public void execute(Connection connection) throws SQLException {

					try {

						dbUnitConn = new DatabaseConnection(connection);

						FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
						builder.setColumnSensing(true);
						IDataSet dataSet = builder.build(PersonTest.class
								.getResourceAsStream(DBUNIT_FILE));

						DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn,
								dataSet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			// dbUnitConn.close();
		} catch (Throwable th) {

			th.printStackTrace();
		}
	}

	/**
	 * testAddPerson
	 */
	@Test
	public void testAddPerson() {
		log.debug("testAddPerson");

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Person person = new Person();
		person.setFirstname("firstname");
		person.setLastname("lastname");
		person.setBirthday(new Date());
		person.setSex('M');
		em.persist(person);
		log.debug("PersonId " + person.getPersonid() + " inserted");

		tx.commit();
		assertTrue("PersonId should not be null", null != person.getPersonid());
	}

	/**
	 * testFindall
	 */
	@Test
	public void testFindall() {
		log.debug("testFindall");

		@SuppressWarnings("unchecked")
		List<Person> persons = em.createNamedQuery(Person.FIND_ALL)
				.getResultList();
		for (Person p : persons) {
			log.debug("> " + p);
		}
		assertTrue("We should have 5 peoples in the DB", persons.size() == 5);
	}

}
