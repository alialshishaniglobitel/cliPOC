package com.globitel.diameterCodec.actions;

import java.util.List;

public class RemoveVendorId implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		List<Integer> avpPath = objToIntList(args[0]);
		int code = objToInt(args[1]);
		removeLastItemIfEqualsAvpCode(avpPath,code);
		return factory.getActionRemoveVendorID(avpPath, code);
	}

}
