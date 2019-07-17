package com.globitel.common.utils;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import com.globitel.utilities.commons.ConfigurationManager;


public class Common
{
	public static String toString(Element node) {
		try {

			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

			transformer.transform(new DOMSource(node), new StreamResult(sw));
			return sw.toString();

		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

	public static int SetBigEndian(byte[] dst, int src, int length, int startIndex)
	{
		if ( length == 0 )
		{
			if ( ((src) >> 8) == 0 )
				length = 1;
			else if ( ((src) >> 16) == 0 )
				length = 2;
			else if ( ((src) >> 24) == 0 )
				length = 3;
			else
				length = 4;
		}

		int x = 0x000000FF;

		int k = length-1;
		int j = 0;
		while( k > -1 )
		{
			// casting to uchar will always yeild to zero here!
			dst[startIndex+j++] = (byte) (((src) & (x << (k*8))) >> (k*8));
			k-- ;
		}
		return length;
	}

	public static byte[] integerToByteArray(int src) {
		// TODO Auto-generated method stub
		int length = 0;
		if ( ((src&0xff) >> 8) == 0 )
			length = 1;
		else if ( ((src&0xff) >> 16) == 0 )
			length = 2;
		else if ( ((src&0xff) >> 24) == 0 )
			length = 3;
		else
			length = 4;

		long x = 0x000000FF;

		int shift =1;
		int k = length-1;
		int j = shift;

		byte[] dst = null;


		dst = new byte[length+shift];

		while( k > -1 )
		{
			// casting to uchar will always yeild to zero here!
			dst[j++] = (byte) (((src) & (x << (k*8))) >> (k*8));
			k-- ;
		}

		dst[0] = (byte) (0x80 | length);

		return dst;
	}
	
	public static String[] tokenize(String noReplay_CF)
	{
		String[] splittedItems = noReplay_CF.split(",");
		return splittedItems;
	}

	public static int[] tokenizeInteger(String noReplay_CF)
	{
		String[] splittedItems = noReplay_CF.split(",");
		int[]    splittedIntegerItems = new int[splittedItems.length];
		int index = 0;
		for(String splittedItem : splittedItems)
		{
			splittedIntegerItems[index++] = Integer.parseInt(splittedItem);
		}
		return splittedIntegerItems;
	}

	public static int checkLengthType(byte[] array, int tcapTagIndex)
	{
		if ((array[tcapTagIndex] & 0xff) == 0x81)
		{
			tcapTagIndex += 2;
		}
		else
		{
			tcapTagIndex += 1;
		}
		return tcapTagIndex;
	}

	public static byte[] hexStringToByteArray(String src)
	{
		if(src.length() %2 == 1)
		{
			src += "f";
		}
		return DatatypeConverter.parseHexBinary(src);
	}
	
	public static String flipByte (String src)
	{
		byte[] srcBytes = hexStringToByteArray(src);
		byte[] destBytes = new byte[srcBytes.length];
		for (int i = 0; i < srcBytes.length; i++) {
			destBytes[i] = (byte)(((srcBytes[i] << 4) & 0xF0) + ((srcBytes[i] >> 4) & 0x0F));
		}
		
		return convertByteArrayToString(destBytes);
	}

	public static byte[] getSubByteArray(byte[] array, int from, int to)
	{
		byte[] returnArray = Arrays.copyOfRange(array, from, to);
		return returnArray;
	}
	
	public static String byteArrayToString(byte[] src)
	{
		return byteArrayToString(src, 0, src == null? 0 : src.length);
	}

	public static String byteArrayToString(byte[] src, int start, int len)
	{
		if(src == null)
		{
			return null;
		}		
		String res = "";
		for( int i = start; i < start + len; i++)
		{
			res += String.format("%02x", src[i]);
		}
		return res;
	}
	
	public static String byteArrayToString(byte[] src, int len)
	{
		return byteArrayToString(src, 0, len);
	}
	
	@Deprecated
	public static String TakeByteDigitsGetString(byte[] src, int n)
	{
		String dst = "";
		if(src == null)
		{
			dst = "00000000";
		}
		else
		{
			for (int i = 0; i < n; ++i)
			{
				dst += String.format("%02x", src[i]);
			}
		}
		return dst;
	}

	public static int getInteger(byte[] src, int start, int size )
	{
		int value = 0;
		int ii = 0;
		while (ii < size) 
		{
			value = value << 8;
			value |= (byte) src[start + ii] & 0xff;
			ii++;
		}
		return value;
	}
	
	public static Long byteArrayToLong(byte[] bytes)
	{
		return (long) (bytes[0] << 24 | (bytes[1] & 0xFF) << 16
				| (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF));
	}

	public static byte[] longToByteArray(long input)
	{
		
		int serverId = ConfigurationManager.getInstance().getIntValue("ServerID");
		
		byte[] bytes = new byte[4];
		
		if (serverId != -1) {
			bytes[0] = (byte) serverId;			
		} else {
			bytes[0] = (byte) ((input & 0xFF000000) >> 24);
		}
		bytes[1] = (byte) ((input & 0x00FF0000) >> 16);
		bytes[2] = (byte) ((input & 0x0000FF00) >> 8);
		bytes[3] = (byte) ((input & 0x000000FF));
		return bytes;
	}
	
	public static String convertByteArrayToString(byte[] array)
	{
		if(array == null)
		{
			return "NULL";
		}
		String str = "";
		for(int index = 0 ; index < array.length ; index++)
		{
			str += String.format("%02x", array[index]);
		}
		return str;
	}

	public static String convertJavaDateToMySQLDate(Date date)
	{
		if(date == null)
		{
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static Date getDate(String time, String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = df.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String getTimeOffset() 
	{
		// TODO Auto-generated method stub
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getRawOffset();
		return String.format("%s%02d%02d", offset >= 0 ? "+" : "-", offset / 3600000, (offset / 60000) % 60);
	}

	public static String getTime(Date date, String dateFormat) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	public static String getParameter(String regex, String parameter)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(parameter);
		if(matcher.find())
		{
			parameter =	matcher.group(1);
		}
		else
		{
			parameter = null;
		}
		return parameter;
	}

	public static int[] stringArrayToIntegerArray(String[] s) 
	{
		if(s[0].isEmpty())
		{
			return null;
		}
		int[] result = new int[s.length];
		for (int i = 0; i < s.length; i++) 
		{
			result[i] = Integer.parseInt(s[i]);
		}
		return result;
	}

	public static String getTime(LocalDate localDate, String dateFormat)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return localDate.format(formatter);
	}
}
