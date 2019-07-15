/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.mobicents.protocols.sctp.netty.multihome;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import io.netty.buffer.Unpooled;
import javolution.util.FastList;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.sctp.SctpTransferTest;
import org.mobicents.protocols.sctp.netty.NettyAssociationImpl;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;
import org.mobicents.protocols.sctp.netty.NettyServerImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

/**
 * <p>
 * This test is for SCTP Multihoming. Make sure you change SERVER_HOST1 and
 * CLIENT_HOST1 to match your current ip before you run this test.
 * <p>
 * <p>
 * Once this test is started you can randomly bring down loop back interface or
 * real interafce and see that traffic still continues.
 * </p>
 * <p>
 * This is not automated test. Please don't add in automation.
 * </p>
 * 
 * @author amit bhayani
 * 
 */
public class NettySctpServerTest {
	private static int tps = 100;
    private static final String SERVER_NAME = "testserver";
    private static String SERVER_HOST = "127.0.0.1";
    private static final String SERVER_HOST1 = "192.168.0.228"; // "10.0.2.15"

    private static int SERVER_PORT = 2350;

    private static final String SERVER_ASSOCIATION_NAME = "serverAssociation";
    private static final String CLIENT_ASSOCIATION_NAME = "clientAssociation";

    private static String CLIENT_HOST = "127.0.0.1";
    private static final String CLIENT_HOST1 = "192.168.0.228"; // "10.0.2.15"

    private static int CLIENT_PORT = 2351;

    private final String CLIENT_MESSAGE = "Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi Client says Hi ";
    private final String SERVER_MESSAGE = "Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi Server says Hi ";

    private NettySctpManagementImpl management = null;

    // private Management managementClient = null;
    private NettyServerImpl server = null;

    private NettyAssociationImpl serverAssociation = null;
    private NettyAssociationImpl clientAssociation = null;

    private volatile boolean clientAssocUp = false;
    private volatile boolean serverAssocUp = false;

    private volatile boolean clientAssocDown = false;
    private volatile boolean serverAssocDown = false;

    private FastList<String> clientMessage = null;
    private FastList<String> serverMessage = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
	public NettySctpServerTest(String sERVER_HOST, int sERVER_PORT, String cLIENT_HOST, int cLIENT_PORT, int TPS) {
		super();
		SERVER_HOST = sERVER_HOST;
		SERVER_PORT = sERVER_PORT;
		CLIENT_HOST = cLIENT_HOST;
		CLIENT_PORT = cLIENT_PORT;
		tps = TPS;
	}

    public void setUp(IpChannelType ipChannelType) throws Exception {
//        this.clientAssocUp = false;
        this.serverAssocUp = false;

//        this.clientAssocDown = false;
        this.serverAssocDown = false;

//        this.clientMessage = new FastList<String>();
        this.serverMessage = new FastList<String>();

        this.management = new NettySctpManagementImpl("server-management");
////        this.management.setSingleThread(true);
        this.management.start();
        this.management.setConnectDelay(10000);// Try connecting every 10 secs
        this.management.removeAllResourses();

        this.server = (NettyServerImpl) this.management.addServer(SERVER_NAME, SERVER_HOST, SERVER_PORT, ipChannelType, false, 0, new String[] { SERVER_HOST1 });
        this.serverAssociation = (NettyAssociationImpl) this.management.addServerAssociation(CLIENT_HOST, CLIENT_PORT, SERVER_NAME, SERVER_ASSOCIATION_NAME, ipChannelType);
//        this.clientAssociation = (NettyAssociationImpl) this.management.addAssociation(CLIENT_HOST, CLIENT_PORT, SERVER_HOST, SERVER_PORT, CLIENT_ASSOCIATION_NAME, ipChannelType, new String[] { CLIENT_HOST1 });         
    }

    public void tearDown() throws Exception {

//        this.management.removeAssociation(CLIENT_ASSOCIATION_NAME);
        this.management.removeAssociation(SERVER_ASSOCIATION_NAME);
        this.management.removeServer(SERVER_NAME);

        this.management.stop();
    }

    /**
     * Simple test that creates Client and Server Association, exchanges data
     * and brings down association. Finally removes the Associations and Server
     */
    @Test(groups = { "functional", "sctp-multihome" })
    public void testDataTransferSctp() throws Exception {

        // Testing only is sctp is enabled
        if (!SctpTransferTest.checkSctpEnabled())
            return;
        
        this.setUp(IpChannelType.SCTP);

        this.management.startServer(SERVER_NAME);

        this.serverAssociation.setAssociationListener(new ServerAssociationListener());
        this.management.startAssociation(SERVER_ASSOCIATION_NAME);

//        this.clientAssociation.setAssociationListener(new ClientAssociationListener());
//        this.management.startAssociation(CLIENT_ASSOCIATION_NAME);

        for (int i1 = 0; i1 < 40; i1++) {
            if (serverAssocUp)
                break;
            Thread.sleep(1000 * 5); // was: 40
        }
        Thread.sleep(1000 * 15000); // was: 40

//        this.management.stopAssociation(CLIENT_ASSOCIATION_NAME);

//        Thread.sleep(1000);

        this.management.stopAssociation(SERVER_ASSOCIATION_NAME);
        this.management.stopServer(SERVER_NAME);

        Thread.sleep(1000 * 2);

        // assertTrue(Arrays.equals(SERVER_MESSAGE, clientMessage));
        // assertTrue(Arrays.equals(CLIENT_MESSAGE, serverMessage));

//        assertTrue(clientAssocUp);
        assertTrue(serverAssocUp);

//        assertTrue(clientAssocDown);
        assertTrue(serverAssocDown);

        Runtime runtime = Runtime.getRuntime();

        this.tearDown();
    }

