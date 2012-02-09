package am.ajf.persistence.jpa.test;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.EntityManagerProvider.TransactionType;

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
		Assert.assertEquals(2, pus.size());
	}

}
