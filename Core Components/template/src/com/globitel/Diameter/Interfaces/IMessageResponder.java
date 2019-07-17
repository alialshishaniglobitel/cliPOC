package com.globitel.Diameter.Interfaces;

import java.io.IOException;

import com.globitel.XmlDiameter.xml.XmlMessage;
import com.globitel.diameterCodec.Diameter.DiameterMessage;

public interface IMessageResponder
{
	byte[] sendCapabilityAnswer(DiameterMessage srcMsg, CapabilityExchangeInfo parent) throws IOException;
	byte[] sendWatchDogResponse(DiameterMessage srcMsg) throws IOException;
	void UpdateCapabilityExchange(XmlMessage xmlMsg, CapabilityExchangeInfo parent);
	void setInfo(XmlMessage xmlMsg, CapabilityExchangeInfo parent);
}
