package com.globitel.sctp;

import java.io.IOException;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;

import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

import io.netty.buffer.Unpooled;

public class SCTPModule extends Thread {
	private boolean testMode = ConfigurationManager.getInstance().getIntValue("Test_Mode") == 1 ? true : false;
	private boolean isServer = ConfigurationManager.getInstance().getIntValue("IS_SERVER") == 1 ? true : false;
	private IpChannelType ipChannelType = ConfigurationManager.getInstance().getIntValue("USE_TCP") == 1
			? IpChannelType.TCP
			: IpChannelType.SCTP;
	private String sctpManagerName = ConfigurationManager.getInstance().getValue("SCTP_MANAGER_NAME");
	private boolean generateSctpConfig = ConfigurationManager.getInstance().getIntValue("GENERATE_SCTP_CONFIG") == 1
			? true
			: false;
	private String serverIp = ConfigurationManager.getInstance().getValue("SERVER_IP");
	private int serverPort = ConfigurationManager.getInstance().getIntValue("SERVER_PORT");
	private String serverName = ConfigurationManager.getInstance().getValue("SERVER_NAME");
	private String associationName = ConfigurationManager.getInstance().getValue("ASSOCIATION_NAME");
	private String clientIp = ConfigurationManager.getInstance().getValue("CLIENT_IP");
	private int clientPort = ConfigurationManager.getInstance().getIntValue("CLIENT_PORT");

	private NettySctpManagementImpl sctpManagement;
	private ClientAssociationWarpper clientAssociation;
	private boolean running;

	private void initSCTP() throws Exception {
		MyLoggerFactory.getInstance().getAppLogger().debug("Initializing SCTP Stack .... " + sctpManagerName);

		this.sctpManagement = new NettySctpManagementImpl(sctpManagerName);
		this.sctpManagement.setSingleThread(false);
		this.sctpManagement.setWorkerThreads(10);
		this.sctpManagement.start();
		this.sctpManagement.setConnectDelay(10000);// Try connecting every 10 secs
		this.sctpManagement.addManagementEventListener(new SCTPManagementEventListenerImpl());

		if (generateSctpConfig) {
			this.sctpManagement.removeAllResourses();
		}

		MyLoggerFactory.getInstance().getAppLogger().debug("Initialized SCTP Stack ....");
	}

	private void startSCTP() throws Exception {
		MyLoggerFactory.getInstance().getAppLogger().debug("Starting SCTP Stack");
		if (isServer) {
			// 1. Create SCTP Server
			// String[] extraHostAddresses = {"192.168.0.111"}; // for multi-homing
			this.sctpManagement.addServer(serverName, serverIp, serverPort, ipChannelType, false, 0,
					null /* extraHostAddresses */);
			// 2. Create SCTP Server Association
			this.sctpManagement.addServerAssociation(clientIp, clientPort, serverName, associationName, ipChannelType);

			// sctpManagement.addServerAssociation("192.168.0.229", 5002,
			// Application.SERVER_NAME, "server assoc2", ipChannelType);
			// 3. Start Server
			this.sctpManagement.startServer(serverName);
			// sctpManagement.startAssociation(assocName);
		} else {
			// String[] extraHostAddresses = {"192.168.0.222"}; // for multi-homing
			this.clientAssociation = (ClientAssociationWarpper) this.sctpManagement.addAssociation(clientIp, clientPort, serverIp, serverPort,
					associationName, ipChannelType, null/* extraHostAddresses */);
		}
	}

	public void initializeStack() throws Exception {

		this.initSCTP();
		Thread.sleep(10000);

		if (generateSctpConfig) {
			this.startSCTP();
			Thread.sleep(10000);
		}

		while (!isSCTPAssociationUp()) {
			Thread.sleep(30000);	
		}
		
		MyLoggerFactory.getInstance().getAppLogger().debug("[[[[[[[[[[    SCTP Stack Started    ]]]]]]]]]]");		
	}

