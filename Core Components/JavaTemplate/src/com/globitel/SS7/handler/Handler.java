package com.globitel.SS7.handler;

import com.globitel.SS7.codec.Message;

public abstract class Handler
{
	public abstract void handleNewMessage(Message message);

}
