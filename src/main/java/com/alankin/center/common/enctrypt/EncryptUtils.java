package com.alankin.center.common.enctrypt;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.alankin.center.common.encrypt.DESUtils;

public class EncryptUtils {

	private static Logger logger = LoggerFactory.getLogger(EncryptUtils.class);

	private final static String ENCODING = "UTF-8";

	private static Map<String, String> map = new ConcurrentHashMap<String, String>();

	/**
	 * 加密报文
	 * 
	 * @param merchantNo
	 * @param decryptData
	 * @return
	 */
	public static String encodeData(String merchantNo, String decryptData) {
		String key = getKeyByMerchantNo(merchantNo);

		String encryptData = null;
		try {
			encryptData = Base64Utils.encodeToString(DESUtils.Encrypt3DES(Base64Utils.decodeFromString(key), decryptData.getBytes("utf-8"), true));
		} catch (Exception e) {
			logger.error("解密报文出错!", e);
			return null;
		}

		return encryptData;
	}

	/**
	 * 解密报文
	 * 
	 * @param merchantNo
	 * @param encryptData
	 * @return
	 */
	public static String decodeData(String merchantNo, String encryptData) {
		String key = getKeyByMerchantNo(merchantNo);

		String decryptData = null;
		try {
			decryptData = new String(DESUtils.Decrypt3DES(Base64Utils.decodeFromString(key), Base64Utils.decodeFromString(encryptData), true), ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.error("解密报文出错!", e);
			return null;
		}

		return decryptData;
	}

	private static String getKeyByMerchantNo(String merchantNo) {
		String key = map.get(merchantNo);
		if (key == null) {
			key = EncryptConfig.getInstans().getValue(merchantNo);
			map.put(merchantNo, key);
		}
		return key;
	}

}
