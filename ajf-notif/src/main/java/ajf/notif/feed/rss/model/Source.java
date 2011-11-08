package ajf.notif.feed.rss.model;

public class Source {

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	String value;
	String url;

	public Source(String url) {
		this(url, null);
	}

	public Source(String url, String value) {
		if (url == null || "".equals(url.trim())) {
			throw new InvalidRequiredParamException("url required: " + url);
		}

		this.value = value;
		this.url = url;

	}
}