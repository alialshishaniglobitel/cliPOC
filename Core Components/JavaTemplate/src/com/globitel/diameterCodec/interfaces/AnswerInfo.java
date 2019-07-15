package com.globitel.diameterCodec.interfaces;

public class AnswerInfo
{
	public AnswerInfo()
	{
	}
	public AnswerInfo(String string, int _avpCode, String _data)
	{
		name = string;
		avpCode = _avpCode;
		data = _data;
	}
	public int id;
	public int answer_id;
	public String name;
	public int vendor_id;
	public boolean is_mandatory;
	public boolean is_protected;
	public String data;
	public int avpCode;
	public int parent_id;
	public int PARENT_VENDOR_ID;
	public boolean parent_mandatory;
	public boolean parent_protected;
	public int parent_avp_code;

}
