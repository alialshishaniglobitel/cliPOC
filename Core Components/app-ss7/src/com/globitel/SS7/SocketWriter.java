package com.globitel.SS7;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.globitel.Application.RGS_Configuration;
import com.globitel.SS7.codec.Message;
import com.globitel.common.utils.ByteArray;

public class SocketWriter extends Thread {
	public ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
	private RGSSocket sock;
	int rgsIndex;

	public SocketWriter(boolean _flushEnabled, RequestHandler _rgsInstance, RGS_Configuration RGS_Object,
			int rgsIndex) {
		sock = new RGSSocket(_flushEnabled, _rgsInstance, RGS_Object, rgsIndex);
		sock.start();
	}

	public SocketWriter(RGSSocket sock) {
		this.sock = sock;
	}

	public void write(ByteArray buffer) {
		RequestHandler.logDebug("SocketWriter write, before creating msg");
		Message msg = new Message(rgsIndex);
		msg.rawData = Arrays.copyOfRange(buffer.buffer, 0, buffer.getSize());
		msg.rawLength = buffer.getSize();
		RequestHandler.logDebug("SocketWriter write, msg object created, enqueueing");
		queue.add(msg);
		RequestHandler.logDebug("SocketWriter write, enqueued");
	}

	public void run() {
		int idle_threshold = 0;
		while (!(Thread.interrupted())) {
			try {
				Message msg = null;
				msg = queue.poll();
				if (msg != null) {
					if (msg.rawLength > 0) {
						RequestHandler.logBuffer(msg.rawData, "Writting to socket...");
						sock.write(msg.rawData, 0, msg.rawLength);
						RequestHandler.logDebug("Written to socket...");
					}
					msg = null;
				}
				if (++idle_threshold > 100) {
					try {
						sleep(1);
					} catch (InterruptedException e) {
						RequestHandler.logError("Exception, SocketWriter::Sleep: " + e.getMessage());
					}
					idle_threshold = 0;
				}
			} catch (Exception e) {
				RequestHandler.logError("Exception, SocketWriter::Run: " + e.getMessage());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e1) {
					RequestHandler.logError("Exception, SocketWriter:InterupptedException: " + e.getMessage());
				}
			}
		}
	}
}
