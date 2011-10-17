package ajf.datas;

import java.io.Serializable;
import java.util.Set;

/**
 * @author u002617
 */
public interface AuditData extends Serializable {

	static final String KEY_UUID = "uuid";
	static final String KEY_USERID = "userid";
	static final String KEY_DATE = "date";

	/**
	 * Get Audit data value.
	 * @param key ID to get specific data.
	 * @return Object value in the audit Data
	 */
	Object get(String key);
	
	/**
	 * Get Audit data value.
	 * @param key ID to get specific data.
	 * @return String value in the audit Data
	 */
	String getString(String key);
	
	/**
	 * 
	 * @return list of string, list of keys fill in the Audit Data object.
	 */
	Set<String> getKeys();

}
