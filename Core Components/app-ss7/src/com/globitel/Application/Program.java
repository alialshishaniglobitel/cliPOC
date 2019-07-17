
package com.globitel.Application;

import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;
import com.globitel.xmlrpc.XmlRPCRequestImp;

public class Program {

	public static Application app = null;

	public static MessageLogger tdrLogger = MyLoggerFactory.getInstance().getLogger("tdr.log");

	public static void main(String args[]) {
		final String version = "2.0.0";
		final String releaseDate = "Jul 07, 2019";
		final String appName = "Gateway Mobile Location Center - GMLC";

		System.out.printf("Starting %s:%s ,Release Date = %s\n", appName, version, releaseDate);

		try {
			app = new Application();
			XmlRPCRequestImp.getInstance().app = app;
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error("Exception, insufficeint Application Arguments, " + e.getMessage());
			System.exit(0);
		}

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
