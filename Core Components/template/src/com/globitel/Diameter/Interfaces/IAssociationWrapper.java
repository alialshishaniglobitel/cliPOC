package com.globitel.Diameter.Interfaces;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.PayloadData;

public interface IAssociationWrapper
{
	public String getClusterName();

	public void sendWithoutCheck(PayloadData payloadData);

	public void send(byte[] data, int i);

	public void sendAndIncrement(byte[] data, int i);

	public Association getAssociation();

	String getRoute();

	void disableAssociation(String associationString);

	boolean isAssociationEnabled();

	public void updateTimeStamp();

	public boolean isDiameter_up();

	void setDiameter_up(boolean diameter_up);

	String getAssociationName();

	public void addRouteList(String detailedName);

	public void setRoute(String routeName);

	public void setAssociation(Association ass);

	void setClusterName(String clusterName, float percentage, int priority);

	public int getClusterPriority();

	public float getClusterWeight();
}
