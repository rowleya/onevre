/*
 * @(#)LowBagClient.java
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

package com.googlecode.onevre.bridges.lowbag.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;
import org.xml.sax.SAXException;

import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.bridges.lowbag.common.LowBagClientInterface;
import com.googlecode.onevre.bridges.lowbag.common.LowBagDefaults;
import com.googlecode.onevre.bridges.lowbag.common.LowBagReader;
import com.googlecode.onevre.bridges.lowbag.common.LowBagWriter;
import com.googlecode.onevre.bridges.lowbag.common.StreamsHandler;
import com.googlecode.vicovre.media.Misc;
import com.googlecode.vicovre.repositories.rtptype.RTPType;


/**
 * A Bridge Client for the LowBAG Bridge.
 * <p/>LowBAG Bridge Client concept<p/> <img src="../../../images/lowbag_bridge-client.png" />
 * @author Sebastian Starke
 * @version 1.0
 */
public class LowBagClient implements LowBagClientInterface {

    /** Birdge server address. */
    private InetAddress serverAddress = null;

    /** Birdge server control port. */
    private int serverControlPort = 0;

    /** Bridge client data port. */
    private int listenPort = 0;

    /** XML-RPC connection. */
    private XmlRpcClient rpcClient = null;

    /** For sending data back to the bridge server. */
    private LowBagWriter writer = null;

    /** For receiving data and sending it to the video consumer. */
    private LowBagReader reader = null;

    /** Unique client identifier. */
    private String clientID = null;

    /** Thread control variable. */
    private boolean done = true;

    /** Hash map with XML-RPC updates for bridge server. */
    private HashMap<String, Object> updates = new HashMap<String, Object>();

    /** Thread control variable for updates. */
    private Boolean updatesSync = true;

    /** Object of the client interface. */
    private LowBagClientUI clientInterface = null;

    /** Video client socket for stand alone video client. */
    private InetSocketAddress vicSocket = new InetSocketAddress(
            LowBagDefaults.DEFAULT_VIC_ADDRESS,
            LowBagDefaults.DEFAULT_VIC_PORT);

    /** Audio client socket for stand alone audio client. */
    private InetSocketAddress ratSocket = new InetSocketAddress(
            LowBagDefaults.DEFAULT_RAT_ADDRESS,
            LowBagDefaults.DEFAULT_RAT_PORT);

    /** The bridge output format rather the format transmitted to the client. */
    private RTPType outputFormat = LowBagDefaults.getSupportedFormats().
                findRtpType(LowBagDefaults.DEFAULT_RTP_TYPE);

    /** Handles all incomming streams. */
    private StreamsHandler streamsHandler = new StreamsHandler();

    /** Switch for automated controlling of ratio by the server. */
    private boolean autoRatio = LowBagDefaults.DEFAULT_AUTO_RATIO;

    /** Vector of all AG video stream locations. */
    private Vector<NetworkLocation> videoLocations =
        new Vector<NetworkLocation>();

    /** Vector of all AG audio stream locations. */
    private Vector<NetworkLocation> audioLocations =
        new Vector<NetworkLocation>();

