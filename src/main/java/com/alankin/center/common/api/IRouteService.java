package com.alankin.center.common.api;

import java.util.Map;

import com.alankin.center.common.vo.BaseRespVO;

public interface IRouteService {

	/**
	 * 各个子系统的web请求通用入口
	 * 
	 * @param mode
	 * @param service
	 * @param method
	 * @param reqMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	BaseRespVO webProcess(String mode, String service, String method, Map reqMap);

}
