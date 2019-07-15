package com.globitel.XmlDiameter.xml;

import java.io.IOException;

import com.globitel.XmlDiameter.xml.XmlMessage;

import com.globitel.diameterCodec.Diameter.DiameterMessage;

public class XmlMessageFactory implements  IXmlMessageFactory
{
	private String xmlDirectory;
	public void initialize(String _xmlDirectory)
	{
		xmlDirectory = new String(_xmlDirectory);
	}
	public XmlMessageReader create(String messageName) throws IOException
	{
		XmlMessageReader xmlMsgReader = new XmlMessageReader(xmlDirectory+"/"+messageName+".xml");
		return xmlMsgReader;
	}
	public byte[] encodeMessage(XmlMessage xmlMsg, XmlMessageReader reader, DiameterMessage msg)
	{
		reader.createMessage(xmlMsg, msg);
		byte[] buff = msg.encode();
		return buff;
	}
}
