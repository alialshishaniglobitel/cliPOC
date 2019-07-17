package com.globitel.sctp.common;

import java.nio.ByteBuffer;

public class Utility
{
	public static boolean isWatchDogOrCapabilityExchange(byte[] data)
	{
		int loc = 0;
		loc++; // JUMP TO LENGTH
		loc += 3; // SKIP LENGTH
		loc++; // JUMP TO COMMAND CODE //CODE TO EXTRACT THE
		int commandCode = GetBigEndian(data, loc, 3); // COMMAND
				
		if (commandCode == 257 || commandCode == 280)
		{
			return true;
		}
		
		return false;
	}

	public static int GetBigEndian(byte[] data, int start, int length)
	{
		if (length > 8)
		{
			// printf( "Length is %ld\n", length );
			return 1;
		}
		int value = 0;
		char i = 0;
		while (i < length)
		{
			value = value << 8;
			value += data[start + (i++)] & 0xff;
		}
		return value;
	}

	public static int GetBigEndian(ByteBuffer cached_buffer, int start, int length)
	{
		int value = 0;
		for(int index = 0; index< length; start++, index++)
		{
			value = value << 8;
			value += cached_buffer.get(start) & 0xff;
		}
		return value;
	}
}
