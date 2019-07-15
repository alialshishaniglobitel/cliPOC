package com.globitel.diameterCodec.Diameter;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class DiameterAVPUTF8String extends AVP
{
	private static final int VALUE_ARRAY_LENGTH = 1000;
	public DiameterAVPUTF8String(String c_pcValue, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption );
	   setValue(new String(c_pcValue));
	}
	public DiameterAVPUTF8String()
	{
		
	}

	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	
	public AVP decode()
	{
	   if ( dataInfo.length > 0 && dataInfo.length < VALUE_ARRAY_LENGTH )
		   setString(new String(decoding_buffer,dataInfo.start, dataInfo.length));
	   is_decoded = true;
	   return this;
	}
	public void setValue( String c_pcValue )
	{
		setString(c_pcValue);
	}
	public int encode(byte[] data, int start)
	{
		int currIndex = 0;
		try{
	   currIndex = encodeHeader( data, start );//give your data length to the function and it will evaluate the total length
	   byte[] bytes = getValue().getBytes();
	   System.arraycopy(bytes, 0, data, currIndex, bytes.length);
	   dataInfo.length = bytes.length;
	   currIndex += dataInfo.length;
	   writeLength(data, currIndex-start);
	   /// convert the string supplied by the user to buffer
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error("encode DiameterAVPUTF8String, " + e);
		}

	   return (currIndex + getAVPPadding(dataInfo.length));
	}

}
