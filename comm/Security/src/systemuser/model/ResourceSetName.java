package systemuser.model;

import kernel.bo.EntityObject;
/**
 * 权限资源集
 *
 */
public class ResourceSetName extends EntityObject<String> {

	private static final long serialVersionUID = 3575411649937943312L;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
