/*
 * @(#)Server.java
 * Created: 11 Feb 2010
 * Version: 1.0
 * Copyright (c) 2005-2010, University of Manchester All rights reserved.
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

package com.googlecode.onevre.bridges.lowbag.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.media.Format;
import javax.xml.parsers.ParserConfigurationException;

import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.bridges.lowbag.common.LowBagDefaults;
import com.googlecode.onevre.bridges.lowbag.common.LowBagWriter;
import com.googlecode.onevre.bridges.lowbag.common.LowBagReader;
import com.googlecode.onevre.bridges.lowbag.common.StreamHandler;
import com.googlecode.onevre.bridges.lowbag.common.StreamsHandler;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.XmlRpcMapping;
import com.googlecode.onevre.utils.Utils;

import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.xml.sax.SAXException;

import com.googlecode.vicovre.media.renderer.RGBRenderer;



/**
 * A Bridge Server for the LowBAG Bridge.
 * <p/>LowBAG Bridge Server concept<p/> <img src="../../../images/lowbag_bridge-server.png" />
 * @author Sebastian Starke
 * @version 1.0
 */
public class Server extends Thread {

    /** Port for XML-RPC commands. */
    private static int controlPort = LowBagDefaults.DEFAULT_SERVER_CONTROL_PORT;

    /** Port for incomming RTP & RTCP packages. */
    private static int dataPort = LowBagDefaults.DEFAULT_SERVER_DATA_PORT;

    /** XML-RPC mapping. */
    private XmlRpcMapping mapping = new XmlRpcMapping();

    /** Hash map of all connected clients. */
    private HashMap<String, Object> clients = new HashMap<String, Object>();

    /** Socket for incomming RTP & RTCP packages. */
    private DatagramSocket dataSocket = null;

    /** Thread control variable. */
    private boolean done = false;

    /** Hash map of XML-RPC updates. */
    private HashMap<String, Object> updates = new HashMap<String, Object>();

    /** Thread control variable for updates. */
    private Boolean updatesSync = true;

    /**
     * Returns the data port.
     * @return Data port for data channel
     */
    public static int getDataPort() {
        return dataPort;
    }

    /**
     * Runs the bridge XML-RPC server.
     * @param controlPort2 XML-RPC control remote port for the clients
     * @throws IOException start()
     * @throws ParserConfigurationException RtpTypeRepositoryXmlImpl()
     * @throws SAXException RtpTypeRepositoryXmlImpl()
     */
    public Server(final int controlPort2) throws IOException, SAXException,
            ParserConfigurationException {
        controlPort = controlPort2;
        WebServer webServer = new WebServer(controlPort);

        //- xmlRpc server
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
        xmlRpcServer.setHandlerMapping(mapping);
        mapping.addHandler("", this);
        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        //- web server
        webServer.start();
        System.err.println("LowBAG server running on port "
                + controlPort + " ...");
    }

    /**
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        boolean failed = false;
        int port = 0;
        String[] registries = null;
        String name = null;

        //- parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")
                    || args[i].equals("--port")) {
                port = Integer.parseInt(args[i + 1]);
                i += 1;
            } else if (args[i].equals("-n")
                    || args[i].equals("--name")) {
                name = args[i + 1];
                i += 1;
            } else if (args[i].equals("-r")
                    || args[i].equals("--registry")) {
                registries = args[i + 1].split(";");
                i += 1;
            } else if (args[i].equals("-h")
                    || args[i].equals("--help")) {
                printUsage();
            }
        }

        //- check if client arguments are set?
        if (port == 0) {
            port = LowBagDefaults.DEFAULT_SERVER_CONTROL_PORT;
        }
        if (registries != null) {
            if ((registries.length == 0) || (name == null)) {
                printUsage();
            }
            for (int i = 0; i < registries.length; i++) {
                try {
                    System.err.println("Trying to connect to registry at "
                            + registries[i]);
                    AGBridgeRegistryClient client = new AGBridgeRegistryClient(
                            registries[i], name,
                            InetAddress.getLocalHost().getHostAddress(), port,
                            port, port, "LowBagBridge");
                    client.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //- run server
        try {
            new Server(port);
        } catch (IOException e) {
            failed = true;
        } catch (SAXException e) {
            failed = true;
        } catch (ParserConfigurationException e) {
            failed = true;
        }
        if (failed) {
            System.err.println("Error, starting server!");
        }
    }

    /**
     * Prints information how to use the server.
     */
    private static void printUsage() {
        System.out.println("server [-p <port> -n <name>"
                + " -r <registry>[;<registry2>...]]");
        System.out.println("  -p <port> Specifies the port to listen on XML-RPC commands");
        System.out.println("  -n <name> Specifies the bridge name");
        System.out.println("  -r|registry <registry> Specifies the address of the registry used by PAG");
        System.out.println("  -h|--help This help message");
        System.exit(0);
    }

