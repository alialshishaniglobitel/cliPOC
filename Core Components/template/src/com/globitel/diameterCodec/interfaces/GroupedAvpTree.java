package com.globitel.diameterCodec.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.globitel.diameterCodec.format.AvpTree;
import com.globitel.diameterCodec.format.MyTree;
import com.globitel.diameterCodec.format.Node;

public class GroupedAvpTree extends AvpTree
{
	MyTree<Integer> t = new MyTree<Integer>();
	@Override
	public void print()
	{
		t.printTree(t.root, "	");
	}
	@Override
	public List<String> getLeafsList()
	{
		List<String> results = new ArrayList<>();
		t.getPaths(results );
		return results;
	}
	public List<String> getLeafsList(Integer x)
	{
		List<Node<Integer>> item = new ArrayList<Node<Integer>>();
		t.searchTreeByCode(t.root, x, item);
		List<String> strList = new ArrayList<String>();
		
		for(Node<Integer> e : item)
		{
			List<String> myList = getList(e);
			strList.addAll(myList);
		}
		return strList;
	}
	private List<String> getList(Node<Integer> item)
	{
		List<Node<Integer>> myList = new ArrayList<Node<Integer>>();
		item.getAllLeafs(item, myList);
		List<String> strList = new ArrayList<String>();
		for(Node<Integer> e:myList)
		{
			String str = e.printNodeCode();
			strList.add(str);
		}
		return strList;
		
	}
	public void addAvps(List<AvpCodeRelation> groupedAvps)
	{
		for( AvpCodeRelation g : groupedAvps )
		{
			t.addChild(g.id, g.code, g.parentId, g.name);
		}
	}

}
