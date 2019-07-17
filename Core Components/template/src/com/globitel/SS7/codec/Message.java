package com.globitel.SS7.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.service.lsm.ProvideSubscriberLocationResponseImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SendRoutingInfoForLCSResponseImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationResponseImpl;
import org.restcomm.protocols.ss7.map.service.sms.SendRoutingInfoForSMResponseImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCAbortMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCBeginMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCContinueMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCEndMessage;

import com.globitel.common.structure.Enumerations.RGS_Messages_Types;
import com.globitel.common.structure.Enumerations.SS7_DECODE_TYPE;
import com.globitel.common.utils.ByteArray;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Message {
	private boolean messageDecoded = false;
	protected static final byte ExtendedUnitData = 0x11;
	public static final byte UnitDataService = 0x0A;
	public static final byte UnitData = 0x09;

	public int operationCode;
	private SS7_DECODE_TYPE decodeType;

	private RGS_Messages_Types RGS_Message_Type = RGS_Messages_Types.MSG_INSTANT_MSG;

	public boolean isMessageDecoded() {
		return messageDecoded;
	}

	public MTP3 mtp3;
	public SCCP sccp;
	public TCAP tcap;

	ByteArray encodingBuffer;

	// RGSMESSAGE MEMBERS HERE
	public int rawDataSS7Index;
	public byte[] rawData;
	public byte[] msgRef = new byte[4];
	public int rawLength;
	public int rawSS7Length;
	public int rawDecodingIndex;
	public int rgsIndex;

	public long timestamp;
	private int invokeId;
	private int mapVersion;
	private IMSI imsi;
	private ISDNAddressString mscNumber;
	private boolean isIgnoreMSgSent = false;
	private int cellCountryCode = -1;
	private int cellNetworkCode = -1;
	private int cellid = -1;

	private LMSI lmsi;
	private ISDNAddressString networkNodeNumber;
	
	 TypeOfShape typeOfShape;
	  double latitude;
	  double longitude;
	  double uncertainty;
	  double uncertaintySemiMajorAxis;
	  double uncertaintySemiMinorAxis;
	  double angleOfMajorAxis;
	  double confidence;
	  double altitude;
	  double uncertaintyAltitude;
	  double innerRadius;
	  double uncertaintyRadius;
	  double offsetAngle;
	  double includedAngle;
	  int ageOfLocationEstimate;
	  int mcc;
	  int mnc;
	  int lac;
	  int cellsac;  
	  
	public Message(Message msg) {
		rawData = Arrays.copyOf(msg.rawData, msg.rawLength);
		rawDecodingIndex = msg.rawDecodingIndex;
		rawSS7Length = msg.rawSS7Length;
		rawDataSS7Index = msg.rawDataSS7Index;

		mtp3 = new MTP3();
		sccp = new SCCP();
		tcap = new TCAP();
		encodingBuffer = new ByteArray();
	}

	public int getDecodingIndex() {
		return rawDecodingIndex;
	}

	public void incrementDecodingIndex(int value) {
		rawDecodingIndex += value;
	}

	public void decrementDecodingIndex(int value) {
		rawDecodingIndex -= value;
	}

	public void setDecodingIndex(int value) {
		rawDecodingIndex = value;
	}

	public void setTimeStamp() {
		this.timestamp = System.currentTimeMillis();
	}

	public long getTimeDifference() {
		long difference = System.currentTimeMillis() - timestamp;
		return difference;
	}

	public byte[] getOtid() {
		if (tcap.tcapMessage instanceof TCBeginMessage) {
			return ((TCBeginMessage) tcap.tcapMessage).getOriginatingTransactionId();
		} else if (tcap.tcapMessage instanceof TCContinueMessage) {
			return ((TCContinueMessage) tcap.tcapMessage).getOriginatingTransactionId();
		} else {
			return null;
		}
	}

	public byte[] getDtid() {
		if (tcap.tcapMessage instanceof TCEndMessage) {
			return ((TCEndMessage) tcap.tcapMessage).getDestinationTransactionId();
		} else if (tcap.tcapMessage instanceof TCContinueMessage) {
			return ((TCContinueMessage) tcap.tcapMessage).getDestinationTransactionId();
		} else if (tcap.tcapMessage instanceof TCAbortMessage) {
			return ((TCAbortMessage) tcap.tcapMessage).getDestinationTransactionId();
		} else {
			return null;
		}
	}

	public Message(int rgsIndex, byte[] msgRef) {
		rawLength = 0;
		rawSS7Length = 0;
		mtp3 = new MTP3();
		sccp = new SCCP();
		tcap = new TCAP();
		encodingBuffer = new ByteArray();
		this.rgsIndex = rgsIndex;
		this.msgRef = Arrays.copyOfRange(msgRef, 0, msgRef.length);
	}

	public Message(int rgsIndex) {
		rawLength = 0;
		rawSS7Length = 0;
		mtp3 = new MTP3();
		sccp = new SCCP();
		tcap = new TCAP();
		encodingBuffer = new ByteArray();
		this.rgsIndex = rgsIndex;
		// this.msgRef = Arrays.copyOfRange(msgRef, 0, msgRef.length);
	}

	public ByteArray getBuffer() {
		return encodingBuffer;
	}

	public void encode() {
		Message.logDebug("Encoding started");
		try {
			encodingBuffer.reset();
			byte[] instantMsgHeader = { RGS_Message_Type.get(), 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0, 0x00, 0x07,
					(byte) 0, 0x00 };

			instantMsgHeader[1] = this.msgRef[0];
			instantMsgHeader[2] = this.msgRef[1];
			instantMsgHeader[3] = this.msgRef[2];
			instantMsgHeader[4] = this.msgRef[3];

			encodingBuffer.write(instantMsgHeader);
			mtp3.encode(encodingBuffer);
			sccp.encode(encodingBuffer);
			tcap.encode(encodingBuffer);

			encodingBuffer.buffer[6] = (byte) ((encodingBuffer.getSize() + 3 - 11) & 0xff);
			encodingBuffer.buffer[7] = (byte) (((encodingBuffer.getSize() + 3 - 11) >> 8) & 0xff);
			encodingBuffer.buffer[9] = (byte) ((encodingBuffer.getSize() - 11) & 0xff);
			encodingBuffer.buffer[10] = (byte) (((encodingBuffer.getSize() - 11) >> 8) & 0xff);
		} catch (Exception e) {
			Message.logError("Exception, Message.encode:" + e.getMessage());
		}
	}

	public void decode() {
		Message.logDebug("Decoding started");

		int RGS_Type = ConfigurationManager.getInstance().getIntValue("RGS_Type");
		
		logDebug("RGS Type: " + RGS_Type);
		
		// 0: ussd rgs , 1: active rgs

		if (RGS_Type == 0) { // 0: ussd rgs 

			short usParameterLength = 0;
			ByteBuffer bb = ByteBuffer.wrap(this.rawData);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			byte[] bbarr = new byte[bb.remaining()];
			bb.get(bbarr);
			bb.position(0);

			byte[][] msg = new byte[5][];
			for (int iIndex = 0; iIndex < 5; iIndex++) {
				byte tcapType = bb.get();
				usParameterLength = bb.getShort();
				int length = usParameterLength & 0xff;
				byte[] temp = new byte[length];
				System.arraycopy(bbarr, bb.position(), temp, 0, length);
				msg[iIndex] = temp;
				bb.position(bb.position() + usParameterLength);
			}

			mtp3.decode(this);
			rawDecodingIndex = rawDataSS7Index;
			sccp.decode(msg[1], msg[2]);
			tcap.decode(msg[3]);
		} else if (RGS_Type == 1) { // 1: active rgs
			mtp3.decode(this);
			rawDecodingIndex = rawDataSS7Index;
			sccp.decode(this);
			tcap.decode(this);
		}
		messageDecoded = true;
	}

	public static void logDebug(String string) {
		// TODO Auto-generated method stub
		if (MyLoggerFactory.getInstance().getAppLogger() != null)
			MyLoggerFactory.getInstance().getAppLogger().debug(string);
	}

	public static void logBuffer(byte[] buffer, String header) {
		StringBuilder sb = new StringBuilder();
		sb.append(header + ":");

		for (byte b : buffer) {
			sb.append(String.format("%02x", b));
		}
		if (MyLoggerFactory.getInstance().getAppLogger() != null)
			MyLoggerFactory.getInstance().getAppLogger().debug(sb.toString());
	}

	public static void logError(byte[] buffer, String header) {
		StringBuilder sb = new StringBuilder();
		sb.append(header + ":");

		for (byte b : buffer) {
			sb.append(String.format("%02x", b));
		}
		if (MyLoggerFactory.getInstance().getAppLogger() != null)
			MyLoggerFactory.getInstance().getAppLogger().error(sb.toString());
	}

	public static void logError(String string) {
		// TODO Auto-generated method stub
		if (MyLoggerFactory.getInstance().getAppLogger() != null)
			MyLoggerFactory.getInstance().getAppLogger().error(string);
	}

	public static void logInfo(String string) {
		if (MyLoggerFactory.getInstance().getAppLogger() != null)
			MyLoggerFactory.getInstance().getAppLogger().info(string);
	}

	public int getOpc() {
		// TODO Auto-generated method stub
		return mtp3.getOPC();
	}

	public int getDpc() {
		// TODO Auto-generated method stub
		return mtp3.getDPC();
	}

	public int getSls() {
		// TODO Auto-generated method stub
		return mtp3.getSLS();
	}

	public byte getMessageHandling() {
		// TODO Auto-generated method stub
		return sccp.getMessageHandling();
	}

	public SccpAddressImpl getCalledAddress() {
		// TODO Auto-generated method stub
		return sccp.getCalledAddress();
	}

	public SccpAddressImpl getCallingAddress() {
		// TODO Auto-generated method stub
		return sccp.getCallingAddress();
	}

	public byte getMessageType() {
		// TODO Auto-generated method stub
		return sccp.getMessageType();
	}

	public void setOpc(int opc) {
		// TODO Auto-generated method stub
		mtp3.setOPC(opc);
	}

	public void setDpc(int dpc) {
		// TODO Auto-generated method stub
		mtp3.setDPC(dpc);
	}

	public void setClg(SccpAddressImpl callingAddress) {
		// TODO Auto-generated method stub
		sccp.setCallingAddress(callingAddress);
	}

	public void setCld(SccpAddressImpl calledAddress) {
		// TODO Auto-generated method stub
		sccp.setCalledAddress(calledAddress);
	}

	public void setMessageType(byte messageType) {
		// TODO Auto-generated method stub
		sccp.setMessageType(messageType);
	}

	public void setMessageHandling(byte messageHandling) {
		// TODO Auto-generated method stub
		sccp.setMessageHandling(messageHandling);
	}

	public byte getServiceInfo() {
		// TODO Auto-generated method stub
		return mtp3.getServiceInfo();
	}

	public void setServiceInfo(byte serviceInfo) {
		// TODO Auto-generated method stub
		mtp3.setServiceInfo(serviceInfo);
	}

	public void setSLS(int sls) {
		// TODO Auto-generated method stub
		mtp3.setSLS(sls);
	}

	public static void logInfo(String txt, Message message, String sessionInfo) {
		logInfo(txt + ":" + (message == null ? "" : message.toString()) + ":"
				+ (sessionInfo == null ? "" : sessionInfo));
	}

	public int getRGS_Index() {
		return this.rgsIndex;
	}

	public void setInvokeID(int invokeId) {
		this.invokeId = invokeId;

	}

	public int getInvokeID() {
		return invokeId;
	}

	public void setMAP_Version(ApplicationContextName applicationContextName) {
		int length = applicationContextName.getOid().length;
		this.mapVersion = (int) applicationContextName.getOid()[length - 1];
	}

	public int getMAP_Version() {
		return this.mapVersion;
	}

	public void setOperationCode(int operationCode) {
		this.operationCode = operationCode;
	}

	public int getOperationCode() {
		return operationCode;
	}

	public void setSS7DecodeType(SS7_DECODE_TYPE decodeType) {
		// TODO Auto-generated method stub
		this.decodeType = decodeType;
	}

	public SS7_DECODE_TYPE getSS7DecodeType() {
		return this.decodeType;
	}

	public void setIMSI(IMSI imsi) {
		this.imsi = imsi;
	}

	public IMSI getIMSI() {
		return this.imsi;
	}

	public void setMSC(ISDNAddressString mscNumber) {
		this.mscNumber = mscNumber;
	}

	public ISDNAddressString getMSC() {
		return this.mscNumber;
	}

	public void ignoreMessageHasBeenSent() {
		isIgnoreMSgSent = true;
	}

	public boolean isIgnoreMsgSent() {
		return isIgnoreMSgSent;
	}

	public void setRGS_Message_Type(RGS_Messages_Types rgs_message_types) {
		this.RGS_Message_Type = rgs_message_types;
	}

	public void setAnyTimeInterogationAttributes(AnyTimeInterrogationResponseImpl anyTimeInterogationResp,
			String sessionID) throws NullPointerException {
		try {
			this.cellCountryCode = anyTimeInterogationResp.getSubscriberInfo().getLocationInformation()
					.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			this.cellCountryCode = -1;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get Cell Country Code from ATI Response Message, Exception : %s.",
					sessionID, e.getMessage()), e);
		}
		try {
			this.cellNetworkCode = anyTimeInterogationResp.getSubscriberInfo().getLocationInformation()
					.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			this.cellNetworkCode = -1;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get Cell Network Code from ATI Response Message, Exception : %s.",
					sessionID, e.getMessage()), e);
		}
		try {
			this.cellid = anyTimeInterogationResp.getSubscriberInfo().getLocationInformation()
					.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength()
					.getCellIdOrServiceAreaCode();
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			this.cellid = -1;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get Cell ID from ATI Response Message, Exception : %s.", sessionID, e.getMessage()), e);			
		}
		MyLoggerFactory.getInstance().getAppLogger().info(
				String.format("Session[%s]: ATI returned, Cell Country Code[%d], Mobile Country Code[%d], CellID[%d].",
						sessionID, this.cellCountryCode, this.cellNetworkCode, this.cellid));
	}

	public void setSendRoutingInfoForSMAttributes(SendRoutingInfoForSMResponseImpl sendRoutingInfoForSMResp,
			String sessionID) throws NullPointerException {
		try {
			this.imsi = sendRoutingInfoForSMResp.getIMSI();
		} catch (Exception e) {
			this.imsi = null;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get imsi from SendRoutingInfoForSM Response Message, Exception : %s.", sessionID, e.getMessage()), e);
		}
		try {
			this.networkNodeNumber = sendRoutingInfoForSMResp.getLocationInfoWithLMSI().getNetworkNodeNumber();
		} catch (Exception e) {
			this.cellNetworkCode = -1;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get Cell networkNodeNumber from SendRoutingInfoForSM Response Message, Exception : %s.", sessionID, e.getMessage()), e);
		}
		try {
			this.lmsi = sendRoutingInfoForSMResp.getLocationInfoWithLMSI().getLMSI();
		} catch (Exception e) {
			this.lmsi = null;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get LMSI from SendRoutingInfoForSM Response Message, Exception : %s.", sessionID, e.getMessage()), e);
		}
		MyLoggerFactory.getInstance().getAppLogger().info(
				String.format("Session[%s]: RRL SendRoutingInfoForSM is captured, IMSI[%s], Network Node Number[%s].",
						new Object[] { sessionID, this.imsi.getData(), this.networkNodeNumber.getAddress() }));
	}
	
	public void setSendRoutingInfoForLCSAttributes(SendRoutingInfoForLCSResponseImpl sendRoutingInfoForLCSResp, String sessionID) throws NullPointerException
	  {
	    try {
	      this.imsi = sendRoutingInfoForLCSResp.getTargetMS().getIMSI();
	    }
	    catch (Exception e) {
	      this.imsi = null;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get imsi from SendRoutingInfoForLCS Response Message, Exception : %s.\", new", sessionID, e.getMessage()), e);
	    }
	    try {
	      this.networkNodeNumber = sendRoutingInfoForLCSResp.getLCSLocationInfo().getNetworkNodeNumber();
	    }
	    catch (Exception e) {
	      this.cellNetworkCode = -1;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get Cell networkNodeNumber from SendRoutingInfoForLCS Response Message, Exception : %s.", sessionID, e.getMessage()), e);
	    }
		MyLoggerFactory.getInstance().getAppLogger().info(
	      String.format("Session[%s]: RRL SendRoutingInfoForLCS is captured, IMSI[%s], Network Node Number[%s].", new Object[] {
	      sessionID, this.imsi.getData(), this.networkNodeNumber.getAddress() }));
	  }
	  
	  public void setProvideSubscriberLocationAttributes(ProvideSubscriberLocationResponseImpl provideSubscriberLocationResp, String sessionID) throws NullPointerException
	  {
	    try {
	      if (provideSubscriberLocationResp.getLocationEstimate() != null) {
	        this.typeOfShape = provideSubscriberLocationResp.getLocationEstimate().getTypeOfShape();
	        this.latitude = provideSubscriberLocationResp.getLocationEstimate().getLatitude();
	        this.longitude = provideSubscriberLocationResp.getLocationEstimate().getLongitude();
	        this.uncertainty = provideSubscriberLocationResp.getLocationEstimate().getUncertainty();
	        this.uncertaintySemiMajorAxis = provideSubscriberLocationResp.getLocationEstimate().getUncertaintySemiMajorAxis();
	        this.uncertaintySemiMinorAxis = provideSubscriberLocationResp.getLocationEstimate().getUncertaintySemiMinorAxis();
	        this.angleOfMajorAxis = provideSubscriberLocationResp.getLocationEstimate().getAngleOfMajorAxis();
	        this.confidence = provideSubscriberLocationResp.getLocationEstimate().getConfidence();
	        this.altitude = provideSubscriberLocationResp.getLocationEstimate().getAltitude();
	        this.uncertaintyAltitude = provideSubscriberLocationResp.getLocationEstimate().getUncertaintyAltitude();
	        this.innerRadius = provideSubscriberLocationResp.getLocationEstimate().getInnerRadius();
	        this.uncertaintyRadius = provideSubscriberLocationResp.getLocationEstimate().getUncertaintyRadius();
	        this.offsetAngle = provideSubscriberLocationResp.getLocationEstimate().getOffsetAngle();
	        this.includedAngle = provideSubscriberLocationResp.getLocationEstimate().getIncludedAngle();
	      }
	      
	      this.ageOfLocationEstimate = provideSubscriberLocationResp.getAgeOfLocationEstimate().intValue();
	      
	      if (provideSubscriberLocationResp.getCellIdOrSai() != null) {
	        if (provideSubscriberLocationResp.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
	          this.mcc = provideSubscriberLocationResp.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
	          this.mnc = provideSubscriberLocationResp.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
	          this.lac = provideSubscriberLocationResp.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
	          this.cellsac = provideSubscriberLocationResp.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
	        }
	        
	        if (provideSubscriberLocationResp.getCellIdOrSai().getLAIFixedLength() != null) {
	          this.mcc = provideSubscriberLocationResp.getCellIdOrSai().getLAIFixedLength().getMCC();
	          this.mnc = provideSubscriberLocationResp.getCellIdOrSai().getLAIFixedLength().getMNC();
	          this.lac = provideSubscriberLocationResp.getCellIdOrSai().getLAIFixedLength().getLac();
	        }
	      }
	    }
	    catch (Exception e)
	    {
	      this.imsi = null;
			MyLoggerFactory.getInstance().getAppLogger().error(String.format(
					"Session[%s]: Failed To get ProvideSubscriberLocation Attributes, Exception : %s.", sessionID, e.getMessage()), e);
	      
	    }
		MyLoggerFactory.getInstance().getAppLogger().info(
	      String.format("Session[%s]: RRL ProvideSubscriberLocation is captured, Type of Shape[%s], LAT[%s], LONG[%s], Cell/SAC[%d].", new Object[] {
	      sessionID, this.typeOfShape.toString(), Double.valueOf(this.latitude), Double.valueOf(this.longitude), Integer.valueOf(this.cellsac) }));
	  }

	/**
	 * @return the cellCountryCode
	 */
	public int getCellCountryCode() {
		return cellCountryCode;
	}

	/**
	 * @return the mobileCountryCode
	 */
	public int getCellNetworkCode() {
		return cellNetworkCode;
	}

	/**
	 * @return the cellid
	 */
	public int getCellid() {
		return cellid;
	}

	public ISDNAddressString getNetworkNodeNumber() {
		return this.networkNodeNumber;
	}

	public void setNetworkNodeNumber(ISDNAddressString networkNodeNumber) {
		this.networkNodeNumber = networkNodeNumber;
	}
	 public TypeOfShape getTypeOfShape() {
		    return this.typeOfShape;
		  }
		  
		  public void setTypeOfShape(TypeOfShape typeOfShape) {
		    this.typeOfShape = typeOfShape;
		  }
		  
		  public double getLatitude() {
		    return this.latitude;
		  }
		  
		  public void setLatitude(double latitude) {
		    this.latitude = latitude;
		  }
		  
		  public double getLongitude() {
		    return this.longitude;
		  }
		  
		  public void setLongitude(double longitude) {
		    this.longitude = longitude;
		  }
		  
		  public double getUncertainty() {
		    return this.uncertainty;
		  }
		  
		  public void setUncertainty(double uncertainty) {
		    this.uncertainty = uncertainty;
		  }
		  
		  public double getUncertaintySemiMajorAxis() {
		    return this.uncertaintySemiMajorAxis;
		  }
		  
		  public void setUncertaintySemiMajorAxis(double uncertaintySemiMajorAxis) {
		    this.uncertaintySemiMajorAxis = uncertaintySemiMajorAxis;
		  }
		  
		  public double getUncertaintySemiMinorAxis() {
		    return this.uncertaintySemiMinorAxis;
		  }
		  
		  public void setUncertaintySemiMinorAxis(double uncertaintySemiMinorAxis) {
		    this.uncertaintySemiMinorAxis = uncertaintySemiMinorAxis;
		  }
		  
		  public double getAngleOfMajorAxis() {
		    return this.angleOfMajorAxis;
		  }
		  
		  public void setAngleOfMajorAxis(double angleOfMajorAxis) {
		    this.angleOfMajorAxis = angleOfMajorAxis;
		  }
		  
		  public double getConfidence() {
		    return this.confidence;
		  }
		  
		  public void setConfidence(double confidence) {
		    this.confidence = confidence;
		  }
		  
		  public double getAltitude() {
		    return this.altitude;
		  }
		  
		  public void setAltitude(double altitude) {
		    this.altitude = altitude;
		  }
		  
		  public double getUncertaintyAltitude() {
		    return this.uncertaintyAltitude;
		  }
		  
		  public void setUncertaintyAltitude(double uncertaintyAltitude) {
		    this.uncertaintyAltitude = uncertaintyAltitude;
		  }
		  
		  public double getInnerRadius() {
		    return this.innerRadius;
		  }
		  
		  public void setInnerRadius(double innerRadius) {
		    this.innerRadius = innerRadius;
		  }
		  
		  public double getUncertaintyRadius() {
		    return this.uncertaintyRadius;
		  }
		  
		  public void setUncertaintyRadius(double uncertaintyRadius) {
		    this.uncertaintyRadius = uncertaintyRadius;
		  }
		  
		  public double getOffsetAngle() {
		    return this.offsetAngle;
		  }
		  
		  public void setOffsetAngle(double offsetAngle) {
		    this.offsetAngle = offsetAngle;
		  }
		  
		  public double getIncludedAngle() {
		    return this.includedAngle;
		  }
		  
		  public void setIncludedAngle(double includedAngle) {
		    this.includedAngle = includedAngle;
		  }
		  
		  public int getAgeOfLocationEstimate() {
		    return this.ageOfLocationEstimate;
		  }
		  
		  public void setAgeOfLocationEstimate(int ageOfLocationEstimate) {
		    this.ageOfLocationEstimate = ageOfLocationEstimate;
		  }
		  
		  public int getMcc() {
		    return this.mcc;
		  }
		  
		  public void setMcc(int mcc) {
		    this.mcc = mcc;
		  }
		  
		  public int getMnc() {
		    return this.mnc;
		  }
		  
		  public void setMnc(int mnc) {
		    this.mnc = mnc;
		  }
		  
		  public int getLac() {
		    return this.lac;
		  }
		  
		  public void setLac(int lac) {
		    this.lac = lac;
		  }
		  
		  public int getCellsac() {
		    return this.cellsac;
		  }
		  
		  public void setCellsac(int cellsac) {
		    this.cellsac = cellsac;
		  }
}
