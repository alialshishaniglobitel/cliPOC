package com.globitel.diameterCodec.Diameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class AVP
{
	protected DiameterMessage parentMsg;
	
	class CC_REQUEST_TYPE
	{
		static final int INITIAL_REQUEST = 1;
		static final int UPDATE_REQUEST = 2;
		static final int TERMINATION_REQUEST = 3;
		static final int EVENT_REQUEST = 4;
	}
	class ParseInfo
	{
		int start;
		int length;
	}
	ParseInfo dataInfo = new ParseInfo();
	ParseInfo avpInfo  = new ParseInfo();
	
	public int vendorID;
	private int avpCode;
//	protected int avpType;
	protected boolean constructorAVP = false;
	public byte flags;
	private AVP next;
	public long applicationId;
	public byte[] decoding_buffer;
	public String txtValue;
	protected AVP parent;
	@Override
	public void finalize()
	{
		try
		{
			txtValue = null;
			parent = null;
			decoding_buffer = null;
			next = null;
			avpInfo = null;
			dataInfo = null;

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
	public AVP getParent()
	{
		return parent;
	}


	public void setParent(AVP parent)
	{
		this.parent = parent;
	}
	public boolean is_decoded=false;
	protected int avpLocationIndex;

	public AVP decode()
	{
		return this;
	}
	public void setParentMsg(DiameterMessage _parentMsg)
	{
		parentMsg = _parentMsg;
	}

	public int encode(byte[] src, int c_pucStartPosition)
	{
		// Should return the index to write the next AVP.....
		return 0;
	}

	void print(int offset)
	{

	}

	public AVP()
	{
		setAvpCode(0);
		avpInfo.length = 0;
		flags = 0;
		vendorID = 0;
		next = null;
		dataInfo.start = 0;
		dataInfo.length = 0;
	}
	public AVP(int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption)
	{
		setHeaderInfo(c_iVendorID, c_iVendorID, c_bNeedEncryption, c_bNeedEncryption, c_bNeedEncryption);
	}
	public void setHeaderInfo(int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption)
	{
		if ( c_iAVPCode != -1)
		setAvpCode(c_iAVPCode);
		avpInfo.length = 0;
		flags = 0x00;

		if (c_bIsVendorIDAvailable)
		{
			flags = (byte) (flags | 0x80);
		}
		if (c_bIsMandatory)
		{
			flags = (byte) (flags | 0x40);
		}
		if (c_bNeedEncryption)
		{
			flags = (byte) (flags | 0x20);
		}

		vendorID = c_iVendorID;
		next = null;
		dataInfo.start = 0;
		dataInfo.length = 0;
	}

	public void addNextAVP(AVP c_pNextAVP)
	{
		next = c_pNextAVP;
	}

	public AVP getNextAVP()
	{
		return next;
	}
	// ALI:::::PLEASE TEST THIS
	void writeLength(byte[] out, int len)
	{
		Util.setLittleIndian(len, 3, out, avpLocationIndex);
	}
	int encodeHeader(byte[] out, int c_pucStartPosition)// THIS METHOD SHOULD BE
														// CALLED WHEN YOU
														// CREATE messages not
	{
		
		// THE VALUES THAT ARE FILLED HERE ARE FILLED BY THE DECODE FUNCTION IN
		// CASE OF A MESSAGE DECODING SCENARIO, SO IN THE CASE OF ENCODING WE
		// NEED TO CALL THIS METHOD
		// loop though the values in the header of the avp and encode them in
		// the buffer specified
		// This should be easy
		// the avp code is 4 bytes long so we need to encode it in big indian
		// format
		avpInfo.length = dataInfo.length + 8;

		if (hasVendorID())
		{
			avpInfo.length = avpInfo.length + 4;
		}

		int iWriteIndex = 0;

		Util.setLittleIndian(getAvpCode(), 4, out, c_pucStartPosition + iWriteIndex);
		iWriteIndex += 4;

		Util.setLittleIndian(flags, 1, out, c_pucStartPosition + iWriteIndex);
		iWriteIndex += 1;

		// we need to sum up the length of the AVP
		avpLocationIndex = c_pucStartPosition+ iWriteIndex;
		//Util.setLittleIndian(avpInfo.length, 3, out, c_pucStartPosition + iWriteIndex);
		iWriteIndex += 3;

		if (hasVendorID())
		{
			Util.setLittleIndian(vendorID, 4, out, c_pucStartPosition + iWriteIndex);
			iWriteIndex += 4;
		}

		return (c_pucStartPosition + iWriteIndex);
	}

	public boolean hasVendorID()
	{
		if (((byte)(flags & 0x80)) == (byte)0x80)
			return true;
		else
			return false;
	}
	public boolean isMandatory()
	{
		if (((byte)(flags & 0x40)) == (byte)0x40)
			return true;
		else
			return false;
	}
	public boolean isProtected()
	{
		if (((byte)(flags & 0x20)) == (byte)0x20)
			return true;
		else
			return false;
	}
	public void markAVP(byte[] orig_buffer, int pucStart, int c_iLength)
	{
		decoding_buffer = orig_buffer;
		dataInfo.start = pucStart;
		dataInfo.length = c_iLength;
	}

	public int getAVPPadding(int iAVPLength)
	{
		int iAVPLengthToUse = avpInfo.length;
		if (iAVPLength != 0)
			iAVPLengthToUse = iAVPLength;
		int padding = 4  - ((iAVPLengthToUse % 4)==0?4:(iAVPLengthToUse % 4));
		return  padding;
	}

	public static String GetOctetString(byte[] pucValue, int start, int c_iLength)
	{
		// TODO Auto-generated method stub
		int end = start + c_iLength;
		String result = "";
		while( start < end)
		{
			result += String.format("%02x", pucValue[start++]);
		}
		return result;
	}

	public String getHeader(String header)
	{
		int i = avpInfo.start+ avpInfo.length+ getAVPPadding(0);
		return header +getAVPCodeDescription()+
		String.format("(%05d),Loc:%d,Len:%d,Padd:%d=(((%d)))",getAvpCode(), avpInfo.start, avpInfo.length, getAVPPadding(0), i);
	}
	public String getAVPCodeDescription()
	{
		return DiameterMessage.getAVPDescription(getAvpCode());
	}

	public String toString(String header)
	{
		return header;
	}

	public void setValueFromBuffer(byte[] data, int offset, int len)
	{
		// TODO Auto-generated method stub
		txtValue = "";
		for( int i = offset; i < offset+len; i++)
		{
			txtValue+= String.format("%02x", data[i]);
		}
	}
	public void set(String input)
	{
		parentMsg.getDiameterMessageStatus().avpAccessed(getAvpCode());
		// TODO Auto-generated method stub
		if ( input != null )
		{
			txtValue = null;
			txtValue = new String(input);
		}
	}

	public String getValue()
	{
		// TODO Auto-generated method stub
		return txtValue;
	}
	protected int getIntValue()
	{
		// TODO Auto-generated method stub
		return Integer.parseInt(getValue());
	}
	protected void setLong(long longNumber)
	{
		// TODO Auto-generated method stub
		txtValue = Long.toString(longNumber);
	}
	protected long getLongValue()
	{
		// TODO Auto-generated method stub
		return Long.parseLong(txtValue);
	}
	protected void setDate(long input)
	{
		Date date = new Date();
		long tBufferValue       = input;
		date.setTime(tBufferValue);
		date.setYear(date.getYear()-70);
	}
	protected void setDate(Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		txtValue = dateFormat.format(date);
	}
	protected Date getDateValue()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = 		null;
		try
		{
			date = sdf.parse(txtValue);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error(String.format("Parse Exception: %s", e.getMessage()), e);
		}
		return date;
	}
	public void setString(String value)
	{
		// TODO Auto-generated method stub
		txtValue = value;
	}
	protected void setInt(int input)
	{
		// TODO Auto-generated method stub
		txtValue = Integer.toString(input);
	}

	public int getParentAvpCode()
	{
		// TODO Auto-generated method stub
		return parent==null?0:parent.getAvpCode();
	}


	public String getParentAvpType()
	{
		// TODO Auto-generated method stub
		return parent==null?"":getType(parent);
	}

	public String getAvpType()
	{
		// TODO Auto-generated method stub
		return getType(this);
	}

	private String getType(AVP avp)
	{
		return avp.getClass().getName().replace("Diameter.DiameterAVP", "");
	}
	@Override
	public String toString()
	{
		return txtValue;
	}


	public int decodedHeader(byte[] data, int loc )
	{
		// TODO Auto-generated method stub
		avpInfo.start = loc;//added for testing integrity of encoded avp. in encodeNewMsg
		loc += 4; // JUMP TO FLAGS OF AVP
		flags = data[loc];
		loc++; // JUMP TO LENGTH
		avpInfo.length = Util.GetBigEndian(data, loc, 3); // this
																					// msg.
		loc += 3; // JUMP TO NEXT TAG : vendor id or
									// buffer

		if (hasVendorID())
		{
			vendorID = Util.GetBigEndian(data, loc, 4);
			loc += 4;
		}
		return loc;
	}


	public DiameterAVPList getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void removeAVP(int i)
	{
		// TODO Auto-generated method stub
	}
	
	public AVP addAVP(int c_uiAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption, String value)
	{
		return null;
	}
	
	public void removeVendorId()
	{
		// TODO Auto-generated method stub
		vendorID = 0;
		flags &= 0x40;
	}


	public void addVendorId(int _vendorId)
	{
		// TODO Auto-generated method stub
		vendorID = _vendorId;
		flags |= 0x80;
	}


	public int getVendorID()
	{
		// TODO Auto-generated method stub
		return vendorID;
	}


	public int getAvpCode()
	{
		return avpCode;
	}


	public void setAvpCode(int avpCode)
	{
		this.avpCode = avpCode;
	}


	public void addAVP(AVP avp)
	{
		// TODO Auto-generated method stub
		
	}
}
