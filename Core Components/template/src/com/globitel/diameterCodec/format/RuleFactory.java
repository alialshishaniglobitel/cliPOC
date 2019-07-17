package com.globitel.diameterCodec.format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import com.globitel.diameterCodec.format.AvpTree.AvpCodeRelation;
import com.globitel.diameterCodec.interfaces.GroupedAvpTree;

public class RuleFactory
{
	AvpTree tree = null;
	public RuleFactory()
	{
		
	}
	public String getConditionSrcIP(String value)
	{
		return String.format("msg.getSrcIP().equals(\"%s\")", value);
	}
	public void setAvps(List<AvpCodeRelation> listOfAvps)
	{
		tree = new GroupedAvpTree();
		tree.addAvps(listOfAvps);
	}
	public void printGroupedAvps()
	{
		tree.print();
	}
	public RuleFactory(List<AvpCodeRelation> listOfAvps)
	{
		setAvps(listOfAvps);
	}
	private List<String> getCodeForCode(int code)
	{
		return tree.getLeafsList(code);
	}
	private String getConditionEqualsForGrouped(List<String> leafsList)
	{
		String result = "";
		for( String s : leafsList )
		{
			result += String.format("msg.getAVPById(Arrays.asList(%s)).getValue().equals", s);
			result += "(\"%s\") && ";
		}
		return result.substring(0, result.length()-4);
	}
	public String getConditionAvpAbsent(List<Integer> parents, int avpCode)
	{
		String result = getAvpPath(parents);
		result+= getConditionAvpGet(false, avpCode)+"==null";
		return result;
	}
	public String getConditionAvpPresent(int avpCode, boolean isGrouped)
	{
		if ( isGrouped )
		{
			List<String> leafsList = getCodeForCode(avpCode);
			String result = "";
			for(  int k = 0; k < leafsList.size(); k++)
			{
				String e = leafsList.get(k);
				if ( k > 0 )
					result += getAndOperator();
				int i = e.lastIndexOf(",");
				String parent = e.substring(0,i);
				String child = e.substring(i+1);
				result+=String.format("msg.getAVPById(Arrays.asList(%s,%s)) != null\n", parent, child );
			}
			return result;
		}
		else
		{
			return "msg."+getConditionAvpGet(isGrouped, avpCode)+" != null";
		}
	}
	public String getConditionStartsWith(List<Integer> parents, int code, String token)
	{
		String result = getAvpPath(parents);
		result+= String.format("getAVP(%d).getValue().startsWith(\"%s\")", code, token);
		return result;
	}
	public String getConditionEndsWith(List<Integer> parents, int code, String suffix)
	{
		String result = getAvpPath(parents);
		result+= String.format("getAVP(%d).getValue().endsWith(\"%s\")", code, suffix);
		return result;
	}
	private String getconditionIsWhiteListed(String string)
	{
		// TODO Auto-generated method stub
		return String.format("isWhiteListed(%s)", string);
	}
	public String getAndOperator()
	{
		return " && ";
	}
	public String getOrOperator()
	{
		return " || ";
	}
//	public String getConditionCallGetDecision()
//	{
//		return String.format(
//				"msg.calledITS();\n "
//				+ "return (null != (itsResult = getDecision(msg.getAVP(1).getValue()," 
//				+ "handleDigits(msg.getAVP(1407).getValue()), 100, 1, Integer.toHexString(msg.getEndToEndIdentifier()), 1)));\n"
//				);
//	}
	
