package com.globitel.diameterCodec.actions;

public class ChangeCC implements InvokeInterface
{

	@Override
	public String invoke(Object... args)
	{
		// TODO Auto-generated method stub
		int commandCode = objToInt(args[0]);
		return factory.getActionChangeCommandCode(commandCode);
	}

}
