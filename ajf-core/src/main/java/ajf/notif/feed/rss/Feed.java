package ajf.notif.feed.rss;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Feed implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final String title;
	final String link;
	final String description;
	final String language;
	final String copyright;
	final String pubDate;
	final String lastBuildDate;
	
	final List<String> categories = new ArrayList<String>();
	final List<FeedMessage> entries = new ArrayList<FeedMessage>();
	
	final static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	
	public Feed(String title, String link, String description, String language,
			String copyright, String pubDate, String lastBuildDate) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.copyright = copyright;
		this.pubDate = pubDate;
		this.lastBuildDate = lastBuildDate;
	}

	/**
	 * 
	 * @param title
	 * @param link
	 * @param description
	 * @param language
	 * @param copyright
	 * @param pubDate
	 */
	public Feed(String title, String link, String description, String language,
			String copyright, Date pubDate, Date lastBuildDate) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.copyright = copyright;
		this.pubDate = dateFormatter.format(pubDate);
		this.lastBuildDate = dateFormatter.format(lastBuildDate);
	}

	public static String formatDate(Date date) {
		return dateFormatter.format(date);
	}

	public List<FeedMessage> getMessages() {
		return entries;
	}
	
	public List<String> getCategories() {
		return categories;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getPubDate() {
		return pubDate;
	}
	
	public String getLastBuildDate() {
		return lastBuildDate;
	}

	@Override
	public String toString() {
		return "Feed [copyright=" + copyright + ", description=" + description
				+ ", language=" + language + ", link=" + link + ", pubDate="
				+ pubDate + ", lastBuildDate="
				+ lastBuildDate + ", title=" + title + "]";
	}

}
