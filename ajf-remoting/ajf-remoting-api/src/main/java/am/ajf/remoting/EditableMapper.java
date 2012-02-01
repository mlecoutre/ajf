package am.ajf.remoting;

public abstract class EditableMapper {
	
	private Class<?> entity;

	public Class<?> getEntity() {
		return entity;
	}

	public void setEntity(Class<?> entity) {
		this.entity = entity;
	}
}