    private class ClientAssociationListener implements AssociationListener {
        
        private LoadGenerator loadGenerator = null;

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationUp
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationUp(Association association, int maxInboundStreams, int maxOutboundStreams) {
            MyLoggerFactory.getInstance().getAPILogger().info(" onCommunicationUp");

            clientAssocUp = true;
            loadGenerator = new LoadGenerator(association, CLIENT_MESSAGE);
            (new Thread(loadGenerator)).start();

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationShutdown
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationShutdown(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn( " onCommunicationShutdown");
            clientAssocDown = true;
            loadGenerator.stop();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationLost
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationLost(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn(" onCommunicationLost");
            loadGenerator.stop();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationRestart
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationRestart(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn(" onCommunicationRestart");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onPayload(org.mobicents
         * .protocols.sctp.Association,
         * org.mobicents.protocols.sctp.PayloadData)
         */
        @Override
        public void onPayload(Association association, PayloadData payloadData) {
            byte[] data = new byte[payloadData.getDataLength()];
            payloadData.getByteBuf().readBytes(data);
            String rxMssg = new String(data);
            //MyLoggerFactory.getInstance().getAPILogger().debug("CLIENT received " + rxMssg);
            //clientMessage.add(rxMssg);

        }

        /* (non-Javadoc)
         * @see org.mobicents.protocols.api.AssociationListener#inValidStreamId(org.mobicents.protocols.api.PayloadData)
         */
        @Override
        public void inValidStreamId(PayloadData payloadData) {
            // TODO Auto-generated method stub
            
        }

    }

    private class LoadGenerator implements Runnable {

        private String message = null;
        private Association association;
        private volatile boolean started = true;

        LoadGenerator(Association association, String message) {
            this.association = association;
            this.message = message;
        }

        void stop() {
            this.started = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
        	

			Date start = new Date();
			long counter = 0;
			float tpsFactor = tps/834.0f;
			while (started) {
				byte[] data = (this.message + counter).getBytes();
				PayloadData payloadData = new PayloadData(data.length, Unpooled.copiedBuffer(data), true, false, 3, 1);

				try {
					this.association.send(payloadData);
					payloadData = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					counter++;
					if (counter % 10000 == 0) {
						Date end = new Date();
						float diff = (end.getTime() - start.getTime())/1000f;						
						System.out.println("Time: "+end+", Association: " +association.getName() +", TPS: "+ counter/diff);
						end = null;
					}
					if (tpsFactor > 1 && counter % (int)tpsFactor == 0) {
						Thread.sleep(1);						
					} else if (tpsFactor <= 1 && counter % (int)(tpsFactor * 10) == 0) {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
        }

    }

    private class ServerAssociationListener implements AssociationListener {
        private LoadGenerator loadGenerator = null;

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationUp
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationUp(Association association, int maxInboundStreams, int maxOutboundStreams) {
            MyLoggerFactory.getInstance().getAPILogger().info(" onCommunicationUp");
            System.out.println("onCommunicationUp ...");

            serverAssocUp = true;

            loadGenerator = new LoadGenerator(association, SERVER_MESSAGE);
            System.out.println("load generator started ...");

            (new Thread(loadGenerator)).start();
            
            System.out.println("thread started ...");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationShutdown
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationShutdown(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn(" onCommunicationShutdown");
            serverAssocDown = true;
            loadGenerator.stop();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationLost
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationLost(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn(" onCommunicationLost");
            loadGenerator.stop();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onCommunicationRestart
         * (org.mobicents.protocols.sctp.Association)
         */
        @Override
        public void onCommunicationRestart(Association association) {
            MyLoggerFactory.getInstance().getAPILogger().warn(" onCommunicationRestart");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.mobicents.protocols.sctp.AssociationListener#onPayload(org.mobicents
         * .protocols.sctp.Association,
         * org.mobicents.protocols.sctp.PayloadData)
         */
        @Override
        public void onPayload(Association association, PayloadData payloadData) {
            byte[] data = new byte[payloadData.getDataLength()];
            payloadData.getByteBuf().readBytes(data);
            String rxMssg = new String(data);
            //MyLoggerFactory.getInstance().getAPILogger().debug("SERVER received " + rxMssg);
            //serverMessage.add(rxMssg);
        }

        /* (non-Javadoc)
         * @see org.mobicents.protocols.api.AssociationListener#inValidStreamId(org.mobicents.protocols.api.PayloadData)
         */
        @Override
        public void inValidStreamId(PayloadData payloadData) {
            // TODO Auto-generated method stub
            
        }

    }
    
	public static void main(String[] args) {
		NettySctpServerTest instance = new NettySctpServerTest(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		try {
			instance.testDataTransferSctp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
