package com.globitel.diameterCodec.Diameter;

public class DiameterEncodings
{
	public static void fromAVPToBytes(AVP avp)
	{
		String type = avp.getClass().getSimpleName();
		switch(type)
		{
		case "DiameterAVPAddress":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPEnumerated":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPFloat32":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPFloat64":
		{
			System.out.println("test");
		}
		break;
		
		
		case "DiameterAVPGrouped":
		{
			System.out.println("test");
		}
		break;
		
		
		case "DiameterAVPIdentity":
		{
			System.out.println("test");
		}
		break;
		
		
		
		case "DiameterAVPInteger32":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPInteger64":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPIPFilterRule":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPOctetString":
		{
			System.out.println("test");
		}
		break;
		
		
		case "DiameterAVPQosFilterRule":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPTime":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPUnsigned32":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPUnsigned64":
		{
			System.out.println("test");
		}
		break;
		
		case "DiameterAVPUTF8String":
		{
			System.out.println("test");
		}
		break;
		
		}
	}
}
