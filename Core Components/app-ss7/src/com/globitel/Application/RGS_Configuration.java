package com.globitel.Application;

import java.util.ArrayList;
import java.util.List;

public class RGS_Configuration {
	private String[] address;
	private String[] port;

	private boolean hasData = true;
	public List<byte[]> registrationMessages;
	private boolean sendRegistrationMessage = true;
	private int numberOfRGSs = 0;

	public RGS_Configuration(String[] address, String[] port) {
		this.address = address;
		this.port = port;

		if (address[0] == null) {
			hasData = false;
			sendRegistrationMessage = false;
		} else {
			registrationMessages = new ArrayList<byte[]>();
			numberOfRGSs = address.length;
		}
	}

	public String getAddress(int index) {
		return this.address[index];
	}

	public String getPort(int index) {
		return this.port[index];
	}

	public boolean hasData() {
		return this.hasData;
	}

	public void sendRegistrationMessage(boolean sendRegistrationMessage) {
		this.sendRegistrationMessage = sendRegistrationMessage;
	}

	public boolean sendRegistrationMessage() {
		return this.sendRegistrationMessage;
	}

	public int getNumberOfRGSs() {
		return this.numberOfRGSs;
	}
}
