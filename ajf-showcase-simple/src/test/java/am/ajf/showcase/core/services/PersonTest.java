package am.ajf.showcase.core.services;

import static org.junit.Assert.assertTrue;

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

import am.ajf.showcase.lib.model.Person;

@RunWith(Arquillian.class)
public class PersonTest {

	@Inject
	Logger log;

	@Inject
	private EntityManager em;

	private static IDatabaseConnection dbUnitConn;
	// private static IDataSet dataset;

	private static String _dbFile = "dataset.xml";

	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addPackages(true, "am.ajf.showcase")
				.addPackages(true, "am.ajf.injection")
				.addPackages(true, "am.ajf.core.logger")
				.addPackages(true, "am.ajf.core.helper")
				.addPackages(true, "am.ajf.core.utils")
				.addPackages(true, "am.ajf.persistence")
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/persistence.xml",
						ArchivePaths.create("persistence.xml"));
	}

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
								.getResourceAsStream(_dbFile));

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
