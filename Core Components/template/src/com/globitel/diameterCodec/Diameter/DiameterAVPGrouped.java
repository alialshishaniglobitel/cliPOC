package com.globitel.diameterCodec.Diameter;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class DiameterAVPGrouped extends AVP
{
	private DiameterAVPList children = null;
	
	public AVP getAVP(int code)
	{
		return children.searchItem(code);
	}
	public DiameterAVPGrouped getGAVP(int code)
	{
		return ((DiameterAVPGrouped)getAVP(code));
	}
	@Override
	public void addAVP(AVP avp)
	{
		children.add(avp);
	}
	@Override
	public AVP addAVP(int c_uiAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption, String value)
	{
		AVP avp = parentMsg.CreateAVP(c_uiAVPCode);
		avp.setHeaderInfo(-1, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption);
		avp.set(value);
		addAVP(avp);
		return avp;
	}
	
	public String toString(String header)
	{
		String result = (getHeader(header));
		for(int i =0;i<children.size();i++)
		{
			AVP avp = children.GetItem(i);
			result+= avp.toString(header+"|||")+"\n";
		}
		return result;
	}
	
	private void LogError(Object object, String string, int iAVPDataLength, int iRemainingBytes)
	{
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().error(String.format(string,iAVPDataLength, iRemainingBytes));
	}
	
	public AVP decode()
	{
		if (dataInfo.length > 0 )
			children.clear();
		
		int pucCurrentLocation = dataInfo.start;
		
		if ( constructorAVP)
		{
			while( pucCurrentLocation < (dataInfo.start + dataInfo.length))
			{
				int _avpStart 		= pucCurrentLocation;
				int iCommandCode    = Util.GetBigEndian( decoding_buffer, pucCurrentLocation, 4 );
				pucCurrentLocation += 5;
				int iAVPLength      = Util.GetBigEndian( decoding_buffer, pucCurrentLocation, 3 );
				int _iavpPadding	= getAVPPadding( iAVPLength );
				pucCurrentLocation  = _avpStart +iAVPLength +_iavpPadding;
				
				AVP cc = createAVP(_avpStart, iAVPLength);
				cc.avpInfo.start = _avpStart;
				cc.decoding_buffer = decoding_buffer;
				cc.decode();
				children.add(cc);
			}
		}
		is_decoded = true;
		return this;
	}
	
	private AVP createAVP(int start, int length)
	{
		int pucCurrentLocation = start;
		int c_iAVPCode  = Util.GetBigEndian( decoding_buffer, pucCurrentLocation, 4 );
		AVP pCreatedAVP = parentMsg.CreateAVP(c_iAVPCode, (int) applicationId);
		pCreatedAVP.setAvpCode(c_iAVPCode);
			
		pucCurrentLocation       += 4;                     // JUMP TO FLAGS OF AVP
		pCreatedAVP.flags    = decoding_buffer[pucCurrentLocation];
		pucCurrentLocation++;                              // JUMP TO LENGTH
		pCreatedAVP.avpInfo.length = Util.GetBigEndian( decoding_buffer, pucCurrentLocation, 3 );
		pucCurrentLocation+=3;     
		// JUMP TO NEXT TAG : vendor id or buffer

		if (pCreatedAVP.hasVendorID())
		{
			pCreatedAVP.vendorID = Util.GetBigEndian( decoding_buffer, pucCurrentLocation, 4 );
			pucCurrentLocation+=4;                          //NOT TESTED
		}

		// THIS IS MANDATORY:: MULTIPLE AVPS HERE NOT SINGLE, SO I THINK LENGTH EVALUATION IS WRONG, THE FUNCTION SHOULD BE RECURSIVE
		// if this pCreatedAVP is Grouped Then here we should call the function for each avp.
		// if the pointer passed to us is empty decode normally. else use special functions to evaluate the lengths and decide how many times the decode function is called.
		int iAVPDataLength = pCreatedAVP.avpInfo.length - (pucCurrentLocation - start);
		// This length might be the length of one avp or the length of multiple avps. decide no

		int iRemainingBytes  = ((start+length)-pucCurrentLocation);
		//check the length we are trying to assign
		if ((iAVPDataLength < 0) || ( iAVPDataLength > iRemainingBytes ))
		{
			LogError( null, "DiameterAVPGrouped::decodeGroupedAVP single avp, AVP Length %i is invalid it should be %i", iAVPDataLength, iRemainingBytes );
		}
		else//   if valid
		{
			pCreatedAVP.markAVP( decoding_buffer, pucCurrentLocation, iAVPDataLength );
			pCreatedAVP.applicationId = applicationId;
			pucCurrentLocation += iAVPDataLength;
			pucCurrentLocation += pCreatedAVP.getAVPPadding(0);
		}
		return pCreatedAVP;
	}

	void setValue( AVP c_apValue[], int c_iNumberOfValues )
	{
		// store this array of components in the avp list
		int iAVPLoopIndex = 0;
		while( iAVPLoopIndex < c_iNumberOfValues )
		{
			//add this component to the avp list
			children.add( c_apValue[ iAVPLoopIndex ] );
			iAVPLoopIndex++;
		}
	}

	public DiameterAVPGrouped()
	{
		// do nothing
		children = new DiameterAVPList();
		InitializeClassType();
	}
	public DiameterAVPGrouped(AVP c_apValue[], int c_iNumberOfValues, int c_iAVPCode, int c_iVendorID, boolean c_bIsVendorIDAvailable, boolean c_bIsMandatory, boolean c_bNeedEncryption )
	{
		super(c_iAVPCode, c_iVendorID, c_bIsVendorIDAvailable, c_bIsMandatory, c_bNeedEncryption );
		children = new DiameterAVPList();
		// This constructor is special, it takes an array of avp pointers
		setValue( c_apValue, c_iNumberOfValues );//array of AVPs
		InitializeClassType();
	}
	@Override
	public void finalize()
	{
		try{
			children = null;	
		}
		finally{
			super.finalize();
		}
		
	}
	public void InitializeClassType()
	{
		constructorAVP = true;
	}
	
	DiameterAVPList getListValue()
	{
		return children;
	}
	public int encode(byte[] data, int start)
	{
		dataInfo.length = 0;
		int loc = 0;
		if ( hasVendorID() )
		{
			loc = start + 12;
		}
		else
		{
			loc = start + 8;
		}
		// if grouped
		for( int iAVPListLoopIndex = 0; iAVPListLoopIndex < children.getNumberOfItems(); iAVPListLoopIndex++ )
		{
			AVP pCurrentAVP   = children.GetItem( iAVPListLoopIndex );
			int beforeIndex = loc;
			loc = pCurrentAVP.encode(data, loc);
			dataInfo.length += (loc - beforeIndex) ;//pCurrentAVP->m_iAVPLength;
		}
		int dataStart = encodeHeader(data, start);
		writeLength(data, loc-start);
		int iPaddingValue = getAVPPadding(loc - dataStart);
		return (loc + iPaddingValue);
	}

	public AVP find(int c_uiAVPCommandCode)
	{
		// this method should be consdiered again, because we have many avps with the same code.
		return children.searchItem(c_uiAVPCommandCode );
	}
	@Override
	public void removeAVP(int code)
	{
		// this method should be consdiered again, because we have many avps with the same code.
		children.remove(code);
	}
	public void remove(AVP found)
	{
		// this method should be consdiered again, because we have many avps with the same code.
		children.remove(found);
	}
	@Override
	public DiameterAVPList getChildren()
	{
		// TODO Auto-generated method stub
		return children;
	}
}
