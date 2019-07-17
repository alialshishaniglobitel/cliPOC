package com.globitel.xmlrpc;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import com.globitel.Application.Application;
import com.globitel.Application.CDR;
import com.globitel.Logic.SS7Manager;
import com.globitel.common.utils.Common;
import com.globitel.utilities.commons.ConfigurationManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class XmlRPCRequestImp {
	static boolean glIsKeepAlive;
	static int glShutDownTimeWait;
	static int glListiningPort;
	static String glServerSideUserName;
	static String glServerSidePassword;
	static boolean glEnableAuth = true;

	public static ConcurrentHashMap<String, CDR> concurrentHashMap = new ConcurrentHashMap<String, CDR>();

	private static XmlRPCRequestImp instance = null;
	public static Application app = null; 
	private int processorID = 0;

	public static XmlRPCRequestImp getInstance() {
		if (instance == null) {
			instance = new XmlRPCRequestImp();
			loadConfiguration();
			startServer();
		}
		return instance;
	}

	public static void loadConfiguration() {
		try {

			String strTemp;
			// ServerSide Configurations
			glListiningPort = Integer
					.parseInt(ConfigurationManager.getInstance().getValue("XmlRPC.server.listeningPort").trim());

			strTemp = ConfigurationManager.getInstance().getValue("XmlRPC.server.enableAuthentication").trim();
			glEnableAuth = strTemp.equals("1") ? true : false;

			strTemp = ConfigurationManager.getInstance().getValue("XmlRPC.server.isKeepAlive").trim();
			glIsKeepAlive = strTemp.equals("1") ? true : false;

			glShutDownTimeWait = Integer
					.parseInt(ConfigurationManager.getInstance().getValue("XmlRPC.server.shutdownTimeWait").trim());

			if (glEnableAuth) {
				glServerSideUserName = ConfigurationManager.getInstance().getValue("XmlRPC.server.username").trim();
				glServerSidePassword = ConfigurationManager.getInstance().getValue("XmlRPC.server.password").trim();
			}

		} catch (Exception ex) {
			MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
		}
	}

	private static void startServer() {
		try {
			MyLoggerFactory.getInstance().getAPILogger().info("Starting xmlRpc web server ...");
			WebServer webServer = new WebServer(glListiningPort);
			MyLoggerFactory.getInstance().getAPILogger()
					.info("xmlRpc web Server start listining on port:" + glListiningPort);

			// create XML RPC server
			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

			// create property handler
			PropertyHandlerMapping PropertyHandlerMapping = (PropertyHandlerMapping) newXmlRpcHandlerMapping();

			PropertyHandlerMapping.addHandler(XmlRPCRequestImp.class);
			// PropertyHandlerMapping.addHandler( HeartBeat.class );

			xmlRpcServer.setHandlerMapping(PropertyHandlerMapping);

			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();

			serverConfig.setContentLengthOptional(true);
			serverConfig.setEnabledForExceptions(true);
			serverConfig.setKeepAliveEnabled(glIsKeepAlive);

			// Start the server

			webServer.start();

			MyLoggerFactory.getInstance().getAPILogger().info("web Server started ...");

		} catch (XmlRpcException | IOException ex) {
			MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
		} catch (Exception ex) {
			MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
		}
	}

	/*
	 * Override handler mapping that allows the serves to authenticate connection
	 * through basic http authentication
	 */
	protected static XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
		PropertyHandlerMapping mapping = new PropertyHandlerMapping();

		AbstractReflectiveHandlerMapping.AuthenticationHandler handler = new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
			@Override
			public boolean isAuthorized(XmlRpcRequest xmlRpcRequest) throws XmlRpcException {
				XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) xmlRpcRequest.getConfig();

				return isAuthorizedRequest(config.getBasicUserName(), config.getBasicPassword());
			};
		};
		mapping.setAuthenticationHandler(handler);

		return mapping;
	}

	private static boolean isAuthorizedRequest(String username, String password) {
		boolean bIsAuthorized = false;
		if (!glEnableAuth)
			return true;

		try {
			if (username != null && username.equals(glServerSideUserName)) {
				if (password != null && password.equals(glServerSidePassword)) {
					bIsAuthorized = true;
				}
			}
		} catch (Exception ex) {
			MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
		}
		return bIsAuthorized;
	}

	public Map<String, Object> getLocation(Map<String, Object> request) throws XmlRpcException {
		Map<String, Object> respMessage = null;
		String keySession = null;
		try {
			respMessage = new HashMap<String, Object>();

			if (request.containsKey("msisdn")) {
				String msisdn = String.valueOf(request.get("msisdn"));
				MyLoggerFactory.getInstance().getAPILogger()
						.trace("parse paramName:" + "MSISDN" + ", ParamValue:" + msisdn);
				keySession = ((SS7Manager) app.requestHandler.sigProcessors[getProcessorID()].handler).handleLocationRequest(msisdn);
				long requestTime = new Date().getTime();
				this.concurrentHashMap.put(keySession, new CDR(null));

				try {
					while (true) {
						if (this.concurrentHashMap.get(keySession)._tid == null) {
							// 1000ms is the maximum execution time for the request, after that it will be considered as timeout.
							if ((new Date().getTime() - requestTime) < (app.sessionTimeOut + 1000)) {
								Thread.sleep(10);
							} else {
								// TimeOut Error
								MyLoggerFactory.getInstance().getAPILogger().error("TimeOut Error");
								respMessage.put("returnValue", 1002);
								respMessage.put("returnDesc", "TimeOut Error");
								break;
							}
						} else {
							respMessage.put("Date", Common.convertJavaDateToMySQLDate(this.concurrentHashMap.get(keySession)._date));
							respMessage.put("Return_Value", this.concurrentHashMap.get(keySession).returnValue + "");
							respMessage.put("Key_Session", keySession + "");
							respMessage.put("MSISDN", this.concurrentHashMap.get(keySession).msisdn + "");
							respMessage.put("3G_MSC_Network_Node_Number", this.concurrentHashMap.get(keySession).networkNodeNumber + "");
							respMessage.put("IMSI", this.concurrentHashMap.get(keySession).imsi + "");
							respMessage.put("3G_Type_Of_Shape", this.concurrentHashMap.get(keySession).typeOfShape + "");
							respMessage.put("3G_Latitude", this.concurrentHashMap.get(keySession).latitude + "");
							respMessage.put("3G_Longitude", this.concurrentHashMap.get(keySession).longitude + "");
							respMessage.put("3G_CellId", this.concurrentHashMap.get(keySession).cellsac + "");
							respMessage.put("4G_Serving_Node", this.concurrentHashMap.get(keySession).servingNode + "");
							respMessage.put("4G_Additional_Serving_Node", this.concurrentHashMap.get(keySession).additionalServingNode + "");
							respMessage.put("4G_Location_Estimation", this.concurrentHashMap.get(keySession)._4GlocInfo.locationEstimation + "");
							respMessage.put("4G_Accuracy_Fulfilment_Indicator", this.concurrentHashMap.get(keySession)._4GlocInfo.accuracyFulfilmentIndicator + "");
							respMessage.put("4G_EUTRAN_Positioning_Data", this.concurrentHashMap.get(keySession)._4GlocInfo.eutranPositioningData + "");
							respMessage.put("4G_ECGI", this.concurrentHashMap.get(keySession)._4GlocInfo.ecgi + "");
							respMessage.put("4G_MCC", this.concurrentHashMap.get(keySession)._4GlocInfo.mcc + "");
							respMessage.put("4G_MNC", this.concurrentHashMap.get(keySession)._4GlocInfo.mnc + "");
							respMessage.put("4G_CellId", this.concurrentHashMap.get(keySession)._4GlocInfo.cellID + "");
							respMessage.put("4G_PLMNId", this.concurrentHashMap.get(keySession)._4GlocInfo.plmnId + "");							
							break;
						}
					}
				} catch (InterruptedException ex) {
					MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
					respMessage.put("returnValue", 1003);
					respMessage.put("returnDesc", "Internal server error");		
				}
			} else {
				MyLoggerFactory.getInstance().getAPILogger().error("Mandatory field missing [MSISDN]");
				respMessage.put("returnValue", 1001);
				respMessage.put("returnDesc", "Mandatory field missing [MSISDN]");
			}
		} catch (Exception ex) {
			MyLoggerFactory.getInstance().getAPILogger().error(ex.getMessage(), ex);
			respMessage.put("returnValue", 1003);
			respMessage.put("returnDesc", "Internal server error");
		} finally {
			clean(keySession);
			return respMessage;
		}
	}
	
	private void clean (String keySession) {
		if((keySession != null) && this.concurrentHashMap.containsKey(keySession)) {
			this.concurrentHashMap.remove(keySession);	
		}
	}
	
	private synchronized int getProcessorID() {
		if (processorID < app.numberOfThreads - 1) {
			processorID++;
		} else {
			processorID = 0;
		}
		return processorID;
	}

}
