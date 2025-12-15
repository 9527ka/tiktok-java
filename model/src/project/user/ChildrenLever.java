package project.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChildrenLever implements Serializable {

	private static final long serialVersionUID = 3690160023364384941L;
	
	private List<String> lever1 = new ArrayList<String>();
	private List<String> lever2 = new ArrayList<String>();
	private List<String> lever3 = new ArrayList<String>();
	private List<String> lever4 = new ArrayList<String>();

	public List<String> getLever1() {
		return lever1;
	}

	public void setLever1(List<String> lever1) {
		this.lever1 = lever1;
	}

	public List<String> getLever2() {
		return lever2;
	}

	public void setLever2(List<String> lever2) {
		this.lever2 = lever2;
	}

	public List<String> getLever3() {
		return lever3;
	}

	public void setLever3(List<String> lever3) {
		this.lever3 = lever3;
	}

	public List<String> getLever4() {
		return lever4;
	}

	public void setLever4(List<String> lever4) {
		this.lever4 = lever4;
	}

}
