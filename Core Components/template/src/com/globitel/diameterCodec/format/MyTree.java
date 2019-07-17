package com.globitel.diameterCodec.format;

import java.util.ArrayList;
import java.util.List;

public class MyTree<T>
{
	public Node<T> root = new Node<T>(null, null, null);

	public Node<T> addChild(T id, T code, T parentId, String name)
	{
		Node<T> parent = searchTreeById(root, parentId);

		if ( parent == null )
			parent = root;
		
		Node<T> node = new Node<T>(parent);
		node.setId(id);
		node.setCode(code);
		node.setName(name);
		return node;
	}
	
	public void getPaths(List<String> results)
	{
		List<Node<T>> out = new ArrayList<>();
		getPaths(root, out );
		
		for(Node<T> e : out)
		{
			results.add(e.printNodeCode());
			results.add(e.printNodeName());
		}
	}
	private void getPaths(Node<T> start, List<Node<T>> out)
	{
		for (Node<T> each : start.getChildren())
		{
			getPaths(each, out);
		}
		if ( start.getChildren().size() == 0 )
		{
			out.add(start);
		}
	}
	
	public void printTree(Node<T> node, String appender)
	{
		System.out.println(appender.toString() + node.getId()+", "+ node.code +", "+ node.name);
		for (Node<T> each : node.getChildren())
		{
			printTree(each, appender.toString() + appender.toString());
		}
	}

	public void searchTreeByCode(Node<T> node, T parent, List<Node<T>> result)
	{
		for (Node<T> each : node.getChildren())
		{
			searchTreeByCode(each, parent, result);
		}

		if (node.code != null && node.code.equals(parent))
		{
			result.add(node);
		}
	}
	
	public Node<T> searchTreeById(Node<T> node, T parent)
	{
		Node<T> result = null;
		for (Node<T> each : node.getChildren())
		{
			result = searchTreeById(each, parent);
			if ( result != null )
				return result;
		}

		if (node.getId() != null && node.getId().equals(parent))
		{
			return node;
		}
		return null;
	}
}
