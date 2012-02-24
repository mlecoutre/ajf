package am.ajf.persistence.jpa.test.harness;

import java.util.List;

public interface ManualServiceBD {
	
	public List<ModelManual> findByNameOrderBy(String name, String order);
	public void insertNew(ModelManual model);

}
