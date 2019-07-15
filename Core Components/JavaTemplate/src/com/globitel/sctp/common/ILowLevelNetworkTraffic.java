package com.globitel.sctp.common;

import org.mobicents.protocols.api.Association;

public interface ILowLevelNetworkTraffic
{
	public void handleLowLevelTraffic(int payloadProtocolId, Association association, byte[] msgData);

}
