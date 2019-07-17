package com.globitel.diameterCodec.actions;

public class RouteToPeer implements InvokeInterface
{
	public String invoke(Object... args){
		String dstIP = objToStr( args[0]);
		return factory.getActionRouteToPeer(dstIP);
	}
}
