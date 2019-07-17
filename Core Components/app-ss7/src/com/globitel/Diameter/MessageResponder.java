package com.globitel.Diameter;

import java.io.IOException;

import com.globitel.Diameter.Interfaces.CapabilityExchangeInfo;
import com.globitel.Diameter.Interfaces.IAppConfig;
import com.globitel.Diameter.Interfaces.IMessageResponder;
import com.globitel.XmlDiameter.xml.AVPNode;
import com.globitel.XmlDiameter.xml.IXmlMessageFactory;
import com.globitel.XmlDiameter.xml.XmlMessage;
import com.globitel.XmlDiameter.xml.XmlMessageReader;
import com.globitel.diameterCodec.Diameter.DiameterMessage;


public class MessageResponder implements IMessageResponder
{
	private static final String ORIGIN_REALM_KEY = "296";
	private static final String ORIGIN_HOST_KEY = "264";
	private IAppConfig appConfig;
	private IXmlMessageFactory xmlFactory;
	public MessageResponder(IAppConfig _appConfig, IXmlMessageFactory _xmlFactory)
	{
		appConfig = _appConfig;
		xmlFactory = _xmlFactory;
	}
	@Override
	public void UpdateCapabilityExchange(XmlMessage xmlMsg, CapabilityExchangeInfo parent)
	{
		setInfo(xmlMsg, parent);
	}
	@Override
	public void setInfo(XmlMessage xmlMsg, CapabilityExchangeInfo parent)
	{
//		AVPNode xmlOriginHost = xmlMsg.getNode(ORIGIN_HOST_KEY);
//		xmlOriginHost.setValue(appConfig.getOriginHost());
//		AVPNode xmlOriginRealm = xmlMsg.getNode(ORIGIN_REALM_KEY);
//		xmlOriginRealm.setValue(appConfig.getOriginRealm());
//		if ( parent != null )
//		{
//			parent.setApplicationIPS(xmlMsg);
//		}
	}
	@Override
	public byte[] sendCapabilityAnswer(DiameterMessage srcMsg, CapabilityExchangeInfo parent) throws IOException
	{
		DiameterMessage result = new DiameterMessage();
		XmlMessageReader xmlMsgReader = xmlFactory.create("CAPABILITY_EXCHANGE_RES");
		XmlMessage xmlMsg = xmlMsgReader.parse();
		UpdateCapabilityExchange(xmlMsg, parent);
		
		xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + srcMsg.getHopByHopIdentifier());
		xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + srcMsg.getEndToEndIdentifier());
		byte[] bytes = xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, result);
		return bytes;
	}

	@Override
	public byte[] sendWatchDogResponse(DiameterMessage srcMsg) throws IOException
	{
		DiameterMessage result = new DiameterMessage();
		XmlMessageReader xmlMsgReader = xmlFactory.create("WATCH_DOG_RES");
		XmlMessage xmlMsg = xmlMsgReader.parse();
		setInfo(xmlMsg, null);
		xmlMsg.setValue("HOP_BY_HOP_IDENTIFIER", "" + srcMsg.getHopByHopIdentifier());
		xmlMsg.setValue("END_TO_END_IDENTIFIER", "" + srcMsg.getEndToEndIdentifier());
		byte[] bytes = xmlFactory.encodeMessage(xmlMsg, xmlMsgReader, result);
		return bytes;
	}
}
