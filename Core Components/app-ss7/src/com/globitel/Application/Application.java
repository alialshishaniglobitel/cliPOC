
package com.globitel.Application;

import com.globitel.Diameter.AppConfig;
import com.globitel.Diameter.MessageBuffers;
import com.globitel.Diameter.MessageResponder;
import com.globitel.Diameter.Interfaces.IAppConfig;
import com.globitel.Diameter.Interfaces.IAssociationManager;
import com.globitel.Diameter.Interfaces.IMessageResponder;
import com.globitel.Logic.Logic;
import com.globitel.SS7.RequestHandler;
import com.globitel.XmlDiameter.xml.IXmlMessageFactory;
import com.globitel.XmlDiameter.xml.XmlMessageFactory;
import com.globitel.common.utils.Common;
import com.globitel.diameterCodec.Diameter.DiameterMessage;
import com.globitel.diameterCodec.interfaces.AvpDefinitions;
import com.globitel.sctp.SCTPModule;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;

public class Application {
	
	public RequestHandler requestHandler;

	public static int serverId;
	public static int numberOfThreads = 0;
	public static long sessionTimeOut = 0;
	public static SccpAddressImpl System_GT_SCCP = null;

	private static int systemGTRoutingIndicator;

	public static String systemGT;

	public static int queueLimit = 0;

	public static byte abortNetworkInd, atiNetworkInd;

	public static String[] activeRGS;

	public static int systemOPC;

	public static int[] RE_DPC, Abort_DPC;

	public static boolean ignoreUDTS_Enabled = false;

	public static boolean UL_FWD_DPCs_Enabled = false;
	public static int[] UL_FWD_DPCs;

	public static boolean Swap_HLRGT_UL;
	public static String HLRGT;

	public static String Home_CCNDC;

	public static int ULTicketTimeout;

	public static int RequestTimeOut;
	public static String SystemSCF;
	
	public static String xmlMessagesPath;
	public static String xmlCERForHSS;	
	public static String xmlCERForMME;		
	public static String originHost;
	public static String originRealm;
	public static String dstHost;
	public static String dstRealm;	
	public static int watchDogTime;
	public static boolean isSocketMultiThreaded;
	public static int associationTimeout;
	public static String[] hssAssociations;
	public static String[] mmeAssociations;
	
	
	public static IAppConfig configs;
	public static IXmlMessageFactory xmlFactory;
	public static IMessageResponder responder;
	
	private static SCTPModule SCTP;
	public static MessageBuffers msgBuffer;
	public static Logic[] logic;

