package com.alankin.center.common.utils;

import java.math.BigDecimal;

/**
 * 单位转换工具类
 *
 * @author lansongtao
 * @Date 2018年5月24日
 * @since 1.0
 */
public class UnitHandler {

	private static final BigDecimal HUNDRED_UNIT = new BigDecimal("100");

	/**
	 * 小数乘以100，转为长整型
	 * 
	 * @param bd
	 * @return
	 */
	public static Long hundredToLong(BigDecimal bd) {
		if (bd == null) {
			return null;
		}

		return (bd.multiply(HUNDRED_UNIT)).longValue();
	}

	/**
	 * 长整型除以100转为2位小数
	 * 
	 * @param l
	 * @return
	 */
	public static BigDecimal hundredToDecimal(Long l) {
		if (l == null) {
			return null;
		}
		return new BigDecimal(l).divide(HUNDRED_UNIT, 2, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
	}

}
