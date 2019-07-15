package com.globitel.utilities.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

/**
 *
 * @author Ayyoub Al-Qaissi
 */
public class ConfigurationManager {

	private static ConfigurationManager instance = null;
	private Properties properties = null;
	private static Object mutexObj = new Object();

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			synchronized (mutexObj) {
				instance = new ConfigurationManager();
			}
		}
		return instance;
	}

	private ConfigurationManager() {
		properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(Util.geConfigAbsolutePath("system.properties"))));

			if (properties == null) {
				System.out.println("problem in load system.properties file");
			}
		} catch (IOException ex) {
			System.out.println("Exception: " + ex.toString());
			MyLoggerFactory.getInstance().getAPILogger().debug("Exception: " + ex.toString());
		}

	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}

	public int getIntValue(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
}