	private String getconditionIsBlackListed(String string)
	{
		// TODO Auto-generated method stub
		return String.format("isBlackListed(%s)", string);
	}
	public String getConditionContainsForGrouped(int code){
		List<String> leafsList = getCodeForCode(code);
		return getConditionContainsForGrouped(leafsList);
	}
	private String getConditionContainsForGrouped(List<String> leafsList)
	{
		String result = "";
		for( String s : leafsList )
		{
			result += String.format("msg.getAVPById(Arrays.asList(%s)).getValue().contains", s);
			result += "(\"%s\") && ";
		}
		return result.substring(0, result.length()-4);
	}
	public String getConditionGenericForGrouped(int code, String methodText){

		List<String> leafsList = getCodeForCode(code);
		return getConditionGenericForGrouped(leafsList, methodText);
	}
	public List<String> getParentList(int code){

		List<String> leafsList = getCodeForCode(code);
		UnaryOperator<String> operator = new UnaryOperator<String>()
		{
			@Override
			public String apply(String t)
			{
				// TODO Auto-generated method stub
				return t.replace(","+code, "");
			}
		};
		leafsList.replaceAll(operator);;
		return leafsList;
	}
	public List<String> getParentList(){

		List<String> leafsList = getCodeForCode();
		return leafsList;
	}
	
	private List<String> getCodeForCode()
	{
		// TODO Auto-generated method stub
		return tree.getLeafsList();
	}
	private String getConditionGenericForGrouped(List<String> leafsList, String methodText)
	{
		String result = "";
		for( String s : leafsList )
		{
			result += String.format("msg.getAVPById(Arrays.asList(%s)).getValue().%s(\"\") && ", s, methodText);
		}
		return result.substring(0, result.length()-4);
	}
	public String getConditionEqualsForGrouped(int code){
		List<String> leafsList = getCodeForCode(code);
		return getConditionEqualsForGrouped(leafsList);
	}
	public final String getConditionAvpFlagsNoVendor(List<Integer> parents, int code, boolean isMandatory, boolean isProtected)
	{
		String line = getAvpPath(parents);
		line+= "getAVP("+code+")";
		return String.format("%s.isMandatory()==%b && %s.isProtected()==%b",
				line, isMandatory, line, isProtected);
	}
	public final String getConditionAvpFlagsWithVendor(List<Integer> parents, int code, boolean hasVendor, int vendorId,
			boolean isMandatory, boolean isProtected){
		String line = getAvpPath(parents);
		line+= "getAVP("+code+")";
		return String.format("%s.hasVendorID()==%b && %s.getVendorID()==%d && %s.isMandatory()==%b && %s.isProtected()==%b",
				line,hasVendor, line, vendorId, line, isMandatory, line, isProtected);
	}
	
	public final String getConditionAvpFlagsNoVendor(String line, boolean isMandatory, boolean isProtected)
	{
		return String.format("%s.isMandatory()==%b && %s.isProtected()==%b",
				line, isMandatory, line, isProtected);
	}
	public final String getConditionAvpFlagsWithVendor(String line, boolean hasVendor, int vendorId,
			boolean isMandatory, boolean isProtected){
		return String.format("%s.hasVendor()==%b && %s.getVendorID()==%s && %s.isMandatory()==%b && %s.isProtected()==%b",
				line,hasVendor, line, vendorId, line, isMandatory, line, isProtected);
	}
	
