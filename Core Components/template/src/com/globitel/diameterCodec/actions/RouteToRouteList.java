package com.globitel.diameterCodec.actions;

public class RouteToRouteList implements InvokeInterface
{
	public final String invoke(Object... args)
	{
		String dstRoute = objToStr(args[0]);
		return factory.getActionRouteToRouteList(dstRoute);
	}
}
