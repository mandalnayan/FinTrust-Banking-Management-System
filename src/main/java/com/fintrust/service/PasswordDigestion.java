package com.fintrust.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordDigestion {

	public static String digestPassword(String password) {
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(password.getBytes("UTF-8"));
			
			return toHashString(messageDigest.digest());
			
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String toHashString(byte [] digest) {
		StringBuffer digestPassword = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
//			convert byte to hexa-decimal
			digestPassword.append(Integer.toHexString(0xFF & digest[i]));			
		}
		System.out.println("\nDigested Password: " + digestPassword.toString());
		return digestPassword.toString();
	}
}