	private final String getConditionAvpGet(int code)
	{
		return String.format("getAVP(%d)", code );
	}
	private final String getConditionGAvpGet(int code)
	{
		return String.format("getGAVP(%d)", code );
	}
	public String getConditionGetGenericCondition(List<Integer> parents, int code, String condition, String val)
	{
		String result = getAvpPath(parents);
		result+= String.format("getAVP(%d).getValue().%s(\"%s\")", code,  condition, val);
		return result;
	}
	public final String getConditionApplicationIdAdd(int id){
		return String.format("msg.getAppID()==%d", id);
	}
	public final String getConditionCommandCodeADD(int code)
	{
		return String.format("msg.getCommandCode()==%d", code);
	}
//	public final String getActionAddAvp(int code)
//	{
//		return String.format("msg.addAVP(%d, 0, false, false, false, \"\");", code);
//	}
	public String getConditionAvpGet(boolean isGrouped, int code)
	{
		return isGrouped ? getConditionGAvpGet(code) : getConditionAvpGet(code);
	}
	private String getAvpPath(List<Integer> parents)
	{
		String result = "msg.";
		if (parents != null)
		{
			for (Integer x : parents)
			{
				result += String.format("getGAVP(%d).", x);
			}
		}
		return result;
	}
	public final String getConditionMessageType(boolean isIncoming, boolean isRequest)
	{
		return String.format("isIncoming(msg)==%b && msg.isRequest()==%b", isIncoming,
				isRequest);
	}
	public final String getActionAddAvp(List<Integer> parents, int code, int vendor,
			boolean hasVendor, boolean isMandatory, boolean isProtected, String value)
	{
		String result = getAvpPath(parents);
		result+= String.format("addAVP(%d, %d, %b, %b, %b, \"%s\");\n", code, vendor,
				hasVendor, isMandatory, isProtected, value);
		return result;
	}
	public final String getActionAddAvp(List<Integer> avpPath, int vendor,
			boolean hasVendor, boolean isMandatory, boolean isProtected, String value)
	{
		String result = getAvpPath(avpPath.subList(0, avpPath.size()-1));
		result+= String.format("addAVP(%d, %d, %b, %b, %b, \"%s\");\n", avpPath.get(avpPath.size()-1), vendor,
				hasVendor, isMandatory, isProtected, value);
		return result;
	}
	public final String getActionRouteToPeer(String dstIP){
		return String.format("msg.setDstIP(\"%s\");\n", dstIP);
	}
	public final String getActionRouteToRouteList(String dstRoute)
	{
		return String.format("msg.setDstRoute(\"%s\");\n", dstRoute);
	}
	public String getActionAvpReplaceValueInternal(List<Integer> parents, String string)
	{
		String result = getAvpPath(parents);
		result+= String.format("%s.set(\"\");\n", string) ;
		return result;
	}
	public String getActionAvpReplaceValue(List<Integer> parents, int code, String value)
	{
		String result = getAvpPath(parents);
		result+= String.format("getAVP(%d).set(\"%s\");\n", code, value) ;
		return result;
	}
	public String getActionAvpReplaceValue(List<Integer> avpPath, String value)
	{
		return getActionAvpReplaceValue(avpPath.subList(0, avpPath.size()-1), avpPath.get(avpPath.size()-1), value);
	}
	public String getActionAvpReplace(List<Integer> parents, int oldCode, int newCode, 
			int vendor, boolean hasVendor, boolean isMandatory, boolean secure, String value)
	{
		String aaaa = getAvpPath(parents);
		String result = "";
		result+= aaaa+String.format("removeAVP(%d);\n",
				oldCode);
		result+= aaaa+String.format("addAVP(%d, %d, %b, %b, %b, \"%s\");\n",
				 newCode, vendor, hasVendor, isMandatory, secure, value);
		return result;
	}
	public String getActionAvpReplace(List<Integer> parents, int oldCode, int newCode)
	{
		return getActionAvpReplace(parents, oldCode, newCode, 0, false, false, false, "");
	}
	public String getAvpReplace_NonGrouped(List<Integer> parents, int code)
	{
		return getActionAvpReplaceValueInternal(parents, getConditionAvpGet(false, code));
	}
	
	public final String getActionUpdateServingMmeCommand()
	{
		return "registerIMSI(getSessionIMSI(msg.getSessionID()), \"mme\", msg.getDstIP().get(0), getSessionOriginHost(msg.getSessionID()), getSessionOriginRealm(msg.getSessionID()));\n";
	}
	public final String getActionUseServingMmeCommand(){ 
		return "imsi_info = getIMSIInfo(msg.getIMSI(),\"mme\");\n" + "msg.updateHeaders( null, null, imsi_info.host, imsi_info.realm);\n"
			+ "msg.setDstIP(imsi_info.association);\n";
	}

	public final String getActionUpdateServingHssCommand(){
		return	"registerIMSI(getSessionIMSI(msg.getSessionID()), \"hss\", msg.getSrcIP(), getSessionOriginHost(msg.getSessionID()), getSessionOriginRealm(msg.getSessionID()));\n";
	}
	public final String getActionUseServingHssCommand(){
		 return "imsi_info = getIMSIInfo(msg.getIMSI(),\"hss\");\n" + "msg.updateHeaders( null, null, imsi_info.host, imsi_info.realm);\n"
			+ "msg.setDstIP(imsi_info.association);\n";
	}

