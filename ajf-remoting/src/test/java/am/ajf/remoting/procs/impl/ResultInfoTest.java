package am.ajf.remoting.procs.impl;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import am.ajf.remoting.ConfigurationException;
import am.ajf.remoting.procs.annotation.Out;
import am.ajf.remoting.procs.annotation.Result;

/**
 * Test the ResultInfo class.
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ResultInfoTest {

	@Test
	public void test() throws ConfigurationException {
		ResultInfo ri = new ResultInfo(ResultSimple.class, Void.TYPE);
		Assert.assertNotNull(ri);
		Assert.assertEquals(true, ri.isResultList());
		Assert.assertEquals(false, ri.isResultNull());
		Assert.assertEquals(true, ri.isResultWrapped());
		Assert.assertEquals(2, ri.getOutParameters().size());		
	}
	
	@SuppressWarnings("unused")
	public class ResultSimple {		
		private @Out("out1") String out1;
		private @Out("out2") String out2;
		private @Result List<Model> result;		
	}
	
	public class Model {}

}
