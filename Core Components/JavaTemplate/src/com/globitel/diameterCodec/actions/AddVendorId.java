package com.globitel.diameterCodec.actions;

import java.util.List;

public class AddVendorId implements InvokeInterface
{

	@Override
	public String invoke(Object...args)
	{
		List<Integer> avpPath = objToIntList(args[0]);
		int code = objToInt(args[1]);
		removeLastItemIfEqualsAvpCode(avpPath,code);
		int vendorId = objToInt(args[2]);
		return factory.getActionAddVendorID(avpPath, code, vendorId);
	}

}
