package ajf.notif.feed.rss.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Channel {

	public Channel(String title, String link, String description) {
		ttl = -1;
		if (title == null || "".equals(title.trim()))
			throw new InvalidRequiredParamException("title required: " + title);
		if (link == null || "".equals(link.trim()))
			throw new InvalidRequiredParamException("link required: " + link);

		this.title = title;
		this.link = link;
		this.description = description;
		this.docs = "http://blogs.law.harvard.edu/tech/rss";
		this.generator = "AJF RSSGenerator";
		this.categories = new ArrayList<Category>();
		this.items = new ArrayList<Item>();

	}

	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public Cloud getCloud() {
		return cloud;
	}

	public void setCloud(Cloud cloud) {
		this.cloud = cloud;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getLastBuildDate() {
		return lastBuildDate;
	}

	public void setLastBuildDate(Date lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getManagingEditor() {
		return managingEditor;
	}

	public void setManagingEditor(String email) {
		managingEditor = email;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public byte getSkipDays() {
		return skipDays;
	}

	public void setSkipDays(byte skipDays) {
		this.skipDays = skipDays;
	}

	public String getSkipHours() {
		return skipHours;
	}

	public void setSkipHours(String skipHours) {
		this.skipHours = skipHours;
	}

	public TextInput getTextInput() {
		return textInput;
	}

	public void setTextInput(TextInput textInput) {
		this.textInput = textInput;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public String getWebMaster() {
		return webMaster;
	}

	public void setWebMaster(String email) {
		webMaster = email;
	}

	public String getDocs() {
		return docs;
	}

	public String getGenerator() {
		return generator;
	}

	public static final String DOCS = "http://blogs.law.harvard.edu/tech/rss";

	public static final byte SKIP_SUNDAY = 1;
	public static final byte SKIP_MONDAY = 2;
	public static final byte SKIP_TUESDAY = 4;
	public static final byte SKIP_WEDNESDAY = 8;
	public static final byte SKIP_THURSDAY = 16;
	public static final byte SKIP_FRIDAY = 32;
	public static final byte SKIP_SATURDAY = 64;
	public static final byte SKIP_WEEK = 127;
	private String title;
	private String link;
	private String description;
	private String language;
	private String copyright;
	private String managingEditor;
	private String webMaster;
	private Date pubDate;
	private Date lastBuildDate;
	private List<Category> categories;
	private String generator;
	private String docs;
	private Cloud cloud;
	private int ttl;
	private Image image;
	private String rating;
	private TextInput textInput;
	private String skipHours;
	private byte skipDays;
	private List<Item> items;
}