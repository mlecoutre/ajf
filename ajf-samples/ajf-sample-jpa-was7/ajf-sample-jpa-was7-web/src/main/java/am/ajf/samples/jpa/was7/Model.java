package am.ajf.samples.jpa.was7;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = Model.FIND_BY_NAME, 
					query = "SELECT model FROM Model model WHERE model.name = :name") ,
				@NamedQuery(name = Model.FIND_ALL, 
					query = "SELECT model FROM Model model"),
				@NamedQuery(name = Model.COUNT_ALL, 
					query = "SELECT count(model) FROM Model model")})
public class Model {

	public static final String FIND_BY_NAME = "Model.findByName";
	public static final String FIND_ALL = "Model.findAll";
	public static final String COUNT_ALL = "Model.countAll";
	
	private Long id;
	private String name;

	public Model() {
		super();
	}
	
	public Model(String name) {
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
