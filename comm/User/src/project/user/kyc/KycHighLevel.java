package project.user.kyc;

import java.util.Date;

import kernel.bo.EntityObject;

public class KycHighLevel extends EntityObject<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1820111372329385339L;
	
	private String partyId;
	/**
	 * 工作地址
	 */
	private String work_place;
	/**
	 * 家庭地址
	 */
	private String home_place;
	/**
	 * 亲属关系
	 */
	private String relatives_relation;
	/**
	 * 亲属名称
	 */
	private String relatives_name;
	/**
	 * 亲属地址
	 */
	private String relatives_place;
	/**
	 * 亲属电话
	 */
	private String relatives_phone;
	
	/**
	 * 0已申请未审核 ，1审核中 ，2 审核通过,3审核未通过
	 */
	private int status;
	/**
	 * 审核消息，未通过原因
	 * 
	 */
	private String msg;
	/**
	 * 申请时间
	 */
	private Date apply_time;
	/**
	 * 审核时间
	 */
	private Date operation_time;

	
	private String name;
	
	private String username;
	
	/**
	 * 证件正面照
	 */
	private String idimg_1;
	/**
	 * 证件背面照
	 */
	private String idimg_2;
	/**
	 * 正面手持证件照
	 */
	private String idimg_3;
	/**
	 * 证件正面照文件名
	 */
	private String idimg_1_path;
	/**
	 * 证件背面照文件名
	 */
	private String idimg_2_path;
	/**
	 * 手持正面证件照文件名
	 */
	private String idimg_3_path;
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getWork_place() {
		return work_place;
	}
	public void setWork_place(String work_place) {
		this.work_place = work_place;
	}
	public String getHome_place() {
		return home_place;
	}
	public void setHome_place(String home_place) {
		this.home_place = home_place;
	}
	public String getRelatives_relation() {
		return relatives_relation;
	}
	public void setRelatives_relation(String relatives_relation) {
		this.relatives_relation = relatives_relation;
	}
	public String getRelatives_name() {
		return relatives_name;
	}
	public void setRelatives_name(String relatives_name) {
		this.relatives_name = relatives_name;
	}
	public String getRelatives_place() {
		return relatives_place;
	}
	public void setRelatives_place(String relatives_place) {
		this.relatives_place = relatives_place;
	}
	public String getRelatives_phone() {
		return relatives_phone;
	}
	public void setRelatives_phone(String relatives_phone) {
		this.relatives_phone = relatives_phone;
	}
	public Date getApply_time() {
		return apply_time;
	}
	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getOperation_time() {
		return operation_time;
	}
	public String getIdimg_1() {
		return idimg_1;
	}
	public String getIdimg_2() {
		return idimg_2;
	}
	public String getIdimg_3() {
		return idimg_3;
	}
	public String getIdimg_1_path() {
		return idimg_1_path;
	}
	public String getIdimg_2_path() {
		return idimg_2_path;
	}
	public String getIdimg_3_path() {
		return idimg_3_path;
	}
	public void setOperation_time(Date operation_time) {
		this.operation_time = operation_time;
	}
	public void setIdimg_1(String idimg_1) {
		this.idimg_1 = idimg_1;
	}
	public void setIdimg_2(String idimg_2) {
		this.idimg_2 = idimg_2;
	}
	public void setIdimg_3(String idimg_3) {
		this.idimg_3 = idimg_3;
	}
	public void setIdimg_1_path(String idimg_1_path) {
		this.idimg_1_path = idimg_1_path;
	}
	public void setIdimg_2_path(String idimg_2_path) {
		this.idimg_2_path = idimg_2_path;
	}
	public void setIdimg_3_path(String idimg_3_path) {
		this.idimg_3_path = idimg_3_path;
	}
	
	
	
}
