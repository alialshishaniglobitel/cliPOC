package com.globitel.SS7;

import com.globitel.Application.Application;
import com.globitel.Application.Session;
import com.globitel.SS7.codec.Message;
import com.globitel.common.structure.Enumerations.RGS_Messages_Types;
import com.globitel.common.utils.ByteArray;
import com.globitel.common.utils.Common;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

import java.io.IOException;
import java.util.Arrays;


import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdOrLAIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ExtGeographicalInformationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSLocationInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSQoSImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LocationTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationRequestImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ResponseTimeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSRequestImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SupportedGADShapesImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.tcap.asn.DialogPortionImpl;
import org.restcomm.protocols.ss7.tcap.asn.ParseException;
import org.restcomm.protocols.ss7.tcap.asn.TCAbortMessageImpl;
import org.restcomm.protocols.ss7.tcap.asn.TCBeginMessageImpl;
import org.restcomm.protocols.ss7.tcap.asn.TCEndMessageImpl;
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.comp.Component;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.Parameter;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultLast;

public class MessageSender {

	private static int gDPC_IndexForUL_FWD = 0;
	private static int gDPC_IndexForRE_VLR = 0;
	private static int gDPC_IndexForAbort_HLR = 0;

	private static int SLS;

	public static class SendErrors {
		public static final int MessageHandlingNotAvailable = -1;
		public static final int TcapTypeNotAvailable = -2;
		public static final int CldNotAvailable = -3;
		public static final int ClgNotAvailable = -4;
		public static final int MessageTypeNotAvailable = -5;
		public static final int Success = 0;
		public static final int TIME_NOT_ELAPSED = -6;
		public static final int Failed_To_Build_Msg = -7;
	}

