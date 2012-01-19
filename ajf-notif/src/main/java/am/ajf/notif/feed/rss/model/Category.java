package am.ajf.notif.feed.rss.model;

public class Category {

	public Category(String name) {
		this(name, null);
	}

	public Category(String name, String domain) {
		if (name == null || "".equals(name.trim())) {
			throw new InvalidRequiredParamException("name required: " + name);
		}

		this.name = name;
		this.domain = domain;

	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String name;
	String domain;
}