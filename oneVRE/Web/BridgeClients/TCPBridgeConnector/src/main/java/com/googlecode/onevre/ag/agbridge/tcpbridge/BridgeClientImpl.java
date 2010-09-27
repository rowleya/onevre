/*
 * @(#)BridgeClientImpl.java
 * Created: 24 Oct 2007
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

package com.googlecode.onevre.ag.agbridge.tcpbridge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.bridges.tcpbridge.common.ClientReader;
import com.googlecode.onevre.bridges.tcpbridge.common.ClientWriter;


/**
 * A Bridge Client for the TCP Bridge
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeClientImpl implements BridgeClient {

    private static final int TIMEOUT = 1000;

    private static final int CODE_LENGTH = 26;

    private static final int LF_CHAR = 10;

    private static final int CR_CHAR = 13;

    private static final int SOCKET_BUFFER_SIZE = 80000;

    private InetAddress host = null;

    private int port = 0;

    private Socket listenSocket = null;

    private Socket sendSocket = null;

    private HashMap<NetworkLocation, InetSocketAddress> locs =
        new HashMap<NetworkLocation, InetSocketAddress>();

    private ClientWriter writer = null;

    private ClientReader reader = null;

    private int lastChar = -1;

    private void writeString(String string, OutputStream output)
            throws IOException {
        output.write(string.getBytes("UTF-8"));
    }

    private Socket connectHttpSocket(InetAddress host, int port)
            throws URISyntaxException,  IOException {
        List<Proxy> proxies = ProxySelector.getDefault().select(
                new URI("http", null, host.getHostAddress(), port, null, null,
                        null));
        Iterator<Proxy> iter = proxies.iterator();
        InetAddress proxyHost = host;
        int proxyPort = port;
        while (iter.hasNext()) {
            Proxy proxy = iter.next();
            if (proxy.type().equals(Proxy.Type.HTTP)) {
                InetSocketAddress address = (InetSocketAddress) proxy.address();
                proxyHost = address.getAddress();
                proxyPort = address.getPort();
            }
        }
        return new Socket(proxyHost, proxyPort);
    }

    private String readLine(InputStream input) throws IOException {
        int c = lastChar;
        String string = null;
        if (lastChar == -1) {
            c = input.read();
        }
        while ((c > 0) && (c != CR_CHAR) && (c != LF_CHAR)) {
            if (string == null) {
                string = "";
            }
            string += (char) c;
            c = input.read();
        }
        lastChar = -1;
        if (c == CR_CHAR) {
            c = input.read();
            if (c != LF_CHAR) {
                lastChar = c;
            }
        }
        return string;
    }

    private HashMap<String, String> readHeaders(InputStream input)
            throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        String line = readLine(input);
        while ((line != null) && !line.equals("")) {
            String[] parts = line.split(":");
            headers.put(parts[0].trim(), parts[1].trim());
            line = readLine(input);
        }
        return headers;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#init(java.net.InetAddress, int)
     */
    public void init(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#ping()
     */
    public void ping() throws IOException {
        Socket sock = new Socket(host, port);
        sock.setSoTimeout(TIMEOUT);
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        DataInputStream in = new DataInputStream(sock.getInputStream());

        writeString("GET http://" + host.getHostName() + ":" + port
                + "/ HTTP/1.0\r\n", out);
        writeString("Content-Type: application/octet-stream\r\n", out);
        writeString("Content-Length: " + Integer.MAX_VALUE + "\r\n", out);
        writeString("Pragma: no-cache\r\n", out);
        writeString("Cache-Control: no-cache\r\n", out);
        writeString("x-sessioncookie: None\r\n", out);
        writeString("\r\n", out);
        out.writeInt(0);
        in.readInt();
        sock.close();
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#joinBridge(ag3.interfaces.types.NetworkLocation[])
     */
     public void joinBridge(NetworkLocation[] locations)
            throws IOException {
        if (locations.length == 0) {
            return;
        }

        Random rand = new Random();
        String session = Long.toString(Math.abs(rand.nextLong()), CODE_LENGTH);
        try {
            listenSocket = connectHttpSocket(host, port);
        } catch (URISyntaxException e) {
           throw new IOException(e.getMessage());
        }
        OutputStream outputStream = listenSocket.getOutputStream();
        InputStream input = listenSocket.getInputStream();
        DataOutputStream output = new DataOutputStream(outputStream);

        System.err.println("Writing request...");
        writeString("GET http://" + host.getHostName() + ":" + port
                + "/ HTTP/1.0\r\n", output);
        writeString("Content-Type: application/octet-stream\r\n", output);
        writeString("Content-Length: " + Integer.MAX_VALUE + "\r\n", output);
        writeString("Pragma: no-cache\r\n", output);
        writeString("Cache-Control: no-cache\r\n", output);
        writeString("x-sessioncookie: " + session + "\r\n", output);
        writeString("\r\n", output);

        output.writeInt(locations.length * 2);
        for (int i = 0; i < locations.length; i++) {
            InetAddress listenAddress = InetAddress.getByName(
                    locations[i].getHost());
            InetAddress sendAddress = InetAddress.getByName("224.0.24.124");

            output.write(listenAddress.getAddress());
            output.writeInt(locations[i].getPort());
            output.write(sendAddress.getAddress());
            output.writeInt(locations[i].getPort());
            output.write(listenAddress.getAddress());
            output.writeInt(locations[i].getPort() + 1);
            output.write(sendAddress.getAddress());
            output.writeInt(locations[i].getPort() + 1);

            if (locations[i] instanceof MulticastNetworkLocation) {
                MulticastNetworkLocation rtcpLocation =
                    new MulticastNetworkLocation();
                rtcpLocation.setHost(locations[i].getHost());
                rtcpLocation.setPort(locations[i].getPort() + 1);
                rtcpLocation.setTtl(((MulticastNetworkLocation)
                        locations[i]).getTtl());
                InetSocketAddress mappedAddress = new InetSocketAddress(
                        sendAddress, locations[i].getPort());
                InetSocketAddress rtcpMappedAddress = new InetSocketAddress(
                        sendAddress, rtcpLocation.getPort());
                locs.put(locations[i], mappedAddress);
                locs.put(rtcpLocation, rtcpMappedAddress);
            } else {
                UnicastNetworkLocation rtcpLocation =
                    new UnicastNetworkLocation();
                rtcpLocation.setHost(locations[i].getHost());
                rtcpLocation.setPort(locations[i].getPort() + 1);
                InetSocketAddress mappedAddress = new InetSocketAddress(
                        sendAddress, locations[i].getPort());
                InetSocketAddress rtcpMappedAddress = new InetSocketAddress(
                        sendAddress, rtcpLocation.getPort());
                locs.put(locations[i], mappedAddress);
                locs.put(rtcpLocation, rtcpMappedAddress);
            }
        }
        output.flush();
        String response = readLine(input);
        if (response.endsWith("200 OK")) {
            readHeaders(input);
            try {
                sendSocket = connectHttpSocket(host, port);
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
            sendSocket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
            DataOutputStream sendOutput = new DataOutputStream(
                    sendSocket.getOutputStream());
            writeString("POST http://" + host.getHostName() + ":" + port
                    + "/ HTTP/1.0\r\n", sendOutput);
            writeString("Content-Type: application/octet-stream\r\n",
                    sendOutput);
            writeString("Content-Length: " + Integer.MAX_VALUE + "\r\n",
                    sendOutput);
            writeString("Pragma: no-cache\r\n", sendOutput);
            writeString("Cache-Control: no-cache\r\n", sendOutput);
            writeString("x-sessioncookie: " + session + "\r\n", sendOutput);
            writeString("\r\n", sendOutput);
            sendOutput.flush();

            reader = new ClientReader(input);
            writer = new ClientWriter(sendOutput);
            reader.start();
            writer.start();
        } else {
            listenSocket.close();
            throw new IOException("Invalid response: " + response);
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#leaveBridge()
     */
    public void leaveBridge() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (listenSocket != null) {
            listenSocket.close();
        }
        if (sendSocket != null) {
            sendSocket.close();
        }
        locs.clear();
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#receivePacket(
     *     ag3.interfaces.types.NetworkLocation)
     */
    public DatagramPacket receivePacket(NetworkLocation location) {
        return null;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#sendPacket(java.net.DatagramPacket,
     *     ag3.interfaces.types.NetworkLocation)
     */
    public void sendPacket(DatagramPacket packet,
            NetworkLocation location) {
        InetSocketAddress address = locs.get(location);
        if (address != null) {
            writer.addPacket(packet, address);
        }
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#isSinglePacketStream()
     */
    public boolean isSinglePacketStream() {
        return true;
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#receivePacket()
     */
    public DatagramPacket receivePacket() {
        return reader.getNextPacket();
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#setReceiveTimeout(int)
     */
    public void setReceiveTimeout(int timeout) throws IOException {
        listenSocket.setSoTimeout(timeout);
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#getWarning()
     */
    public String getWarning() {
        return "Warning:  This connection is using the TCP Bridge.\n"
         + "This may result in a additional delays and losses versus other\n"
         + "bridge services.  For a better service, please ask your network\n"
         + "administrator to open additional ports in your firewall";
    }

    /**
     *
     * @see ag3.bridge.BridgeClient#getOrder()
     */
    public int getOrder() {
        return BridgeClient.ORDER_WORST_BRIDGE;
    }

}
