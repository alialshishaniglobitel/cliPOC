package com.globitel.sctp.common;


import org.mobicents.protocols.api.Association;
public interface IMessageCallBack
{
	public abstract void handleNewMessage(int payloadProtocolId, Association association, byte[] msgData);
}
