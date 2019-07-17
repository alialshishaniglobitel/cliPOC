package com.globitel.diameterCodec.actions;


public class blacklist implements InvokeInterface
{
	@Override
	public String invoke(Object... args)
	{
		String input = objToStr(args[0]);
		String answer = objToStr(args[1]);
		
		String result = factory.getActionBlackList(input, answer);
		return result;
	}
}
