package ajf.persistence.jpa.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ajf.persistence.jpa.EntityManagerProvider;
import ajf.persistence.jpa.EntityManagerProvider.TransactionType;

public class EntityManagerProviderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTransactionType() {
		TransactionType ttype = EntityManagerProvider.getTransactionType("default");
		Assert.assertNotNull(ttype);
		Assert.assertEquals(TransactionType.LOCAL, ttype);
	}

	@Test
	public void testGetPersistenceUnitNames() {
		Set<String> pus = EntityManagerProvider.getPersistenceUnitNames();
		Assert.assertNotNull(pus);
		Assert.assertEquals(1, pus.size());
	}

}
