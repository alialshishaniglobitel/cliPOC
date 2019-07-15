package com.globitel.m3ua.dialogic;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitive;

import com.dialogic.signaling.gct.GctLib;
import com.dialogic.signaling.gct.GctMsg;
import com.globitel.m3ua.commons.Application;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class DialogicWriter extends DialogicBase
{
	public static MessageLogger logger =  MyLoggerFactory.getDefaultLogger();
	
	private static ArrayBlockingQueue<Mtp3TransferPrimitive> m3uaReceivedMessages = new ArrayBlockingQueue<>( 2000, true );
	
	public static boolean AddM3UAMsg(Mtp3TransferPrimitive msg)
	{
		if(!m3uaReceivedMessages.offer(msg))
		{
			logger.error(String.format("Error in DialogicReader::AddM3UAMsg, m3uaReceivedMessages queue Size:%d", m3uaReceivedMessages.size()));
			return false;
		}
		return true;
	}

	@Override
	public void run() 
	{
		logger.info("Starting DialogicWriter::Run");
		
		Mtp3TransferPrimitive m3uaMsg;
		GctMsg txMsg = null;
		while(true)
		{
			try 
			{
				//Thread.sleep(1);
				m3uaMsg = m3uaReceivedMessages.take();
				if(null != m3uaMsg)
				{
					logger.debug("new m3ua message handled");
					byte[] mtp3Buffer = new byte[5];
					mtp3Buffer[0] = (byte) m3uaMsg.getSi();
					mtp3Buffer[1] = (byte) (m3uaMsg.getDpc() & 0xff);
					mtp3Buffer[2] = (byte) (((m3uaMsg.getDpc() & 0x3f00) >> 8) + ((m3uaMsg.getOpc() & 0x03) << 6));
					mtp3Buffer[3] = (byte) (m3uaMsg.getOpc() >> 2);
					mtp3Buffer[4] = (byte) ((byte) (m3uaMsg.getSls() << 4) + ((m3uaMsg.getOpc() >> 10) & 0x0f));
					
					byte[] msgBuffer = new byte[m3uaMsg.getData().length + 5];
					System.arraycopy(mtp3Buffer, 0, msgBuffer, 0, mtp3Buffer.length);
					System.arraycopy(m3uaMsg.getData(), 0, msgBuffer, mtp3Buffer.length, m3uaMsg.getData().length);
					
					txMsg = GctLib.getm(msgBuffer.length);
					logger.debug("after GctLib.getm");
					if(null != txMsg)
					{
						txMsg.setSrc((short) 0);
						txMsg.setDst(Application.WRITER_MODULE_ID);
						txMsg.setId(0);
						txMsg.setType(DIALOGIC_MESSAGE_TYPE_API_MSG_RX_IND);
						ByteBuffer bytebuffer = txMsg.getParam();
			            bytebuffer.put(msgBuffer);
			            bytebuffer.flip();
						
			            DisplayGCTMessage(txMsg, msgBuffer, "I");	//This is an Incoming message that will be raised to the RGS
						try
						{
							GctLib.send(Application.WRITER_MODULE_ID, txMsg);
							logger.debug("Message sent successfully to RGS");
						}
						catch(Exception e)
						{
							logger.error("Exception in DialogicWriter::Run, sending failed, Ex:" + e.toString());
							GctLib.relm(txMsg);
						}
					}
				}
			}
			catch(Exception e)
			{
				logger.error("Exception in DialogicWriter::Run, Ex:" + e.toString());
			}
		}
	}
}
