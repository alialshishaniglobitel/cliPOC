package com.globitel.diameterCodec.Diameter;

public class DiameterAVPInteger32 extends AVP
{
	private static final int UNSIGNED_INT_LENGTH = 4;
	public DiameterAVPInteger32()
	{
	}
	DiameterAVPInteger32(int c_iInput, int c_iAVPCode,  int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
	   setValue( c_iInput );
	}
	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	public AVP decode()
	{
	   if ( dataInfo.length <= UNSIGNED_INT_LENGTH )
	      setInt(Util.GetBigEndian(decoding_buffer, dataInfo.start, dataInfo.length ));//GetSignedBigEndian( pucValue, c_iLength );
	   is_decoded = true;
	   return this;
	}
	void setValue( int c_iInput )
	{
	   setInt(c_iInput);
	   dataInfo.length  = AVP_Lengths.INTEGER32; 
	}
	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader(data, start);
	   //setSignedLittleIndian( m_iAVPValue, pucCurrentLocation );//THIS TYPE DOESNT NEED PADDING YOU SHOULD CONSIDER THIS
	   Util.setLittleIndian(getIntValue(), AVP_Lengths.INTEGER32, data, currIndex);
	   currIndex+= AVP_Lengths.INTEGER32;
	   writeLength(data, currIndex-start);
	   return currIndex + getAVPPadding( AVP_Lengths.INTEGER32);
	}

}
