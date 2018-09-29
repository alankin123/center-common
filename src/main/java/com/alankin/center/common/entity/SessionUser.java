package com.alankin.center.common.entity;

public class SessionUser extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 用户ID */
	public Long userId;

	/** 登录名 */
	public String loginName;

	/** 用户昵称 */
	public String nickName;

	/** 用户手机. */
	private String mobile;

	/** 身份证号. */
	private String identityCard;

	/** 系统ID */
	public String sysId;

	/** 用户类型id(是市场管理员还是商家). */
	private String typeId;

	/** 用户类型编码(商家就存商家编码、市场就存市场编码). */
	private String typeCode;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	@Override
	public String toString() {
		return "SessionUser [userId=" + userId + ", loginName=" + loginName + ", nickName=" + nickName + ", mobile=" + mobile + ", identityCard=" + identityCard
		        + ", sysId=" + sysId + ", typeId=" + typeId + ", typeCode=" + typeCode + "]";
	}

}
