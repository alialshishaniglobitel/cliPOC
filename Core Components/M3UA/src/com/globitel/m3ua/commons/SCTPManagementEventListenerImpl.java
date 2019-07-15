package com.globitel.m3ua.commons;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.ManagementEventListener;
import org.mobicents.protocols.api.Server;

public class SCTPManagementEventListenerImpl implements ManagementEventListener {

	@Override
	public void onAssociationAdded(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationAdded, " + arg0.getName());
	}

	@Override
	public void onAssociationDown(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationDown, " + arg0.getName());
	}

	@Override
	public void onAssociationModified(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationModified, " + arg0.getName());
	}

	@Override
	public void onAssociationRemoved(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationRemoved, " + arg0.getName());
	}

	@Override
	public void onAssociationStarted(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationStarted, " + arg0.getName());
	}

	@Override
	public void onAssociationStopped(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationStopped, " + arg0.getName());
	}

	@Override
	public void onAssociationUp(Association arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onAssociationUp, " + arg0.getName());
	}

	@Override
	public void onRemoveAllResources() {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onRemoveAllResources");
	}

	@Override
	public void onServerAdded(Server arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onServerAdded, " + arg0.getName());
	}

	@Override
	public void onServerModified(Server arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onServerModified, " + arg0.getName());
	}

	@Override
	public void onServerRemoved(Server arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onServerRemoved, " + arg0.getName());
	}

	@Override
	public void onServiceStarted() {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onServiceStarted");
	}

	@Override
	public void onServiceStopped() {
		// TODO Auto-generated method stub
		Application.logger.info("SCTP onServiceStopped");
	}

}
