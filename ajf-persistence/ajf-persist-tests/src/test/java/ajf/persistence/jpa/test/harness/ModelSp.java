package ajf.persistence.jpa.test.harness;

public class ModelSp {
	
	private Long id;
	private String name;
	
	public ModelSp() {}

	public ModelSp(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
