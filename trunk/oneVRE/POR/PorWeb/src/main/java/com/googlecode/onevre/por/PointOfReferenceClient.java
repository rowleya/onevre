/*
 * @(#)PointOfReferenceClient.java
 * Created: 10 Dec 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.por;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;


/**
 * A client for the PointOfReferenceServer
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PointOfReferenceClient {

    private static final int IP_ADDRESS_LENGTH = 4;

    private static final int DEFAULT_RECV_COUNT = 200;

    private static final int DEFAULT_SEND_COUNT = 10;

    private static final int CODE_LENGTH = 36;

    private URL url = null;

    private String code = null;

    private class SendThread extends Thread {

        private static final int SEND_DELAY_TIME = 100;

        private BridgeClient bridge = null;

        private NetworkLocation location = null;

        private boolean done = false;

        private SendThread(BridgeClient bridge, NetworkLocation location) {
            this.bridge = bridge;
            this.location = location;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                byte[] data = code.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length);
                for (int i = 0; (i < DEFAULT_SEND_COUNT) && !done; i++) {
                    bridge.sendPacket(packet, location);
                    try {
                        Thread.sleep(SEND_DELAY_TIME);
                    } catch (InterruptedException e) {
                        // Does Nothing
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void close() {
            done = true;
        }
    }

    private class ReceiveThread extends Thread {

        private boolean found = false;

        private boolean done = false;

        private BridgeClient bridge = null;

        private NetworkLocation location = null;

        private ReceiveThread(BridgeClient bridge, NetworkLocation location) {
            this.bridge = bridge;
            this.location = location;
        }

        private boolean isFound() {
            synchronized (this) {
                while (!done) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
            }
            return found;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            found = false;
            done = false;
            int count = DEFAULT_RECV_COUNT;
            while (!found && (count > 0)) {
                try {
                    DatagramPacket packet = null;
                    if (bridge.isSinglePacketStream()) {
                        packet = bridge.receivePacket();
                    } else {
                        packet = bridge.receivePacket(location);
                    }
                    String packetCode = new String(packet.getData(),
                            packet.getOffset(), packet.getLength(), "UTF-8");
                    if (packetCode.equals(code)) {
                        found = true;
                    }
                } catch (IOException e) {
                    count = 0;
                }
                count -= 1;
            }
            synchronized (this) {
                done = true;
                notifyAll();
            }
        }
    }

    /**
     * Creates a new PointOfReferenceClient
     * @param url The url of the server
     * @throws IOException
     */
    public PointOfReferenceClient(String ... url) throws IOException{
		Random rand = new Random();
		System.err.println("PointOfReferenceClient - Set Point of Reference:");
		code = Long.toString(Math.abs(rand.nextLong()), CODE_LENGTH);
		for (String u : url) {
			try {
				System.err.print("trying - " + u.toString());
				URL server = new URL(u);
				HttpURLConnection connection = (HttpURLConnection) server
						.openConnection();
				connection.connect();
				connection.getInputStream();
				this.url = server;
				System.err.println(" - success ");
				break;
			} catch (IOException e) {
				System.err.println(" - failed ");
				// do nothing
			}
		}
		if (this.url==null){
            JOptionPane.showMessageDialog(null,
                    "Error finding Point of Reference Server.\n"
                    + "Please manually change the Point of reference by clicking on the "
                    + "Mutlicast / Unicast configuration icon.",
                    "Point of Reference Error", JOptionPane.ERROR_MESSAGE);
		}
	}

    /**
     * Tests if packets sent remotely are received locally
     * @param bridge The bridge through which packets are to be received
     * @param location The location from which packets are to be received
     * @return True if the sent packets are received locally
     * @throws IOException
     */
    public boolean testReceive(BridgeClient bridge, NetworkLocation location)
            throws IOException {
    	if (url==null) {
    		return false;
    	}
        ReceiveThread receive = new ReceiveThread(bridge, location);
        receive.start();
        URL server = new URL(url.toString() + "?ping=1&code=" + code
                + "&address=" + location.getHost()
                + "&port=" + location.getPort()
                + "&timeout=1000&count=" + DEFAULT_SEND_COUNT + "&send=1");
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        connection.getInputStream();
        return receive.isFound();
    }

    /**
     * Tests if packets sent locally are received remotely
     * @param bridge The bridge through which packets are to be sent
     * @param location The location to send packets to
     * @return True if the packets were received, false otherwise
     * @throws IOException
     */
    public boolean testSend(BridgeClient bridge, NetworkLocation location)
            throws IOException {
    	if (url == null) {
    		return false;
    	}
        URL server = new URL(url.toString() + "?ping=1&code=" + code
                + "&address=" + location.getHost()
                + "&port=" + location.getPort()
                + "&timeout=1000&count=" + DEFAULT_RECV_COUNT + "&recv=1");
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        SendThread send = new SendThread(bridge, location);
        send.start();
        InputStream input = connection.getInputStream();
        byte[] addr = new byte[IP_ADDRESS_LENGTH];
        int bytesRead = 0;
        while (bytesRead < addr.length) {
            int result = input.read(addr, bytesRead, addr.length - bytesRead);
            if (result < 0) {
                return false;
            }
            bytesRead += result;
        }
        boolean allZeros = true;
        for (int i = 0; i < addr.length; i++) {
            if (addr[i] != 0) {
                allZeros = false;
            }
        }
        send.close();
        return !allZeros;
    }

    /**
     * Starts the monitoring of locations
     * @param locations The locations to monitor
     * @param encryptionKeys The encryption keys to use
     * @throws IOException
     */
    public void startMonitor(NetworkLocation[] locations,
            String[] encryptionKeys) throws IOException {
        if (url!=null){
	        String addresses = "";
	        for (int i = 0; i < locations.length; i++) {
	            addresses += "&address=" + locations[i].getHost();
	            addresses += "&port=" + locations[i].getPort();
	            addresses += "&key=" + encryptionKeys[i];
	        }
	        URL server = new URL(url.toString() + "?listen=1&id=" + code
	                + addresses);
	        HttpURLConnection connection = (HttpURLConnection)
	            server.openConnection();
	        connection.connect();
	        connection.getInputStream();
        }
    }

    /**
     * Gets the number of packets received by the POR since the last call
     * @param ssrc The source to check
     * @return The number of packets
     * @throws IOException
     */
    public int getPacketCount(long ssrc)
            throws IOException {
    	if (url==null) {
    		return 0;
    	}
        URL server = new URL(url.toString() + "?getCount=1&id=" + code
                + "&ssrc=" + ssrc);
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        return input.readInt();
    }

    /**
     * Gets the fraction of packets missing by the POR since the last call
     * @param ssrc The source to check
     * @return The number of packets
     * @throws IOException
     */
    public float getMissingFraction(long ssrc)
            throws IOException {
    	if (url==null){
    		return 1;
    	}
        URL server = new URL(url.toString() + "?getMissingFraction=1&id=" + code
                + "&ssrc=" + ssrc);
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        return input.readFloat();
    }

    /**
     * Stops the monitoring
     * @throws IOException
     */
    public void stopMonitor() throws IOException {
    	if (url!=null){
	        URL server = new URL(url.toString() + "?stoplisten=1&id=" + code);
	        HttpURLConnection connection = (HttpURLConnection)
	            server.openConnection();
	        connection.connect();
	        connection.getInputStream();
    	}
    }

    /**
     * Gets the ssrcs at the remote location
     * @return The list of ssrcs
     * @throws IOException
     */
    public Vector<Long> getSSRCs() throws IOException {
    	if (url==null){
    		return new Vector<Long>(0);
    	}
        URL server = new URL(url.toString() + "?getSSRCs=1&id=" + code);
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        int length = input.readInt();
        Vector<Long> ssrcs = new Vector<Long>(length);
        for (int i = 0; i < length; i++) {
            ssrcs.add(input.readLong());
        }
        return ssrcs;
    }

    /**
     * Gets the bridges that have tested OK by the server
     * @return The list of OK Bridges
     * @throws IOException
     */
    public BridgeDescription[] getOKBridges() throws IOException {
    	if (url==null){
    		return new BridgeDescription[0];
    	}
    	URL server = new URL(url.toString() + "?getOKBridges=1");
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        int length = input.readInt();
        BridgeDescription[] bridges = new BridgeDescription[length];
        for (int i = 0; i < bridges.length; i++) {
            bridges[i] = new BridgeDescription();
            bridges[i].setName(input.readUTF());
            bridges[i].setDescription(input.readUTF());
            bridges[i].setGuid(input.readUTF());
            bridges[i].setHost(input.readUTF());
            bridges[i].setPort(input.readInt());
            bridges[i].setServerType(input.readUTF());
            bridges[i].setPortMin(input.readInt());
            bridges[i].setPortMax(input.readInt());
        }
        return bridges;
    }

    /**
     * Gets the parameters used to test a connection
     * @return The test parameters
     * @throws IOException
     */
    public ConnectionParameters getConnectionParameters() throws IOException {
    	if (url==null){
    		return new ConnectionParameters(null,0,0);
    	}
        URL server = new URL(url.toString() + "?getConnectParameters=1");
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        MulticastNetworkLocation location = new MulticastNetworkLocation();
        location.setHost(input.readUTF());
        location.setPort(input.readInt());
        long multissrc = input.readLong();
        long ssrc = input.readLong();
        return new ConnectionParameters(location, multissrc, ssrc);
    }

    /**
     * Releases an ssrc from the returned ConnectionParameters
     * @param ssrc The ssrc to release
     * @throws IOException
     */
    public void releaseSSRC(long ssrc) throws IOException {
    	if (url!=null){
	    	URL server = new URL(url.toString() + "?releaseSSRC=" + ssrc);
	        HttpURLConnection connection = (HttpURLConnection)
	            server.openConnection();
	        connection.connect();
	        connection.getInputStream();
    	}
    }

    /**
     * @return the URL string
     */
    public String getUrl(){
    	if (url!=null){
    		return url.toString();
    	}
    	return "";
    }
    /**
     * Gets the amount of loss seen for a given ssrc
     * @param ssrc The ssrc to get the loss for
     * @return The fraction of packets lost
     * @throws IOException
     */
    public float getLossFraction(long ssrc) throws IOException {
    	if (url==null){
    		return 100;
    	}
        URL server = new URL(url.toString() + "?getLostFraction=" + ssrc);
        HttpURLConnection connection = (HttpURLConnection)
            server.openConnection();
        connection.connect();
        DataInputStream input = new DataInputStream(
                connection.getInputStream());
        float loss = input.readFloat();
        return loss;
    }

    /**
     * Test method
     * @param args ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        PointOfReferenceClient por = new PointOfReferenceClient(
                "http://rosie.rcs.manchester.ac.uk:8080/por");
        BridgeDescription[] bridges = por.getOKBridges();
        for (int i = 0; i < bridges.length; i++) {
            System.err.println(bridges[i]);
        }
    }
}
