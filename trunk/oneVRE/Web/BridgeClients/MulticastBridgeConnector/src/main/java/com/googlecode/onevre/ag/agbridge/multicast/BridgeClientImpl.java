/*
 * @(#)BridgeClientImpl.java
 * Created: 25 Oct 2007
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

package com.googlecode.onevre.ag.agbridge.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;


/**
 * A bridge client for multicast connections
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeClientImpl implements BridgeClient {

    private HashMap<NetworkLocation, DatagramSocket> sockets =
        new HashMap<NetworkLocation, DatagramSocket>();

    /**
     *
     * @see ag3.bridge.BridgeClient#init(java.net.InetAddress, int)
     */
    public void init(InetAddress host, int port) {
        // Does Nothing
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#joinBridge(
     * ag3.interfaces.types.NetworkLocation[])
     */
    public void joinBridge(NetworkLocation[] locations)
            throws IOException {
        for (int i = 0; i < locations.length; i++) {
            if (locations[i] instanceof MulticastNetworkLocation) {
                int ttl = ((MulticastNetworkLocation) locations[i]).getTtl();
                MulticastNetworkLocation rtcpLocation =
                    new MulticastNetworkLocation();
                rtcpLocation.setHost(locations[i].getHost());
                rtcpLocation.setPort(locations[i].getPort() + 1);
                rtcpLocation.setTtl(ttl);
                MulticastSocket socket = new MulticastSocket(
                        locations[i].getPort());
                MulticastSocket rtcpSocket = new MulticastSocket(
                        rtcpLocation.getPort());
                socket.joinGroup(InetAddress.getByName(locations[i].getHost()));
                rtcpSocket.joinGroup(InetAddress.getByName(
                        rtcpLocation.getHost()));
                socket.setTimeToLive(ttl);
                rtcpSocket.setTimeToLive(ttl);
                socket.setLoopbackMode(true);
                rtcpSocket.setLoopbackMode(true);
                socket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(locations[i].getHost()),
                        locations[i].getPort()));
                rtcpSocket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(rtcpLocation.getHost()),
                        rtcpLocation.getPort()));
                sockets.put(locations[i], socket);
                sockets.put(rtcpLocation, rtcpSocket);
            } else {
                UnicastNetworkLocation rtcpLocation =
                    new UnicastNetworkLocation();
                rtcpLocation.setHost(locations[i].getHost());
                rtcpLocation.setPort(locations[i].getPort() + 1);
                DatagramSocket socket = new DatagramSocket(
                        locations[i].getPort());
                DatagramSocket rtcpSocket = new DatagramSocket(
                        rtcpLocation.getPort());
                socket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(locations[i].getHost()),
                        locations[i].getPort()));
                rtcpSocket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(rtcpLocation.getHost()),
                        rtcpLocation.getPort()));
                sockets.put(locations[i], socket);
                sockets.put(rtcpLocation, rtcpSocket);
            }
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#leaveBridge()
     */
    public void leaveBridge() throws IOException {
        Iterator<NetworkLocation> iterator =
            sockets.keySet().iterator();
        while (iterator.hasNext()) {
            NetworkLocation location = iterator.next();
            DatagramSocket socket = sockets.get(location);
            if (socket instanceof MulticastSocket) {
                ((MulticastSocket) socket).leaveGroup(
                        InetAddress.getByName(location.getHost()));
            }
            socket.close();
        }
        sockets.clear();
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#ping()
     */
    public void ping() {
        // Does Nothing
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#receivePacket(
     *     ag3.interfaces.types.NetworkLocation)
     */
    public DatagramPacket receivePacket(NetworkLocation location)
            throws IOException {
        DatagramSocket socket = sockets.get(location);
        if (socket != null) {
            byte[] data = new byte[socket.getReceiveBufferSize()];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            return packet;
        }
        return null;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#sendPacket(java.net.DatagramPacket,
     *     ag3.interfaces.types.NetworkLocation)
     */
    public void sendPacket(DatagramPacket packet,
            NetworkLocation location)
            throws IOException {
        DatagramSocket socket = sockets.get(location);
        packet.setAddress(InetAddress.getByName(location.getHost()));
        packet.setPort(location.getPort());
        socket.send(packet);
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#isSinglePacketStream()
     */
    public boolean isSinglePacketStream() {
        return false;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#receivePacket()
     */
    public DatagramPacket receivePacket() {
        return null;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#setReceiveTimeout(int)
     */
    public void setReceiveTimeout(int timeout) throws SocketException {
        System.err.println("Setting receive timeout to " + timeout);
        try {
            Iterator<NetworkLocation> iterator =
                sockets.keySet().iterator();
            while (iterator.hasNext()) {
                NetworkLocation location = iterator.next();
                System.err.println("Setting receive timeout for " + location);
                DatagramSocket socket = sockets.get(location);
                socket.setSoTimeout(timeout);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#getWarning()
     */
    public String getWarning() {
        return null;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#getOrder()
     */
    public int getOrder() {
        return BridgeClient.ORDER_BEST_BRIDGE;
    }

    public void setDoLoopback(boolean doLoopback) throws SocketException {
        Iterator<NetworkLocation> iterator =
            sockets.keySet().iterator();
        while (iterator.hasNext()) {
            NetworkLocation location = iterator.next();
            DatagramSocket socket = sockets.get(location);
            if (socket instanceof MulticastSocket) {
                ((MulticastSocket) socket).setLoopbackMode(!doLoopback);
            }
        }

    }
}
