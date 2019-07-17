package com.globitel.diameterCodec.Diameter;

public class DiameterAVPUnsigned64 extends AVP
{
	private static final int UNSIGNED64_INT_LENGTH = 8;
	public DiameterAVPUnsigned64(long c_ullInput,int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption );
	   setValue( c_ullInput );
	}
	public DiameterAVPUnsigned64()
	{
		
	}

	public String toString(String header)
	{
		return getHeader(header)+", Value:"+getValue();
	}
	public AVP decode()
	{
	   if ( dataInfo.length <= UNSIGNED64_INT_LENGTH )
		   setLong(Util.GetSignedBigEndian64( decoding_buffer, dataInfo.start, dataInfo.length ));//getbigendian64
	   is_decoded = true;
	   return this;
	}
	void setValue( long c_ullInput )
	{
	   setLong(c_ullInput);
	   dataInfo.length = AVP_Lengths.UNSIGNED64;
	}
	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader(data, start);
	   Util.setLittleIndian64( getLongValue(), 8, data, currIndex );//THIS TYPE DOESNT NEED PADDING YOU SHOULD CONSIDER THIS when dealing with other types
	   currIndex += AVP_Lengths.UNSIGNED64;
	   writeLength(data, currIndex-start);
	   return currIndex+getAVPPadding( AVP_Lengths.UNSIGNED64);
	}

}
