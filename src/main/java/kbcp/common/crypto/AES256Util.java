package kbcp.common.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
public class AES256Util {
	/**
	 * 16자리의 키값을 입력하여 객체를 생성
	 *
	 * @param key 암/복호화를 위한 키값
	 * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
	 */
	@Value("${app.crypto.key16}")
	private String key16;

	private String iv;
	private Key keySpec;

	public AES256Util() {
		
		try{
			this.iv = key16.substring(0, 16);
			byte[] keyBytes = new byte[16];
			byte[] b = key16.getBytes("UTF-8");
			int len = b.length;
			if (len > keyBytes.length) {
				len = keyBytes.length;
			}
			System.arraycopy(b, 0, keyBytes, 0, len);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

			this.keySpec = keySpec;			
		} catch(UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * AES256 으로 암호화
	 * 
	 * @param str 암호화할 문자열   
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public String encrypt(String str) throws NoSuchAlgorithmException,
			GeneralSecurityException, UnsupportedEncodingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));
		return enStr;
	}

	/**
	 * AES256으로 암호화된 txt를 복호화
	 * 
	 * @param str 복호화할 문자열    
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public String decrypt(String str) throws NoSuchAlgorithmException,
			GeneralSecurityException, UnsupportedEncodingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
		byte[] byteStr = Base64.decodeBase64(str.getBytes());
		return new String(c.doFinal(byteStr), "UTF-8");
	}

	/*
	public static void main(String[] args) {
		
		testCrypto();

	}
	
	private void testCrypto() {
		
		log.debug("sta testCrypto");
		
		
		try {
			AES256Util util = new AES256Util();
			log.debug("key: " + key16);
			
			String testData = "";
			
			testData = "1q2w3e1!";
			log.debug("testData: " + testData);
			
			String encData = util.encrypt(testData);
			log.debug("encrypt testData: " + encData);
			
			String dncData = util.decrypt(encData);
			log.debug("decryp testDatat: " + dncData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("end testCrypto");
	}
	 */
	
}
