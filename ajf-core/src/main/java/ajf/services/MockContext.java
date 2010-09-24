package ajf.services;

import java.util.HashMap;
import java.util.Map;

/**
 * manage Mocks in ThreadLocal
 * @author vincent
 *
 */
public abstract class MockContext {
	
	// a ThreadLocal of mocks
	private static ThreadLocal<Map<String, Object>> mocksMap = new ThreadLocal<Map<String,Object>>();
	
	/**
	 * bind a Mock
	 * @param mockedClass
	 * @param mock
	 */
	public static void bind(Class<?> mockedClass, Object mock) {
		Map<String, Object> map = mocksMap.get();
		if (null == map) {
			map = new HashMap<String, Object>();
			mocksMap.set(map);
		}
		map.put(mockedClass.getName(), mock);
	}
	
	/**
	 * lookup a Mock
	 * @param <T>
	 * @param mockedClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lookup(Class<?> mockedClass) {
		Map<String, Object> map = mocksMap.get();
		if (null == map) 
			return null;
		Object bean = map.get(mockedClass.getName());
		return (T) bean;
	}
	
	/**
	 * reset a Mock context
	 */
	public static void reset() {
		Map<String, Object> map = mocksMap.get();
		if (null == map) 
			return ;
		map.clear();
	}
	
	
}
