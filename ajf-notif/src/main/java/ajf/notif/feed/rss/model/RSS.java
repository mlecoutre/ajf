package ajf.notif.feed.rss.model;

import java.util.ArrayList;
import java.util.List;

public class RSS {

	public RSS() {
		this("2.0");
	}

	public RSS(String version) {
		if (!"2.0".equals(version)) {
			throw new InvalidRequiredParamException("version invalid: "
					+ version);
		}

		this.version = version;
		channels = new ArrayList<Channel>();

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void addChannel(Channel channel) {
		channels.add(channel);
	}
	
	public static final String VERSION_2_0 = "2.0";
	public static final String DEFAULT_VERSION = "2.0";
	private String version;
	private List<Channel> channels;
}