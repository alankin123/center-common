package com.alankin.center.common.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.alankin.center.common.utils.ByteUtils;
import com.alankin.center.common.utils.StringUtils;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全</br>
 * Linux使用openssl生成RSA私钥和公钥</br>
 * openssl genrsa -out rsa_private_key.pem 1024</br>
 * openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt</br>
 * openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem</br>
 * </p>
 * 发送者使用私钥进行签名，接收者使用公钥进行签名校验
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * 
 * @since 2016年10月20日
 * @version 1.0
 * @author WillYang
 */
public class RSAUtils {
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	private static final String UTF8 = "UTF-8";

	/**
	 * 
	 * 功能描述 使用私钥进行签名
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:29:39
	 */
	public static String sign(String data, String privateKey) throws Exception {
		checkEmpty(data, "data");
		checkEmpty(privateKey, "private key");
		return sign(data.getBytes(UTF8), privateKey);
	}

	/**
	 * 
	 * 功能描述 使用私钥对数据进行签名
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:13:25
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		checkNull(data, "data");
		checkEmpty(privateKey, "private key");
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return Base64.encodeBase64String(signature.sign());
	}

	/**
	 * 
	 * 功能描述
	 * 
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:31:01
	 */
	public static boolean verify(String data, String publicKey, String sign) throws Exception {
		checkEmpty(data, "data");
		checkEmpty(publicKey, "public key");
		checkEmpty(sign, "sign");
		return verify(data.getBytes(UTF8), publicKey, sign);
	}

	/**
	 * 
	 * 功能描述 使用公钥对签名进行校验
	 * 
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:15:11
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		checkNull(data, "data");
		checkEmpty(publicKey, "public key");
		checkEmpty(sign, "sign");
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}

	/**
	 * 
	 * 功能描述 使用私钥进行解密并对解密结果进行BASE64编码
	 * 
	 * @param encryptedData
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:50:10
	 */
	public static String decryptByPrivateKey(String encryptedData, String privateKey) throws Exception {
		checkEmpty(encryptedData, "encrypted data");
		checkEmpty(privateKey, "private key");
		return new String(decryptByPrivateKey(Base64.decodeBase64(encryptedData), privateKey), UTF8);
	}

	/**
	 * 
	 * 功能描述 使用私钥进行解密
	 * 
	 * @param encryptedData
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:19:36
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
		checkNull(encryptedData, "encrypted data");
		checkEmpty(privateKey, "private key");
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 
	 * 功能描述 使用公钥进行解密,并对解密结果进行BASE64编码
	 * 
	 * @param encryptedData
	 * @param publicKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:49:28
	 */
	public static String decryptByPublicKey(String encryptedData, String publicKey) throws Exception {
		checkEmpty(encryptedData, "encrypted data");
		checkEmpty(publicKey, "public key");
		return new String(decryptByPublicKey(Base64.decodeBase64(encryptedData), publicKey), UTF8);
	}

	/**
	 * 
	 * 功能描述 使用公钥解密
	 * 
	 * @param encryptedData
	 * @param publicKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:20:02
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
		checkNull(encryptedData, "encrypted data");
		checkEmpty(publicKey, "public key");
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 
	 * 功能描述 使用公钥加密,对加密结果进行BASE64编码
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:41:19
	 */
	public static String encryptByPublicKey(String data, String publicKey) throws Exception {
		checkEmpty(data, "data");
		checkEmpty(publicKey, "public key");
		return Base64.encodeBase64String(encryptByPublicKey(data.getBytes(UTF8), publicKey));
	}

	/**
	 * 
	 * 功能描述 使用公钥加密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:20:20
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		checkNull(data, "data");
		checkEmpty(publicKey, "public key");
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * 
	 * 功能描述 使用私钥加密,对加密结果进行BASE64编码
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:39:23
	 */
	public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
		checkEmpty(data, "data");
		checkEmpty(privateKey, "private key");
		return Base64.encodeBase64String(encryptByPrivateKey(data.getBytes(UTF8), privateKey));
	}

	/**
	 * 
	 * 功能描述 使用私钥加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 * @author WillYang
	 * @since 2016年10月20日上午8:20:33
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		checkNull(data, "data");
		checkEmpty(privateKey, "private key");
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	private static void checkNull(Object value, String description) {
		if (value == null) {
			throw new IllegalArgumentException(String.format("%s is null.", description));
		}
	}

	private static void checkEmpty(String value, String description) {
		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException(String.format("%s is null.", description));
		}
	}

	/**
	 * 利用java原生的摘要实现SHA256加密
	 * 
	 * @param str
	 *            加密后的报文
	 * @return
	 */
	public static String signSHA256(String str) {
		MessageDigest messageDigest;
		String encodeStr = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = ByteUtils.bytesToHexString(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}
}
