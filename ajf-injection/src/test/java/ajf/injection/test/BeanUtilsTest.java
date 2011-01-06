package ajf.injection.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ajf.utils.BeanUtils;
import foo.lib.model.MyMock;

public class BeanUtilsTest {
	
	@Test
	public void testInstanciate() throws Exception {
		MyMock beanMock = (MyMock) BeanUtils.newInstance(MyMock.class);
		assertTrue("check if injection is working",
				beanMock.getMockDAO() != null);
	}
	
}
