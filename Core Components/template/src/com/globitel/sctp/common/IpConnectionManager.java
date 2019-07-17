package com.globitel.sctp.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.sctp.ServerImpl;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class IpConnectionManager implements IConnectionManager{
	private List<ManagementImpl> managementList = new ArrayList<>();	
	String name = "serverName";
	ServerImpl server = null;
	String managementStack = "server-management";
	public ManagementImpl management = null;
	int reconnectionTime = 100;
	
	static List<String> hosts = new ArrayList<String>();
	int port = 1234;
	IpChannelType ipChannelType = IpChannelType.SCTP;
	public Map<String,Association> associations = new ConcurrentHashMap<String,Association>();
	private boolean serverMode;
	public IpConnectionManager()
	{
	}
	public IpConnectionManager(boolean _serverMode, String _name,
			String _managementStack, int _reconnectionTime, List<String> _hosts,
			int _port, IpChannelType _ipChannelType) throws Exception {
		name = _name;
		managementStack = _managementStack;
		reconnectionTime = _reconnectionTime;
		hosts = _hosts;
		port = _port;
		ipChannelType = _ipChannelType;
		serverMode = _serverMode;
		setUp();
		if (serverMode)
		{
			setUPServerMode();
		}
	}

	public void setUp() throws Exception {
		management = new ManagementImpl(managementStack);
		management.setSingleThread(true);
		management.start();
        management.setConnectDelay(reconnectionTime);// Try connecting every 10 secs
		management.removeAllResourses();
	}	
	
	public ManagementImpl getManagement(boolean isSingleThread) throws Exception
	{
		ManagementImpl mng = null;
		mng = new ManagementImpl(managementStack);
		mng.setSingleThread(isSingleThread);
		mng.start();
		mng.setConnectDelay(reconnectionTime);// Try connecting every 10 secs
		mng.removeAllResourses();	
		managementList.add(mng);
		return mng;
	}

	
	private void setUPServerMode() throws Exception {
		String[] extraServerHost = null;
		if (hosts.size() > 1) {
			extraServerHost = (hosts.subList(1, hosts.size()).toArray(new String[hosts.size() - 1]));
		}
		server = management.addServer(name, hosts.get(0), port, ipChannelType, false, 0, extraServerHost);
		management.startServer(name);
	}
	

	
	public Association createServerAssociation (String serverAssociationName, String clientHost, 
			int clientPort,//IMessageCallBack cb, IConnectionStateCallBack csb, ILowLevelNetworkTraffic _llCB
			AssociationListener listener) {
		try
		{
			Association serverAssociation = management.addServerAssociation(clientHost, clientPort, name, serverAssociationName, ipChannelType);
			serverAssociation.setAssociationListener(listener);
			management.startAssociation(serverAssociationName);
			associations.put(serverAssociationName, serverAssociation);
			return serverAssociation;
		}
		catch(Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
		return null;
	}

	public void tearDown() throws Exception {
		for (String serverAssociationName : associations.keySet())
		{
			management.stopAssociation(serverAssociationName);
			management.removeAssociation(serverAssociationName);
			Thread.sleep(1000 * 2);
		}
		Thread.sleep(1000 * 2);
		management.stopServer(name);
		Thread.sleep(1000 * 2);
		management.removeServer(name);
		Thread.sleep(1000 * 2);
			
		if ( management != null )
			management.stop();
		for(ManagementImpl mng: managementList)
		{
			mng.stop();
		}
	}
	
	public void stopAssociation(String association) throws Exception
	{
		management.stopAssociation(association);
	}
	public ServerImpl addServer(ManagementImpl management, List<String> hosts,String name, int port, IpChannelType ipChannelType, 
			boolean allowAnnonymous, int maxConcurrentConnections) throws Exception
	{
		String[] extraServerHost = null;
		if (hosts.size() > 1) {
			extraServerHost = (hosts.subList(1, hosts.size()).toArray(new String[hosts.size() - 1]));
		}
		server = management.addServer(name, hosts.get(0), port, ipChannelType, allowAnnonymous, maxConcurrentConnections, extraServerHost);
		management.startServer(name);
		return server;
	}
	public Association createServerAssociation (ManagementImpl management,
			String name, 
			IpChannelType ipChannelType, String serverAssociationName, 
			String clientHost, int clientPort,
			AssociationListener listener
			) 
	{
		Association serverAssociation = null;
		try {
			serverAssociation = management.addServerAssociation(clientHost, 
					clientPort, name, serverAssociationName, ipChannelType);
			serverAssociation.setAssociationListener(listener);
			management.startAssociation(serverAssociationName);
			associations.put(serverAssociationName, serverAssociation);
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error("Add Server Association Exception - "+ e.getMessage());
		}
		
		return serverAssociation;
	}
	@Override
	public boolean createAssociationWithType(ManagementImpl management, String hostAddress, IpChannelType ipChannelType, 
			int localPort, String _clientAssociationName, String _peerAddress, int _peerPort, 
			List<String> extraHostAddresses, AssociationListener callBack, IFirstMessage first, boolean isServerType)
	{
		int hostPort = 0;
		if ( localPort != 0)
			hostPort = localPort;
		
		try
		{
			String[] extraHostAddressesArr = new String[extraHostAddresses.size()];
			for (int i = 0; i < extraHostAddressesArr.length; i++) {
				extraHostAddressesArr[i] = extraHostAddresses.get(i);
			}			
			management.addAssociation(hostAddress, hostPort, _peerAddress, _peerPort, _clientAssociationName,
					ipChannelType, extraHostAddressesArr);
			return true;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error(e);
		}
		return false;
	}
	@Override
	public boolean containsAssociation(String associationName)// to be removed later
	{
		return associations.containsKey(associationName);
	}
	@Override
	public void removeAssociation(String association)
	{
		Association remove = associations.remove(association);
		if ( remove != null )
		{
			MyLoggerFactory.getInstance().getAppLogger().debug(association+", Successfully removed from connection manager");
		}
	}
	@Override
	public void addNewAssociationToMap(String name, Association association)
	{
		associations.put(name, association);
	}
	public List<String> removeDisabledAssociations(ManagementImpl management, List<String> allNewAndOldPeers)
	{
		List<String> removing = new ArrayList<String>();
		
		for( Association association : associations.values())
		{
			if ( allNewAndOldPeers.contains(association.getName())==false)//doesn't exists means it was removed from table
			{
				MyLoggerFactory.getInstance().getAppLogger().info(association.getName()+", added to toRemove list");
				removing.add(association.getName());
			}
			else
			{
				MyLoggerFactory.getInstance().getAppLogger().info(association.getName()+", NOT added to toRemove list");
			}
		}
		return removing;
	}
}
