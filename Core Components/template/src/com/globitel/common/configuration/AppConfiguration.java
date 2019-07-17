package com.globitel.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfiguration extends ConfigBase
{
	public static Properties prop = new Properties();

	private static String configPath = "";
	
	public static void load()
	{
		try
		{
			prop = new Properties();
			configPath = System.getProperty("config.dir");
			File file = new File(configPath + "/config.cfg");
			FileInputStream configFile = new FileInputStream(file);
			prop.load(configFile);
			configFile.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception, AppConfiguration:Error: " + e.getMessage());
		}
	}

	public static String getString(String key)
	{
		return prop.getProperty(key);
	}
	
	public static boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(prop.getProperty(key));
	}

	public static int getInt(String key)
	{
		String propValue = prop.getProperty(key);
		return Integer.parseInt(propValue.replaceAll(" ", ""));
	}
}
