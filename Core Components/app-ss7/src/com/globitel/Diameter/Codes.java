package com.globitel.Diameter;

public class Codes
{
	public class AVPCodes
	{
		public static final int lcsClientType 				= 1241	;
		public static final int LcsEPSClientName 			= 2501	;
		public static final int lcsRequestorName			= 2502	;
		public static final int lcsRequestorIdString		= 1240	;
		public static final int lcsFormatIndicator			= 1237	;
		public static final int sessionID					= 263	;
		public static final int authStateId					= 277	;
		public static final int originHost					= 264	;
		public static final int originRealm					= 296	;
		public static final int destinationHost				= 293	;
		public static final int destinationRealm			= 283	;
		public static final int locationType				= 2500	;
		public static final int userNameIMSI				= 1		;
		public static final int msisdn						= 701	;
		public static final int imei						= 1402	;
		public static final int lcsNameString				= 1238	;
		public static final int lcsPriority					= 2503	;
		public static final int lcsQoSClass					= 2523	;
		public static final int lcsQoS						= 2504	;
		public static final int horizontalAccuracy			= 2505	;
		public static final int verticalAccuracy			= 2506	;
		public static final int verticalRequested			= 2507	;
		public static final int responseTime				= 2509	;
		public static final int velocityRequested			= 2508	;
		public static final int supportedGADShapes			= 2510	;
		public static final int lcsServiceTypeID			= 2520	;
		public static final int lcsCodeword					= 2511	;
		public static final int lcsPrivacyCheckNonSession	= 2521	;
		public static final int lcsPrivacyCheck				= 2512	;
		public static final int lcsPrivacyCheckSession		= 2522	;
		public static final int serviceSelection			= 493	;
		public static final int supportedFeatures			= 628	;
		public static final int feature_List_ID 			= 629	;
		public static final int feature_List 				= 630	;	

		public static final int host_IP_Address 			= 257	;
		public static final int vendor_Id					= 266	;
		public static final int product_Name				= 269	;
		public static final int inband_Security_Id			= 299	;
		public static final int vendor_Specific_Application_Id		= 260;
		public static final int auth_Application_Id			= 258	;
		public static final int resultCode					= 268	;
		public static final int subscriptionData			= 1400	;
		public static final int lcsInfo						= 1473	;
		public static final int gmlcNumber					= 1474	;
		public static final int supportedVendorID 			= 265	;
		public static final int firmware_Revision 			= 267	;
		public static final int serving_Node      			= 2401	;
		public static final int location_Estimate		    = 1242	;
		public static final int ECGI						= 2517	;
		public static final int age_Of_Location_Estimate    = 2514	;
		public static final int experimentalResultCode		= 298	;
		public static final int experimentalResult			= 297	;
		public static final int additionalServingNode		= 2406	;
		public static final int accuracyFulfilmentIndicator	= 2513	;
		public static final int eutranPositioningData		= 2516	;
	}

	public class CommandCodes 
	{
		public final static int  CapabilitiesExchange = 257;
		public final static int  LCS_RoutingInfo      = 8388622;
		public final static int  WatchDOG			  = 280;
		public final static int  ProvideLocation	  = 8388620;
		public final static int  InsertSubscriberData = 319;
	}

	public class ResultCodes 
	{
		public static final int DIAMETER_MULTI_ROUND_AUTH         				= 1001;
		
		public static final int DIAMETER_SUCCESS 								= 2001;
		public static final int	DIAMETER_LIMITED_SUCCESS           				= 2002;
		
		public static final int	DIAMETER_COMMAND_UNSUPPORTED       				= 3001;
		public static final int	DIAMETER_UNABLE_TO_DELIVER        				= 3002;
		public static final int	DIAMETER_REALM_NOT_SERVED         				= 3003;
		public static final int	DIAMETER_TOO_BUSY                 				= 3004;
		public static final int	DIAMETER_LOOP_DETECTED            				= 3005;
		public static final int	DIAMETER_REDIRECT_INDICATION      				= 3006;
		public static final int	DIAMETER_APPLICATION_UNSUPPORTED  				= 3007;
		public static final int	DIAMETER_INVALID_HDR_BITS         				= 3008;
		public static final int	DIAMETER_INVALID_AVP_BITS         				= 3009;
		public static final int	DIAMETER_UNKNOWN_PEER             				= 3010;
		
		public static final int DIAMETER_AUTHENTICATION_REJECTED   				= 4001;
		public static final int DIAMETER_OUT_OF_SPACE             				= 4002;
		public static final int ELECTION_LOST                     				= 4003;
		public static final int DIAMETER_USER_DATA_NOT_AVAILABLE 				= 4100;
		public static final int DIAMETER_PRIOR_UPDATE_IN_PROGRESS 				= 4101;
		public static final int DIAMETER_ERROR_ABSENT_USER 						= 4201;
		public static final int DIAMETER_ERROR_UNREACHABLE_USER 				= 4221;
		public static final int DIAMETER_ERROR_SUSPENDED_USER 					= 4221;
		public static final int DIAMETER_ERROR_DETACHED_USER 					= 4223;
		public static final int DIAMETER_ERROR_POSITIONING_DENIED  				= 4224;
		public static final int DIAMETER_ERROR_POSITIONING_FAILED 				= 4225;
		public static final int DIAMETER_ERROR_UNKNOWN_UNREACHABLE_LCS_CLIENT 	= 4226;
		
