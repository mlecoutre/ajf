package ajf.notif.feed.rss;

import java.io.OutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RSSFeedWriter {

	private XMLEventFactory eventFactory = null;
	private XMLEvent end = null;
	private XMLEvent tab = null;
	
	private Feed rssfeed;

	/**
	 * 
	 * @param rssfeed
	 */
	public RSSFeedWriter(Feed rssfeed) {
		this.rssfeed = rssfeed;
	}

	/**
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void write(OutputStream out) throws Exception {

		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

		// Create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);

		// Create a EventFactory
		eventFactory = XMLEventFactory.newInstance();
		end = eventFactory.createDTD("\n");
		tab = eventFactory.createDTD("\t");

		// Create and write Start Tag

		StartDocument startDocument = eventFactory.createStartDocument();

		eventWriter.add(startDocument);

		// Create open tag
		eventWriter.add(end);

		StartElement rssStart = eventFactory.createStartElement("", "", "rss");
		eventWriter.add(rssStart);
		eventWriter.add(eventFactory.createAttribute("version", "2.0"));
		eventWriter.add(end);

		eventWriter.add(eventFactory.createStartElement("", "", "channel"));
		eventWriter.add(end);

		// Write the different nodes

		createNode(eventWriter, "title", rssfeed.getTitle());
		createNode(eventWriter, "link", rssfeed.getLink());
		createNode(eventWriter, "description", rssfeed.getDescription());
		createNode(eventWriter, "language", rssfeed.getLanguage());
		createNode(eventWriter, "copyright", rssfeed.getCopyright());
		createNode(eventWriter, "pubDate", rssfeed.getPubDate());
		createNode(eventWriter, "lastBuildDate", rssfeed.getLastBuildDate());
		
		for (String category : rssfeed.getCategories()) {
			createNode(eventWriter, "category", category);
		}

		for (FeedMessage entry : rssfeed.getMessages()) {
			eventWriter.add(eventFactory.createStartElement("", "", "item"));
			eventWriter.add(end);
			createNode(eventWriter, "title", entry.getTitle());
			createNode(eventWriter, "description", entry.getDescription());
			createNode(eventWriter, "link", entry.getLink());
			createNode(eventWriter, "author", entry.getAuthor());
			createNode(eventWriter, "pubDate", entry.getPubDate());
			createNode(eventWriter, "guid", entry.getGuid());
			for (String category : entry.getCategories()) {
				createNode(eventWriter, "category", category);
			}			
			eventWriter.add(end);
			eventWriter.add(eventFactory.createEndElement("", "", "item"));
			eventWriter.add(end);
		}

		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndElement("", "", "channel"));
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndElement("", "", "rss"));

		eventWriter.add(end);

		eventWriter.add(eventFactory.createEndDocument());

		eventWriter.close();
	}

	/**
	 * 
	 * @param eventWriter
	 * @param name
	 * @param value
	 * @throws XMLStreamException
	 */
	private void createNode(XMLEventWriter eventWriter, String name,
			String value) throws XMLStreamException {
		
		if (null == value)
			return;
		
		// Create Start node
		StartElement sElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		// Create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// Create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(end);
	}

}
