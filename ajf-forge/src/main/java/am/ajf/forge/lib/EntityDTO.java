package am.ajf.forge.lib;

import java.util.List;

public class EntityDTO {

	private String entityName;
	private String entityLibPackage;
	private List<String> entityAttributeList;

	public EntityDTO() {
		super();
	}

	public String getEntityLibPackage() {
		return entityLibPackage;
	}

	public void setEntityLibPackage(String entityLibPackage) {
		this.entityLibPackage = entityLibPackage;
	}

	public List<String> getEntityAttributeList() {
		return entityAttributeList;
	}

	public void setEntityAttributeList(List<String> entityAttributeList) {
		this.entityAttributeList = entityAttributeList;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}
