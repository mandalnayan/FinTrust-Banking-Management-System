package com.fintrust.util;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyUtil {

	public static String getKey() {
		 try {
			 KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			 keyGen.init(256);
			 SecretKey key = keyGen.generateKey();

			 String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
			 return base64Key;
		 } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 return null;
	        
	}
}
