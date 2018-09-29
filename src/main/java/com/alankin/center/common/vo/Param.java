package com.alankin.center.common.vo;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 普通类型参数
 *
 * @author lansongtao
 * @Date 2017年4月5日
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
	String value();
}