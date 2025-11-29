package com.fintrust.security;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptionUtil {
//	Generated key
	private String ENCRIPTION_ALGROTHIM = "RSA";
	KeyPair keyPair = null; 
	public EncryptionUtil() {
		try {
			keyPair = generateSecretKey();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private KeyPair generateSecretKey() throws Exception{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ENCRIPTION_ALGROTHIM);	
		//keyGen.initialize(2048);
		SecureRandom secRandom = new SecureRandom();
		keyGen.initialize(2048, secRandom);		

		KeyPair key = keyGen.generateKeyPair();
		return key;
	}
	
	/**
	 * Encrypte the message
	 * @param data
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRIPTION_ALGROTHIM);
		cipher.init(cipher.ENCRYPT_MODE, keyPair.getPublic());
		byte[] encryptedBytes = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);		
	}
	
	/**
	 * Decrypte message
	 * @param encryptedData
	 * @param key
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private String decrypt(String encryptedData) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRIPTION_ALGROTHIM);
		cipher.init(cipher.DECRYPT_MODE, keyPair.getPrivate());
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(decryptedBytes);
	}
	
	
	public static void main(String args[]) throws Exception {
		String message = "Hello, how are you?";
		
		EncryptionUtil encryptionUtil =  new EncryptionUtil();
		
//		Encrypting
		String encryptedData = encryptionUtil.encrypt(message);
		
//		Decrypting
		String decyptedMess = encryptionUtil.decrypt(encryptedData);
		
		System.out.println("Orginal message: " + message);
		System.out.println("Encrypted message: " + encryptedData);
		System.out.println("Decrypted message: " + decyptedMess);
	}
}
