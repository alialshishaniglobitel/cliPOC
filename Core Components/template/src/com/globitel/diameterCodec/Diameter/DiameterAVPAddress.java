package com.globitel.diameterCodec.Diameter;

public class DiameterAVPAddress extends AVP
{
	final int VALUE_ARRAY_LENGTH = 1000;
	final int ADDRESS_SIZE = 50;
	private int m_usAddressType;

	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	class IPAddressType
	{
		static final int IPv4 = 1;
		static final int IPv6 = 2;
	}

	public DiameterAVPAddress()
	{
	}

	public DiameterAVPAddress(String c_pcValue, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption)
	{
		setValue(c_pcValue);
	}

	int getBufferStart()
	{
		return 2;
	}

	public AVP decode()
	{
		m_usAddressType = (short) Util.GetBigEndian(decoding_buffer, dataInfo.start, 2);
		String acCharToUse = ".";

		if (m_usAddressType == IPAddressType.IPv6)
			acCharToUse = ":";
		String value =  "";
		for (int i = 2; i < dataInfo.length; i++)
		{
			value += String.format((i==2?"%d":".%d"), decoding_buffer[dataInfo.start + i] & 0xff); 
		}
		setString(value);
		is_decoded = true;
		return this;
	}


	void setValue(String c_pcValue)
	{
		setString("");
		int c_iLength = c_pcValue.length();
		int iActualBufferLength = 0;
		boolean bIsIPv6 = false;

		if (c_pcValue.length() < ADDRESS_SIZE)
			setString(c_pcValue);

		dataInfo.length = c_iLength;// should equal the number of dots +1 + 2

		for (int iCharLoopIndex = 0; iCharLoopIndex < c_iLength; iCharLoopIndex++)
		{
			String part = getValue().substring(iCharLoopIndex, 1);
			if (part.compareTo(".") == 0)
			{
				iActualBufferLength++;
			}
			// if ( strncmp((const char*)(m_acAddress+iCharLoopIndex),".", 1 )
			// == 0 )
			// iActualBufferLength++;

			if (part.compareTo(":") == 0)
				bIsIPv6 = true;
		}

		dataInfo.length = iActualBufferLength + 3;// length of address type
													// plus extra 1

		if (bIsIPv6)
		{
			m_usAddressType = IPAddressType.IPv6;
		}
		else
		{
			m_usAddressType = IPAddressType.IPv4;
		}
	}

	public int encode(byte[] data, int start)
	{
		int currIndex = encodeHeader(data, start);
		int dataStart = currIndex;
		Util.setLittleIndian16((short) (m_usAddressType==0?1:m_usAddressType), 2, data, currIndex);
		currIndex += 2;// jump to actual data
		String v = getValue();
		String[] parts = v.split("\\.");
		for (int i = 0; parts != null && i < parts.length; i++)
		{
			data[currIndex++] = (byte) Integer.parseInt(parts[i]);
		}
		writeLength( data, currIndex - start);
		return (currIndex + getAVPPadding( currIndex- dataStart));
	}

	void print(int offset)
	{
		// char logStr[512] = {0};
		// FormatStringForPrinting( logStr, offset );
		// strcat( logStr, "[%0.4i] [%0.4i] [%s]" );
		//
		// LogInfo( NULL, logStr, m_iAVPCode, m_iAVPLength, m_acAddress );
	}
}
