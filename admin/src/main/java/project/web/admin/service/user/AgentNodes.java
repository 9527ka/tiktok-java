package project.web.admin.service.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AgentNodes implements Serializable {
	private static final long serialVersionUID = -279469351490108330L;
	private String tags;
	private String text;
	private String href;
	private String backColor;
	private String color;
	private Map<String, Object> state;
	private List<AgentNodes> nodes;

	public String getTags() {
		return this.tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHref() {
		return this.href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List<AgentNodes> getNodes() {
		return this.nodes;
	}

	public void setNodes(List<AgentNodes> nodes) {
		this.nodes = nodes;
	}

	public String getBackColor() {
		return this.backColor;
	}

	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Map<String, Object> getState() {
		return this.state;
	}

	public void setState(Map<String, Object> state) {
		this.state = state;
	}
}
