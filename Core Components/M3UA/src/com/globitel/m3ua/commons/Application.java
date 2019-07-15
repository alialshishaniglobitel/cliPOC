package com.globitel.m3ua.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.mobicents.protocols.api.IpChannelType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UACounterProviderImpl;

import com.globitel.m3ua.dialogic.DialogicReader;
import com.globitel.m3ua.dialogic.DialogicWriter;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Application {

	public static MessageLogger logger;
	
	
	public static String SERVER_NAME = "testserver";
	public static String SERVER_IP = "127.0.0.1";
	public static int SERVER_PORT = 2309;
	public static int ROUTING_CONTEXT = 1001;
	public static String ASSOCIATION_NAME = "clientAsscoiation";
	public static String CLIENT_IP = "127.0.0.1";
	public static int CLIENT_PORT = 2905;
	public static int REMOTE_PC = 2;
	public static boolean GENERATE_SCTP_CONFIG = false;
	public static boolean GENERATE_M3UA_CONFIG = false;
	
	public static String SCTP_MANAGER_NAME = "Client";
	public static String M3UA_MANAGER_NAME = "Client";
	
	public static boolean USE_TCP = false;
	public static boolean IS_SERVER = false;
	public static int TESTER_MSG_PER_SECOND = 1;
	public static int TESTER_MSG_LOOPS = 30;
	
	public static short READER_MODULE_ID;
	public static short WRITER_MODULE_ID;
	
	public final static M3UAModule M3UA = new M3UAModule();
	//public final static Counters m3uaCounters = new Counters();
	
	public static DialogicReader dialogicReader = null;
	public static DialogicWriter dialogicWriter = null;
	
	public static void main(String[] args) 
	{
		InitializeLoggerDirectories();
		// start logger
		logger = MyLoggerFactory.getDefaultLogger();
		
		logger.info("FileVersion: " + Assembly.FileVersion);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("./m3uaversion");
			writer.write("FileVersion: " + Assembly.FileVersion);
			writer.close();
			writer = null;
		}
		catch (FileNotFoundException e1) {
			logger.warn("FileNotFoundException while creating the version file: %s" + e1.getMessage());
			e1.printStackTrace();
		}
		catch(Exception e)
		{
			logger.warn("Exception while creating the version file: %s" + e.getMessage());
			e.printStackTrace();
		}
		
		ConfigurationManager configMngr = ConfigurationManager.getInstance();
		try{USE_TCP = (0==Integer.parseInt(configMngr.getValue("USE_TCP").trim()))?false:true;}catch(Exception e) {}
		try{IS_SERVER = (0==Integer.parseInt(configMngr.getValue("IS_SERVER").trim()))?false:true;}catch(Exception e) {}
		
		SCTP_MANAGER_NAME = configMngr.getValue("SCTP_MANAGER_NAME").trim();
		M3UA_MANAGER_NAME = configMngr.getValue("M3UA_MANAGER_NAME").trim();
		
		/*
		SERVER_NAME = configMngr.getValue("SERVER_NAME").trim();
		SERVER_IP = configMngr.getValue("SERVER_IP").trim();
		SERVER_PORT = Integer.parseInt(configMngr.getValue("SERVER_PORT").trim());
		ROUTING_CONTEXT = Integer.parseInt(configMngr.getValue("ROUTING_CONTEXT").trim());
		ASSOCIATION_NAME = configMngr.getValue("ASSOCIATION_NAME").trim();
		CLIENT_IP = configMngr.getValue("CLIENT_IP").trim();
		CLIENT_PORT = Integer.parseInt(configMngr.getValue("CLIENT_PORT").trim());
		//LOCAL_PC = Integer.parseInt(configMngr.getValue("LOCAL_PC").trim());		
		REMOTE_PC = Integer.parseInt(configMngr.getValue("REMOTE_PC").trim());
		*/
		READER_MODULE_ID = Short.parseShort(configMngr.getValue("READER_MODULE_ID").trim());
		WRITER_MODULE_ID = Short.parseShort(configMngr.getValue("WRITER_MODULE_ID").trim());
		
		TESTER_MSG_PER_SECOND = Integer.parseInt(configMngr.getValue("TESTER_MSG_PER_SECOND").trim());
		TESTER_MSG_LOOPS = Integer.parseInt(configMngr.getValue("TESTER_MSG_LOOPS").trim());
		
		try{GENERATE_SCTP_CONFIG = (0==Integer.parseInt(configMngr.getValue("GENERATE_SCTP_CONFIG").trim()))?false:true;}catch(Exception e) {}
		try{GENERATE_M3UA_CONFIG = (0==Integer.parseInt(configMngr.getValue("GENERATE_M3UA_CONFIG").trim()))?false:true;}catch(Exception e) {}
		
		Initialize();
		if(0 != Application.READER_MODULE_ID)
		{
			dialogicReader = new DialogicReader();
		}
		if(0 != Application.WRITER_MODULE_ID)
		{
			dialogicWriter = new DialogicWriter();
		}
	}
	
	public static void Initialize()
	{
		System.out.println("*************************************");
		System.out.println("************Initializing************");
		/*
		IpChannelType ipChannelType = IpChannelType.SCTP;
		if(USE_TCP)
		{
			ipChannelType = IpChannelType.TCP;
			System.out.println("***          TCP           ***");
		}
		else
		{
			ipChannelType = IpChannelType.SCTP;
			System.out.println("***          SCTP           ***");
		}
		*/
		System.out.println("*************************************");
		
		try {
			M3UA.initializeStack(/*ipChannelType*/);
			
			if(0 < TESTER_MSG_LOOPS && 0 < TESTER_MSG_PER_SECOND)
			{
				Thread.sleep(10000);
				
				System.out.println("Start Sending MTP3 Messages...");
				for(int i = 0; i < TESTER_MSG_LOOPS; ++i)
				{
					Date start = new Date();
					
					for(int j = 0; j < TESTER_MSG_PER_SECOND; ++j)
						M3UA.sendM3UAMsg();
					
					long sleepTime = 0;
					Date now = new Date();
					if(1000 < now.getTime() - start.getTime())
					{
						sleepTime = 1;
					}
					else
						sleepTime = 1000 - (now.getTime() - start.getTime());
					logger.info("sleep for:" + sleepTime);
					System.out.println("Sleep " + sleepTime + " after sending for second:" + i);
					Thread.sleep(sleepTime);
				}
				System.out.println("Finish Sending MTP3 Messages...");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void InitializeLoggerDirectories()
	{
		String strPath = "";
		File file = null;
		Properties prop = new Properties();
	    FileInputStream configFile = null;
	    try {configFile = new FileInputStream( System.getProperty("config.dir") + "/log4j.properties" );} 
	    catch (FileNotFoundException e) {e.printStackTrace();}
	    try {
			prop.load( configFile );
			strPath = prop.getProperty("log4j.appender.app.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			
			strPath = prop.getProperty("log4j.appender.smpp.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			
			strPath = prop.getProperty("log4j.appender.charging.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			
			strPath = prop.getProperty("log4j.appender.http.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			
			strPath = prop.getProperty("log4j.appender.profile.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			strPath = prop.getProperty("log4j.appender.ss7.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
			strPath = prop.getProperty("log4j.appender.tcp.File");
			if(null != strPath)
			{
				strPath = new File(strPath).getAbsolutePath();
				file = new File(strPath.substring(0, strPath.lastIndexOf(File.separator)));
				file.mkdirs();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    try {
			configFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
//	private static class Counters implements Runnable
//	{
//		public static int lastCountedTPS = 0;
//		private static long lastCounterMinute;
//		private static long lastTPSTimeMillis;
//		private static boolean gIsRunning = true;
//		
//		public Counters()
//		{		
//			new Thread(this).start();
//		}
//		
//		@Override
//		public void run() 
//		{
//			logger.info(String.format("ThreadID:%02d, Counters:run started", Thread.currentThread().getId()));
//			lastCounterMinute = System.currentTimeMillis() / (60*1000);
//			lastTPSTimeMillis = System.currentTimeMillis();
//			long lDurationInMillis = 1000;
//			while(gIsRunning)
//			{
//				try
//				{
//					// calculate TPS every 1 minute
//					if( lastTPSTimeMillis / (60*1000) != System.currentTimeMillis() / (60*1000))
//					{
//						lDurationInMillis = System.currentTimeMillis() - lastTPSTimeMillis;
//						if(lDurationInMillis < 1000)
//							lDurationInMillis = 1000; //to prevent divide by zero
//						//lastCountedTPS = (int) (counterTransactionsAll.getAndSet(0) / (lDurationInMillis/1000));
//						
//						
//						M3UACounterProviderImpl m3uaCounter = Application.M3UA.getM3uaMgmt().getCounterProviderImpl();
//						m3uaCounter.getPacketsPerAssTx(compainName);
//						m3uaCounter.getPacketsPerAssRx(compainName);
//						
//						lastTPSTimeMillis = System.currentTimeMillis();
//					}
//					
//					// log counters every 10 minutes
////					if( 0 == ((System.currentTimeMillis() / (60*1000)) % 10) && lastCounterMinute != System.currentTimeMillis() / (60*1000))
////					{
////						Date startTime = new Date(lastCounterMinute*60*1000);
////						lastCounterMinute = System.currentTimeMillis() / (60*1000);
////						CounterKey key;
////						CounterValue value;
////						synchronized(CountersArr)
////						{
////							Iterator<Entry<CounterKey, CounterValue>> iter = CountersArr.entrySet().iterator();
////							while(iter.hasNext())
////							{
////								@SuppressWarnings("rawtypes")
////								Map.Entry pair = (Map.Entry) iter.next();
////								key = (CounterKey) pair.getKey();
////								value = (CounterValue) pair.getValue();
////								CountersLogger.getInstance().addTDR(new Date(), startTime, key.ShortCode, key.ServiceID, key.ServiceName, value.CounterSessions, value.CounterTransactions/*, value.CounterSucceededHTTPActions + value.CounterSucceededSMSActions, value.CounterFailedHTTPActions + value.CounterFailedSMSActions, value.CounterSucceededHTTPActions, value.CounterFailedHTTPActions, value.CounterSucceededSMSActions, value.CounterFailedSMSActions*/);
////							}
////							CountersArr.clear();
////						}
////					}
//					TimeUnit.MILLISECONDS.sleep(500);
//				}
//				catch(InterruptedException e)
//				{
//					e.printStackTrace();
//					logger.error(String.format("ThreadID:%02d, Exception 1 in Counters:run, %s", Thread.currentThread().getId(), e.getMessage()));
//					try {
//						TimeUnit.MILLISECONDS.sleep(500);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//					logger.error(String.format("ThreadID:%02d, Exception 2 in Counters:run, %s", Thread.currentThread().getId(), e.getMessage()));
//					try {
//						TimeUnit.MILLISECONDS.sleep(500);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//			System.out.println("Exiting Counters thread");	
//			logger.info(String.format("ThreadID:%02d, Exiting Counters thread", Thread.currentThread().getId()));
//		}
//	}

}
