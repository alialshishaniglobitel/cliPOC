package com.globitel.diameterCodec.Diameter;


public class DiameterAVPIdentity extends AVP
{
	static int VALUE_ARRAY_LENGTH = 1000;
	public DiameterAVPIdentity(String c_pcValue, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption)
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
		setValue(c_pcValue);
	}
	public DiameterAVPIdentity()
	{
		
	}

	public String toString(String header)
	{
		return header +getAVPCodeDescription()+
				String.format("(%05d)",getAvpCode()) + ", Value:" + getValue();
	}

	public AVP decode()
	{
		if (dataInfo.length < VALUE_ARRAY_LENGTH && decoding_buffer != null)
			setString(new String(decoding_buffer, dataInfo.start, dataInfo.length));
		// This part here is responsible of encoding and decoding the ascii
		// values
		is_decoded = true;
		return this;
	}

	void setValue(String c_pcValue)
	{
		setString(c_pcValue);
		dataInfo.length = c_pcValue.length();
	}

	public int encode(byte[] data, int start)
	{
		int currIndex = encodeHeader(data, start);
		byte[] bytes  = getValue().getBytes();
		System.arraycopy(bytes, 0, data, currIndex, bytes.length);
		dataInfo.length = bytes.length;//new
		currIndex += dataInfo.length;
		writeLength(data, currIndex-start);
		return (currIndex + getAVPPadding( dataInfo.length));
	}
}
