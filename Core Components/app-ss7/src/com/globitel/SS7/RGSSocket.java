package com.globitel.SS7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import com.globitel.Application.Application;
import com.globitel.Application.RGS_Configuration;
import com.globitel.SS7.codec.Message;
import com.globitel.SS7.codec.Tids;

public class RGSSocket extends Thread {
	public static final byte message_heartbeat = 0x00;
	public static final byte message_Received = 0x03;
	public static final byte message_registration_failed = 0x06;
	public static final byte message_send_response_message = 0x05;
	public static final byte message_instant_message = 0x07;
	public DataInputStream in;
	private DataOutputStream out;
	private Socket sock;
	private boolean connected = false;
	String address;
	String rgsPort;
	private RequestHandler rgsInstance;
	private boolean registrationEnabled;
	private boolean flushEnabled;
	private int rgsIndex;
	private List<byte[]> registrationMessages;

	public RGSSocket(boolean _flushEnabled, RequestHandler _rgsInstance, RGS_Configuration RGS_Object, int rgsIndex) {
		rgsInstance = _rgsInstance;
		address = RGS_Object.getAddress(rgsIndex);
		rgsPort = RGS_Object.getPort(rgsIndex);
		registrationEnabled = RGS_Object.sendRegistrationMessage();
		registrationMessages = RGS_Object.registrationMessages;
		flushEnabled = _flushEnabled;
		this.rgsIndex = rgsIndex;
	}

	public boolean connect() {
		try {
			logInfo("RequestHandler::connect: connecting to " + getRGS_NAME() + ", address: " + address + ", Port: "
					+ rgsPort);
			sock = new Socket(address, Integer.parseInt(rgsPort));
			if (sock.isConnected()) {
				connected = true;
				in = new DataInputStream(sock.getInputStream());
				out = new DataOutputStream(sock.getOutputStream());
				logInfo("RequestHandler::connect: Connected To " + getRGS_NAME() + " Successfully.");
				SendRegistrationMessages();
			}
		} catch (Exception e) {
			logError("RequestHandler::connect: Connection refused to " + getRGS_NAME() + ".");
			connected = false;
		}
		return connected;
	}

	public synchronized void write(byte[] data, int i, int length) {
		try {

			out.write(data, i, length);
			if (flushEnabled) {
				out.flush();
			}
		} catch (Exception e) {
			logError("Exception, " + getRGS_NAME() + " Requesthandler::write:" + e.getMessage());
		}
	}

	private synchronized void AddToWriter(byte[] buffer) {
		write(buffer, 0, buffer.length);
	}

	public byte[] getBody(byte[] data) throws IOException {
		int bodyLength = (data[7] << 8) + (data[6] & 0xff);
		byte[] body = new byte[bodyLength];
		in.read(body, 0, body.length);
		return body;
	}

	public int getParameters(byte[] body) {
		int index = 3;
		byte dialogOperation = body[index];
		index += 4;
		byte applicationContext = body[index];
		logDebug("New Message Received, Application Context: " + applicationContext + ", Dialogue Operation: "
				+ dialogOperation);
		index += 4;
		return index;
	}

	public void SendRegistrationMessages() {
		if (isRegistrationEnabled()) {
			Iterator<byte[]> iter = registrationMessages.iterator();
			while (iter.hasNext()) {
				byte[] tmp = iter.next();
				registerMessage(tmp[0], tmp[1], tmp[2]);
			}
			logInfo("Registration messages sent to " + getRGS_NAME(), null, null);
		}
	}

	private boolean isRegistrationEnabled() {
		// TODO Auto-generated method stub
		return registrationEnabled;
	}

	public void registerMessage(byte operation, byte context, byte mode) {
		// sending registration messages
		byte[] registrationMsg = new byte[] { 0x02, 0x00, 0x00, 0x00, 0x00, 0x03, 0x0C, 0x00, 0x01, 0x00, 0x01,
				operation, 0x02, 0x00, 0x01, context, 0x03, 0x00, 0x01, mode };
		AddToWriter(registrationMsg);
	}

	public void run() {
		connect();
		byte[] messageBody = null;
		byte[] header = new byte[8];
		int begin_of_messageContent_index;
		byte[] response_heartbeat = new byte[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		Tids tids = new Tids();
		byte[] Message_ignore = new byte[] { 0x08, header[1], header[2], header[3], header[4], 0x00, 0x00, 0x00 };

		byte[] Message_Ref = new byte[4];

		while (!(Thread.interrupted())) {
			int readBytes = 0;
			try {
				readBytes = in.read(header, 0, header.length);
			} catch (Exception e) {
				logError("RequestHandler::run: Failed reading Socket, " + getRGS_NAME() + " Connection Failure.");
				connect();
				continue;
			}
			if (readBytes != -1) {
				switch (header[0]) {
				case message_heartbeat: {
					AddToWriter(response_heartbeat);
					logInfo("Heartbeat request received...response sent to " + getRGS_NAME(), null, null);
				}
					break;
				case message_Received: {
					try {
						Message_ignore[1] = header[1];
						Message_ignore[2] = header[2];
						Message_ignore[3] = header[3];
						Message_ignore[4] = header[4];
						Message_Ref = setMessageReference(header);

						messageBody = getBody(header);

						if (messageBody[5] == 0x0a && Application.ignoreUDTS_Enabled) // Ignore UDTS Messages
						{
							// send ignore message
							AddToWriter(Message_ignore);
							break;
						}
						begin_of_messageContent_index = getParameters(messageBody);
						// getting message dtid to get Thread Id that should be sent to.
						tids.GetTids(messageBody, begin_of_messageContent_index);
						logBuffer(messageBody, " Message received from " + getRGS_NAME());

						rgsInstance.receivedNewMessage(messageBody, Message_Ref, begin_of_messageContent_index, tids,
								rgsIndex);
					} catch (Exception e) {
						logError("Exception, " + getRGS_NAME() + " Connection Failure: " + e.getMessage());
						connect();
						continue;
					}
				}
					break;
				case message_registration_failed: // Registration Failed
				{ // Message
					// RGS to Client
					try {
						messageBody = getBody(header);
					} catch (IOException e) {
						logError(getRGS_NAME() + " Connection Failure.");
						connect();
						continue;
					}
					logError("Registration Message Failed to " + getRGS_NAME());
				}
					break;
				default:
					logError("Unknown Message Id recieved from " + getRGS_NAME());
					break;
				}
			}
		}
	}

	private byte[] setMessageReference(byte[] header) {
		// TODO Auto-generated method stub
		byte[] msgRef = new byte[4];
		msgRef[0] = header[1];
		msgRef[1] = header[2];
		msgRef[2] = header[3];
		msgRef[3] = header[4];
		return msgRef;
	}

	public void setRegistrationMessage(List<byte[]> registrationMessage) {
		this.registrationMessages = registrationMessage;
	}

	private void logBuffer(byte[] buffer, String txt) {
		Message.logBuffer(buffer, txt);
	}

	private void logError(String txt) {
		Message.logError(txt);
	}

	private void logDebug(String txt) {
		Message.logDebug(txt);
	}

	private void logInfo(String txt, Message message, String sessionInfo) {
		Message.logInfo(txt, message, sessionInfo);
	}

	private void logInfo(String txt) {
		Message.logInfo(txt);
	}

	public String getRGS_NAME() {
		return this.getName();
	}
};