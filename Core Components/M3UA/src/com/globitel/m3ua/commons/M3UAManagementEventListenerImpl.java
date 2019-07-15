package com.globitel.m3ua.commons;

import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.M3UAManagementEventListener;
import org.restcomm.protocols.ss7.m3ua.State;

public class M3UAManagementEventListenerImpl implements M3UAManagementEventListener {

	@Override
	public void onAsActive(As arg0, State arg1) {
		// TODO Auto-generated method stub
		 Application.logger.info("M3UA onAsActive for As:" + arg0.getName() + ", State:" + arg1.getName());

	}

	@Override
	public void onAsCreated(As arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAsCreated for As:" + arg0.getName());

	}

	@Override
	public void onAsDestroyed(As arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAsDestroyed for As:" + arg0.getName());

	}

	@Override
	public void onAsDown(As arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAsDown for As:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAsInactive(As arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAsInactive for As:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAsPending(As arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAsPending for As:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAspActive(Asp arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspActive for As:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAspAssignedToAs(As arg0, Asp arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspAssignedToAs for As:" + arg0.getName() + ", Asp:" + arg1.getName());
	}

	@Override
	public void onAspDown(Asp arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspDown for Asp:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAspFactoryCreated(AspFactory arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspFactoryCreated for AspFactory:" + arg0.getName());
	}

	@Override
	public void onAspFactoryDestroyed(AspFactory arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspFactoryDestroyed for AspFactory:" + arg0.getName());
	}

	@Override
	public void onAspFactoryStarted(AspFactory arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspFactoryStarted for AspFactory:" + arg0.getName());
	}

	@Override
	public void onAspFactoryStopped(AspFactory arg0) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspFactoryStopped for AspFactory:" + arg0.getName());
	}

	@Override
	public void onAspInactive(Asp arg0, State arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspInactive for Asp:" + arg0.getName() + ", State:" + arg1.getName());
	}

	@Override
	public void onAspUnassignedFromAs(As arg0, Asp arg1) {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onAspUnassignedFromAs for As:" + arg0.getName() + ", Asp:" + arg1.getName());
	}

	@Override
	public void onRemoveAllResources() {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onRemoveAllResources");
	}

	@Override
	public void onServiceStarted() {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onServiceStarted");
	}

	@Override
	public void onServiceStopped() {
		// TODO Auto-generated method stub
		Application.logger.info("M3UA onServiceStopped");
	}

}
