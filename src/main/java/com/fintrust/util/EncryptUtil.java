package com.fintrust.util;

	import javax.crypto.Cipher;
	import javax.crypto.SecretKey;
	import javax.crypto.spec.GCMParameterSpec;
	import javax.crypto.spec.SecretKeySpec;
	import java.util.Base64;

	public class EncryptUtil {

	    private static final int GCM_TAG_LENGTH = 128;
	    private static final String secretKey = "fgso98/uasjX4kblCr/YSD0UW31DOmAslKZnvC6Rxfg=";

	    public static String encrypt(String plainText, String base64Key) throws Exception {
	        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
	        SecretKey key = new SecretKeySpec(keyBytes, "AES");

	        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

	        byte[] iv = new byte[12]; // recommended IV size for GCM
	        new java.security.SecureRandom().nextBytes(iv);

	        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
	        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

	        byte[] cipherText = cipher.doFinal(plainText.getBytes());

	        // Store: iv + ciphertext (together)
	        byte[] encrypted = new byte[iv.length + cipherText.length];
	        System.arraycopy(iv, 0, encrypted, 0, iv.length);
	        System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

	        return Base64.getEncoder().encodeToString(encrypted);
	    }
	    
	    public static String decrypt(String encrypted, String base64Key) throws Exception {

	        byte[] decoded = Base64.getDecoder().decode(encrypted);

	        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
	        SecretKey key = new SecretKeySpec(keyBytes, "AES");

	        byte[] iv = new byte[12];
	        System.arraycopy(decoded, 0, iv, 0, 12);

	        byte[] cipherText = new byte[decoded.length - 12];
	        System.arraycopy(decoded, 12, cipherText, 0, cipherText.length);

	        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
	        cipher.init(Cipher.DECRYPT_MODE, key, spec);

	        byte[] plain = cipher.doFinal(cipherText);
	        return new String(plain);
	    }
	    
	    
	    public static String getSecretKey() {
	    	return secretKey;
	    }
	    

	}

