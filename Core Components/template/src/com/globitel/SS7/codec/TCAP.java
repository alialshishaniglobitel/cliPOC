
package com.globitel.SS7.codec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.tcap.asn.DialogAPDUType;
import org.restcomm.protocols.ss7.tcap.asn.DialogPortion;
import org.restcomm.protocols.ss7.tcap.asn.DialogRequestAPDU;
import org.restcomm.protocols.ss7.tcap.asn.DialogResponseAPDU;
import org.restcomm.protocols.ss7.tcap.asn.EncodeException;
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.comp.Component;
import org.restcomm.protocols.ss7.tcap.asn.comp.ComponentType;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
import org.restcomm.protocols.ss7.tcap.asn.comp.Reject;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnError;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResult;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultLast;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCAbortMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCBeginMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCContinueMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCEndMessage;

import com.globitel.common.utils.ByteArray;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class TCAP
{
	public static final byte	Begin		= 0x62;
	public static final byte	End			= 0x64;
	public static final byte	Continue	= 0x65;
	public static final byte	Abort		= 0x67;

	public Object 				tcapMessage;
	public DialogPortion		dialogPortion;
	private List<Component>		undefinedComponents		= new ArrayList<Component>();

	public ArrayList<Object>    definedComponents = new ArrayList<>();

	public byte tcapType;

	public Object tcMessage;

	public void decode(byte[] tcap)
	{ 
		byte[] tcapPart = tcap ; 
		AsnInputStream ais = new AsnInputStream(tcapPart);
		try
		{
			int tag = ais.readTag();

			switch(tag | 0x60)
			{
				case Begin:
					tcapType 		= Begin;
					tcapMessage 	= TcapFactory.createTCBeginMessage(ais);
					dialogPortion 	= ((TCBeginMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCBeginMessage) tcapMessage).getComponent());
					break;

				case Continue:
					tcapType 		= Continue;
					tcapMessage 	= TcapFactory.createTCContinueMessage(ais);
					dialogPortion 	= ((TCContinueMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCContinueMessage) tcapMessage).getComponent());
					break;

				case End:
					tcapType 		= End;
					tcapMessage 	= TcapFactory.createTCEndMessage(ais);
					dialogPortion 	= ((TCEndMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCEndMessage) tcapMessage).getComponent());
					break;

				case Abort:
					tcapType 		= Abort;
					tcapMessage 	= TcapFactory.createTCAbortMessage(ais);
					dialogPortion 	= ((TCAbortMessage) tcapMessage).getDialogPortion();
					break;
			}

			decodeComponentPart();
		}

		catch (IOException e)
		{
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error("IO Exception occured while decoding Dialog Portion : " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			MyLoggerFactory.getInstance().getAppLogger().error("ASN1 Exception occured while decoding Dialog Portion : " + e.getMessage(), e);
		}

	}
	
 	public void decode(Message message)
	{


		int tcapLength = message.rawData.length - message.getDecodingIndex();
		byte[] tcapPart = new byte[tcapLength];
		System.arraycopy(message.rawData, message.getDecodingIndex(), tcapPart, 0, tcapLength);

		AsnInputStream ais = new AsnInputStream(tcapPart);
		try
		{
			int tag = ais.readTag();

			switch(tag | 0x60)
			{
				case Begin:
					tcapType 		= Begin;
					tcapMessage 	= TcapFactory.createTCBeginMessage(ais);
					dialogPortion 	= ((TCBeginMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCBeginMessage) tcapMessage).getComponent());
					break;

				case Continue:
					tcapType 		= Continue;
					tcapMessage 	= TcapFactory.createTCContinueMessage(ais);
					dialogPortion 	= ((TCContinueMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCContinueMessage) tcapMessage).getComponent());
					break;

				case End:
					tcapType 		= End;
					tcapMessage 	= TcapFactory.createTCEndMessage(ais);
					dialogPortion 	= ((TCEndMessage) tcapMessage).getDialogPortion();
					undefinedComponents 		= Arrays.asList(((TCEndMessage) tcapMessage).getComponent());
					break;

				case Abort:
					tcapType 		= Abort;
					tcapMessage 	= TcapFactory.createTCAbortMessage(ais);
					dialogPortion 	= ((TCAbortMessage) tcapMessage).getDialogPortion();
					break;
			}

			if(dialogPortion != null && dialogPortion.getDialogAPDU().getType() == DialogAPDUType.Request)
			{
				DialogRequestAPDU dialogRequestAPDU = (DialogRequestAPDU) message.tcap.dialogPortion.getDialogAPDU();
				message.setMAP_Version(dialogRequestAPDU.getApplicationContextName());
			}
			else if(dialogPortion != null && dialogPortion.getDialogAPDU().getType() == DialogAPDUType.Response)
			{
				DialogResponseAPDU dialogResponseAPDU = (DialogResponseAPDU) message.tcap.dialogPortion.getDialogAPDU();
				message.setMAP_Version(dialogResponseAPDU.getApplicationContextName());
			}
			

			decodeComponentPart();
		}

		catch (IOException e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error("IO Exception occured while decoding Dialog Portion : " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			MyLoggerFactory.getInstance().getAppLogger().error("ASN1 Exception occured while decoding Dialog Portion : " + e.getMessage(), e);
		}

	}



	private void decodeComponentPart()
	{
		ComponentType compType;
		for(Component comp : undefinedComponents)
		{
			compType = comp.getType();

			switch (compType)
			{
				case Invoke:
					Invoke invoke = (Invoke) comp;
					definedComponents.add(invoke);
					break;

				case ReturnResultLast:
					ReturnResultLast rrl = (ReturnResultLast) comp;
					definedComponents.add(rrl);
					break;

				case ReturnResult:
					ReturnResult rr = (ReturnResult) comp;
					definedComponents.add(rr);
					break;

				case ReturnError:
					ReturnError re = (ReturnError) comp;
					definedComponents.add(re);
					break;

				case Reject:
					Reject reject = (Reject) comp;
					definedComponents.add(reject);
					break;
				default:
					break;
			}
		}	
	}

	public  void encode(ByteArray tcap)
	{
		AsnOutputStream aos = new AsnOutputStream();
		try
		{
			if(tcMessage instanceof TCBeginMessage)
			{
				((TCBeginMessage)tcMessage).encode(aos);
			}
			else if(tcMessage instanceof TCContinueMessage)
			{
				((TCContinueMessage)tcMessage).encode(aos);
			}
			else if(tcMessage instanceof TCEndMessage)
			{
				((TCEndMessage)tcMessage).encode(aos);
			}
			else if(tcMessage instanceof TCAbortMessage)
			{
				((TCAbortMessage)tcMessage).encode(aos);
			}
			else
			{
				//Issue Found
			}
			tcap.write((byte) aos.toByteArray().length);
			tcap.write(aos.toByteArray());
			
		}
		catch (EncodeException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
