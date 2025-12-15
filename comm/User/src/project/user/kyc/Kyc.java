package project.user.kyc;

import java.io.Serializable;
import java.util.Date;

import kernel.bo.EntityObject;

/**
 * 
 * 实名认证
 */
public class Kyc extends EntityObject<String> {

	private static final long serialVersionUID = 7664064141060377449L;
	private String partyId;
	/**
	 * ID名称，如身份证等
	 */
	private String idname;
	/**
	 * 证件号码
	 */
	private String idnumber;
	/**
	 * 实名姓名
	 */
	private String name;
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
	/**
	 * 手持证件
	 */
//	private String idimg_3;
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
	 * 国籍
	 */
	private String nationality;
	/**
	 * 提交时间
	 */
	private Date apply_time;
	/**
	 * 审核时间
	 */
	private Date operation_time;

	/**
	 * 性别，man:男，woman:女
	 */
	private String sex;
	/**
	 * 出生日期
	 */
	private String borth_date;

	/**
	 * 店铺名称
	 */
	private String sellerName;

	/**
	 * 店铺log
	 */
	private String sellerImg;

	/**
	 * 店铺地址
	 */
	private String sellerAddress;
	/**
	 * 邀请码
	 */
	private String invitationCode;

	/**
	 * 用户签名PDF地址
	 */
	private String signPdfUrl;

	/**
	 * 备注
	 * @return
	 */
	private String remark;


	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getIdname() {
		return idname;
	}

	public void setIdname(String idname) {
		this.idname = idname;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdimg_1() {
		return idimg_1;
	}

	public void setIdimg_1(String idimg_1) {
		this.idimg_1 = idimg_1;
	}

	public String getIdimg_2() {
		return idimg_2;
	}

	public void setIdimg_2(String idimg_2) {
		this.idimg_2 = idimg_2;
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

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Date getApply_time() {
		return apply_time;
	}

	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
	}

	public String getIdimg_1_path() {
		return idimg_1_path;
	}

	public void setIdimg_1_path(String idimg_1_path) {
		this.idimg_1_path = idimg_1_path;
	}

	public String getIdimg_2_path() {
		return idimg_2_path;
	}

	public void setIdimg_2_path(String idimg_2_path) {
		this.idimg_2_path = idimg_2_path;
	}

	public String getIdimg_3() {
		return idimg_3;
	}

	public void setIdimg_3(String idimg_3) {
		this.idimg_3 = idimg_3;
	}

	public String getIdimg_3_path() {
		return idimg_3_path;
	}

	public void setIdimg_3_path(String idimg_3_path) {
		this.idimg_3_path = idimg_3_path;
	}

	public String getSex() {
		return sex;
	}

	public String getBorth_date() {
		return borth_date;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setBorth_date(String borth_date) {
		this.borth_date = borth_date;
	}

	public Date getOperation_time() {
		return operation_time;
	}

	public void setOperation_time(Date operation_time) {
		this.operation_time = operation_time;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerImg() {
		return sellerImg;
	}

	public void setSellerImg(String sellerImg) {
		this.sellerImg = sellerImg;
	}

	public String getSellerAddress() {
		return sellerAddress;
	}

	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public String getSignPdfUrl() {
		return signPdfUrl;
	}

	public void setSignPdfUrl(String signPdfUrl) {
		this.signPdfUrl = signPdfUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
