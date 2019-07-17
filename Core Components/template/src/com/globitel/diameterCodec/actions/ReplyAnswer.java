package com.globitel.diameterCodec.actions;

public class ReplyAnswer implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		String answer = objToStr(args[0]);
		return factory.getActionReplyAnswer(answer);
	}

}
