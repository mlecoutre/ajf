package am.ajf.notif.feed.rss.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item {

	public Item(String title, String description) {
		this(title, null, description);
	}

	public Item(String title, String link, String description) {

		if (title == null || "".equals(title.trim())) {
			throw new InvalidRequiredParamException(
					"title or description required: " + title + " "
							+ description);
		}

		this.title = title;
		this.link = link;
		this.description = description;
		this.categories = new ArrayList<Category>();

	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Enclosure enclosure) {
		this.enclosure = enclosure;
	}

	public Guid getGuid() {
		return guid;
	}

	public void setGuid(Guid guid) {
		this.guid = guid;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	private String title;
	private String link;
	private String description;
	private String author;
	private List<Category> categories;
	private String comments;
	private Enclosure enclosure;
	private Guid guid;
	private Date pubDate;
	private Source source;
}