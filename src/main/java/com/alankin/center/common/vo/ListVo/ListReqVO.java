/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo.ListVo;

import java.util.List;

import org.apache.commons.lang.CharUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.alankin.center.common.utils.StringUtils;
import com.alankin.center.common.vo.BaseReqVO;

/**
 * 查询视图基类
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月8日
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class ListReqVO<T> extends BaseReqVO {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 分页页码 */
	private int pageNum = 1;

	/** 分页开始条数 */
	private int startNum = 0;

	/** 每页记录数 */
	private int pageSize = 1000;

	/** 排序字段 */
	private String orderField;

	/** ID,分号分隔,用于批量处理 */
	private String idStrs;

	/** ID列表 */
	private List<String> idList;

	/** 开始时间 */
	private String startTime;

	/** 结束时间 */
	private String endTime;

	/** 业务实体字段 */
	private T whereCondition;

	/** 关键字查询 */
	private String keyWord;

	/** 扩展字段，可自定义用途 */
	private String extend;

	public String getKeyWord() {
		if (StringUtils.isEmpty(this.keyWord)) {
			return null;
		}
		return "%" + this.keyWord + "%";
	}

	@JsonIgnore
	public String getKeyWordByLike() {
		if (StringUtils.isEmpty(this.keyWord)) {
			return null;
		}
		return "%" + this.keyWord + "%";
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderField() {
		return propertyToField(orderField);
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getIdStrs() {
		return idStrs;
	}

	public void setIdStrs(String idStrs) {
		this.idStrs = idStrs;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	public T getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(T whereCondition) {
		this.whereCondition = whereCondition;
	}

	public String getStartTime() {
		if (StringUtils.isEmpty(startTime) || startTime.trim().startsWith("undefined")) {
			return null;
		}
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		if (StringUtils.isEmpty(endTime) || endTime.trim().startsWith("undefined")) {
			return null;
		}
		if (endTime != null && endTime.trim().length() > 6 && endTime.trim().length() <= 10) {
			if (endTime.indexOf("-") > -1) {
				return endTime + " 23:59:59";
			} else {
				return endTime + "235959";
			}
		}
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public static String propertyToField(String property) {
		if (null == property) {
			return null;
		}
		char[] chars = property.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (CharUtils.isAsciiAlphaUpper(c)) {
				sb.append("_" + org.apache.commons.lang.StringUtils.lowerCase(CharUtils.toString(c)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/** * 字段转换成对象属性 例如：user_name to userName * @param field * @return */
	public static String fieldToProperty(String field) {
		if (null == field) {
			return "";
		}
		char[] chars = field.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(org.apache.commons.lang.StringUtils.upperCase(CharUtils.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof ListReqVO == false)
			return false;
		if (this == obj)
			return true;
		ListReqVO other = (ListReqVO) obj;
		if (other.pageNum != pageNum) {
			return false;
		}
		if (other.pageSize != pageSize) {
			return false;
		}
		if (!(other.startTime + "").equals(startTime + "")) {
			return false;
		}
		if (!(other.endTime + "").equals(endTime + "")) {
			return false;
		}
		if (!(other.keyWord + "").equals(keyWord + "")) {
			return false;
		}
		if (!(other.orderField + "").equals(orderField + "")) {
			return false;
		}
		if (!(other.extend + "").equals(extend + "")) {
			return false;
		}

		if (whereCondition != null) {
			return whereCondition.equals(other.getWhereCondition());
		} else if (other.getWhereCondition() != null) {
			return other.whereCondition.equals(getWhereCondition());
		}

		return true;
	}

	@Override
	public int hashCode() {
		if (whereCondition != null) {
			return ((ListReqVO) this).getWhereCondition().hashCode();
		}
		return 1;
	}

	@Override
	public String toString() {
		return "ListReqVO [pageNum=" + pageNum + ", pageSize=" + pageSize + ", orderField=" + orderField + ", idStrs=" + idStrs + ", idList=" + idList
		        + ", startTime=" + startTime + ", endTime=" + endTime + ", whereCondition=" + whereCondition + "]";
	}

}
