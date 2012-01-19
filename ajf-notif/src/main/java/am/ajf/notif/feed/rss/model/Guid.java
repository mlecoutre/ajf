package am.ajf.notif.feed.rss.model;

public class Guid {

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPermaLink() {
		return permaLink;
	}

	public void setPermaLink(boolean permaLink) {
		this.permaLink = permaLink;
	}

	public static final boolean DEFAULT_PERMA_LINK = true;
	String id;
	boolean permaLink;

	public Guid(String id) {
		this(id, true);
	}

	public Guid(String id, boolean permaLink) {
		if (id == null || "".equals(id.trim())) {
			throw new InvalidRequiredParamException("id required: " + id);
		}

		this.id = id;
		this.permaLink = permaLink;
	}
}