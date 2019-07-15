package com.globitel.m3ua.commons;


import org.restcomm.protocols.ss7.mtp.Mtp3EndCongestionPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3PausePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3ResumePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3StatusPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPartListener;

import com.globitel.m3ua.dialogic.DialogicWriter;

public class Mtp3UserPartListenerImpl implements Mtp3UserPartListener {

    //private FastList<Mtp3TransferPrimitive> receivedData = new FastList<Mtp3TransferPrimitive>();

   /* public FastList<Mtp3TransferPrimitive> getReceivedData() {
        return receivedData;
    }*/

    @Override
    public void onMtp3PauseMessage(Mtp3PausePrimitive arg0) {
        Application.logger.info("Mtp3PausePrimitive, type=" + arg0.getType() + ", DPC=" + arg0.getAffectedDpc());

    }

    @Override
    public void onMtp3ResumeMessage(Mtp3ResumePrimitive arg0) {
        // TODO Auto-generated method stub
    	Application.logger.info("onMtp3ResumeMessage, type=" + arg0.getType() + ", DPC=" + arg0.getAffectedDpc());

    }

    @Override
    public void onMtp3StatusMessage(Mtp3StatusPrimitive arg0) {
        // TODO Auto-generated method stub
    	Application.logger.info("onMtp3StatusMessage, type=" + arg0.getType() + ", DPC=" + arg0.getAffectedDpc());
    }

    @Override
    public void onMtp3TransferMessage(Mtp3TransferPrimitive value) {
       // receivedData.add(value);
    	if(0 == Application.WRITER_MODULE_ID)
    	{
    		Application.logger.info("Receiving M3UA message and dropping it");
    	}
    	else
    	{
    		DialogicWriter.AddM3UAMsg(value);
    	}
    }

    @Override
    public void onMtp3EndCongestionMessage(Mtp3EndCongestionPrimitive msg) {
        // TODO Auto-generated method stub
        
    }

}
