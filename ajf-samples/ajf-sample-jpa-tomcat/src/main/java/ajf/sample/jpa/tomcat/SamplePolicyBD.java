package ajf.sample.jpa.tomcat;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;


public interface SamplePolicyBD {

	ListAllModelsRB listAllModelsCrud();
	ListAllModelsRB listAllModelsCustom();
	void createModelCustomCrud();
	void createModelManual() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException;
	void createModelAutoCrud();
	
}