    /**
     * Constructor with server address, port and stream locations as arguments.
     * @param address The server address
     * @param port The server port
     * @param locations2 Stream locations (address and port)
     */
    public LowBagClient(final String address, final int port,
            final NetworkLocation[] locations2) {
        clientInterface = new LowBagClientUI(this);
        streamsHandler.setClient(this);
        boolean error = false;
        try {
            setServerAddress(address);
            setLocations(locations2);
            setServerControlPort(port);
            setListenPort(0);
            Misc.configureCodecs(LowBagDefaults.CODECS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            error = true;
        } catch (SAXException e) {
            e.printStackTrace();
            error = true;
        }
        if (error) {
            LowBagClientUI.errorOpenClient();
        }
    }

    /**
     * Start the LowBAG brdige client from the commandline.
     * @param args Commandline arguments
     */
    public static void main(final String[] args) {
        String serverAddress2 = null;
        int serverControlPort2 = 0;
        MulticastNetworkLocation[] locations = new MulticastNetworkLocation[2];

        //- parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-l")
                    || args[i].equals("--location")) {
                String[] address = args[i + 1].split("/", 3);
                i += 1;
                String host = address[0];
                int port = Integer.parseInt(address[1]);
                int ttl = LowBagDefaults.DEFAULT_TTL;
                if (address.length > 2) {
                    ttl = Integer.parseInt(address[2]);
                }
                locations[0] = new MulticastNetworkLocation();
                locations[0].setHost(host);
                locations[0].setPort(port);
                locations[0].setTtl(ttl);
            } else if (args[i].equals("-s")
                    || args[i].equals("--server")) {
                String[] address = args[i + 1].split("/", 2);
                i += 1;
                serverAddress2 = address[0];
                serverControlPort2 = Integer.parseInt(address[1]);
            } else if (args[i].equals("-h")
                    || args[i].equals("--help")) {
                printUsage();
            }
        }

        //- check if client arguments are set?
        if (locations[0] == null) {
            locations[0] = new MulticastNetworkLocation();
            locations[0].setHost("233.33.100.16");
            locations[0].setPort("57006");
            locations[0].setTtl(LowBagDefaults.DEFAULT_TTL);

            locations[1] = new MulticastNetworkLocation();
            locations[1].setHost("233.33.100.16");
            locations[1].setPort("57004");
            locations[1].setTtl(LowBagDefaults.DEFAULT_TTL);
        }
        if (serverAddress2 == null) {
            serverAddress2 = LowBagDefaults.DEFAULT_SERVER_ADDRESS;
        }
        if (serverControlPort2 == 0) {
            serverControlPort2 = LowBagDefaults.DEFAULT_SERVER_CONTROL_PORT;
        }

