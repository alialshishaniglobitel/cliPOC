package com.globitel.sctp;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.ManagementEventListener;
import org.mobicents.protocols.api.Server;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class SCTPManagementEventListenerImpl implements ManagementEventListener {
	
	@Override
	public void onAssociationAdded(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationAdded, " + arg0.getName());
	}

	@Override
	public void onAssociationDown(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationDown, " + arg0.getName());
	}

	public void onAssociationModified(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationModified, " + arg0.getName());
	}

	@Override
	public void onAssociationRemoved(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationRemoved, " + arg0.getName());
	}

	@Override
	public void onAssociationStarted(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationStarted, " + arg0.getName());
	}

	@Override
	public void onAssociationStopped(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationStopped, " + arg0.getName());
	}

	@Override
	public void onAssociationUp(Association arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onAssociationUp, " + arg0.getName());
	}

	@Override
	public void onRemoveAllResources() {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onRemoveAllResources");
	}

	@Override
	public void onServerAdded(Server arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onServerAdded, " + arg0.getName());
	}

	public void onServerModified(Server arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onServerModified, " + arg0.getName());
	}

	@Override
	public void onServerRemoved(Server arg0) {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onServerRemoved, " + arg0.getName());
	}

	@Override
	public void onServiceStarted() {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onServiceStarted");
	}

	@Override
	public void onServiceStopped() {
		// TODO Auto-generated method stub
		MyLoggerFactory.getInstance().getAppLogger().info("SCTP onServiceStopped");
	}

}
