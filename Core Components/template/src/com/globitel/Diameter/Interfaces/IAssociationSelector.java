package com.globitel.Diameter.Interfaces;

import java.util.Collection;
import java.util.List;


public interface IAssociationSelector
{
	public void updateNodes(Collection<IAssociationWrapper> collection);
	public IAssociationWrapper selectAssociationWrapper(String _routeName);
	public void setAssociationManager(IWrapperGet mmc);
	public void removeNode(Object association);
}
