package com.globitel.SS7;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.globitel.Application.RGS_Configuration;
import com.globitel.Application.SessionPool;
import com.globitel.SS7.codec.Message;
import com.globitel.SS7.codec.TCAP;
import com.globitel.SS7.codec.Tids;
import com.globitel.common.utils.ByteArray;
import com.globitel.common.utils.Common;

public class RequestHandler {
	private static int NUMBER_OF_THREADS;
	private static int roundRobinCounter = 0;

	private static RGS_Configuration _Active_RGS_Object = null;

	public Processor[] sigProcessors;
	RGSSocket[] _Active_Socket;

	private static SocketWriter[] _Active_Socket_Writer;

	RGSSocket[] _Passive_Socket;
	RGSSocket[] _Cycle_Socket;

	public static int queuelimit;
	public static boolean deocdeMsgsImmediately;

	public static void initializeRGS(String[] _Active_RGS_Config) {
		byte[][] _ActiveRegistrationMessages;
		_Active_RGS_Object = decodeRGS_Config(_Active_RGS_Config);
		_ActiveRegistrationMessages = new byte[][] { { TCAP.Begin, (byte) 0x20, 0x01 },
				{ TCAP.Begin, (byte) 0x01, 0x01 }, { TCAP.End, 0x25, 0x01 }, { TCAP.End, 0x26, 0x01 } };

		add_Registration_Messages(_Active_RGS_Object, _ActiveRegistrationMessages);

	}

	private static RGS_Configuration decodeRGS_Config(String[] _RGS_Config) {
		String[] address = new String[_RGS_Config.length];
		String[] port = new String[_RGS_Config.length];
		for (int i = 0; i < _RGS_Config.length; i++) {
			Pattern pattern = Pattern.compile("(.+):(.+)");
			Matcher matcher = pattern.matcher(_RGS_Config[i]);
			if (matcher.find()) {
				address[i] = matcher.group(1);
				port[i] = matcher.group(2);
			}

		}
		return new RGS_Configuration(address, port);
	}

	public RequestHandler(boolean _flushEnabled, int numberOfThreads, boolean _deocdeMsgsImmediately, int queueLimit,
			int serverID) {
		deocdeMsgsImmediately = _deocdeMsgsImmediately;
		NUMBER_OF_THREADS = numberOfThreads;
		sigProcessors = new Processor[NUMBER_OF_THREADS];

		RequestHandler.queuelimit = queueLimit;

		for (int processorID = 0; processorID < NUMBER_OF_THREADS; ++processorID) {
			sigProcessors[processorID] = new Processor(processorID, false,
					SessionPool.getSessionManager(processorID));
			sigProcessors[processorID].start();
			sigProcessors[processorID].setName("Processor[" + processorID + "]");
		}

		_Active_Socket = new RGSSocket[_Active_RGS_Object.getNumberOfRGSs()];
		_Active_Socket_Writer = new SocketWriter[_Active_RGS_Object.getNumberOfRGSs()];
		for (int index = 0; index < _Active_RGS_Object.getNumberOfRGSs(); index++) {
			_Active_Socket[index] = new RGSSocket(_flushEnabled, this, _Active_RGS_Object, index);
			_Active_Socket[index].start();
			_Active_Socket[index].setName("ActiveRGS[" + index + "]");
			_Active_Socket_Writer[index] = new SocketWriter(_Active_Socket[index]);// here change constructor to take
																					// its own socket
			_Active_Socket_Writer[index].start();
			_Active_Socket_Writer[index].setName("SocketWriter[" + index + "]");
		}

	}

	public static void AddInstantMessage(ByteArray buffer, int rgsIndex) {
		logDebug("RequestHandler::AddInstantMessage: adding to writer(" + rgsIndex + ")");
		AddToWriter(buffer, rgsIndex);
		logDebug("RequestHandler::AddInstantMessage: added to writer(" + rgsIndex + ")");
	}

	public static void AddToWriter(ByteArray input, int rgsIndex) {
		logDebug("RequestHandler::AddToWriter: using socketWriter(" + rgsIndex + ")->'write'");
		_Active_Socket_Writer[rgsIndex].write(input);
		logDebug("RequestHandler::AddToWriter: socketWriter(" + rgsIndex + ") msg written");
	}
	
	private Processor selectProcessor(Tids tids) {
		int thread_id = 0;
		Processor processor = null;
		thread_id = (tids.otid[tids.otid.length - 1] & 0xff) % NUMBER_OF_THREADS;
		processor = sigProcessors[(int) (thread_id)];
		return processor;
	}	

	public static void logDebug(String string) {
		Message.logDebug(string);
	}

	public static void logError(String string) {
		Message.logError(string);
	}

	public static void logInfo(String string) {
		Message.logInfo(string);
	}

	public void receivedNewMessage(byte[] messageBody, byte[] msgRef, int begin_of_messageContent_index, Tids tids,
			int rgsIndex) {
		Processor processor = selectProcessor(tids);
		logInfo("OTID received [" + Common.byteArrayToString(tids.otid) + "], With Msg Refernce ["
				+ Common.byteArrayToString(msgRef, msgRef.length) + "].");
		if (processor != null) {
			processor.copySS7Buffer(messageBody, msgRef, begin_of_messageContent_index, rgsIndex);
		}
	}

	public static void logBuffer(byte[] rawData, String txt) {
		Message.logBuffer(rawData, txt);
	}

	public static void add_Registration_Messages(RGS_Configuration rgs_Object, byte[][] registrationMessage) {
		for (int k = 0; registrationMessage != null && k < registrationMessage.length; k++) {
			rgs_Object.registrationMessages.add(
					new byte[] { registrationMessage[k][0], registrationMessage[k][1], registrationMessage[k][2] });
		}
	}
}
