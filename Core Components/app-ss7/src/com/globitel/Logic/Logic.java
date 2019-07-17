package com.globitel.Logic;

import java.nio.ByteBuffer;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationType;

import com.globitel.Application.Application;
import com.globitel.Application.CDR;
import com.globitel.Application.Session;
import com.globitel.Application.SessionManagerImp;
import com.globitel.Diameter.Codes.AVPCodes;
import com.globitel.Diameter.Codes.AttributeCodes;
import com.globitel.Diameter.Codes.ResultCodes;
import com.globitel.Diameter.MessageBuffers;
import com.globitel.Diameter.MessageBuffers.MessageBean;
import com.globitel.Diameter.Interfaces.IMessageResponder;
import com.globitel.XmlDiameter.xml.XmlMessage;
import com.globitel.XmlDiameter.xml.XmlMessageReader;
import com.globitel.common.utils.Common;
import com.globitel.diameterCodec.Diameter.AVP;
import com.globitel.diameterCodec.Diameter.COMMAND_CODES;
import com.globitel.diameterCodec.Diameter.DiameterMessage;
import com.globitel.sctp.ClientAssociationWarpper;
import com.globitel.sctp.SCTPModule;
import com.globitel.sctp.Util;
import com.globitel.utilities.commons.AppEnumerations.TRANSACTION_STATUS;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Logic extends Thread {
	private SCTPModule sctpModule;
	private MessageBuffers msgBuffer;
	private int processorID;
	private boolean running = true;
	private SessionManagerImp sessionManager;
	//private Session session;
	private int identifier;

	public Logic(int processorID, IMessageResponder _responder, SCTPModule _SCTP, MessageBuffers msgBugger,
			SessionManagerImp sessionManager) throws Exception {
		this.processorID = processorID;
		this.msgBuffer = msgBugger;
		this.sctpModule = _SCTP;
		this.sessionManager = sessionManager;
	}

	public void handleNewMessage(int payloadProtocolId, Association assoc, DiameterMessage msg) throws Exception {
		ClientAssociationWarpper iAssociationWrapper = (ClientAssociationWarpper) sctpModule
				.getAssociation(assoc.getName()).getAssociationListener();
		if (iAssociationWrapper != null) {
			iAssociationWrapper.updateTimeStamp();
		}
		boolean decode = msg.decode(true);
		if (decode) {
			MyLoggerFactory.getInstance().getAppLogger()
					.info(getHeader() + "received, CommandCode:" + msg.getCommandCode() + ", isReq:" + msg.isRequest());
		} else {
			MyLoggerFactory.getInstance().getAppLogger().warn(getHeader() + "received, CommandCode:"
					+ msg.getCommandCode() + ", isReq:" + msg.isRequest() + ", Decoding Failure");
		}
		msg.setSrcIP(assoc.getName());

		// String sessionId = new String(msg.getSessionID() == null ? "" :
		// msg.getSessionID());
		// String oHost = new String(msg.getOriginHost() == null ? "" :
		// msg.getOriginHost());
		// String oRealm = new String(msg.getOriginRealm() == null ? "" :
		// msg.getOriginRealm());
		// String dHost = new String(msg.getDestHost() == null ? "" :
		// msg.getDestHost());
		// String dRealm = new String(msg.getDestRealm() == null ? "" :
		// msg.getDestRealm());
		//
		// int returnV = -1;
		// int customerGroup = 100;
		// int gprs = 1;
		// int serverId = 1;
		
		String tid = String.format("%08x", msg.getEndToEndIdentifier());
		//session.tidStr = tid;

		MyLoggerFactory.getInstance().getAppLogger()
				.info(String.format("New message received, End to End Identifier : [%s].", tid));

		switch (msg.getCommandCode()) {
		case COMMAND_CODES.ROUTING_INFO_ANSWER:
			MyLoggerFactory.getInstance().getAppLogger()
					.info(msg.getSessionID() + ", " + "receiving RIA from HSS");
			handleRoutingInfoAnswer(msg);
			break;			
		case COMMAND_CODES.PROVIDE_LOCATION_ANSWER:
			MyLoggerFactory.getInstance().getAppLogger()
					.info(msg.getSessionID() + ", " + "receiving PLA from MME");
			handleProvideLocationAnswer(msg);
			break;
		default:
			break;
		}
	}
	
	private void handleRoutingInfoAnswer(DiameterMessage msg) {
		MyLoggerFactory.getInstance().getAppLogger().debug("RIA message is recieved ...");
		CDR cdr = updateStatus(null, msg.getSessionID(), TRANSACTION_STATUS.RCV_RIA);

		AVP resultCode = msg.getAVP(AVPCodes.resultCode);
		if (resultCode != null) {
			if (resultCode.getValue().equals(ResultCodes.DIAMETER_SUCCESS + "")) {	
				cdr = updateStatus(cdr, msg.getSessionID(), TRANSACTION_STATUS.RCV_RIA_SUCCESS);
				
				AVP servingNode = msg.getAVP(AVPCodes.serving_Node);
				for (AVP avp : servingNode.getChildren().list) {
					if (avp.getAvpCode() == AttributeCodes.MME_Name) {
						MyLoggerFactory.getInstance().getAppLogger().debug("MME name: " + avp.getValue());
						cdr.servingNode = avp.getValue();
						if (cdr.servingNode != null && !cdr.servingNode.equals("")) {
							cdr = updateStatus(cdr, msg.getSessionID(), TRANSACTION_STATUS.RCV_RIA_WITH_MME);
						}
					}
				}
				
				AVP imsi = msg.getAVP(AVPCodes.userNameIMSI);
				cdr.imsi = imsi.getValue();				
				
				AVP additionalServingNode = msg.getAVP(AVPCodes.additionalServingNode);
				for (AVP avp : additionalServingNode.getChildren().list) {
					if (avp.getAvpCode() == AttributeCodes.MSC_Number) {
						MyLoggerFactory.getInstance().getAppLogger().debug("MSC number: " + Common.flipByte(avp.getValue()));
						cdr.additionalServingNode = Common.flipByte(avp.getValue());
					}
					if (avp.getAvpCode() == AttributeCodes.SGSN_Number) {
						MyLoggerFactory.getInstance().getAppLogger().debug("SGSN number: " + Common.flipByte(avp.getValue()));
						cdr.additionalServingNode = Common.flipByte(avp.getValue());
					}					
				}
				
				if ((cdr.returnValue & TRANSACTION_STATUS.RCV_RIA_WITH_MME.get()) == TRANSACTION_STATUS.RCV_RIA_WITH_MME.get()) {
					sendPLRMsg(cdr.servingNode, msg.getSessionID(), cdr.servingNode, cdr.imsi, cdr.msisdn, cdr.imsi);
					cdr = updateStatus(cdr, msg.getSessionID(), TRANSACTION_STATUS.SEND_PLR);	
				}
			}
		}
	}
	
	private CDR updateStatus(CDR cdr, String keySession, TRANSACTION_STATUS status) {
		Session session = sessionManager.getSession(keySession);
		if (session != null) {
			cdr = session.getCDR();
			if (cdr != null) {
				cdr.returnValue |= status.get();
				session.setCDR(cdr);
				sessionManager.addSession(keySession, session);					
			}
		}
		return cdr;
	}

	private String getHeader() {
		return "Logic " + processorID + ",";
	}

	@Override
	public void run() {
		while (running) {
			MessageBean bean = msgBuffer.getMsg();
			if (bean != null) {
				MyLoggerFactory.getInstance().getAppLogger().info(getHeader() + "received msg.");
				try {
					if (true /*SCTP.isDiameterUp(bean.assoc)*/) {
						handleNewMessage(bean.payloadProtocolId, bean.assoc, bean.msg);
						bean.msg = null;
						bean.assoc = null;
						bean = null;
					} else {
						MyLoggerFactory.getInstance().getAppLogger()
								.error(bean.assoc.getName() + ", Diameter link not up, dropping message");
					}
				} catch (Exception e) {
					MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
					bean.msg.print("Related To Exception:");
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void end() {
		running = false;
	}

	private void handleProvideLocationAnswer(DiameterMessage msg) throws NumberFormatException, Exception {
		MyLoggerFactory.getInstance().getAppLogger().debug("PLA message is recieved ...");
		CDR cdr = updateStatus(null, msg.getSessionID(), TRANSACTION_STATUS.RCV_PLA);				

		
		AVP resultCode = msg.getAVP(AVPCodes.resultCode);
		if (resultCode != null) {
			if (resultCode.getValue().equals(ResultCodes.DIAMETER_SUCCESS + "")) {
				MyLoggerFactory.getInstance().getAppLogger().debug("PLA message is success ...");
				cdr = updateStatus(cdr, msg.getSessionID(), TRANSACTION_STATUS.RCV_PLA_SUCCESS);				

				String locationEstimation = msg.getAVP(AVPCodes.location_Estimate).getValue();
				String accuracyFulfilmentIndicator = msg.getAVP(AVPCodes.accuracyFulfilmentIndicator).getValue();
				String eutranPositioningData = msg.getAVP(AVPCodes.eutranPositioningData).getValue();
				String ecgi = msg.getAVP(AVPCodes.ECGI).getValue();
				if (ecgi != null && !ecgi.equals("")) {
					MyLoggerFactory.getInstance().getAppLogger().debug("PLA message with ECGI = " + ecgi);
					if (cdr != null) {
						if (cdr._4GlocInfo != null) {
							cdr._4GlocInfo.decode(locationEstimation, accuracyFulfilmentIndicator, eutranPositioningData, ecgi);							
						}
					}
					cdr = updateStatus(cdr, msg.getSessionID(), TRANSACTION_STATUS.RCV_PLA_WITH_ECGI);					
				}
			}
		}
	}
	 
	
	public void sendRIRMsg(String associationName, String sessionId, String msisdn, String gmlcNumber) {		
		try {
			if (sctpModule.getAssociation(associationName).getAssociationType() == AssociationType.CLIENT) {
				ClientAssociationWarpper associationWrapper = (ClientAssociationWarpper) sctpModule.getAssociation(associationName).getAssociationListener();
				if (associationWrapper != null) {
					associationWrapper.updateTimeStamp();
				}
				DiameterMessage diaMsg = new DiameterMessage();
				
				XmlMessageReader xmlMsgReader = Application.xmlFactory.create("RIR");
				XmlMessage xmlMsg;

				xmlMsg = xmlMsgReader.parse();
				xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
				xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier++);
				xmlMsg.setValue("SessionID", sessionId);				
				xmlMsg.setValue("msisdn", msisdn);
				xmlMsg.setValue("gmlcNumber", gmlcNumber);

				byte[] byteArr = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, diaMsg);
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArr);
				sctpModule.sendSctpMsg(46, associationName, byteBuffer.array());
				associationWrapper.updateTimeStamp();
				if (sctpModule.isTestMode()) {
					// TODO to be removed
					receiveRIA(associationName, sessionId);
				}				
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
	}
	public void sendPLRMsg(String associationName, String sessionId, String destinationHost, String imsi, String msisdn, String imei) {		
		try {
			if (sctpModule.getAssociation(associationName).getAssociationType() == AssociationType.CLIENT) {
				ClientAssociationWarpper associationWrapper = (ClientAssociationWarpper) sctpModule.getAssociation(associationName).getAssociationListener();
				if (associationWrapper != null) {
					associationWrapper.updateTimeStamp();
				}
				DiameterMessage diaMsg = new DiameterMessage();
				
				XmlMessageReader xmlMsgReader = Application.xmlFactory.create("PLR");
				XmlMessage xmlMsg;

				xmlMsg = xmlMsgReader.parse();
				xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
				xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier++);
				xmlMsg.setValue("SessionID", sessionId);				
				xmlMsg.setValue("destinationHost", destinationHost);				
				xmlMsg.setValue("userNAMEIMSI", imsi);
				xmlMsg.setValue("msisdn", msisdn);
				xmlMsg.setValue("imei", imei);

				byte[] byteArr = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, diaMsg);
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArr);
				sctpModule.sendSctpMsg(46, associationName, byteBuffer.array());
				associationWrapper.updateTimeStamp();
				if (sctpModule.isTestMode()) {
					// TODO to be removed
					receivePLA(associationName, sessionId);
				}				
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
	}	
	public void receiveRIA(String associationName, String sessionId) throws Exception {
		Application.msgBuffer.handleNewMessage(46, sctpModule.getAssociation(associationName), getRIAMsg(associationName, sessionId).array());
	}		
	public ByteBuffer getRIAMsg(String associationName, String sessionId) {
		ByteBuffer byteBuffer = null;
		try {
			if (sctpModule.getAssociation(associationName).getAssociationType() == AssociationType.CLIENT) {
				ClientAssociationWarpper associationWrapper = (ClientAssociationWarpper) sctpModule.getAssociation(associationName).getAssociationListener();
				if (associationWrapper != null) {
					associationWrapper.updateTimeStamp();
				}
				DiameterMessage diaMsg = new DiameterMessage();
				
				XmlMessageReader xmlMsgReader = Application.xmlFactory.create("RIA");
				XmlMessage xmlMsg;

				xmlMsg = xmlMsgReader.parse();
				xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
				xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier++);
				xmlMsg.setValue("SessionID", sessionId);				

				byte[] byteArr = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, diaMsg);
				byteBuffer = ByteBuffer.wrap(byteArr);
				associationWrapper.updateTimeStamp();
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return byteBuffer;
	
	}	
	
	public void receivePLA(String associationName, String sessionId) throws Exception {
		// PLA with ERROR
		Application.msgBuffer.handleNewMessage(46, sctpModule.getAssociation(associationName), Util.fromHex("010000d04080000c0100002700000003000000030000010740000010303030303030303100000104400000200000010a4000000c000028af000001024000000c0100002700000108400000316871312e6d6d652e6570632e6d6e633030332e6d63633431362e336770706e6574776f726b2e6f726700000000000128400000296570632e6d6e633030332e6d63633431362e336770706e6574776f726b2e6f7267000000000001154000000c0000000100000129400000200000010a4000000c000028af0000012a4000000c00001081"));
		// PLA with ECGI
		// Application.msgBuffer.handleNewMessage(46, sctpModule.getAssociation(associationName), Util.fromHex("0100010c4080000c010000270000000d0000000d0000010740000010303030303030303100000104400000200000010a4000000c000028af000001024000000c0100002700000108400000316871312e6d6d652e6570632e6d6e633030332e6d63633431362e336770706e6574776f726b2e6f726700000000000128400000296570632e6d6e633030332e6d63633431362e336770706e6574776f726b2e6f7267000000000001154000000c000000010000010c4000000c000007d1000004dac0000019000028afa02d77051987f10000285d045a000000000009d1c0000010000028af00000000000009d4c000000f000028af41100300000009d5c0000013000028af14f6300259920500"));
		// PLA from XML file
		// Application.msgBuffer.handleNewMessage(46, sctpModule.getAssociation(associationName), getPLAMsg(associationName, sessionId).array());
	}		
	public ByteBuffer getPLAMsg(String associationName, String sessionId) {
		ByteBuffer byteBuffer = null;
		try {
			if (sctpModule.getAssociation(associationName).getAssociationType() == AssociationType.CLIENT) {
				ClientAssociationWarpper associationWrapper = (ClientAssociationWarpper) sctpModule.getAssociation(associationName).getAssociationListener();
				if (associationWrapper != null) {
					associationWrapper.updateTimeStamp();
				}
				DiameterMessage diaMsg = new DiameterMessage();
				
				XmlMessageReader xmlMsgReader = Application.xmlFactory.create("PLA");
				XmlMessage xmlMsg;

				xmlMsg = xmlMsgReader.parse();
				xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + identifier);
				xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + identifier++);
				xmlMsg.setValue("SessionID", sessionId);				

				byte[] byteArr = Application.xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, diaMsg);
				byteBuffer = ByteBuffer.wrap(byteArr);
				associationWrapper.updateTimeStamp();
			}
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(e.getMessage(), e);
		}
		return byteBuffer;
	
	}		
}
