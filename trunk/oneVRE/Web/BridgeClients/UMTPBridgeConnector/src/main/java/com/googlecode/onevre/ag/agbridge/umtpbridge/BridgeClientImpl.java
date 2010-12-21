/*
 * @(#)BridgeClientImpl.java
 * Created: 24 May 2008
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

package com.googlecode.onevre.ag.agbridge.umtpbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeClientImpl implements BridgeClient {

    private static final byte VERSION = 0;

    private static final long MILLIS_BETWEEN_JOINS = 15000;

    private DatagramSocket socket = null;

    private InetAddress host = null;

    private int port = 0;

    private int sourceCookie = 0;

    private int destinationCookie = 0;

    private JoinThread[] joinThreads = null;

    private LinkedList<DatagramPacket> packetQueue =
        new LinkedList<DatagramPacket>();

    private Boolean probeResult = new Boolean(false);

    private class JoinThread extends Thread {

        private boolean done = false;

        private DatagramPacket packet = null;

        private JoinThread(byte[] sourceAddress, byte[] multicastAddress,
                int multicastPort, short ttl) {
            byte[] data = null;
            if (sourceAddress != null) {
                data = new byte[16];
            } else {
                data = new byte[12];
            }

            Trailer trailer = new Trailer(sourceCookie, destinationCookie,
                    multicastAddress, multicastPort, ttl, VERSION,
                    Trailer.COMMAND_JOIN_RTP_GROUP);
            trailer.addToBytes(data, 0, data.length);
            packet = new DatagramPacket(data, data.length, host, port);
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(MILLIS_BETWEEN_JOINS);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }

        private void close() {
            done = true;
        }
    }

    private class ListenThread extends Thread {

        private boolean done = false;

        public void run() {
            while (!done) {
                try {
                    byte[] data = new byte[socket.getReceiveBufferSize()];
                    DatagramPacket packet =
                        new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    synchronized (packetQueue) {
                        packetQueue.addLast(packet);
                        packetQueue.notifyAll();
                    }
                } catch (IOException e) {
                    if (!done) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void close() {
            done = true;
        }
    }

    /**
     * @see ag3.bridge.BridgeClient#getOrder()
     */
    public int getOrder() {
        return BridgeClient.ORDER_GREAT_BRIDGE;
    }

    /**
     * @see ag3.bridge.BridgeClient#getWarning()
     */
    public String getWarning() {
        return null;
    }

    /**
     * @see ag3.bridge.BridgeClient#init(java.net.InetAddress, int)
     */
    public void init(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sourceCookie = (short) (Math.random() * (Short.MAX_VALUE * 2));
        destinationCookie = (short) (Math.random() * (Short.MAX_VALUE * 2));
    }

    /**
     * @see ag3.bridge.BridgeClient#isSinglePacketStream()
     */
    public boolean isSinglePacketStream() {
        return true;
    }

    /**
     * @see ag3.bridge.BridgeClient#joinBridge(
     *     ag3.interfaces.types.NetworkLocation[])
     */
    public void joinBridge(NetworkLocation[] locations) throws IOException {
        if (socket == null) {
            throw new IOException("Socket was not created");
        }
        joinThreads = new JoinThread[locations.length];
        for (int i = 0; i < locations.length; i++) {
            InetAddress address = InetAddress.getByName(locations[i].getHost());
            int port = locations[i].getPort();
            short ttl = 127;
            if (locations[i] instanceof MulticastNetworkLocation) {
                ttl = (short) ((MulticastNetworkLocation)
                        locations[i]).getTtl();
            }
            joinThreads[i] = new JoinThread(null, address.getAddress(),
                    port, ttl);
            joinThreads[i].start();
        }
    }

    /**
     * @see ag3.bridge.BridgeClient#leaveBridge()
     */
    public void leaveBridge() throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * @see ag3.bridge.BridgeClient#ping()
     */
    public void ping() throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * @see ag3.bridge.BridgeClient#receivePacket(
     *     ag3.interfaces.types.NetworkLocation)
     */
    public DatagramPacket receivePacket(NetworkLocation location)
            throws IOException {
        return null;
    }

    /**
     * @see ag3.bridge.BridgeClient#receivePacket()
     */
    public DatagramPacket receivePacket() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see ag3.bridge.BridgeClient#sendPacket(java.net.DatagramPacket,
     *     ag3.interfaces.types.NetworkLocation)
     */
    public void sendPacket(DatagramPacket packet, NetworkLocation location)
            throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * @see ag3.bridge.BridgeClient#setReceiveTimeout(int)
     */
    public void setReceiveTimeout(int timeout) throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * @see ag3.bridge.BridgeClient#setDoLoopback(boolean)
     */
    public void setDoLoopback(boolean doLoopback) throws SocketException {
        // Does Nothing
    }

}
