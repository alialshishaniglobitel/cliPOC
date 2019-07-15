package com.globitel.m3ua.commons;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.mobicents.protocols.api.Association;
//import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitiveFactory;

import com.globitel.m3ua.commons.Definitions.MTP3Data;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

import io.netty.buffer.Unpooled;


public class M3UAModule {

	public M3UAManagementImpl getM3uaMgmt() {
		return m3uaMgmt;
	}

	public static MessageLogger logger =  MyLoggerFactory.getDefaultLogger();

	// SCTP
	//private ManagementImpl sctpManagement;
	private NettySctpManagementImpl sctpManagement;
	
	// M3UA
	private M3UAManagementImpl m3uaMgmt;

	//private Mtp3UserPartListenerImpl mtp3UserPartListener = null;
	//private M3UAManagementEventListenerImpl m3uaManagementEventListener = null;
	
	protected final org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl factory = new org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl();

	private void initSCTP() throws Exception
	{
		Application.logger.debug("Initializing SCTP Stack .... " + Application.SCTP_MANAGER_NAME);
		this.sctpManagement = new NettySctpManagementImpl(Application.SCTP_MANAGER_NAME);
		//this.sctpManagement = new ManagementImpl(name);
		this.sctpManagement.setSingleThread(false);
		this.sctpManagement.setWorkerThreads(100);
		this.sctpManagement.start();
		//this.sctpManagement.setConnectDelay(10000);
		this.sctpManagement.addManagementEventListener(new SCTPManagementEventListenerImpl());
		if(Application.GENERATE_SCTP_CONFIG)
		{
			this.sctpManagement.removeAllResourses();
		}
		
		Application.logger.debug("Initialized SCTP Stack ....");
	}
	
	private void startSCTP(IpChannelType ipChannelType) throws Exception
	{
		logger.debug("Starting SCTP Stack");
		if(Application.IS_SERVER)
		{
			// 1. Create SCTP Server
			//String[] extraHostAddresses = {"192.168.0.111"}; // for multi-homing
			sctpManagement.addServer(Application.SERVER_NAME, Application.SERVER_IP, Application.SERVER_PORT, ipChannelType, false, 0, null /*extraHostAddresses*/);
			// 2. Create SCTP Server Association
			sctpManagement.addServerAssociation(Application.CLIENT_IP, Application.CLIENT_PORT, Application.SERVER_NAME, Application.ASSOCIATION_NAME, ipChannelType);
			
			//sctpManagement.addServerAssociation("192.168.0.229", 5002, Application.SERVER_NAME, "server assoc2", ipChannelType);
			// 3. Start Server
			sctpManagement.startServer(Application.SERVER_NAME);
			//sctpManagement.startAssociation(assocName);
		}
		else
		{
			//String[] extraHostAddresses = {"192.168.0.222"}; // for multi-homing
			sctpManagement.addAssociation(Application.CLIENT_IP, Application.CLIENT_PORT, Application.SERVER_IP, Application.SERVER_PORT, Application.ASSOCIATION_NAME, ipChannelType, null/*extraHostAddresses*/);
		}
	}
	
	private void initM3UA() throws Exception 
	{
		logger.debug("Initializing M3UA Stack .... " + Application.M3UA_MANAGER_NAME);
		this.m3uaMgmt = new M3UAManagementImpl(Application.M3UA_MANAGER_NAME, null, null);
		this.m3uaMgmt.setTransportManagement(this.sctpManagement);
		this.m3uaMgmt.setDeliveryMessageThreadCount(100);
		this.m3uaMgmt.addMtp3UserPartListener(/*(Mtp3UserPartListener)*/ new Mtp3UserPartListenerImpl());
		this.m3uaMgmt.addM3UAManagementEventListener(new M3UAManagementEventListenerImpl());
		this.m3uaMgmt.start();
		/*
		this.m3uaMgmt.setHeartbeatTime(3000);
		*/
		if(Application.GENERATE_M3UA_CONFIG)
		{
			this.m3uaMgmt.removeAllResourses();
		}
		this.m3uaMgmt.setStatisticsEnabled(true);
				
		logger.debug("M3UA Stack Initialized Successfully....");
	}
	
