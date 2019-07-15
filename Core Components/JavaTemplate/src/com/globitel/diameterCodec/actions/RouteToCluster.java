package com.globitel.diameterCodec.actions;


public class RouteToCluster implements InvokeInterface
{
	@Override
	public final String invoke(Object... args) throws Exception
	{
		String dstCluster = objToStr(args[0]);
		return factory.getActionRouteToClsuter(dstCluster);
	}
}
