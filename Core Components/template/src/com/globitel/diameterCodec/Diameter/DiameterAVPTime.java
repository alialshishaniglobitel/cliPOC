package com.globitel.diameterCodec.Diameter;

import java.util.Date;

public class DiameterAVPTime extends AVP
{
	private static final int TIME64_LENGTH = 8;
	private boolean m_bTmObjectCreatedDynmaically;
	//private Date date;
	//private Date m_Value;
	public DiameterAVPTime()
	{//do nothing
	   m_bTmObjectCreatedDynmaically = true;
	}

	public DiameterAVPTime( Date c_pTime, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
	   m_bTmObjectCreatedDynmaically = false;
	   setValue(c_pTime);//the 
	}
	
	public String toString(String header)
	{
		return getHeader(header)+", Value:"+getValue();
	}
	
	public AVP decode()
	{
		boolean success = false;
	   if ( dataInfo.length < TIME64_LENGTH )
	   {
		   setDate(Util.GetSignedBigEndian64( decoding_buffer, dataInfo.start, dataInfo.length));

		   success= true;
	   }
	   is_decoded = true;
	   return this;
	}


	void setValue( Date c_pTime )
	{
	   setDate(c_pTime);
	   dataInfo.length  = AVP_Lengths.TIME; // This is always four
	}

	public int encode(byte[] data, int start)
	{
	   int currIndex = encodeHeader(data, start);
	   /* We Should Convert a tm object to a time_t object while considering the 
	   the difference between the representations, the time_t can hold from 1970, 
	   while the tm can hold from the 1900.
	   so we need to increment 70 years to the tm object for the purpose of encoding 
	   the right time_t value.
	   */
//	   tm tNewTm;
//	   memcpy( &tNewTm, m_pTime, sizeof(tm));//copy the original value so that we don't miss up 
//	   tNewTm.tm_year += 70;
//	   
	   Date t = new Date(getDateValue().getTime());
	   t.setYear(t.getYear()+70);
	   long write = t.getTime();

	   //time_t tBufferStorage = mktime( &tNewTm );
	   //now just consider the byte representation of the generated vaue
	   Util.setLittleIndian((int) write, 4, data, currIndex);
	   currIndex += AVP_Lengths.TIME;
	   writeLength(data, currIndex-start);
	   return currIndex + getAVPPadding( AVP_Lengths.TIME);
	}



}
