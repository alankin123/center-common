/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo.ListVo;

import java.util.ArrayList;
import java.util.List;

import com.alankin.center.common.vo.BaseRespVO;

/**
 * 查询视图基类
 * <p>
 * 
 * @author lansongtao
 * @param <T>
 * @Date 2015年10月8日
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class ListRespVO extends BaseRespVO {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private long dataCount = 0;

	private List aaData = new ArrayList();

	public ListRespVO() {
	}

	public List getAaData() {
		return aaData;
	}

	public void setAaData(List aaData) {
		this.aaData = aaData;
	}

	public long getDataCount() {
		return dataCount;
	}

	public void setDataCount(long dataCount) {
		this.dataCount = dataCount;
	}

	@Override
	public String toString() {
		return "ListRespVO [dataCount=" + dataCount + ", aaData=" + aaData + "]";
	}

}
