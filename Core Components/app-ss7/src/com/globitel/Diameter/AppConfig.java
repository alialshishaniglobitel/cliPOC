package com.globitel.Diameter;

import java.util.List;

import com.globitel.Diameter.Interfaces.IAppConfig;
import com.globitel.Diameter.structure.NetworkPeer;

public class AppConfig implements IAppConfig {
	public String originHost;
	public String originRealm;
	public String dstHost;
	public String dstRealm;
	private String lcsMSISDN;
	private List<NetworkPeer> association;
	public List<String> ipList;
	private boolean isSocketMultiThreaded;
	private boolean isEnableRemovalWDTimeout;
	private int associationTimeout;
	public int watchDogTime;

	public void initialize(String _originHost, String _originRealm, String _dstHost, String _dstRealm,
			int _watchDogTime, boolean _isSocketMultiThreaded) {
		originHost = _originHost;
		originRealm = _originRealm;
		dstHost = _dstHost;
		dstRealm = _dstRealm;
		watchDogTime = _watchDogTime;
		isSocketMultiThreaded = _isSocketMultiThreaded;
	}

	@Override
	public String getOriginHost() {
		// TODO Auto-generated method stub
		return originHost;
	}

	@Override
	public String getOriginRealm() {
		// TODO Auto-generated method stub
		return originRealm;
	}

	@Override
	public String getDstHost() {
		// TODO Auto-generated method stub
		return dstHost;
	}

	@Override
	public String getDstRealm() {
		// TODO Auto-generated method stub
		return dstRealm;
	}

	@Override
	public List<String> getIpList() {
		// TODO Auto-generated method stub
		return ipList;
	}

	@Override
	public int getWatchDogTime() {
		// TODO Auto-generated method stub
		return watchDogTime;
	}

	public boolean isSocketMultiThreaded() {
		return isSocketMultiThreaded;
	}

	@Override
	public boolean isEnableWDTimeOutRemoval() {
		// TODO Auto-generated method stub
		return isEnableRemovalWDTimeout;
	}

	@Override
	public long getAssociationTimeout() {
		// TODO Auto-generated method stub
		return associationTimeout;
	}

	@Override
	public String getLcsMSISDN() {
		return lcsMSISDN;
	}

	@Override
	public void setLcsMSISDN(String lcsMSISDN) {
		this.lcsMSISDN = lcsMSISDN;
	}

	@Override
	public List<NetworkPeer> getAssociations() {
		// TODO Auto-generated method stub
		return association;
	}

	@Override
	public void initialize(int associationTimeout, String originHost, String originRealm, String dstHost,
			String dstRealm, List<String> ipList, int _watchDogTime, boolean _isSocketMultiThreaded,
			boolean _isEnableRemovalWDTimeout, List<NetworkPeer> association) {
		// TODO Auto-generated method stub
		
	}
}
