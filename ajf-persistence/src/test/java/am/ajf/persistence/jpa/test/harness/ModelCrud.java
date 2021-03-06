package am.ajf.persistence.jpa.test.harness;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@Entity
	@NamedQueries({ 
		@NamedQuery(name = ModelCrud.FIND_BY_NAME, 
			query = "FROM ModelCrud model WHERE model.name = ?1"),
		@NamedQuery(name = ModelCrud.COUNT_BY_NAME, 
			query = "SELECT count(model) FROM ModelCrud model WHERE model.name = ?1"),
		@NamedQuery(name = ModelCrud.FIND_ALL, 
			query = "FROM ModelCrud model")
	})	
public class ModelCrud {

	public static final String FIND_ALL = "ModelCrud.findAll";
	public static final String FIND_BY_NAME = "ModelCrud.findByName";
	public static final String COUNT_BY_NAME = "ModelCrud.countByName";
	
	private Long id;
	private String name;

	public ModelCrud() {}
	
	public ModelCrud(String name) {
		this.name = name;
	}
	
	
	@Id @GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
