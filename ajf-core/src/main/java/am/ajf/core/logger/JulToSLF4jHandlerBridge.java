package am.ajf.core.logger;

import org.slf4j.bridge.SLF4JBridgeHandler;

public final class JulToSLF4jHandlerBridge {

	static {
		if (!SLF4JBridgeHandler.isInstalled())
			SLF4JBridgeHandler.install();
	}

	private JulToSLF4jHandlerBridge() {
		super();
	}
	
}
