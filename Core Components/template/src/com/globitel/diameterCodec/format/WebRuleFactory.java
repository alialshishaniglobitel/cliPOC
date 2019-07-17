package com.globitel.diameterCodec.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.globitel.diameterCodec.actions.AddAvp;
import com.globitel.diameterCodec.actions.AddVendorId;
import com.globitel.diameterCodec.actions.ChangeApplicationId;
import com.globitel.diameterCodec.actions.ChangeCC;
import com.globitel.diameterCodec.actions.DropMessage;
import com.globitel.diameterCodec.actions.InvokeInterface;
import com.globitel.diameterCodec.actions.RemoveAvp;
import com.globitel.diameterCodec.actions.RemoveVendorId;
import com.globitel.diameterCodec.actions.ReplaceAvp;
import com.globitel.diameterCodec.actions.ReplaceAvpValue;
import com.globitel.diameterCodec.actions.ReplaceSessionId;
import com.globitel.diameterCodec.actions.ReplyAnswer;
import com.globitel.diameterCodec.actions.RouteToCluster;
import com.globitel.diameterCodec.actions.RouteToPeer;
import com.globitel.diameterCodec.actions.RouteToRouteList;
import com.globitel.diameterCodec.actions.UpdateServingHSS;
import com.globitel.diameterCodec.actions.UpdateServingMME;
import com.globitel.diameterCodec.actions.UseServingHSS;
import com.globitel.diameterCodec.actions.UseServingMME;
import com.globitel.diameterCodec.actions.UseSessionAssociation;
import com.globitel.diameterCodec.actions.UseSessionInformation;
import com.globitel.diameterCodec.actions.blacklist;
import com.globitel.diameterCodec.actions.whitelist;

public class WebRuleFactory
{
	RuleFactory factory = new RuleFactory();
	public static String SEPARATOR = ",";
	
	Map<String,InvokeInterface> map = new HashMap<String,InvokeInterface>();
	private void init()
	{
		map.put("Add AVP", new AddAvp());
		map.put("Route to Node", new RouteToPeer());
		map.put("Route to Route List", new RouteToRouteList());
		map.put("Use Serving MME", new UseServingMME());
		map.put("Update Serving MME", new UpdateServingMME());
		map.put("Use Serving HSS", new UseServingHSS());
		map.put("Update Serving HSS", new UpdateServingHSS());
		map.put("Use Session Information", new UseSessionInformation());
		map.put("Reply Answer", new ReplyAnswer());
		map.put("Drop Message", new DropMessage());
		map.put("Change CC", new ChangeCC());
		map.put("Change Application ID", new ChangeApplicationId());
		map.put("Remove AVP", new RemoveAvp());
		map.put("Replace AVP Value", new ReplaceAvpValue());
		map.put("Replace Session ID", new ReplaceSessionId());
		map.put("Remove Vendor ID", new RemoveVendorId());
		map.put("Add Vendor ID", new AddVendorId());
		map.put("Replace AVP", new ReplaceAvp());
		map.put("Whitelist", new whitelist());
		map.put("Blacklist", new blacklist());
		map.put("Use Session Association", new UseSessionAssociation());
		map.put("Route To Cluster", new RouteToCluster());
	}
	public String invoke(String methodName, Object...args) throws Exception
	{
		InvokeInterface obj =  map.get(methodName);
		return obj.invoke(args);
	}
	public WebRuleFactory(String separator)
	{
		SEPARATOR = new String(separator);
		
		init();
	}
	private List<Integer> objToIntList(Object obj)
	{
		String avpList = (String)obj;
		if ( avpList.equals(""))
			return null;
		
		List<Integer> avpPath = Arrays.stream(avpList.split(SEPARATOR)).map(Integer::parseInt).collect(Collectors.toList());
		return avpPath;
	}
	private int objToInt(Object obj)
	{
		return Integer.parseInt((String) obj);
	}
	private boolean objToBool(Object obj)
	{
		return Boolean.parseBoolean((String) obj);
	}
	private String objToStr(Object obj)
	{
		return (String)obj;
	}
	/**
	 * @param args List parents, avp code, vendor, hasVendor, isMandatory
	 * isProtected, value
	 * @return command generated
	 */
//	public final String getActionAddAvp(Object... args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code = objToInt(args[1]);
//		int vendor = objToInt(args[2]);
//		boolean hasVendor = objToBool( args[3]);
//		boolean isMandatory = objToBool(args[4]);
//		boolean isProtected = objToBool(args[5]);
//		String value = objToStr(args[6]);
//		String result = factory.getActionAddAvp(avpPath, code, vendor, hasVendor, isMandatory, isProtected, value);
//		return result;
//	}
	
