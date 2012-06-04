package am.ajf.forge.lib;

import java.util.List;

public class EntityDTO {

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

}
