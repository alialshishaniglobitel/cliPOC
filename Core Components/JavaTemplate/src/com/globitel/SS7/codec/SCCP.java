package com.globitel.SS7.codec;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.message.ParseException;

import com.globitel.common.utils.ByteArray;
import com.globitel.common.utils.Common;

public class SCCP
{

	public byte messageType;
	public byte messageHandling;
	public byte hopCounter;
	private byte[] cld;
	private byte[] clg;
	private SccpAddressImpl CldAddress;
	private SccpAddressImpl ClgAddress;
	
	public int cldIndex;
	public int clgIndex;

	public SccpAddressImpl getCalledAddress()
	{
		return CldAddress;
	}

	public void setCalledAddress(SccpAddressImpl calledAddress)
	{
		this.CldAddress = calledAddress;
	}

	public SccpAddressImpl getCallingAddress()
	{
		return ClgAddress;
	}

	public void setCallingAddress(SccpAddressImpl callingAddress)
	{
		this.ClgAddress = callingAddress;
	}

	public void decode(byte[] called, byte[] calling)//Message message)
	{
		Message.logDebug("SCCP.decode: decoding SCCP part: ");

		cldIndex = 0;
		byte _cldLen = (byte) called.length;
		cld = called;
		CldAddress = new SccpAddressImpl();
		try
		{
			CldAddress.decode(new ByteArrayInputStream(cld), new ParameterFactoryImpl(), SccpProtocolVersion.ITU);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			Message.logError(cld, "Failed To Decode Called Address");
		}

		Message.logBuffer(cld, "SCCP Called Party Address");
	
 
		clgIndex = 0;
		byte _clgLen = (byte) calling.length;
		clg = calling;

		ClgAddress = new SccpAddressImpl();
		try
		{
			ClgAddress.decode(new ByteArrayInputStream(clg), new ParameterFactoryImpl(), SccpProtocolVersion.ITU);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			Message.logError(clg, "Failed To Decode Calling Address");
		}

		Message.logBuffer(clg, "SCCP Calling Party Address");
		Message.logDebug("SCCP.decode: SCCP Part Decoded");
	}
	
	
	
	public void decode(Message message)
	{
		Message.logDebug("SCCP.decode: decoding SCCP part: ");
		message.rawDecodingIndex += 5;
		messageType = message.rawData[message.rawDecodingIndex];
		Message.logDebug("Message type: " + messageType);
		if (messageType == Message.UnitDataService)
		{
			Message.logDebug("Message Type UNIT DATA SERVICE");
		}
		messageHandling = message.rawData[message.rawDecodingIndex + 1];
		decodeSCCP(message);
		Message.logDebug("SCCP.decode: SCCP Part Decoded");
	}

	public void decodeSCCP(Message message)
	{
		message.rawDecodingIndex += 2;
		if (messageType == Message.ExtendedUnitData)
		{
			hopCounter = message.rawData[message.rawDecodingIndex];
			message.rawDecodingIndex += 1;
		}
		byte pointerToFirstParam = message.rawData[message.rawDecodingIndex];
		cldIndex = (message.rawDecodingIndex + (pointerToFirstParam&0xff));
		byte _cldLen = message.rawData[cldIndex];
		cld = Common.getSubByteArray(message.rawData, cldIndex + 1, cldIndex + 1
				+ _cldLen);
		CldAddress = new SccpAddressImpl();
		try
		{
			CldAddress.decode(new ByteArrayInputStream(cld), new ParameterFactoryImpl(), SccpProtocolVersion.ITU);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			Message.logError(cld, "Failed To Decode Called Address");
		}

		Message.logBuffer(cld, "SCCP Called Party Address");
		message.rawDecodingIndex += 1;
		byte pointerToSecondParam = message.rawData[message.rawDecodingIndex];
		clgIndex = (message.rawDecodingIndex + (pointerToSecondParam&0xff));
		byte _clgLen = message.rawData[clgIndex];
		clg = Common.getSubByteArray(message.rawData, clgIndex + 1, clgIndex + 1
				+ _clgLen);

		ClgAddress = new SccpAddressImpl();
		try
		{
			ClgAddress.decode(new ByteArrayInputStream(clg), new ParameterFactoryImpl(), SccpProtocolVersion.ITU);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			Message.logError(clg, "Failed To Decode Calling Address");
		}

		Message.logBuffer(clg, "SCCP Calling Party Address");
		message.rawDecodingIndex += 1;
		byte pointerTothirdParam = message.rawData[message.rawDecodingIndex];
		message.rawDecodingIndex = message.rawDecodingIndex +(pointerTothirdParam&0xff) + 1;
	}

	public void encode(ByteArray sccp) throws Exception
	{
		Message.logDebug("SCCP.encode: Encoding SCCP");
		byte pointerToFirstParameter = 0x03;
		byte pointerToSecondParameter;
		byte pointerToThirdParameter;
		byte pointerToOptionalParameter = 0x00;
		try
		{
			sccp.write(messageType);
			sccp.write(messageHandling);

			if (this.messageType == Message.ExtendedUnitData)
			{
				pointerToFirstParameter = 0x04;
				sccp.write(hopCounter);
			}

			try
			{
				cld = CldAddress.encode(false, SccpProtocolVersion.ITU);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				Message.logError("Failed To Encode Called Address: " + CldAddress.getGlobalTitle().getDigits());
				throw new Exception();
			}

			try
			{
				clg = ClgAddress.encode(false, SccpProtocolVersion.ITU);
			}
			catch(ParseException e)
			{
				Message.logError("Failed To Encode Calling Address: " + ClgAddress.getGlobalTitle().getDigits());
				throw new Exception();
			}
			pointerToSecondParameter = (byte) (pointerToFirstParameter + this.cld.length);
			pointerToThirdParameter = (byte) (pointerToSecondParameter + this.clg.length);

			sccp.write(pointerToFirstParameter);
			sccp.write(pointerToSecondParameter);
			sccp.write(pointerToThirdParameter);

			if (this.messageType == Message.ExtendedUnitData)
			{
				sccp.write(pointerToOptionalParameter);
			}

			sccp.write((byte) cld.length);
			sccp.write(cld);
			sccp.write((byte) clg.length);
			sccp.write(clg);

			Message.logBuffer(cld, "SCCP Called Party Address");
			Message.logBuffer(clg, "SCCP Calling Party Address");
		}
		catch (IOException e)
		{
			Message.logError("Exception, SCCP.encode " + e.getMessage());
		}
		Message.logDebug("SCCP encoded");
	}

	public byte getMessageHandling()
	{
		return messageHandling;
	}

	public void setMessageHandling(byte messageHandling)
	{
		this.messageHandling = messageHandling;
	}

	public byte getMessageType()
	{
		return messageType;
	}

	public void setMessageType(byte messageType)
	{
		this.messageType = messageType;
	}
}