    /**
     * Set the AG stream locations.
     * @param streams AG stream locations (address and port)
     * @return Vector of AG stream locations.
     */
    public final Vector<NetworkLocation> getLocations(
                final HashMap<String, Object> streams) {
         HashMap<String, Object> map = null;
         MulticastNetworkLocation location = null;
         Vector<NetworkLocation> locations = new Vector<NetworkLocation>();
         Iterator i = streams.entrySet().iterator();
         while (i.hasNext()) {
             map = (HashMap<String, Object>)
                 ((Map.Entry) i.next()).getValue();
             location = new MulticastNetworkLocation();
             location.setHost(map.get("host").toString());
             location.setPort(map.get("port").toString());
             if (map.containsKey("ttl")) {
                 location.setTtl(map.get("ttl").toString());
             }
             locations.add(location);
        }
        return locations;
    }

    /**
     * Open client connection and passing through the requested streams.
     * @param address Address of the client
     * @param port Address client wants the connection to
     * @param videoMap Hash map of AG video streams to listen on
     * @param audioMap Hash map of AG audio streams to listen on
     * @param vicSocket Socket of the video client
     * @param ratSocket SOcket of the audio client
     * @param outputFormat Bridge output format
     * @return The port which the server opened to the client
     * @throws IOException Error, local port set wrong!
     */
    public final HashMap<String, Object> openClientConnection(
                final InetAddress address, final int port,
                final HashMap<String, Object> videoMap,
                final HashMap<String, Object> audioMap,
                final InetSocketAddress vicSocket,
                final InetSocketAddress ratSocket,
                final Format outputFormat)
            throws IOException {
        Vector<NetworkLocation> videoLocations = getLocations(videoMap);
        Vector<NetworkLocation> audioLocations = getLocations(audioMap);

        //- init HashMap for response
        HashMap<String, Object> response = new HashMap<String, Object>();

        //- if not open, open input connection from bridge client
        //	on local port (12346)
        if (dataSocket == null) {
            dataSocket = new DatagramSocket(dataPort);
        }
        int localPort = dataSocket.getLocalPort();
        if (localPort < 1 || localPort > LowBagDefaults.MAX_PORT) {
            throw new IOException("Error, local port set wrong!");
        }
        response.put("dataPort", localPort);

        //- open reader to pass through the streams to multicast group
        LowBagReader reader = new LowBagReader(dataSocket);
        reader.setServerSocket(true);
        reader.setForwardMap(videoLocations, vicSocket);
        reader.setForwardMap(audioLocations, ratSocket);
        reader.startForwarding();
        reader.start();

        //- open writer to pass through the streams to the bridge client
        LowBagWriter writer = new LowBagWriter(address, port);
        writer.setOutputFormat(outputFormat);
        writer.setListenLocations(videoLocations);
        writer.setListenLocations(audioLocations);
        writer.setLocalIgnorePort(reader.getLocalPort());
        writer.setStreamHandler(new StreamsHandler());
        writer.startListening();
        writer.start();

        //- generate ID
        String clientID = Utils.generateID();
        response.put("clientID", clientID);

        //- add client data to clients hash map
        HashMap<String, Object> client = new HashMap<String, Object>();
        client.put("writer", writer);
        client.put("reader", reader);
        clients.put(clientID, client);

        //- run the bridge updater thread
        runBridgeUpdater();

        return response;
    }

