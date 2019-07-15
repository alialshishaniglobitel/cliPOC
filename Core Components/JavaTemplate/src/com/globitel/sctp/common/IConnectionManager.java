package com.globitel.sctp.common;

import java.util.List;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.sctp.ServerImpl;

public interface IConnectionManager
{
	ServerImpl addServer(ManagementImpl management, List<String> hosts,String name, int port, IpChannelType ipChannelType, 
			boolean allowAnnonymous, int maxConcurrentConnections) throws Exception;

	boolean containsAssociation(String string);
	Association createServerAssociation (ManagementImpl management, String name, IpChannelType ipChannelType,
			String serverAssociationName, String clientHost, int clientPort,
			AssociationListener listener);
	Association createServerAssociation (String serverAssociationName, String clientHost, int clientPort,
			AssociationListener listener);

	ManagementImpl getManagement(boolean isSingleThread) throws Exception;

	boolean createAssociationWithType(ManagementImpl management, String hostAddress, 
			IpChannelType ipChannelType, int localPort, String _clientAssociationName, String _peerAddress,
			int _serverPort, List<String> extraHostAddresses, 
			AssociationListener callback,
			IFirstMessage first, boolean isServerType);

	void addNewAssociationToMap(String name, Association association);

	void removeAssociation(String association);
}
