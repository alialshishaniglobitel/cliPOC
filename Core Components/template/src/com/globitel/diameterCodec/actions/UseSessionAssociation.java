package com.globitel.diameterCodec.actions;

public class UseSessionAssociation implements InvokeInterface
{
	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String association = objToStr(args[0]);//"getSessionAssociation(msg.getSessionID())";
		return "msg.setDstIP(" + association + ");\n";
	}
}
