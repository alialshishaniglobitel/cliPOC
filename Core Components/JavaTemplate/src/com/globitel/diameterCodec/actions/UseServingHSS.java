package com.globitel.diameterCodec.actions;

public class UseServingHSS implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String imsi = objToStr(args[0]);//"msg.getIMSI()";
		String compType =objToStr(args[1]);//"\"hss\"";
		String oHost = objToStr(args[2]);//"null";
		String oRealm = objToStr(args[3]);//"null";
		String dhost = objToStr(args[4]);//"imsi_info.host";
		String drealm = objToStr(args[5]);//"imsi_info.realm";
		String association =objToStr(args[6]);//"imsi_info.association";
		return "imsi_info = getIMSIInfo(" + imsi + "," + compType + ");\n" + "msg.updateHeaders( " + oHost + ", " + oRealm + ", " + dhost + ", " + drealm + ");\n"
			+ "msg.setDstIP(" + association + ");\n";
	}

}