	private byte[] ignoreMessage = new byte[] { 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private byte[] forwardMsgHeader = new byte[] { 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	public int sendMsg(Session session, int opc, int dpc, int sls, byte messageHandling, SccpAddressImpl cldAddress,
			SccpAddressImpl clgAddress, byte messageType, Object tcMessage, Message message) {
		MyLoggerFactory.getInstance().getAppLogger().debug("Message to be sent");
		message.mtp3.setServiceInfo(message.getServiceInfo());
		if (opc != 0) {
			message.mtp3.setOPC(opc);
		}
		if (dpc != 0) {
			message.mtp3.setDPC(dpc);
		}
		message.mtp3.setSLS(sls);
		if (messageHandling != ((byte) 0xff)) {
			message.sccp.messageHandling = messageHandling;
		} else {
			return SendErrors.MessageHandlingNotAvailable;
		}

		message.tcap.tcMessage = tcMessage;

		if (cldAddress != null) {
			message.sccp.setCalledAddress(cldAddress);
		} else {
			return SendErrors.CldNotAvailable;
		}
		if (clgAddress != null) {
			message.sccp.setCallingAddress(clgAddress);
		} else {
			return SendErrors.ClgNotAvailable;
		}

		if (messageType != 0x00) {
			message.sccp.messageType = messageType;
		} else {
			return SendErrors.MessageTypeNotAvailable;
		}

		MyLoggerFactory.getInstance().getAppLogger().debug(session.tidStr + ", invoke ids Added");
		encodeInstantMessage(message, session.tidStr);
		MyLoggerFactory.getInstance().getAppLogger().debug(session.tidStr + ", message encoded and sent");

		return SendErrors.Success;
	}

	private void encodeInstantMessage(Message messageToEncode, String tidStr) {
		MyLoggerFactory.getInstance().getAppLogger().debug(tidStr + ", encoding instant Message");
		messageToEncode.encode();
		MyLoggerFactory.getInstance().getAppLogger().debug(tidStr + ", message encoded");
		RequestHandler.AddInstantMessage(messageToEncode.getBuffer(), messageToEncode.getRGS_Index());
		MyLoggerFactory.getInstance().getAppLogger().debug(tidStr + ", instant message added");
	}

	public void sendIgnoreMessage(Message message) {
		if (message.isIgnoreMsgSent()) {
			return;
		}
		MyLoggerFactory.getInstance().getAppLogger().info("Sending Ignore Message OTID [" + Common.byteArrayToString(message.getOtid()) + "].");

		ignoreMessage[1] = message.msgRef[0];
		ignoreMessage[2] = message.msgRef[1];
		ignoreMessage[3] = message.msgRef[2];
		ignoreMessage[4] = message.msgRef[3];

		ByteArray buffer = new ByteArray();
		buffer.write(ignoreMessage);
		RequestHandler.AddInstantMessage(buffer, message.getRGS_Index());
		message.ignoreMessageHasBeenSent();
	}

	private void forwardMessage(Message message) {
		MyLoggerFactory.getInstance().getAppLogger().info("Sending Instant Message OTID [" + Common.byteArrayToString(message.getOtid()) + "].");

		forwardMsgHeader[1] = message.msgRef[0];
		forwardMsgHeader[2] = message.msgRef[1];
		forwardMsgHeader[3] = message.msgRef[2];
		forwardMsgHeader[4] = message.msgRef[3];

		ByteArray buffer = new ByteArray();
		buffer.write(forwardMsgHeader);
		RequestHandler.AddInstantMessage(buffer, message.getRGS_Index());
	}

	public void forwardMessage(Message message, Session session) {
		int dpc;
		int opc;

		try {
			if (message.getCalledAddress().getGlobalTitle().getDigits().length() > 9) {
				if (message.getCalledAddress().getGlobalTitle().getDigits().startsWith(Application.Home_CCNDC)) {
					sendIgnoreMessage(message);
				} else {
					if (Application.UL_FWD_DPCs_Enabled == false) {
						forwardMessage(message);
						return;
					}
				}
			} else {
				if (Application.UL_FWD_DPCs_Enabled == false) {
					forwardMessage(message);
					return;
				}
			}

			opc = Application.systemOPC;

			if (Application.UL_FWD_DPCs_Enabled == false) {
				dpc = Application.Abort_DPC[getDPCIndexForAbort_HLR()];
			} else {
				dpc = Application.UL_FWD_DPCs[getDPCIndexForUL_FWD()];
			}

			message.mtp3.dpc = dpc;
			message.mtp3.opc = opc;
			message.mtp3.sls = getSLS();
			message.mtp3.setServiceInfo(Application.atiNetworkInd);

			if (Application.Swap_HLRGT_UL) {
				boolean isOdd = Application.HLRGT.length() % 2 == 1 ? true : false;
				SccpAddressImpl calledAddress = message.sccp.getCalledAddress();

				int globalTitleIndicator = calledAddress.getAddressIndicator().getGlobalTitleIndicator().getValue();
				if (globalTitleIndicator == GlobalTitleIndicator.GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS
						.getValue()) {
					GlobalTitle0100Impl CldGlobalTitle = new GlobalTitle0100Impl();
					CldGlobalTitle = (GlobalTitle0100Impl) calledAddress.getGlobalTitle();

					int translationType = CldGlobalTitle.getTranslationType();
					EncodingScheme encodingScheme = isOdd == false ? BCDEvenEncodingScheme.INSTANCE
							: BCDOddEncodingScheme.INSTANCE;
					org.restcomm.protocols.ss7.indicator.NumberingPlan numberingPlan = CldGlobalTitle
							.getNumberingPlan();
					NatureOfAddress natureOfAddress = CldGlobalTitle.getNatureOfAddress();
					GlobalTitle0100Impl HLR_GT = new GlobalTitle0100Impl(Application.HLRGT, translationType,
							encodingScheme, numberingPlan, natureOfAddress);

					RoutingIndicator ri = RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE;
					int _dpc = calledAddress.getSignalingPointCode();
					int ssn = calledAddress.getSubsystemNumber();
					SccpAddressImpl HLRCalledAddress = new SccpAddressImpl(ri, HLR_GT, _dpc, ssn);

					message.sccp.setCalledAddress(HLRCalledAddress);
				}

				message.tcap.tcMessage = message.tcap.tcapMessage;
			}

			message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
			MyLoggerFactory.getInstance().getAppLogger().debug(session.tidStr + ", invoke ids Added");
			encodeInstantMessage(message, session.tidStr);
			MyLoggerFactory.getInstance().getAppLogger().debug(session.tidStr + ", message encoded and sent");

			return;

		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(String.format("Session [%s]: Failed to Forward Coming Message.", session.tidStr));
			return;
		}

	}

	public void sendAbortInstant(Message message, Session session) {
		int dpc = 0;
		int opc = 0;

		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Send Abort Instant Message.", session.tidStr));
		opc = Application.systemOPC;
		dpc = Application.Abort_DPC[getDPCIndexForAbort_HLR()];
		message.mtp3.setDPC(dpc);
		message.mtp3.setOPC(opc);
		message.mtp3.serviceInfo = Application.abortNetworkInd;
		message.mtp3.sls = getSLS();

		TCAbortMessageImpl abort = new TCAbortMessageImpl();
		abort.setDestinationTransactionId(message.getOtid());

		message.tcap.tcMessage = abort;
		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
		encodeInstantMessage(message, session.tidStr);
	}

	public void sendAbortResponse(Message message, Session session) {
		int dpc = 0;
		int opc = 0;
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Session [%s]: Send Abort Message.", session.tidStr));
		opc = Application.systemOPC;
		dpc = Application.Abort_DPC[getDPCIndexForAbort_HLR()];
		message.mtp3.setDPC(dpc);
		message.mtp3.setOPC(opc);
		message.mtp3.serviceInfo = Application.abortNetworkInd;
		message.mtp3.sls = getSLS();

		TCAbortMessageImpl abort = new TCAbortMessageImpl();
		abort.setDestinationTransactionId(message.getOtid());

		message.tcap.tcMessage = abort;

		message.setRGS_Message_Type(RGS_Messages_Types.MSG_SEND_RESPONSE_MESSAGE);
		encodeInstantMessage(message, session.tidStr);
	}

	public void sendRejectResponse(Message message, Session session) {
	}
	
	public void sendRoutingInfoForLCSReq(Message message, String _msisdn, byte[] otid) {
		byte[] localDialogPortion = { 
				(byte) 0x6b, (byte) 0x1a, 
				
				(byte) 0x28, (byte) 0x18, 
				
					(byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x11, (byte) 0x86, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01,
		
					(byte) 0xa0, (byte) 0x0d, 
				
						(byte) 0x60, (byte) 0x0b, 
				
							(byte) 0xa1, (byte) 0x09, 
				
								(byte) 0x06, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x25, (byte) 0x03 
		};		
		
		int dpc = 0, opc = 0;
		opc = Application.systemOPC;
		dpc = message.getCalledAddress().getSignalingPointCode();

		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);

		message.mtp3.serviceInfo = Application.atiNetworkInd;
		message.mtp3.dpc = dpc;
		message.mtp3.opc = opc;
		message.mtp3.sls = getSLS();
		message.sccp.messageType = 0x09;
		message.sccp.messageHandling = 0x00;

		TCBeginMessageImpl begin = new TCBeginMessageImpl();
		begin.setOriginatingTransactionId(otid);

		AsnInputStream ais = new AsnInputStream(localDialogPortion);

		DialogPortionImpl dpi = new DialogPortionImpl();
		try {
			ais.readTag();
			dpi.decode(ais);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		begin.setDialogPortion(dpi);

		Invoke invoke = TcapFactory.createComponentInvoke();
		invoke.setInvokeId(1l);
		OperationCode operationCode = TcapFactory.createOperationCode();
		operationCode.setLocalOperationCode((long) MAPOperationCode.sendRoutingInfoForLCS);
		invoke.setOperationCode(operationCode);

		SubscriberIdentity msisdn = new SubscriberIdentityImpl(
				new ISDNAddressStringImpl(false, AddressNature.international_number, NumberingPlan.ISDN, _msisdn));
		ISDNAddressString mlcNumber = new ISDNAddressStringImpl(false, AddressNature.international_number,
				NumberingPlan.ISDN, Application.SystemSCF);

		SendRoutingInfoForLCSRequestImpl rir = new SendRoutingInfoForLCSRequestImpl(mlcNumber, msisdn);

		AsnOutputStream asnOS = new AsnOutputStream();
		try {
			rir.encodeAll(asnOS);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameter param = TcapFactory.createParameter();

		param.setTagClass(rir.getTagClass());
		param.setPrimitive(rir.getIsPrimitive());
		try {
			param.setTag(rir.getTag());
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] atiArray = Arrays.copyOfRange(asnOS.toByteArray(), 2, asnOS.toByteArray().length);
		param.setData(atiArray);

		invoke.setParameter(param);

		begin.setComponent(new Component[] { invoke });
		message.tcap.tcMessage = begin;
		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
		encodeInstantMessage(message, Common.byteArrayToString(otid));

	}
	
	public void sendRoutingInfoForLCSRes(Message message, String _imsi, String _networkNodeNumber) {
		byte[] localDialogPortion = {
				(byte) 0x6b, (byte) 0x1a, 
				
				(byte) 0x28, (byte) 0x18, 
			
					(byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x11, (byte) 0x86, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01,

					(byte) 0xa0, (byte) 0x0d, 
				
						(byte) 0x60, (byte) 0x0b, 
				
							(byte) 0xa1, (byte) 0x09, 
				
							(byte) 0x06, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x25, (byte) 0x03 				
		};		
		
		int dpc = 0, opc = 0;
		opc = message.getCalledAddress().getSignalingPointCode();
		dpc = Application.systemOPC;

		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);

		message.mtp3.serviceInfo = Application.atiNetworkInd;
		message.mtp3.dpc = dpc;
		message.mtp3.opc = opc;
		message.mtp3.sls = getSLS();
		message.sccp.messageType = 0x09;
		message.sccp.messageHandling = 0x00;

		TCEndMessageImpl end = new TCEndMessageImpl();
		end.setDestinationTransactionId(message.getOtid());

		AsnInputStream ais = new AsnInputStream(localDialogPortion);

		DialogPortionImpl dpi = new DialogPortionImpl();
		try {
			ais.readTag();
			dpi.decode(ais);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end.setDialogPortion(dpi);

		ReturnResultLast returnResultLast = TcapFactory.createComponentReturnResultLast();
		// Invoke invoke = TcapFactory.createComponentInvoke();
		returnResultLast.setInvokeId((long) message.getInvokeID());
		OperationCode operationCode = TcapFactory.createOperationCode();
		operationCode.setLocalOperationCode((long) MAPOperationCode.sendRoutingInfoForLCS);
		returnResultLast.setOperationCode(operationCode);

//		SubscriberIdentity msisdn = new SubscriberIdentityImpl(new ISDNAddressStringImpl(false, AddressNature.international_number,NumberingPlan.ISDN, _msisdn));
		SubscriberIdentity imsi = new SubscriberIdentityImpl(new IMSIImpl(_imsi));
		ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(false, AddressNature.international_number,
				NumberingPlan.ISDN, _networkNodeNumber);
		LCSLocationInfoImpl locationInfoImpl = new LCSLocationInfoImpl(networkNodeNumber, null, null, false, null, null,
				null, null, null);

		SendRoutingInfoForLCSResponseImpl rir = new SendRoutingInfoForLCSResponseImpl(imsi, locationInfoImpl, null,
				null, null, null, null);

		AsnOutputStream asnOS = new AsnOutputStream();
		try {
			rir.encodeAll(asnOS);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameter param = TcapFactory.createParameter();

		param.setTagClass(rir.getTagClass());
		param.setPrimitive(rir.getIsPrimitive());
		try {
			param.setTag(rir.getTag());
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] atiArray = Arrays.copyOfRange(asnOS.toByteArray(), 2, asnOS.toByteArray().length);
		param.setData(atiArray);

		returnResultLast.setParameter(param);

		end.setComponent(new Component[] { returnResultLast });
		message.tcap.tcMessage = end;
		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
		encodeInstantMessage(message, Common.byteArrayToString(message.getOtid()));

	}
	
	public void sendProvideSubscriberLocationReq(Message message, String _imsi, String _msisdn, byte[] otid) {
		byte[] localDialogPortion = { 
				
				(byte) 0x6b, (byte) 0x1a, 
				
				(byte) 0x28, (byte) 0x18, 
				
					(byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x11, (byte) 0x86, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01,
		
					(byte) 0xa0, (byte) 0x0d, 
				
						(byte) 0x60, (byte) 0x0b, 
				
							(byte) 0xa1, (byte) 0x09, 
				
								(byte) 0x06, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x26, (byte) 0x03 
		};		
		
		int dpc = 0, opc = 0;
		opc = Application.systemOPC;
		dpc = message.getCalledAddress().getSignalingPointCode();		

		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);

		message.mtp3.serviceInfo = Application.atiNetworkInd;
		message.mtp3.dpc = dpc;
		message.mtp3.opc = opc;
		message.mtp3.sls = getSLS();
		message.sccp.messageType = 0x09;
		message.sccp.messageHandling = 0x00;

		TCBeginMessageImpl begin = new TCBeginMessageImpl();
		begin.setOriginatingTransactionId(otid);

		AsnInputStream ais = new AsnInputStream(localDialogPortion);

		DialogPortionImpl dpi = new DialogPortionImpl();
		try {
			ais.readTag();
			dpi.decode(ais);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		begin.setDialogPortion(dpi);

		Invoke invoke = TcapFactory.createComponentInvoke();
		invoke.setInvokeId(1l);
		OperationCode operationCode = TcapFactory.createOperationCode();
		operationCode.setLocalOperationCode((long) MAPOperationCode.provideSubscriberLocation);
		invoke.setOperationCode(operationCode);

		IMSIImpl imsi = new IMSIImpl(_imsi);
		ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl(false, AddressNature.international_number,
				NumberingPlan.ISDN, _msisdn);
		ISDNAddressString mlcNumber = new ISDNAddressStringImpl(false, AddressNature.international_number,
				NumberingPlan.ISDN, Application.SystemSCF);
		LocationTypeImpl locationType = new LocationTypeImpl(LocationEstimateType.currentLocation, null);
		LCSClientIDImpl lcsClientID = new LCSClientIDImpl(LCSClientType.getLCSClientType(3), null, null, null, null,
				null, null);
		ResponseTimeImpl responseTime = new ResponseTimeImpl(ResponseTimeCategory.delaytolerant);
		LCSQoSImpl lcsQoS = new LCSQoSImpl(0x3c, null, false, responseTime, null);
		SupportedGADShapesImpl supportedGADShapes = new SupportedGADShapesImpl(true, true, true, true, true, true,
				true);
		ProvideSubscriberLocationRequestImpl psl = new ProvideSubscriberLocationRequestImpl(locationType, mlcNumber,
				lcsClientID, false, imsi, msisdn, null, null, LCSPriority.getInstance(0), lcsQoS, null,
				supportedGADShapes, null, null, null, null, null, null, false, null, null);

		AsnOutputStream asnOS = new AsnOutputStream();
		try {
			psl.encodeAll(asnOS);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameter param = TcapFactory.createParameter();

		param.setTagClass(psl.getTagClass());
		param.setPrimitive(psl.getIsPrimitive());
		try {
			param.setTag(psl.getTag());
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] atiArray = Arrays.copyOfRange(asnOS.toByteArray(), 2, asnOS.toByteArray().length);
		param.setData(atiArray);

		invoke.setParameter(param);

		begin.setComponent(new Component[] { invoke });
		message.tcap.tcMessage = begin;
		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
		encodeInstantMessage(message, Common.byteArrayToString(otid));

	}
	
	public void sendProvideSubscriberLocationRes(Message message) {
		byte[] localDialogPortion = {
				(byte) 0x6b, (byte) 0x1a, 
				
				(byte) 0x28, (byte) 0x18, 
			
					(byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x11, (byte) 0x86, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01,

					(byte) 0xa0, (byte) 0x0d, 
				
						(byte) 0x60, (byte) 0x0b, 
				
							(byte) 0xa1, (byte) 0x09, 
				
							(byte) 0x06, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x26, (byte) 0x03 				
		};		
		
		int dpc = 0, opc = 0;
		opc = message.getCalledAddress().getSignalingPointCode();		
		dpc = Application.systemOPC;

		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);

		message.mtp3.serviceInfo = Application.atiNetworkInd;
		message.mtp3.dpc = dpc;
		message.mtp3.opc = opc;
		message.mtp3.sls = getSLS();
		message.sccp.messageType = 0x09;
		message.sccp.messageHandling = 0x00;

		TCEndMessageImpl end = new TCEndMessageImpl();
		end.setDestinationTransactionId(message.getOtid());

		AsnInputStream ais = new AsnInputStream(localDialogPortion);

		DialogPortionImpl dpi = new DialogPortionImpl();
		try {
			ais.readTag();
			dpi.decode(ais);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end.setDialogPortion(dpi);

		ReturnResultLast returnResultLast = TcapFactory.createComponentReturnResultLast();
		// Invoke invoke = TcapFactory.createComponentInvoke();
		returnResultLast.setInvokeId((long) message.getInvokeID());
		OperationCode operationCode = TcapFactory.createOperationCode();
		operationCode.setLocalOperationCode((long) MAPOperationCode.provideSubscriberLocation);
		returnResultLast.setOperationCode(operationCode);

//		SubscriberIdentity msisdn = new SubscriberIdentityImpl(new ISDNAddressStringImpl(false, AddressNature.international_number,NumberingPlan.ISDN, _msisdn));
//		SubscriberIdentity imsi = new SubscriberIdentityImpl(new IMSIImpl(_imsi));
//		ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(false, AddressNature.international_number, NumberingPlan.ISDN, _networkNodeNumber);
//		LCSLocationInfoImpl locationInfoImpl = new LCSLocationInfoImpl(networkNodeNumber, null, null, false, null, null, null, null, null);

		ExtGeographicalInformationImpl extGeographicalInformation = null;
		try {
			extGeographicalInformation = new ExtGeographicalInformationImpl(TypeOfShape.EllipsoidArc, 0x2d8b58d,
					0x19a4b9d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x11d, 0x6ed, 0xed);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CellGlobalIdOrServiceAreaIdFixedLengthImpl cellGlobalIdOrServiceAreaIdFixedLength = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(
				new byte[] { (byte) 0x14, (byte) 0xF6, (byte) 0x30, (byte) 0x8D, (byte) 0x05, (byte) 0xC4,
						(byte) 0xCF });
		CellGlobalIdOrServiceAreaIdOrLAIImpl cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(
				cellGlobalIdOrServiceAreaIdFixedLength);

		ProvideSubscriberLocationResponseImpl psl = new ProvideSubscriberLocationResponseImpl(
				extGeographicalInformation, null, null, 0, null, null, false, cellGlobalIdOrServiceAreaIdOrLAI, false,
				null, null, false, null, null, null);

		AsnOutputStream asnOS = new AsnOutputStream();
		try {
			psl.encodeAll(asnOS);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameter param = TcapFactory.createParameter();

		param.setTagClass(psl.getTagClass());
		param.setPrimitive(psl.getIsPrimitive());
		try {
			param.setTag(psl.getTag());
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] atiArray = Arrays.copyOfRange(asnOS.toByteArray(), 2, asnOS.toByteArray().length);
		param.setData(atiArray);

		returnResultLast.setParameter(param);

		end.setComponent(new Component[] { returnResultLast });
		message.tcap.tcMessage = end;
		message.setRGS_Message_Type(RGS_Messages_Types.MSG_INSTANT_MSG);
		encodeInstantMessage(message, Common.byteArrayToString(message.getOtid()));

	}	

	private synchronized static int getDPCIndexForUL_FWD() {
		for (gDPC_IndexForUL_FWD++; gDPC_IndexForUL_FWD < Application.UL_FWD_DPCs.length;) {
			if (Application.UL_FWD_DPCs[gDPC_IndexForUL_FWD] == 0) {
				gDPC_IndexForUL_FWD = 0;
				return gDPC_IndexForUL_FWD;
			} else {
				return gDPC_IndexForUL_FWD;
			}
		}
		return 0;
	}

	private synchronized static int getDPCIndexForRE_VLR() {
		for (gDPC_IndexForRE_VLR++; gDPC_IndexForRE_VLR < Application.RE_DPC.length;) {
			if (Application.RE_DPC[gDPC_IndexForRE_VLR] == 0) {
				gDPC_IndexForRE_VLR = 0;
				return gDPC_IndexForRE_VLR;
			} else {
				return gDPC_IndexForRE_VLR;
			}
		}
		return 0;
	}

	private synchronized static int getDPCIndexForAbort_HLR() {
		for (gDPC_IndexForAbort_HLR++; gDPC_IndexForAbort_HLR < Application.Abort_DPC.length;) {
			if (Application.Abort_DPC[gDPC_IndexForAbort_HLR] == 0) {
				gDPC_IndexForAbort_HLR = 0;
				return gDPC_IndexForAbort_HLR;
			} else {
				return gDPC_IndexForAbort_HLR;
			}
		}
		return 0;
	}

	private synchronized static int getSLS() {
		SLS++;
		if (SLS < 15) {
			return SLS;
		} else {
			SLS = 0;
			return SLS;
		}
	}

}
