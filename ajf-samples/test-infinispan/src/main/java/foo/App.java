package foo;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class App {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		int pos = pid.indexOf("@");
		if (pos > 0) {
			pid = pid.substring(0, pos);
		}
		System.setProperty("runtime.pid", pid);
		
		String nodeName = InetAddress.getLocalHost().getHostName();
		System.setProperty("node.name", nodeName);
		
		Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
		if (networks.hasMoreElements()) {
			NetworkInterface network = networks.nextElement();
			byte[] mac = network.getHardwareAddress();
	 		if ((null != mac) && (0 < mac.length)) {
	 			System.out.println(network.getName());
	 			System.out.println(network.getDisplayName());
	 			System.out.print("Current MAC address : ");
		 		StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				String macAddress = sb.toString();
				System.out.println(macAddress);
				
				System.setProperty("machine.id", macAddress);
	 		}
		}
		
		String configFile = "infinispan_config.xml";
		DefaultCacheManager m = new DefaultCacheManager(configFile);
		//m.start();
		Cache<String, String> cache = m.getCache();
		
		String key = "aKey";
		cache.put(key, "aValue");
		
		String value = cache.get(key);
		System.out.println(value);
		m.stop();

	}

}
