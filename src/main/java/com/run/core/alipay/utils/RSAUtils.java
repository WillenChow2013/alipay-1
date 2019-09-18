package com.run.core.alipay.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import net.sf.json.JSONObject;


public class RSAUtils {
	public static final String CHARSET = "UTF-8";
	public static final String RSA_ALGORITHM = "RSA";
	public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQgJItduPUnOloXHQkUSk2vMOfvBazTeeATGUtkv8ql6jDhLluXiIBhHcV3u96k5wfYSHQQVrfmfh4MEEBe0wT3fk/K0IxS4/xd1zk7xy8W1YK4hBJeUBPLulibzm0JO/9+R1sN38VEkgJOfzmqSG92dt2qNn6wEYAUkTPq/jJsQIDAQAB";
	public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJCAki1249Sc6WhcdCRRKTa8w5+8FrNN54BMZS2S/yqXqMOEuW5eIgGEdxXe73qTnB9hIdBBWt+Z+HgwQQF7TBPd+T8rQjFLj/F3XOTvHLxbVgriEEl5QE8u6WJvObQk7/35HWw3fxUSSAk5/OapIb3Z23ao2frARgBSRM+r+MmxAgMBAAECgYACq42H74l4NUoO1p6CyUkkrMpEmlolCAx+D2tTM0Mpy1GgA0TV6EFim8sDiXINZsrcvZO9YGfId/Tia6nX2CposHVdgm4pgItoQceRDarr6KkdsmUzjqYgLUW4mILWiJwVnXh8siq4YDeY6kVmLzBeFSLaCHeHe+qT8fCppASHPQJBAMfM9OjjYiL+HhaALW0LhXI3wzpTYptq80w73drLIGFSdJUXiEFnbYMgpdoEHYl4s/ZVnjojZFTTBRUJupThStMCQQC5Jb5bzqbYDA3lO6sylnuz12MHT2ix7iEIiggEzXv+QK+/UwO+aEr7LsJRvevhLpVNqERjY/E5whKYNrtQjj7rAkAPA8L/4V6VnJ0sObOwNyZvH+um7W9CmLXx66nPcGZdifHC5oLRz3D2YrSz/o0tsIltoJ+EZPZ8PWNrYlDIMWoLAkEAuFHW/NbySstVWgzZFnexlwyqTbDEbb6/rByedwmKk0garsIaTAjP/NhEI9SLa5ZQlQsbakco83M9x8NVH5E9GQJAWj/yZ8ic258Luh6e1ceID4ubcCZnHmVmql5tkY6WBx5SlLFO//SPOzESIRqfNT0aB8fCHxGFpynKtI3fqzrfUg==";


	/**
	 * 得到公钥
	 * 
	 * @param publicKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过X509编码的Key指令获得公钥对象
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		return key;
	}

	/**
	 * 得到私钥
	 * 
	 * @param privateKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static RSAPrivateKey getPrivateKey(String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过PKCS#8编码的Key指令获得私钥对象
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return key;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicEncrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET),
					publicKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */

	public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data),
					privateKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}
	
	public static String privateDecrypt(String data) {
		try {
			return privateDecrypt(data,getPrivateKey(PRIVATE_KEY));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */

	public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET),
					privateKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}
	

	/**
	 * 公钥解密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */

	public static String publicDecrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data),
					publicKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}
	

	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		byte[] resultDatas = out.toByteArray();
		IOUtils.closeQuietly(out);
		return resultDatas;
	}

	/**
	 * 私钥加密
	 * 
	 * @param data 需要加密的数据
	 * @return
	 */
	public static String privateEncrypt(String data) {
		try {
			return privateEncrypt(data, getPrivateKey(PRIVATE_KEY));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 * @return
	 */
	public static String publicDecrypt(String data) {
		try {
			return publicDecrypt(data, getPublicKey(PUBLIC_KEY));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args) {
		
		JSONObject json = new JSONObject();
		json.put("consNo", "01234567");
		System.out.println(privateEncrypt(json.toString()));
	}
	
}
