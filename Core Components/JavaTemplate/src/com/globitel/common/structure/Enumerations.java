package com.globitel.common.structure;

public class Enumerations 
{
	public enum NumberingPlan
	{
		Unknown((byte)0x00),
		ISDN_Telephony((byte)0x01),
		Generic((byte)0x02),
		Data((byte)0x03),
		Telex((byte)0x04),
		Maritime_Mobile((byte)0x05),
		Land_Mobile((byte)0x06),
		ISDN_Mobile((byte)0x07);

		byte numberingPlan;
		private NumberingPlan(byte numberingPlan)
		{
			this.numberingPlan = numberingPlan;
		}

		public byte get()
		{
			return this.numberingPlan;
		}
	}

	public enum SubSystem
	{
		HLR((byte)0x06),
		VLR((byte)0x07), 
		MSC((byte)0x08), 
		EIR((byte)0x09),
		CAP((byte)0x92), 
		SCF((byte)0x93),
		SGSN((byte)0x95), 
		GGSN((byte)0x96),
		SIN	((byte)0xf1);

		byte subSystem;
		private SubSystem(byte subSystem)
		{
			this.subSystem = subSystem;
		}

		public byte get()
		{
			return this.subSystem;
		}
	}

	public enum AddressIndicator
	{
		RouteOnGlobalTitle((byte)0x12),
		RouteOnSubSystemNumber((byte)0x07);

		byte addressIndicator;
		private AddressIndicator(byte addressIndicator)
		{
			this.addressIndicator = addressIndicator;
		}

		public byte get()
		{
			return this.addressIndicator;
		}
	}

	public enum NatureOfAddress
	{
		Unknown((byte)0x00),				
		SubscriberNumber((byte)0x01),		
		UnknownNationalUse((byte)0x02),	
		NationalNumber((byte)0x03),		
		InternationalNumber((byte)0x04),	
		NetworkSpecificNumber((byte)0x05);

		byte natureOfAddress;
		private NatureOfAddress(byte natureOfAddress)
		{
			this.natureOfAddress = natureOfAddress;
		}

		public byte get()
		{
			return this.natureOfAddress;
		}

		public static NatureOfAddress valueOf(byte c_iNatureOfAddress)
		{
			// TODO Auto-generated method stub
			if(c_iNatureOfAddress == NatureOfAddress.Unknown.get())
			{
				return Unknown;
			}
			else if(c_iNatureOfAddress == SubscriberNumber.get())
			{
				return SubscriberNumber;
			}
			else if(c_iNatureOfAddress == UnknownNationalUse.get())
			{
				return UnknownNationalUse;
			}
			else if(c_iNatureOfAddress == NationalNumber.get())
			{
				return NationalNumber;
			}
			else if(c_iNatureOfAddress == InternationalNumber.get())
			{
				return InternationalNumber;
			}
			else if(c_iNatureOfAddress == NetworkSpecificNumber.get())
			{
				return NetworkSpecificNumber;
			}
			else
			{
				return Unknown;
			}
		}
	}

	public enum AddressPresentation
	{
		Allowed(0),
		Restricted(1),
		Absent(2);

		int number;

		private AddressPresentation(int number)
		{
			this.number = number;
		}

		public int get()
		{
			return this.number;
		}

	}

	public enum ChargingType
	{
		Invalid(0),
		Prepaid(1),
		Postpaid(2),
		Prepaid_Roaming_Allowed(3),
		Prepaid_Roaming_Denied(4);


		private int number;

		private ChargingType(int number)
		{
			this.number	= number;
		}

		public int get()
		{
			return this.number;
		}


	}

	public enum RoamingType
	{
		Invalid(0),
		InboundRoamer(1),
		OutboundRoamer(2),
		NationalRoamer(4),
		Local(8);


		private int number;

		private RoamingType(int number)
		{
			this.number	= number;
		}

		public int get()
		{
			return this.number;
		}
	}

	public static String toStringChargingType(int chargingType)
	{
		switch (chargingType)
		{
			case 0:
				return "Invalid";
			case 1:
				return "Prepaid";
			case 2:
				return "Postpaid";
			case 3:
				return "Prepaid_Roaming_Allowed";
			case 4:
				return "Prepaid_Roaming_Denied";

		}
		return "NULL";

	}

	public static String toStringAddressPresentation(AddressPresentation addressPresentation)
	{
		switch (addressPresentation)
		{
			case Allowed:
				return "Allowed";

			case Restricted:
				return "Restricted";

			case Absent:
				return "Absent";		
		}
		return "NULL";
	}

	public static Object toStringRoamingType(RoamingType iRoamingType)
	{
		switch (iRoamingType)
		{
			case Invalid:

				return "Invalid";

			case InboundRoamer:

				return "InboundRoamer";
			case OutboundRoamer:

				return "OutboundRoamer";
			case NationalRoamer:

				return "NationalRoamer";
				
			case Local:

				return "Local";
		}
		return "NULL";
	}

	public enum SS7_DECODE_TYPE
	{
		FULL,
		PARSE_ONLY
	}
	
	public enum RGS_Messages_Types
	{
		MSG_ECHO((byte)0x00),
		MSG_ECHO_RESPONSE((byte)0x01),
		MSG_REGISTER_MESSAGE_REQUEST((byte)0x02),
		MSG_MESSAGE_RECEIVED((byte)0x03),
		MSG_FORWARD_MESSAGE((byte)0x04),
		MSG_SEND_RESPONSE_MESSAGE((byte)0x05),
		MSG_ACTIVE_REGISTRATION_FAILED((byte)0x06),
		MSG_INSTANT_MSG((byte)0x07),
		MSG_IGNORE_MESSAGE((byte)0x08);
		
		private byte number;
		
		private RGS_Messages_Types(byte number)
		{
			this.number = number;
		}
		
		public byte get()
		{
			return this.number;
		}

	}
}
