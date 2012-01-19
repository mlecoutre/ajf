package am.ajf.notif.feed.rss.model;

public class Enclosure {

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	String url;
	int length;
	String type;

	public Enclosure(String url, int length, String type) {
		if (url == null || "".equals(url.trim()))
			throw new InvalidRequiredParamException("url required: " + url);
		if (length <= 0)
			throw new InvalidRequiredParamException(
					"length must be a positive non-zero value: " + length);
		if (type == null || "".equals(type.trim())) {
			throw new InvalidRequiredParamException("type required: " + type);
		}

		this.url = url;
		this.length = length;
		this.type = type;
	}
}