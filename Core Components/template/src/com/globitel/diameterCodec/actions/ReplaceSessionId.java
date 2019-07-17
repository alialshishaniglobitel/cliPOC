package com.globitel.diameterCodec.actions;

public class ReplaceSessionId implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		String oldSessionId = objToStr(args[0]);
		String newSessionId = objToStr(args[1]);
		return factory.getActionReplaceSessionId(oldSessionId, newSessionId);
	}

}
