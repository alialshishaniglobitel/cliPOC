
package com.globitel.Logic;

import com.globitel.Application.Application;
import com.globitel.Application.CDR;
import com.globitel.Application.Session;
import com.globitel.Application.SessionManagerImp;
import com.globitel.SS7.codec.*;
import com.globitel.SS7.handler.Handler;
import com.globitel.SS7.MessageSender;
import com.globitel.common.utils.*;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;
import com.globitel.utilities.commons.AppEnumerations.TRANSACTION_STATUS;

import org.mobicents.protocols.asn.AsnInputStream;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSResponseImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultLast;

public class SS7Manager extends Handler {

	private static long tid = 1;
	private SessionManagerImp sessionManager = null;
	private int processorID = 0;
	private MessageSender msgSender = new MessageSender();

	static {
		LocalOperationClassMAP.Initialize();
	}

	public SS7Manager(int processorID, SessionManagerImp sessionManager) {
		this.processorID = processorID;
		this.sessionManager = sessionManager;
	}

	@Override
	public void handleNewMessage(Message message) {
		Session session = new Session(processorID);
		String otidStr = null, dtidStr = null;
		byte[] otid = message.getOtid(), dtid = message.getDtid();
		session.tidStr = otidStr = Common.byteArrayToString(otid, otid == null ? 4 : otid.length);
		dtidStr = Common.byteArrayToString(dtid, dtid == null ? 4 : dtid.length);

		MyLoggerFactory.getInstance().getAppLogger().info(String.format("New message received, OTID : [%s] : DTID : [%s].", otidStr, dtidStr));

		switch (message.tcap.tcapType) {
		case TCAP.Begin:
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("TCAP Type for the OTID : [%s] is Begin!", otidStr));
			handleBegin(message);
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("OTID : [%s] has been handled (Begin).", otidStr));
			break;

		case TCAP.Continue:

