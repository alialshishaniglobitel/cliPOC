package com.globitel.sctp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationType;
import org.mobicents.protocols.api.PayloadData;

import com.globitel.Application.Application;
import com.globitel.Diameter.ConnectionState;
import com.globitel.Diameter.Interfaces.CapabilityExchangeInfo;
import com.globitel.XmlDiameter.xml.XmlMessage;
import com.globitel.XmlDiameter.xml.XmlMessageReader;
import com.globitel.diameterCodec.Diameter.AVP;
import com.globitel.diameterCodec.Diameter.COMMAND_CODES;
import com.globitel.diameterCodec.Diameter.DiameterMessage;
import com.globitel.sctp.common.Utility;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

import io.netty.buffer.Unpooled;

public class ClientAssociationWarpper extends ClientAssociationListener implements Runnable, CapabilityExchangeInfo {
	public boolean diameterUp;
	private boolean running = true;
	private boolean responseReceived;
	private int state = ConnectionState.ConnectionIdle;
	private int identifier;
	private boolean watchDogSent;
	private long lastAccessTime;
	private Association association;
	private SCTPModule sctpModule;

	public ClientAssociationWarpper(Association association, SCTPModule sctpModule) {
		super();
		this.association = association;
		this.sctpModule = sctpModule;
	}
	
	

