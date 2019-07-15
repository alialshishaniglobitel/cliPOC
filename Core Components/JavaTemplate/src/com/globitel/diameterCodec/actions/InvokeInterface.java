package com.globitel.diameterCodec.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.globitel.diameterCodec.format.RuleFactory;
import com.globitel.diameterCodec.format.WebRuleFactory;

public interface InvokeInterface
{
	RuleFactory factory = new RuleFactory();
	String invoke(Object... args) throws Exception;
	default void removeLastItemIfEqualsAvpCode(List<Integer> avpPath, int code)
	{
		Integer obj = null;
		if ( avpPath != null && (obj  = avpPath.get(avpPath.size()-1)) == code )
		{
			avpPath.remove(obj);
		}
	}
	default List<Integer> objToIntList(Object obj)
	{
		String avpList = (String)obj;
		if ( avpList.equals("") ||  avpList.equals("\"\""))
			return null;
		
		List<Integer> avpPath = Arrays.stream(avpList.split(WebRuleFactory.SEPARATOR)).map(Integer::parseInt).collect(Collectors.toList());
		return avpPath;
	}
	default int objToInt(Object obj)
	{
		return Integer.parseInt((String) obj);
	}
	default boolean objToBool(Object obj)
	{
		return Boolean.parseBoolean((String) obj);
	}
	default String objToStr(Object obj)
	{
		return (String)obj;
	}
//	default String fixStringParam(String str)
//	{
//		if ( str.contains("(") || str.contains(")"))
//		{
//			return str;
//		}
//		return "\""+str+"\"";
//	}
}
