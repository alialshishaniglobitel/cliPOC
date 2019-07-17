package com.globitel.diameterCodec.interfaces;

import com.globitel.diameterCodec.Diameter.DiameterMessage;

public interface IMessagePrinter
{
	void endPcap(String out, DiameterMessage msg, boolean removeFile);

	boolean write(String name, DiameterMessage msg);
	
	boolean freeResources();

	String getFilePath();
}
