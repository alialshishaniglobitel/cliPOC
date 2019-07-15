package com.globitel.diameterCodec.Diameter;

public class AVPInfo
{
	public int avpCode;
	public String avpName;
	public String avpType;
	public int appId;
	public int useVendorId;
	public int vendorId;
	public int isMandatory;
	public int needsEncryption;
	public int id;
	public int avpId;
	public String avpValue;
	@Override
	public String toString()
	{
		return avpCode+","+avpName+","+avpType+","+appId;
	}
	public AVPInfo(int _id, int _avpCode, String _avpName, String _avpType, String abnf)
	{
		id = _id;
		avpCode = _avpCode;
		avpName = _avpName;
		avpType =_avpType; 
	}
	public AVPInfo(int _id, int _avpCode, String _avpName, String _avpType, int _appid)
	{
		id = _id;
		avpCode = _avpCode;
		avpName = _avpName;
		avpType =_avpType; 
		appId = _appid;
	}
	public AVPInfo()
	{
		
	}
}
