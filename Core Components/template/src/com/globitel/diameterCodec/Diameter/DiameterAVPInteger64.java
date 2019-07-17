package com.globitel.diameterCodec.Diameter;


public class DiameterAVPInteger64 extends AVP
{
	private static final int INT64_LENGTH = 8; 
	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	public DiameterAVPInteger64()
	{
		
	}
	public DiameterAVPInteger64(long c_llInput,int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
	   super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
	   setValue( c_llInput );
	}
	public AVP decode()
	{
	   if ( dataInfo.length <= INT64_LENGTH )
	      setLong(Util.GetSignedBigEndian64( decoding_buffer, dataInfo.start, dataInfo.length ));
	   is_decoded = true;
	   return this;
	}


	void setValue( long c_llInput )
	{
		setLong(c_llInput);
	   dataInfo.length  = AVP_Lengths.INTEGER64; 
	}
	
	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader(data, start);
	   Util.setSignedLittleIndian64( getLongValue(), data, currIndex );//THIS TYPE DOESNT NEED PADDING YOU SHOULD CONSIDER THIS
	   currIndex += AVP_Lengths.INTEGER64;
	   writeLength(data, currIndex-start);
	   return currIndex + getAVPPadding( AVP_Lengths.INTEGER64) ;
	}
}
