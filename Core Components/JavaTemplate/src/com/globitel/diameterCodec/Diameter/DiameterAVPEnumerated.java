package com.globitel.diameterCodec.Diameter;

public class DiameterAVPEnumerated extends AVP
{
	private static final int INTEGER_SIZE = 4;
	//private int m_Value;

	public String toString(String header)
	{
		return getHeader(header)+ ", Value:"+getValue();
	}
	public DiameterAVPEnumerated()
	{
	}
	public DiameterAVPEnumerated(int c_iInput,int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
	   setValue( c_iInput );
	}
	public AVP decode()
	{
	   if ( dataInfo.length <= INTEGER_SIZE )
		   setInt(Util.GetBigEndian( decoding_buffer, dataInfo.start, dataInfo.length ));
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
	   Util.setSignedLittleIndian( getIntValue(), data, currIndex );//THIS TYPE DOESNT NEED PADDING YOU SHOULD CONSIDER THIS
	   currIndex += AVP_Lengths.INTEGER32;
		writeLength( data, currIndex - start );
		return currIndex + getAVPPadding(AVP_Lengths.INTEGER32);
	}
}
