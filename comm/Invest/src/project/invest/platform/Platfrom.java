package project.invest.platform;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Platfrom extends EntityObject<String> {

    private static final long serialVersionUID = -6324021713418425625L;

    private String name;

    private Date createTime;

    private int status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
    
}