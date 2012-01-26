package am.ajf.samples.jpa.was7;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

public interface SamplePolicyBD {

	public ListAllModelsRB listAllModels();
	public void createModelManualTransaction(Model model) throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException;
	public void createModelAutoTransaction(Model model);
	
}
