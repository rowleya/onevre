/*
 * @(#)VenueClient.java
 * Created: 16 July 2007
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

package com.googlecode.onevre.web.venueclient;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.Timer;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.xml.sax.SAXException;

import com.googlecode.onevre.ag.agbridge.BridgeClientCreator;
import com.googlecode.onevre.ag.clientbridge.ClientBridge;
import com.googlecode.onevre.ag.interfaces.ServiceManagerInterface;
import com.googlecode.onevre.ag.interfaces.VenueClientInterface;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.ProviderProfile;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.application.AGSharedApplicationDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.ag.types.server.AGNodeService;
import com.googlecode.onevre.ag.types.server.AGSharedApplicationManager;
import com.googlecode.onevre.ag.types.service.AGBridgeConnectorDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.OptionSetParameter;
import com.googlecode.onevre.ag.types.service.parameter.RangeParameter;
import com.googlecode.onevre.ag.types.service.parameter.TextParameter;
import com.googlecode.onevre.por.PointOfReferenceClient;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.soapserver.SoapServer;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLDeserializer;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.XmlRpcMapping;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Download;
import com.googlecode.onevre.utils.Preferences;
import com.googlecode.onevre.utils.Utils;
import com.googlecode.onevre.web.ui.NodeManagementUI;



/**
 * Implements the VenueClient - a program run on the clients pc without a user
 * interface
 *
 * Communication point between the portlet and the programs and the services
 * using XML-RPC
 *
 * @author Anja Le Blanc
 * @version 1.0
 *
 */
public class VenueClient implements VenueClientInterface {

    // Time to wait
    private static final int WAIT_TIME = 1000;

    // Time to wait before closing when contact with server is lost
    private static final int CLOSE_TIMEOUT = 10000;

