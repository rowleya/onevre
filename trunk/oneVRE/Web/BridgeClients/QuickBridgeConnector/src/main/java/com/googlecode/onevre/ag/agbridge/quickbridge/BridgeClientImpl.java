/*
 * @(#)QuickBridgeClient.java
 * Created: 13-Nov-2006
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

package com.googlecode.onevre.ag.agbridge.quickbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.ProviderProfile;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLRPCSerializer;


/**
 * An AG3 Quick Bridge Client
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeClientImpl implements BridgeClient {

    private static final int TIMEOUT_MS = 2000;

    private XmlRpcClient client = null;

    private HashMap<NetworkLocation, UnicastNetworkLocation> locs =
        new HashMap<NetworkLocation, UnicastNetworkLocation>();

    private HashMap<NetworkLocation, DatagramSocket> sockets =
        new HashMap<NetworkLocation, DatagramSocket>();

    /**
     *
     * @see ag3.bridge.BridgeClient#init(java.net.InetAddress, int)
     */
    public void init(InetAddress host, int port) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + host.getHostAddress()
                    + ":" + port));
            config.setEnabledForExtensions(true);
            config.setConnectionTimeout(TIMEOUT_MS);
            config.setReplyTimeout(TIMEOUT_MS);
            client = new XmlRpcClient();
            client.setTransportFactory(new XmlRpcLiteHttpTransportFactory(
                    client));
            client.setConfig(config);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private UnicastNetworkLocation joinBridge(
            NetworkLocation location) throws IOException {
        try {
            Hashtable mnl = XMLRPCSerializer.serialize(location);
            Vector<Hashtable<Object, Object>> args =
                new Vector<Hashtable<Object, Object>>();
            args.add(mnl);
            HashMap unl = (HashMap) client.execute("JoinBridge", args);
            HashMap prov = (HashMap) unl.get("profile");
            ProviderProfile provider = new ProviderProfile();
            UnicastNetworkLocation unicastLocation =
                new UnicastNetworkLocation();
            provider.setName((String) prov.get("name"));
            provider.setLocation((String) prov.get("location"));
            unicastLocation.setHost((String) unl.get("host"));
            unicastLocation.setPort(((Integer) unl.get("port")).intValue());
            unicastLocation.setType((String) unl.get("type"));
            unicastLocation.setPrivateId((String) unl.get("privateId"));
            unicastLocation.setId((String) unl.get("id"));
            unicastLocation.setProfile(provider);
            return unicastLocation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    private void leaveBridge(UnicastNetworkLocation location) {
        try {
            Hashtable unl = XMLRPCSerializer.serialize(location);
            Vector<Hashtable<Object, Object>> args =
                new Vector<Hashtable<Object, Object>>();
            args.add(unl);
            client.execute("LeaveBridge", args);
        } catch (Exception e) {
            // Ignored as this is not universal for QuickBridge servers
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#ping()
     */
    public void ping() throws IOException {
        try {
            client.execute("Ping", new Object[]{new Integer(0)});
        } catch (XmlRpcException e) {
            throw new IOException(e.getMessage());
        }

    }

    /**
     *
     * @see ag3.bridge.BridgeClient#joinBridge(
     *     ag3.interfaces.types.NetworkLocation[])
     */
    public void joinBridge(NetworkLocation[] locations)
            throws IOException {
        for (int i = 0; i < locations.length; i++) {
            if (locations[i] instanceof MulticastNetworkLocation) {
                MulticastNetworkLocation rtcpLocation =
                    new MulticastNetworkLocation();
                rtcpLocation.setHost(locations[i].getHost());
                rtcpLocation.setPort(locations[i].getPort() + 1);
                rtcpLocation.setTtl(((MulticastNetworkLocation)
                        locations[i]).getTtl());
                UnicastNetworkLocation response = joinBridge(locations[i]);
                UnicastNetworkLocation rtcpResponse =
                    new UnicastNetworkLocation();
                rtcpResponse.setHost(response.getHost());
                rtcpResponse.setPort(response.getPort() + 1);
                DatagramSocket socket = new DatagramSocket(
                        response.getPort());
                DatagramSocket rtcpSocket = new DatagramSocket(
                        rtcpResponse.getPort());
                socket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(locations[i].getHost()),
                        locations[i].getPort()));
                rtcpSocket.send(new DatagramPacket(new byte[1], 1,
                        InetAddress.getByName(rtcpLocation.getHost()),
                        rtcpLocation.getPort()));
                sockets.put(locations[i], socket);
                sockets.put(rtcpLocation, rtcpSocket);
                locs.put(locations[i], response);
                locs.put(rtcpLocation, rtcpResponse);
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
                locs.put(locations[i], (UnicastNetworkLocation) locations[i]);
                locs.put(rtcpLocation, rtcpLocation);
            }
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#leaveBridge()
     */
    public void leaveBridge() {
        Iterator<DatagramSocket> iterator = sockets.values().iterator();
        while (iterator.hasNext()) {
            DatagramSocket socket = iterator.next();
            socket.close();
        }
        Iterator<NetworkLocation> iter = locs.keySet().iterator();
        while (iter.hasNext()) {
            NetworkLocation location = iter.next();
            UnicastNetworkLocation loc = locs.get(location);
            if (!loc.equals(location)) {
                leaveBridge(loc);
            }
        }
        sockets.clear();
        locs.clear();
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
            NetworkLocation location) throws IOException {
        DatagramSocket socket = sockets.get(location);
        UnicastNetworkLocation unicastLocation = locs.get(location);
        packet.setAddress(InetAddress.getByName(unicastLocation.getHost()));
        packet.setPort(unicastLocation.getPort());
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
    public void setReceiveTimeout(int timeout) throws IOException {
        Iterator<DatagramSocket> iterator = sockets.values().iterator();
        while (iterator.hasNext()) {
            DatagramSocket socket = iterator.next();
            socket.setSoTimeout(timeout);
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
        return 0;
    }

    /**
     * @see ag3.bridge.BridgeClient#setDoLoopback(boolean)
     */
    public void setDoLoopback(boolean doLoopback) throws SocketException {
        // Does Nothing
    }
}