		public static final int DIAMETER_AVP_UNSUPPORTED          				= 5001;
		public static final int DIAMETER_ERROR_IDENTITIES_DONT_MATCH 			= 5002;
		public static final int DIAMETER_AUTHORIZATION_REJECTED    				= 5003;
		public static final int DIAMETER_INVALID_AVP_VALUE						= 5004;
		public static final int DIAMETER_MISSING_AVP               				= 5005;
		public static final int DIAMETER_RESOURCES_EXCEEDED        				= 5006;
		public static final int DIAMETER_CONTRADICTING_AVPS        				= 5007;
		public static final int DIAMETER_AVP_NOT_ALLOWED           				= 5008;
		public static final int DIAMETER_AVP_OCCURS_TOO_MANY_TIMES 				= 5009;
		public static final int DIAMETER_NO_COMMON_APPLICATION     				= 5010;
		public static final int DIAMETER_ERROR_FEATURE_UNSUPPORTED 				= 5011;
		public static final int DIAMETER_UNABLE_TO_COMPLY          				= 5012;
		public static final int DIAMETER_INVALID_BIT_IN_HEADER     				= 5013;
		public static final int DIAMETER_INVALID_AVP_LENGTH        				= 5014;
		public static final int DIAMETER_INVALID_MESSAGE_LENGTH    				= 5015;
		public static final int DIAMETER_INVALID_AVP_BIT_COMBO     				= 5016;
		public static final int DIAMETER_NO_COMMON_SECURITY        				= 5017;
		public static final int DIAMETER_ERROR_USER_DATA_NOT_RECOGNIZED 		= 5100;
		public static final int DIAMETER_ERROR_OPERATION_NOT_ALLOWED 			= 5101;
		public static final int DIAMETER_ERROR_USER_DATA_CANNOT_BE_READ 		= 5102;
		public static final int DIAMETER_ERROR_USER_DATA_CANNOT_BE_MODIFIED 	= 5103;
		public static final int DIAMETER_ERROR_USER_DATA_CANNOT_BE_NOTIFIED 	= 5104;
		public static final int DIAMETER_ERROR_TRANSPARENT_DATA_OUT_OF_SYNC 	= 5105;		
		public static final int DIAMETER_ERROR_SUBS_DATA_ABSENT 				= 5106;
		public static final int DIAMETER_ERROR_NO_SUBSCRIPTION_TO_DATA 			= 5107;
		public static final int DIAMETER_ERROR_DSAI_NOT_AVAILABLE 				= 5108;
		public static final int DIAMETER_ERROR_UNAUTHORIZED_REQUESTING_NETWORK 	= 5490;
		
	}

	public class AttributeCodes
	{
		public static final int LMSI 				= 2400;
		public static final int MME_Name 			= 2402;
		public static final int MSC_Number 			= 2403;
		public static final int GMLC_Address 		= 2405;
		public static final int PPR_Address 		= 2407;
		public static final int SGSN_Number 		= 1489;

	}

	public class LocationType
	{
		public static final String CURRENT_LOCATION 					= "0";
		public static final String CURRENT_OR_LAST_KNOWN_LOCATION 		= "1";
		public static final String INITIAL_LOCATION						= "2";
		public static final String RESERVED_1							= "3";
		public static final String RESERVED_2							= "4";
		public static final String NOTIFICATION_VERIFICATION_ONLY		= "5";
	}


	public class VelocityRequested
	{
		public static final String VELOCITY_IS_NOT_REQUESTED 			= "0";
		public static final String VELOCITY_IS_REQUESTED 				= "1";
	}

	public class SupportedGADShapes
	{
		public static final String ellipsoidPoint 										= "0";
		public static final String ellipsoidPointWithUncertaintyCircle 					= "1";
		public static final String ellipsoidPointWithUncertaintyEllipse 				= "2";
		public static final String polygon 												= "3";
		public static final String ellipsoidPointWithAltitude							= "4";
		public static final String ellipsoidPointWithAltitudeAndUncertaintyElipsoid 	= "5";
		public static final String ellipsoidArc 										= "6";

	}


	public class LcsPrivacyCheck
	{
		public static final String ALLOWED_WITHOUT_NOTIFICATION 		= "0";
		public static final String ALLOWED_WITH_NOTIFICATION 			= "1";
		public static final String ALLOWED_IF_NO_RESPONSE 				= "2";
		public static final String RESTRICTED_IF_NO_RESPONSE 			= "3";
		public static final String NOT_ALLOWED 							= "4";
	}

	public class ApplicationInterface
	{
		public static final int SLg								=	16777255;
		public static final int SLh								=	16777291;
		public static final int DiameterCommonMessages			=	0		;
		public static final int s6a_s6d							=	16777251;
	}



}
