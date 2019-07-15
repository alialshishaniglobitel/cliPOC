package com.globitel.Diameter.Interfaces;

import java.util.List;

import com.globitel.Diameter.structure.NetworkPeer;

public interface IAppConfig
{
	public String getOriginHost();
	public String getOriginRealm();
	public String getDstHost();
	public String getDstRealm();
	public List<String> getIpList();
	public void initialize(int associationTimeout, String originHost, String originRealm, String dstHost, 
			String dstRealm, List<String> ipList, int _watchDogTime, boolean _isSocketMultiThreaded, 
			boolean _isEnableRemovalWDTimeout, List<NetworkPeer> association);
	int getWatchDogTime();
	public boolean isSocketMultiThreaded();
	public boolean isEnableWDTimeOutRemoval();
	public long getAssociationTimeout();
	public String getLcsMSISDN();
	void setLcsMSISDN(String lcsMSISDN);
	public List<NetworkPeer> getAssociations();
}