    static {
        SoapDeserializer.mapType(Capability.class);
        SoapDeserializer.mapType(VenueState.class);
        SoapDeserializer.mapType(ClientProfile.class);
        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(DataDescription.class);
        SoapDeserializer.mapType(ServiceDescription.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(MulticastNetworkLocation.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(TextParameter.class);
        SoapDeserializer.mapType(OptionSetParameter.class);
        SoapDeserializer.mapType(RangeParameter.class);
        SoapDeserializer.mapType(BridgeDescription.class);
    }

    private Timer close = null;
    private AGNodeService nodeService = null;
    private AGSharedApplicationManager applicationManager = null;
    private ClientProfile clientProfile = null;
    private SoapServer server = null;

    private Integer startSync = new Integer(0);

    private boolean started = false;

    private Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();

    private Preferences preferences = Preferences.getInstance();

    private DoShutdown shutdownHook = new DoShutdown();

    private Socket holderSocket = null;

    private int currentHolderPort = 0;

    private HashMap<String, AGServicePackageDescription> allServices = null;

    private HashMap<String, AGSharedApplicationDescription> allApplications =
        null;

    private boolean audioEnabled = true;

    private boolean videoEnabled = true;

    private boolean displayEnabled = true;

    private String currentEncryptionKey = "";

    private String currentBridgeId = "";

    private boolean isAutomaticBridging = true;

    private boolean nodeServiceLoadingConfig = false;

    private PagXmlRpcServer xmlRpcServer = new PagXmlRpcServer();

    /**
     * Creates a new VenueClient
     * @param port The port to run the XMLRPC server on
     * @param clientProfileXml The current client profile
     * @param services The set of node services available
     * @param applications The set of applications available
     * @param pointOfReferenceUrl The url of the point of reference
     * @param bridgeConnectors The list of bridge jars
     * @throws IOException
     * @throws SAXException
     */
    public VenueClient(int port, String clientProfileXml,
            String services, String applications, String pointOfReferenceUrl,
            String bridgeConnectors)
            throws IOException, SAXException {
        BridgeClientCreator.setBridgeConnectors((HashMap<String, AGBridgeConnectorDescription>)
        		XMLDeserializer.deserialize(StringEscapeUtils.unescapeXml(bridgeConnectors)));
        this.clientProfile = (ClientProfile) XMLDeserializer.deserialize(
                StringEscapeUtils.unescapeXml(clientProfileXml));
        this.allServices = (HashMap<String, AGServicePackageDescription>)
            XMLDeserializer.deserialize(StringEscapeUtils.unescapeXml(
                    services));
        this.allApplications = (HashMap<String, AGSharedApplicationDescription>)
        XMLDeserializer.deserialize(StringEscapeUtils.unescapeXml(
                applications));
        xmlRpcServer.addHandler("", this);

        WebServer webserver = new WebServer(port);
        XmlRpcServer localXmlRpcServer = webserver.getXmlRpcServer();
        XmlRpcMapping mapping = new XmlRpcMapping();
        mapping.addHandler("VenueClient", this);
        localXmlRpcServer.setHandlerMapping(mapping);
        XmlRpcServerConfigImpl severConfig =
            (XmlRpcServerConfigImpl) localXmlRpcServer.getConfig();
        severConfig.setEnabledForExtensions(true);
        severConfig.setContentLengthOptional(false);
        webserver.start();

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        System.err.println("Loading Bridges");
        loadBridges();

        System.out.println("POR: "+pointOfReferenceUrl );

        String [] PORurl = pointOfReferenceUrl.split(",");
        PointOfReferenceClient POR=new PointOfReferenceClient(PORurl);
        String url=POR.getUrl();

        System.err.println("Starting Node Service");
        startNodeService();
        try {
            nodeService.setPointOfReference(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the client
     *
     */
    public void shutdown() {
        if (close != null) {
            close.stop();
        }
        try {
            nodeService.stopServices();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            applicationManager.stopApplications();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            server.end();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File portFile = new File(
                    Preferences.getInstance().getConfigDir()
                    + "/port");
            portFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        System.exit(0);
    }

    /**
     * Ping signalling the portlet that it is running on client PC -
     * it is being restarted if ping fails
     * shuts down if no one requests a ping within a minute
     * @param holderPort The port to connect to for holding open
     * @return true to signal being alive
     */
    public boolean ping(final int holderPort) {

        if ((holderSocket == null) || (holderPort != currentHolderPort)) {
            if (holderSocket != null) {
                try {
                    System.err.println("closing socket");
                    holderSocket.close();
                } catch (IOException e) {
                    System.err.println("Venuclient.ping");
                    e.printStackTrace();
                }
            }
            currentHolderPort = holderPort;
            Thread holderThread = new Thread() {
                public void run() {
                    try {
                        holderSocket = new Socket(
                                Utils.getLocalHostAddress(), holderPort);
                        holderSocket.setKeepAlive(true);
                        holderSocket.setSoTimeout(0);
                        InputStream inputStream = holderSocket.getInputStream();
                        while (holderSocket.isConnected()) {
                            System.err.println("Holder Socket Connected");
                            if (close != null) {
                                close.stop();
                                close = null;
                            }
                            if (inputStream.read() == -1) {
                                throw new IOException("End of Stream");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        holderSocket = null;

                        ActionListener die = new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                if (!Download.isDownloading()) {
                                    shutdown();
                                } else {
                                    close.stop();
                                    Download.waitForDownloadsToComplete();
                                    close.start();
                                }
                            }
                        };
                        close = new Timer(CLOSE_TIMEOUT, die);
                        close.start();
                    }
                }
            };
            holderThread.start();
        }
        return true;
    }

    /**
     * enables/disables rat
     * @param enabledFlag 0 disable; 1 enable
     * @return true for having received call
     */
    public boolean setAudioEnabled(int enabledFlag) {
        audioEnabled = enabledFlag > 0;
        System.err.println("Setting audio enabled = " + audioEnabled);
        waitForNodeServices();
        try {
            nodeService.setServiceEnabledByMediaType("audio", enabledFlag == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * enables/disables vic viewing
     * @param enabledFlag 0 disable; 1 enable
     * @return true for having received call
     */
    public boolean setDisplayEnabled(int enabledFlag) {
        displayEnabled = enabledFlag > 0;
        System.err.println("Setting display enabled = " + displayEnabled);
        waitForNodeServices();
        try {
            ServiceManagerInterface[] managers =
                nodeService.getServiceManagers();
            for (int i = 0; i < managers.length; i++) {
                AGServiceDescription[] services = managers[i].getServices();
                for (int j = 0; j < services.length; j++) {
                    Vector<Capability> caps = services[j].getCapabilities();
                    for (int k = 0; k < caps.size(); k++) {
                        Capability cap = caps.get(k);
                        if (cap.getType().equals("video")
                                && cap.getRole().equals("consumer")) {
                            managers[i].enableService(services[j],
                                    displayEnabled);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * enables/disables vic streaming
     * @param enabledFlag 0 disable; 1 enable
     * @return true for having received call
     */
    public boolean setVideoEnabled(int enabledFlag) {
        videoEnabled = enabledFlag > 0;
        System.err.println("Setting video enabled = " + videoEnabled);
        waitForNodeServices();
        try {
            ServiceManagerInterface[] managers =
                nodeService.getServiceManagers();
            for (int i = 0; i < managers.length; i++) {
                AGServiceDescription[] services = managers[i].getServices();
                for (int j = 0; j < services.length; j++) {
                    Vector<Capability> caps = services[j].getCapabilities();
                    for (int k = 0; k < caps.size(); k++) {
                        Capability cap = caps.get(k);
                        if (cap.getType().equals("video")
                                && cap.getRole().equals("producer")) {
                            managers[i].enableService(services[j],
                                    videoEnabled);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Configures the node services
     * @return True if successful
     */
    public boolean configureNodeServices() {
        waitForNodeServices();
        NodeManagementUI nodeManagement =
            new NodeManagementUI(allServices, clientProfile);
        nodeManagement.attachToNode(nodeService);

        return true;
    }

    /**
     * Updates the client profile
     *
     * @param profileMap A map of parameters to update
     * @return true if the profile was updated, false otherwise
     */
    public boolean updateClientProfile(HashMap<String, String> profileMap) {
        if (clientProfile == null) {
            clientProfile = new ClientProfile();
        }
        clientProfile.setProfileType(profileMap.get("type"));
        clientProfile.setName(profileMap.get("name"));
        clientProfile.setEmail(profileMap.get("email"));
        clientProfile.setPhoneNumber(profileMap.get("phoneNumber"));
        clientProfile.setLocation(profileMap.get("location"));
        clientProfile.setHomeVenue(profileMap.get("homeVenue"));
        nodeService.setClientProfile(clientProfile);
        return true;
    }

    /**
     * Sets the streams
     * @param xml The xml representing the stream list
     * @return true if the streams were set
     */
    public boolean setStreams(String xml) {
        waitForNodeServices();
        System.err.println("VenueClient set Streams called " + xml);
        try {
            StreamDescription[] streams =
                (StreamDescription[]) XMLDeserializer.deserialize(xml);
            nodeService.setStreams(streams);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Waits for the node services to start
    private void waitForNodeServices() {

        synchronized (startSync) {
            if (!started && !nodeServiceLoadingConfig) {
                nodeServiceLoadingConfig = true;
                loadConfiguration(null);
                synchronized (startSync) {
                    this.started = true;
                    startSync.notifyAll();
                }
            }
            while (!started) {
                try {
                    System.err.println("Waiting for node services to start");
                    startSync.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
    }

    /**
     * Gets the capabilities of the node service
     * @return The capabilities
     */
    public String getCapabilities() {
        waitForNodeServices();
        Capability[] capabilities = null;
        try {
            capabilities = nodeService.getCapabilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String out = XMLSerializer.serialize(capabilities);
        System.out.println("CAPABILITIES:  ");
        System.out.println(out);
        return out;
    }

    /**
     * Download a data Item from the venue to be used with a shared application
     * @param venueFilename The filename to download
     * @param applicationUrl the application the data is sent to
     */
    public void downloadData(String venueFilename, String applicationUrl) {
        // does nothing at the moment
    }

    /**
     * Loads a particular node configuration
     * @param nodeConfig The configuration to load
     */
    public void loadConfiguration(String nodeConfig) {
        if (nodeConfig == null) {
            nodeConfig = preferences.getStringValue(
                    Preferences.DEFAULT_NODE_CONFIG);
        }
        try {
            nodeService.loadConfiguration(nodeConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startNodeService() {

        /*
         * start web (soap) service
         */
        startWebService();
        System.err.println("Started Web Service");

        try {
            applicationManager = new AGSharedApplicationManager(allApplications,
                    this);
            server.registerObject("/ApplicationManger", applicationManager);
            String amuri = server.findURLForObject(applicationManager);
            applicationManager.setUri(amuri);

            nodeService = new AGNodeService(allServices);
            nodeService.setClientBridge(new ClientBridge());
            server.registerObject("/NodeService", nodeService);
            String nodeServiceUri = server.findURLForObject(nodeService);
            nodeService.setUri(nodeServiceUri);

            nodeService.setClientProfile(clientProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a shared application
     * @param applicationXml XML of the Application Description
     * @param appStateXml XML of the initial Application state
     * @param namespace PAG namespace
     * @param downloadUrl URL to download data from
     * @param sessionid Session Id of the PAG session
     */
    public void runApplication(String applicationXml, String appStateXml,
            String namespace, String downloadUrl, String sessionid) {
        try {
            ApplicationDescription application = (ApplicationDescription)
                XMLDeserializer.deserialize(applicationXml);
            SharedAppState appState = (SharedAppState)
                XMLDeserializer.deserialize(Utils.unescapeXmlRpcValue(
                        appStateXml));
            applicationManager.startApplication(application, appState,
                    namespace, downloadUrl, sessionid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Distribute an event to all running instances of a shared application
     * @param applicationXml XML description of the shared application that is
     *         to receive the events
     * @param eventXml XML description of the event
     */
    public void distributeApplicationEvent(String applicationXml,
            String eventXml) {
        try {
            ApplicationDescription application = (ApplicationDescription)
                XMLDeserializer.deserialize(applicationXml);
            EventDescription event = (EventDescription)
                XMLDeserializer.deserialize(eventXml);
//            System.err.println("Start Application" + application.getName());
            applicationManager.distributeEvent(application, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startWebService() {
        try {
            int nodeServicePort = Utils.searchPort(1,
                    Utils.PAG_PORT_RANDOM, true);
            System.err.println("Starting SOAP server on port "
                    + nodeServicePort);
            server = new SoapServer(nodeServicePort, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Loads the bridges
    private void loadBridges() throws IOException {
        bridges.clear();
        try {
            HashMap<String, HashMap<String, String>> bridgeFile =
                ConfigFile.read(Preferences.getInstance().getConfigDir()
                        + "/bridges");
            Iterator<String> iter = bridgeFile.keySet().iterator();
            while (iter.hasNext()) {
                BridgeDescription bridge = new BridgeDescription();
                String id = iter.next();
                HashMap<String, String> values = bridgeFile.get(id);
                bridge.setGuid(id);
                bridge.setEnabled(values.get("status").equals(
                        "Enabled"));
                bridge.setName(values.get("name"));
                bridge.setDescription(values.get("description"));
                bridge.setServerType(values.get("serverType"));
                bridge.setHost(values.get("host"));
                bridge.setPort(values.get("port"));
                bridge.setPortMin(values.get("portMin"));
                bridge.setPortMax(values.get("portMax"));
                bridge.setPingTime((int)
                        Double.parseDouble(values.get("rank")));
                bridge.setRank((int)
                        Double.parseDouble(values.get("userRank")));
                bridges.add(bridge);
            }
        } catch (FileNotFoundException e) {
            // Do Nothing
        }
    }

    // Stores the bridges
    private void storeBridges() throws IOException {
        HashMap<String, HashMap<String, String>> bridgeFile =
            new HashMap<String, HashMap<String, String>>();
        for (int i = 0; i < bridges.size(); i++) {
            HashMap<String, String> values = new HashMap<String, String>();
            String status = "Disabled";
            if (bridges.get(i).isEnabled()) {
                status = "Enabled";
            }
            values.put("status", status);
            values.put("name", bridges.get(i).getName());
            values.put("description", bridges.get(i).getDescription());
            values.put("serverType", bridges.get(i).getServerType());
            values.put("host", bridges.get(i).getHost());
            values.put("port", String.valueOf(bridges.get(i).getPort()));
            values.put("portMin", String.valueOf(bridges.get(i).getPortMin()));
            values.put("portMax", String.valueOf(bridges.get(i).getPortMax()));
            values.put("rank", String.valueOf(bridges.get(i).getPingTime()));
            values.put("userRank", String.valueOf(bridges.get(i).getRank()));
            bridgeFile.put(bridges.get(i).getGuid(), values);
        }
        ConfigFile.store(Preferences.getInstance().getConfigDir()
                + "/bridges", bridgeFile);
    }

    /**
     * Removes all the bridges
     */
    public void clearBridges() {
        bridges.clear();
    }

    /**
     * Adds a set of bridges
     * @param bridgeXML The bridges XML representation
     */
    public void addBridges(String bridgeXML) {
        try {
            Vector<BridgeDescription> newBridges = (Vector<BridgeDescription>)
                XMLDeserializer.deserialize(bridgeXML);
            System.err.println("Attempting to add " + newBridges.size()
                    + " bridges");
            for (int i = 0; i < newBridges.size(); i++) {
                System.err.println("Checking for bridge " + newBridges.get(i));
                if (!bridges.contains(newBridges.get(i))) {
                    System.err.println("Adding bridge " + i);
                    bridges.add(newBridges.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            storeBridges();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a list of bridges
     * @return The list of bridges as XML
     */
    public String[] getBridges() {
        Collections.sort(bridges, new BridgeDescription.BridgeNameComparator());
        String[] bridgeXML = new String[bridges.size()];
        for (int i = 0; i < bridges.size(); i++) {
            bridgeXML[i] = XMLSerializer.serialize(bridges.get(i));
        }
        return bridgeXML;
    }

    /**
     * Joins a bridge
     * @param bridgeId The id of the bridge to join or null for multicast
     * @return true if the bridge was joined, false otherwise
     */
    public boolean joinBridge(String bridgeId) {

        waitForNodeServices();
        BridgeDescription bridge = null;
        if (bridgeId.equals("")) {
            bridge = new BridgeDescription();
            bridge.setServerType("multicast");
        } else {
            for (int i = 0; i < bridges.size(); i++) {
                if (bridges.get(i).getGuid().equals(bridgeId)) {
                    bridge = bridges.get(i);
                }
            }
        }

        if (bridge != null) {
            try {
                nodeService.joinBridge(bridge);
                currentBridgeId = bridgeId;
                isAutomaticBridging = false;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Returns the current bridge registries as XML
     * @return The xml of the registries
     */
    public String getRegistries() {
        String[] registryUrls = preferences.getStringValue(
                Preferences.BRIDGE_REGISTRY).split(";");
        return XMLSerializer.serialize(registryUrls);
    }

    /**
     * Sets the bridge registries to use
     * @param registries The registries as a string with ; separators
     */
    public void setRegistries(String registries) {
        try {
            preferences.setStringValue(Preferences.BRIDGE_REGISTRY, registries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if the audio is enabled
     * @return True if enabled, false otherwise
     */
    public boolean isAudioEnabled() {
       return audioEnabled;
    }

    /**
     * Determines if the video is enabled
     * @return True if enabled, false otherwise
     */
    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    /**
     * Determines if the display is enabled
     * @return True if enabled, false otherwise
     */
    public boolean isDisplayEnabled() {
        return displayEnabled;
    }

    /**
     * Sets the encryption to use
     * @param encryption The encryption
     * @return the current key
     */
    public String setEncryption(String encryption) {
        try {
            nodeService.setEncryption(encryption);
            if (encryption == null) {
                encryption = "";
            }
            currentEncryptionKey = encryption;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentEncryptionKey;
    }

    /**
     * Gets the current encryption key
     * @return The current encryption key
     */
    public String getEncryption() {
        return currentEncryptionKey;
    }

    /**
     * Runs automatic bridging
     *
     */
    public void runAutomaticBridging() {
        try {
            nodeService.runAutomaticBridging();
            isAutomaticBridging = true;
            currentBridgeId = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the point of reference url
     * @param url The new url
     */
    public void setPointOfReference(String url) {
        try {
            nodeService.setPointOfReference(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the id of the current bridge
     * @return The current bridge id
     */
    public String getCurrentBridgeId() {
        return currentBridgeId;
    }

    /**
     * Determines if automatic bridging is being used
     * @return True if automatic bridging is being used
     */
    public boolean isAutomaticBridging() {
        return isAutomaticBridging;
    }


    /**
     * Uploads files to the server
     * @param namespace The namespace of the portlet
     * @param url The url of the upload
     * @param sessionId The id of the session
     */
    public void uploadFiles(final String namespace, final String urlString,
            final String sessionId) {
        System.err.println("UploadFiles to " + urlString);
        Thread uploader = new Thread() {
            public void run() {
                Frame parent = new Frame("Select a file");
                parent.setSize(0, 0);
                parent.setLocationRelativeTo(null);
                parent.setUndecorated(true);
                parent.setVisible(true);
                parent.toFront();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setApproveButtonText("Select");
                fileChooser.setAcceptAllFileFilterUsed(true);
                fileChooser.setMultiSelectionEnabled(true);
                int returnVal = fileChooser.showDialog(parent, "Select a file");
                parent.setVisible(false);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                	System.out.println("in UploadFile APPROVE_OPTION");
                    File[] files = fileChooser.getSelectedFiles();
                    Part[] parts = new Part[files.length + 1];
                    parts[0] = new StringPart("namespace", namespace);
                    for (int i = 0; i < files.length; i++) {
                        try {
                            parts[i + 1] = new FilePart(files[i].getName(),
                                    files[i]);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Uploading " + parts.length + " files");

                    try {
	                    URL url = new URL(urlString);
	                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	                    connection.setDoOutput(true);
	                    Utils.addSslConnection(connection);
	                    PostMethod filePost = new PostMethod(urlString);
	                    RequestEntity requestEntity = new MultipartRequestEntity(parts,
	                            filePost.getParams());
	                    connection.setRequestMethod("POST");
	                    connection.addRequestProperty("Content-Type", requestEntity.getContentType());
	                    connection.addRequestProperty("Content-Length", String.valueOf(requestEntity.getContentLength()));
	                    connection.addRequestProperty("Cookie","JSESSIONID=" + sessionId);
	                 //   connection.connect();
	                    OutputStream os = connection.getOutputStream();
	                    System.out.println("writing files to "+ connection.getURL());
	                    requestEntity.writeRequest(os);
	                    os.flush();
	                    os.close();
	                    int response=connection.getResponseCode();
	                    System.out.println("response :" +connection.getResponseMessage() + "( "+ response+")");
	                    if(connection != null) {
	                    	connection.disconnect();
	                    }
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                }
            }
        };
        uploader.start();
    }


    /**
     * Gets the next response to an asynchronous XMLRPC server request
     * @return The next response
     */
    public String getNextResponse() {
        return xmlRpcServer.getNextResponse();
    }

    /**
     * Adds a new service
     * @param name The name of the service
     * @param descriptionXml The xml of the description of the service
     */
    public void addService(String name,
            String descriptionXml) {
        waitForNodeServices();
        try {
            AGServicePackageDescription description =
                (AGServicePackageDescription)
                XMLDeserializer.deserialize(descriptionXml);
            allServices.put(name, description);
            nodeService.addService(name, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DoShutdown extends Thread {

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            shutdown();
        }
    }
}
