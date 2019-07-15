package com.globitel.Diameter.structure;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class NetworkPeer
{
	public int nodeId;
	public String hostName;
	public String hostRealm;
	public boolean isServerType;
	public String description;
	public String publicName;
	public int internalNode;
	public int routelistId;
	public String peerAddress;
	public int hostPort;
	public int remotePort;
	public String routeListName;
	private int msgCount;
	public int throttling;
	public int action;
	public String action_name;
	public String action_value;
	public String name;
	public String hostAddress;
	public List<String> extraHostAddresses = new ArrayList<String>();
	public String clusterName;
	public float clusterWeight;
	public int clusterPriority;

	public NetworkPeer()
	{
	}

	public NetworkPeer(int id, String _name, String _ip, int port1, int port2, String _routeListName)
	{
		setName(_name);
		nodeId = id;
		peerAddress = _ip;
		hostPort = port1;
		remotePort = port2;
		routeListName = _routeListName;
	}

	public NetworkPeer(int id, boolean _isServer, String _name, String _ip, int port1, int port2, String _routeListName)
	{
		setName(_name);
		nodeId = id;
		peerAddress = _ip;
		hostPort = port1;
		remotePort = port2;
		routeListName = _routeListName;
		isServerType = _isServer;
	}

	// public String extraInfo()
	// {
	// return routeListName+"->"+(isServerType?"Server-":"")+ nodeId +
	// ":"+ip+":"+hostPort+":"+remotePort;
	// }
	@Override
	public String toString()
	{
		// return ip+":"+hostPort+":"+remotePort;
		return getName();
	}

	// public String getAddress()
	// {
	// // TODO Auto-generated method stub
	// return ip+":"+hostPort+":"+remotePort;
	// }
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (!NetworkPeer.class.isAssignableFrom(obj.getClass()))
		{
			return false;
		}
		final NetworkPeer other = (NetworkPeer) obj;
		if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName()))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.getName());
	}

	public void increaseMsgCount()
	{
		msgCount++;
	}

	public void decreaseMsgCount()
	{
		msgCount--;
	}

	public int getMsgCount()
	{
		return msgCount;
	}

	public String getFullAddress()
	{
		// TODO Auto-generated method stub
		return peerAddress + ":" + hostPort + ":" + remotePort;
	}

	public int getThrottling()
	{
		// TODO Auto-generated method stub
		return throttling;
	}

	public int getAction()
	{
		// TODO Auto-generated method stub
		return action;
	}

	// public String getAssociationName()
	// {
	// return nodeId + ",association-" + toString();
	// }
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addHostAddresses(String[] split)
	{
		hostAddress = split[0];
		// TODO Auto-generated method stub
		for (int i = 1; split != null && i < split.length; i++)
		{
			extraHostAddresses.add(split[i]);
		}
	}

	public String str()
	{
		// TODO Auto-generated method stub
		return String.format("name=%s, hostAddress=%s, ExtraHostAddress=%s, peerAddress=%s, hostPort=%d, remotePort=%d, isServer=%b", name, hostAddress, (extraHostAddresses == null ? ""
				: extraHostAddresses.toString()), peerAddress, hostPort,
				remotePort, isServerType);
	}
}
