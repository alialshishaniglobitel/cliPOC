package com.globitel.m3ua.commons;

import com.globitel.m3ua.commons.Definitions.MTP3Data;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Utility {

	public static MessageLogger logger =  MyLoggerFactory.getDefaultLogger();
	
	public static String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
	
	public static byte[] fromHex(String hex)
	{
		byte[] bytes = new byte[hex.length() / 2];
		for(int i = 0; i<bytes.length ;i++)
		{
			bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}  
	
	public static MTP3Data decodeMTP3(byte[] mtp3)
	{
		logger.debug("DialogicReader::decodeMTP3, " + Utility.getHexString(mtp3));
		int index = 0;
		byte serviceInfo = mtp3[index];
		int ni = serviceInfo & 0xC0;
		ni >>= 6;
		int si = serviceInfo & 0x0f;
		// getting SLS portion and shifting it 4 bits to the right.
		index += 4;
		int sls = (((mtp3[index] & 0xf0) & 1) << 15 | ((mtp3[index] & 0xf0)) >> 4);
		// shifting the most left 2 bits to the right most bits
		int shifted_opc = (((mtp3[index - 2] & 0xC0) & 1) << 15)
				| ((mtp3[index - 2] & 0xC0) >> 6);
		// concatenating bits together to get OPC Value
		int opc = (mtp3[index] & 0x0f);
		opc <<= 8;
		index -= 1;
		opc |= mtp3[index] & 0xff;
		opc <<= 2;
		opc |= shifted_opc;
		// concatenating bits together to get DPC Value
		index -= 1;
		int dpc = (mtp3[index] & 0x3f);
		dpc <<= 8;
		index -= 1;
		dpc |= (mtp3[index] & 0xff);
		logger.debug("DialogicReader::decodeMTP3. MTP3 Part Decoded: OPC: " + opc + ", DPC: "
						+ dpc + ", SLS: " + sls);
		MTP3Data mtp3Data = new MTP3Data(serviceInfo, ni, si, sls, opc, dpc);
		return mtp3Data;
	}
}