	public final String getActionUseSessionInfoCommand(){ 
		return "msg.setDstIP(getSessionAssociation(msg.getSessionID()));\n"
			+ "msg.updateHeaders( msg.getOriginHost(), getSessionDestinationRealm(msg.getSessionID()), null, null);\n" + "msg.setSessionId(getSessionID(msg.getSessionID()));\n";
	}
	

	public final String getActionReplyAnswer(String answer){
		return String.format("createAnswer(\"%s\", msg);\n", answer);
	}

	private String getActionAddForGrouped(List<String> leafsList)
	{
		String result = "";
		for( String e : leafsList)
		{
			int i = e.lastIndexOf(",");
			String parent = e.substring(0,i);
			String child = e.substring(i+1);
			result+=String.format("msg.getAVPById(Arrays.asList(%s)).addAVP(%s, 0, false, false, false, \"\");\n", parent, child );
		}
		return result;
	}
	public String getActionAddForGrouped(int code)
	{
		List<String> leafsList = getCodeForCode(code);
		return getActionAddForGrouped(leafsList);
	}
	public String getActionRemoveForGrouped(int code)
	{
		List<String> leafsList = getCodeForCode(code);
		return getActionRemoveForGrouped(leafsList);
	}
	private String getActionRemoveForGrouped(List<String> leafsList)
	{
		String result = "";
		for( String e : leafsList)
		{
			int i = e.lastIndexOf(",");
			String parent = e.substring(0,i);
			String child = e.substring(i+1);
			result+=String.format("msg.getAVPById(Arrays.asList(%s)).removeAVP(%s);\n", parent, child );
		}
		return result;
	}
	public String getActionReplaceValueForGrouped(int code){
		List<String> leafsList = getCodeForCode(code);
		return getActionReplaceValueForGrouped(leafsList);
	}
	private String getActionReplaceValueForGrouped(List<String> leafsList)
	{
		String result = "";
		for( String e : leafsList)
		{
			result+=String.format("msg.getAVPById(Arrays.asList(%s)).set(\"\");\n", e);
		}
		return result;
	}
	


	public String getActionRemoveVendorID(List<Integer> parents, int code)
	{
		String result = getAvpPath(parents);
		result += String.format("getAVP(%d).removeVendorId();\n", code);
		return result;
	}
	
	public String getActionRemoveVendorID(List<Integer> avpPath)
	{
		String result = getAvpPath(avpPath.subList(0, avpPath.size()-1));
		result += String.format("getAVP(%d).removeVendorId();\n", avpPath.get(avpPath.size()-1));
		return result;
	}
	
	public String getActionAddVendorID(List<Integer> parents, int code, int vendorId)
	{
		String result = getAvpPath(parents);
		result += String.format("getAVP(%d).addVendorId(%d);\n", code, vendorId);
		return result;
	}
	
	public String getActionAddVendorID(List<Integer> avpPath, int vendorId)
	{
		String result = getAvpPath(avpPath.subList(0, avpPath.size()-1));
		result += String.format("getAVP(%d).addVendorId(%d);\n", avpPath.get(avpPath.size()-1), vendorId);
		return result;
	}
//	
//	public String getActionChangeAvpCode(List<Integer> parents, int oldCode, int newCode)
//	{
//		String result = getAvpPath(parents);
//		result+= String.format("changeAVPCode(%d,%d);", oldCode, newCode);
//		return result;
//	}
	
	public String getActionChangeCommandCode(int commandCode)
	{
		return  String.format("msg.setCommandCode(%d);\n", commandCode);
	}
	
	public String getActionChangeApplicationId(int code)
	{
		return String.format("msg.setApplicationID(%d);\n", code);
	}
	
	public String getActionReplaceSessionId(String oldSessionId, String newSessionId)
	{
		return String.format("msg.replaceSessionId(\"%s\",\"%s\");\n", oldSessionId, newSessionId);
	}
	
	public String getActionDropMessage()
	{
		return "msg.setDropped();\n";
	}
	
