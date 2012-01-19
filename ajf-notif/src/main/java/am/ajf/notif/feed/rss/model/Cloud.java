package am.ajf.notif.feed.rss.model;

public class Cloud {

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRegisterProcedure() {
		return registerProcedure;
	}

	public void setRegisterProcedure(String registerProcedure) {
		this.registerProcedure = registerProcedure;
	}

	String domain;
	int port;
	String path;
	String registerProcedure;
	String protocol;

	public static final String XML_RPC_PROTOCOL = "xml-rpc";
	public static final String SOAP_PROTOCOL = "soap";

	public Cloud(String domain, int port, String path,
			String registerProcedure, String protocol) {

		if (domain == null || "".equals(domain.trim()))
			throw new InvalidRequiredParamException("domain required: "
					+ domain);
		if (port <= 0 || port > 65000)
			throw new InvalidRequiredParamException(
					"port must be between 1 and 65000: " + port);
		if (path == null || "".equals(path.trim()))
			throw new InvalidRequiredParamException("path required: " + path);
		if (registerProcedure == null || "".equals(registerProcedure.trim()))
			throw new InvalidRequiredParamException(
					"registerProcedure required: " + registerProcedure);
		if (!"xml-rpc".equals(protocol) || !"soap".equals(protocol)) {
			throw new InvalidRequiredParamException(
					"protocol must be 'xml-rpc' or 'soap': " + protocol);
		}
		this.domain = domain;
		this.port = port;
		this.path = path;
		this.registerProcedure = registerProcedure;
		this.protocol = protocol;
	}
}