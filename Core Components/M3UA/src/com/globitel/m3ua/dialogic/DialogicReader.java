package com.globitel.m3ua.dialogic;

import java.util.Arrays;

import com.dialogic.signaling.gct.GctLib;
import com.dialogic.signaling.gct.GctMsg;
import com.globitel.m3ua.commons.Application;
import com.globitel.m3ua.commons.Definitions.MTP3Data;
import com.globitel.m3ua.commons.Utility;

public class DialogicReader extends DialogicBase
{
	@Override
	public void run() 
	{
		logger.info("Starting DialogicReader::Run");
		//int counter = 0;
		while(true)
		{
			try
			{
				while(!Application.M3UA.isAppServersUp())
				{
					/*
					if(10 == ++counter) 
					{
						counter = 0;
						logger.info("app servers down");
					}*/
					Thread.sleep(10);
				}
				//logger.info("app servers up");
				GctMsg rxMsg = GctLib.grab(Application.READER_MODULE_ID);
				if(null != rxMsg)
				{
					logger.info("New message read from Dialogic Module");
					
					byte[] msgBuffer = new byte[rxMsg.getParam().remaining()];
					rxMsg.getParam().get(msgBuffer);
					
					DisplayGCTMessage(rxMsg, msgBuffer, "O");	//This is an RGS message and so it is an Output message.
					
					byte[] mtp3Buffer = Arrays.copyOfRange(msgBuffer, 0, 5);
					MTP3Data mtp3Data = Utility.decodeMTP3(mtp3Buffer);
					
					byte[] sccpBuffer = Arrays.copyOfRange(msgBuffer, 5, msgBuffer.length);
					
					Application.M3UA.sendM3UAMsg(mtp3Data, sccpBuffer);
					logger.debug("Before GctLib.relm");
					GctLib.relm(rxMsg);
					logger.debug("After GctLib.relm");
				} else {
					logger.debug("waiting to read message ... ");
					Thread.sleep(10);
				}
			}
			catch(Exception e)
			{
				logger.error("Exception in DialogicReader::Run, Ex:" + e.toString());
			}
		}
	}

	
	
	

}
