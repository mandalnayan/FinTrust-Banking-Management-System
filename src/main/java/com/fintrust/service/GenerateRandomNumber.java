package com.fintrust.service;

import java.util.Random;

public class GenerateRandomNumber {
	private static final Random RANDOM = new Random(); // or SecureRandom

	public static long generateRandomNumber(long min, long max) {

		long randomNo = min + (long) ((max - min + 1) * RANDOM.nextDouble());

		return randomNo;
	}
}