	public String getActionSkipThisRule()
	{
		return "msg.skipThis();\n";
	}
	
	public String getActionITS(String conditionTruePart, String conditionFalsePart)
	{
		return String.format(
				"itsResult = getDecision(msg.getAVP(1).getValue()," 
				+ "handleDigits(msg.getAVP(1407).getValue()), 100, 1, "
				+ "%s, 1);"
				
				+"msg.calledITS();\n"
				+ "if (itsResult.getReturnValue()==1) {%s} else {%s}", 
				
				"String.format(\"%08x\",msg.getEndToEndIdentifier())", 
				conditionTruePart, conditionFalsePart);
	}
	
	public String getActionLiveTracer(String fileNameWithoutExtension)
	{
		return String.format("String path = \"%s\";\nprinter.write(path, msg);\n", fileNameWithoutExtension);
	}
	
	public String getActionRemoveAvp(List<Integer> parents, int code)
	{
		String result = getAvpPath(parents);
		result+=String.format("removeAVP(%d);\n", code); 
		return result;
	}
	public String getActionRemoveAvp(List<Integer> avpPath)
	{
		String result = getAvpPath(avpPath.subList(0, avpPath.size()-1));
		result+=String.format("removeAVP(%d);\n", avpPath.get(avpPath.size()-1)); 
		return result;
	}
	public String getActionWhiteList(String input, String answer)
	{
		// TODO Auto-generated method stub
		return String.format("msg.filtered();\nif (%s)\n{%s} else\n{%s}", getconditionIsWhiteListed(input),
				getActionSkipThisRule(),
				getActionReplyAnswer(answer));
	}
	public String getActionBlackList(String input, String answer)
	{
		return String.format("msg.filtered();\nif (%s)\n{%s} else\n{%s}", getconditionIsBlackListed(input),
				getActionReplyAnswer(answer),		getActionSkipThisRule());
	}
	public String getActionRouteToClsuter(String dstCluster)
	{
		// TODO Auto-generated method stub
		return String.format("msg.setDstRouteCluster(\"%s\");\n", dstCluster);
	}
	public List<String> getMainParameters()
	{
		return Arrays.asList("OriginHost","OriginRealm", "DestinationHost", "DestinationRealm", "IMSI",
				"Association", "Route List", "Cluster");
	}
	public String getMainParameterCode(String key, String condition, String value)
	{
		String result = "";
		switch(key)
		{
		case "OriginHost":
		case "OriginRealm":
		case "DestinationHost":
		case "DestinationRealm":
		case  "IMSI":
		{
			result = String.format("(msg.get%s().%s(\"%s\") || getSession%s(msg.getSessionID()).%s(\"%s\"))", key, condition,
					value, key, condition, value);
			result = result.replace("ination", "");
		}
		break;
		case "Association":
		{
			result = String.format("msg.getSrcIP().%s(\"%s\")", condition, value);
		}
		break;
		case "Route List":
		{
			result = String.format("msg.getSrcRoute().%s(\"%s\")", condition, value);
		}
		break;
		case "Cluster":
		{
			result = String.format("msg.getSrcCluster().%s(\"%s\")", condition, value);
		}
		break;
		}
		
		return result;
	}
	public List<String> getRuleConditions()
	{
		final String T_DELETE_VENDOR_ID = "Delete Vendor ID";
		final String T_ENDS_WITH = "endsWith";
		final String T_STARTS_WITH = "startsWith";
		final String T_PRESENT = "present";
		final String T_EQUALS_SUB_AVPS = "equalsSubAvps";
		final String T_CONTAINS_SUB_AVPS = "containsSubAvps";
		final String T_EQUALS = "equals";
		final String T_CONTAINS = "contains";
		
		List<String> list = new ArrayList<String>();
		list.add(T_CONTAINS);
		list.add(T_EQUALS);
		list.add(T_CONTAINS_SUB_AVPS);
		list.add(T_EQUALS_SUB_AVPS);
		list.add(T_PRESENT);
		list.add(T_STARTS_WITH);
		list.add(T_ENDS_WITH);
		
		return list;
	}   
}
