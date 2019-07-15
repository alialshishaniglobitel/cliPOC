package com.globitel.diameterCodec.Diameter;

import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Util
{
	public static byte[] TakeStringDigitsGetBytes(int index, String _Src)
	{
		int i = 0, j = 0;
		byte[] _Dst = new byte[_Src.length() / 2 + _Src.length() % 2];
		String digit = "";
		digit = "";
		if (_Src.length() % 2 != 0)
		{
			_Src = _Src + '0';
		}
		
		BigInteger temp;
		while (j < _Src.length())
		{
			digit = ""; 
			digit += _Src.charAt(j);
			temp = new BigInteger(digit, 16);
			_Dst[index + i] = (byte) (temp.byteValue() << 4);

			digit = "";
			digit += _Src.charAt(j + 1);
			temp = new BigInteger(digit, 16);
			_Dst[index + i] += temp.byteValue();
			++i;
			j += 2;
		}
		return _Dst;
	}

	public static void setSignedLittleIndian(int value, byte[] data, int pucCurrentLocation)
	{
	   int c_uiLength = 4;
		   
	   int i = 0;
	   while( i < c_uiLength )
	   {
		   data[pucCurrentLocation+c_uiLength-i-1] = (byte)value;
	      value = value >> 8 ;
	      i++;
	   }
	}
	protected static void setSignedLittleIndian64(long m_llAVPValue2, byte[] data, int pucCurrentLocation)
	{
		throw new NotImplementedException();
	}

	protected static long GetSignedBigEndian64(byte[] pucValue, int index, int c_iLength)
	{
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	static void setLittleIndian16(short c_uiInput, int c_uiLength, byte[] pucOutData, int outIndex)
	{
		// now we have a big indian integer we need to convert it to little with
		// spec len
		short value = c_uiInput;
		char i = 0;
		while (i < c_uiLength)
		{
			pucOutData[outIndex + c_uiLength - i - 1] = (byte) value;
			value = (short) (value >> 8);
			i++;
		}
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
	static void setLittleIndian(int c_uiInput, int c_uiLength, byte[] pucOutData, int outIndex)
	{
		// now we have a big indian integer we need to convert it to little with
		// spec len
		int value = c_uiInput;
		char i = 0;
		while (i < c_uiLength)
		{
			pucOutData[outIndex + c_uiLength - i - 1] = (byte) value;
			value = value >> 8;
			i++;
		}
	}
	public static byte[] getBytesFromHex(String avp_replacement_value) throws Exception
	{
		return toByteArray(avp_replacement_value);
	}
	
	public static byte[] toByteArray(String s) {
	    return DatatypeConverter.parseHexBinary(s);
	}
	public static void setLittleIndian64(long longValue, int length, byte[] data, int outIndex)
	{
		// TODO Auto-generated method stub
		long value = longValue;
		char i = 0;
		while (i < length)
		{
			data[outIndex + length - i - 1] = (byte) value;
			value = value >> 8;
			i++;
		}
	}
}
