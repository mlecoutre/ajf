package am.ajf.injection.harness;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = Model2.FIND_BY_NAME, 
					query = "FROM Model2 model WHERE model.name = :name") ,
				@NamedQuery(name = Model2.FIND_ALL, 
					query = "FROM Model2 model"),
				@NamedQuery(name = Model2.COUNT_ALL, 
					query = "SELECT count(model) FROM Model2 model")})
public class Model2 {

	public static final String FIND_BY_NAME = "Model2.findByName";
	public static final String FIND_ALL = "Model2.findAll";
	public static final String COUNT_ALL = "Model2.countAll";
	
	private Long id;
	private String name;

	public Model2() {}
	
	public Model2(String name) {
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
