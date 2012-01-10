package ajf.persistence.jpa.test.harness;

import java.util.List;

import ajf.persistence.jpa.annotation.StoredProcedure;

public interface StoredProcedureNoImplServiceBD {

	@StoredProcedure(name="ZZTESTNOPARAM", resultClass=ModelSp.class)
	List<ModelSp> findAllModels();
	
	@StoredProcedure(name="ZZTESTWITHPARAM", resultClass=ModelSp.class)
	List<ModelSp> findAllModelsByName(String name);
	
	@StoredProcedure(name="ZZTESTONEWITHPARAM", resultClass=ModelSp.class)
	ModelSp findOneModelByName(String name);
	
}
