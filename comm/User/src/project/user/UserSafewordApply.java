package project.user;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

public class UserSafewordApply extends EntityObject<String> {
	
	private static final long serialVersionUID = -4196439149608747292L;
	
	/**
	 * 实名姓名
	 */
	private String partyId;
	
	/**
	 * 证件正面照
	 */
	private String idcard_path_front;
	
	/**
	 * 证件背面照
	 */
	private String idcard_path_back;
	
	/**
	 * 正面手持证件照
	 */
	private String idcard_path_hold;
	
	/**
	 * 资金密码
	 */
	private String safeword;
	
	/**
	 * 1审核中 ，2 审核通过,3审核未通过
	 */
	private int status;
	
	/**
	 * 审核消息，未通过原因
	 */
	private String msg;
	
	/**
	 * 创建时间
	 */
	private Date create_time;
	
	/**
	 * 审核时间
	 */
	private Date apply_time;
	
    /**
     * 操作类型
     */
    private int operate;
	
    /**
     * 备注
     */
    private String remark;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getIdcard_path_front() {
		return idcard_path_front;
	}

	public void setIdcard_path_front(String idcard_path_front) {
		this.idcard_path_front = idcard_path_front;
	}

	public String getIdcard_path_back() {
		return idcard_path_back;
	}

	public void setIdcard_path_back(String idcard_path_back) {
		this.idcard_path_back = idcard_path_back;
	}

	public String getIdcard_path_hold() {
		return idcard_path_hold;
	}

	public void setIdcard_path_hold(String idcard_path_hold) {
		this.idcard_path_hold = idcard_path_hold;
	}

	public String getSafeword() {
		return safeword;
	}

	public void setSafeword(String safeword) {
		this.safeword = safeword;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getApply_time() {
		return apply_time;
	}

	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
	}

	public int getOperate() {
		return operate;
	}

	public void setOperate(int operate) {
		this.operate = operate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
