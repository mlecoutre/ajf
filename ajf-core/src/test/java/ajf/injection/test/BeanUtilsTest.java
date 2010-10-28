package ajf.injection.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ajf.injection.test.model.MyMock;
import ajf.utils.BeanUtils;

public class BeanUtilsTest {

	
	@Test
	public void testInstanciate() throws Exception {
		MyMock beanMock = (MyMock) BeanUtils.instanciate(MyMock.class);
		assertTrue("check if injection is working",
				beanMock.getMockDAO() != null);
	}
}
