package com.globitel.Diameter.Interfaces;


import java.util.List;
import java.util.Map;

import org.mobicents.protocols.api.Association;

import com.globitel.Diameter.structure.NetworkPeer;
import com.globitel.sctp.common.IMessageCallBack;

public interface IAssociationManager
{
	void createAssociations(List<NetworkPeer> peers, List<String> hosts, IMessageCallBack logic) throws Exception;
	IAssociationWrapper get(String key);
	IAssociationWrapper get(String key, String routeName);
	void tearDown() throws Exception;
	Map<String, IAssociationWrapper> getClients();
	boolean areAllDiameterLinksUp();
	boolean isDiameterUp(Association assoc);
	void setPeers(List<NetworkPeer> allAssociations);
}
