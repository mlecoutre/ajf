package am.ajf.core.datas;


public interface EditableAuditData extends AuditData {

	/**
	 * Put a value for an audit data.
	 * Predefine key are available in using AuditData.KEY_XXXX
	 * @param key audit data key
	 * @param value value to push in the audit data.
	 */
	public void put(String key, Object value);
	
	/**
	 * clean the AuditData element
	 */
	public void clear();

}
