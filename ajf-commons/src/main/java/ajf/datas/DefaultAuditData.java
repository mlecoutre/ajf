package ajf.datas;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.MDC;

/**
 * AuditData implementation Predefined key are available in AuditData.KEY_XXXX
 * 
 * @author u002617
 */
public class DefaultAuditData implements EditableAuditData {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(DefaultAuditData.class.getName());

	private Map<String, Object> map = null;

	/*
	 * 
	 */
	public DefaultAuditData() {
		map = new HashMap<String, Object>();
		// generate correlation ID if needed
		try {
			this.put(AuditData.KEY_DATE, new Date());			
			String uuid = UUID.randomUUID().toString();
			this.put(AuditData.KEY_UUID, uuid);
		}
		catch (Throwable e) {
			logger.log(
					Level.WARNING,
					"Unable to generate UUID in AuditData, because "
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.datas.AuditData#get(java.lang.String)
	 */
	public Object get(String key) {
		Object value = map.get(key);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.datas.AuditData#getString(java.lang.String)
	 */
	public String getString(String key) {
		Object value = map.get(key);
		if (value instanceof String) 
			return (String) value;
		return String.valueOf(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.datas.EditableAuditData#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object value) {
		map.put(key, value);
		// propagate in the logging
		if ((null != value) && (value instanceof String))
			MDC.put(key, (String) value);				
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.datas.EditableAuditData#clear()
	 */
	public void clear() {
		map.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.datas.AuditData#getKeys()
	 */
	public Set<String> getKeys() {
		return map.keySet();
	}

	@Override
	public String toString() {
		return "DefaultAuditData [map=" + map + "]";
	}

}
