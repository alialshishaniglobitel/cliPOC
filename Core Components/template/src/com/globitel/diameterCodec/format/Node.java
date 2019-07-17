package com.globitel.diameterCodec.format;

import java.util.ArrayList;
import java.util.List;

public class Node<T>
{
	private T id;
	private final List<Node<T>> children = new ArrayList<>();
	private final Node<T> parent;
	T code;
	String name;

	public Node(Node<T> parent, T _id, String _name)
	{
		this.parent = parent;
		this.id = _id;
		name  = _name;
	}

	public Node(Node<T> parent)
	{
		this.parent = parent;
		this.parent.getChildren().add(this);
	}

	public T getId()
	{
		return id;
	}

	public void setId(T id)
	{
		this.id = id;
	}

	public List<Node<T>> getChildren()
	{
		return children;
	}

	public Node<T> getParent()
	{
		return parent;
	}

	private void printAscCode(Node<T> start, StringBuilder result)
	{
		if (start.parent != null)
			printAscCode(start.parent, result);
		result.append((start.code == null)? (""):(start.code + ","));
	}
	
	private void printAscName(Node<T> start, StringBuilder result)
	{
		if (start.parent != null)
		{
			Node<T> p = start.parent;
			System.out.printf("printAscName(%d), code(%d), name(%s), Parent (%d), code(%d), name(%s)\n",
					getId(),code, name,
			p.getId(),p.code, p.name);
			printAscName(start.parent, result);
		}
		result.append((start.name== null)? (""):(start.name + ","));
		System.out.printf("printAscName(%d), code(%d), name(%s), LOG(%s)\n",getId(),code, name, result.toString());
	}
	

	@Override
	public String toString()
	{
		return "" +code;
	}

	public void getAllLeafs(Node<T> item, List<Node<T>> myList)
	{
		// TODO Auto-generated method stub
		for( Node<T> e: item.getChildren())
		{
			getAllLeafs(e, myList);
		}
		if ( item.getChildren().size() == 0 )
		{
			myList.add(item);
		}
	}

	public void setCode(T _code)
	{
		// TODO Auto-generated method stub
		code = _code;
	}
	public String printNodeCode()
	{
		StringBuilder result = new StringBuilder();
		printAscCode(this, result);
		return result.substring(0, result.length()-1);
	}
	public String printNodeName()
	{
		// TODO Auto-generated method stub
		StringBuilder result = new StringBuilder();
		System.out.println("BEFORE PRINTING " + result);
		printAscName(this, result);
		System.out.println("AFTER PRINTING " + result);
		return result.substring(0, result.length()-1);
	}

	public void setName(String _name)
	{
		// TODO Auto-generated method stub
		name = _name;
	}
}