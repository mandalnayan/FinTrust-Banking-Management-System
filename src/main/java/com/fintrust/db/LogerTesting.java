package com.fintrust.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogerTesting {
	private static final Logger logger = LogManager.getLogger(LogerTesting.class);

	public static void main(String args[]) {
		logger.trace("This is a trace message");
		logger.debug("This is a debug message");
		logger.info("This is an INFO message");
		logger.warn("This is a WARN message");
		logger.error("This is a error message");
		logger.fatal("This is a fatal message (Critical error)");			
	}
}
