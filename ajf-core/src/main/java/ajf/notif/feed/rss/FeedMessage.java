package ajf.notif.feed.rss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String title;
	String description;
	String link;
	String author;
	String pubDate;
	String guid;
	
	final List<String> categories = new ArrayList<String>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	
	public void setPubDate(Date pubDate) {
		this.pubDate = Feed.formatDate(pubDate);
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public List<String> getCategories() {
		return categories;
	}

	@Override
	public String toString() {
		return "FeedMessage [title=" + title + ", description=" + description
				+ ", link=" + link + ", author=" + author + ", pubDate=" + pubDate + ", guid=" + guid
				+ "]";
	}

}