	public boolean isSCTPAssociationUp() throws Exception {
		boolean isUp = true;

		Association association;
		for (String assocKey : this.sctpManagement.getAssociations().keySet()) {
			association = this.sctpManagement.getAssociations().get(assocKey);
			isUp &= association.isUp();
			if(!association.isStarted()) {
				ClientAssociationWarpper associationWarpper = new ClientAssociationWarpper(association, this);
				association.setAssociationListener(associationWarpper);
				this.sctpManagement.startAssociation(assocKey);				
			} else {
				MyLoggerFactory.getInstance().getAppLogger().debug(assocKey + " isUp: " + association.isUp());
				MyLoggerFactory.getInstance().getAppLogger().debug(assocKey + " isConnected: " + association.isConnected());
				MyLoggerFactory.getInstance().getAppLogger().debug(assocKey + " isStarted: " + association.isStarted());	
				Thread worker = new Thread((ClientAssociationWarpper)association.getAssociationListener());
				worker.start();
			}
		}

		return isUp;
	}

	public void sendSctpMsg() throws IOException {

		//ISD
		//byte[] data = Util.fromHex("01000154c000013f010000238070000980700009000001074000002f74657374676d6c633b373235393830323539393332313233393535323b3078303130303030303000000001154000000c00000000000001084000003274657374676d6c632e6570632e6d6e633030312e6d63633431362e336770706e6574776f726b2e6f7267000000000128400000296570632e6d6e633030312e6d63633431362e336770706e6574776f726b2e6f7267000000000001254000003461617a61696e2e6d6d652e6570632e6d6e633030312e6d63633431362e336770706e6574776f726b2e6f72670000011b400000296570632e6d6e633030312e6d63633431362e336770706e6574776f726b2e6f726700000000000001400000173431363031313031323337383136380000000578c000002c000028af000005c1c0000020000028af000005c2c0000012000028af6972092040400000");
		//RIR
		//byte[] data = Util.fromHex("010000D4C080000E0100004B01000128020000AA00000107400000173B313438363931363935303B39323400000001154000000C00000001000001084000001B676D7063312E756D6E6961682E6A6F2E636F6D000000012840000015756D6E6961682E6A6F2E636F6D00000000000125400000216871312E6873732E6570632E756D6E6961682E6A6F2E636F6D0000000000011B400000196570632E756D6E6961682E6A6F2E636F6D000000000002BDC0000012000028AF6972884717100000000005C2C0000012000028AF6972889920200000");		
		//RIA
		byte[] data = Util.fromHex("010001644080000E0100004B01000128020000AA00000107400000173B313438363931363935303B39323400000001154000000C0000000100000108400000216871312E6873732E6570632E756D6E6961682E6A6F2E636F6D00000000000128400000196570632E756D6E6961682E6A6F2E636F6D0000000000010C4000000C000007D100000001400000173431363033323131303736383930370000000961C0000088000028AF00000962C0000035000028AF6871312E6D6D652E6570632E6D6E633030332E6D63633431362E336770706E6574776F726B2E6F7267000000000009688000002D000028AF6570632E6D6E633030332E6D63633431362E336770706E6574776F726B2E6F726700000000000965C0000012000028AF00010A6EB812000000000966C0000020000028AF00000963C0000012000028AF697288990063000000000966C0000020000028AF000005D1C0000012000028AF6972889900250000");		
		PayloadData payloadData = new PayloadData(data.length, Unpooled.copiedBuffer(data), true, false, 46, 1);

		try {
			this.sctpManagement.getAssociation(associationName).send(payloadData);
			payloadData = null;
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
	}
	
	
	public Association getAssociation (String associationName) throws Exception {
		return this.sctpManagement.getAssociation(associationName);
	}
	
	public void sendSctpMsg(int protocolIdentifier, String associationName, byte[] data) {
		try {
			PayloadData payloadData = new PayloadData(data.length, data, true, false, protocolIdentifier, 0);
			getAssociation(associationName).send(payloadData);
			payloadData = null;
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
	}

	public boolean isTestMode() {
		return testMode;
	}	
	
}
