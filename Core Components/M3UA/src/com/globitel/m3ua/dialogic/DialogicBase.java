package com.globitel.m3ua.dialogic;

import com.dialogic.signaling.gct.GctMsg;
import com.globitel.m3ua.commons.Utility;
import com.globitel.utilities.commons.logger.MessageLogger;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public abstract class DialogicBase implements Runnable
{
	public static MessageLogger logger =  MyLoggerFactory.getDefaultLogger();
	
	protected static final int DIALOGIC_MESSAGE_TYPE_API_MSG_TX_REQ = 0xcf00;
    protected static final int DIALOGIC_MESSAGE_TYPE_API_MSG_RX_IND = 0x8f01;
    
    
    public DialogicBase()
	{
		new Thread(this).start();
	}
    
    
	protected void DisplayGCTMessage(GctMsg gctMsg, byte[] buffer, String Prefix)
	{
		try 
		{
			String log = String.format("M-%s r%04x-t%04x-i%04x-f%02x-d%02x-s%02x-p(%02x)%s",
										Prefix, 0, gctMsg.getType(), gctMsg.getId(), gctMsg.getSrc(), 
										gctMsg.getDst(), gctMsg.getStatus(), buffer.length, Utility.getHexString(buffer));
			logger.debug(log);
		}
		catch (Exception e) 
		{
			logger.error("Exception in DisplayGCTMessage, Ex:" + e.toString());
			e.printStackTrace();
		}
	}
}
