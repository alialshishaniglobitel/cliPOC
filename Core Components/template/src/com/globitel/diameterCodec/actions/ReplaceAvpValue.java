package com.globitel.diameterCodec.actions;

import java.util.List;

public class ReplaceAvpValue implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		List<Integer> avpPath = objToIntList(args[0]);
		int code = objToInt(args[1]);
		removeLastItemIfEqualsAvpCode(avpPath,code);
		String value = objToStr(args[2]);
		String result = factory.getActionAvpReplaceValue(avpPath, code, value);
		return result;
	}

}
