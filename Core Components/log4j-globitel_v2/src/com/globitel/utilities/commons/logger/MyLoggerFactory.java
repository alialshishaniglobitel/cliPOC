package com.globitel.utilities.commons.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;

import com.globitel.utilities.commons.Util;

public class MyLoggerFactory {

	private Map<String, MessageLogger> loggerMap = null;
	private static MyLoggerFactory instance = null;
	private static Object mutexObj = new Object();

	public static MyLoggerFactory getInstance() {
		if (instance == null) {
			synchronized (mutexObj) {
				if (instance == null) {
					instance = new MyLoggerFactory();
				}
			}
		}
		return instance;
	}

	private MyLoggerFactory() {
		super();
		Util.geLog4jConfigPath();
		loggerMap = new ConcurrentHashMap<String, MessageLogger>();
		loggerMap.put("app.log", new MessageLogger(LogManager.getLogger("app.log")));
		loggerMap.put("api.log", new MessageLogger(LogManager.getLogger("api.log")));
	}

	public MessageLogger getDefaultLogger() {
		return getLogger("app.log");
	}

	public MessageLogger getAppLogger() {
		return getLogger("app.log");
	}

	public MessageLogger getAPILogger() {
		return getLogger("api.log");
	}
	
	public MessageLogger getLogger(String name) {
		if (!loggerMap.containsKey(name)) {
			loggerMap.put(name, new MessageLogger(LogManager.getLogger(name)));			
		}
		return loggerMap.get(name);
	}
	
}
