package com.globitel.Diameter.Interfaces;

import com.globitel.Diameter.structure.NetworkPeer;

public interface IWrapperGet
{
	IAssociationWrapper getAssociationWrapper(NetworkPeer node, String _routeName);
}
