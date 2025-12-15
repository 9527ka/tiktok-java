package project.redis.interal;

import java.io.Serializable;

public class AsynItem implements Serializable {
	private static final long serialVersionUID = -6863297417530461916L;

	public final static String TYPE_MAP = "MAP_CACHE";

	public final static String TYPE_QUEUE = "QUEUE_CACHE";

	private String key;

	private Object object;

	private String type;

	public AsynItem() {

	}

	public AsynItem(String key, Object object, String type) {
		this.key = key;
		this.object = object;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
