package com.alankin.center.common.encrypt;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DESUtils {

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	private static final String AlgorithmDES = "DES"; // 定义 加密算法,可用'DES','DESede','Blowfish'
	private static final String Algorithm3DES = "DESede"; // 定义 加密算法,可用'DES','DESede','Blowfish'

	/**
	 * encrypt data by des ECB
	 * 
	 * @param key
	 * @param src
	 * @return
	 */
	public static byte[] EncryptDES(byte[] key, byte[] src, boolean isPadding) throws Exception {

		// 生成密钥
		SecretKey deskey = new SecretKeySpec(key, AlgorithmDES);
		// 加密
		String paddingMode = null;
		if (isPadding) {
			paddingMode = "/ECB/PKCS5Padding";
		} else {
			paddingMode = "/ECB/NoPadding";
		}

		Cipher c1 = Cipher.getInstance(AlgorithmDES + paddingMode);
		c1.init(Cipher.ENCRYPT_MODE, deskey);
		return c1.doFinal(src);

	}

	public static byte[] Encrypt3DES(byte[] key, byte[] src, boolean isPadding) throws Exception {

		// 生成密钥
		SecretKey deskey = new SecretKeySpec(key, Algorithm3DES);

		String paddingMode = null;
		if (isPadding) {
			paddingMode = "/ECB/PKCS5Padding";
		} else {
			paddingMode = "/ECB/NoPadding";
		}
		// 加密
		Cipher c1 = Cipher.getInstance(Algorithm3DES + paddingMode);
		c1.init(Cipher.ENCRYPT_MODE, deskey);
		return c1.doFinal(src);

	}

	/**
	 * decrypt data by des ECB
	 * 
	 * @param key
	 * @param src
	 * @return
	 */
	public static byte[] DecryptDES(byte[] key, byte[] src, boolean isPadding) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(key, AlgorithmDES);
			String paddingMode = null;
			if (isPadding) {
				paddingMode = "/ECB/PKCS5Padding";
			} else {
				paddingMode = "/ECB/NoPadding";
			}
			// 解密
			Cipher c1 = Cipher.getInstance(AlgorithmDES + paddingMode);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	public static byte[] Decrypt3DES(byte[] key, byte[] src, boolean isPadding) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(key, Algorithm3DES);
			String paddingMode = null;
			if (isPadding) {
				paddingMode = "/ECB/PKCS5Padding";
			} else {
				paddingMode = "/ECB/NoPadding";
			}
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm3DES + paddingMode);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

}
