package com.alankin.center.common.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 *  加解密工具类
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2016年10月18日
 * @version 1.0
 * @author WillYang
 */
public class CodeUtils {

  private CodeUtils() {}

  /**
   * 
   * 功能描述 编码
   * @param value
   * @return
   * @author WillYang
   * @since 2016年9月8日下午1:34:13
   */
  public static byte[] encode(byte[] value) {
    return Base64.encodeBase64(value);
  }

  /**
   * 
   * 功能描述 解码
   * @param value
   * @return
   * @author WillYang
   * @since 2016年9月8日下午1:34:23
   */
  public static byte[] decode(byte[] value) {
    return Base64.decodeBase64(value);
  }
}