        //- run client
        new LowBagClient(serverAddress2, serverControlPort2, locations);
    }


    /**
     * Prints information how to use the server.
     */
    private static void printUsage() {
        System.out.println("client [-l <location (address/port/ttl)> -s <server(address/port)>");
        System.out.println("  -l|--location <location (address/port/ttl)> Specifies the AG stream lcoation");
        System.out.println("  -s|--server <server(address/port)> Specifies server address with port");
        System.out.println("  -h|--help This help message");
        System.exit(0);
    }

    /**
     * Adds the missing RTCP location into the array.
     * @param locations2 Array of AG stream RTP locations.
     */
    public final void setLocations(final NetworkLocation[] locations2) {
        for (int i = 0; i < locations2.length; i++) {
            MulticastNetworkLocation location = new MulticastNetworkLocation();
            location.setHost(locations2[i].getHost());
            location.setPort(locations2[i].getPort() + 1);
            if (locations2[i] instanceof MulticastNetworkLocation) {
                location.setTtl(((MulticastNetworkLocation)
                        locations2[i]).getTtl());
            }
            // TODO fidn smarter way that works!!!
            if (locations2[i].getHost().equals("233.33.100.16") && locations2[i].getPort() == 57004) {
                audioLocations.add(locations2[i]);
                audioLocations.add(location);
            } else {
                videoLocations.add(locations2[i]);
                videoLocations.add(location);
            }
        }
    }

    /**
     * Set the socket for the video client.
     * @param vicSocket2 Socket video client is listening.
     */
    public final void setVicSocket(final InetSocketAddress vicSocket2) {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        vicSocket = vicSocket2;
        if (connect) {
            clientInterface.connect();
        }
    }

    /**
     * Gets the socket of the video client.
     * @return Socket of the video client.
     */
    public final InetSocketAddress getVicSocket() {
        return vicSocket;
    }

    /**
     * Set the socket for the audio client.
     * @param ratSocket2 Socket audio client is listening.
     */
    public final void setRatSocket(final InetSocketAddress ratSocket2) {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        ratSocket = ratSocket2;
        if (connect) {
            clientInterface.connect();
        }
    }

    /**
     * Gets the socket of the audio client.
     * @return Socket of the audio client.
     */
    public final InetSocketAddress getRatSocket() {
        return ratSocket;
    }

    /**
     * Gets the object of client interface.
     * @return Object of client interface.
     */
    public final LowBagClientUI getClientUI() {
        return clientInterface;
    }

    /**
     * Get current LowBAG brdige client listen port.
     * @return Port client is listening on
     */
    public final int getListenPort() {
        if (listenPort == 0) {
            try {
                setListenPort(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listenPort;
    }

    /**
     * Set the LowBAG bridge client listen port.
     * @param port Port client has to listen on
     * @throws IOException addUpdate()
     */
    public final void setListenPort(final int port)
            throws IOException {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        if (port == 0) {
            String a = InetAddress.getLocalHost().getHostAddress();
            listenPort = LowBagDefaults.BASE_PORT + Integer.parseInt(
                    a.substring(a.lastIndexOf('.') + 1,
                    a.length()));
        } else if (port < 1 || port > LowBagDefaults.MAX_PORT) {
            throw new IOException();
        } else {
            listenPort = port;
        }
        if (connect) {
            clientInterface.connect();
        }
    }

    /**
     * Get current LowBAG brdige server address.
     * @return Address of server
     */
    public final String getServerAddress() {
        return serverAddress.getHostAddress().toString();
    }

    /**
     * Set the LowBAG bridge server address.
     * @param address Address of the server
     * @throws IOException InetAddress.getByName()
     */
    public final void setServerAddress(final String address)
            throws IOException {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        serverAddress = InetAddress.getByName(address);
        if (connect) {
            clientInterface.connect();
        }
    }

    /**
     * Get current LowBAG brdige server control port.
     * @return Port server is listening on
     */
    public final int getServerControlPort() {
        return serverControlPort;
    }

    /**
     * Set the LowBAG bridge server data port.
     * @param port Port server has to listen on
     * @throws IOException addUpdate()
     */
    public final void setServerControlPort(final int port)
            throws IOException {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        if (port < 1 || port > LowBagDefaults.MAX_PORT) {
            throw new IOException();
        }
        serverControlPort = port;
        if (connect) {
            clientInterface.connect();
        }
    }

    /**
     * Adds data to the updates hash map.
     * @note The SSRC "0" contains data about the client itself!
     * @param ssrc SSRC identifier
     * @param key Hash map key
     * @param value Hash map value
     * @throws IOException Key already exists.
     */
    public final void addUpdate(final String ssrc,
                final String key, final Object value)
            throws IOException {
        HashMap<String, Object> map = null;
        synchronized (updatesSync) {
            if (updates == null) {
                updates = new HashMap<String, Object>();
            }
            if (!updates.containsKey(ssrc)) {
                map = new HashMap<String, Object>();
            } else {
                map = (HashMap<String, Object>)
                    updates.get(ssrc);
            }
            if (map == null) {
                throw new IOException();
            }
            map.put(key, value);
            //- INFO: ssrc == 0 contains data
            //  about the client itself
            updates.put(ssrc, map);
            if (writer != null && writer.isPacketQueueEmpty()) {
                updatesSync.notifyAll();
            }
        }
    }

    /**
     * Set the AG stream locations.
     * @param locations2 AG stream locations (address and port)
     * @return Hash map with AG stream sockets.
     */
    public final HashMap<String, Object> getMap(
                final Vector<NetworkLocation> locations2) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> value = null;
        String key = null;
        for (int i = 0; i < locations2.size(); i++) {
            value = new HashMap<String, Object>();
            value.put("host", locations2.get(i).getHost());
            value.put("port", locations2.get(i).getPort());
            if (locations2.get(i) instanceof MulticastNetworkLocation) {
                value.put("ttl", ((MulticastNetworkLocation)
                    locations2.get(i)).getTtl());
            }
            key = value.get("host").toString() + ":"
                    + value.get("port").toString();
            map.put(key, value);
        }
        return map;
    }

    /**
     * Send a datagram packet to a destination address.
     * @param packet Datagram data packet
     * @param address Packet destination address
     * @throws IOException IOException()
     */
    public final void sendPacket(final DatagramPacket packet,
                final InetSocketAddress address)
            throws IOException {
        if (address == null || writer == null) {
            throw new IOException();
        }
        writer.addPacket(packet, address);
    }

    /**
     * Recive next packet from the pag queue.
     * @return Next packet in the queue
     */
    public final DatagramPacket receivePacket() {
        return reader.getNextPacket();
    }

    /**
     * Recive next packet from the pag queue.
     * @return Next packet in the queue
     */
    public final boolean isConnected() {
        return !done;
    }

    /**
     * Set the timeout of the packges.
     * @param timeout The timeout to be set
     * @throws IOException IOException()
     */
    public final void setReceiveTimeout(final int timeout)
            throws IOException {
        if (reader == null) {
            throw new IOException();
        }
        reader.setReceiveTimeout(timeout);
    }

    /**
     * Opens the connection to the bridge server.
     * @throws IOException Error in XML-RPC response!
     * @throws IOException getLocalHost()
     * @throws IOException getHostAddress()
     * @throws IOException DatagramSocket()
     * @throws IOException ClientReader()
     * @throws IOException openServerConnection()
     * @throws IOException ClientWriter()
     * @throws XmlRpcException openServerConnection()
     */
    public final void openServerConnecion()
            throws IOException, XmlRpcException {

        //- xmlRpc client
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://" + getServerAddress() + ":"
                + getServerControlPort()));
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(LowBagDefaults.XMLRPC_TIMEOUT);
        config.setReplyTimeout(LowBagDefaults.XMLRPC_TIMEOUT);
        if (rpcClient == null) {
            rpcClient = new XmlRpcClient();
            rpcClient.setTransportFactory(
                new XmlRpcLiteHttpTransportFactory(rpcClient));
        }
        rpcClient.setConfig(config);

        //- open input connection from bridge server
        //  on local port (55000 + IP)
        if (reader != null) {
            reader.close();
        }
        reader = new LowBagReader(getListenPort());
        reader.setForwardMap(videoLocations, vicSocket);
        reader.setForwardMap(audioLocations, ratSocket);
        reader.setStreamsHandler(streamsHandler);
        reader.startForwarding();
        reader.start();

        //- open output connection to bridge server
        //  on remote port (12346)
        // TODO replace string with InetAddress.getLocalHost(), InetAddress.getByName("localhost")
        HashMap<String, Object> response = (HashMap<String, Object>)
            rpcClient.execute("openClientConnection", new Object[] {
                InetAddress.getByName("localhost"), getListenPort(),
                getMap(videoLocations), getMap(audioLocations),
                vicSocket, ratSocket,
                getOutputFormat().getFormat()});

        if (!response.containsKey("dataPort")) {
            throw new IOException("Error in XML-RPC response!");
        }
        if (writer != null) {
            writer.close();
        }
        writer = new LowBagWriter(serverAddress, (Integer) response.get("dataPort"));
        writer.setListenLocations(getLocations(getVicSocket()));
        writer.setListenLocations(getLocations(getRatSocket()));
        writer.setLocalIgnorePort(reader.getLocalPort());
        writer.setClientSocket(true);
        writer.startListening();
        writer.start();

        //- set clientID recived by LowBAG server
        clientID = response.get("clientID").toString();

        //- start bridge updater thread
        runBridgeUpdater();

        //- update status of client interface
        clientInterface.setStatus(LowBagClientUI.CONNECTED);
    }

    private Vector<NetworkLocation> getLocations(final InetSocketAddress socket) {
        Vector<NetworkLocation> locations = new Vector<NetworkLocation>();
        if (socket != null && socket.getAddress() != null
                && socket.getPort() != 0) {
            NetworkLocation rtp = new NetworkLocation();
            rtp.setHost(socket.getAddress().getHostAddress().toString());
            rtp.setPort(socket.getPort());
            locations.add(rtp);
            NetworkLocation rtcp = new NetworkLocation();
            rtcp.setHost(socket.getAddress().getHostAddress().toString());
            rtcp.setPort(socket.getPort() + 1);
            locations.add(rtcp);
        }
        return locations;
    }

    /**
     * Updates the bridge server.
     */
    private void runBridgeUpdater() {
        done = false;
        Thread updater = new Thread() {
            public void run() {
                while (!done) {
                    try {
                        if (reader.getStatus()
                        || writer.getStatus()) {
                            throw new IOException();
                        }

                        //- add client infromation
                        addUpdate("0", "clientID", clientID);
                        addUpdate("0", "update_time",
                                LowBagDefaults.XMLRPC_UPDATE_TIME);

                        //- send xmlRpc command
                        synchronized (updatesSync) {
                            if (rpcClient != null) {
                                rpcClient.execute("updateClientConnection",
                                    new Object[] {updates});
                            }
                            updates.clear();
                            updatesSync.notifyAll();
                        }

                        //- check ssrc timestamp list
                        streamsHandler.checkStreamActivity();
                        Thread.sleep(LowBagDefaults.XMLRPC_UPDATE_INTERVAL);
                    } catch (IOException e) {
                        clientInterface.disconnect();
                    } catch (InterruptedException e) {
                        clientInterface.disconnect();
                    } catch (XmlRpcException e) {
                        clientInterface.disconnect();
                    }
                }
            }
        };
        updater.start();
    }

    /**
     * Closes the connection to the LowBAG bridge server.
     */
    public final void closeServerConnecion() {
        synchronized (updatesSync) {
            done = true;
            updates.clear();
            updatesSync.notifyAll();
        }

        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }

        //- send xmlRpc command to server to close connection
        if (clientID != null && rpcClient != null) {
            try {
                rpcClient.execute("closeClientConnection",
                        new Object[] {clientID});
            } catch (XmlRpcException e) {
                clientInterface.errorServerConnection();
            }
            clientID = null;
            rpcClient = null;
        }
        streamsHandler.clearStreams();

        //- update status of client interface
        clientInterface.setStatus(LowBagClientUI.DISCONNECTED);
    }

    /**
     * Gets the supported bridge formats.
     * @return By the bridge supported formats.
     */
    public final RTPType[] getOutputFormats() {
        RTPType[] formats = LowBagDefaults.getSupportedFormats().findRtpTypes().toArray(new RTPType[0]);
        return formats;
    }

    /**
     * Gets the bridge output format used for encoding.
     * @return Current used bridge format.
     */
    public final RTPType getOutputFormat() {
        return outputFormat;
    }

    /**
     * Returns the state if auto ratio is enabled or disabled.
     * @return State if auto ratio is enabled or disabled.
     */
    public final boolean isAutoRatio() {
        return autoRatio;
    }

    /**
     * Gets the bridge output format used for encoding.
     * @param autoRatio2 Enabled/disabled auto ratio
     */
    public final void setAutoRatio(boolean autoRatio2) {
        autoRatio = autoRatio2;
    }

    /**
     * Gets the stream handler object.
     * @return StreamHandler object.
     */
    public final StreamsHandler getStreamsHandler() {
        return streamsHandler;
    }

    /**
     * Sets the bridge output format.
     * @param outputFormat2 Format to use.
     * @throws IOException RTPType not supported by bridge!
     */
    public final void setOutputFormat(final RTPType outputFormat2)
            throws IOException {
        boolean connect = false;
        if (!done) {
            clientInterface.disconnect();
            connect = true;
        }
        if (writer != null && !writer.checkFormatSupport(
                outputFormat2.getFormat())) {
            throw new IOException("Unsupported format!");
        }
        outputFormat = outputFormat2;
        if (connect) {
            clientInterface.connect();
        }
    }

	public void setReportSSRC(String ssrc) throws IOException {
		clientInterface.setReport(ssrc);
	}

	public int getReportRate(final Object report)
    throws IOException {
		return clientInterface.getReportRate(report);
	}

	public void removeReportSSRC(String ssrc) throws IOException {
		clientInterface.removeReport(ssrc);
	}

}