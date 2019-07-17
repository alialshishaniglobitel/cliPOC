package com.globitel.XmlDiameter.xml;


class XmlMessageHeader
{
	public int applicationId;
	public int commandCode;
	public byte flags;
	public byte version;
	public int hopyByHopIdentifier;
	public int endToEndIdentifier;
}

public class XmlMessage
{
	AVPNode avpTree;
	XmlMessageHeader header;
	

	public void setValue(String key, String value)
	{
		switch (key)
		{
		case "APPLICATION_ID":
		{
			header.applicationId = Integer.parseInt(value);
		}
			break;
		case "COMMAND_CODE":
		{
			header.commandCode = Integer.parseInt(value);
		}
			break;
		case "FLAGS":
		{
			int flags = Integer.parseInt(value, 10);
			header.flags = (byte) flags;
		}
			break;
		case "VERSION":
		{
			header.version = (byte) Integer.parseInt(value);
		}
			break;
		case "HOP_BY_HOP_IDENTIFIER":
		{
			header.hopyByHopIdentifier = Integer.parseInt(value);
		}
			break;
		case "END_TO_END_IDENTIFIER":
		{
			header.endToEndIdentifier = Integer.parseInt(value);
		}
			break;
		default:
		{
			AVPNode node = getNode(key);
			if ( node != null )
			{
				node.setValue(value);
			}
			else
			{
				
			}
		}
		}
	}

	public AVPNode getNode(String string)
	{
		for (AVPNode node : avpTree.getChildren())
		{
			AVPNode foundNode = search(node, string);
			if (foundNode != null)
				return foundNode;
		}
		return null;
	}

	private AVPNode search(AVPNode node, String string)
	{
		String mainKey = getMainKey(string);
		if (node.getValue("CODE").equals(mainKey) || node.getValue("NAME").equals(mainKey))
		{
			for (AVPNode child : node.getChildren())
			{
				AVPNode childNode = search(child, removeMainKey(string));
				if (childNode != null)
					return childNode;
			}

			return node;
		}
		// TODO Auto-generated method stub

		return null;
	}

	private String removeMainKey(String key)
	{
		String result = "";
		String[] keys = key.split("-");
		for (int k = 1; k < keys.length; k++)
		{
			if (k > 1)
				result += "-";
			result += keys[k];
		}
		return result;
	}

	private String getMainKey(String key)
	{
		return key.split("-")[0];
	}
}