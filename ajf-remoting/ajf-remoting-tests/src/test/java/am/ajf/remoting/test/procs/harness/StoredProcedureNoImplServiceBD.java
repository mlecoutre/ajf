package am.ajf.remoting.test.procs.harness;

import java.util.List;

import am.ajf.remoting.Remote;
import am.ajf.remoting.procs.annotation.In;
import am.ajf.remoting.procs.annotation.StoredProcedure;

@Remote(jndi="java:comp/env/jdbc/remoting")
public interface StoredProcedureNoImplServiceBD {

	@StoredProcedure(name="ZZTESTNOPARAM")
	List<ModelSp> findAllModels();
	
	@StoredProcedure(name="ZZTESTWITHPARAM")
	List<ModelSp> findAllModelsByName(@In("name") String name);
	
	@StoredProcedure(name="ZZTESTONEWITHPARAM")
	ModelSp findOneModelByName(String name);
	
}
