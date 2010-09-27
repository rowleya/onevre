/*
 * @(#)Server.java
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

package com.googlecode.onevre.bridges.tcpbridge.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.parser.XmlRpcRequestParser;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.googlecode.onevre.bridges.tcpbridge.common.ClientReader;
import com.googlecode.onevre.bridges.tcpbridge.common.ClientWriter;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.XmlRpcMapping;


/**
 * The server of the Simple TCP Bridge
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Server extends Thread {

    private static final int CODE_FILE_NOT_FOUND = 404;

    private static final int CODE_SERVER_ERROR = 500;

    private static final int CODE_METHOD_NOT_ALLOWED = 405;

    private static final int CODE_PRECONDITION_FAILED = 412;

    private static final int CODE_BAD_REQUEST = 400;

    private static final int LF_CHAR = 10;

    private static final int CR_CHAR = 13;

    private static final int MAX_PORT = 65535;

    private static final int ADDRESS_SIZE = 4;

    private ServerSocket server = null;

    private boolean done = false;

    private int lastChar = -1;

    private HashMap<String, ClientReader> readerMap =
        new HashMap<String, ClientReader>();

    private XmlRpcServer xmlRpcServer = new XmlRpcServer();

    private XmlRpcMapping mapping = new XmlRpcMapping();

    /**
     * Creates a new server
     * @param port The port to listen on
     * @throws IOException
     */
    public Server(int port) throws IOException {
        server = new ServerSocket(port);
        xmlRpcServer.setHandlerMapping(mapping);
        XmlRpcServerConfigImpl severConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        severConfig.setEnabledForExtensions(true);
        severConfig.setContentLengthOptional(false);
        mapping.addHandler("", this);
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

    private void writeError(int code, String message, OutputStream output)
            throws IOException {
        String error = "HTTP/1.0 " + code + " " + message + "\r\n\r\n";
        System.err.println("Writing Error: " + error);
        output.write(error.getBytes("UTF-8"));
        output.flush();
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

    private void writeOKHeaders(OutputStream output, String contentType,
            int contentLength)
            throws IOException {
        String response = "HTTP/1.0 200 OK\r\n";
        response += "Connection: close\r\n";
        response += "Cache-Control: no-store\r\n";
        response += "Pragma: no-cache\r\n";
        response += "Content-Type: " + contentType + "\r\n";
        response += "Content-Length: " + contentLength + "\r\n";
        response += "\r\n";
        output.write(response.getBytes("UTF-8"));
    }

    private String getXmlRpcResult(Object result) {
        if (result instanceof Integer) {
            return "<int>" + result + "</int>";
        } else if (result instanceof Boolean) {
            return "<boolean>" + result + "</boolean>";
        } else if (result instanceof String) {
            return "<string>" + result + "</string>";
        } else if ((result instanceof Double)
                || (result instanceof Float)) {
            return "<double>" + result + "</double>";
        } else if (result instanceof Hashtable) {
            Iterator iter = ((Hashtable) result).keySet().iterator();
            String xml = "<struct>";
            while (iter.hasNext()) {
                String name = (String) iter.next();
                Object value = ((Hashtable) result).get(name);
                xml += "<member>";
                xml += "<name>" + name + "</name>";
                xml += "<value>" + getXmlRpcResult(value) + "</value>";
                xml += "</member>";
            }
            xml += "</struct>";
            return xml;
        } else if (result instanceof Vector) {
            Vector vector = (Vector) result;
            String xml = "<array><data>";
            for (int i = 0; i < vector.size(); i++) {
                xml += "<value>" + getXmlRpcResult(vector.get(i)) + "</value>";
            }
            xml += "</data></array>";
            return xml;
        }
        return result.toString();
    }

    private void handleRootRequest(String method, DataInputStream input,
            DataOutputStream output, OutputStream outputStream, Socket socket)
            throws IOException {
        if (method.equals("GET")) {
            HashMap<String, String> headers =
                readHeaders(input);
            String session = headers.get("x-sessioncookie");
            if (session == null) {
                writeError(CODE_PRECONDITION_FAILED,
                        "Missing session identifier", output);
                socket.close();
            } else {
                int count = input.readInt();
                if (count == 0) {
                    output.writeInt(0);
                    socket.close();
                } else {
                    InetSocketAddress[] addresses =
                        new InetSocketAddress[count];
                    HashMap<InetSocketAddress,
                            InetSocketAddress> addressMap =
                                new HashMap<InetSocketAddress,
                                           InetSocketAddress>();
                    for (int i = 0; i < count; i++) {
                        byte[] addr = new byte[ADDRESS_SIZE];
                        input.read(addr);
                        InetAddress listenAddress =
                            InetAddress.getByAddress(addr);
                        int listenPort = input.readInt();
                        input.read(addr);
                        InetAddress sendAddress =
                            InetAddress.getByAddress(addr);
                        int sendPort = input.readInt();
                        addressMap.put(
                            new InetSocketAddress(sendAddress,
                                    sendPort),
                            new InetSocketAddress(listenAddress,
                                    listenPort));
                        addresses[i] = new InetSocketAddress(
                                listenAddress, listenPort);
                    }
                    ClientReader reader = new ClientReader(
                            addressMap);
                    readerMap.put(session, reader);
                    ClientWriter writer = new ClientWriter(
                            outputStream, addresses);
                    writer.setLocalIgnorePort(
                            reader.getLocalPort());
                    writer.start();
                    writeOKHeaders(output,
                            "application/octet-stream",
                            Integer.MAX_VALUE);
                    output.flush();
                }
            }
        } else if (method.equals("POST")) {
            HashMap<String, String> headers =
                readHeaders(input);
            String session = headers.get("x-sessioncookie");
            if (session == null) {
                writeError(CODE_PRECONDITION_FAILED,
                        "Missing session identifier", output);
                socket.close();
            } else {
                ClientReader reader = readerMap.get(session);
                if (reader == null) {
                    writeError(CODE_SERVER_ERROR,
                            "Reader not found for session "
                            + session, output);
                    socket.close();
                } else {
                    reader.setInputStream(input);
                    reader.start();
                }
            }
        } else {
            writeError(CODE_METHOD_NOT_ALLOWED,
                    "Method not allowed", output);
            socket.close();
        }
    }

    private void handleXmlRpc(DataInputStream input, DataOutputStream output,
            Socket socket) throws IOException {
        String methodName = null;
        int errorCode = 0;
        String errorMessage = null;
        try {
            HashMap<String, String> headers =
                readHeaders(input);
            XmlRpcHttpRequestConfigImpl config =
                new XmlRpcHttpRequestConfigImpl();
            XmlRpcRequestParser requestParser =
                new XmlRpcRequestParser(config,
                        new TypeFactoryImpl(xmlRpcServer));
            XMLReader parser =
                new org.apache.xerces.parsers.SAXParser();
            int contentLength = Integer.parseInt(
                    headers.get("Content-Length"));
            byte[] content = new byte[contentLength];
            input.readFully(content);
            InputSource source = new InputSource(
                        new ByteArrayInputStream(content));
            parser.setContentHandler(requestParser);
            parser.parse(source);
            methodName = requestParser.getMethodName();
            System.err.println("Executing " + methodName);

            XmlRpcRequest xmlRpcRequest =
                new XmlRpcClientRequestImpl(config, methodName,
                    requestParser.getParams());
            Object result = xmlRpcServer.execute(
                    xmlRpcRequest);
            String xml = "";
            xml += "<?xml version=\"1.0\"?>";
            xml += "<methodResponse>";
            xml += "<methodName>" + methodName
                    + "</methodName>";
            xml += "<params><param><value>";
            xml += getXmlRpcResult(result);
            xml += "</value></param></params>";
            xml += "</methodResponse>";
            byte[] response = xml.getBytes("UTF-8");
            writeOKHeaders(output, "text/xml", response.length);
            output.write(response);
            output.flush();
            socket.close();
        } catch (XmlRpcException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
            errorCode = e.code;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        if (errorMessage != null) {
            String xml = "";
            xml += "<?xml version=\"1.0\"?>";
            xml += "<methodResponse>";
            if (methodName != null) {
                xml += "<methodName>" + methodName
                    + "</methodName>";
            }
            xml += "<fault><value><struct>";
            xml += "<member><name>faultCode</name><value><int>";
            xml += errorCode;
            xml += "</int></value></member>";
            xml += "<member><name>faultString</name><value>";
            xml += "<string>" + errorMessage + "</string>";
            xml += "</value></member>";
            xml += "</struct></value></fault>";
            xml += "</methodResponse>";
            byte[] response = xml.getBytes("UTF-8");
            writeOKHeaders(output, "text/xml", response.length);
            output.write(response);
            output.flush();
            socket.close();
        }
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        while (!done) {
            boolean accepted = false;
            try {
                final Socket socket = server.accept();
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            System.err.println("New connection from "
                                    + socket.getRemoteSocketAddress());
                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream =
                                socket.getOutputStream();
                            DataInputStream input =
                                new DataInputStream(inputStream);
                            DataOutputStream output =
                                new DataOutputStream(outputStream);

                            // Read the HTML first line
                            String request = readLine(input);
                            System.err.println("Read request line " + request);
                            if (request == null) {
                                writeError(CODE_BAD_REQUEST, "Bad Request",
                                        output);
                                socket.close();
                            } else {
                                String[] parts = request.split(" ");
                                URL url = null;
                                try {
                                    url = new URL(parts[1]);
                                } catch (MalformedURLException e) {
                                    URL serverURL = new URL(
                                            "http://localhost/");
                                    url = new URL(serverURL, parts[1]);
                                }
                                if (url.getPath().equals("/")) {
                                    handleRootRequest(parts[0], input, output,
                                            outputStream, socket);
                                } else if (url.getPath().startsWith("/RPC2")) {
                                    handleXmlRpc(input, output, socket);
                                } else {
                                    writeError(CODE_FILE_NOT_FOUND,
                                            "File Not Found", output);
                                    socket.close();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

            } catch (SocketException e) {
                e.printStackTrace();
                if (!accepted) {
                    close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the server
     *
     */
    public void close() {
        done = true;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("Server -p <port> [-n <name> -l <location>"
                + " -r <registry>[;<registry2>...]");
        System.out.println("    -p <port> Specifies the port to listen on");
        System.exit(0);
    }

    /**
     * The main method
     * @param args The port to run the server on
     */
    public static void main(String[] args) {
        int port = 0;
        String[] registries = null;
        String name = null;
        String location = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    if (port < 1 || port > MAX_PORT) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.err.println(
                            "Port must be a number between 1 and 65535!");
                    printUsage();
                }
                i += 1;
            } else if (args[i].equals("-n")) {
                name = args[i + 1];
                i += 1;
            } else if (args[i].equals("-l")) {
                location = args[i + 1];
                i += 1;
            } else if (args[i].equals("-r")) {
                registries = args[i + 1].split(";");
                i += 1;
            }
        }
        if (port == 0) {
            printUsage();
        }
        if (registries != null) {
            if ((registries.length == 0) || (name == null)
                      || (location == null)) {
                printUsage();
            }
            for (int i = 0; i < registries.length; i++) {
                try {
                    System.err.println("Trying to connect to registry at "
                            + registries[i]);
                    AGBridgeRegistryClient client = new AGBridgeRegistryClient(
                            registries[i], name,
                            InetAddress.getLocalHost().getHostAddress(), port,
                            port, port, "TcpBridge");
                    client.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pings the bridge
     * @param time The time at which the ping was sent
     * @return The time
     */
    public double Ping(double time) {
        return time;
    }
}
