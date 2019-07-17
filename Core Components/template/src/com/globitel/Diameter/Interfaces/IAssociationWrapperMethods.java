package com.globitel.Diameter.Interfaces;
import org.mobicents.protocols.api.Association;

public interface IAssociationWrapperMethods
{
	IAssociationWrapper getWrapperAndAddToRouteList(String associationName, String detailedName);
	IAssociationWrapper createWrapper(Association association, String detailedName);
}
