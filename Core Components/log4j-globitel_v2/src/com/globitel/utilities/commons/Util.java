package com.globitel.utilities.commons;

import java.io.File;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Util {

	public static String geConfigAbsolutePath(String config) {
		String jarPath = "";
		try {
			if (System.getProperty("config.dir") != null) {
				jarPath = System.getProperty("config.dir") + "/" + config;
			} else {
				jarPath = Util.class.getResource("Util.class").toString();
				jarPath = jarPath.replace("%20", " ");
				System.out.println("jarPath-step1= " + jarPath);
				String[] pathArr = jarPath.split("jar:file:/");
				System.out.println("pathArr= " + pathArr.length);
				jarPath = pathArr[1];
				System.out.println("jarPath-step2= " + jarPath);
				pathArr = jarPath.split("!");
				jarPath = pathArr[0];
				System.out.println("jarPath-step3= " + jarPath);
				File jarFile = new File(jarPath);
				File configFile = new File((new File(jarFile.getParent())).getParent() + "/config/" + config);
				jarPath = "/" + configFile.getPath();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jarPath;
	}

	public static void geLog4jConfigPath() {
		String jarPath = "";
		try {
			if (System.getProperty("config.dir") != null) {
				System.setProperty("log4j.configurationFile", System.getProperty("config.dir") + "/log4j2.xml");
			} else {
				jarPath = Util.class.getResource("Util.class").toString();
				jarPath = jarPath.replace("%20", " ");
				System.out.println("jarPath-step1= " + jarPath);
				String[] pathArr = jarPath.split("jar:file:/");
				System.out.println("pathArr= " + pathArr.length);
				jarPath = pathArr[1];
				System.out.println("jarPath-step2= " + jarPath);
				pathArr = jarPath.split("!");
				jarPath = pathArr[0];
				System.out.println("jarPath-step3= " + jarPath);
				File jarFile = new File(jarPath);
				System.setProperty("log4j.configurationFile",
						(new File(jarFile.getParent())).getParent() + "/config/log4j2.xml");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String geConfigCommonPath(String config) {
		String path = "NA";
		try {

			if (System.getenv("APP_HOME") != null) {
				path = System.getenv("APP_HOME") + "/" + config;
			} else if (System.getenv("CATALINA_HOME") != null) {
				// $CATALINA_HOME/webapps/config
				File configFile = new File(System.getenv("CATALINA_HOME") + "/webapps/config/" + config);
				System.out.println(configFile.getAbsolutePath());
				path = configFile.getAbsolutePath();
			} else {
				path = ConfigurationManager.getInstance().getValue("path.common") + "/" + config;
			}

			MyLoggerFactory.getInstance().getAPILogger().debug("ConfigCommonPath=" + path);
		} catch (Exception ex) {
			MyLoggerFactory.getInstance().getAPILogger().debug("Exception: " + ex.toString());
		}

		return path;
	}

}
