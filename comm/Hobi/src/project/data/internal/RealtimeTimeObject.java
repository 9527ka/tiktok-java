package project.data.internal;

import java.util.List;

import project.data.model.Realtime;

public class RealtimeTimeObject extends TimeObject{

	private static final long serialVersionUID = -597193064229646966L;
	
	List<Realtime> list;

	public List<Realtime> getList() {
		return list;
	}

	public void setList(List<Realtime> list) {
		this.list = list;
	}
	
	
}
