package com.globitel.XmlDiameter.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AVPNode
{
	private List<AVPNode> childs = new ArrayList<AVPNode>();
	private Map<String,String> attributes = new HashMap<String, String>();
	private String name = "";
	public AVPNode(String _name)
	{
		name = _name;
	}
	public void add(String key, String value)
	{
		// TODO Auto-generated method stub
		attributes.put(key,value);
	}
	public void addChild(AVPNode avpNode)
	{
		// TODO Auto-generated method stub
		childs.add(avpNode);
	}
	public String getValue(String string)
	{
		// TODO Auto-generated method stub
		return attributes.get(string);
	}
	public List<AVPNode> getChildren()
	{
		// TODO Auto-generated method stub
		return childs;
	}
	public void setValue(String string)
	{
		// TODO Auto-generated method stub
		attributes.put("VALUE",string);
	}
	public void setCode(String string)
	{
		// TODO Auto-generated method stub
		attributes.put("CODE",string);
	}
	public void disable()
	{
		// TODO Auto-generated method stub
		attributes.put("ENABLED", "FALSE");
	}
	public void enable()
	{
		// TODO Auto-generated method stub
		attributes.put("ENABLED", "TRUE");
	}
}
