/*
 * @(#)Client.java
 * Created: 15 Oct 2007
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

package com.googlecode.onevre.bridges.tcpbridge.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.googlecode.onevre.bridges.tcpbridge.common.ClientReader;
import com.googlecode.onevre.bridges.tcpbridge.common.ClientWriter;

/**
 * The client of the TCP Bridge
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Client {

    private static final int CODE_LENGTH = 26;

    private static final int SOCKET_BUFFER_SIZE = 80000;

    private static final int LF_CHAR = 10;

    private static final int CR_CHAR = 13;

    private Socket sendSocket = null;

    private Socket listenSocket = null;

    private ClientReader reader = null;

    private ClientWriter writer = null;

    private int lastChar = -1;

    private void writeString(String string, OutputStream output)
            throws IOException {
        System.err.println("Writing: " + string);
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
                System.err.println("Proxy = " + proxyHost + ":" + proxyPort);
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
        System.err.println("Reading: " + string);
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
     * Creates a new Client
     * @param host The host to connect to
     * @param port The port to connect to
     * @param listenAddresses The addresses to listen on
     * @param listenPorts The ports to listen on
     * @param sendAddresses The addresses to send to
     * @param sendPorts The ports to send to
     * @throws IOException
     * @throws URISyntaxException
     */
    public Client(InetAddress host, int port,
            Vector<InetAddress> listenAddresses, Vector<Integer> listenPorts,
            Vector<InetAddress> sendAddresses, Vector<Integer> sendPorts)
            throws IOException, URISyntaxException {
        Random rand = new Random();
        String session = Long.toString(Math.abs(rand.nextLong()), CODE_LENGTH);
        listenSocket = connectHttpSocket(host, port);
        listenSocket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
        HashMap<InetSocketAddress, InetSocketAddress> addressMap =
            new HashMap<InetSocketAddress, InetSocketAddress>();
        InetSocketAddress[] addresses =
            new InetSocketAddress[listenAddresses.size()];
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

        output.writeInt(listenAddresses.size());
        for (int i = 0; i < listenAddresses.size(); i++) {
            output.write(sendAddresses.get(i).getAddress());
            output.writeInt(sendPorts.get(i));
            output.write(listenAddresses.get(i).getAddress());
            output.writeInt(listenPorts.get(i));
            addressMap.put(new InetSocketAddress(sendAddresses.get(i),
                               sendPorts.get(i)),
                           new InetSocketAddress(listenAddresses.get(i),
                                listenPorts.get(i)));
            addresses[i] = new InetSocketAddress(listenAddresses.get(i),
                    listenPorts.get(i));
        }
        output.flush();
        String response = readLine(input);
        if (response.endsWith("200 OK")) {
            readHeaders(input);
            sendSocket = connectHttpSocket(host, port);
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

            reader = new ClientReader(input, addressMap);
            writer = new ClientWriter(sendOutput, addresses);
            writer.setLocalIgnorePort(reader.getLocalPort());
            reader.start();
            writer.start();
        } else {
            System.err.println("Invalid response: " + response);
            close();
        }
    }

    /**
     * Closes the client
     */
    public void close() {
        reader.close();
        writer.close();
        try {
            listenSocket.close();
            sendSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.err.println("Client <host>:<port> "
                + "<listenAddr>:<listenPort>-<sendAddr>:<sendPort>"
                + "[<listenAddr2>:<listenPort2>-<sendAddr2>:<sendPort2> ...]");
        System.exit(0);
    }

    /**
     * The main method
     * @param args The arguments
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            printUsage();
        }
        Vector<InetAddress> listenAddresses = new Vector();
        Vector<Integer> listenPorts = new Vector();
        Vector<InetAddress> sendAddresses = new Vector();
        Vector<Integer> sendPorts = new Vector();

        String[] hostParts = args[0].split(":");
        if (hostParts.length != 2) {
            printUsage();
        }

        try {
            InetAddress host = InetAddress.getByName(hostParts[0]);
            int port = Integer.parseInt(hostParts[1]);
            for (int i = 1; i < args.length; i++) {
                String[] listenSend = args[i].split("-");
                if (listenSend.length < 2) {
                    printUsage();
                }
                String[] listenParts = listenSend[0].split(":");
                String[] sendParts = listenSend[1].split(":");
                if ((listenParts.length != 2) || (sendParts.length != 2)) {
                    printUsage();
                }
                InetAddress listenAddress =
                    InetAddress.getByName(listenParts[0]);
                int listenPort = Integer.parseInt(listenParts[1]);
                InetAddress sendAddress = InetAddress.getByName(sendParts[0]);
                int sendPort = Integer.parseInt(sendParts[1]);
                listenAddresses.add(listenAddress);
                listenPorts.add(listenPort);
                sendAddresses.add(sendAddress);
                sendPorts.add(sendPort);
            }


            new Client(host, port, listenAddresses, listenPorts, sendAddresses,
                    sendPorts);
        } catch (NumberFormatException e) {
            System.err.println("Port format incorrect");
            printUsage();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            printUsage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