	/**
	 * @param args contains Dst Route Peer as String
	 * @return command generated
	 */
//	public final String getActionRouteToPeer(Object... args){
//		String dstIP = objToStr( args[0]);
//		return factory.getActionRouteToPeer(dstIP);
//	}
	
	/**
	 * @param args contains Dst Route List as String
	 * @return command generated
	 */
//	public final String getActionRouteToRouteList(Object... args)
//	{
//		String dstRoute = objToStr(args[0]);
//		return factory.getActionRouteToRouteList(dstRoute);
//	}
	/**
	 * @param args parent - code - value
	 * @return string command
	 */
//	public String getActionAvpReplaceValue(Object...args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code = objToInt(args[1]);
//		String value = objToStr(args[2]);
//		String result = factory.getActionAvpReplaceValue(avpPath, code, value);
//		return result;
//	}
	
	/**
	 * @param args parent, code, new code, vendor, hasVendor, isMandatory, isSecure, Value to use.
	 * @return
	 */
//	public String getActionAvpReplace(Object...args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code = objToInt(args[1]);
//		int newCode = objToInt(args[2]);
//		int vendor = objToInt(args[3]);
//		boolean hasVendor = objToBool(args[4]);
//		boolean isMandatory = objToBool(args[5]);
//		boolean isSecure = objToBool(args[6]);
//		String value = objToStr(args[7]);
//		return factory.getActionAvpReplace(avpPath, code, newCode, vendor, hasVendor,
//				isMandatory, isSecure, value);
//	}
	
//	public String getAvpReplace_NonGrouped(List<Integer> parents, int code)
//	{
//		return getActionAvpReplaceValueInternal(parents, getConditionAvpGet(false, code));
//	}
	
	/**
	 * @param args imsi, componentType, address, host, realm
	 * @return  string command
	 */
//	public final String getActionUpdateServingMmeCommand(Object... args)
//	{
//		String imsi = objToStr(args[0]);//"getSessionIMSI(msg.getSessionID())";
//		String compType = objToStr(args[1]);//"\"mme\"";
//		String address = objToStr(args[2]);//"msg.getDstIP().get(0)";
//		String host = objToStr(args[3]);//"getSessionOriginHost(msg.getSessionID())";
//		String realm = objToStr(args[4]);//"getSessionOriginRealm(msg.getSessionID())";
//		return String.format("registerIMSI(%s, %s, %s, %s, %s);", 
//				imsi, compType, address, host, realm );
//	}
	
	/**
	 * @param args imsi, network component type, oHost, oRealm, dHost, dRealm, association
	 * @return string command
	 */
//	public final String getActionUseServingMmeCommand(Object... args)
//	{ 
//		String imsi = objToStr(args[0]);//"msg.getIMSI()";
//		String compType = objToStr(args[1]);//"\"mme\"";
//		String oHost = objToStr(args[2]);//"null";
//		String oRealm = objToStr(args[3]);//"null";
//		String dHost = objToStr(args[4]);//"imsi_info.host";
//		String dRealm = objToStr(args[5]);//"imsi_info.realm";
//		String association = objToStr(args[6]);//"imsi_info.association";
//		return "imsi_info = getIMSIInfo(" + imsi + "," + compType + ");\n" + "msg.updateHeaders( " + oHost + ", " + oRealm + ", " + dHost + ", " + dRealm + ");\n"
//			+ "msg.setDstIP(" + association + ");\n";
//	}

