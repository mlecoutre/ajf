package ajf.notif.feed.rss;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ajf.notif.feed.rss.model.Category;
import ajf.notif.feed.rss.model.Channel;
import ajf.notif.feed.rss.model.Cloud;
import ajf.notif.feed.rss.model.Enclosure;
import ajf.notif.feed.rss.model.Guid;
import ajf.notif.feed.rss.model.Image;
import ajf.notif.feed.rss.model.Item;
import ajf.notif.feed.rss.model.RSS;
import ajf.notif.feed.rss.model.Source;
import ajf.notif.feed.rss.model.TextInput;

public class RSSFeedWriter {

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss z");
	private final RSS rss;

	public RSSFeedWriter(RSS rss) {
		super();
		this.rss = rss;
	}

	public void write(OutputStream outputStream) throws IOException {

		Document doc = buildDocument();

		OutputFormat format = new OutputFormat(doc);
		format.setLineSeparator("\r\n");
		format.setIndenting(true);
		format.setLineWidth(0);
		format.setPreserveSpace(true);

		XMLSerializer serializer = new XMLSerializer(outputStream, format);
		serializer.asDOMSerializer();
		serializer.serialize(doc);

		outputStream.flush();

	}

	private Document buildDocument() {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element root = doc.createElement("rss");
			root.setAttribute("version", rss.getVersion());
			Element chan;
			for (Iterator<Channel> channels = rss.getChannels().iterator(); channels
					.hasNext(); root.appendChild(chan)) {

				Channel channel = (Channel) channels.next();
				chan = writeChannel(doc, channel);
				writeItems(doc, chan, channel.getItems());

			}

			doc.appendChild(root);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return doc;
	}

	private void writeItems(Document doc, Element chan, List<Item> itemsList) {
		for (Iterator<Item> items = itemsList.iterator(); items
				.hasNext();) {
			Item item = (Item) items.next();
			if (null != item) {
				
				writeItem(doc, chan, item);
				
			}
		}
	}

	private void writeItem(Document doc, Element chan, Item item) {
		Element itelem = doc.createElement("item");
		if (item.getTitle() != null) {
			org.w3c.dom.Text content = doc.createTextNode(item
					.getTitle());
			Element elem = doc.createElement("title");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getLink() != null) {
			org.w3c.dom.Text content = doc.createTextNode(item
					.getLink());
			Element elem = doc.createElement("link");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getDescription() != null) {
			org.w3c.dom.Text content = doc.createTextNode(item
					.getDescription());
			Element elem = doc.createElement("description");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getAuthor() != null) {
			org.w3c.dom.Text content = doc.createTextNode(item
					.getAuthor());
			Element elem = doc.createElement("author");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}

		{
			Element elem;
			for (Iterator<Category> icategories = item
					.getCategories().iterator(); icategories
					.hasNext(); itelem.appendChild(elem)) {
				Category category = (Category) icategories
						.next();
				org.w3c.dom.Text content = doc
						.createTextNode(category.getName());
				elem = doc.createElement("category");
				if (category.getDomain() != null)
					elem.setAttribute("domain",
							category.getDomain());
				elem.appendChild(content);
			}
		}

		if (item.getComments() != null) {
			org.w3c.dom.Text content = doc.createTextNode(item
					.getComments());
			Element elem = doc.createElement("comments");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getEnclosure() != null) {
			Enclosure enclosure = item.getEnclosure();
			Element elem = doc.createElement("enclosure");
			elem.setAttribute("url", enclosure.getUrl());
			elem.setAttribute("length",
					String.valueOf(enclosure.getLength()));
			elem.setAttribute("type", enclosure.getType());
			itelem.appendChild(elem);
		}
		if (item.getGuid() != null) {
			Guid guid = item.getGuid();
			Element elem = doc.createElement("guid");
			if (!guid.isPermaLink())
				elem.setAttribute("isPermaLink", "false");
			org.w3c.dom.Text content = doc.createTextNode(guid
					.getId());
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getPubDate() != null) {
			org.w3c.dom.Text content = doc
					.createTextNode(dateFormatter.format(item
							.getPubDate()));
			Element elem = doc.createElement("pubDate");
			elem.appendChild(content);
			itelem.appendChild(elem);
		}
		if (item.getSource() != null) {
			Source source = item.getSource();
			Element elem = doc.createElement("source");
			elem.setAttribute("url", source.getUrl());
			if (source.getValue() != null) {
				org.w3c.dom.Text content = doc
						.createTextNode(source.getValue());
				elem.appendChild(content);
			}
			itelem.appendChild(elem);
		}
		chan.appendChild(itelem);
	}

