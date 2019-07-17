package com.globitel.diameterCodec.actions;

public class UseSessionInformation implements InvokeInterface
{
	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String association = objToStr(args[0]);//"getSessionAssociation(msg.getSessionID())";
		String originHost = objToStr(args[1]);//"msg.getOriginHost()";
		String originRealm = objToStr(args[2]);//"getSessionDestinationRealm(msg.getSessionID())";
		String destHost = objToStr(args[3]);//"null";
		String destRealm = objToStr(args[4]);//"null";
		String sessionId = objToStr(args[5]);//"getSessionID(msg.getSessionID())";
		return "msg.setDstIP(" + association + ");\n"
			+ "msg.updateHeaders( " + originHost + ", " + originRealm + ", " + destHost + ", " + destRealm + ");\n" + "msg.setSessionId(" + sessionId + ");\n";
	}
}