	/**
	 * @param args imsi, componentType, association, originHost, originRealm
	 * @return string command
	 */
	public final String getActionUpdateServingHssCommand(Object... args)
	{
		String imsi = objToStr(args[0]);//"getSessionIMSI(msg.getSessionID())";
		String componentType = objToStr(args[1]);//"\"hss\"";
		String association = objToStr(args[2]);//"msg.getSrcIP()";
		String originHost = objToStr(args[3]);//"getSessionOriginHost(msg.getSessionID())";
		String originRealm = objToStr(args[4]);//"getSessionOriginRealm(msg.getSessionID())";
		return	"registerIMSI(" + imsi + ", " + componentType + ", " + association + ", " + originHost + ", " + originRealm + ");\n";
	}
	/**
	 * @param args imsi, compType, oHost, oRealm, dhost, drealm, association
	 * @return string command
	 */
//	public final String getActionUseServingHssCommand(Object... args)
//	{
//		String imsi = objToStr(args[0]);//"msg.getIMSI()";
//		String compType =objToStr(args[1]);//"\"hss\"";
//		String oHost = objToStr(args[2]);//"null";
//		String oRealm = objToStr(args[3]);//"null";
//		String dhost = objToStr(args[4]);//"imsi_info.host";
//		String drealm = objToStr(args[5]);//"imsi_info.realm";
//		String association =objToStr(args[6]);//"imsi_info.association";
//		return "imsi_info = getIMSIInfo(" + imsi + "," + compType + ");\n" + "msg.updateHeaders( " + oHost + ", " + oRealm + ", " + dhost + ", " + drealm + ");\n"
//			+ "msg.setDstIP(" + association + ");\n";
//	}

	/**
	 * @param args association oHost oRealm dHost dRealm SessionId
	 * @return string command
	 */
//	public final String getActionUseSessionInfoCommand(Object... args){ 
//		String association = objToStr(args[0]);//"getSessionAssociation(msg.getSessionID())";
//		String originHost = objToStr(args[1]);//"msg.getOriginHost()";
//		String originRealm = objToStr(args[2]);//"getSessionDestinationRealm(msg.getSessionID())";
//		String destHost = objToStr(args[3]);//"null";
//		String destRealm = objToStr(args[4]);//"null";
//		String sessionId = objToStr(args[5]);//"getSessionID(msg.getSessionID())";
//		return "msg.setDstIP(" + association + ");\n"
//			+ "msg.updateHeaders( " + originHost + ", " + originRealm + ", " + destHost + ", " + destRealm + ");\n" + "msg.setSessionId(" + sessionId + ");";
//	}
	/**
	 * @param args int commandCode
	 * @return string command
	 */
//	public String getActionChangeCommandCode(Object... args)
//	{
//		int commandCode = objToInt(args[0]);
//		return factory.getActionChangeCommandCode(commandCode);
//	}
	/**
	 * @param args int application id 
	 * @return
	 */
//	public String getActionChangeApplicationId(Object...args)
//	{
//		int code = objToInt(args[0]);
//		return factory.getActionChangeApplicationId(code);
//	}
	
	/**
	 * @param args String oldSessionId   String newSessionId
	 * @return
	 */
//	public String getActionReplaceSessionId(Object...args)
//	{
//		String oldSessionId = objToStr(args[0]);
//		String newSessionId = objToStr(args[1]);
//		return factory.getActionReplaceSessionId(oldSessionId, newSessionId);
//	}
	
	/**
	 * @return takes no parameters
	 */
//	public String getActionDropMessage()
//	{
//		return factory.getActionDropMessage();
//	}
	
	/**
	 * @return takes no parameters
	 */
	public String getActionSkipThisRule()
	{
		return factory.getActionSkipThisRule();
	}
	
	/**
	 * @param args List<Integer> parents int avp code
	 * @return
	 */
//	public String getActionRemoveAvp(Object...args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code= objToInt(args[1]);
//		return factory.getActionRemoveAvp(avpPath, code);
//	}
	/**
	 * @param args String answer
	 * @return
	 */
	public final String getActionReplyAnswer(Object...args){
		String answer = objToStr(args[0]);
		return factory.getActionReplyAnswer(answer);
	}

	/**
	 * @param args List<Integer> parents int code
	 * 
	 * @return String command
	 */
//	public String getActionRemoveVendorID(Object...args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code = objToInt(args[1]);
//		return factory.getActionRemoveVendorID(avpPath, code);
//	}
	
	/**
	 * @param args List<Integer> parents - int code - int vendorId
	 * @return String command
	 */
//	public String getActionAddVendorID(Object...args)
//	{
//		List<Integer> avpPath = objToIntList(args[0]);
//		int code = objToInt(args[1]);
//		int vendorId = objToInt(args[2]);
//		return factory.getActionAddVendorID(avpPath, code, vendorId);
//	}
}
