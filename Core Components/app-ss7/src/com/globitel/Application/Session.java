package com.globitel.Application;

import com.globitel.SS7.codec.Message;

public class Session {
	public int processorID;
	public String tidStr;
	private CDR cdr;
	long time;
	private Message message;
	private String msisdn;

	public Session(int processorID) {
		this.processorID = processorID;
		updateSessionTime();
	}

	protected void updateSessionTime() {
		time = System.currentTimeMillis();
	}

	public Session setCDR(CDR cdr) {
		this.cdr = cdr;
		return this;
	}

	public Session setMessage(Message message) {
		this.message = message;
		return this;
	}

	public CDR getCDR() {
		return this.cdr;
	}

	public Message getMessage() {
		return this.message;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public Session setMsisdn(String msisdn) {
		this.msisdn = msisdn;
		return this;
	}

}