	@Override
	public void run() {
		MyLoggerFactory.getInstance().getAppLogger()
				.debug("ASSOCIATION WRAPPER THREAD STARTED: " + association.getName());
		MyLoggerFactory.getInstance().getAppLogger().info(association.getName() + ", preparing CER");
		if (Arrays.asList(Application.hssAssociations).contains(association.getName())) {
			sendCER(Application.xmlCERForHSS);			
		} else if (Arrays.asList(Application.mmeAssociations).contains(association.getName())) {
			sendCER(Application.xmlCERForMME);						
		}
		MyLoggerFactory.getInstance().getAppLogger().info(association.getName() + ", CER sent");
		if (sctpModule.isTestMode()) {
			// TODO to be removed
			receiveCEA();			
		}
		int counter = 0;
		while (responseReceived == false && counter++ < 100) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
			}
		}
		if (responseReceived == false) {
			MyLoggerFactory.getInstance().getAppLogger()
					.info(association.getName() + ", CEA not received removing association");
			state = ConnectionState.NoCEAReceived;
			return;
		}
		while (running) {
			try {
				if (shouldSendWatchDog()) {
					MyLoggerFactory.getInstance().getAppLogger()
							.debug(association.getName() + ", preparing watchdog request");
					DiameterMessage msg = new DiameterMessage();
					XmlMessageReader xmlMsgReader = Application.xmlFactory.create("WATCH_DOG_REQ");
					XmlMessage xmlMsg = xmlMsgReader.parse();
					xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
					xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier++);

					Application.responder.setInfo(xmlMsg, this);

					byte[] bb = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, msg);
					sctpModule.sendSctpMsg(46, association.getName(), bb);
					updateTimeStamp();
					watchDogSent = true;
					MyLoggerFactory.getInstance().getAppLogger().debug(association.getName() + ", sent watchdog");
					
					if (sctpModule.isTestMode()) {
						// TODO to be removed
						receiveWDA();
						receiveWDR();
					}					
				}
			} catch (IOException e) {
				MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
			}

			try {
				Thread.sleep(2000);
				if (watchDogSent && responseReceived == false) {
					running = false;
					MyLoggerFactory.getInstance().getAppLogger()
							.info(association.getName() + ", WatchDog not received in 2 seconds");
					state = ConnectionState.NoWatchDogReceived;
				} else {
					responseReceived = false;
				}
			} catch (Exception e) {
				MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
			}
		}

	}

	public void updateTimeStamp() {
		lastAccessTime = System.currentTimeMillis();
	}

	public boolean shouldSendWatchDog() {
		long now = System.currentTimeMillis() - lastAccessTime;
		boolean sendWD = false;
		int watchDogMax = Application.configs.getWatchDogTime() * 1000;
		if (now > watchDogMax) {
			sendWD = true;
			MyLoggerFactory.getInstance().getAppLogger()
					.debug(association.getName() + ", WATCHDOG SENT, TIMEOUT:" + now + ", > " + watchDogMax);
		}
		return sendWD;
	}

	public void end() {
		running = false;
	}

	public int getConnectionState() {
		return state;
	}

	public boolean isStalled() {
		long diff = System.currentTimeMillis() - lastAccessTime;
		boolean stalled = (diameterUp == false) && (diff > Application.configs.getAssociationTimeout());
		if (stalled) {
			MyLoggerFactory.getInstance().getAppLogger()
					.info("Association: " + association.getName() + ", Stalled, TimeDiff:" + diff + " milliseconds.");
		}
		return stalled;
	}

	public boolean isDiameterUp() {
		return diameterUp;
	}

	public void setDiameterUp(boolean diameterUp) {
		MyLoggerFactory.getInstance().getAppLogger().debug("Diameter link up:" + diameterUp);
		this.diameterUp = diameterUp;
	}

	public void handleLowLevelTraffic(int payloadProtocolId, Association assoc, byte[] msgData) {
		try {
			DiameterMessage msg = new DiameterMessage(msgData);
			msg.decode(true);

			updateTimeStamp();

			byte[] bytes = null;
			if (msg.getCommandCode() == COMMAND_CODES.WATCH_DOG) {
				if (msg.isRequest()) {
					bytes = Application.responder.sendWatchDogResponse(msg);
					MyLoggerFactory.getInstance().getAppLogger()
							.debug(association.getName() + ", WATCH_DOG response sent");
				} else {
					if (watchDogSent) {
						watchDogSent = false;

						AVP avp = msg.getAVP(268);
						if (avp != null) {
							if (avp.getValue().equals("2001")) {
								responseReceived = true;
								MyLoggerFactory.getInstance().getAppLogger()
										.debug(association.getName() + ", WATCH_DOG response(2001) received");
							} else {
								MyLoggerFactory.getInstance().getAppLogger().debug(association.getName()
										+ ", WATCH_DOG response(" + avp.getValue() + ") received");
							}
						} else {
							MyLoggerFactory.getInstance().getAppLogger()
									.debug(association.getName() + ", WATCH_DOG received NOT SUCCESS");
						}
					} else {
						MyLoggerFactory.getInstance().getAppLogger()
								.debug(association.getName() + ", WATCH_DOG received Request was not sent originally");
					}
					return;
				}
			} else if (msg.getCommandCode() == COMMAND_CODES.CAPABILITY_EXCHANGE) {
				if (msg.isRequest()) {
					MyLoggerFactory.getInstance().getAppLogger()
							.debug(association.getName() + ", CAPABILITY_EXCHANGE Req received");
					setDiameterUp(true);
					MyLoggerFactory.getInstance().getAppLogger()
							.debug(association.getName() + ", CAPABILITY_EXCHANGE generating response");
					bytes = Application.responder.sendCapabilityAnswer(msg, this);
					MyLoggerFactory.getInstance().getAppLogger()
							.debug(association.getName() + ", CAPABILITY_EXCHANGE response sent");
				} else {
					{
						AVP avp = msg.getAVP(268);
						if (avp != null) {
							if (avp.getValue().equals("2001")) {
								setDiameterUp(true);
								responseReceived = true;
								MyLoggerFactory.getInstance().getAppLogger()
										.debug(association.getName() + ", CAPABILITY_EXCHANGE response(2001) received");
							} else {
								MyLoggerFactory.getInstance().getAppLogger().debug(association.getName()
										+ ", CAPABILITY_EXCHANGE response(" + avp.getValue() + ") received");
							}
						} else {
							MyLoggerFactory.getInstance().getAppLogger()
									.debug(association.getName() + ", CAPABILITY_EXCHANGE received NOT SUCCESS");
						}

					}
					return;
				}
			}
			if (bytes != null) {
				assoc.send(new PayloadData(bytes.length, bytes, true, false, 46, 0));
				MyLoggerFactory.getInstance().getAppLogger()
						.debug(association.getName() + ", response sent to association");
				return;
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
	}

	
	public boolean sendCER(String xmlCER) {		
		try {
			if (association.getAssociationType() == AssociationType.CLIENT) {
				updateTimeStamp();
				DiameterMessage diaMsg = new DiameterMessage();
				
				XmlMessageReader xmlMsgReader = Application.xmlFactory.create(xmlCER);
				XmlMessage xmlMsg;

				xmlMsg = xmlMsgReader.parse();
				xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
				xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier);
				setApplicationIPS(xmlMsg);

				Application.responder.UpdateCapabilityExchange(xmlMsg, this);
				byte[] byteArr = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, diaMsg);
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArr);
				sctpModule.sendSctpMsg(46, association.getName(), byteBuffer.array());
				updateTimeStamp();
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return responseReceived;
	}

	@Override
	public void setApplicationIPS(XmlMessage xmlMsg2) {
//		xmlMsg2.setValue("hostIpAddress1", hostAddress);
//		xmlMsg2.setValue("hostIpAddress2", extraHostAddresses.get(0));
	}

	@Override
	public void onPayload(Association arg0, PayloadData arg1) {
		super.onPayload(arg0, arg1);
		byte[] itr = arg1.getData();
		if (Utility.isWatchDogOrCapabilityExchange(itr)) {
			handleLowLevelTraffic(arg1.getPayloadProtocolId(), association, itr);
		} else {
			Application.msgBuffer.handleNewMessage(arg1.getPayloadProtocolId(), association, itr);
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////	
	public void receiveCEA() {
		handleLowLevelTraffic(46, association, getCEAMsg().array());
	}	
	public ByteBuffer getCEAMsg() {
		updateTimeStamp();
		ByteBuffer b = null;
		DiameterMessage msg2 = new DiameterMessage();
		try {
			XmlMessageReader xmlMsgReader2 = Application.xmlFactory.create("CAPABILITY_EXCHANGE_RES");
			XmlMessage xmlMsg2;

			xmlMsg2 = xmlMsgReader2.parse();
			xmlMsg2.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
			xmlMsg2.setValue("END_TO_END_IDENTIFIER", "" + identifier);
			setApplicationIPS(xmlMsg2);

			Application.responder.UpdateCapabilityExchange(xmlMsg2, this);
			byte[] bb2 = Application.xmlFactory.encodeMessage(xmlMsg2, xmlMsgReader2, msg2);
			b = ByteBuffer.wrap(bb2);
			updateTimeStamp();
		} catch (IOException e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return b;
	}	
	public void receiveWDA() {
		handleLowLevelTraffic(46, association, getWDAMsg().array());
	}	
	public ByteBuffer getWDAMsg() {
		updateTimeStamp();
		ByteBuffer b = null;
		DiameterMessage msg2 = new DiameterMessage();
		try {
			XmlMessageReader xmlMsgReader2 = Application.xmlFactory.create("WATCH_DOG_RES");
			XmlMessage xmlMsg2;

			xmlMsg2 = xmlMsgReader2.parse();
			xmlMsg2.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
			xmlMsg2.setValue("END_TO_END_IDENTIFIER", "" + identifier);
			setApplicationIPS(xmlMsg2);

			Application.responder.UpdateCapabilityExchange(xmlMsg2, this);
			byte[] bb2 = Application.xmlFactory.encodeMessage(xmlMsg2, xmlMsgReader2, msg2);
			b = ByteBuffer.wrap(bb2);
			updateTimeStamp();
		} catch (IOException e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return b;
	}	
	public void receiveWDR() {
		handleLowLevelTraffic(46, association, getWDRMsg().array());
	}	
	public ByteBuffer getWDRMsg() {
		updateTimeStamp();
		ByteBuffer b = null;
		DiameterMessage msg2 = new DiameterMessage();
		try {
			XmlMessageReader xmlMsgReader2 = Application.xmlFactory.create("WATCH_DOG_REQ");
			XmlMessage xmlMsg2;

			xmlMsg2 = xmlMsgReader2.parse();
			xmlMsg2.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
			xmlMsg2.setValue("END_TO_END_IDENTIFIER", "" + identifier);
			setApplicationIPS(xmlMsg2);

			Application.responder.UpdateCapabilityExchange(xmlMsg2, this);
			byte[] bb2 = Application.xmlFactory.encodeMessage(xmlMsg2, xmlMsgReader2, msg2);
			b = ByteBuffer.wrap(bb2);
			updateTimeStamp();
		} catch (IOException e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return b;
	}
	public void receiveRIA() {
		Application.msgBuffer.handleNewMessage(46, association, Util.fromHex("010001644080000E0100004B01000128020000AA00000107400000173B313438363931363935303B39323400000001154000000C0000000100000108400000216871312E6873732E6570632E756D6E6961682E6A6F2E636F6D00000000000128400000196570632E756D6E6961682E6A6F2E636F6D0000000000010C4000000C000007D100000001400000173431363033323131303736383930370000000961C0000088000028AF00000962C0000035000028AF6871312E6D6D652E6570632E6D6E633030332E6D63633431362E336770706E6574776F726B2E6F7267000000000009688000002D000028AF6570632E6D6E633030332E6D63633431362E336770706E6574776F726B2E6F726700000000000965C0000012000028AF00010A6EB812000000000966C0000020000028AF00000963C0000012000028AF697288990063000000000966C0000020000028AF000005D1C0000012000028AF6972889900250000"));
	}	
}
