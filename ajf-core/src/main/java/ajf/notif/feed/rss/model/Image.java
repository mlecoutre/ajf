package ajf.notif.feed.rss.model;

public class Image {

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public static final int DEFAULT_WIDTH = 88;
	public static final int DEFAULT_HEIGHT = 31;
	public static final int MAX_WIDTH = 144;
	public static final int MAX_HEIGHT = 400;

	String url;
	String title;
	String link;
	int width;
	int height;
	String description;

	public Image(String url, String title, String link) {
		this(url, title, link, 88, 31, null);
	}

	public Image(String url, String title, String link, int width, int height,
			String description) {

		if (url == null || "".equals(url.trim()))
			throw new InvalidRequiredParamException("url required: " + url);
		if (title == null || "".equals(title.trim()))
			throw new InvalidRequiredParamException("title required: " + title);
		if (link == null || "".equals(link.trim()))
			throw new InvalidRequiredParamException("link required: " + link);
		if (width <= 0 || width > 144)
			throw new InvalidRequiredParamException(
					"width must be between 1 and 144: " + width);
		if (height <= 0 || height > 400) {
			throw new InvalidRequiredParamException(
					"height must be between 1 and 400: " + height);
		}

		this.url = url;
		this.title = title;
		this.link = link;
		this.width = width;
		this.height = height;
		this.description = description;

	}
}