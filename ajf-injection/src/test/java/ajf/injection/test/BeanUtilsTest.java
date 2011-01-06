package ajf.injection.test;

import static ajf.utils.BeanUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

import foo.lib.model.MyMock;

public class BeanUtilsTest {
	
	@Test
	public void testInstanciate() throws Exception {
		MyMock beanMock = (MyMock) newInstance(MyMock.class);
		assertTrue("check if injection is working",
				beanMock.getMockDAO() != null);
	}
	
}
