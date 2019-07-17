package com.globitel.XmlDiameter.xml;


import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.globitel.diameterCodec.Diameter.AVP;
import com.globitel.diameterCodec.Diameter.DiameterAVPGrouped;
import com.globitel.diameterCodec.Diameter.DiameterMessage;
public class XmlMessageReader {
    private String xmlSrc = "";
	private DiameterMessage msg = new DiameterMessage();
	public XmlMessageReader(String input)
	{
		xmlSrc = new String(input);
	}
    public XmlMessage parse() throws IOException
    {
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument;
        XmlMessage msg = null;
		try
		{
			jdomDocument = jdomBuilder.build(xmlSrc);
			Element rss = jdomDocument.getRootElement();
	        List<Element> channel = rss.getChildren();
	        msg = new XmlMessage();
	        msg.avpTree = new AVPNode("ROOT");
	        for (Element e:channel)
	        {
	        	if ( e.getName().equals("HEADER"))
	        	{
	        		msg.header = storeHeader(e);
	        		continue;
	        	}
	        	AVPNode avpNode = storeAttributes( e, msg.avpTree);
	        	for( Element t : e.getChildren() )
	        	{
	        		storeAttributes(t, avpNode);	
	        	}
	        }
		}
		catch (JDOMException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return msg;
    }
    
    private XmlMessageHeader storeHeader(Element e)
	{
		XmlMessageHeader header = new XmlMessageHeader();
    	for( Element child: e.getChildren())
    	{
    		switch(child.getName())
    		{
    		case "APPLICATION_ID":
    		{
    			header.applicationId = Integer.parseInt(child.getValue());
    		}
    		break;
    		case "COMMAND_CODE":
    		{
    			header.commandCode = Integer.parseInt(child.getValue());
    		}
    		break;
    		case "FLAGS":
    		{
    			int flags = Integer.parseInt(child.getValue(),10);
    			header.flags =(byte)flags;
    		}
    		break;
    		case "VERSION":
    		{
    			header.version = (byte) Integer.parseInt(child.getValue());
    		}
    		break;
    		case "HOP_BY_HOP_IDENTIFIER":
    		{
    			header.hopyByHopIdentifier = Integer.parseInt(child.getValue());
    		}
    		break;
    		case "END_TO_END_IDENTIFIER":
    		{
    			header.endToEndIdentifier = Integer.parseInt(child.getValue());
    		}
    		break;
    		
    		}
    	}
    	return header;
	}
	private AVPNode storeAttributes(Element t, AVPNode _avpNode)
	{
		// TODO Auto-generated method stub
    	AVPNode avpNode = storeAttributes(t);
    	_avpNode.addChild(avpNode);
    	return avpNode;
	}
	public AVPNode storeAttributes(Element e)
    {
		AVPNode avpNode = new AVPNode(e.getName());
    	for( Attribute att: e.getAttributes())
    	{
    		String name = att.getName();
    		String value = att.getValue();
    		avpNode.add(name,value);
    	}
    	return avpNode;
    }
	
	public void createMessage(XmlMessage xmlMsg, DiameterMessage msg)
	{
		msg.setApplicationID(xmlMsg.header.applicationId);
		msg.setCommandCode(xmlMsg.header.commandCode);
		msg.setFlags(xmlMsg.header.flags);
		msg.setVersion(xmlMsg.header.version);
		msg.setHopByHopIdentifier(xmlMsg.header.hopyByHopIdentifier);
		msg.setEndToEndIdentifier(xmlMsg.header.endToEndIdentifier);
		
		AVPNode nodeTree = xmlMsg.avpTree;
		for(AVPNode node:nodeTree.getChildren())
		{
			if ( isAVPEnabled(node))
			{
				AVP avp = CreateRecursive(node, null);
				msg.addAVP(avp);
			}
		}
	}
	private boolean isAVPEnabled(AVPNode node)
	{
		String enabled = node.getValue("ENABLED");
		if ( enabled == null || enabled.equals("TRUE"))
			return true;
		else
			return false;
	}
	private AVP CreateRecursive(AVPNode avpTree, AVPNode parent)
	{
		AVP avp = null;
		int vendorID = Integer.parseInt(avpTree.getValue("VENDOR_ID"));
		if ( avpTree.getChildren().size() != 0 )
		{
			avp = 
					(DiameterAVPGrouped)msg.addAVP(
							Integer.parseInt(avpTree.getValue("CODE")), 
					vendorID,
					(vendorID!=0), Boolean.parseBoolean(avpTree.getValue("IS_MANDATORY"))
					, Boolean.parseBoolean(avpTree.getValue("IS_SECURE")),null);
			
		}
		else
		{
			avp = msg.addAVP(
					Integer.parseInt(avpTree.getValue("CODE")), 
					vendorID,
					(vendorID!=0), Boolean.parseBoolean(avpTree.getValue("IS_MANDATORY"))
					, Boolean.parseBoolean(avpTree.getValue("IS_SECURE")), 
					avpTree.getValue("VALUE"));
		}
		for(AVPNode node:avpTree.getChildren())
		{
			if ( isAVPEnabled(node))
			{
				AVP avpChild = CreateRecursive(node, avpTree);
				((DiameterAVPGrouped)avp).addAVP(avpChild);
			}
		}
		return avp;
	}

}
 
