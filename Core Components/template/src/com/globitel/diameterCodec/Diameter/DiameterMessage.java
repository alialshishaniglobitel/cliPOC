package com.globitel.diameterCodec.Diameter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.globitel.diameterCodec.interfaces.AnswerInfo;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

/**
 * @author Ali.Shishani
 *
 */
public class DiameterMessage implements Evaluatable
{
	public class SESSION_AVP_CODES
	{
		public static final int Session_id = 263;
		public static final int Origin_host = 264;
		public static final int Origin_realm = 296;
		public static final int Dest_host = 293;
		public static final int Dest_realm = 283;
		public static final int IMSI = 1;
	}

	private static Map<Integer, AVPInfo> avpMap = new HashMap<>();
	final int MESSAGE_BUFFER_LENGTH = 2000;
	private DiameterAVPList avpList = new DiameterAVPList();
	private static final int AVP_START_LOCATION_IN_MESSAGE = 20;
	private static final String DEFAULT_RESULT_CODE = "1";

	private byte[] data;
	private Header header = new Header();;
	private int encodeLength;
	private int currentLocation;
	// int startLocation;
	private String srcIP;
	private String srcCluster;
	private String srcRoute;
	private List<String> dstIP;
	private List<String> dstRoute;
	private List<String> dstRouteCluster;
	// private String imsi;
	private DiameterMessage generatedAnswer;
	private boolean continueOnUnknownAvps;
	private boolean dropped = false;
	private DiameterMessageStatus diameterMessageStatus = new DiameterMessageStatus();
	public int srcPort;
	public int dstPort;
	public String srcAddress;
	public String dstAddress;

