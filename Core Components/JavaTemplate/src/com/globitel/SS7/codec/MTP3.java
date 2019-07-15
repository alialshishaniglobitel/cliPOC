package com.globitel.SS7.codec;

import java.io.IOException;

import com.globitel.common.utils.ByteArray;

public class MTP3
{

	public byte serviceInfo = (byte) 0x83;
	public int opc = 0;
	public int dpc = 0;
	public int sls = 0;

	public void setServiceInfo(byte serv_info)
	{
		serviceInfo = serv_info;
	}

	public int getOPC()
	{
		return opc;
	}

	public void setOPC(int new_opc)
	{
		opc = new_opc;
	}

	public int getDPC()
	{
		return dpc;
	}

	public void setDPC(int new_dpc)
	{
		dpc = new_dpc;
	}

	public int getSLS()
	{
		return sls;
	}

	public void setSLS(int new_sls)
	{
		sls = new_sls;
	}

	public void encode(ByteArray mpt3)
	{
		try
		{
			mpt3.write(serviceInfo);
			mpt3.write((byte) (dpc & 0xff));
			mpt3.write((byte) (((dpc & 0x3f00) >> 8) + ((opc & 0x03) << 6)));
			mpt3.write((byte) (opc >> 2));
			mpt3.write((byte) ((byte) (sls << 4) + ((opc >> 10) & 0x0f)));
			Message.logDebug("MTP3 OPC: " + opc + ", DPC: " + dpc + ", SLS: " + sls + "MTP3 Encoded");
		}
		catch (IOException e)
		{
			Message.logError("failed to encode MTP3");
		}
	}

	public void decode(Message message)
	{
		message.setDecodingIndex(message.rawDataSS7Index);
		Message.logDebug("MTP3.decode: decoding MTP3 Part: ");
		serviceInfo = message.rawData[message.getDecodingIndex()];
		//getting SLS portion and shifting it 4 bits to the right.
		message.incrementDecodingIndex(4);
		sls = (((message.rawData[message.getDecodingIndex()] & 0xf0) & 1) << 15 | ((message.rawData[message.getDecodingIndex()] & 0xf0)) >> 4);
		// shifting the most left 2 bits to the right most bits
		int shifted_opc = (((message.rawData[message.getDecodingIndex() - 2] & 0xC0) & 1) << 15)
				| ((message.rawData[message.getDecodingIndex() - 2] & 0xC0) >> 6);
		//concatenating bits together to get OPC Value
		opc = (message.rawData[message.getDecodingIndex()] & 0x0f);
		opc <<= 8;
		message.decrementDecodingIndex(1);
		opc |= message.rawData[message.getDecodingIndex()] & 0xff;
		opc <<= 2;
		opc |= shifted_opc;
		// concatenating bits together to get DPC Value
		message.decrementDecodingIndex(1);
		dpc = (message.rawData[message.getDecodingIndex()] & 0x3f);
		dpc <<= 8;
		message.decrementDecodingIndex(1);
		dpc |= (message.rawData[message.getDecodingIndex()] & 0xff);
		Message.logDebug("MTP3.decode: MTP3 Part Decoded!");
	}

	public byte getServiceInfo()
	{
		// TODO Auto-generated method stub
		return serviceInfo;
	}

}
