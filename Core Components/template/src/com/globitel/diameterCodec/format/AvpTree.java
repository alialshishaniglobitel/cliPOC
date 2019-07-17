package com.globitel.diameterCodec.format;

import java.util.List;

public abstract class AvpTree
{
	public static class AvpCodeRelation
	{
		
		public AvpCodeRelation()
		{
		}
		public AvpCodeRelation(int _id, int parent, String _name, int child)
		{
			id = _id;
			code = parent;
			parentId = child;
			name = new String(_name);
		}
		public int code;
		public int parentId;
		public int id;
		public String name;
	}
	public abstract List<String> getLeafsList(Integer x);
	public abstract void addAvps(List<AvpCodeRelation> groupedAvps);
	public abstract List<String> getLeafsList();
	public abstract void print();
}