			MyLoggerFactory.getInstance().getAppLogger().info(String.format("TCAP Type for the OTID : [%s], DTID : [%s], is Continue!", otidStr, dtidStr));
			handleContinue(message);
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("OTID : [%s], DTID : [%s], has been handled (Continue).", otidStr, dtidStr));
			break;

		case TCAP.End:

			MyLoggerFactory.getInstance().getAppLogger().info(String.format("TCAP Type for the DTID : [%s], is End!", dtidStr));
			handleEnd(message);
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("DTID : [%s], has been handled (End).", dtidStr));
			break;

		case TCAP.Abort:

			MyLoggerFactory.getInstance().getAppLogger().info(String.format("TCAP Type for the DTID : [%s], is Abort!", dtidStr));
			handleAbort(message);
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("DTID : [%s], has been handled (Abort).", dtidStr));

			break;
		}
	}

	private void handleAbort(Message message) {
	}

	private void handleBegin(Message message) {
	}

	private void handleContinue(Message message) {
	}

	private void handleEnd(Message message) {
		Object component = null;
		OperationCode operationCode;
		msgSender.sendIgnoreMessage(message);
		byte[] buf;

		for (int index = 0; index < message.tcap.definedComponents.size(); index++) {
			component = message.tcap.definedComponents.get(index);

			if (component instanceof ReturnResultLast) {
				ReturnResultLast returnResultLast = (ReturnResultLast) component;
				message.setInvokeID((int) (long) returnResultLast.getInvokeId());
				operationCode = returnResultLast.getOperationCode();
				String dtid = Common.byteArrayToString(message.getDtid());
				buf = returnResultLast.getParameter().getData();

				switch ((int) (long) operationCode.getLocalOperationCode()) {
				case MAPOperationCode.sendRoutingInfoForLCS:

					Session session = sessionManager.getSession(dtid);
					if (session != null) {
						MyLoggerFactory.getInstance().getAppLogger().info(
								String.format("Session [%s]: A RRL SendRoutingInfoForLCS message has been captured.",
										session.tidStr));
						SendRoutingInfoForLCSResponseImpl sendRoutingInfoForLCSResp = new SendRoutingInfoForLCSResponseImpl();
						AsnInputStream rir = new AsnInputStream(buf);
						try {
							message.setOperationCode(MAPOperationCode.sendRoutingInfoForLCS);
							sendRoutingInfoForLCSResp.decodeData(rir, buf.length);
							message.setSendRoutingInfoForLCSAttributes(sendRoutingInfoForLCSResp, session.tidStr);
							handleSriForLCS(message, session);
						} catch (MAPParsingComponentException e) {
							// TODO Auto-generated catch block
							// error while decoding Update Location request
						}
					} else {
						MyLoggerFactory.getInstance().getAppLogger().warn(String.format(
								"Cannot find the corrosponding session [%s] for captured RRL SendRoutingInfoForLCS message",
								dtid));
					}
					break;

				case MAPOperationCode.provideSubscriberLocation:

					session = sessionManager.getSession(dtid);
					if (session != null) {
						MyLoggerFactory.getInstance().getAppLogger().info(String.format(
								"Session [%s]: A RRL ProvideSubscriberLocation message has been captured.",
								session.tidStr));
						ProvideSubscriberLocationResponseImpl provideSubscriberLocationResp = new ProvideSubscriberLocationResponseImpl();
						AsnInputStream psl = new AsnInputStream(buf);
						try {
							message.setOperationCode(MAPOperationCode.provideSubscriberLocation);
							provideSubscriberLocationResp.decodeData(psl, buf.length);
							message.setProvideSubscriberLocationAttributes(provideSubscriberLocationResp,
									session.tidStr);
							handlePSL(message, session);
						} catch (MAPParsingComponentException e) {
							// TODO Auto-generated catch block
							// error while decoding Update Location request
						}
					} else {
						MyLoggerFactory.getInstance().getAppLogger().warn(String.format(
								"Cannot find the corrosponding session [%s] for captured RRL ProvideSubscriberLocation message",
								dtid));
					}
					sessionManager.removeSession(dtid);
					break;
				default:
					MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Unknown End Message Received.", dtid));
					msgSender.sendIgnoreMessage(message);

					break;
				}
			}
		}

	}

	private void handleSriForLCS(Message sriForLCSMessage, Session session) {
		Message message = new Message(0);
		byte[] otid = sriForLCSMessage.getDtid();
		String keySession = Common.byteArrayToString(otid);

		session.setMessage(message).setMsisdn(session.getMsisdn());
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Recieve Routing Info For LCS", session.tidStr));
		sessionManager.addSession(keySession, session);

		if (sriForLCSMessage.getNetworkNodeNumber() != null
				&& !sriForLCSMessage.getNetworkNodeNumber().getAddress().equals("")) {
			CDR cdr = session.getCDR();
			cdr.msisdn = session.getMsisdn();
			cdr.imsi = sriForLCSMessage.getIMSI().getData();
			cdr.networkNodeNumber = sriForLCSMessage.getNetworkNodeNumber().getAddress();

			updateStatus(cdr, keySession, session, TRANSACTION_STATUS.RCV_SRIForLCS_ACK);

			int counter1 = 0;
			int counter2 = 0;
			boolean sendPSL = false;
			while(true) {
				if ((cdr.returnValue & TRANSACTION_STATUS.SEND_PLR.get()) == TRANSACTION_STATUS.SEND_PLR.get()) {
					if ((cdr.returnValue & TRANSACTION_STATUS.RCV_PLA.get()) == TRANSACTION_STATUS.RCV_PLA.get()) {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {}
						if ((cdr.returnValue & TRANSACTION_STATUS.RCV_PLA_WITH_ECGI.get()) == TRANSACTION_STATUS.RCV_PLA_WITH_ECGI.get()) {
							sendPSL = false;
							sessionManager.removeSession(keySession);
						} else {
							sendPSL = true;
						}
						break;
					} else {
						if(counter1 < 2000) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						} else {
							sendPSL = true;
							break;
						}
						counter1++;
					}					
				} else {
					if(counter2 < 20) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
					} else {
						sendPSL = true;
						break;
					}
					counter2++;
				}
			}
			if (sendPSL) {
				MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Send Provide Subscriber Location due to 4G failure", session.tidStr));
				message.sccp.setCalledAddress(constructGT_SCCP(sriForLCSMessage.getNetworkNodeNumber().getAddress(),
						RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, 0x00, NumberingPlan.ISDN_TELEPHONY,
						NatureOfAddress.INTERNATIONAL,
						GetMSC_PC(sriForLCSMessage.getNetworkNodeNumber().getAddress(), session), 8));
				message.sccp.setCallingAddress(Application.System_GT_SCCP);
				msgSender.sendProvideSubscriberLocationReq(message, sriForLCSMessage.getIMSI().getData(), session.getMsisdn(), otid);
				
				updateStatus(cdr, keySession, session, TRANSACTION_STATUS.SEND_PSL);
			}			
			MyLoggerFactory.getInstance().getAppLogger().info(String.format(
					"Session[%s]: RRL SendRoutingInfoForLCS is captured, IMSI[%s], Network Node Number[%s].",
					session.tidStr, sriForLCSMessage.getIMSI().getData(),
					sriForLCSMessage.getNetworkNodeNumber().getAddress()));
		}
	}

	private void handlePSL(Message pslMessage, Session session) {
		byte[] otid = pslMessage.getDtid();
		String keySession = Common.byteArrayToString(otid);

		if (pslMessage != null && pslMessage.getCellsac() > 0) {
			CDR cdr = session.getCDR();
			cdr.msisdn = session.getMsisdn();
			cdr.typeOfShape = pslMessage.getTypeOfShape().toString();
			cdr.latitude = pslMessage.getLatitude() + "";
			cdr.longitude = pslMessage.getLongitude() + "";
			cdr.cellsac = pslMessage.getCellsac() + "";
			
			updateStatus(cdr, keySession, session, TRANSACTION_STATUS.RCV_PSL_ACK);	
			
			MyLoggerFactory.getInstance().getAppLogger().info(String.format(
					"Session[%s]: RRL ProvideSubscriberLocation is captured, Type of Shape[%s], LAT[%s], LONG[%s], Cell/SAC[%d].",
					session.tidStr, pslMessage.getTypeOfShape().toString(), pslMessage.getLatitude(),
					pslMessage.getLongitude(), pslMessage.getCellsac()));
		}
	}

	public SccpAddressImpl constructGT_SCCP(String _gtSCCP, RoutingIndicator _ri, int _translationType,
			NumberingPlan _numberingPlan, NatureOfAddress _natureOfAddress, int _dpc, int _ssn) {

		boolean odd = _gtSCCP.length() % 2 == 0 ? false : true;
		EncodingScheme encodingScheme = odd == true ? BCDOddEncodingScheme.INSTANCE : BCDEvenEncodingScheme.INSTANCE;
		GlobalTitle gt = new GlobalTitle0100Impl(_gtSCCP, _translationType, encodingScheme, _numberingPlan,
				_natureOfAddress);

		return new SccpAddressImpl(_ri, gt, _dpc, _ssn);

	}

	public String handleLocationRequest(String _msisdn) {
		Message message = new Message(0);
		Session session = new Session(processorID);
		byte[] otid = Common.longToByteArray(tid++);
		String keySession = Common.byteArrayToString(otid);
		int _HLR_PC = GetHLR_PC(new IMSIImpl(_msisdn), session);
		if (_HLR_PC == 0) {
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session [%s]: Error HLR SCCP GT can not be constructed with the available configuration.",
					session.tidStr));
		}
		SccpAddressImpl _SCCP_HLR_GT = null;
		String _HLR_GT = ConfigurationManager.getInstance().getValue("HLRPC_" + _HLR_PC);
		boolean odd = _HLR_GT.length() % 2 == 0 ? false : true;
		int translationType = 0x00;
		EncodingScheme encodingScheme = odd == true ? BCDOddEncodingScheme.INSTANCE : BCDEvenEncodingScheme.INSTANCE;
		NumberingPlan numberingPlan = NumberingPlan.ISDN_TELEPHONY;
		NatureOfAddress natureOfAddress = NatureOfAddress.INTERNATIONAL;
		GlobalTitle gt = new GlobalTitle0100Impl(_HLR_GT, translationType, encodingScheme, numberingPlan,
				natureOfAddress);
		int dpc = _HLR_PC;
		int ssn = 6;
		CDR cdr = new CDR(otid);
		
		
		/*********************************************************
		 * route on global title
		 */
		//RoutingIndicator ri = RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE;
		//_SCCP_HLR_GT = new SccpAddressImpl(ri, gt, dpc, ssn);
		//message.sccp.setCalledAddress(_SCCP_HLR_GT);
		
		/*********************************************************/
		 
		/*********************************************************
		 * route on point code
		 */
		RoutingIndicator ri = RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN;
		_SCCP_HLR_GT = new SccpAddressImpl(ri, null, dpc, ssn);
		message.sccp.setCalledAddress(_SCCP_HLR_GT);
		/********************************************************/				
		
		message.sccp.setCallingAddress(Application.System_GT_SCCP);

		session.setMessage(message).setMsisdn(_msisdn);
		cdr.msisdn = _msisdn;
		session.setCDR(cdr);
		sessionManager.addSession(keySession, session);
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Send Routing Info For LCS", session.tidStr));

		msgSender.sendRoutingInfoForLCSReq(message, session.getMsisdn(), otid);
		updateStatus(cdr, keySession, session, TRANSACTION_STATUS.SEND_SRIForLCS);

		Application.logic[processorID].sendRIRMsg(Application.hssAssociations[0], keySession, Common.flipByte(session.getMsisdn()), Common.flipByte(Application.systemGT));
		updateStatus(cdr, keySession, session, TRANSACTION_STATUS.SEND_RIR);
		
		return keySession;
	}

	private int GetHLR_PC(IMSI imsi, Session session) {
		String temp = null;
		for (int i = 3; i <= 7; i++) {
			temp = ConfigurationManager.getInstance().getValue("SUB_"+imsi.getData().substring(0, i));
			if (temp != null) {
				return Integer.parseInt(temp);
			}
		}

		MyLoggerFactory.getInstance().getAppLogger().error(String.format(
				"Session [%s]: Cannot find matching PC for the IMSI: %s, tried prefixes with digits ranging from 3 to 7!",
				session.tidStr, imsi.getData()));
		return 0;
	}

	private int GetMSC_PC(String mscGT, Session session) {
		String temp = ConfigurationManager.getInstance().getValue("MSCGT_" + mscGT);
		if (temp != null) {
			return Integer.parseInt(temp);
		}

		MyLoggerFactory.getInstance().getAppLogger().error(String.format(
				"Session [%s]: Cannot find matching PC for the MSC GT: %s, it should be exist in the configuration file",
				session.tidStr, mscGT));
		return 0;
	}
	
	private void updateStatus(CDR cdr, String keySession, Session session, TRANSACTION_STATUS status) {
		cdr.returnValue |= status.get();
		session.setCDR(cdr);
		sessionManager.addSession(keySession, session);		
	}	
}
