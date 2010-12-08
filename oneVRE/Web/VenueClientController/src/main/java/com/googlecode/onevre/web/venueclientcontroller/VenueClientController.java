/*
 * @(#)VenueClientController.java
 * Created: 31 May 2007
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

package com.googlecode.onevre.web.venueclientcontroller;

import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import netscape.javascript.JSObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;

import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLDeserializer;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;
import com.googlecode.onevre.utils.Preferences;
import com.googlecode.onevre.utils.Utils;

/**
 * An applet for talking between the portlet and the application
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueClientController extends Applet {

	Log log = LogFactory.getLog(this.getClass());

    private static final String QUOTE = "\"";

    // The OS string for Mac
    private static final String MAC_OS_STRING = "mac";

    // The OS string for Linux
    private static final String LINUX_OS_STRING = "linux";

    // The OS substring for windows
    private static final String WINDOWS_OS_STRING = "windows";

    // The windows executable
    private static final String WINDOWS_EXEC = "\\bin\\java.exe";

    // The linux executable
    private static final String LINUX_EXEC = "/bin/java";

    // The mac executable
    private static final String MAC_EXEC = "/usr/bin/java";

    private static final long serialVersionUID = 1L;

    private static final String HTTP = "http://";

    private static Process venueClient = null;

    private static Reader inputReader = null;

    private static Reader errorReader = null;

    private DoShutdown shutdown = null;

    private int remotePort = 0;

    private XmlRpcClient client = null;

    private Timer hartbeattimer = null;

    private ServerSocket holderSocket = null;

    private Socket cli = null;

    private int holderPort = 0;

    private int localPort = 0;

    private boolean xmlrpcServerStarted = false;

    private String xmlrpcServerError = null;

    private Integer serverSync = new Integer(0);

    private String sessionid = null;

    private long lastVenueClientStartTime = 0;

    private String [] pointOfReferenceUrl = null;

 //   private Integer threadLock = new Integer(0);

    private JSObject jsWindow = null;

    private PagXmlRpcServer xmlRpcServer = new PagXmlRpcServer();

    private XmlRpcListener xmlRpcListener = null;

    private EvalThread evalThread = new EvalThread();

    private String clientProfile = null;

    private String applications = null;

    private String services = null;

    private String bridgeConnectors = null;


    private boolean importCertificate() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                try {
                    String keypass = "";
                    String keystorename = System.getProperty(
                            "deployment.user.security.trusted.certs");
                    if (keystorename == null) {
                        throw new IOException("No trusted certs keystore");
                    }
                    InputStream input = getClass().getResourceAsStream("/cert");
                    KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
                    File file = new File(keystorename);
                    if (!file.exists()) {
                        keystore.load(null, keypass.toCharArray());
                    } else {
                        keystore.load(new FileInputStream(keystorename),
                            keypass.toCharArray());
                    }
                    CertificateFactory certFactory =
                        CertificateFactory.getInstance("X.509");
                    Certificate cert = certFactory.generateCertificate(input);
                    boolean isInStore = false;
                    Enumeration<String> aliases = keystore.aliases();
                    while (aliases.hasMoreElements() && !isInStore) {
                        String alias = aliases.nextElement();
                        Certificate certificate =
                            keystore.getCertificate(alias);
                        if (certificate != null) {
                            if (certificate.equals(cert)) {
                                isInStore = true;
                            }
                        }
                    }
                    if (!isInStore) {
                        keystore.setEntry("deploymentusercert-"
                                    + System.currentTimeMillis(),
                                new KeyStore.TrustedCertificateEntry(cert),
                                null);
                        FileOutputStream output =
                            new FileOutputStream(keystorename);
                        keystore.store(output, keypass.toCharArray());
                        output.close();
                    }
                    return true;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return false;
            }
        });

    }

    private void appendDefaultProperties() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    String slash = System.getProperty("file.separator");
                    String filename = System.getProperty("user.home") + slash;
                    if (os.indexOf("windows") != -1) {
                        filename += "Application Data" + slash + "Sun"
                            + slash + "Java" + slash + "Deployment";
                    } else if (os.indexOf("mac") != -1) {
                        filename += "Library" + slash + "Caches"
                            + slash + "Java";
                    } else if (os.indexOf("linux") != -1) {
                        filename += ".java" + slash + "deployment";
                    }
                    filename += slash + "deployment.properties";
                    File file = new File(filename);
                    boolean isInFile = false;
                    if (file.exists()) {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(filename));
                        isInFile = properties.containsKey(
                                "deployment.javaws.secure.properties");
                    }
                    if (!isInFile) {
                        PrintWriter writer = new PrintWriter(
                                new FileWriter(file, true));
                        writer.println("deployment.javaws.secure.properties="
                                + "java.net.preferIPv4Stack");
                        writer.close();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * sets up the XML-RPC client
     * @see java.applet.Applet#init()
     */
    public void init() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                if (shutdown == null) {
                    shutdown = new DoShutdown();
                    Runtime.getRuntime().addShutdownHook(shutdown);
                }
                return null;
            }});
        if (!importCertificate()) {
            JOptionPane.showMessageDialog(null,
                "There is a known bug in Java causing an error accessing"
                + " certificates.  Please restart your browser and try again!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        appendDefaultProperties();
        // Read the port last used
        try {
            File portFile = new File(Preferences.getInstance().getConfigDir()
                    + "/port");
            if (!portFile.exists()) {
                try {
                    remotePort = Utils.searchPort(1,
                            Utils.PAG_PORT_RANDOM,
                            true);
                    localPort = Utils.searchPort(1,
                            Utils.PAG_PORT_RANDOM,
                            true);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    FileWriter writer = new FileWriter(portFile);
                    writer.write(String.valueOf(remotePort) + "\n");
                    writer.write(String.valueOf(localPort) + "\n");
                    writer.close();
                } catch (FileNotFoundException e) {
                    // don't worry file will be created
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(portFile));
                    remotePort = Integer.parseInt(reader.readLine());
                    localPort = Integer.parseInt(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.assignPort(remotePort);
            Utils.assignPort(localPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("VenueClientController connecting to port: " + remotePort );

        // setup XmlRpc environment
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(HTTP + "localhost:" + remotePort));
            config.setEnabledForExtensions(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessionid = getParameter("sessionid");
        URL docbase= getDocumentBase();
        pointOfReferenceUrl = new String[]{getParameter("pointOfReference"),getParameter("defaultPointOfReference")};
        try {
            pointOfReferenceUrl =  new String[]{getParameter("pointOfReference"),getParameter("defaultPointOfReference"),new URL(docbase.getProtocol(),docbase.getHost(),docbase.getPort(),"/por").toString()};
        } catch (MalformedURLException e) {
            //do nothing
        }
        clientProfile = getParameter("clientProfile");
        services = getParameter("services");
        bridgeConnectors = getParameter("bridgeConnectors");
        applications = getParameter("applications");
        config.setConnectionTimeout(120 * 1000);
        client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcLiteHttpTransportFactory(client));
        client.setConfig(config);
        xmlRpcServer.addHandler("", this);

        jsWindow = JSObject.getWindow(this);
    }

    /**
     * Tests if a url is valid
     * @param url The url to test
     * @return True if the url is valid, false otherwise
     */
    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private Object call(final String name, final Object[] parameters) {
        waitForServer(name);
        log.info("VenueClientController - call " + name +"("+ parameters+")");
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
             public Object run() {
                try {
                    return client.execute(name, parameters);
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * Gets the capabilities of the node service
     */
    public void getCapabilitiesXml() {
        Thread thread = new Thread() {
            public void run() {
                String capabilities = StringEscapeUtils.escapeXml((String)
                        call("VenueClient.getCapabilities", new Object[0]));
                jsWindow.call("pag_negotiateCapabilities",
                        new Object[]{capabilities});
            }
        };
        thread.start();
    }

    /**
     * enables/disables rat on VenueClient
     * @param enable enableFlag 0-off, 1-on
     */
    public void enableAudio(final int enable) {
    	log.info("enable audio");
        call("VenueClient.setAudioEnabled", new Object[]{enable});
    }

    /**
     * Determines if the audio is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isAudioEnabled() {
        return (Boolean) call("VenueClient.isAudioEnabled", new Object[0]);
    }

    /**
     * enables/disables vic display on VenueClient
     * @param enable enableFlag 0-off, 1-on
     */
    public void enableDisplay(final int enable) {
        call("VenueClient.setDisplayEnabled", new Object[]{enable});
    }

    /**
     * Determines if the display is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isDisplayEnabled() {
        return (Boolean) call("VenueClient.isDisplayEnabled", new Object[0]);
    }

    /**
     * enables/disables vic streaming on VenueClient
     * @param enable enableFlag 0-off, 1-on
     */
    public void enableVideo(final int enable) {
        call("VenueClient.setVideoEnabled", new Object[]{enable});
    }

    /**
     * Determines if the video is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isVideoEnabled() {
        return (Boolean) call("VenueClient.isVideoEnabled", new Object[0]);
    }

    /**
     * Configures the node services
      */
    public void configureNodeServices() {
        call("VenueClient.configureNodeServices", new Object[0]);
    }

    /**
     * set streams after negotiation with venue
     * @param xml string of streamDescriptions
     * @return true if the streams were set
     */
    public boolean setStreams(final String xml) {
        return (Boolean) call("VenueClient.setStreams", new Object[]{
                        StringEscapeUtils.unescapeXml(xml)});
    }

    /**
     * Adds a service
     * @param nameXml The name of the service
     * @param descriptionXml The description of the service in xml
     */
    public void addService(String nameXml, String descriptionXml) {
        String name = (String) getObjectDec(nameXml);
        call("VenueClient.addService", new Object[]{name,
                StringEscapeUtils.unescapeXml(descriptionXml)});
    }

    /**
     * Start timer to keep Venue Client alive
     * <dl><dt><b>overrides:</b></dt><dd>{@link java.applet.Applet#start()}</dd></dl>
     */
    public void start() {
       try {
            holderPort = Utils.searchPort(1, Utils.PAG_PORT_RANDOM, true);
            holderSocket = new ServerSocket(holderPort);
            holderSocket.setSoTimeout(0);
            log.info("Holder Socket listening on port " + holderPort);
            Thread listener = new Thread() {
                public void run() {
                    while (!holderSocket.isClosed()) {
                        try {
                            cli = holderSocket.accept();
                            cli.setSoTimeout(0);
                            cli.getInputStream().read();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int delay = 5000;
        int initdelay = 0;

        Hartbeat hartbeat = new Hartbeat();
        hartbeattimer = new Timer(delay, hartbeat);
        hartbeattimer.setInitialDelay(initdelay);
        hartbeattimer.setRepeats(true);
        hartbeattimer.start();
    }

    /**
     * shutdown timer thats keeps the VenueClient alive
     * <dl><dt><b>overrides:</b></dt><dd>{@link java.applet.Applet#stop()}</dd></dl>
     */
    public void stop() {
        try {
            cli.close();
            holderSocket.close();
            log.info("Holder Socket Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hartbeattimer != null) {
            hartbeattimer.stop();
        }
        if (xmlRpcListener != null) {
            xmlRpcListener.close();
        }
        if (evalThread != null) {
            evalThread.close();
        }
    }

    /**
     * Uploads a data file
     * @param namespace The namespace of the application
     * @param uploadUrl The url to upload to
     */
    public void uploadFiles(String namespace, String uploadUrl) {
        URL url = getDocumentBase();
        uploadUrl = url.getProtocol() + "://" + url.getHost() + ":"
            + url.getPort() + uploadUrl;
        call("VenueClient.uploadFiles", new Object[]{
            namespace, uploadUrl, sessionid});
    }

    /**
     * Sets the client profile
     * @param type
     * @param name
     * @param email
     * @param phoneNumber
     * @param location
     * @param homeVenue
     */
    public void setClientProfile(String type,
            String name, String email, String phoneNumber, String location,
            String homeVenue) {
        final HashMap<String, String> clHash = new HashMap<String, String>();
        clHash.put("type", type);
        clHash.put("name", name);
        clHash.put("email", email);
        clHash.put("phoneNumber", phoneNumber);
        clHash.put("location", location);
        clHash.put("homeVenue", homeVenue);

        call("VenueClient.updateClientProfile", new Object[]{clHash});
    }

    private void waitForServer(String text) {
        synchronized (serverSync) {
             while (!xmlrpcServerStarted && (xmlrpcServerError == null)) {
                try {
                	log.info("Waiting for server " + text);
                	log.info("    timer started = " + hartbeattimer.isRunning());
                    serverSync.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
             }
        }
    }

    /**
     * ActionListener class to keep the VenueClient alive
     * if a 'ping' fails this class restarts the VenueClient
     * @author Anja Le Blanc
     */
    private class Hartbeat implements ActionListener {
        /**
         * implementing the interface
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(ActionEvent event) {
            Object[] params = new Object[]{new Integer(holderPort)};
            try {
                client.execute("VenueClient.ping", params);
                synchronized (serverSync) {
                    xmlrpcServerStarted = true;
                    serverSync.notifyAll();
                    lastVenueClientStartTime = 0;
                }
                if (xmlRpcListener == null) {
                    xmlRpcListener = new XmlRpcListener();
                    xmlRpcListener.start();
                }
                if (!evalThread.isAlive()) {
                    evalThread.start();
                }
            } catch (XmlRpcException e) {
                if (xmlrpcServerError == null) {
                    startXmlRpcServer();
                    log.info("Finished starting server");
                    synchronized (serverSync) {
                        xmlrpcServerStarted = false;
                        log.info("Time since last start = "
                                + (System.currentTimeMillis()
                                    - lastVenueClientStartTime));
                        if ((System.currentTimeMillis()
                                - lastVenueClientStartTime) > 30000) {
                            String message = e.getMessage();
                            if (message == null) {
                                xmlrpcServerError = "Unknown Error";
                            } else {
                                xmlrpcServerError = message;
                            }
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null,
                                    "Error Starting VenueClient.\n"
                                    + "Please try restarting your browser.\n"
                                    + "If this problem persists, "
                                    + "please contact your administrator",
                                    "Error Starting Venue Client",
                                    JOptionPane.ERROR_MESSAGE);
                            hartbeattimer.stop();
                            serverSync.notifyAll();
                        }
                        log.info("Leaving sync block");
                    }
                }
            }
        }
    }

    private class XmlRpcListener extends Thread {

        private boolean done = false;

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                String xmlRpcRequest = (String) call(
                        "VenueClient.getNextResponse", new Object[0]);
                if (xmlRpcRequest.endsWith("<done></done>")) {
                	log.info("Queue done");
                    done = true;
                } else if (!xmlRpcRequest.endsWith("<empty></empty>")) {
                    xmlRpcServer.handleRequest(xmlRpcRequest);
                }
            }
        }

        private void close() {
            done = true;
        }
    }

    /**
     * Start up the VenueClient via WebStart
     *
     */
    private synchronized void startXmlRpcServer() {
        if (lastVenueClientStartTime > 0) {
            return;
        }
        System.err.println("startXmlRpcServer");
        try {
            File venueClientDirectory = new File(
                Preferences.getInstance().getServicesDir());
            venueClientDirectory.mkdirs();
            URL venueClientLauncher = new URL(
                    this.getCodeBase().toExternalForm()
                    + "../applications/VenueClientLauncher.jar");
            HttpURLConnection connection = (HttpURLConnection)
                venueClientLauncher.openConnection();

            Utils.addSslConnection(connection);
            InputStream input = connection.getInputStream();
            File outputJar = new File(venueClientDirectory,
                    "VenueClientLauncher.jar");
            FileOutputStream output = new FileOutputStream(outputJar);
            byte[] buffer = new byte[8096];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
            input.close();
            output.close();

            String os = System.getProperty("os.name").toLowerCase();
            String javaHome = System.getProperty("java.home");
            String exec = "";
            if (os.indexOf(WINDOWS_OS_STRING) != -1) {
                exec += "\"" + javaHome + WINDOWS_EXEC + "\"";
            } else if (os.indexOf(LINUX_OS_STRING) != -1) {
                exec += javaHome + LINUX_EXEC;
            } else if (os.indexOf(MAC_OS_STRING) != -1) {
                exec += MAC_EXEC;
            }
            URL bridgeJarList = new URL(this.getCodeBase().toExternalForm()
                    + "../jsp/getBridgeJars.jsp");
            URL venueClientUrl = new URL(this.getCodeBase().toExternalForm()
                    + "../jsp/findJarForVenueClient.jsp");
            String pors ="";
            String sep ="";
            for (String s:pointOfReferenceUrl){
                pors+=sep+s;
                sep=",";
            }
            ProcessBuilder builder = new ProcessBuilder(exec,
                "-Ddeployment.user.security.trusted.certs="
                    + System.getProperty(
                            "deployment.user.security.trusted.certs"),
                "-Djava.net.preferIPv4Stack=true",
                "-jar", "VenueClientLauncher.jar",
                "port=" + remotePort,
                QUOTE + "clientProfileXml=" + clientProfile + QUOTE,
                QUOTE + "services=" + services + QUOTE,
                QUOTE + "applications=" + applications + QUOTE,
                QUOTE + "bridgeConnectors=" + bridgeConnectors + QUOTE,
                QUOTE + "pointOfReferenceUrl=" + pors + QUOTE,
                QUOTE + "resourceDir="
                    + venueClientDirectory.getCanonicalPath() + QUOTE,
                QUOTE + "url=" + venueClientUrl.toString() + QUOTE);
            builder.directory(venueClientDirectory);
            venueClient = builder.start();
            inputReader = new Reader(venueClient.getInputStream(),
                    "VenueClient: ");
            errorReader = new Reader(venueClient.getErrorStream(),
                    "VenueClient: ");
            inputReader.start();
            errorReader.start();
            lastVenueClientStartTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the bridges currently stored in the venue client
     * @return The set of bridges
     */
    public Vector<BridgeDescription> loadBridges() {
        Object[] bridges = (Object[]) call("VenueClient.getBridges",
                new Object[]{});
        Vector<BridgeDescription> result =
            new Vector<BridgeDescription>();
        try {
            for (int i = 0; i < bridges.length; i++) {
                BridgeDescription bridge = (BridgeDescription)
                   XMLDeserializer.deserialize((String) bridges[i]);
                result.add(bridge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Instructs the venue client to join a bridge
     * @param bridgeId The id of the bridge to join
     * @return True if the bridge was joined, false otherwise
     */
    public boolean joinBridge(final String bridgeId) {
        return (Boolean) call("VenueClient.joinBridge", new Object[]{bridgeId});
    }

    /**
     * Starts automatic bridging on the client
     *
     */
    public void runAutomaticBridging() {
        call("VenueClient.runAutomaticBridging", new Object[]{});
    }

    /**
     * Starts a shared application
     * @param applicationXml XML description of the application to start
     * @param appStateXml XML description of the initial application state
     * @param namespace the PAG namespace
     * @param dataDownloadUrl the URL where the application downloads its data from
     */
    public void runApplication(String applicationXml,String appStateXml,String namespace,String dataDownloadUrl){
        URL url = getCodeBase();
        String downloadUrl = url.getProtocol() + "://" + url.getHost() + ":"
            + url.getPort()	+ dataDownloadUrl;
        call("VenueClient.runApplication",new Object[]{
                    StringEscapeUtils.unescapeXml(applicationXml),
                    StringEscapeUtils.unescapeXml(appStateXml),
                    namespace, downloadUrl, sessionid
                    });
    }

    /**
     * distributes an event across multiple instances of a shared application
     * @param applicationXml XML description of the application issuing the event
     * @param eventXml XML description of the event
     */
    public void distributeApplicationEvent(String applicationXml,String eventXml){
        call("VenueClient.distributeApplicationEvent",new Object[]{
                StringEscapeUtils.unescapeXml(applicationXml),
                StringEscapeUtils.unescapeXml(eventXml)
                });
    }

    /**
     * Serializes an object to XML
     * @param object The object to serialize
     * @return The xml representing the object
     */
    public String getXML(Object object) {
        return XMLSerializer.serialize(object);
    }

    /**
     * Serializes and escapes an object to XML
     * @param object The object to serialize
     * @return The escaped XML representing the object
     */
    public String getXMLEnc(Object object) {
        return Utils.escapeXmlRpcValue(getXML(object));
    }

    /**
     * Deserializes XML into an object
     * @param xml The xml to deserialize
     * @return The object deserialized
     */
    public Object getObject(String xml) {
        try {
        	return XMLDeserializer.deserialize(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unescapes an decodes an xml object
     * @param xmlEnc The escaped xml
     * @return The object represented by the xml
     */
    public Object getObjectDec(String xmlEnc) {
    	return getObject(Utils.unescapeXmlRpcValue(xmlEnc));
    }

    /**
     * Gets the bridge registries
     * @return The urls of the bridge registries
     */
    public Vector<String> getRegistries() {
        String registryXML = (String) call("VenueClient.getRegistries",
                                    new Object[0]);
        Vector<String> urls = new Vector<String>();
        try {
            String[] registries = (String[]) XMLDeserializer.deserialize(
                    registryXML);
            for (int i = 0; i < registries.length; i++) {
                urls.add(registries[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Adds new bridges
     * @param bridgeXml The xml of the vector of bridges to add
     */
    public void addBridges(final String bridgeXml) {
        call("VenueClient.addBridges", new Object[]{bridgeXml});
    }

    /**
     * Clears the bridge list
     */
    public void clearBridges() {
        call("VenueClient.clearBridges", new Object[]{});
    }

    /**
     * Sets the encryption to use
     * @param encryption The new encryption
     * @return the new key set or the old one if error
     */
    public String setEncryption(final String encryption) {
        return (String) call("VenueClient.setEncryption",
                                    new Object[]{encryption});
    }


    /**
     * Gets the encryption in use
     * @return The encryption
     */
    public String getEncryption() {
        return (String) call("VenueClient.getEncryption",
                                    new Object[]{});
    }

    /**
     * Sets the bridge registries to use
     * @param registries The registries to use
     */
    public void setRegistries(final String registries) {
        call("VenueClient.setRegistries", new Object[]{registries});
    }

    /**
     * Sets the current point of reference url
     * @param url The new url
     */
    public void setPointOfReference(final String[] url) {
        call("VenueClient.setPointOfReference", new Object[]{url});
    }

    /**
     * Sets the current point of reference url
     * @param url The new url
     */
    public void setPointOfReference(final String url) {
        call("VenueClient.setPointOfReference", new Object[]{url});
    }

    /**
     * Gets the current bridge id
     * @return The id of the current bridge
     */
    public String getCurrentBridgeId() {
        return (String) call("VenueClient.getCurrentBridgeId", new Object[]{});
    }

    /**
     * Determines if automatic bridging is being used
     * @return True if automatic bridging is being used, false otherwise
     */
    public boolean isAutomaticBridging() {
        return (Boolean) call("VenueClient.isAutomaticBridging", new Object[0]);
    }

    /**
     * Invalidates the bridge implementations causing them to be reloaded
     *
     */
    public void invalidateBridges() {
        call("VenueClient.invalidateBridges", new Object[0]);
    }

    private class EvalThread extends Thread {

        private LinkedList<String> evalList = new LinkedList<String>();

        private boolean done = false;

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!evalList.isEmpty() || !done) {
                String eval = null;
                synchronized (evalList) {
                    while (evalList.isEmpty() && !done) {
                        try {
                            Thread.yield();
                            evalList.wait(5000);
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                    if (!evalList.isEmpty()) {
                        eval = evalList.removeFirst();
                    }
                }
                if (eval != null) {
                    jsWindow.eval(eval);
                }
            }
        }

        private void close() {
            done = true;
            synchronized (evalList) {
                evalList.notifyAll();
            }
        }

        private void addEval(String eval) {
            synchronized (evalList) {
                evalList.addLast(eval);
                evalList.notifyAll();
            }
        }
    }

    /**
     * Executes a javascript method in a thread
     * @param code The code to execute
     */
    public void threadedEval(final String code) {
        evalThread.addEval(code);
    }

    /**
     * Gets the error in the server
     * @return The error
     */
    public String getError() {
        if (xmlrpcServerError == null) {
            return "";
        }
        return xmlrpcServerError;
    }

    private class Reader extends Thread {

        private boolean done = false;

        private InputStream input = null;

        private String string = null;

        private Reader(InputStream input, String string) {
            this.input = input;
            this.string = string;
        }

        public void run() {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input));
            while (!done) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                    	log.info(string + line);
                    } else {
                        done = true;
                    }
                } catch (IOException e) {
                    done = true;
                }
            }
        }
    }

    private class DoShutdown extends Thread {

        public void run() {
            /*try {
                client.execute("VenueClient.shutdown", new Object[0]);
            } catch (XmlRpcException e) {
                // Does Nothing
            } */
        }
    }

}