	private void startM3UA() throws Exception
	{
		// Step 1 : Create App Server
		RoutingContext rc = factory.createRoutingContext(new long[] { Application.ROUTING_CONTEXT });
		TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
		if(Application.IS_SERVER)
		{
			//this.m3uaMgmt.createAs("RAS1", Functionality.SGW, ExchangeType.SE, IPSPType.CLIENT, rc, trafficModeType, 0, null);
			this.m3uaMgmt.createAs("RAS1", Functionality.IPSP, ExchangeType.SE, IPSPType.SERVER, rc, trafficModeType, 0, null);
		}
		else
		{
			this.m3uaMgmt.createAs("RAS1", Functionality.IPSP, ExchangeType.SE, IPSPType.CLIENT, rc, trafficModeType, 0, null);
		}
		
		// Step 2 : Create ASP
		this.m3uaMgmt.createAspFactory("RASP1", Application.ASSOCIATION_NAME);

		// Step3 : Assign ASP to AS
		this.m3uaMgmt.assignAspToAs("RAS1", "RASP1");

		// Step 4: Add Route. Remote point code is 2
		this.m3uaMgmt.addRoute(Application.REMOTE_PC, -1, -1, "RAS1");
	}
	
	protected void initializeStack(/*IpChannelType ipChannelType*/) throws Exception {

		this.initSCTP();
		Thread.sleep(100);
		
		if(Application.GENERATE_SCTP_CONFIG)
		{
			this.startSCTP(IpChannelType.TCP);
			Thread.sleep(100);
		}
		
		this.initM3UA();
		Thread.sleep(100);
		
		if(Application.GENERATE_M3UA_CONFIG)
		{
			this.startM3UA();
			Thread.sleep(500);
			// Start ASP
			m3uaMgmt.startAsp("RASP1");
		}
				
		logger.debug("[[[[[[[[[[    M3UA Stack Started       ]]]]]]]]]]");
	}
	
	
	public boolean isAppServersUp()
	{
		logger.debug("isAppServersUp started");
		boolean isUp = false;
		for(As as : m3uaMgmt.getAppServers())
		{
			if(as.isUp())
			{
				isUp = true;
				break;
			}
		}
		logger.debug(String.format("isAppServersUp=%b", isUp));
		return isUp;
	}
	
	public boolean isSCTPAssociationUp()
	{
		boolean isUp = false;
		
		Association assoc;
		for(String assocKey : sctpManagement.getAssociations().keySet())
		{
			assoc = sctpManagement.getAssociations().get(assocKey);
			assoc.isUp();
			assoc.isConnected();
			assoc.isStarted();
		}
		
		return isUp;
	}
	
	public void sendM3UAMsg(MTP3Data mtp3Data, byte[] sccpData) throws IOException
	{
		//System.out.println("M3UAModule::sendM3UAMsg");
		logger.debug("M3UAModule::sendM3UAMsg");
		Mtp3TransferPrimitiveFactory factory = m3uaMgmt.getMtp3TransferPrimitiveFactory();
		//byte[] data = fromHex("090103070b044302000804430100085262504804000000016b1e281c060700118605010101a011600f80020780a1090607040000010013026c28a12602010002013b301e04010f0410aa98aca65acd6236190e37cbe572b9118007911326888300f2");
		Mtp3TransferPrimitive mtp3TransferPrimitive = factory.createMtp3TransferPrimitive(mtp3Data.serviceInfo_si, mtp3Data.serviceInfo_ni, 0, mtp3Data.opc, mtp3Data.dpc, mtp3Data.sls, sccpData); 
		m3uaMgmt.sendMessage(mtp3TransferPrimitive);
		//System.out.println("M3UAModule::sendM3UAMsg end");
		//logger.debug("M3UAModule::sendM3UAMsg end");
	}
	
	public void sendM3UAMsg() throws IOException
	{
		//System.out.println("Sending MTP3 Message...");
		Mtp3TransferPrimitiveFactory factory = m3uaMgmt.getMtp3TransferPrimitiveFactory();
		byte[] data = Utility.fromHex("098103101b0d120600710469770116813351000b12070012046956066104705a62584804f300015b6b1e281c060700118605010101a011600f80020780a1090607040000010001036c30a12e0201010201023026040824010116813351f0810791695606610470040791695606610470a608800204c0850204f0");
		//byte[] data = Utility.fromHex("098103101b0d120600710469770116813351000b1207001204"); /*not complete*/
		//byte[] data = Utility.fromHex("090103070b044302000804430100085262504804000000016b1e281c060700118605010101a011600f80020780a1090607040000010013026c28a12602010002013b301e04010f0410aa98aca65acd6236190e37cbe572b9118007911326888300f2");
		Mtp3TransferPrimitive mtp3TransferPrimitive = factory.createMtp3TransferPrimitive(3, 0, 0, 8673, 8533, 10, data); 
		m3uaMgmt.sendMessage(mtp3TransferPrimitive);
		//System.out.println("After sending MTP3 Message...");
	}
	
}