	private Element writeChannel(Document doc, Channel channel) {
		Element chan;
		chan = doc.createElement("channel");
		if (channel.getTitle() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel.getTitle());
			Element elem = doc.createElement("title");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getLink() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel.getLink());
			Element elem = doc.createElement("link");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getDescription() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel
					.getDescription());
			Element elem = doc.createElement("description");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getLanguage() != null) {
			org.w3c.dom.Text content = doc
					.createTextNode(channel.getLanguage());
			Element elem = doc.createElement("language");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getCopyright() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel
					.getCopyright());
			Element elem = doc.createElement("copyright");
			elem.appendChild(content);
			chan.appendChild(elem);
		}
		if (channel.getWebMaster() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel
					.getWebMaster());
			Element elem = doc.createElement("webMaster");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getPubDate() != null) {
			org.w3c.dom.Text content = doc.createTextNode(dateFormatter
					.format(channel.getPubDate()));
			Element elem = doc.createElement("pubDate");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		if (channel.getLastBuildDate() != null) {
			org.w3c.dom.Text content = doc.createTextNode(dateFormatter
					.format(channel.getLastBuildDate()));
			Element elem = doc.createElement("lastBuildDate");
			elem.appendChild(content);
			chan.appendChild(elem);
		}

		{
			Element elem;
			for (Iterator<Category> categories = channel.getCategories()
					.iterator(); categories.hasNext(); chan.appendChild(elem)) {
				Category category = (Category) categories.next();
				org.w3c.dom.Text content = doc.createTextNode(category
						.getName());
				elem = doc.createElement("category");
				if (category.getDomain() != null)
					elem.setAttribute("domain", category.getDomain());
				elem.appendChild(content);
			}
		}
		if (channel.getGenerator() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel
					.getGenerator());
			Element elem = doc.createElement("generator");
			elem.appendChild(content);
			chan.appendChild(elem);
		}
		if (channel.getDocs() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel.getDocs());
			Element elem = doc.createElement("docs");
			elem.appendChild(content);
			chan.appendChild(elem);
		}
		if (channel.getCloud() != null) {
			Cloud cloud = channel.getCloud();
			Element elem = doc.createElement("cloud");
			elem.setAttribute("domain", cloud.getDomain());
			elem.setAttribute("port", String.valueOf(cloud.getPort()));
			elem.setAttribute("path", cloud.getPath());
			elem.setAttribute("registerProcedure", cloud.getRegisterProcedure());
			elem.setAttribute("protocol", cloud.getProtocol());
			chan.appendChild(elem);
		}
		if (channel.getTtl() >= 0) {
			org.w3c.dom.Text content = doc.createTextNode(String
					.valueOf(channel.getTtl()));
			Element elem = doc.createElement("ttl");
			elem.appendChild(content);
			chan.appendChild(elem);
		}
		if (channel.getImage() != null) {
			Image image = channel.getImage();
			Element elem = doc.createElement("image");
			org.w3c.dom.Text urlct = doc.createTextNode(image.getUrl());
			Element url = doc.createElement("url");
			url.appendChild(urlct);
			elem.appendChild(url);
			org.w3c.dom.Text titlect = doc.createTextNode(image.getTitle());
			Element title = doc.createElement("title");
			url.appendChild(titlect);
			elem.appendChild(title);
			org.w3c.dom.Text linkct = doc.createTextNode(image.getLink());
			Element link = doc.createElement("link");
			url.appendChild(linkct);
			elem.appendChild(link);
			if (image.getWidth() > 0) {
				org.w3c.dom.Text wct = doc.createTextNode(String.valueOf(image
						.getWidth()));
				Element w = doc.createElement("width");
				w.appendChild(wct);
				elem.appendChild(w);
			}
			if (image.getHeight() > 0) {
				org.w3c.dom.Text hct = doc.createTextNode(String.valueOf(image
						.getHeight()));
				Element h = doc.createElement("height");
				h.appendChild(hct);
				elem.appendChild(h);
			}
			if (image.getDescription() != null) {
				org.w3c.dom.Text descct = doc.createTextNode(image
						.getDescription());
				Element desc = doc.createElement("description");
				desc.appendChild(descct);
				elem.appendChild(desc);
			}
			chan.appendChild(elem);
		}
		if (channel.getRating() != null) {
			org.w3c.dom.Text content = doc.createTextNode(channel.getRating());
			Element elem = doc.createElement("rating");
			elem.appendChild(content);
			chan.appendChild(elem);
		}
		if (channel.getTextInput() != null) {
			TextInput tinput = channel.getTextInput();
			Element elem = doc.createElement("textInput");
			org.w3c.dom.Text content = doc.createTextNode(tinput.getTitle());
			Element subelem = doc.createElement("title");
			subelem.appendChild(content);
			elem.appendChild(subelem);
			content = doc.createTextNode(tinput.getDescription());
			subelem = doc.createElement("description");
			subelem.appendChild(content);
			elem.appendChild(subelem);
			content = doc.createTextNode(tinput.getName());
			subelem = doc.createElement("name");
			subelem.appendChild(content);
			elem.appendChild(subelem);
			content = doc.createTextNode(tinput.getLink());
			subelem = doc.createElement("link");
			subelem.appendChild(content);
			elem.appendChild(subelem);
			chan.appendChild(elem);
		}
		if (channel.getSkipHours() != null) {
			Element skip = doc.createElement("skipHours");
			StringTokenizer hours = new StringTokenizer(channel.getSkipHours(),
					",");
			label0: while (hours.hasMoreTokens()) {
				String hr = hours.nextToken().trim();
				if (hr.indexOf("-") != -1) {
					int x = Integer.parseInt(hr.substring(0, hr.indexOf("-")));
					int y = Integer.parseInt(hr.substring(hr.indexOf("-") + 1));
					do {
						Element hour = doc.createElement("hour");
						org.w3c.dom.Text hourct = doc.createTextNode(String
								.valueOf(x % 24));
						hour.appendChild(hourct);
						skip.appendChild(hour);
						if (x % 24 == y) continue label0;
						x++;
					}
					while (true);
				}
				if (!"".equals(hr)) {
					Element hour = doc.createElement("hour");
					org.w3c.dom.Text hourct = doc.createTextNode(hr);
					hour.appendChild(hourct);
					skip.appendChild(hour);
				}
			}
			chan.appendChild(skip);
		}
		if (channel.getSkipDays() != 0 && channel.getSkipDays() != -128) {
			Element skip = doc.createElement("skipDays");
			byte days = channel.getSkipDays();
			days = (byte) ((days << 25) >>> 25);
			if ((days & 1) == 1) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Sunday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 2) == 2) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Monday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 4) == 4) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Tuesday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 8) == 8) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Wednesday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 0x10) == 16) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Thursday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 0x20) == 32) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Friday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			if ((days & 0x40) == 64) {
				Element day = doc.createElement("day");
				org.w3c.dom.Text dayct = doc.createTextNode("Saturday");
				day.appendChild(dayct);
				skip.appendChild(day);
			}
			chan.appendChild(skip);
		}

		return chan;
	}
}