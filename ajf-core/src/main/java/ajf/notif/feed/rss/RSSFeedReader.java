package ajf.notif.feed.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class RSSFeedReader {

	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String CATEGORY = "category";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String LAST_BUILD_DATE = "lastBuildDate";
	static final String DOCS = "pubDate";
	static final String GUID = "guid";

	/**
	 * 
	 */
	public RSSFeedReader() {
		super();
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws XMLStreamException
	 */
	public Feed readFeed(InputStream in) throws XMLStreamException {
		Feed feed = null;

		boolean isFeedHeader = true;
		
		// Set header values intial 
		String description = "";
		String title = "";
		String link = "";
		String language = null;
		String copyright = null;
		String author = "";
		String pubdate = null;
		String lastbuilddate = null;
		String guid = null;
		List<String> categories = new ArrayList<String>();

		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		// Read the XML document
		while (eventReader.hasNext()) {

			XMLEvent event = eventReader.nextEvent();

			if (event.isStartElement()) {
				if (event.asStartElement().getName().getLocalPart()
						.equals(ITEM)) {
					if (isFeedHeader) {
						
						isFeedHeader = false;
						
						feed = new Feed(title, link, description, language,
								copyright, pubdate, lastbuilddate);
						feed.getCategories().addAll(categories);
						
						categories.clear();
						title = "";
						link = "";
						description = "";
						pubdate = null;
						
					}
					event = eventReader.nextEvent();
					continue;
				}

				if (event.asStartElement().getName().getLocalPart()
						.equals(TITLE)) {
					event = eventReader.nextEvent();
					title = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(DESCRIPTION)) {
					event = eventReader.nextEvent();
					description = event.asCharacters().getData();
					continue;
				}

				if (event.asStartElement().getName().getLocalPart()
						.equals(LINK)) {
					event = eventReader.nextEvent();
					link = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(CATEGORY)) {
					event = eventReader.nextEvent();
					String cat = event.asCharacters().getData();
					categories.add(cat);
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(GUID)) {
					event = eventReader.nextEvent();
					guid = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(LANGUAGE)) {
					event = eventReader.nextEvent();
					language = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(AUTHOR)) {
					event = eventReader.nextEvent();
					author = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(PUB_DATE)) {
					event = eventReader.nextEvent();
					pubdate = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(LAST_BUILD_DATE)) {
					event = eventReader.nextEvent();
					lastbuilddate = event.asCharacters().getData();
					continue;
				}
				if (event.asStartElement().getName().getLocalPart()
						.equals(COPYRIGHT)) {
					event = eventReader.nextEvent();
					copyright = event.asCharacters().getData();
					continue;
				}
			}
			else
				if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart()
							.equals(ITEM)) {
						
						FeedMessage message = new FeedMessage();
						message.setAuthor(author);
						message.setDescription(description);
						message.setGuid(guid);
						message.setLink(link);
						message.setTitle(title);
						message.setPubDate(pubdate);
						message.getCategories().addAll(categories);
						
						categories.clear();
						title = "";
						link = "";
						description = "";
						pubdate = null;
						guid = null;
						
						feed.getMessages().add(message);
						event = eventReader.nextEvent();
						continue;
					}
				}
		}

		return feed;

	}

}
