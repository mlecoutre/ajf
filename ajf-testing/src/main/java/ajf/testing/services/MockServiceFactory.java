package ajf.testing.services;

import java.util.HashMap;
import java.util.Map;

import ajf.services.ServiceFactory;

/**
 * manage Mocks in ThreadLocal
 * 
 * @author vincent
 * 
 */
public class MockServiceFactory implements ServiceFactory {

	// a ThreadLocal of mocks
	private static final ThreadLocal<Map<String, Object>> mocksMap = new ThreadLocal<Map<String, Object>>();
	
	//private static final MockServiceFactory instance = new MockServiceFactory();
	
	public MockServiceFactory() {
		super();
	}
	
	/*
	public static MockServiceFactory getInstance() {
		return instance;
	}
	*/

	/**
	 * bind a Mock
	 * 
	 * @param mockedClass
	 * @param mock
	 */
	public static void bind(Class<?> mockedClass, Object mock) {
		
		Map<String, Object> map = mocksMap.get();
		if (null == map) {
			map = new HashMap<String, Object>();
			mocksMap.set(map);
		}
		// could rebind service implementation
		map.put(mockedClass.getName(), mock);
		
	}
	
	/**
	 * remove a Mock
	 * 
	 * @param mockedClass
	 */
	public static void remove(Class<?> mockedClass) {
		
		Map<String, Object> map = mocksMap.get();
		if (null != map) {
			if (map.containsKey(mockedClass.getName())) 
				map.remove(mockedClass.getName());
		}
		
	}

	/**
	 * lookup a mock
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lookup(Class<?> serviceClass) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		Map<String, Object> map = mocksMap.get();
		if (null == map) 
			return null;
		Object bean = map.get(serviceClass.getName());
		return (T) bean;
	}

	/**
	 * reset a Mock context
	 */
	public static void reset() {
		
		Map<String, Object> map = mocksMap.get();
		if (null == map) return;
		map.clear();
		mocksMap.remove();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.services.ServiceFactory#accept(java.lang.Class)
	 */
	public boolean accept(Class<?> serviceClass) {
		Map<String, Object> map = mocksMap.get();
		if (null == map) 
			return false;
		return map.containsKey(serviceClass.getName());
	}
	
	public int getPriorityLevel() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.services.ServiceFactory#get(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> serviceClass) throws Exception {
		
		Map<String, Object> map = mocksMap.get();
		if (null == map) 
			return null;
		Object bean = map.get(serviceClass.getName());
		return (T) bean;
		
	}

}