	public Application() {
		try {
			initializeHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initializeHandler() {
		systemOPC = ConfigurationManager.getInstance().getIntValue("SystemOPC");

		serverId = ConfigurationManager.getInstance().getIntValue("ServerID");

		numberOfThreads = ConfigurationManager.getInstance().getIntValue("NumberOfThreads");

		sessionTimeOut = ConfigurationManager.getInstance().getIntValue("SESSION_TIMEOUT");

		RequestTimeOut = ConfigurationManager.getInstance().getIntValue("RequestTimeOut");

		queueLimit = ConfigurationManager.getInstance().getIntValue("WorkerQueueLimitSize");

		abortNetworkInd = Common.hexStringToByteArray(
				ConfigurationManager.getInstance().getValue("abort_net_ind").replaceAll("0x", "").replaceAll(" ", ""))[0];
		atiNetworkInd = Common.hexStringToByteArray(
				ConfigurationManager.getInstance().getValue("ati_net_ind").replaceAll("0x", "").replaceAll(" ", ""))[0];

		systemGT = ConfigurationManager.getInstance().getValue("SystemGT");
		systemGTRoutingIndicator = ConfigurationManager.getInstance().getIntValue("SystemGT_routing_indicator");

		constructSystemGT_SCCP();

		SystemSCF = ConfigurationManager.getInstance().getValue("SystemSCF");

		activeRGS = ConfigurationManager.getInstance().getValue("Active_RGS").split(",");

		int socketFlush = ConfigurationManager.getInstance().getIntValue("SocketFlushEnabled");

		ignoreUDTS_Enabled = ConfigurationManager.getInstance().getIntValue("Ignore_UDTS") == 1 ? true : false;

		UL_FWD_DPCs_Enabled = ConfigurationManager.getInstance().getIntValue("UL_FWD_DPCs_Enabled") == 1 ? true : false;
		if (UL_FWD_DPCs_Enabled) {
			UL_FWD_DPCs = Common.stringArrayToIntegerArray(ConfigurationManager.getInstance().getValue("UL_FWD_DPCs").split(","));
			if (UL_FWD_DPCs == null) {
				UL_FWD_DPCs_Enabled = false;
			}
		}

		RE_DPC = Common.stringArrayToIntegerArray(ConfigurationManager.getInstance().getValue("RE_DPC").split(","));
		Abort_DPC = Common.stringArrayToIntegerArray(ConfigurationManager.getInstance().getValue("Abort_DPC").split(","));

		Home_CCNDC = ConfigurationManager.getInstance().getValue("Home_CCNDC");

		Swap_HLRGT_UL = ConfigurationManager.getInstance().getIntValue("Swap_HLRGT_UL") == 1 ? true : false;
		HLRGT = ConfigurationManager.getInstance().getValue("HLRGT");

		ULTicketTimeout = Integer
				.parseInt(ConfigurationManager.getInstance().getValue("ULTicketTimeout"));

//		ticketHandler = new TicketHandler();
//		ticketHandler.start();
		
		//diameter configuration
		xmlMessagesPath = ConfigurationManager.getInstance().getValue("XmlMessagesPath");	
		xmlCERForHSS = ConfigurationManager.getInstance().getValue("XML_CER_FOR_HSS");	
		xmlCERForMME = ConfigurationManager.getInstance().getValue("XML_CER_FOR_MME");	
				
		originHost = ConfigurationManager.getInstance().getValue("OriginHost");
		originRealm = ConfigurationManager.getInstance().getValue("OriginRealm");
		dstHost = ConfigurationManager.getInstance().getValue("DstHost");
		dstRealm = ConfigurationManager.getInstance().getValue("DstRealm");
		watchDogTime = ConfigurationManager.getInstance().getIntValue("WatchDogTime");
		isSocketMultiThreaded = ConfigurationManager.getInstance().getIntValue("SocketMultiThreaded") == 1 ? true : false;		
		associationTimeout = ConfigurationManager.getInstance().getIntValue("AssociationTimeout");
		hssAssociations = ConfigurationManager.getInstance().getValue("HSS_ASSOCIATIONS").split(",");
		mmeAssociations = ConfigurationManager.getInstance().getValue("MME_ASSOCIATIONS").split(",");
		
		RequestHandler.initializeRGS(activeRGS);
		
		SessionPool.initialize(serverId, numberOfThreads);
		
		requestHandler = new RequestHandler(socketFlush == 1 ? true : false, numberOfThreads, true,
				Application.queueLimit, Application.serverId);		
		

		configs = new AppConfig();
		((AppConfig) configs).initialize(originHost, originRealm, dstHost, dstRealm, watchDogTime,
				isSocketMultiThreaded);
		DiameterMessage.FillAVPs(new AvpDefinitions().getAvpCodes());
		xmlFactory = new XmlMessageFactory();
		xmlFactory.initialize(xmlMessagesPath);
		responder = new MessageResponder(configs, xmlFactory);
		
		try {
			SCTP = new SCTPModule();
			SCTP.initializeStack();
			//SCTP.sendSctpMsg();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		msgBuffer = new MessageBuffers(numberOfThreads);
		msgBuffer.start();
		
		logic = new Logic[numberOfThreads];
		for (int processorID = 0; processorID < numberOfThreads; processorID++) {
			try {
				logic[processorID] = new Logic(processorID, responder, SCTP, msgBuffer,
						SessionPool.getSessionManager(processorID));
				logic[processorID].start();
			} catch (Exception e) {
				MyLoggerFactory.getInstance().getAppLogger().error(
						String.format("Failed to create processor[%d], Exception; %s", processorID, e.getMessage()), e);
				System.exit(0);
			}
		}		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					msgBuffer.end();
					msgBuffer.join(3000);
					Thread.sleep(200);
					System.out.println("Shutting down ...");
					tearDown();
				} catch (InterruptedException e) {
					MyLoggerFactory.getInstance().getAppLogger().error(
							String.format("Failure Dimeter Shutdown Hook, Exception; %s.", e.getMessage()), e);
				} catch (Exception e) {
					MyLoggerFactory.getInstance().getAppLogger().error(
							String.format("Failure Dimeter Shutdown Hook, Exception; %s.", e.getMessage()), e);
				}
			}
		});	
	}

	/*
	private void initializeDiameter() {
		numberOfThreadsFor4G = ConfigurationManager.getInstance().getIntValue("NumberOfThreadsFor4G");

		xmlMessagesPath = ConfigurationManager.getInstance().getValue("XmlMessagesPath");

		String originHost = ConfigurationManager.getInstance().getValue("OriginHost");
		String originRealm = ConfigurationManager.getInstance().getValue("OriginRealm");
		String dstHost = ConfigurationManager.getInstance().getValue("DstHost");
		String dstRealm = ConfigurationManager.getInstance().getValue("DstRealm");

		boolean isSocketMultiThreaded = ConfigurationManager.getInstance().getIntValue("SocketMultiThreaded") == 1 ? true : false;

		List<String> ipList = Arrays.asList(localIPS.split(","));
		IAppConfig configs = new AppConfig();

		configs.initialize(originHost, originRealm, dstHost, dstRealm, watchDogTimeInt,
				isSocketMultiThreaded);
		IMessageResponder responder = new MessageResponder(configs, xmlFactory);

		AssociationWrapper.initResponder(responder, xmlFactory, configs);

		DiameterMessage.FillAVPs(new AvpDefinitions().getAvpCodes());

		try {
			manager = new AssociationManager(configs, serverName, stackName, reconnectTime, ipList, publicPort,
					sctpMode ? IpChannelType.SCTP : IpChannelType.TCP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Failed to get connection manager for association, Exception; %s", Common.printStackTrace(e)));
			System.exit(0);
		}
		manager.setPeers(peers);

		msgBuffer = new MessageBuffers(numberOfThreadsFor4G);
		msgBuffer.start();

		try {
			manager.createAssociations(peers, ipList, msgBuffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error(String.format("Failed to create association, Exception; %s", Common.printStackTrace(e)));
			System.exit(0);
		}

		logic = new Logic[numberOfThreadsFor4G];
		for (int processorID = 0; processorID < numberOfThreadsFor4G; processorID++) {
			try {
				logic[processorID] = new Logic(processorID, responder, manager, msgBuffer,
						SessionPool.getSessionManagerFor4G(processorID));

				logic[processorID].start();
			} catch (Exception e) {
				MyLoggerFactory.getInstance().getAppLogger().error(String.format("Failed to create processor[%d], Exception; %s", processorID,
						Common.printStackTrace(e)));
				System.exit(0);
			}
		}

		((AssociationManager) manager).start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					msgBuffer.end();
					msgBuffer.join(3000);
					Thread.sleep(200);
					System.out.println("Shutting down ...");
					// some cleaning up code...
					((AssociationManager) manager).tearDown();
					((AssociationManager) manager).join(10000);
					tearDown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					MyLoggerFactory.getInstance().getAppLogger().error(
							String.format("Failure Dimeter Shutdown Hook, Exception; %s.", Common.printStackTrace(e)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					MyLoggerFactory.getInstance().getAppLogger().error(
							String.format("Failure Dimeter Shutdown Hook, Exception; %s.", Common.printStackTrace(e)));
				}
			}
		});
	}
*/	

	private void constructSystemGT_SCCP() {

		boolean odd = systemGT.length() % 2 == 0 ? false : true;
		RoutingIndicator ri = systemGTRoutingIndicator == 0 ? RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE
				: RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN;
		int translationType = 0x00;
		EncodingScheme encodingScheme = odd == true ? BCDOddEncodingScheme.INSTANCE : BCDEvenEncodingScheme.INSTANCE;
		NumberingPlan numberingPlan = NumberingPlan.ISDN_TELEPHONY;

		NatureOfAddress natureOfAddress = NatureOfAddress.INTERNATIONAL;
		GlobalTitle gt = new GlobalTitle0100Impl(systemGT, translationType, encodingScheme, numberingPlan,
				natureOfAddress);
		int dpc = 0;
		// GMLC
		int ssn = 145;
		System_GT_SCCP = new SccpAddressImpl(ri, gt, dpc, ssn);

	}

	protected static void tearDown() throws Exception {
		SessionPool.endSessions();
	}
}