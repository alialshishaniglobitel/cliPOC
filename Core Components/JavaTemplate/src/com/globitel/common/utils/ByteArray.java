package com.globitel.common.utils;

import java.io.IOException;



import org.apache.commons.lang3.ArrayUtils;

import com.globitel.SS7.codec.Message;

public class ByteArray
{
	public static class Index
	{
		public int offset;
		public int componentLengthOffset;
		public int ArgumentsOffset;

		public Index()
		{
			offset = 0;
			componentLengthOffset = 0;
		}
	}
	public Index msg_index;
	public byte[] buffer;
	public int index;
	private int blockSize = 500;
	public String msgKey = "";

	public ByteArray()
	{
		reset();
	}

	public int getSize()
	{
		return index;
	}

	public byte[] toByteArray()
	{
		byte[] data = new byte[getSize()];
		int destpos = 0;
		System.arraycopy(buffer, 0, data, destpos, index);
		return data;
	}

	public void resetIndex()
	{
		msg_index.componentLengthOffset = 0;
	}

	public void reset()
	{
		buffer = null;
		msg_index = null;
		
		buffer = new byte[blockSize];
		msg_index = new Index();
		
		msg_index.offset = 0;
		msg_index.componentLengthOffset = 0;
		index = 0;
	}

	public void write(byte[] data, int offset, int length) throws IOException
	{
		if (data == null)
		{
			throw new NullPointerException();
		}
		else if ((offset < 0) || ((index + length) > blockSize) || (length < 0))
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			System.arraycopy(data, 0, buffer, offset, length);
			if (offset + length > index)
			{
				index += length;
			}
		}
	}

	public void write(byte data) throws IOException
	{
		int writeIndex = msg_index.offset;
		if (writeIndex > blockSize)
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			buffer[writeIndex] = data;
			msg_index.offset++;
			index++;
		}
	}
	public void writeArrayWithStartIndex(byte[] data, int dstStart) throws IOException
	{
		int writeIndex = msg_index.offset;
		if (writeIndex > blockSize)
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			int lengthToWrite = data.length-dstStart;
			System.arraycopy(data, dstStart, buffer, writeIndex, lengthToWrite);
			msg_index.offset+=lengthToWrite;
			index+=lengthToWrite;
		}
	}
	public void writeLengthByte( byte length, int index)
	{
		// TODO Auto-generated method stub
		try {
			if ((length&0xff) >= 0x80)
			{
				byte[] lenInBytes = Common.integerToByteArray(length);
				write(lenInBytes[0], index);
				index++;
				for( int i = 1; i < lenInBytes.length; i++)
				{
					buffer = ArrayUtils.add(buffer, index, lenInBytes[i]);
					this.index++;
					this.msg_index.offset++;
				}
			}
			else
			{
				write((byte) length, index);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void write(byte data, int writeIndex) throws IOException
	{
		if (writeIndex > blockSize)
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			buffer[writeIndex] = data;
			index++;
		}
	}	

	public void write(byte[] array)
	{
		try
		{
			write(array, msg_index.offset, array.length);
			msg_index.offset += array.length;
		}
		catch (Exception e)
		{
			Message.logError("Exception, ByteArray.write: Error writing array" + e.getMessage());
		}
	}
}
