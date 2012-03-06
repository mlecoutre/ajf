package am.ajf.showcase.core.services;

import static org.junit.Assert.assertTrue;

import am.ajf.core.services.exceptions.ServiceLayerException;
import am.ajf.showcase.lib.model.Person;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
 * PersonServiceTest
 * 
 * @author E010925
 * 
 */
@RunWith(Arquillian.class)
public class PersonServiceTest {

	private static final String DBUNIT_FILE = "dataset.xml";

	private static IDatabaseConnection dbUnitConn;

	@Inject
	private EntityManager em;

	@Inject
	private Logger log;

	@Inject
	private PersonService personService;

	/**
	 * create archive
	 * 
	 * @return arquillian archive
	 */
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

	/**
	 * initTable
	 * 
	 * @throws Exception
	 *             on error
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
						IDataSet dataSet = builder
								.build(PersonServiceTest.class
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
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Test
	public void testAddPerson() throws Exception {
		log.debug("testAddPerson");

		Person person = new Person();
		person.setFirstname("firstname");
		person.setLastname("lastname");
		person.setBirthday(new Date());
		person.setSex('M');
		boolean result = personService.create(person);
		log.debug("Person inserted");

		assertTrue("Person should be created", result);
	}

	/**
	 * testFindall
	 * 
	 * @throws ServiceLayerException
	 *             on error
	 */
	@Test
	public void testFindall() throws ServiceLayerException {
		log.debug("testFindall");

		List<Person> persons = personService.findByLastname("%");
		for (Person p : persons) {
			log.debug("> " + p);
		}
		assertTrue("We should have 5 peoples in the DB", persons.size() == 5);
	}

	/**
	 * testRemovePerson
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Test
	public void testRemovePerson() throws Exception {
		log.debug("testRemovePerson");

		boolean result = personService.removeByPrimaryKey(10l);
		log.debug("Person deleted");

		assertTrue("Person should be remove", result);

		List<Person> persons = personService.findByLastname("%");
		assertTrue("We should have 4 people in db intead of 5 in the DB",
				persons.size() == 4);
	}

}
