package ajf.persistence.jpa.test.harness;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = Model1.FIND_BY_NAME, 
					query = "FROM Model1 model WHERE model.name = :name") ,
				@NamedQuery(name = Model1.FIND_ALL, 
					query = "FROM Model1 model"),
				@NamedQuery(name = Model1.COUNT_ALL, 
					query = "SELECT count(model1) FROM Model1 model")})
public class Model1 {

	public static final String FIND_BY_NAME = "Model1.findByName";
	public static final String FIND_ALL = "Model1.findAll";
	public static final String COUNT_ALL = "Model1.countAll";
	
	private Long id;
	private String name;

	public Model1() {}
	
	public Model1(String name) {
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
