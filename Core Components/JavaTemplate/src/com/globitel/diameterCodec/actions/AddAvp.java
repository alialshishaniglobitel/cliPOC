package com.globitel.diameterCodec.actions;

import java.util.List;

public class AddAvp implements InvokeInterface
{
	@Override
	public String invoke(Object... args)
	{
		List<Integer> avpPath = objToIntList(args[0]);
		int code = objToInt(args[1]);
		removeLastItemIfEqualsAvpCode(avpPath,code);
		int vendor = objToInt(args[2]);
		boolean hasVendor = objToBool( args[3]);
		boolean isMandatory = objToBool(args[4]);
		boolean isProtected = objToBool(args[5]);
		String value = objToStr(args[6]);
		String result = factory.getActionAddAvp(avpPath, code, vendor, hasVendor, isMandatory, isProtected, value);
		return result;
	}
}
