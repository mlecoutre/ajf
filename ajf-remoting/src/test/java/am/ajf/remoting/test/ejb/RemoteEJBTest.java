package am.ajf.remoting.test.ejb;

import java.net.MalformedURLException;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import am.ajf.remoting.ejb.annotation.RemoteEJB;
import am.ajf.remoting.ejb.impl.RemoteEJBHelper;
import am.ajf.remoting.ejb.impl.RemoteEJBImplHandler;
import am.ajf.remoting.test.ejb.harness.Model;
import am.ajf.remoting.test.ejb.harness.RemoteEJBServiceBD;
import am.ajf.remoting.test.ejb.harness.RemotingEjb;
import am.ajf.remoting.test.ejb.harness.RemotingEjbRemote;

@Ignore
@RunWith(Arquillian.class)
public class RemoteEJBTest {
	
	@Inject
	public RemoteEJBServiceBD service;
	
	
	@Deployment
	public static JavaArchive createTestArchiveOwb() {
		return ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(RemotingEjb.class)
				.addClasses(RemotingEjbRemote.class)
				.addClasses(RemoteEJB.class)
				.addClasses(RemoteEJBImplHandler.class)				
				.addClasses(RemoteEJBHelper.class)
				.addClasses(RemoteEJBServiceBD.class)
				.addClasses(Model.class)
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}
	
	@Before
	public void setUp() throws NamingException, MalformedURLException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "am.ajf.remoting.test.ejb.helper.RemotingEJBInitialContextFactory");
	}
	
	@Test
	public void testSimpleEJBCallNoParamNoRes() {
		
		service.emptyRes();
	}

	@Test	
	public void testSimpleEJBCallNoParamStringRes() {
		String res = service.simpleTypeRes();
		Assert.assertNotNull(res);
		Assert.assertEquals("res", res);
	}
	
	@Test
	public void testSimpleEJBCallNoParamComplexTypeRes() {
		Model res = service.complexTypeRes();
		Assert.assertNotNull(res);
		Assert.assertEquals("res", res.getResult());
	}
	
	@Test
	public void testSimpleEJBCall2ParamsWithRes(int p1, int p2) {
		String res = service.resWithParam(1, 2);
		Assert.assertNotNull(res);
		Assert.assertEquals("res(1,2)", res);
	}
}
