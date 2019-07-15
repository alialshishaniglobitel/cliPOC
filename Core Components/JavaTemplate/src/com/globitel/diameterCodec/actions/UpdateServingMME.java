package com.globitel.diameterCodec.actions;

public class UpdateServingMME implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String imsi = objToStr(args[0]);//"getSessionIMSI(msg.getSessionID())";
		String compType = objToStr(args[1]);//"\"mme\"";
		String address = objToStr(args[2]);//"msg.getDstIP().get(0)";
		String host = objToStr(args[3]);//"getSessionOriginHost(msg.getSessionID())";
		String realm = objToStr(args[4]);//"getSessionOriginRealm(msg.getSessionID())";
		return String.format("registerIMSI(%s, %s, %s, %s, %s);\n", 
				imsi, compType, address, host, realm );
	}

}
