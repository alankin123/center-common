package com.alankin.center.common.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 加密类服务公用父类
 * 
 * @author lansongtao
 * @Date 2015年8月12日
 * @since 1.0
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
public class EncryptSupperService {

}