    /**
     * Updates the bridge server.
     */
    private void runBridgeUpdater() {
            done = false;
        Thread updater = new Thread() {
            public void run() {
                Iterator i = null;
                Map.Entry e = null;
                int n = 0;
                String[] remove = null;
                while (!done) {
                   try {
                       synchronized (updatesSync) {
                           n = 0;
                           remove = new String[updates.size()];
                           i = updates.entrySet().iterator();
                           while (i.hasNext()) {
                               e = (Map.Entry) i.next();
                               if (System.currentTimeMillis()
                                 > ((Long) e.getValue())) {
                                   remove[n++] = e.getKey().toString();
                               }
                           }
                           updatesSync.notifyAll();
                       }
                       //- remove keys
                       for (int j = 0; j < n; j++) {
                           closeClientConnection(remove[j]);
                       }
                       Thread.sleep(LowBagDefaults.SERVER_UPDATE_TIME);
                   } catch (InterruptedException e1) {
                        e1.printStackTrace();
                   }
                }
            }
        };
        updater.start();
    }

    /**
     * Update the connection information belonging to a client.
     * @param update Id that identifies the client
     * @throws IOException Missing required fields!
     * @throws IOException Unkown client!
     * @throws IOException Missing writer key!
     */
    public final void updateClientConnection(
                final HashMap<String, Object> update)
            throws IOException {
        HashMap<String, Object> map = null;
        String clientID = null;
        LowBagWriter writer = null;
        StreamHandler streamHandler = null;
        synchronized (updatesSync) {
            //- is there client information?
            if (!update.containsKey("0")) {
                throw new IOException("Error, no client information!");
            }
            map = (HashMap<String, Object>) update.get("0");

            //- are there the required fields?
            if (!map.containsKey("clientID")
            ||  !map.containsKey("update_time")) {
                throw new IOException("Missing required fields!");
            }

            //- do we know the client?
            if (!clients.containsKey(map.get("clientID"))) {
                throw new IOException("Unkown client!");
            }
            clientID = map.get("clientID").toString();
            updates.put(clientID, System.currentTimeMillis()
                + Long.parseLong(map.get("update_time").toString()));
                HashMap<String, Object> client =
                    (HashMap<String, Object>) clients.get(clientID);

                //- is there a writer?
            if (!client.containsKey("writer")) {
                throw new IOException("Missing writer key!");
            }
             writer = (LowBagWriter) client.get("writer");
             updatesSync.notifyAll();
        }

        update.remove("0");
        Iterator i = update.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            String ssrc = e.getKey().toString();
            map = (HashMap<String, Object>) e.getValue();
            streamHandler = writer.getStreamsHandler().getStream(ssrc);

            if (map.containsKey("rate") && streamHandler != null) {
                    if (((Integer) map.get("rate")) > 0) {
                        streamHandler.setReceiving(true);
                        streamHandler.setRatio((Integer)
                            map.get("rate"));
                    } else {
                        streamHandler.setReceiving(false);
                    }
                   RGBRenderer renderer = writer.getStreamsHandler().
                       getStream(ssrc).getRenderer();
                   if (map.containsKey("frameRate") && renderer != null
                   && streamHandler.isReceiving()) {
                          float scope = renderer.getFrameRate() - ((Float)
                                  map.get("frameRate"));

                          //- scale down
                          if (scope > LowBagDefaults.FRAME_TOLERNACE) {
                               renderer.setFrameRate((Integer)
                                map.get("rate") - LowBagDefaults.SCALE_STEP);

                           //- scale up
                           } else if (scope < -LowBagDefaults.FRAME_TOLERNACE) {
                               renderer.setFrameRate((Integer)
                                map.get("rate") + LowBagDefaults.SCALE_STEP);
                           }
                          map.put("frameRate", renderer.getFrameRate());
                   }
            }
        }
    }

    /**
     * Close the connection to the client and remove it from clients.
     * @param clientID Id that identifies the client
     */
    public final void closeClientConnection(final String clientID) {
        synchronized (updatesSync) {
            HashMap<String, Object> client =
                (HashMap<String, Object>) clients.get(clientID);

            //- remove reader and writer of the client
            if (client.get("reader") != null) {
                ((LowBagReader) client.get("reader")).close();
            }
            if (client.get("writer") != null) {
                ((LowBagWriter) client.get("writer")).close();
            }

            //- remove client from clients map
            clients.remove(clientID);

            //- remove client from updates map
            updates.remove(clientID);

            updatesSync.notifyAll();
        }
    }
}