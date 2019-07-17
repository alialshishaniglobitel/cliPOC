package com.globitel.Diameter;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.codec.binary.Hex;
import org.mobicents.protocols.api.Association;

import com.globitel.diameterCodec.Diameter.DiameterMessage;
import com.globitel.sctp.common.IMessageCallBack;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class MessageBuffers extends Thread implements IMessageCallBack
{
	ConcurrentLinkedQueue<MessageBean> queue;
	private int numberOfThreads;
	private boolean running = true;
	
	public class MessageBean
	{
		public DiameterMessage msg;
		public int payloadProtocolId;
		public Association assoc;
	}
	public MessageBuffers(int _numberOfThreads)
	{
		numberOfThreads = _numberOfThreads;
		queue = new ConcurrentLinkedQueue<>();
	}
	
	public void end()
	{
		running = false;
	}
	
	@Override
	public void handleNewMessage(int payloadProtocolId, Association assoc, byte[] bfr_)
	{
		MyLoggerFactory.getInstance().getAppLogger().debug(assoc.getName() + ", New msg " + Hex.encodeHexString(bfr_) + ", before adding to buffer, current size:" + queue.size());
		MessageBean bean = new MessageBean();
		bean.msg = new DiameterMessage(bfr_);
		bean.payloadProtocolId = payloadProtocolId;
		bean.assoc = assoc;
		queue.add(bean);
		MyLoggerFactory.getInstance().getAppLogger().debug(assoc.getName() + ", New msg " + Hex.encodeHexString(bfr_) + ", added to buffer, current size:" + queue.size());
	}
	
	public MessageBean getMsg()
	{
		// TODO Auto-generated method stub
		//logger.debug("Accessing Buffer, read_index:"+read_index);
		MessageBean poll = queue.poll();
		return poll;
	}
	@Override
	public void run()
	{
		while(running )
		{
			MyLoggerFactory.getInstance().getAppLogger().info("MessageBuffer size is: "+queue.size());
			try
			{
				Thread.sleep(20000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
