package com.globitel.SS7.codec;

import com.globitel.SS7.codec.Message;

public class LocalOperationClassMAP
{
	public static  String[]				classNames						= new String[256];
	
	public static void Initialize()
	{
		classNames[0] = "InitialDPArg";
		
	}
	
	private static String getClassName(long opCode)
	{
		int index = (int) (opCode & 0xff);
		String className = classNames[index];
		if(className == null)
		{
			return "Unknown Message(OC = " + Integer.toHexString(index) + ")";
		}
		else
		{
			return className;
		}
	}
	
	public synchronized static Object createObject(long opCode)
	{
		@SuppressWarnings("rawtypes")
		Class c = null;
		Object obj = null;
		try
		{
			String className = getClassName(opCode);
			c = Class.forName("CAP_datatypes." + className );
			obj = c.newInstance();
		}
		catch (Exception e)
		{
			Message.logError("Exception, createObject: " + e.getMessage());
			e.printStackTrace();
		}
		return obj;
	}
}
