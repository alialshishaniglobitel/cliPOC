package com.globitel.diameterCodec.Diameter;

public class DiameterAVPUnsigned32 extends AVP
{
	private static final int UNSIGNED_INT_SIZE = 4;
	public DiameterAVPUnsigned32()
	{
		
	}
	public DiameterAVPUnsigned32(int c_uiInput,int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
	   setValue( c_uiInput );
	}
	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	public AVP decode()
	{
	   if ( dataInfo.length <= UNSIGNED_INT_SIZE )
	      setInt(Util.GetBigEndian( decoding_buffer, dataInfo.start, dataInfo.length));
	   is_decoded = true;
	   return this;
	}
	void setValue( int c_uiInput )
	{
	   setInt(c_uiInput);
	   dataInfo.length  = AVP_Lengths.UNSIGNED32; 
	}
	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader(data, start);
	   Util.setLittleIndian( getIntValue(), 4, data, currIndex );//THIS TYPE DOESNT NEED PADDING YOU SHOULD CONSIDER THIS
	   currIndex += AVP_Lengths.UNSIGNED32;
	   writeLength(data, currIndex-start);
	   return currIndex+ getAVPPadding( AVP_Lengths.UNSIGNED32);
	}

}
