package com.globitel.XmlDiameter.xml;

import java.io.IOException;

import com.globitel.diameterCodec.Diameter.DiameterMessage;


public interface IXmlMessageFactory
{
	void initialize(String _xmlDirectory);
	XmlMessageReader create(String messageName) throws IOException;
	byte[] encodeMessage(XmlMessage xmlMsg, XmlMessageReader reader, DiameterMessage msg);
}
