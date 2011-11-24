package ajf.configuration;


public enum EnumEventType {
	ADD_PROPERTY("ADD_PROPERTY", 1),
	CLEAR_PROPERTY("CLEAR_PROPERTY", 2),
	SET_PROPERTY("SET_PROPERTY", 3),
	CLEAR("CLEAR", 4);
	
	private String readableValue = "";
	private int value;
	
	private EnumEventType(String readableValue, int value) {
		this.readableValue = readableValue;
		this.value=value;
	}

	public static EnumEventType getEnum(String param){
		EnumEventType[] liste =  EnumEventType.values();
		for (EnumEventType e : liste){
			if (e.getReadableValue().equals(param)){
				return e;
			}
		}
		return null;
	}
	
	public static EnumEventType getEnum(int param){
		EnumEventType[] liste =  EnumEventType.values();
		for (EnumEventType e : liste){
			if (e.getValue() == param){
				return e;
			}
		}
		return null;
	}

	public String getReadableValue() {
		return readableValue;
	}

	public int getValue() {
		return value;
	}
	
		
}
