package com.globitel.diameterCodec.actions;

public class UpdateServingHSS implements InvokeInterface
{
	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String imsi = objToStr(args[0]);//"getSessionIMSI(msg.getSessionID())";
		String componentType = objToStr(args[1]);//"\"hss\"";
		String association = objToStr(args[2]);//"msg.getSrcIP()";
		String originHost = objToStr(args[3]);//"getSessionOriginHost(msg.getSessionID())";
		String originRealm = objToStr(args[4]);//"getSessionOriginRealm(msg.getSessionID())";
		return	"registerIMSI(" + imsi + ", " + componentType + ", " + association + ", " + originHost + ", " + originRealm + ");\n";
	}
}
