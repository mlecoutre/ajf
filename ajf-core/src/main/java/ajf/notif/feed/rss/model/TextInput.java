package ajf.notif.feed.rss.model;


public class TextInput {

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;		
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	String title;
	String description;
	String name;
	String link;

	public TextInput(String title, String description, String name,
			String link) {

		if (title == null || "".equals(title.trim()))
			throw new InvalidRequiredParamException("title required: "
					+ title);
		if (name == null || "".equals(name.trim()))
			throw new InvalidRequiredParamException("name required: "
					+ name);
		if (link == null || "".equals(link.trim())) {
			throw new InvalidRequiredParamException("link required: "
					+ link);
		}
		
		this.title = title;
		this.description = description;
		this.name = name;
		this.link = link;
		
	}
}