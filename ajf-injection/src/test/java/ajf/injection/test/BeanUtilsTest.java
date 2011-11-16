package ajf.injection.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ajf.utils.BeanUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import foo.lib.model.MyMock;

public class BeanUtilsTest {
	
	@Test
	public void testInstanciate() throws Exception {
		MyMock beanMock = (MyMock) BeanUtils.newInstance(MyMock.class);
		assertTrue("check if injection is working",
				beanMock.getMockDAO() != null);
	}
	
	@Test
	public void testSimpleInjection() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(MyMock.class);
			}
		});
		MyMock myMock = injector.getInstance(MyMock.class);
		System.out.println(myMock);
	}
	
}
