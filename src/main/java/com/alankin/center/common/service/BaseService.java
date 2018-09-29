package com.alankin.center.common.service;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alankin.center.common.vo.BaseRespVO;

/**
 * 接口实现公用父类
 * 
 * @author lansongtao
 * @Date 2015年8月12日
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
public class BaseService {

	/**
	 * 验证是否通过，返回Null 代表通过
	 * 
	 * @param mode
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 * @return
	 */
	public BaseRespVO isPass(String mode, String serviceName, String methodName, Map reqMap) {
		return null;
	}

}
