package com.alankin.center.common.service;

import java.util.Map;

import com.alankin.center.common.Constants;
import com.alankin.center.common.utils.StringUtils;
import com.alankin.center.common.vo.BaseRespVO;

/**
 * 限制只能POST方式访问
 * 
 * @author lansongtao
 * @Date 2015年8月12日
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class PostBaseService extends BaseService {

	@Override
	public BaseRespVO isPass(String mode, String serviceName, String methodName, Map reqMap) {
		// 限制只有POST请求可以通过
		String requestMethod = reqMap.get(Constants.REQUEST_METHOD) + "";
		if (!StringUtils.isEmpty(requestMethod) && "POST".equals(requestMethod)) {
			return null;
		} else {
			return BaseRespVO.error("只能通过POST方式访问");
		}
	}

}
