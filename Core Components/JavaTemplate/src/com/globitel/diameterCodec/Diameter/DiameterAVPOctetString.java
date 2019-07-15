package com.globitel.diameterCodec.Diameter;


public class DiameterAVPOctetString extends AVP
{
	private static final int VALUE_ARRAY_LENGTH = 1000;
	public DiameterAVPOctetString(String c_pcValue, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
	   super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption );
	   setValue(c_pcValue);
	}
	public DiameterAVPOctetString()
	{
		
	}
	
	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}

	public AVP decode()
	{
	   if ( dataInfo.length < VALUE_ARRAY_LENGTH )
		   setString(AVP.GetOctetString(decoding_buffer, dataInfo.start, dataInfo.length));
	   // This part here is responsible of incoding and decoding the ascii values
	   is_decoded = true;
	   return this;
	}
	void setValue( String c_pcValue )
	{
	   setString(c_pcValue);
	   dataInfo.length  = c_pcValue.length();
	}
	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader( data, start );//give your data length to the function and it will evaluate the total length
	   
	   //byte[] bytes = m_Value.getBytes();
	   //System.arraycopy(bytes, 0, data, pucCurrentLocation, bytes.length);
	   byte[] bytes2 = Util.TakeStringDigitsGetBytes(0,getValue());
	   System.arraycopy(bytes2, 0, data, currIndex, bytes2.length);
	   /// convert the string supplied by the user to buffer
	   dataInfo.length = bytes2.length;
	   currIndex+= dataInfo.length;
	   writeLength(data, currIndex-start);
	   return (currIndex + getAVPPadding( dataInfo.length));
	   
	}
	
}
