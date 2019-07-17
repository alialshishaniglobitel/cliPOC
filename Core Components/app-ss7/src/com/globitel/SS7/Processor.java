package com.globitel.SS7;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.globitel.Application.Application;
import com.globitel.Application.SessionManagerImp;
import com.globitel.Logic.SS7Manager;
import com.globitel.SS7.codec.Message;
import com.globitel.SS7.codec.Tids;
import com.globitel.SS7.handler.Handler;
import com.globitel.common.structure.Enumerations.SS7_DECODE_TYPE;
import com.globitel.common.utils.Common;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class Processor extends Thread {
	public ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
	private boolean valid;
	public int processorId;
	public Handler handler;
	public boolean idpProc;

	public Processor(int processorID, boolean _procType, SessionManagerImp sessionManager) {
		handler = new SS7Manager(processorID, sessionManager);
		this.processorId = processorID;
		this.idpProc = _procType;
	}

	public void copySS7Buffer(byte[] pcBufferStart, byte[] msgRef, int iStartIndex, int rgsIndex) {
		// copying ss7 message and assigning it to RGSMessage and adding it

		// RGSMEssage Queue
		Message msg = new Message(rgsIndex, msgRef);
		msg.rawData = Arrays.copyOfRange(pcBufferStart, 0, pcBufferStart.length);
		msg.rawDataSS7Index = iStartIndex;
		msg.rawSS7Length = pcBufferStart.length - iStartIndex;
		msg.rawLength = pcBufferStart.length;
		msg.setTimeStamp();
		queue.add(msg);
		Message.logInfo(String.format("Message Ref [%s] added to processor queue, current size %d.",
				Common.byteArrayToString(msgRef, msgRef.length), queue.size()));
	}

	public void run() {
		// polling an RGSMessage From Queue and passing it the the Decoder and then to
		// the LogicHandler
		valid = true;
		Tids tids = new Tids();
		while (this.valid) {
			Message msg = null;
			// this code is aiming to limit the content Size of Queue to Configured Value
			// "queuelimit"
			do {
				msg = queue.poll();

				if (msg != null && idpProc == true && (queue.size() > RequestHandler.queuelimit
						|| msg.getTimeDifference() > Application.RequestTimeOut)) {
					tids.GetTids(msg.rawData, msg.rawDataSS7Index);
					MyLoggerFactory.getInstance().getAppLogger().error(String.format(
							"Dropping Message, OTID: %s, DTID : %s, Cause: Message Queue Size: %d, Configured Queue Limit: %d.",
							Common.byteArrayToString(tids.otid, tids.otid.length),
							Common.byteArrayToString(tids.dtid, tids.dtid.length), queue.size(),
							RequestHandler.queuelimit));

					// Just To Report in CDR
					msg.decode();
					msg.setSS7DecodeType(SS7_DECODE_TYPE.PARSE_ONLY);
					RequestHandler
							.logInfo("Message: " + Common.byteArrayToString(msg.getOtid()) + " Decoded successfully.");
					handler.handleNewMessage(msg);
				}
				if (msg == null) {
					// Message.logInfo(String.format("Message Looping without entering."));
					break;
				}
			} while (idpProc == true && queue.size() > RequestHandler.queuelimit);

			try {
				if (msg != null) {

					tids.GetTids(msg.rawData, msg.rawDataSS7Index);
					String otid = Common.byteArrayToString(tids.otid, tids.otid.length);
					String dtid = Common.byteArrayToString(tids.dtid, tids.dtid.length);
					String msgRef = Common.byteArrayToString(msg.msgRef, msg.msgRef.length);
					Message.logInfo("New Message got from Processor ID: [" + processorId + "], Msg Ref: [" + msgRef
							+ "], OTID: [" + otid + "], DTID: [" + dtid + "] and current Queue Size: [" + queue.size()
							+ "]");
					if (idpProc == true // if IDP thread
							&& msg.getTimeDifference() > Application.RequestTimeOut) // and message age is more that
																						// three seconds
					{
						Message.logError(String.format(
								"Dropping Message, OTID: [%s], DTID : [%s], Cause: Message Exceed Timeout in the queue[%d].",
								otid, dtid, msg.getTimeDifference()));
						msg.decode();
						msg.setSS7DecodeType(SS7_DECODE_TYPE.PARSE_ONLY);
						RequestHandler.logInfo(
								"Message Reference :[" + msgRef + "], OTID :[" + otid + "] Decoded successfully.");
						handler.handleNewMessage(msg);
						continue;
					}
					if (RequestHandler.deocdeMsgsImmediately == true) {
						msg.decode();
					}
					msg.setSS7DecodeType(SS7_DECODE_TYPE.FULL);
					RequestHandler
							.logInfo("Message Reference :[" + msgRef + "], OTID :[" + otid + "] Decoded successfully.");
					handler.handleNewMessage(msg);
				} else {
					try {
						sleep(1);
					} catch (InterruptedException e) {
						RequestHandler.logError("Exception, Processor::run::InterruptedException:" + e.getMessage());
					}
				}
			} catch (Exception e) {
				MyLoggerFactory.getInstance().getAppLogger().error("Exception, " + e.getMessage() + ",  Message: "
						+ Common.byteArrayToString(tids.otid, tids.otid.length) + ", Stopped at index "
						+ msg.rawDecodingIndex, e);
			}
		}
	}
}
