package com.alankin.center.common.api;

import java.util.Map;

/**
 * 需要提供给外部加密接口访问的service，需要实现该接口
 *
 * @author lansongtao
 * @Date 2017年6月23日
 * @since 1.0
 */
public interface IEncryptService {

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
	String encryptProcess(String mode, String service, String method, Map reqMap);

}