	@Override
	public void finalize()
	{
		try
		{
			header = null;
			data = null;
			avpList = null;
			if (dstIP != null)
				dstIP.clear();

			dstIP = null;
			if (dstRoute != null)
				dstRoute.clear();
			if ( dstRouteCluster != null )
				dstRouteCluster.clear();
			dstRoute = null;
			dstRouteCluster = null;
			generatedAnswer = null;
			diameterMessageStatus = null;

		}
		finally
		{
			try
			{
				super.finalize();
			}
			catch (Throwable e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public DiameterMessage(DiameterMessage in)
	{
		copyMsg(in);
	}

	private void setDstIP(List<String> dstIP2)
	{
		if (dstIP2 != null)
		{
			// TODO Auto-generated method stub
			if (this.dstIP == null)
				this.dstIP = new ArrayList<>();

			this.dstIP.clear();
			this.dstIP.addAll(dstIP2);
		}
	}

	private void reset()
	{
		data = null;
		data = new byte[10000];
		// at this point we assume that data member, is set to received buffer
		setEncodeLength(0);
		currentLocation = AVP_START_LOCATION_IN_MESSAGE;
		// startLocation = AVP_START_LOCATION_IN_MESSAGE;
	}

	public DiameterMessage()
	{
		reset();
	}

	public DiameterMessage(byte[] dBuffer)
	{
		this();
		data = Arrays.copyOf(dBuffer, dBuffer.length);
	}

	public DiameterMessage(boolean _continueOnUnknownAvps)
	{
		continueOnUnknownAvps = _continueOnUnknownAvps;
		reset();
	}

	public DiameterMessage(byte[] dBuffer, boolean _continueOnUnknownAvps)
	{
		continueOnUnknownAvps = _continueOnUnknownAvps;
		data = dBuffer;
	}

	private byte[] encodeHeader()
	{
		int iArrayStorageIndex = 0;
		// we call this after adding all the AVPs
		// transfer the data you have in the header to the buffer that u filled
		// using the AVPs

		data[iArrayStorageIndex++] = header.version;

		// first lets evaluate the message length
		// int uiMessageLength = AVP_START_LOCATION_IN_MESSAGE +
		// (currentLocation - startLocation);
		int uiMessageLength = currentLocation;
		Util.setLittleIndian(uiMessageLength, 3, data, (iArrayStorageIndex));
		iArrayStorageIndex += 3;

		data[iArrayStorageIndex++] = header.flags;
		Util.setLittleIndian(header.commandCode, 3, data, (iArrayStorageIndex));
		iArrayStorageIndex += 3;

		Util.setLittleIndian(header.applicationID, 4, data, (iArrayStorageIndex));
		iArrayStorageIndex += 4;

		Util.setLittleIndian(header.hopByHopIdentifier, 4, data, (iArrayStorageIndex));
		iArrayStorageIndex += 4;

		Util.setLittleIndian(header.endToEndIdentifier, 4, data, (iArrayStorageIndex));
		iArrayStorageIndex += 4;
		setEncodeLength(uiMessageLength);
		byte[] cpy = Arrays.copyOf(data, getEncodeLength());
		return cpy;
	}

	void encodeHeader(byte c_ucVersion, boolean c_bIsError, boolean c_bIsRequest, boolean c_bIsProxiable, boolean c_bIsPotentiallyRetransmitted, int c_uiCommandCode, int c_uiApplicationID,
			int c_uiHopByHopIdentifier, int c_uiEndToEndIdentifier)
	{
		header.version = c_ucVersion;
		// we need to create a one byte representation of the flags
		header.flags = 0x00;// initialize

		/*
		 * 0 1 2 3 4 5 6 7 ---------------------- R P E T r r r r
		 */

		if (c_bIsRequest)
		{
			header.flags |= 0x80;
		}
		if (c_bIsProxiable)
		{
			header.flags |= 0x40;
		}
		if (c_bIsError)
		{
			header.flags |= 0x20;
		}
		if (c_bIsPotentiallyRetransmitted)
		{
			header.flags |= 0x10;
		}

		header.commandCode = c_uiCommandCode;
		header.applicationID = c_uiApplicationID;
		header.hopByHopIdentifier = c_uiHopByHopIdentifier;
		header.endToEndIdentifier = c_uiEndToEndIdentifier;
	}

	// we are supposed to have an array here hopefully it will work

	private AVP searchAllAVPS(int avpCodeToSearch, int parent_code)
	{
		AVP avp = null;
		for (AVP current : getAvpList().list)
		{
			if (current.getAvpCode() == avpCodeToSearch && current.getParentAvpCode() == parent_code)
			{
				avp = current;
				break;
			}
			else
			{
				current.setParent(avp);
				avp = find(avpCodeToSearch, current);
			}
		}
		return avp;
	}

	private AVP find(int c_uiAVPCommandCode, AVP c_pAVPToSearch) // THIS METHOD
																	// FINDS
	// THE AVP THEN
	// CALLS ITS DECODE
	// METHOD
	{
		// the extra default parameter determines whether we should decode the
		// next twin avp, it's true by default
		AVP pFoundAVP = null;

		if (c_pAVPToSearch != null)
		{
			DiameterAVPGrouped casted = null;
			if (c_pAVPToSearch instanceof DiameterAVPGrouped)
			{
				casted = (DiameterAVPGrouped) c_pAVPToSearch;
				casted.decode();
				pFoundAVP = casted.find(c_uiAVPCommandCode);
			}

			AVP pLooperAVP = pFoundAVP;

			if (pFoundAVP != null)
			{
				pFoundAVP.setParent(c_pAVPToSearch);
				pFoundAVP.decoding_buffer = data;
				pFoundAVP.decode();
				while (pLooperAVP.getNextAVP() != null && (pLooperAVP.getNextAVP().getAvpCode() == pLooperAVP.getAvpCode()))
				{
					pLooperAVP = pLooperAVP.getNextAVP();
					pLooperAVP.decode();
				}
			}
		}
		else
		{
			pFoundAVP = getAVP(c_uiAVPCommandCode);

			AVP pLooperAVP = pFoundAVP;

			if (pFoundAVP != null)
			{
				pFoundAVP.decode();
				while ((pLooperAVP.getNextAVP() != null) && (pLooperAVP.getNextAVP().getAvpCode() == pLooperAVP.getAvpCode()))
				{
					pLooperAVP = pLooperAVP.getNextAVP();
					pLooperAVP.decode();
				}
			}
		}
		return pFoundAVP;
	}

	public void print(String _header) // num of spaces
	{
		String s = "#####################################################" + "\n" + _header + "\n" + "SRC IP: " + getSrcIP() + "\n" + "DST IP: " + getDstIP() + "\n" + "Version: " + header.version
				+ "\n" + "Length : " + header.messageLength + "\n" + "Flags  : " + header.flags + "\n" + "Command Code  : " + header.commandCode + "\n" + "Application ID: " + header.applicationID
				+ "\n" + "HopByHop Identifier: " + header.hopByHopIdentifier + "\n" + "EndToEnd Identifier: " + header.endToEndIdentifier + "\n";

		// for each avp print CC, except for grouped avp call its print method.
		for (int iPrintIndex = 0; iPrintIndex < getAvpList().getNumberOfItems(); iPrintIndex++)
		{
			AVP avp = getAvpList().GetItem(iPrintIndex);
			avp.decoding_buffer = data;
			s += avp.toString("|||") + "\n";
		}
		MyLoggerFactory.getInstance().getAppLogger().debug(s);
	}

	/**
	 * @param decodeInternally
	 *            specifies whether internal avps are decoded or not.
	 * @return true, if decoded successfully.
	 */
	public boolean decode(boolean decodeInternally)
	{
		try
		{
			int loc = 0;

			header.version = (data[loc]); // FIRST
											// BYTE
											// IS
											// VERSION
			loc++; // JUMP TO LENGTH
			header.messageLength = Util.GetBigEndian(data, loc, 3); // STORE
																	// MESSAGE
																	// LENGTH
			loc += 3; // SKIP LENGTH
			header.flags = data[loc];
			loc++; // JUMP TO COMMAND CODE //CODE TO EXTRACT THE
					// VALUes
			header.commandCode = Util.GetBigEndian(data, loc, 3); // COMMAND
																	// CODE
																	// EXTRACTION
			loc += 3; // SKIP COMMAND CODE
			header.applicationID = Util.GetBigEndian(data, loc, 4); // APPLICATION
																	// ID
																	// EXTRACTION
			loc += 4;
			header.hopByHopIdentifier = Util.GetBigEndian(data, loc, 4);
			loc += 4;
			header.endToEndIdentifier = Util.GetBigEndian(data, loc, 4);
			loc += 4;

			if (header.messageLength < AVP_START_LOCATION_IN_MESSAGE)
			{
				MyLoggerFactory.getInstance().getAppLogger().error(String.format("DiameterMessage::Decode, Message Length : %u is less than possible minimum which is %d", header.messageLength, AVP_START_LOCATION_IN_MESSAGE));
				return false;
			}

			long endOfMsg = header.messageLength;

			while (loc < endOfMsg)
			{
				int avpStart = loc;
				int avpCode = Util.GetBigEndian(data, loc, 4);

				if (false == isExpressionValid((avpCode != 0)))
				{
					return false;
				}
				AVP createdAVP = null;
				if (isAvpDefined(avpCode))
				{
					createdAVP = newAVP(avpCode, header.applicationID, true);
				}
				else
				{
					if (continueOnUnknownAvps)
					{
						createdAVP = newAVP("Diameter.AVP", avpCode, header.applicationID);
					}
				}

				if (createdAVP != null)
				{
					loc = createdAVP.decodedHeader(data, loc);
					int avpDataLength = createdAVP.avpInfo.length - (loc - avpStart);
					long iRemainingBytes = (endOfMsg - loc);

					if ((avpDataLength < 0) || (avpDataLength > iRemainingBytes))
					{
						MyLoggerFactory.getInstance().getAppLogger().error("DiameterMessage::Decode, AVP Data Length : %d exceeds the message length. The AVP length should be %d", avpDataLength, iRemainingBytes);
						return false;
					}

					createdAVP.markAVP(data, loc, avpDataLength);
					if (decodeInternally)
						createdAVP.decode();
					loc += avpDataLength;
					loc += createdAVP.getAVPPadding(0);
				}
				else
				{
					return false;
				}
			}
			return true;// decoded successfully
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(GlobalMethods.getExceptionString(e));
			return false;
		}
	}
	
	public String sessionDecode()
	{
		try
		{
			int loc = 0;

			header.version = (data[loc]); // FIRST
											// BYTE
											// IS
											// VERSION
			loc++; // JUMP TO LENGTH
			header.messageLength = Util.GetBigEndian(data, loc, 3); // STORE
																	// MESSAGE
																	// LENGTH
			loc += 3; // SKIP LENGTH
			header.flags = data[loc];
			loc++; // JUMP TO COMMAND CODE //CODE TO EXTRACT THE
					// VALUes
			header.commandCode = Util.GetBigEndian(data, loc, 3); // COMMAND
																	// CODE
																	// EXTRACTION
			loc += 3; // SKIP COMMAND CODE
			header.applicationID = Util.GetBigEndian(data, loc, 4); // APPLICATION
																	// ID
																	// EXTRACTION
			loc += 4;
			header.hopByHopIdentifier = Util.GetBigEndian(data, loc, 4);
			loc += 4;
			header.endToEndIdentifier = Util.GetBigEndian(data, loc, 4);
			loc += 4;

			if (header.messageLength < AVP_START_LOCATION_IN_MESSAGE)
			{
				MyLoggerFactory.getInstance().getAppLogger().error(String.format("DiameterMessage::Decode, Message Length : %u is less than possible minimum which is %d", header.messageLength, AVP_START_LOCATION_IN_MESSAGE));
				return null;
			}

			long endOfMsg = header.messageLength;

			while (loc < endOfMsg)
			{
				int avpStart = loc;
				int avpCode = Util.GetBigEndian(data, loc, 4);

				if (false == isExpressionValid((avpCode != 0)))
				{
					return null;
				}
				AVP createdAVP = null;
				if (isAvpDefined(avpCode))
				{
					createdAVP = newAVP(avpCode, header.applicationID, false);
				}
				else
				{
					if (continueOnUnknownAvps)
					{
						createdAVP = newAVP("Diameter.AVP", avpCode, header.applicationID);
					}
				}

				if (createdAVP != null)
				{
					loc = createdAVP.decodedHeader(data, loc);
					int avpDataLength = createdAVP.avpInfo.length - (loc - avpStart);
					long iRemainingBytes = (endOfMsg - loc);

					if ((avpDataLength < 0) || (avpDataLength > iRemainingBytes))
					{
						MyLoggerFactory.getInstance().getAppLogger().error("DiameterMessage::Decode, AVP Data Length : %d exceeds the message length. The AVP length should be %d", avpDataLength, iRemainingBytes);
						return null;
					}

					createdAVP.markAVP(data, loc, avpDataLength);
					
					loc += avpDataLength;
					loc += createdAVP.getAVPPadding(0);
					
					if (avpCode == 263)
					{
						createdAVP.decode();
						return createdAVP.getValue();
					}
					createdAVP = null;
				}
				else
				{
					return null;
				}
			}
			return null;// decoded successfully
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(String.format("Exception: %s", e.getMessage()), e);
			return null;
		}
	}

	private AVP newAVP(int c_iAVPCode, long m_uiApplicationID, boolean addToSession)
	{
		AVP pCreatedAVP = CreateAVP(c_iAVPCode, (int) m_uiApplicationID);

		if (pCreatedAVP != null)
		{
			pCreatedAVP.setAvpCode(c_iAVPCode);
			pCreatedAVP.applicationId = m_uiApplicationID;
			if ( addToSession )
			{
				getAvpList().add(pCreatedAVP);
			}
		}
		return pCreatedAVP;
	}

	private AVP newAVP(String type, int c_iAVPCode, long m_uiApplicationID)
	{
		AVP pCreatedAVP = CreateAVP(type, c_iAVPCode, (int) m_uiApplicationID);

		if (pCreatedAVP != null)
		{
			pCreatedAVP.setAvpCode(c_iAVPCode);
			pCreatedAVP.applicationId = m_uiApplicationID;
			getAvpList().add(pCreatedAVP);
		}
		return pCreatedAVP;
	}

	private static AVP CreateAVP(String type, int c_uiAVPCode, int applicationId)
	{

		AVP avp = createObject(type);
		avp.setAvpCode(c_uiAVPCode);
		avp.applicationId = applicationId;
		return avp;
	}

	private boolean isExpressionValid(boolean x)
	{
		// TODO Auto-generated method stub
		if (!(x))
		{
			MyLoggerFactory.getInstance().getAppLogger().error("ERROR!! Assert %s failed on line %d in file %s", x);
			return false;
		}
		return true;
	}

	public int getHopByHopIdentifier()
	{
		// TODO Auto-generated method stub
		return header.hopByHopIdentifier;
	}

	public int getEndToEndIdentifier()
	{
		// TODO Auto-generated method stub
		return header.endToEndIdentifier;
	}

	public AVP getAVP(int code)
	{
		return getAvpList().searchItem(code);
	}

	public void removeAVP(int avpCode)
	{
		getDiameterMessageStatus().avpAccessed(avpCode);
		getAvpList().remove(avpCode);
	}
	// public void changeAVPCode(int oldAvpCode, int newAvpCode, int parentCode)
	// {
	// AVP found = searchAllAVPS(oldAvpCode, parentCode);
	// if (found != null)
	// {
	// AVP parent = found.getParent();
	// if (parent == null)
	// {
	// int i = getAvpList().find(oldAvpCode);
	// if ( i > -1 )
	// {
	// AVP getItem = getAvpList().GetItem(i);
	// String value = getItem.getValue();
	// getAvpList().remove(getItem);
	// addAVP(newAvpCode, getItem.getVendorID(), getItem.hasVendorID(),
	// getItem.isMandatory(), getItem.isProtected(), value);
	// }
	// }
	// else
	// {
	// if (parent instanceof DiameterAVPGrouped)
	// {
	// ((DiameterAVPGrouped) parent).remove(found);
	// ((DiameterAVPGrouped) parent).addAVP(newAvpCode,
	// found.getVendorID(), found.hasVendorID(),
	// found.isMandatory(), found.isProtected(), found.getValue());
	// }
	// }
	// }
	// }

	public void removeAVP(int avpCode, int parentCode)
	{
		getDiameterMessageStatus().avpAccessed(avpCode);
		if (parentCode == 0)
		{
			getAvpList().remove(avpCode);
			return;
		}

		AVP found = searchAllAVPS(avpCode, parentCode);
		if (found != null)
		{
			AVP parent = found.getParent();
			if (parent == null)
			{
				getAvpList().remove(avpCode);
			}
			else
			{
				if (parent instanceof DiameterAVPGrouped)
				{
					((DiameterAVPGrouped) parent).remove(found);
				}
			}
		}
	}

	public boolean hasVendorID(byte flags)
	{
		if (((byte) (flags & 0x80)) == (byte) 0x80)
			return true;
		else
			return false;
	}

	private static AVP createObject(String className)
	{
		String cName = className;
		@SuppressWarnings("rawtypes")
		Class c = null;
		Object obj = null;
		try
		{
			c = Class.forName(cName);
			obj = c.newInstance();
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
		return (AVP) obj;
	}

	public void addAVP(AVP pCreatedAVP)
	{
		AVP avp = searchAllAVPS(pCreatedAVP.getAvpCode(), pCreatedAVP.getParentAvpCode());
		if (avp != null && avp.getParent() != null && avp.getParent() instanceof DiameterAVPGrouped)
		{
			DiameterAVPGrouped grouped = (DiameterAVPGrouped) avp.getParent();
			grouped.addAVP(pCreatedAVP);
		}
		else
		{
			getAvpList().add(pCreatedAVP);
		}
	}

	AVP CreateAVP(int c_uiAVPCode)
	{
		String type = getAVPTYPE(c_uiAVPCode);
		type = appendTypePrefix(type);
		AVP avp = createObject(type);
		avp.setAvpCode(c_uiAVPCode);
		avp.setParentMsg(this);
		return avp;
	}

	public AVP addAVP(int c_uiAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption, String value)
	{
		AVP avp = CreateAVP(c_uiAVPCode);
		avp.setHeaderInfo(-1, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
		avp.set(value);
		addAVP(avp);

		return avp;
	}

	public AVP addAVP(List<Integer> parents, int c_uiAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption, String value)
	{
		AVP avp = CreateAVP(c_uiAVPCode);

		avp.setHeaderInfo(-1, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
		avp.set(value);

		if (parents == null)
		{
			addAVP(avp);
		}
		else
		{
			AVP parent = getAVPById(parents);
			parent.addAVP(avp);
		}
		return avp;
	}

	public String getOriginHost()
	{
		AVP avp = getAVP(SESSION_AVP_CODES.Origin_host);
		if (avp != null)
		{
			DiameterAVPIdentity identity = (DiameterAVPIdentity) avp;
			if (identity.getValue() == null)
			{
				identity.decode();
			}
			return identity.getValue();
		}
		return "";
	}

	public String getOriginRealm()
	{
		AVP avp = getAVP(SESSION_AVP_CODES.Origin_realm);
		if (avp != null)
		{
			DiameterAVPIdentity identity = (DiameterAVPIdentity) avp;
			if (identity.getValue() == null)
			{
				identity.decode();
			}
			return identity.getValue();
		}
		return "";
	}

	public String getSessionID()
	{
		AVP avp = getAVP(SESSION_AVP_CODES.Session_id);
		if (avp != null)
		{
			DiameterAVPUTF8String utf8_session_id = (DiameterAVPUTF8String) avp;
			if (utf8_session_id.getValue() == null)
			{
				utf8_session_id.decode();
			}
			return utf8_session_id.getValue();
		}
		return "";
	}

	public String getDestHost()
	{
		AVP avp = getAVP(SESSION_AVP_CODES.Dest_host);
		if (avp != null)
		{
			DiameterAVPIdentity identity = (DiameterAVPIdentity) avp;
			if (identity.getValue() == null)
			{
				identity.decode();
			}
			return identity.getValue();
		}
		return "";
	}

	public String getDestRealm()
	{
		AVP avp = getAVP(SESSION_AVP_CODES.Dest_realm);
		if (avp != null)
		{
			DiameterAVPIdentity identity = (DiameterAVPIdentity) avp;
			if (identity.getValue() == null)
			{
				identity.decode();
			}
			return identity.getValue();
		}
		return "";
	}

	public void setSessionId(String input)
	{
		// TODO Auto-generated method stub
		AVP avp = getAVP(SESSION_AVP_CODES.Session_id);
		if (avp != null)
		{
			avp.set(input);
		}
	}

	public void setDestinationHost(String input)
	{
		try
		{
			AVP avp = getAVP(SESSION_AVP_CODES.Dest_host);
			if (avp != null)
			{
				avp.set(input);
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void setDestinationRealm(String input)
	{
		try
		{
			// TODO Auto-generated method stub
			AVP avp = getAVP(SESSION_AVP_CODES.Dest_realm);
			if (avp != null)
			{
				avp.set(input);
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void setOriginHost(String input)
	{
		try
		{
			AVP avp = getAVP(SESSION_AVP_CODES.Origin_host);
			if (avp != null)
			{
				avp.set(input);
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void setOriginRealm(String input)
	{
		try
		{
			AVP avp = getAVP(SESSION_AVP_CODES.Origin_realm);
			if (avp != null)
			{
				avp.set(input);
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void createSessionId(String input)
	{
		if (input != null)
		{
			// TODO Auto-generated method stub
			AVP avp = getAVP(SESSION_AVP_CODES.Session_id);
			if (avp != null)
			{
				avp.set(input);
			}
			else
			{
				addAVP(SESSION_AVP_CODES.Session_id, 0, false, true, false, input);
			}
		}
	}

	public void createDestinationHost(String input)
	{
		try
		{
			if (input != null)
			{
				AVP avp = getAVP(SESSION_AVP_CODES.Dest_host);
				if (avp != null)
				{
					avp.set(input);
				}
				else
				{
					addAVP(SESSION_AVP_CODES.Dest_host, 0, false, true, false, input);
				}
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void createDestinationRealm(String input)
	{
		try
		{
			if (input != null)
			{
				// TODO Auto-generated method stub
				AVP avp = getAVP(SESSION_AVP_CODES.Dest_realm);
				if (avp != null)
				{
					avp.set(input);
				}
				else
				{
					addAVP(SESSION_AVP_CODES.Dest_realm, 0, false, true, false, input);
				}
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void createOriginHost(String input)
	{
		try
		{
			if (input != null)
			{
				AVP avp = getAVP(SESSION_AVP_CODES.Origin_host);
				if (avp != null)
				{
					avp.set(input);
				}
				else
				{
					addAVP(SESSION_AVP_CODES.Origin_host, 0, false, true, false, input);
				}
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public void createOriginRealm(String input)
	{
		try
		{
			if (input != null)
			{
				AVP avp = getAVP(SESSION_AVP_CODES.Origin_realm);
				if (avp != null)
				{
					avp.set(input);
				}
				else
				{
					addAVP(SESSION_AVP_CODES.Origin_realm, 0, false, true, false, input);
				}
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public int getAppID()
	{
		// TODO Auto-generated method stub
		return header.applicationID;
	}

	public byte[] encode()
	{
		reset();
		MyLoggerFactory.getInstance().getAppLogger().debug("#####################################################");

		for (AVP avp : getAvpList().list)
		{
			try
			{
				currentLocation = avp.encode(data, currentLocation);
			}
			catch (Exception e)
			{
				MyLoggerFactory.getInstance().getAppLogger().error(e);
			}
		}
		data = encodeHeader();
		return data;
	}

	public int getCommandCode()
	{
		// TODO Auto-generated method stub
		return header.commandCode;
	}

	public boolean isRequest()
	{
		// TODO Auto-generated method stub
		return header.isRequest();
	}

	public String getSrcIP()
	{
		return srcIP;
	}

	public void setSrcIP(String srcIP)
	{
		this.srcIP = srcIP;
	}

	public List<String> getDstIP()
	{
		return dstIP;
	}

	public void setDstIP(String _dstIP)
	{
		getDiameterMessageStatus().setRoutedToPeer(true);
		if (this.dstIP == null)
			this.dstIP = new ArrayList<String>();
		this.dstIP.add(_dstIP);
	}

	public int getEncodeLength()
	{
		return encodeLength;
	}

	public void setEncodeLength(int encodeLength)
	{
		this.encodeLength = encodeLength;
	}

	public DiameterAVPList getAvpList()
	{
		return avpList;
	}

	public void setAvpList(DiameterAVPList avpList)
	{
		this.avpList = avpList;
	}

	public void setApplicationID(int applicationId)
	{
		// TODO Auto-generated method stub
		header.applicationID = applicationId;
	}

	public void setCommandCode(int commandCode)
	{
		// TODO Auto-generated method stub
		header.commandCode = commandCode;
	}

	public void setFlags(byte flags)
	{
		// TODO Auto-generated method stub
		header.flags = flags;
	}

	public void setVersion(byte version)
	{
		// TODO Auto-generated method stub
		header.version = version;
	}

	public void setHopByHopIdentifier(int hopyByHopIdentifier)
	{
		// TODO Auto-generated method stub
		header.hopByHopIdentifier = hopyByHopIdentifier;
	}

	public void setEndToEndIdentifier(int endToEndIdentifier)
	{
		// TODO Auto-generated method stub
		header.endToEndIdentifier = endToEndIdentifier;
	}

	public int getApplicationID()
	{
		// TODO Auto-generated method stub
		return header.applicationID;
	}

	public void updateHeaders(String originHost, String originRealm, String destHost, String destRealm)
	{
		try
		{
			if (originHost != null && !originHost.equals(""))
			{
				setOriginHost(originHost);
			}
			if (originRealm != null && !originRealm.equals(""))
			{
				setOriginRealm(originRealm);
			}
			if (destHost != null && !destHost.equals(""))
			{
				setDestinationHost(destHost);
			}
			if (destRealm != null && !destRealm.equals(""))
			{
				setDestinationRealm(destRealm);
			}
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
	}

	public String getIMSI()
	{
		String imsi = "";
		AVP avp = getAVP(SESSION_AVP_CODES.IMSI);
		if (avp != null)
		{
			avp.decode();
			imsi = avp.getValue();
		}
		return imsi;
	}

	public String getResultCodeWithParentCode()
	{
		String result = "0,"+DEFAULT_RESULT_CODE;
		int parentCode = 268;

		if ( false == isRequest() )
		{
			AVP avpSuccess = getAVP(parentCode);
			if ( avpSuccess == null )
			{
				parentCode = 297;
				AVP experimentalResult = getAVP(parentCode); 
				if ( null !=  experimentalResult)
				{
					AVP experimentalResultCode = ((DiameterAVPGrouped)experimentalResult).getAVP(298);
					if ( null != experimentalResultCode )
					{
						result = parentCode+","+ experimentalResultCode.toString();
					}
				}
			}
			else
			{
				result = parentCode+","+avpSuccess.toString();
			}
		}
		
		return result;	
	}

	public List<AVP> getAvps()
	{
		// TODO Auto-generated method stub
		return avpList.list;
	}

	@Override
	public Object getProperty(String key)
	{
		return valuesMap.get(key);
	}

	@Override
	public void setProperty(String key, Object value)
	{
		valuesMap.put(key, value);
	}

	public DiameterAVPGrouped getGAVP(int code)
	{
		// TODO Auto-generated method stub
		AVP avp = getAVP(code);
		if (avp != null)
		{
			return ((DiameterAVPGrouped) avp);
		}
		return null;
	}

	public void replaceSessionId(String string, String string2)
	{
		String s = null;
		if ((s = getSessionID()).contains(string))
		{
			setSessionId(s.replace(string, string2));
		}
	}

	public List<String> getDstRoute()
	{
		// TODO Auto-generated method stub
		return dstRoute;
	}

	public void setDstRoute(String _dstRoute)
	{
		getDiameterMessageStatus().setRoutedToRouteList(true);
		if (dstRoute == null)
			dstRoute = new ArrayList<>();
		if ( dstRoute.contains(_dstRoute)==false)
			dstRoute.add(_dstRoute);
	}

	public boolean hasAnswer()
	{
		// TODO Auto-generated method stub
		return (getGeneratedAnswer() != null);
	}

	public AVP getAVPById(List<Integer> id)
	{
		int index = 0;
		AVP found = null;
		if (id != null)
		{
			int currentId = id.get(index);
			found = getAVP(currentId);
			if (found != null && id.size() > 1)
			{
				AVP a1 = recursiveFind(found, id, 1 + index);
				found = null;
				if (a1 != null)
				{
					found = a1;
				}
			}
		}
		return found;
	}

	private AVP recursiveFind(AVP found, List<Integer> id, int index)
	{
		AVP result = null;
		DiameterAVPList list = found.getChildren();
		if (list != null && index < id.size())
		{
			Integer avpCode = id.get(index);
			for (AVP e : list.list)
			{
				if (avpCode == e.getAvpCode())
				{
					if ((1 + index) >= id.size())
					{
						result = e;
						break;
					}
					else
					{
						AVP a1 = recursiveFind(e, id, 1 + index);
						if (a1 != null)
							result = a1;
					}
				}
			}
		}
		return result;
	}

	public boolean isAvpDefined(int code)
	{
		return avpMap.containsKey(code);
	}

	public static String getAVPTYPE(int c_uiAVPCode)
	{
		AVPInfo val = null;
		val = avpMap.get(c_uiAVPCode);
		return val.avpType;
	}

	AVP CreateAVP(int c_uiAVPCode, int applicationId)
	{
		String type = getAVPTYPE(c_uiAVPCode);
		type = appendTypePrefix(type);
		AVP avp = CreateAVP(type, c_uiAVPCode, applicationId);
		avp.setParentMsg(this);
		return avp;
	}

	private static String appendTypePrefix(String type)
	{
		return "com.globitel.diameterCodec.Diameter.DiameterAVP" + type;
	}

	public static String getAVPDescription(int avpCode)
	{
		AVPInfo val = null;
		// TODO Auto-generated method stub
		val = avpMap.get(avpCode);
		return val.avpName;
	}

	public static void FillAVPs(List<AVPInfo> l)
	{
		avpMap.clear();
		for (AVPInfo info : l)
		{
			avpMap.put(info.avpCode, info);

		}
		System.out.println("Done loading " + l.size() + " avp codes.");
	}

	public void setDropped()
	{
		getDiameterMessageStatus().setDropped();
		// TODO Auto-generated method stub
		dropped = true;
	}

	public boolean isDropped()
	{
		return dropped;
	}

	public DiameterMessage getGeneratedAnswer()
	{
		return generatedAnswer;
	}

	public void setGeneratedAnswer(DiameterMessage generatedAnswer)
	{
		this.generatedAnswer = generatedAnswer;
	}

	
	public DiameterMessageStatus getStatus()
	{
		// TODO Auto-generated method stub
		return getDiameterMessageStatus();
	}

	public DiameterMessageStatus getDiameterMessageStatus()
	{
		return diameterMessageStatus;
	}

	public void setDiameterMessageStatus(DiameterMessageStatus diameterMessageStatus)
	{
		this.diameterMessageStatus = diameterMessageStatus;
	}

	public void copyMsg(DiameterMessage in)
	{
		if (in == this)
		{
			return;
		}
		reset();

		if (dstIP != null)
			dstIP.clear();
		if (dstRoute != null)
			dstRoute.clear();
		if (dstRouteCluster != null)
			dstRouteCluster.clear();
		
		generatedAnswer = null;
		dropped = false;

		dstIP = null;
		dstRoute = null;
		dstRouteCluster = null;
		avpList.clear();

		this.dstIP = in.getDstIP() != null ? (new ArrayList<>(in.getDstIP())) : (null);
		this.dstRoute = in.getDstRoute() != null ? (new ArrayList<>(in.getDstRoute())) : (null);
		this.dstRouteCluster = in.getDstRouteCluster() != null ? (new ArrayList<>(in.getDstRouteCluster())) : (null);
		this.setGeneratedAnswer(in.getGeneratedAnswer());
		this.dropped = in.isDropped();
		setEncodeLength(in.getEncodeLength());
		currentLocation = in.currentLocation;
		for (AVP avp : in.getAvpList().list)
		{
			getAvpList().add(avp);
		}
		data = in.data;
		header = new Header(in.header);
		header.commandCode = in.header.commandCode;

		setSrcIP(in.getSrcIP());
		setDstIP(in.getDstIP());

		diameterMessageStatus.copy(in.diameterMessageStatus);
	}

	

	public void skipThis()
	{
		// TODO Auto-generated method stub
		getDiameterMessageStatus().setSkipThisRule(true);
	}

	public boolean isSkipThis()
	{
		return getDiameterMessageStatus().isSkipThisRule();
	}

	public void calledITS()
	{
		// TODO Auto-generated method stub
		getDiameterMessageStatus().setItsCalled(true);
	}

	public boolean isITSCalled()
	{
		return getDiameterMessageStatus().isItsCalled();
	}

	public byte[] getBytes()
	{
		// TODO Auto-generated method stub
		return data;
	}

	public void removeAVP(List<Integer> parents, int avpCode)
	{
		getDiameterMessageStatus().avpAccessed(avpCode);
		if (parents == null)
		{
			getAvpList().remove(avpCode);
		}
		else
		{
			AVP avp = getAVPById(parents);
			avp.removeAVP(avpCode);
		}
	}

	public void filtered()
	{
		getDiameterMessageStatus().filtered();
	}

	public boolean isFiltered()
	{
		return getDiameterMessageStatus().isFiltered();
	}

	public void setDstRouteCluster(String string)
	{
		getDiameterMessageStatus().setRoutedToRouteListCluster(true);
		if (dstRouteCluster == null)
			dstRouteCluster = new ArrayList<>();
		if ( dstRouteCluster.contains(string)==false)
			dstRouteCluster.add(string);
	}
	public List<String> getDstRouteCluster()
	{
		// TODO Auto-generated method stub
		return dstRouteCluster;
	}

	public DiameterMessage generateAnswerByAvp(List<AVP> asList)
	{
		// TODO Auto-generated method stub
		generatedAnswer = new DiameterMessage();
		DiameterMessage msg = this;
		DiameterMessage newMsg = getGeneratedAnswer();
		// TODO Auto-generated method stub
		for (AVP info : asList)
		{
			newMsg.addAVP(info);
		}
		newMsg.setVersion((byte) 1);
		newMsg.setCommandCode(msg.getCommandCode());
		newMsg.setApplicationID(msg.getAppID());
		newMsg.setHopByHopIdentifier(msg.getHopByHopIdentifier());
		newMsg.setEndToEndIdentifier(msg.getEndToEndIdentifier());
		newMsg.setFlags((byte) 0x40);

		newMsg.createOriginHost(msg.getDestHost());
		newMsg.createOriginRealm(msg.getDestRealm());
		newMsg.createSessionId(msg.getSessionID());
		getDiameterMessageStatus().answerCreated();
		return getGeneratedAnswer();
		
	}
	public DiameterMessage generateAnswer(List<AnswerInfo> list)
	{
		generatedAnswer = new DiameterMessage();
		DiameterMessage msg = this;
		DiameterMessage newMsg = getGeneratedAnswer();
		// TODO Auto-generated method stub
		for (AnswerInfo info : list)
		{
			String format = String.format("answer avp added: Code:%d, Vendor:%d, hasVendor:%b" + "isMandatory:%b, isProtected:%b, parentId:%d, parentAvpCode:%d, data:%s", info.avpCode, info.vendor_id,
					(info.vendor_id != 0), info.is_mandatory, info.is_protected, info.parent_id, info.parent_avp_code, info.data);
			MyLoggerFactory.getInstance().getAppLogger().debug(format);

			if (info.parent_id != 0)
			{
				AVP avp = msg.getGeneratedAnswer().getAVP(info.parent_avp_code);
				avp.addAVP(info.avpCode, info.vendor_id, info.vendor_id != 0, info.is_mandatory, info.is_protected, info.data);
			}
			else
			{
				msg.getGeneratedAnswer().addAVP(info.avpCode, info.vendor_id, info.vendor_id != 0, info.is_mandatory, info.is_protected, info.data);
			}
		}
		newMsg.setVersion((byte) 1);
		newMsg.setCommandCode(msg.getCommandCode());
		newMsg.setApplicationID(msg.getAppID());
		newMsg.setHopByHopIdentifier(msg.getHopByHopIdentifier());
		newMsg.setEndToEndIdentifier(msg.getEndToEndIdentifier());
		newMsg.setFlags((byte) 0x40);

		newMsg.createOriginHost(msg.getDestHost());
		newMsg.createOriginRealm(msg.getDestRealm());
		newMsg.createSessionId(msg.getSessionID());
		getDiameterMessageStatus().answerCreated();
		return getGeneratedAnswer();
	}

	public int getFlags()
	{
		// TODO Auto-generated method stub
		return 0xff &  header.flags;
	}

	public String getVplmnId()
	{
		// TODO Auto-generated method stub
		StringBuilder result = new StringBuilder();
		AVP avp = getAVP(1407);
		if ( avp != null )
		{
			String visitedPlmnId = avp.toString();
			for (int i = 0; i < visitedPlmnId.length(); i += 2)
			{
				result.append(visitedPlmnId.charAt(i + 1));
				result.append(visitedPlmnId.charAt(i));
			}
		}
		return result.toString();
	}

	public String getSrcRoute()
	{
		return srcRoute;
	}

	public void setSrcRoute(String srcRoute)
	{
		this.srcRoute = srcRoute;
	}

	public String getSrcCluster()
	{
		return srcCluster;
	}

	public void setSrcCluster(String srcCluster)
	{
		this.srcCluster = srcCluster;
	}

	public void setSrcAddress(String peerAddress)
	{
		// TODO Auto-generated method stub
		srcAddress = peerAddress;
	}

	public void setDstAddress(String hostAddress)
	{
		// TODO Auto-generated method stub
		dstAddress = hostAddress;
	}

}
