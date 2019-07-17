package com.globitel.diameterCodec.Diameter;

public class Header
{
	public byte version;
	public int messageLength;
	public byte flags;
	public int commandCode;
	public int applicationID;
	public int hopByHopIdentifier;
	public int endToEndIdentifier;

	public Header()
	{
		initialize();
	}

	public Header(Header header)
	{
		version = header.version;
		messageLength = header.messageLength;
		flags = header.flags;
		commandCode = header.commandCode;
		applicationID = header.applicationID;
		hopByHopIdentifier = header.hopByHopIdentifier;
		endToEndIdentifier = header.endToEndIdentifier;
	}

	public void initialize()
	{
		version = 0;
		messageLength = 0;
		flags = 0;
		commandCode = 0;
		applicationID = 0;
		hopByHopIdentifier = 0;
		endToEndIdentifier = 0;
	}

	public boolean isError()
	{
		if ((flags & 0x20) == 0x20)
		{
			return true;
		}
		return false;
	}

	public boolean isRequest()
	{
		if ((flags & 0x80) == 0x80)
		{
			return true;
		}
		return false;
	}

	public boolean IsProxiable()
	{
		if ((flags & 0x40) == 0x40)
		{
			return true;
		}
		return false;
	}

	public boolean IsPotentiallyRetransmitted()
	{
		if ((flags & 0x10) == 0x10)
		{
			return true;
		}
		return false;
	}

	public void clear()
	{
		// TODO Auto-generated method stub
		commandCode = 0;
		applicationID= 0;
		flags = 0;
		hopByHopIdentifier = 0;
		endToEndIdentifier = 0;
	}
}