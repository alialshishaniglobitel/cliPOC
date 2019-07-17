package com.globitel.sctp.common;

import org.mobicents.protocols.api.Association;

public interface IConnectionStateCallBack
{
	public abstract void connectionUp(Association association);
	
	public abstract void connectionDown(Association association);
	public abstract void connectionLost(Association association);
	public abstract void connectionRestart(Association association);
}
