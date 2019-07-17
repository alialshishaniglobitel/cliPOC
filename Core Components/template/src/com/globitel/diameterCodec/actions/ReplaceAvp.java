package com.globitel.diameterCodec.actions;

import java.util.List;

public class ReplaceAvp implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		List<Integer> avpPath = objToIntList(args[0]);
		int code = objToInt(args[1]);
		removeLastItemIfEqualsAvpCode(avpPath,code);
		int newCode = objToInt(args[2]);
		int vendor = objToInt(args[3]);
		boolean hasVendor = objToBool(args[4]);
		boolean isMandatory = objToBool(args[5]);
		boolean isSecure = objToBool(args[6]);
		String value = objToStr(args[7]);
		return factory.getActionAvpReplace(avpPath, code, newCode, vendor, hasVendor,
				isMandatory, isSecure, value);
	}

}
