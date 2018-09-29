
package com.alankin.center.common.log;

import com.alankin.center.common.entity.BaseEntity;

/**
 * 系统操作日志
 * @author lansongtao
 * @Date 2017-12-18 14:34:42
 * @since 1.0
 */
public class LogBusinessEntity extends BaseEntity {
	
	/** serialVersionUID */
    private static final long serialVersionUID = 1L;
	
	/** 自增id. */
	private Long id;
	
	/** 操作类型. */
	private Long operType;
	
	/** IP. */
	private String clentIp;
	
	/** 模块. */
	private String modeName;
	
	/** 方法. */
	private String methodName;
	
	/** 被操作的记录ID. */
	private String recordId;
	
	/** 参数. */
	private String parameters;
	
	/** 日志内容. */
	private String logContent;
	
	/** 操作结果. */
	private String operResut;
	
	/** 操作员. */
	private Long createUserId;
	
	/** 操作员名称. */
	private String createUserName;
	
	/** 创建日期. */
	private java.util.Date createDate;
	
	/** 修改日期. */
	private java.util.Date updateDate;
	
	

    /** set 自增id. */
	public void setId(Long id) {
		this.id = id;
	}
	
	/** get 自增id. */
	public Long getId() {
		return this.id;
	}
	

    /** set 操作类型. */
	public void setOperType(Long operType) {
		this.operType = operType;
	}
	
	/** get 操作类型. */
	public Long getOperType() {
		return this.operType;
	}
	

    /** set IP. */
	public void setClentIp(String clentIp) {
		this.clentIp = clentIp;
	}
	
	/** get IP. */
	public String getClentIp() {
		return this.clentIp;
	}
	

    /** set 模块. */
	public void setModeName(String modeName) {
		this.modeName = modeName;
	}
	
	/** get 模块. */
	public String getModeName() {
		return this.modeName;
	}
	

    /** set 方法. */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	/** get 方法. */
	public String getMethodName() {
		return this.methodName;
	}
	

    /** set 被操作的记录ID. */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	
	/** get 被操作的记录ID. */
	public String getRecordId() {
		return this.recordId;
	}
	

    /** set 参数. */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	/** get 参数. */
	public String getParameters() {
		return this.parameters;
	}
	

    /** set 日志内容. */
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	
	/** get 日志内容. */
	public String getLogContent() {
		return this.logContent;
	}
	

    /** set 操作结果. */
	public void setOperResut(String operResut) {
		this.operResut = operResut;
	}
	
	/** get 操作结果. */
	public String getOperResut() {
		return this.operResut;
	}
	

    /** set 操作员. */
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	
	/** get 操作员. */
	public Long getCreateUserId() {
		return this.createUserId;
	}
	

    /** set 操作员名称. */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	/** get 操作员名称. */
	public String getCreateUserName() {
		return this.createUserName;
	}
	

    /** set 创建日期. */
	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}
	
	/** get 创建日期. */
	public java.util.Date getCreateDate() {
		return this.createDate;
	}
	

    /** set 修改日期. */
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}
	
	/** get 修改日期. */
	public java.util.Date getUpdateDate() {
		return this.updateDate;
	}
	
    /** constructor */
	public LogBusinessEntity() {
		super();
	}

	/**
	 * constructor.<p>
	 * @param operType			操作类型
	 * @param clentIp			IP
	 * @param modeName			模块
	 * @param methodName			方法
	 * @param recordId			被操作的记录ID
	 * @param parameters			参数
	 * @param logContent			日志内容
	 * @param operResut			操作结果
	 * @param createUserId			操作员
	 * @param createUserName			操作员名称
	 * @param createDate			创建日期
	 * @param updateDate			修改日期
	 */
	public LogBusinessEntity(Long operType,String clentIp,String modeName,String methodName,String recordId,String parameters,String logContent,String operResut,Long createUserId,String createUserName,java.util.Date createDate,java.util.Date updateDate){
		this();
		this.operType = operType;
		this.clentIp = clentIp;
		this.modeName = modeName;
		this.methodName = methodName;
		this.recordId = recordId;
		this.parameters = parameters;
		this.logContent = logContent;
		this.operResut = operResut;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("LogBusinessEntity[")
			.append("Id=").append(getId()).append(", ")
			.append("OperType=").append(getOperType()).append(", ")
			.append("ClentIp=").append(getClentIp()).append(", ")
			.append("ModeName=").append(getModeName()).append(", ")
			.append("MethodName=").append(getMethodName()).append(", ")
			.append("RecordId=").append(getRecordId()).append(", ")
			.append("Parameters=").append(getParameters()).append(", ")
			.append("LogContent=").append(getLogContent()).append(", ")
			.append("OperResut=").append(getOperResut()).append(", ")
			.append("CreateUserId=").append(getCreateUserId()).append(", ")
			.append("CreateUserName=").append(getCreateUserName()).append(", ")
			.append("CreateDate=").append(getCreateDate()).append(", ")
			.append("UpdateDate=").append(getUpdateDate())
		.append("]").toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
	    int result = 1;
	    	result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
	    return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LogBusinessEntity == false) return false;
		if(this == obj) return true;
		LogBusinessEntity other = (LogBusinessEntity)obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return false;
	}
}
